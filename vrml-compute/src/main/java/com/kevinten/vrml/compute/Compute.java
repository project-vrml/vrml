package com.kevinten.vrml.compute;

/**
 * The interface Compute.
 */
public interface Compute {

    /**
     * Compute.
     *
     * @param key the key
     * @param f   the f
     */
    void compute(String key, Runnable f);

    /**
     * Compute.
     *
     * @param key the key
     * @param f1  the f1 when compute failure
     * @param f2  the f2 when compute success
     */
    void compute(String key, Runnable f1, Runnable f2);
}
