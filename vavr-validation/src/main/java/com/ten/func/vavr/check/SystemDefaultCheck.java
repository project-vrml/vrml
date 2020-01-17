package com.ten.func.vavr.check;

public enum SystemDefaultCheck implements Check {

    ASSERT_NULL;

    @Override
    public void thrower() {

    }

    @Override
    public void asserts() {

    }

    @Override
    public boolean check() {
        return false;
    }
}
