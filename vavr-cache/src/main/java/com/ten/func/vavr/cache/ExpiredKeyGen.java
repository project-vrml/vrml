package com.ten.func.vavr.cache;

public interface ExpiredKeyGen extends KeyGen {

    long expiredSecond();
}
