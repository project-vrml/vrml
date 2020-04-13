package com.vavr.func.work.demo;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import io.vavr.Lazy;

public class LazyTest {

    /**
     * Guava.
     */
    public void guava() {
        Supplier<String> supplier = Suppliers.memoize(this::request);
    }

    /**
     * Lazy.
     */
    public void lazy() {
        Lazy<String> lazy = Lazy.of(this::request);
    }

    private String request() {
        return toString();
    }
}
