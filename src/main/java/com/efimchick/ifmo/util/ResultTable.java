package com.efimchick.ifmo.util;

import org.checkerframework.checker.units.qual.C;

import java.util.*;
import java.util.stream.Collectors;

/** Collector that forms a special printable String as described below:<br>
 *
 * Student         | Lab 1. Figures | Lab 2. War and Peace | Lab 3. File Tree | Total | Mark |<br>
 * Eco Johnny      |             56 |                   69 |               90 | 71.67 |    D |<br>
 * Lodbrok Umberto |             70 |                   95 |               59 | 74.67 |    D |<br>
 * Paige Ragnar    |             51 |                   68 |               57 | 58.67 |    F |<br>
 * Average         |          59.00 |                77.33 |            68.67 | 68.33 |    D |<br>
 *
 */

public class ResultTable {
    private static final String STUDENT = "Student";
    private static final String TOTAL = "Total";
    private static final String MARK = "Mark";
    private static final String AVERAGE = "Average";
    private static final int TOTAL_WIDTH = TOTAL.length() + 1;
    private static final int MARK_WIDTH = MARK.length() + 1;
    private static final String LEFT_ALIGNMENT = "%-";
    private static final String RIGHT_ALIGNMENT = "%";
    private static final String STRING_SPACE_LINE_SPACE = "s | ";
    private static final String STRING_SPACE_LINE = "s |";
    private static final String DIGITAL_WITH_LINE = ".0f | ";
    private static final String FLOAT_SPACE_LINE_SPACE = ".2f | ";
    private static final String NEW_LINE = "\n";
    private static final int MIN_COLUMNS_NUMBER = 3;
    private static final String WHITESPACE = " ";

    private final List<CourseResult> sourceCourseResultsList = new ArrayList<>();
    private List<String> sourceNameSurnameList = new ArrayList<>();
    private List<String> sourceTaskList = new ArrayList<>();
    private String emptyHeadRow;
    private String emptyStudentRow;
    private String emptyEndRow;

    public void addResultsCourse(CourseResult courseResult) {
        sourceCourseResultsList.add(courseResult);
    }

    public String createResultTable() {
        StringBuilder sbCollector = new StringBuilder();
        addStudents(sourceCourseResultsList);
        addTasks(sourceCourseResultsList);

        createTableBoundsHeadPart();
        sbCollector.append(fillValuesToHeadRow());

        createTableBoundsStudentPart();
        sbCollector.append(fillValuesToStudentRow());

        createTableBoundsEndPart();
        sbCollector.append(fillValuesToEndRow());

        return sbCollector.toString();
    }

    /**
     * The first part performs preparatory calculations.
     */

    private void addStudents(List<CourseResult> list) {
        sourceNameSurnameList = (list.stream()
                .map(CourseResult::getPerson)
                .distinct()
                .sorted(Comparator.comparing(Person::getLastName))
                .map(person -> person.getLastName() + WHITESPACE + person.getFirstName())
                .collect(Collectors.toList()));
    }

