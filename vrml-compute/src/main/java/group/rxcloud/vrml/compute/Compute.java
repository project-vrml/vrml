package group.rxcloud.vrml.compute;

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
     * @param key   the key
     * @param left  the left when compute failure
     * @param right the right when compute success
     */
    void compute(String key, Runnable left, Runnable right);
}
