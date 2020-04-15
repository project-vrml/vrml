package com.vavr.func.work.demo;

/**
 * Tuple demo.
 */
public class TupleDemo {

    /**
     * Use record as case class.
     */
    public void record() {
        RecordDemo recordDemo = new RecordDemo(hashCode(), hashCode(), toString());
        int x = recordDemo.x();

        System.out.println(x);
    }
}
