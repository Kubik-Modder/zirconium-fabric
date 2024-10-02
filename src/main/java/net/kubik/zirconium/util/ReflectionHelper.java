package net.kubik.zirconium.util;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.ChunkManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * Utility class for handling reflection operations.
 */
public class ReflectionHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReflectionHelper.class);
    private Method setChunkForcedMethod;

    /**
     * Initializes the reflection helper by caching the desired method.
     *
     * @param server the Minecraft server instance
     */
    public void initialize(MinecraftServer server) {
        try {
            ServerWorld world = server.getWorld(server.getOverworld().getRegistryKey());
            ChunkManager chunkManager = world.getChunkManager();

            for (Method method : ChunkManager.class.getDeclaredMethods()) {
                if (method.getName().equals("setChunkForced") &&
                        method.getParameterCount() == 2 &&
                        method.getParameterTypes()[0] == ChunkPos.class &&
                        method.getParameterTypes()[1] == boolean.class) {
                    method.setAccessible(true);
                    setChunkForcedMethod = method;
                    LOGGER.info("'setChunkForced' method successfully cached.");
                    return;
                }
            }

            LOGGER.error("Could not find a suitable 'setChunkForced' method in ChunkManager.");
        } catch (Exception e) {
            LOGGER.error("Failed to initialize setChunkForced method via reflection.", e);
        }
    }

    /**
     * Invokes the cached 'setChunkForced' method to set a chunk's forced state.
     *
     * @param chunkManager the ChunkManager instance
     * @param pos          the position of the chunk
     * @param forced       whether to force the chunk to be loaded
     */
    public void setChunkForced(ChunkManager chunkManager, ChunkPos pos, boolean forced) {
        if (setChunkForcedMethod == null) {
            LOGGER.error("setChunkForced method is not initialized. Cannot set chunk forced state.");
            return;
        }

        try {
            setChunkForcedMethod.invoke(chunkManager, pos, forced);
            LOGGER.info("Successfully set chunk at ({}, {}) to forced: {}", pos.x, pos.z, forced);
        } catch (Exception e) {
            LOGGER.error("Failed to set chunk at ({}, {}) to forced: {}", pos.x, pos.z, forced, e);
        }
    }

    /**
     * Cleans up resources if necessary.
     */
    public void shutdown() {
        // PLACEHOLDER
    }
}