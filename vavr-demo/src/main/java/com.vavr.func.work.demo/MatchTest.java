package com.vavr.func.work.demo;

import io.vavr.Predicates;
import io.vavr.Tuple;
import io.vavr.Tuple2;

import static com.google.common.base.Predicates.instanceOf;
import static io.vavr.API.*;
import static io.vavr.Patterns.$Tuple2;
import static io.vavr.Predicates.isIn;
import static io.vavr.Predicates.isNull;

/**
 * Match 模式匹配
 */
public class MatchTest {

    /**
     * Jdk 14 switch.
     */
    public void jdk14switch() {
        Weekday day = Weekday.MON;

        // 1. JDK14中扩展了switch可以作为表达式
        System.out.println(switch (day) {
            case MON, TUE, WEN -> "上半周";
            case THU, FRI -> "下半周";
            case SAT, SUN -> "周末";
        });

        // 2. 将表达式的值赋值给一个变量 => to old
        String text = switch (day) {
            case MON, TUE, WEN -> "上半周";
            case THU, FRI -> "下半周";
            case SAT, SUN -> "周末";
        };
        System.out.println(text);

        // 3. 通过yield产生一个新的值
        day = Weekday.SAT;
        int x = switch (day) {
            case MON, TUE, WEN -> 1;
            case THU, FRI -> {
                System.out.println();
                yield 2;
            }
            case SAT, SUN -> {
                int rnd = (int) (Math.random() * 10);
                System.out.println("rnd = " + rnd);
                yield rnd;
            }
        };
        System.out.println(x);
    }

    /**
     * Vavr Match.
     */
    public void vavrMatch() {
        Tuple2<Weekday, Weather> multiple = Tuple.of(Weekday.TUE, Weather.SUNNY);
        // 1. 多值匹配
        String multipleMatching = Match(multiple).of(
                Case($Tuple2($(Weekday.TUE), $(Weather.SUNNY)), "11"),
                Case($(Tuple.of(Weekday.MON, Weather.RAIN)), tuple -> "12"),
                Case($Tuple2($(Weekday.SAT), $(Weather.CLOUDY)), "12"),
                Case($Tuple2($(Weekday.SUN), $(Weather.CLOUDY)), "12"),
                Case($(), "null")
        );
        System.out.println(multipleMatching);

        // 2. 类型判断
        Number plusOne = Match(1).of(
                Case($(instanceOf(Integer.class)), i -> i + 1),
                Case($(instanceOf(Double.class)), d -> d + 1),
                Case($(), o -> {
                    throw new NumberFormatException();
                })
        );
        System.out.println(plusOne);

        // 3. 谓词匹配
        Match("arg").of(
                Case($(isIn("-h", "--help")), o -> run(this::displayHelp)),
                Case($(isIn("-v", "--version")), o -> run(this::displayVersion)),
                Case($(isNull()), o -> run(this::displayVersion)),
                Case($(), o -> run(() -> {
                    throw new IllegalArgumentException(o);
                }))
        );
    }

    /**
     * The enum Weather.
     */
    enum Weather {
        /**
         * Sunny day.
         */
        SUNNY,
        /**
         * Cloudy day.
         */
        CLOUDY,
        /**
         * Rain day.
         */
        RAIN
    }

    /**
     * The enum Weekday.
     */
    enum Weekday {
        /**
         * Mon weekday.
         */
        MON,
        /**
         * Tue weekday.
         */
        TUE,
        /**
         * Wen weekday.
         */
        WEN,
        /**
         * Thu weekday.
         */
        THU,
        /**
         * Fri weekday.
         */
        FRI,
        SAT,
        SUN
    }

    private void displayVersion() {
    }

    private void displayHelp() {
    }
}
