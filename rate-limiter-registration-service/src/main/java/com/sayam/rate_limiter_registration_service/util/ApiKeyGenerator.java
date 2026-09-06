package com.sayam.rate_limiter_registration_service.util;

import java.security.SecureRandom;
import java.util.Base64;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class ApiKeyGenerator {

    // Custom Epoch: January 1, 2026, 00:00:00 UTC
    private static final long START_EPOCH = 1767225600000L;

    // Bit allocation for Snowflake components
    private static final long NODE_ID_BITS = 10L;
    private static final long SEQUENCE_BITS = 12L;

    // Max values for safety bitmasking
    private static final long MAX_NODE_ID = -1L ^ (-1L << NODE_ID_BITS);
    private static final long MAX_SEQUENCE = -1L ^ (-1L << SEQUENCE_BITS);

    // Bit shifting positions
    private static final long NODE_ID_SHIFT = SEQUENCE_BITS;
    private static final long TIMESTAMP_SHIFT = SEQUENCE_BITS + NODE_ID_BITS;

    // Instance specific state
    private final long nodeId;
    private long lastTimestamp = -1L;
    private long sequence = 0L;

    /**
     * Constructor for the generator.
     * @param nodeId Unique identifier for this server node (0 to 1023).
     *               In production, fetch this from environment variables or a coordination tool like ZooKeeper.
     */
    public ApiKeyGenerator(long nodeId) {
        if (nodeId < 0 || nodeId > MAX_NODE_ID) {
            throw new IllegalArgumentException(String.format("Node ID must be between 0 and %d", MAX_NODE_ID));
        }
        this.nodeId = nodeId;
    }

    /**
     * Thread-safe method to generate a raw 64-bit Snowflake ID.
     */
    private synchronized long generateSnowflakeId() {
        long currentTimestamp = System.currentTimeMillis();

        if (currentTimestamp < lastTimestamp) {
            throw new RuntimeException("System clock moved backwards! Rejecting requests for " + (lastTimestamp - currentTimestamp) + "ms");
        }

        if (currentTimestamp == lastTimestamp) {
            // Same millisecond, advance sequence
            sequence = (sequence + 1) & MAX_SEQUENCE;
            if (sequence == 0) {
                // Sequence overflow, block until next millisecond
                currentTimestamp = blockTillNextMillis(lastTimestamp);
            }
        } else {
            // New millisecond, reset sequence
            sequence = 0L;
        }

        lastTimestamp = currentTimestamp;

        // Construct Snowflake bit layout
        return ((currentTimestamp - START_EPOCH) << TIMESTAMP_SHIFT)
                | (nodeId << NODE_ID_SHIFT)
                | sequence;
    }

    private long blockTillNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }

    /**
     * Generates a secure, unique API key linked to a specific email ID.
     * Output format: "prefix_Base64EncodedHash"
     */
    public String generateApiKey(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email address cannot be empty");
        }

        // 1. Generate distributed unique components
        long snowflakeId = generateSnowflakeId();
        String normalizedEmail = email.trim().toLowerCase();

        // 2. Combine inputs safely into a payload string
        String rawInput = snowflakeId + ":" + normalizedEmail;

        try {
            // 3. Compute a secure cryptographic digest
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(rawInput.getBytes(StandardCharsets.UTF_8));

            // 4. Encode hash to URL-safe Base64 and clean up trailing padding characters
            String base64Encoded = Base64.getUrlEncoder().withoutPadding().encodeToString(hashBytes);

            // 5. Prepend visual signifier prefix for tracking/revocation rules
            return "ak_" + base64Encoded;

        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm missing from Java runtime environment", e);
        }
    }

    // Quick verification execution block
    public static void main(String[] args) {
        // Assume this node is assigned index 42 by your container orchestrator
        ApiKeyGenerator generator = new ApiKeyGenerator(42);

        String userEmail = "developer@example.com";
        String apiKey = generator.generateApiKey(userEmail);

        System.out.println("Input Email: " + userEmail);
        System.out.println("Generated Key: " + apiKey);
    }
}

