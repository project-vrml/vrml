package com.vavr.func.work.demo;

public record RecordTest(int x, int y, String sum) {
    public RecordTest(int x, int y, String sum) {
        this.x = x;
        this.y = y;
        this.sum = sum;
    }
}
