package net.kubik.zirconium;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.kubik.zirconium.chunk.ChunkGenerationResult;
import net.kubik.zirconium.chunk.ChunkTaskManager;
import net.kubik.zirconium.util.ReflectionHelper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.ChunkManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main class for the Zirconium mod.
 * Offloads chunk loading, terrain generation, and biome processing to multiple threads.
 */
public class Zirconium implements ModInitializer {

	private static final Logger LOGGER = LoggerFactory.getLogger("zirconium");

	private final ReflectionHelper reflectionHelper = new ReflectionHelper();
	private ChunkTaskManager chunkTaskManager;

	@Override
	public void onInitialize() {
		ServerLifecycleEvents.SERVER_STARTED.register(this::onServerStart);
		ServerLifecycleEvents.SERVER_STOPPING.register(this::onServerStop);
	}

	/**
	 * Handles server start event.
	 *
	 * @param server the Minecraft server instance
	 */
	private void onServerStart(MinecraftServer server) {
		LOGGER.info("Zirconium activated.");

		reflectionHelper.initialize(server);

		chunkTaskManager = new ChunkTaskManager(reflectionHelper);
		chunkTaskManager.activate();

		ServerTickEvents.START_SERVER_TICK.register(serverInstance ->
				chunkTaskManager.processCompletedTasks(serverInstance, reflectionHelper)
		);
	}

	/**
	 * Handles server stop event.
	 *
	 * @param server the Minecraft server instance
	 */
	private void onServerStop(MinecraftServer server) {
		LOGGER.info("Zirconium deactivated.");

		if (chunkTaskManager != null) {
			chunkTaskManager.deactivate();
		}

		reflectionHelper.shutdown();
	}

	/**
	 * Example method to submit a chunk task.
	 *
	 * @param server the Minecraft server instance
	 * @param world  the server world where the chunk belongs
	 * @param pos    the position of the chunk
	 */
	public void submitChunkTask(MinecraftServer server, ServerWorld world, ChunkPos pos) {
		if (chunkTaskManager != null) {
			chunkTaskManager.submitChunkTask(server, world, pos);
		} else {
			LOGGER.warn("ChunkTaskManager is not initialized. Cannot submit chunk task.");
		}
	}

	/**
	 * Sets the generated chunk into the world by forcing it to be loaded.
	 *
	 * @param server the Minecraft server instance
	 * @param result the result of the chunk generation task
	 */
	private void setChunk(MinecraftServer server, ChunkGenerationResult result) {
		if (reflectionHelper == null) {
			LOGGER.error("ReflectionHelper is not initialized. Cannot set chunk.");
			return;
		}

		try {
			ServerWorld world = result.getWorld();
			ChunkManager chunkManager = world.getChunkManager();
			reflectionHelper.setChunkForced(chunkManager, result.getChunkPos(), true);

			LOGGER.info("Successfully forced chunk at ({}, {}).", result.getChunkPos().x, result.getChunkPos().z);
		} catch (Exception e) {
			LOGGER.error("Failed to force chunk at ({}, {}).", result.getChunkPos().x, result.getChunkPos().z, e);
		}
	}
}