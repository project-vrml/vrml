package com.vavr.func.work.demo;

public class TupleTest {

    public void record(){
        RecordTest recordTest = new RecordTest(hashCode(), hashCode(), toString());
        int x = recordTest.x();

        System.out.println(x);
    }
}
