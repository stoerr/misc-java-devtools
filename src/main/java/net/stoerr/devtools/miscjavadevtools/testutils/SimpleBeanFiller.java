package net.stoerr.devtools.miscjavadevtools.testutils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;

import net.stoerr.devtools.miscjavadevtools.codegenerator.ValueCodeGenerator;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Utility that fills all attributes Java Beans with predefined values for
 * testing purposes.
 * 
 * @author hps
 */
public class SimpleBeanFiller {

    private final int perturbation;

    /**
     * Fills a Java Bean with values determined by the property name and the
     * perturbation.
     * 
     * @param bean
     *            bean to fill, not null.
     * @param perturbation
     *            a value that is used to change the values accordingly. Different
     *            perturbations give different fillings.
     * @return bean
     */
    public static <T> T fillBean(final T bean, final int perturbation) {
        new SimpleBeanFiller(perturbation).fillBean(bean);
        return bean;
    }

    /**
     * @param perturbation
     *            a value that is used to change the values accordingly. Different
     *            perturbations give different fillings.
     */
    public SimpleBeanFiller(final int perturbation) {
        this.perturbation = perturbation;
    }

    /**
     * Fills a Java Bean with values determined by the property name and the
     * perturbation.
     * 
     * @param bean
     * @deprecated to be independent from further changes please use {@link #beanFillerCode(String, Object)} to generate the
     *             corresponding code.
     */
    @Deprecated
    public void fillBean(final Object bean) {
        try {
            for (final Method method : bean.getClass().getMethods()) {
                if (!Modifier.isPublic(method.getModifiers()) || method.getParameterTypes().length != 1 || !method.getName().startsWith("set")) {
                    continue;
                }
                final Object value = perturbedValue(propertyName(method), method.getGenericParameterTypes()[0]);
                method.invoke(bean, value);
            }
        } catch (final RuntimeException e) {
            throw e;
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns Java code to fill the bean.
     * 
     * @param bean
     */
    public String beanFillerCode(String varname, final Object bean) {
        final StringBuilder buf = new StringBuilder();
        try {
            final Method[] methods = bean.getClass().getMethods();
            Arrays.sort(methods, METHODNAMECOMPARATOR);
            for (final Method method : methods) {
                if (!Modifier.isPublic(method.getModifiers()) || method.getParameterTypes().length != 1 || !method.getName().startsWith("set")) {
                    continue;
                }

                final Type type = method.getGenericParameterTypes()[0];
                final Object value = perturbedValue(propertyName(method), type);
                buf.append(varname).append(".").append(method.getName()).append('(').append(ValueCodeGenerator.makeExpression(value, type, true))
                        .append(");\n");
            }
        } catch (final RuntimeException e) {
            throw e;
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
        return buf.toString();
    }

    /** Prints code to fill the bean with generated values. */
    public static void printBeanFillerCode(String varname, Object bean, int perturbation) {
        System.out.println(new SimpleBeanFiller(perturbation).beanFillerCode(varname, bean));
    }

    /**
     * String representation of the bean to compare with assert
     */
    public static String stringRep(final Object bean) {
        String rep = ReflectionToStringBuilder.toString(bean, ToStringStyle.MULTI_LINE_STYLE);
        rep = rep.replaceAll("@[0-9a-f]{6,8}", "");
        rep = rep.replaceAll("\\r", "");
        rep = rep.replaceAll("java.util.GregorianCalendar\\[time=(-?[0-9]*),[^\\[\\]]*(\\[[^\\[\\]]*\\[[^\\[\\]]*]])[^\\[\\]]*]?",
                "java.util.GregorianCalendar[time=$1]");
        return rep;
    }

    /** Basic name of the property - without get/set/is. */
    private String propertyName(final Method method) {
        final String name = method.getName();
        if (name.startsWith("set") || name.startsWith("get")) { return name.substring(3); }
        if (name.startsWith("is")) { return name.substring(2); }
        throw new IllegalArgumentException("Bug: Unknow attribute accessor name " + name);
    }

    /**
     * Yields a value of Class clazz that depends on name and perturbation.
     * Complete with more types as necessary.
     */
    private Object perturbedValue(final String name, final Type type) {
        final int pseudorandomnumber = Math.abs(1322837333 * hash(name) + 486187739 * perturbation);

        if (String.class.equals(type)) {
            return name + "#" + perturbation;
        } else if (Boolean.TYPE.equals(type) || Boolean.class.equals(type)) {
            return 1 == pseudorandomnumber % 2;
        } else if (Integer.TYPE.equals(type) || Integer.class.equals(type)) {
            return pseudorandomnumber + 256;
        } else if (Character.TYPE.equals(type) || Character.class.equals(type)) {
            return (char) (pseudorandomnumber % 32 + 'A');
        } else if (Long.TYPE.equals(type) || Long.class.equals(type)) {
            return pseudorandomnumber + (long) Integer.MAX_VALUE;
        } else if (GregorianCalendar.class.equals(type)) {
            final GregorianCalendar cal = new GregorianCalendar(Locale.GERMANY);
            cal.setTimeInMillis(new java.util.Date(105, 05, 04, 03, 02, 01).getTime() + 1000L * (pseudorandomnumber % 70000000L));
            return cal;

        } else if (type instanceof Class) {
            final Class<?> clazz = (Class<?>) type;
            if (clazz.isAssignableFrom(Date.class)) {
                return new Date(new java.util.Date(100, 01, 02, 03, 04, 05).getTime() + 1000L * (pseudorandomnumber % 70000000L));
            } else if (clazz.isEnum()) {
                final Object[] values = clazz.getEnumConstants();
                return values[pseudorandomnumber % values.length];
            }
        } else if (type instanceof ParameterizedType) { // Generation for generics
                                                        // like List, Map, Set.
            final ParameterizedType ptype = (ParameterizedType) type;
            final Class<?> clazz = (Class<?>) ptype.getRawType();
            final int cnt = pseudorandomnumber % 2 + 2;

            if (clazz.isAssignableFrom(ArrayList.class)) {
                final Type parmType = ptype.getActualTypeArguments()[0];
                final ArrayList<Object> res = new ArrayList<Object>();
                for (int i = 0; i < cnt; ++i) {
                    res.add(perturbedValue(name + "$" + i, parmType));
                }
                return res;

            } else if (clazz.isAssignableFrom(HashMap.class)) {
                final Type keyType = ptype.getActualTypeArguments()[0];
                final Type valueType = ptype.getActualTypeArguments()[1];
                final HashMap<Object, Object> res = new HashMap<Object, Object>();
                for (int i = 0; i < cnt; ++i) {
                    res.put(perturbedValue(name + "$" + i, keyType), perturbedValue(name + "$" + i, valueType));
                }
                return res;
            } else if (clazz.isAssignableFrom(HashSet.class)) {
                final Type valueType = ptype.getActualTypeArguments()[0];
                final HashSet<Object> res = new HashSet<Object>();
                for (int i = 0; i < cnt; ++i) {
                    res.add(perturbedValue(name + "$" + i, valueType));
                }
                return res;
            }
        }

        throw new IllegalArgumentException("No example generation for type " + type + " implemented. Please extend perturbedValue.");
    }

    /** More sensible hash than {@link String#hashCode()}. */
    private int hash(final String val) {
        int res = 92821;
        for (int i = 0; i < val.length(); ++i) {
            res = (res + val.charAt(i)) * 92821;
        }
        return res;
    }

    private static final Comparator<Method> METHODNAMECOMPARATOR = new Comparator<Method>() {

        @Override
        public int compare(Method o1, Method o2) {
            return o1.getName().compareTo(o2.getName());
        }
    };

}
