package com.ptrufanov;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class RateLimiter {

    private static final Logger logger = LogManager.getLogger(RateLimiter.class);
    private final int replenishmentSpeed;
    private final BlockingQueue<String> tokenBucket;

    public RateLimiter(int capacity, int replenishmentSpeed) {
        this.replenishmentSpeed = replenishmentSpeed;
        this.tokenBucket = new ArrayBlockingQueue<>(capacity);
        for (int i = 0; i < capacity; i++) {
            this.tokenBucket.add(UUID.randomUUID().toString());
        }
        Thread daemonThread = new Thread(new Replenisher());
        daemonThread.setDaemon(true);
        daemonThread.start();
    }

    public Response handleRequest() {
        if (!tokenBucket.isEmpty()) {
            String token = tokenBucket.remove();
            logger.debug("Remove token {} from bucket. Bucket size = {}", token, tokenBucket.size());
            return Response.SUCCESS;
        } else {
            logger.debug("Token bucket is empty");
            return Response.FAILURE;
        }
    }

    public int getCurrentCapacity() {
        return tokenBucket.size();
    }

    class Replenisher implements Runnable {

        public void run() {
            try {
                while (true) {
                    String token = UUID.randomUUID().toString();
                    Thread.sleep(replenishmentSpeed);
                    tokenBucket.put(token);
                    logger.debug("Replenish token = {}, bucket size = {}", token, tokenBucket.size());
                }
            } catch (InterruptedException ex) {
                logger.error(ex);
            }
        }
    }
}


