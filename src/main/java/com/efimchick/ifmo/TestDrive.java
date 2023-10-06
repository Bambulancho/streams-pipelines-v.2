package com.efimchick.ifmo;

import com.efimchick.ifmo.util.CourseResult;
import com.efimchick.ifmo.util.Person;

import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class TestDrive {
    public static void main(String[] args) {
        Collecting test = new Collecting();
        Stream<CourseResult> stream = Stream.of(
                new CourseResult(new Person("Elton", "John", 66),
                        Map.of("Phalanxing", 87, "Tercioing", 55, "Wedging", 85)),
                new CourseResult(new Person("Ivan", "Ivanov", 21),
                        Map.of("Wedging", 77)),
                new CourseResult(new Person("Volodymyr", "Zelenskii", 43),
                        Map.of("Phalanxing", 64, "Shieldwalling", 0, "Tercioing", 21, "Wedging", 13)),
                new CourseResult(new Person("John", "Baiden", 78),
                        Map.of("Phalanxing", 0, "Shieldwalling", 83, "Tercioing", 0, "Wedging", 47)));


//        Map<Person, Double> map = test.totalScores(stream);
//        Double totalAverage = test.averageTotalScore(map);
//        for (Map.Entry<Person, Double> entry : map.entrySet()) {
//            System.out.println(entry.getKey() + " = " + entry.getValue()); }
//        System.out.println(totalAverage);

        Map<Person, Double> map1 = test.totalScores(stream);
        for (Map.Entry<Person, Double> entry : map1.entrySet()) {
            System.out.println(entry.getKey() + " = " + entry.getValue());
        }
    }
}
