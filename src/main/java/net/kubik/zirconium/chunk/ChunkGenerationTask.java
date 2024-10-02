package net.kubik.zirconium.chunk;

import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.*;
import net.minecraft.world.gen.chunk.Blender;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.placement.StructurePlacementCalculator;
import net.minecraft.world.gen.noise.NoiseConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;

/**
 * Represents a chunk generation task.
 * Implements Callable to return a ChunkGenerationResult.
 */
public class ChunkGenerationTask implements Callable<ChunkGenerationResult> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChunkGenerationTask.class);

    private final MinecraftServer server;
    private final ServerWorld world;
    private final ChunkPos pos;

    /**
     * Constructs a new ChunkGenerationTask.
     *
     * @param server the Minecraft server instance
     * @param world  the server world where the chunk belongs
     * @param pos    the position of the chunk
     */
    public ChunkGenerationTask(MinecraftServer server, ServerWorld world, ChunkPos pos) {
        this.server = server;
        this.world = world;
        this.pos = pos;
    }

    @Override
    public ChunkGenerationResult call() {
        try {
            ChunkGenerator generator = world.getChunkManager().getChunkGenerator();
            Random random = world.getRandom();

            ProtoChunk protoChunk = new ProtoChunk(pos, UpgradeData.NO_UPGRADE_DATA, world,
                    world.getRegistryManager().get(RegistryKeys.BIOME), null);

            generator.buildSurface(
                    new ChunkRegion(world, Collections.singletonList(protoChunk), ChunkStatus.SURFACE, 0),
                    world.getStructureAccessor(),
                    world.getChunkManager().getNoiseConfig(),
                    protoChunk
            );

            StructurePlacementCalculator calculator;
            try {
                Constructor<StructurePlacementCalculator> constructor = StructurePlacementCalculator.class.getDeclaredConstructor(
                        NoiseConfig.class, BiomeSource.class, long.class, long.class, List.class);
                constructor.setAccessible(true);
                calculator = constructor.newInstance(
                        world.getChunkManager().getNoiseConfig(), world.getChunkManager().getChunkGenerator().getBiomeSource(),
                        world.getSeed(), world.getSeed(), Collections.emptyList());
            } catch (Exception e) {
                throw new RuntimeException("Failed to create StructurePlacementCalculator", e);
            }
            generator.setStructureStarts(
                    world.getRegistryManager(),
                    calculator,
                    world.getStructureAccessor(),
                    protoChunk,
                    world.getStructureTemplateManager()
            );

            generator.populateBiomes(
                    new FakeExecutor(),
                    world.getChunkManager().getNoiseConfig(),
                    Blender.getBlender(new ChunkRegion(world, Collections.emptyList(), ChunkStatus.FULL, 0)),
                    world.getStructureAccessor(),
                    protoChunk
            );

            generator.generateFeatures(
                    world,
                    protoChunk,
                    world.getStructureAccessor()
            );

            Chunk chunk = protoChunk;
            return new ChunkGenerationResult(world, pos, chunk);
        } catch (Exception e) {
            LOGGER.error("Exception during chunk generation for position: {}", pos, e);
            return new ChunkGenerationResult(world, pos, e);
        }
    }

    private static class FakeExecutor implements Executor {
        @Override
        public void execute(Runnable command) {
            // PLACEHOLDER
        }
    }
}