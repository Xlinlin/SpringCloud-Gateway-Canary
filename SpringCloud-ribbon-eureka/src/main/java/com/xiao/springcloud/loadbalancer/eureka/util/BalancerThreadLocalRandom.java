package com.xiao.springcloud.loadbalancer.eureka.util;

import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @ClassName: ThreadLocalRandom
 * @Description: 随机取值
 * @author: xiaolinlin
 * @date: 2020/5/26 21:21
 **/
public class BalancerThreadLocalRandom extends Random {

    private static final AtomicLong seedUniquifier = new AtomicLong();

    private static volatile long initialSeedUniquifier;

    public static void setInitialSeedUniquifier(long initialSeedUniquifier) {
        BalancerThreadLocalRandom.initialSeedUniquifier = initialSeedUniquifier;
    }

    public static synchronized long getInitialSeedUniquifier() {
        // Use the value set via the setter.
        long initialSeedUniquifier = BalancerThreadLocalRandom.initialSeedUniquifier;
        // Otherwise, generate one.
        if (initialSeedUniquifier == 0) {
            // Try to generate a real random number from /dev/random.
            // Get from a different thread to avoid blocking indefinitely on a machine without much entrophy.
            final BlockingQueue<Long> queue = new LinkedBlockingQueue<Long>();
            Thread generatorThread = new Thread("initialSeedUniquifierGenerator") {
                @Override
                public void run() {
                    SecureRandom random = new SecureRandom(); // Get the real random seed from /dev/random
                    queue.add(random.nextLong());
                }
            };
            generatorThread.start();

            // Get the random seed from the thread with timeout.
            final long timeoutSeconds = 3;
            final long deadLine = System.nanoTime() + TimeUnit.SECONDS.toNanos(timeoutSeconds);
            for (; ; ) {
                long waitTime = deadLine - System.nanoTime();
                if (waitTime <= 0) {
                    break;
                }

                try {
                    Long result = queue.poll(waitTime, TimeUnit.NANOSECONDS);
                    if (result != null) {
                        initialSeedUniquifier = result;
                        break;
                    }
                } catch (InterruptedException ignore) {
                    // Ignore
                }
            }

            // Just in case the initialSeedUniquifier is zero or some other constant
            // just a meaningless random number
            initialSeedUniquifier ^= 0x3255ecdc33bae119L;
            initialSeedUniquifier ^= Long.reverse(System.nanoTime());

            BalancerThreadLocalRandom.initialSeedUniquifier = initialSeedUniquifier;
        }

        return initialSeedUniquifier;
    }

    private static long newSeed() {
        for (; ; ) {
            final long current = seedUniquifier.get();
            final long actualCurrent = current != 0 ? current : getInitialSeedUniquifier();

            // L'Ecuyer, "Tables of Linear Congruential Generators of Different Sizes and Good Lattice Structure", 1999
            final long next = actualCurrent * 181783497276652981L;

            if (seedUniquifier.compareAndSet(current, next)) {
                return next ^ System.nanoTime();
            }
        }
    }

    // same constants as Random, but must be redeclared because private
    private static final long multiplier = 0x5DEECE66DL;
    private static final long addend = 0xBL;
    private static final long mask = (1L << 48) - 1;

    /**
     * The random seed. We can't use super.seed.
     */
    private long rnd;

    /**
     * Initialization flag to permit calls to setSeed to succeed only while executing the Random constructor. We can't allow
     * others since it would cause setting seed in one part of a program to unintentionally impact other usages by the thread.
     */
    boolean initialized = false;

    // Padding to help avoid memory contention among seed updates in
    // different TLRs in the common case that they are located near
    // each other.
    private long pad0, pad1, pad2, pad3, pad4, pad5, pad6, pad7;

    /**
     * Constructor called only by localRandom.initialValue.
     */
    BalancerThreadLocalRandom() {
        super(newSeed());
        initialized = true;
    }

