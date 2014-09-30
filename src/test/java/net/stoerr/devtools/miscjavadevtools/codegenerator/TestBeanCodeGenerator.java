package net.stoerr.devtools.miscjavadevtools.codegenerator;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;

/**
 * Tests for {@link BeanCodeGenerator}
 * @author Hans-Peter Störr www.stoerr.net
 */
public class TestBeanCodeGenerator {

    public static class TestBeanCodeGeneratorTestBean {
        private String attr;

        public void setAttr(final String attr) {
            this.attr = attr;
        }

        public String getAttr() {
            return attr;
        }

        private TestBeanCodeGeneratorTestBean recursive;

        public void setRecursive(final TestBeanCodeGeneratorTestBean recursive) {
            this.recursive = recursive;
        }

        public TestBeanCodeGeneratorTestBean getRecursive() {
            return recursive;
        }

        private List<Integer> list;

        public void setList(final List<Integer> list) {
            this.list = list;
        }

        public List<Integer> getList() {
            return list;
        }

        private Map<Integer, String> map;

        public void setMap(final Map<Integer, String> map) {
            this.map = map;
        }

        public Map<Integer, String> getMap() {
            return map;
        }
    }

    @Test
    public void testGenerateTestBeanCodeGeneratorTestBean() {
        System.out.println("testGenerateTestBeanCodeGeneratorTestBean");
        // code actually generated by TestBeanCodeGenerator:
        final TestBeanCodeGeneratorTestBean bean = new TestBeanCodeGeneratorTestBean();
        bean.setAttr("bla blu foo");

        final TestBeanCodeGeneratorTestBean testBeanCodeGeneratorTestBean1 = new TestBeanCodeGeneratorTestBean();
        testBeanCodeGeneratorTestBean1.setAttr("inner");

        final Map<Integer, String> map1 = new TreeMap();
        map1.put(Integer.valueOf(17), "ha");
        map1.put(Integer.valueOf(42), "hu!");
        testBeanCodeGeneratorTestBean1.setMap(map1);
        bean.setRecursive(testBeanCodeGeneratorTestBean1);

        final List<Integer> list1 = new ArrayList();
        list1.add(Integer.valueOf(7));
        list1.add(Integer.valueOf(14));
        list1.add(Integer.valueOf(32));
        bean.setList(list1);

        // check this
        final BeanCodeGenerator generator = new BeanCodeGenerator();
        assertEquals("bean", generator.makeBean(bean, "bean"));
        final String code = generator.toString();
        System.out.println(code);
        assertEquals(
                ("import net.stoerr.devtools.miscjavadevtools.codegenerator.TestBeanCodeGenerator.TestBeanCodeGeneratorTestBean;\r\n"
                        + "import java.util.Map;\r\n"
                        + "import java.util.List;\r\n"
                        + "\r\n"
                        + "TestBeanCodeGeneratorTestBean bean = new TestBeanCodeGeneratorTestBean();\r\n"
                        + "bean.setAttr(\"bla blu foo\");\r\n"
                        + "\r\n"
                        + "TestBeanCodeGeneratorTestBean testBeanCodeGeneratorTestBean1 = new TestBeanCodeGeneratorTestBean();\r\n"
                        + "testBeanCodeGeneratorTestBean1.setAttr(\"inner\");\r\n"
                        + "\r\n"
                        + "Map<Integer, String> map1 = new TreeMap();\r\n"
                        + "map1.put(Integer.valueOf(17), \"ha\");\r\n"
                        + "map1.put(Integer.valueOf(42), \"hu!\");\r\n"
                        + "testBeanCodeGeneratorTestBean1.setMap(map1);\r\n"
                        + "bean.setRecursive(testBeanCodeGeneratorTestBean1);\r\n"
                        + "\r\n"
                        + "List<Integer> list1 = new ArrayList();\r\n"
                        + "list1.add(Integer.valueOf(7));\r\n"
                        + "list1.add(Integer.valueOf(14));\r\n"
                        + "list1.add(Integer.valueOf(32));\r\n"
                        + "bean.setList(list1);\r\n" + "").replaceAll("\r", ""), code.replaceAll("\r", ""));

    }

    @Test
    public void testGenerateWithRecursion() {
        System.out.println("testGenerateWithRecursion");
        // code actually generated by makeBean
        final TestBeanCodeGeneratorTestBean bean = new TestBeanCodeGeneratorTestBean();
        bean.setAttr("bla blu foo");

        bean.setRecursive(bean);

        // check this
        final BeanCodeGenerator generator = new BeanCodeGenerator();
        assertEquals("bean", generator.makeBean(bean, "bean"));
        final String code = generator.toString();
        System.out.println(code);
        assertEquals(
                ("import net.stoerr.devtools.miscjavadevtools.codegenerator.TestBeanCodeGenerator.TestBeanCodeGeneratorTestBean;\r\n"
                        + "\r\n"
                        + "TestBeanCodeGeneratorTestBean bean = new TestBeanCodeGeneratorTestBean();\r\n"
                        + "bean.setAttr(\"bla blu foo\");\r\n" + "\r\n" + "bean.setRecursive(bean);\r\n" + "").replaceAll(
                        "\r", ""), code.replaceAll("\r", ""));

    }

    @Test
    public void testGenerateWithRecursion2() {
        System.out.println("testGenerateWithRecursion2");
        // code actually generated by makeBean
        final TestBeanCodeGeneratorTestBean bean = new TestBeanCodeGeneratorTestBean();
        bean.setAttr("bla blu foo");

        final TestBeanCodeGeneratorTestBean testBeanCodeGeneratorTestBean1 = new TestBeanCodeGeneratorTestBean();

        testBeanCodeGeneratorTestBean1.setRecursive(bean);
        bean.setRecursive(testBeanCodeGeneratorTestBean1);

        // check this
        final BeanCodeGenerator generator = new BeanCodeGenerator();
        assertEquals("bean", generator.makeBean(bean, "bean"));
        final String code = generator.toString();
        System.out.println(code);
        assertEquals(
                ("import net.stoerr.devtools.miscjavadevtools.codegenerator.TestBeanCodeGenerator.TestBeanCodeGeneratorTestBean;\r\n"
                        + "\r\n"
                        + "TestBeanCodeGeneratorTestBean bean = new TestBeanCodeGeneratorTestBean();\r\n"
                        + "bean.setAttr(\"bla blu foo\");\r\n"
                        + "\r\n"
                        + "TestBeanCodeGeneratorTestBean testBeanCodeGeneratorTestBean1 = new TestBeanCodeGeneratorTestBean();\r\n"
                        + "\r\n"
                        + "testBeanCodeGeneratorTestBean1.setRecursive(bean);\r\n"
                        + "bean.setRecursive(testBeanCodeGeneratorTestBean1);\r\n" + "").replaceAll("\r", ""),
                code.replaceAll("\r", ""));

    }

}