    private void addTasks(List<CourseResult> list) {
        sourceTaskList = list.stream()
                .flatMap(courseResult -> courseResult.getTaskResults().keySet().stream())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    private int calculateFirstColumnWidth() {
        return (sourceNameSurnameList.stream()
                .map(String::length)
                .mapToInt(i -> i)
                .max().orElse(0));
    }

    /**
     * The second part calculate borders and fill header of the table.
     *
     * Example:
     *
     *          Student     | Lab 1. Figures | Lab 2. War and Peace | Lab 3. File Tree | Total | Mark |
     *
     */

    private void createTableBoundsHeadPart() {
        StringBuilder stringBuilder = new StringBuilder()
                .append(LEFT_ALIGNMENT)
                .append(calculateFirstColumnWidth())
                .append(STRING_SPACE_LINE_SPACE);
        for (String s : sourceTaskList) {
            stringBuilder
                    .append(RIGHT_ALIGNMENT)
                    .append(s.length())
                    .append(STRING_SPACE_LINE_SPACE);
        }
        stringBuilder
                .append(RIGHT_ALIGNMENT)
                .append(TOTAL_WIDTH - 1)
                .append(STRING_SPACE_LINE_SPACE)
                .append(RIGHT_ALIGNMENT)
                .append(MARK_WIDTH - 1)
                .append(STRING_SPACE_LINE);
        this.emptyHeadRow = stringBuilder.toString();
    }

    private String fillValuesToHeadRow() {
        StringBuilder generalSB = new StringBuilder();
        String[] args = new String[sourceTaskList.size() + MIN_COLUMNS_NUMBER];
        args[0] = STUDENT;
        for (int taskIndex = 0; taskIndex < sourceTaskList.size(); taskIndex++) {
            args[taskIndex + 1] = sourceTaskList.get(taskIndex);
        }
        args[sourceTaskList.size() + 1] = TOTAL;
        args[sourceTaskList.size() + 2] = MARK;
        String personalResult = String.format(emptyHeadRow, args);
        generalSB.append(personalResult).append(NEW_LINE);
        return generalSB.toString();
    }

    /**
     *The third part calculate borders and fill Person's results for all tasks.
     *
     * Example:
     *
     *          Student         | Lab 1. Figures | Lab 2. War and Peace | Lab 3. File Tree | Total | Mark |
     *          ------------------------------------------------------------------------------------------
     *          Eco Johnny      |             56 |                   69 |               90 | 71.67 |    D |
     *          Lodbrok Umberto |             70 |                   95 |               59 | 74.67 |    D |
     *          Paige Ragnar    |             51 |                   68 |               57 | 58.67 |    F |
     *
     */

    private void createTableBoundsStudentPart() {
        StringBuilder stringBuilder = new StringBuilder()
                .append(LEFT_ALIGNMENT)
                .append(calculateFirstColumnWidth())
                .append(STRING_SPACE_LINE_SPACE);
        for (String s : sourceTaskList) {
            stringBuilder
                    .append(RIGHT_ALIGNMENT)
                    .append(s.length())
                    .append(DIGITAL_WITH_LINE);
        }
        stringBuilder
                .append(RIGHT_ALIGNMENT)
                .append(TOTAL_WIDTH - 1)
                .append(FLOAT_SPACE_LINE_SPACE)
                .append(RIGHT_ALIGNMENT)
                .append(MARK_WIDTH - 1)
                .append(STRING_SPACE_LINE);
        this.emptyStudentRow = stringBuilder.toString();
    }

    private String fillValuesToStudentRow() {
        StringBuilder generalSB = new StringBuilder();
        Object[] args = new Object[sourceTaskList.size() + MIN_COLUMNS_NUMBER];

        for (int personIndex = 0; personIndex < sourceNameSurnameList.size(); personIndex++) {
            args[0] = sourceNameSurnameList.get(personIndex);
            for (int taskIndex = 0; taskIndex < sourceTaskList.size(); taskIndex++) {
                args[taskIndex + 1] = (double)(getPersonalResultPerTask(personIndex, taskIndex));
            }
            args[sourceTaskList.size() + 1] = calculateAveragePersonalResult(args);
            args[sourceTaskList.size() + 2] = defineMark(args);
            String personalResult = String.format(Locale.US, emptyStudentRow, args);
            generalSB.append(personalResult).append(NEW_LINE);
        }
        return generalSB.toString();
    }

    private int getPersonalResultPerTask(int personIndex, int taskIndex) {
        return sourceCourseResultsList.stream()
                .filter(cr -> ((cr.getPerson().getLastName() + WHITESPACE +
                        cr.getPerson().getFirstName()).equals(sourceNameSurnameList.get(personIndex))))
                .map(courseResult -> courseResult.getTaskResults().entrySet())
                .flatMap(Collection::stream)
                .filter(entry -> entry.getKey().equals(sourceTaskList.get(taskIndex)))
                .map(Map.Entry::getValue)
                .findAny().orElse(0);
    }

    /**
     *The fourth part calculate borders and fill Average results for all tasks.
     *
     * Example:
     *
     *          Student         | Lab 1. Figures | Lab 2. War and Peace | Lab 3. File Tree | Total | Mark |
     *          ------------------------------------------------------------------------------------------
     *          Eco Johnny      |             56 |                   69 |               90 | 71.67 |    D |
     *          Lodbrok Umberto |             70 |                   95 |               59 | 74.67 |    D |
     *          Paige Ragnar    |             51 |                   68 |               57 | 58.67 |    F |
     *          ------------------------------------------------------------------------------------------
     *          Average         |          59.00 |                77.33 |            68.67 | 68.33 |    D |
     *
     */

    private void createTableBoundsEndPart() {
        StringBuilder stringBuilder = new StringBuilder()
                .append(LEFT_ALIGNMENT)
                .append(calculateFirstColumnWidth())
                .append(STRING_SPACE_LINE_SPACE);
        for (String s : sourceTaskList) {
            stringBuilder
                    .append(RIGHT_ALIGNMENT)
                    .append(s.length())
                    .append(FLOAT_SPACE_LINE_SPACE);
        }
        stringBuilder
                .append(RIGHT_ALIGNMENT)
                .append(TOTAL_WIDTH - 1)
                .append(FLOAT_SPACE_LINE_SPACE)
                .append(RIGHT_ALIGNMENT)
                .append(MARK_WIDTH - 1)
                .append(STRING_SPACE_LINE);
        this.emptyEndRow = stringBuilder.toString();
    }

    private String fillValuesToEndRow() {
        StringBuilder generalSB = new StringBuilder();
        Object[] args = new Object[sourceTaskList.size() + MIN_COLUMNS_NUMBER];
        args[0] = AVERAGE;
        for (int taskIndex = 0; taskIndex < sourceTaskList.size(); taskIndex++) {
            args[taskIndex + 1] = getAverageResultPerTask(taskIndex);
        }
        args[sourceTaskList.size() + 1] = calculateAveragePersonalResult(args);
        args[sourceTaskList.size() + 2] = defineMark(args);
        String personalResult = String.format(Locale.US, emptyEndRow, args);
        generalSB.append(personalResult);
        return generalSB.toString();
    }

    private Double getAverageResultPerTask(int taskIndex) {
        return sourceCourseResultsList.stream()
                .map(courseResult -> courseResult.getTaskResults().entrySet())
                .flatMap(Collection::stream)
                .filter(entry -> entry.getKey().equals(sourceTaskList.get(taskIndex)))
                .map(Map.Entry::getValue)
                .mapToDouble(d -> d)
                .sum() / sourceNameSurnameList.size();
    }

    private double calculateAveragePersonalResult(Object[] args) {
        double sum = 0;
        for (int i = 1; i <= sourceTaskList.size(); i++) {
            sum += (double) args[i];
        }
        return (sum / sourceTaskList.size());
    }

    private String defineMark(Object[] args) {
        double average = (double) args[sourceTaskList.size() + 1];
        if (average > 90 && average <= 100) {
            return "A";
        } else if (average >= 83) {
            return "B";
        } else if (average >= 75) {
            return "C";
        } else if (average >= 68) {
            return "D";
        } else if (average >= 60) {
            return "E";
        } else return "F";
    }
}
