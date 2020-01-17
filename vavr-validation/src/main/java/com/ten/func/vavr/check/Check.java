package com.ten.func.vavr.check;

public interface Check {

    String name();

    void thrower();

    void asserts();

    boolean check();
}

