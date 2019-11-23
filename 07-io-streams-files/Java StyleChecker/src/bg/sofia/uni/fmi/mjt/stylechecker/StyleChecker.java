package bg.sofia.uni.fmi.mjt.stylechecker;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * Checks adherence to Java style guidelines.
 */
public class StyleChecker {

    static final String WILDCARDS_IN_IMPORTS =
            "// FIXME Wildcards are not allowed in import statements";
    static final String ONE_STATEMENT_PER_LINE =
            "// FIXME Only one statement per line is allowed";
    static final String LINE_LENGTH_EXCEEDED =
            "// FIXME Length of line should not exceed 100 characters";
    static final String OPENING_BRACKETS_ON_SAME_LINE =
            "// FIXME Opening brackets should be placed on the same line as the declaration";
    static final String INVALID_PACKAGE_NAME =
            "// FIXME Package name should not contain upper-case letters or underscores";

    /**
     * For each line from the given {@code source} performs code style checks
     * and writes to the {@code output}
     * 1. a fix-me comment line for each style violation in the input line, if any
     * 2. the input line itself.
     *
     * @param source
     * @param output
     */
    public void checkStyle(Reader source, Writer output) {
        try (BufferedReader bufferedReader = new BufferedReader(source);
             BufferedWriter bufferedWriter = new BufferedWriter(output)) {

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                List<String> violations = getViolations(line);
                for (String violation : violations) {
                    bufferedWriter.write(violation);
                    bufferedWriter.newLine();
                }
                bufferedWriter.write(line);
                bufferedWriter.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<String> getViolations(String line) {
        List<String> violations = new ArrayList<>();
        String trim = line.trim();

        if (trim.matches("^([^;]+[;]){2,}$")) {
            violations.add(ONE_STATEMENT_PER_LINE);
        }
        if (trim.matches("^import .+\\*;$")) {
            violations.add(WILDCARDS_IN_IMPORTS);
        }
        if (trim.matches("^\\{.*$")) {
            violations.add(OPENING_BRACKETS_ON_SAME_LINE);
        }
        if (trim.matches("^package ([a-z.]*[A-Z_]+[a-z.]*)+;$")) {
            violations.add(INVALID_PACKAGE_NAME);
        }
        if (trim.matches("^(?!import).{101,}$")) {
            violations.add(LINE_LENGTH_EXCEEDED);
        }
        return violations;
    }
}