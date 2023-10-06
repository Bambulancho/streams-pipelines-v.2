package com.efimchick.ifmo.util;

import java.util.Collections;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class ResultCollector implements Collector<CourseResult, ResultTable, String> {

    @Override
    public Supplier<ResultTable> supplier() {
        return ResultTable::new;
    }

    @Override
    public BiConsumer<ResultTable, CourseResult> accumulator() {
        return ResultTable::addResultsCourse;
    }

    @Override
    public BinaryOperator<ResultTable> combiner() {
        return (resultTable, resultTable2) -> resultTable;
    }

    @Override
    public Function<ResultTable, String> finisher() {
        return ResultTable::createResultTable;
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Collections.emptySet();
    }
}
