package com.kevinten.vrml.compute;

public abstract class Computes<T> extends Compute.ComputeFactory implements Compute<T> {

    public static final TimeCounterComputes TimeCounterComputes = new TimeCounterComputes();

    private void compute(String key, Class<T> configType, Runnable f) {

    }

    private void compute(String key, Class<T> configType, Runnable f1, Runnable f2) {

    }
}
