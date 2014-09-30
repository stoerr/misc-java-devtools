package net.stoerr.devtools.miscjavadevtools.codegenerator;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

/**
 * Tests the more complex methods of {@link ValueCodeGenerator}.
 * @author Hans-Peter Störr www.stoerr.net
 */
public class TestValueCodeGenerator {

    private List<Integer> list;
    private Set<Integer> set;
    private Map<Integer, String> map;

    @Test
    public void testCreateList() throws Exception {
        final Type type = TestValueCodeGenerator.class.getDeclaredField("list").getGenericType();
        list = java.util.Collections.<Integer> emptyList();
        assertEquals("java.util.Collections.<Integer> emptyList()", ValueCodeGenerator.makeExpression(list, type, true));
        list = java.util.Arrays.asList(1, 2, 3, 4);
        assertEquals("java.util.Arrays.asList(1, 2, 3, 4)", ValueCodeGenerator.makeExpression(list, type, true));
    }

    @Test
    public void testCreateSet() throws Exception {
        final Type type = TestValueCodeGenerator.class.getDeclaredField("set").getGenericType();
        set = java.util.Collections.<Integer> emptySet();
        assertEquals("java.util.Collections.<Integer> emptySet()", ValueCodeGenerator.makeExpression(set, type, true));
        // this is the actually generated code:
        set = new java.util.TreeSet<Integer>(java.util.Arrays.asList(1, 2, 3, 4));
        assertEquals("new java.util.TreeSet<Integer>(java.util.Arrays.asList(1, 2, 3, 4))",
                ValueCodeGenerator.makeExpression(set, type, true));
    }

    @Test
    public void testCreateMap() throws Exception {
        final Type type = TestValueCodeGenerator.class.getDeclaredField("map").getGenericType();
        map = java.util.Collections.<Integer, String> emptyMap();
        assertEquals("java.util.Collections.<Integer, String> emptyMap()",
                ValueCodeGenerator.makeExpression(map, type, true));
        map = java.util.Collections.<Integer, String> singletonMap(1, "p1");
        assertEquals("java.util.Collections.<Integer, String> singletonMap(1, \"p1\")",
                ValueCodeGenerator.makeExpression(map, type, true));
        // this is the actually generated code:
        map = new java.util.TreeMap<Integer, String>() {
            /**
         * 
         */
            private static final long serialVersionUID = 1L;

            {
                put(1, "p1");
                put(2, "p2");
            }
        };
        assertEquals("new java.util.TreeMap<Integer, String>() {{ put(1, \"p1\"); put(2, \"p2\"); }}",
                ValueCodeGenerator.makeExpression(map, type, true));
    }
}
