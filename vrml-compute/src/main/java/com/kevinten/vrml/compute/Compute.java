package com.kevinten.vrml.compute;

public interface Compute<T> {

    abstract class ComputeFactory {
    }

    void compute(String key, Runnable f);

    void compute(String key, Runnable f1, Runnable f2);
}
