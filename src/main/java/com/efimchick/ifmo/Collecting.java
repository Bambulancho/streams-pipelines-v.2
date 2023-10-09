package com.efimchick.ifmo;

import com.efimchick.ifmo.util.CourseResult;
import com.efimchick.ifmo.util.Person;
import com.efimchick.ifmo.util.ResultCollector;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

public class Collecting {
    public int sum(IntStream stream) {return stream.sum();}
    public int production(IntStream stream) {return stream.reduce(1, (a, b) -> a * b);}
    public int oddSum(IntStream stream) {return stream.filter(a -> a % 2 != 0).sum();}
    public Map<Integer, Integer> sumByRemainder(int d, IntStream stream) {
        return stream.boxed().collect(groupingBy(a -> a % d, summingInt(Integer::intValue)));
    }

    public Map<Person, Double> totalScores(Stream<CourseResult> stream) {
        List<CourseResult> listTasks = stream.collect(toList());

        return listTasks.stream()
                .collect(toMap(CourseResult::getPerson,
                        courseResult -> courseResult.getTaskResults().values().stream()
                                .mapToDouble(d -> d)
                                .sum() / amountOfTasks(listTasks)));
    }

    public double averageTotalScore(Stream<CourseResult> stream) {
        return totalScores(stream)
                .values()
                .stream()
                .collect(averagingDouble(a -> a));
    }

    public Map<String, Double> averageScoresPerTask(Stream<CourseResult> stream) {
        List<CourseResult> listTasks = stream.collect(toList());

        return listTasks.stream()
                .flatMap(a -> a.getTaskResults().entrySet().stream())
                .collect(groupingBy(Map.Entry::getKey,
                        summingDouble(e -> (double) e.getValue() / amountOfPersons(listTasks))));
    }

    public Map<Person, String> defineMarks(Stream<CourseResult> stream) {
        return totalScores(stream).entrySet()
                .stream().collect(toMap(Map.Entry::getKey,
                        a -> {
                            double score = a.getValue();
                            if (score < 60) return "F";
                            else if (score >= 60 && score < 68) return "E";
                            else if (score >= 68 && score < 75) return "D";
                            else if (score >= 75 && score < 83) return "C";
                            else if (score >= 83 && score <= 90) return "B";
                            else return "A";
                        }));
    }

    public String easiestTask(Stream<CourseResult> stream) {
        return averageScoresPerTask(stream).entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("not enough values to calculate");
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
