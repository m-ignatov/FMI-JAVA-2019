package bg.sofia.uni.fmi.mjt.stylechecker;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class StyleCheckerTest {

    private static final int MAX_LENGTH = 101;

    private static StyleChecker styleChecker;

    @BeforeClass
    public static void setup() {
        styleChecker = new StyleChecker();
    }

    @Test
    public void givenWildcardImportWhenCheckStyleThenInsertWildcardImportComment() {
        String fileInput = "import java.util.*;";
        Reader reader = new StringReader(fileInput);
        Writer writer = new StringWriter();

        styleChecker.checkStyle(reader, writer);
        String actual = writer.toString();

        assertEquals(StyleChecker.WILDCARDS_IN_IMPORTS
                + System.lineSeparator() + fileInput, actual.strip());
    }

    @Test
    public void givenMultipleStatementsPerLineWhenCheckStyleThenInsertOneStatementPerLineComment() {
        String fileInput = "statement1; statement2;";
        Reader reader = new StringReader(fileInput);
        Writer writer = new StringWriter();

        styleChecker.checkStyle(reader, writer);

        String actual = writer.toString();
        assertEquals(StyleChecker.ONE_STATEMENT_PER_LINE
                + System.lineSeparator() + fileInput, actual.strip());
    }

    @Test
    public void givenOneStatementPerLineWithMultipleSemicolonsWhenCheckStyleThenDoNotInsertComment() {
        String input = "statement1;;;;";
        Reader reader = new StringReader(input);
        Writer writer = new StringWriter();

        styleChecker.checkStyle(reader, writer);

        String actual = writer.toString();
        assertEquals(input, actual.strip());
    }

    @Test
    public void givenOpeningBracketOnSameLineWhenCheckStyleThenInsertOpeningBracketOnSameLineComment() {
        String input = "{ statement;";
        Reader reader = new StringReader(input);
        Writer writer = new StringWriter();

        styleChecker.checkStyle(reader, writer);

        String actual = writer.toString();
        assertEquals(StyleChecker.OPENING_BRACKETS_ON_SAME_LINE
                + System.lineSeparator() + input, actual.strip());
    }

    @Test
    public void givenInvalidPackageNameWhenCheckStyleThenInsertInvalidPackageNameComment() {
        String input = "package com.SU.FMI_UNI.mjt;";
        Reader reader = new StringReader(input);
        Writer writer = new StringWriter();

        styleChecker.checkStyle(reader, writer);

        String actual = writer.toString();
        assertEquals(StyleChecker.INVALID_PACKAGE_NAME
                + System.lineSeparator() + input, actual.strip());
    }

    @Test
    public void givenLineLengthExceededPackageNameWhenCheckStyleThenInsertLineLengthExceededComment() {
        String input = fill('*', MAX_LENGTH);
        Reader reader = new StringReader(input);
        Writer writer = new StringWriter();

        styleChecker.checkStyle(reader, writer);

        String actual = writer.toString();
        assertEquals(StyleChecker.LINE_LENGTH_EXCEEDED
                + System.lineSeparator() + input, actual.strip());
    }

    private String fill(char character, int length) {
        char[] buffer = new char[length];
        Arrays.fill(buffer, character);
        return new String(buffer);
    }
}