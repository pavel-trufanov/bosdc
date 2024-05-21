package com.ptrufanov;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class RateLimiterTest {

    @Test
    void testHandleRequest_theAmountOfRequestsEqualsCapacity() {
        RateLimiter rateLimiter = new RateLimiter(10, 200);
        List<Response> responses = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            responses.add(rateLimiter.handleRequest());
        }
        responses.forEach(response -> assertEquals(Response.SUCCESS, response));
    }

    @Test
    void testHandleRequest_replenishmentStopsWhenCapacityReached() throws InterruptedException {
        RateLimiter rateLimiter = new RateLimiter(10, 100);
        Thread.sleep(2000);
        assertEquals(10, rateLimiter.getCurrentCapacity());
    }

    @Test
    void testHandleRequest_requestsAreFailedWhenCapacityReached() {
        RateLimiter rateLimiter = new RateLimiter(10, 2000);
        List<Response> failedResponses = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            Response response = rateLimiter.handleRequest();
            if (response == Response.FAILURE) {
                failedResponses.add(response);
            }
        }
        assertEquals(10, failedResponses.size());
    }
}
