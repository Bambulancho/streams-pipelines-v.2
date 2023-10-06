package com.efimchick.ifmo;

import com.efimchick.ifmo.util.CourseResult;
import com.efimchick.ifmo.util.ResultCollector;
import com.efimchick.ifmo.util.Person;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class CollectingSpare {

    public int sum(IntStream intStream) {
        return intStream.sum();
    }

    public int production(IntStream intStream) {
        return intStream.reduce(1, (a, b) -> a * b);
    }

    public int oddSum(IntStream intStream) {
        return intStream.filter(i -> i % 2 != 0).sum();
    }

    public Map<Integer, Integer> sumByRemainder(int divider, IntStream intStream) {
        return intStream
                .boxed()
                .collect(Collectors.groupingBy(s -> s % divider, Collectors.summingInt(x -> x)));
    }

    public Map<Person, Double> totalScores(Stream<CourseResult> results) {

        List<CourseResult> listTasks = results.collect(toList());

        return listTasks.stream()
                .collect(Collectors.toMap(CourseResult::getPerson,
                        courseResult -> courseResult.getTaskResults().values().stream()
                                .mapToDouble(d -> d)
                                .sum() / amountOfTasks(listTasks)));
    }

    public double averageTotalScore(Stream<CourseResult> results) {

        List<CourseResult> listTasks = results.collect(toList());

        return listTasks.stream()
                .map(courseResult -> courseResult.getTaskResults().entrySet())
                .flatMap(entries -> entries.stream())
                .map(entry -> entry.getValue())
                .mapToDouble(i -> i)
                .sum() / amountOfTasks(listTasks) / amountOfPersons(listTasks);
    }


    public Map<String, Double> averageScoresPerTask(Stream<CourseResult> results) {

        List<CourseResult> listTasks = results.collect(toList());

        return listTasks.stream()
                .map(courseResult -> courseResult.getTaskResults().entrySet())
                .flatMap(Collection::stream)
                .collect(Collectors.groupingBy(Map.Entry::getKey,
                        Collectors.summingDouble(e -> (double) e.getValue() / amountOfPersons(listTasks))));
    }


    public Map<Person, String> defineMarks(Stream<CourseResult> results) {

        List<CourseResult> listTasks = results.collect(toList());

        Map<Person, String> mapDouble = listTasks.stream()
                .collect(Collectors.toMap(CourseResult::getPerson,
                        courseResult -> change(courseResult.getTaskResults().values().stream()
                                .mapToDouble(d -> d)
                                .sum() / amountOfTasks(listTasks))));

        return mapDouble;
    }

    private String change(Double oldValue) {
        if (oldValue > 90 && oldValue <= 100) {
            return "A";
        } else if (oldValue >= 83) {
            return "B";
        } else if (oldValue >= 75) {
            return "C";
        } else if (oldValue >= 68) {
            return "D";
        } else if (oldValue >= 60) {
            return "E";
        } else return "F";
    }

    public String easiestTask(Stream<CourseResult> results) {

        Map<String, Double> map = averageScoresPerTask(results);

        return (map.entrySet()
                .stream()
                .max(Comparator.comparingDouble(Map.Entry::getValue))
                .map(Map.Entry::getKey))
                .orElse("few values for calculate");
    }

    public Collector<CourseResult, ?, String> printableStringCollector() {
        return new ResultCollector();
    }

    public Long amountOfTasks(List<CourseResult> listTasks) {
        return listTasks.stream()
                .flatMap(courseResult -> courseResult.getTaskResults().keySet().stream())
                .distinct()
                .count();
    }

    public double amountOfPersons(List<CourseResult> listTasks) {
        return listTasks.stream()
                .map(CourseResult::getPerson)
                .count();
    }
}