    /**
     * The actual ThreadLocal
     */
    private static final ThreadLocal<BalancerThreadLocalRandom> localRandom = new ThreadLocal<BalancerThreadLocalRandom>() {
        @Override
        protected BalancerThreadLocalRandom initialValue() {
            return new BalancerThreadLocalRandom();
        }
    };

    /**
     * Returns the current thread's {@code ThreadLocalRandom}.
     *
     * @return the current thread's {@code ThreadLocalRandom}
     */
    public static BalancerThreadLocalRandom current() {
        return localRandom.get();
    }

    /**
     * Throws {@code UnsupportedOperationException}. Setting seeds in this generator is not supported.
     *
     * @throws UnsupportedOperationException always
     */
    @Override
    public void setSeed(long seed) {
        if (initialized) {
            throw new UnsupportedOperationException();
        }
        rnd = (seed ^ multiplier) & mask;
    }

    @Override
    protected int next(int bits) {
        rnd = (rnd * multiplier + addend) & mask;
        return (int) (rnd >>> (48 - bits));
    }

    /**
     * Returns a pseudorandom, uniformly distributed value between the given least value (inclusive) and bound (exclusive).
     *
     * @param least the least value returned
     * @param bound the upper bound (exclusive)
     * @return the next value
     * @throws IllegalArgumentException if least greater than or equal to bound
     */
    public int nextInt(int least, int bound) {
        if (least >= bound) {
            throw new IllegalArgumentException();
        }
        return nextInt(bound - least) + least;
    }

    /**
     * Returns a pseudorandom, uniformly distributed value between 0 (inclusive) and the specified value (exclusive).
     *
     * @param n the bound on the random number to be returned. Must be positive.
     * @return the next value
     * @throws IllegalArgumentException if n is not positive
     */
    public long nextLong(long n) {
        if (n <= 0) {
            throw new IllegalArgumentException("n must be positive");
        }

        // Divide n by two until small enough for nextInt. On each
        // iteration (at most 31 of them but usually much less),
        // randomly choose both whether to include high bit in result
        // (offset) and whether to continue with the lower vs upper
        // half (which makes a difference only if odd).
        long offset = 0;
        while (n >= Integer.MAX_VALUE) {
            int bits = next(2);
            long half = n >>> 1;
            long nextn = ((bits & 2) == 0) ? half : n - half;
            if ((bits & 1) == 0) {
                offset += n - nextn;
            }
            n = nextn;
        }
        return offset + nextInt((int) n);
    }

    /**
     * Returns a pseudorandom, uniformly distributed value between the given least value (inclusive) and bound (exclusive).
     *
     * @param least the least value returned
     * @param bound the upper bound (exclusive)
     * @return the next value
     * @throws IllegalArgumentException if least greater than or equal to bound
     */
    public long nextLong(long least, long bound) {
        if (least >= bound) {
            throw new IllegalArgumentException();
        }
        return nextLong(bound - least) + least;
    }

    /**
     * Returns a pseudorandom, uniformly distributed {@code double} value between 0 (inclusive) and the specified value
     * (exclusive).
     *
     * @param n the bound on the random number to be returned. Must be positive.
     * @return the next value
     * @throws IllegalArgumentException if n is not positive
     */
    public double nextDouble(double n) {
        if (n <= 0) {
            throw new IllegalArgumentException("n must be positive");
        }
        return nextDouble() * n;
    }

    /**
     * Returns a pseudorandom, uniformly distributed value between the given least value (inclusive) and bound (exclusive).
     *
     * @param least the least value returned
     * @param bound the upper bound (exclusive)
     * @return the next value
     * @throws IllegalArgumentException if least greater than or equal to bound
     */
    public double nextDouble(double least, double bound) {
        if (least >= bound) {
            throw new IllegalArgumentException();
        }
        return nextDouble() * (bound - least) + least;
    }

    private static final long serialVersionUID = -5851777807851030925L;
}
