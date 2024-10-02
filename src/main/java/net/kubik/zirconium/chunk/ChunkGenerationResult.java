package net.kubik.zirconium.chunk;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;

/**
 * Represents the result of a chunk generation task.
 */
public class ChunkGenerationResult {
    private final ServerWorld world;
    private final ChunkPos chunkPos;
    private final Chunk chunk;
    private final Exception exception;

    public ChunkGenerationResult(ServerWorld world, ChunkPos chunkPos, Chunk chunk) {
        this.world = world;
        this.chunkPos = chunkPos;
        this.chunk = chunk;
        this.exception = null;
    }

    public ChunkGenerationResult(ServerWorld world, ChunkPos chunkPos, Exception exception) {
        this.world = world;
        this.chunkPos = chunkPos;
        this.chunk = null;
        this.exception = exception;
    }

    public boolean isSuccessful() {
        return chunk != null && exception == null;
    }

    public ServerWorld getWorld() {
        return world;
    }

    public ChunkPos getChunkPos() {
        return chunkPos;
    }

    public Chunk getChunk() {
        return chunk;
    }

    public Exception getException() {
        return exception;
    }
}