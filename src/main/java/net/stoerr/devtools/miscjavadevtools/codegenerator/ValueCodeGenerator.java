package net.stoerr.devtools.miscjavadevtools.codegenerator;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Generates Java-Code for a simple value or a collection of simple values.
 * 
 * @author hps
 */
public class ValueCodeGenerator {

    /**
     * Generates Java-Code that recreates value. Note: this is often not quite
     * exact for brevity (e.g. ignores timezones for {@link Date}).
     * 
     * @param value
     * @return a valid Java-Expression
     * @throws IllegalArgumentException
     *             if the value type is not supported
     */
    public static String makeExpression(final Object value) throws IllegalArgumentException {
        return makeExpression(value, null == value ? null : value.getClass(), true);
    }

    /**
     * Generates Java-Code that recreates value. Note: this is often not quite
     * exact for brevity (e.g. ignores timezones for {@link Date}).
     * 
     * @param value
     * @param type
     *            required type - important e.g. for simple types or parameterized
     *            stuff
     * @param autoboxingEnabled
     *            iff true we do not generate Integer.valueOf etc. - this is useful
     *            if you generate Code like assertEquals(..,..).
     * @return a valid Java-Expression
     * @throws IllegalArgumentException
     *             if the value type is not supported
     */
    public static String makeExpression(final Object value, final Type type, final boolean autoboxingEnabled) throws IllegalArgumentException {
        if (null == value) { return "null"; }

        // primitive types and derivates
        if (type.equals(Integer.TYPE) || (type.equals(Integer.class) && autoboxingEnabled)) { return "" + value; }
        if (type.equals(Integer.class)) { return "Integer.valueOf(" + value + ")"; }
        if (type.equals(Long.TYPE) || (type.equals(Long.class) && autoboxingEnabled)) { return value + "L"; }
        if (type.equals(Long.class)) { return "Long.valueOf(" + value + "L)"; }
        if (type.equals(String.class)) { return "\"" + value + "\""; // needs quoting; add when needed
        }
        if (type.equals(Character.TYPE) || (type.equals(Character.class) && autoboxingEnabled)) { return "'" + value + "'"; // needs quoting; add when
// needed
        }
        if (type.equals(Character.class)) { return "Character.valueOf('" + value + "')"; }
        if (type.equals(Boolean.TYPE) || (type.equals(Boolean.class) && autoboxingEnabled)) { return value.toString(); }
        if (type.equals(Boolean.class)) { return "Boolean." + String.valueOf(value).toUpperCase(); }

        // complex stuff
        if (type.equals(java.util.Date.class)) { return "new java.util.Date(" + makeExpression(((Date) value).getTime()) + ")"; }
        if (type.equals(java.sql.Date.class)) { return "new java.sql.Date(" + makeExpression(((Date) value).getTime()) + ")"; }

        if (type instanceof ParameterizedType) { return makeParameterizedType(value, (ParameterizedType) type); }

        if (value instanceof Enum) {
            final Enum<?> enValue = (Enum<?>) value;
            return enValue.getDeclaringClass().getName() + "." + enValue.name();
        }
        throw new IllegalArgumentException("Type not yet supported; extend makeExpression for " + value.getClass() + " with type " + type);
    }

    /** Handles Lists, Sets, Maps */
    private static String makeParameterizedType(final Object value, final ParameterizedType type) {
        if (value instanceof List) {
            final List<?> list = (List<?>) value;
            final Type elementType = type.getActualTypeArguments()[0];
            if (list.isEmpty()) { return "java.util.Collections.<" + typename(elementType) + "> emptyList()"; }
            return makeList(list, elementType);
        }
        if (value instanceof Set) {
            final Set<?> set = (Set<?>) value;
            final Type elementType = type.getActualTypeArguments()[0];
            if (set.isEmpty()) { return "java.util.Collections.<" + typename(elementType) + "> emptySet()"; }
            return "new java.util.TreeSet<" + typename(elementType) + ">(" + makeList(set, elementType) + ")";
        }
        if (value instanceof Map) {
            final Map<?, ?> map = (Map<?, ?>) value;
            final Type keyType = type.getActualTypeArguments()[0];
            final Type valueType = type.getActualTypeArguments()[1];
            final String signature = "<" + typename(keyType) + ", " + typename(valueType) + ">";
            if (map.isEmpty()) { return "java.util.Collections." + signature + " emptyMap()"; }
            if (1 == map.size()) {
                final Entry<?, ?> entry = map.entrySet().iterator().next();
                return "java.util.Collections." + signature + " singletonMap(" + makeExpression(entry.getKey(), keyType, true) + ", "
                        + makeExpression(entry.getValue(), valueType, true) + ")";
            }
            final StringBuilder buf = new StringBuilder("new java.util.TreeMap" + signature + "() {{ ");
            for (final Entry<?, ?> entry : map.entrySet()) {
                buf.append("put(").append(makeExpression(entry.getKey(), keyType, true)).append(", ");
                buf.append(makeExpression(entry.getValue(), valueType, true)).append("); ");
            }
            return buf + "}}";
        }
        throw new IllegalArgumentException("Type not yet supported; extend makeParameterizedType for " + value.getClass() + " with type " + type);
    }

    /** Java name of the type */
    private static String typename(final Type elementType) {
        if (elementType instanceof Class) {
            final Class<?> clazz = (Class<?>) elementType;
            String name = clazz.getName();
            if (name.startsWith("java.lang.")) {
                name = name.substring("java.lang.".length());
            }
            return name;
        }
        throw new IllegalArgumentException("Type not yet supported - please extend: " + elementType);
    }

    private static String makeList(final Collection<?> list, final Type elementType) throws IllegalArgumentException {
        final StringBuilder buf = new StringBuilder("java.util.Arrays.asList(");
        boolean first = true;
        for (final Object element : list) {
            if (!first) {
                buf.append(", ");
            }
            first = false;
            buf.append(makeExpression(element, elementType, true));
        }
        return buf + ")";
    }

}
