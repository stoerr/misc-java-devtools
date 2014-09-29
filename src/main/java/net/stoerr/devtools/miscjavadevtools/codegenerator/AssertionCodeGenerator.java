package net.stoerr.devtools.miscjavadevtools.codegenerator;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.Map;

import org.apache.commons.lang3.reflect.FieldUtils;

/**
 * Codegenerator to generate assert statements that the contents of a Java Bean
 * are as given in a template. Use
 * AssertionCodeGenerator.printAssertions(somebean) in a unittest and grasp the
 * generated assert statements from standard output.
 * 
 * @author hps
 */
public class AssertionCodeGenerator {

    /**
     * Prints assert statements about the properties of a Java Bean to standard
     * output for cutting and pasting them to a Unittest.
     * 
     * @param variablename
     *            name of the variable that will contain the bean in the code.
     * @param javabean
     *            a simple Java Bean
     */
    public static void printAssertions(final String variablename, final Object javabean) {
        System.out.println(makeAssertions(variablename, javabean));
    }

    /**
     * Generates assert statements about the properties of a Java Bean to standard
     * output for cutting and pasting them to a Unittest.
     * 
     * @param variablename
     *            name of the variable that will contain the bean in the code.
     * @param bean
     *            a simple Java Bean
     * @return the assert statements. They require import static
     *         org.junit.Assert.*;
     */
    public static String makeAssertions(final String variablename, final Object bean) {
        if (null == bean) { return "assertNull(" + variablename + ");\n"; }
        final StringBuilder buf = new StringBuilder("import static org.junit.Assert.*;\n\n");
        final StringBuilder skipped = new StringBuilder();
        final Method[] methods = bean.getClass().getMethods();
        Arrays.sort(methods, new Comparator<Method>() {
            @Override
            public int compare(Method o1, Method o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        for (final Method method : methods) {
            if (method.getParameterTypes().length != 0 || "getClass".equals(method.getName())) {
                continue;
            }
            if (!method.getName().startsWith("get") && !method.getName().startsWith("is")) {
                continue;
            }
            if (skipField(bean.getClass(), method)) {
                skipped.append("// Skipping " + method.getName() + " since field is transient.\n");
                continue;
            }
            Object value;
            try {
                value = method.invoke(bean);
                final String statement = makeStatement(variablename, method.getName(), value, method.getReturnType()) + ";\n";
                if (statement.startsWith("//")) {
                    skipped.append(statement);
                } else {
                    buf.append(statement);
                }
            } catch (final Exception e) {
                skipped.append("// EXCEPTION getting value for " + method.getName() + " : " + e);
            }
        }
        return buf.toString() + skipped.toString();
    }

    /**
     * Checks whether a field is transient. It is probably calculated and we do
     * not need to check it.
     */
    private static boolean skipField(final Class<?> beanclass, final Method method) {
        String fieldname = method.getName();
        if (fieldname.startsWith("get")) {
            fieldname = fieldname.substring(3);
        } else {
            fieldname = fieldname.substring(2); // is...
        }
        fieldname = fieldname.substring(0, 1).toLowerCase() + fieldname.substring(1);
        final Field field = FieldUtils.getField(beanclass, fieldname);
        if (null != field) { return Modifier.isTransient(field.getModifiers()); }
        return false; // can't determine corresponding field
    }

    static String makeStatement(final String variablename, final String methodname, final Object value, final Class<?> valueClazz) {
        final String accessor = variablename + "." + methodname + "()";
        final String printvariable = variablename + " + \"\\n\", ";
        if (null == value) { return "assertNull(" + printvariable + accessor + ")"; }
        if (value instanceof String || value instanceof Number || value instanceof Character || value instanceof Enum) { return "assertEquals("
                + printvariable + ValueCodeGenerator.makeExpression(value, valueClazz, false) + ", " + accessor + ")"; }
        if (value instanceof Boolean) {
            if (((Boolean) value).booleanValue()) {
                return "assertTrue(" + printvariable + accessor + ")";
            } else {
                return "assertFalse(" + printvariable + accessor + ")";
            }
        }
        if (value instanceof java.util.Date) { return "assertEquals(" + printvariable + ((java.util.Date) value).getTime() + "L, " + accessor
                + ".getTime())"; }
        if (value instanceof GregorianCalendar) { return "assertEquals(" + printvariable + ((GregorianCalendar) value).getTimeInMillis() + "L, "
                + accessor + ".getTimeInMillis())"; }
        if (value instanceof Collection<?>) {
            if (((Collection<?>) value).isEmpty()) { return "assertTrue(\"\" + " + variablename + " + \"\\n\" + " + accessor + ", " + accessor
                    + ".isEmpty())"; }
            return "assertEquals(" + printvariable + '"' + value.toString() + '"' + ", String.valueOf(" + accessor + "))";
        }
        if (value instanceof Map<?, ?>) {
            if (((Map<?, ?>) value).isEmpty()) { return "assertTrue(\"\" + " + variablename + " + \"\\n\" + " + accessor + ", " + accessor
                    + ".isEmpty())"; }
            return "assertEquals(" + printvariable + '"' + value.toString() + '"' + ", String.valueOf(" + accessor + "))";
        }
        return "// Not yet implemented: assertions about type " + value.getClass();
    }

}
