package net.stoerr.devtools.miscjavadevtools;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Used for logging purposes whenever a stacktrace should be logged but no exception should be thrown.
 * @author %created_by: hps %
 * @version %version: 1 %
 */
public class StacktraceForLogging extends Exception {

    /**
     * Used for logging purposes whenever a stacktrace should be logged but no exception should be thrown, mainly during
     * debugging. Use e.g.<br/>
     * <code>LOGGER.error("Blabla", new StacktraceForLogging().withCleanedStacktrace());</code>
     */
    public StacktraceForLogging() {
        super("Stacktrace for logging purposes - this exception is not thrown.");
    }

    private static final Pattern DEFAULT_CLEANUP_PATTERN = Pattern.compile("\\$|Cglib|cglib.proxy|java.lang.reflect|org.springframework.aop|org.apache.catalina|net.bull.javamelody|sun.reflect");

    /** Cleans out all lines that do *not* start with <code>de.</code> */
    private static final Pattern ONLY_DE_LINES_CLEANUP_PATTERN = Pattern.compile("^(?!de\\.)|\\$");

    /**
     * Removes the annoying stacktrace lines from AspectJ / CGLib / reflection / Tomcat:
     * {@value #DEFAULT_CLEANUP_PATTERN}.
     * @return this
     */
    public StacktraceForLogging withCleanedStacktrace() {
        cleanupStacktrace(DEFAULT_CLEANUP_PATTERN);
        return this;
    }

    /**
     * Leaves only lines starting with <code>de.</code> in the stacktrace: {@value #ONLY_DE_LINES_CLEANUP_PATTERN}.
     * @return this
     */
    public StacktraceForLogging withOnlyDeLines() {
        cleanupStacktrace(ONLY_DE_LINES_CLEANUP_PATTERN);
        return this;
    }

    /** Removes all lines where the class does not correspond to cleanupPattern */
    private void cleanupStacktrace(Pattern cleanupPattern) {
        List<StackTraceElement> lines = new ArrayList<>();
        boolean cleaned = false;
        for (StackTraceElement element : getStackTrace()) {
            if (!cleanupPattern.matcher(element.getClassName()).find()) {
                lines.add(element);
                cleaned = true;
            }
        }
        if (cleaned) {
            lines.add(0, new StackTraceElement(getClass().toString(), "--stacktrace-was-cleaned-up--",
                    "StacktraceForLogging.java", 0));
        }
        setStackTrace(lines.toArray(new StackTraceElement[lines.size()]));
    }
}
