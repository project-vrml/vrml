package com.ten.func.vavr.core.math;

public class HashUtils {
    /**
     * Improved 32-bit FNV algorithm
     *
     * @param data string
     * @return hashCode
     */
    public static int fnvHash(String data) {
        final int p = 16777619;
        int hash = (int) 2166136261L;
        for (int i = 0; i < data.length(); i++) {
            hash = (hash ^ data.charAt(i)) * p;
        }
        hash += hash << 13;
        hash ^= hash >> 7;
        hash += hash << 3;
        hash ^= hash >> 17;
        hash += hash << 5;
        return hash;
    }
}
