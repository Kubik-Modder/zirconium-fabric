package net.kubik.zirconium.chunk;

import net.kubik.zirconium.util.ReflectionHelper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.ChunkManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Manages chunk generation tasks, including submission and processing.
 */
public class ChunkTaskManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChunkTaskManager.class);

    private final ExecutorService chunkExecutor;
    private final CompletionService<ChunkGenerationResult> completionService;
    private final ReflectionHelper reflectionHelper;
    private final AtomicBoolean isActive;

    /**
     * Constructs a new ChunkTaskManager.
     *
     * @param reflectionHelper the ReflectionHelper instance
     */
    public ChunkTaskManager(ReflectionHelper reflectionHelper) {
        this.reflectionHelper = reflectionHelper;
        this.chunkExecutor = Executors.newFixedThreadPool(
                Runtime.getRuntime().availableProcessors(),
                new CustomThreadFactory("Zirconium-Chunk-Executor")
        );
        this.completionService = new ExecutorCompletionService<>(chunkExecutor);
        this.isActive = new AtomicBoolean(false);
    }

    /**
     * Activates the ChunkTaskManager.
     */
    public void activate() {
        if (isActive.compareAndSet(false, true)) {
            LOGGER.info("ChunkTaskManager activated.");
        }
    }

    /**
     * Deactivates the ChunkTaskManager and shuts down the executor service.
     */
    public void deactivate() {
        if (isActive.compareAndSet(true, false)) {
            LOGGER.info("ChunkTaskManager deactivated.");
            chunkExecutor.shutdownNow();
            try {
                if (!chunkExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                    LOGGER.warn("Chunk executor did not terminate in the allotted time.");
                }
            } catch (InterruptedException e) {
                LOGGER.error("Interrupted while shutting down chunk executor.", e);
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Submits a chunk generation task to be processed asynchronously.
     *
     * @param server the Minecraft server instance
     * @param world  the server world where the chunk belongs
     * @param pos    the position of the chunk
     */
    public void submitChunkTask(MinecraftServer server, ServerWorld world, ChunkPos pos) {
        if (!isActive.get()) {
            LOGGER.warn("Attempted to submit chunk task while ChunkTaskManager is inactive.");
            return;
        }

        ChunkGenerationTask task = new ChunkGenerationTask(server, world, pos);
        completionService.submit(task);
        LOGGER.debug("Submitted chunk generation task for position: {}", pos);
    }

    /**
     * Processes completed chunk generation tasks on the main server thread.
     *
     * @param server the Minecraft server instance
     * @param reflectionHelper the ReflectionHelper instance
     */
    public void processCompletedTasks(MinecraftServer server, ReflectionHelper reflectionHelper) {
        if (!isActive.get()) {
            return;
        }

        while (true) {
            Future<ChunkGenerationResult> future = completionService.poll();
            if (future == null) {
                break;
            }

            try {
                ChunkGenerationResult result = future.get();
                if (result.isSuccessful()) {
                    server.execute(() -> {
                        ServerWorld world = result.getWorld();
                        ChunkManager chunkManager = world.getChunkManager();
                        ChunkPos pos = result.getChunkPos();
                        boolean forceLoad = true;

                        reflectionHelper.setChunkForced(chunkManager, pos, forceLoad);
                    });
                } else {
                    LOGGER.error("Chunk generation failed for position: {}", result.getChunkPos(), result.getException());
                }
            } catch (InterruptedException e) {
                LOGGER.error("Interrupted while processing chunk generation result.", e);
                Thread.currentThread().interrupt();
            } catch (ExecutionException e) {
                LOGGER.error("Error during chunk generation task execution.", e);
            }
        }
    }

    /**
     * Custom ThreadFactory to create daemon threads with meaningful names.
     */
    private static class CustomThreadFactory implements ThreadFactory {
        private final String baseName;
        private final ThreadFactory defaultFactory = Executors.defaultThreadFactory();
        private final AtomicInteger threadNumber = new AtomicInteger(1);

        public CustomThreadFactory(String baseName) {
            this.baseName = baseName;
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = defaultFactory.newThread(r);
            thread.setName(baseName + "-" + threadNumber.getAndIncrement());
            thread.setDaemon(true);
            return thread;
        }
    }
}