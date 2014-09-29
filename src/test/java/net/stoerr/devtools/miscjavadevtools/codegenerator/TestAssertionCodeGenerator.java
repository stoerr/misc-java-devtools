package net.stoerr.devtools.miscjavadevtools.codegenerator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import java.lang.annotation.ElementType;
import java.util.concurrent.atomic.AtomicReference;

import net.stoerr.devtools.miscjavadevtools.testutils.SimpleBeanFiller;
import net.stoerr.devtools.miscjavadevtools.testutils.TestSimpleBeanFiller.TestSimpleBean;
import net.stoerr.devtools.miscjavadevtools.testutils.TestSimpleBeanFiller.TestSimpleBean2;

import org.junit.Test;

/**
 * Test for {@link AssertionCodeGenerator}.
 * 
 * @author hps
 */
public class TestAssertionCodeGenerator {

    /** Test der Assertions fuer uninstantiierte Attribute. */
    @Test
    public void testNulled() {
        final TestSimpleBean bean = new TestSimpleBean();
        final String assertions = AssertionCodeGenerator.makeAssertions("bean", bean);
        // System.out.println(assertions);
        assertEquals("import static org.junit.Assert.*;\n" + "\n" + "assertNull(bean + \"\\n\", bean.getAcalendar());\n"
                + "assertNull(bean + \"\\n\", bean.getAdate());\n" + "assertNull(bean + \"\\n\", bean.getAlong());\n"
                + "assertEquals(bean + \"\\n\", 0, bean.getAnint());\n" + "assertNull(bean + \"\\n\", bean.getAninteger());\n"
                + "assertNull(bean + \"\\n\", bean.getAstring());\n" + "assertFalse(bean + \"\\n\", bean.isAboolean());\n" + "" + "", assertions);

        // the actual generated code comes here:
        assertNull(bean + "\n", bean.getAcalendar());
        assertNull(bean + "\n", bean.getAdate());
        assertNull(bean + "\n", bean.getAlong());
        assertEquals(bean + "\n", 0, bean.getAnint());
        assertNull(bean + "\n", bean.getAninteger());
        assertNull(bean + "\n", bean.getAstring());
        assertFalse(bean + "\n", bean.isAboolean());
    }

    /** Test der Assertions fuer instantiierte Attribute. */
    @Test
    public void testFilled() {
        final TestSimpleBean bean = SimpleBeanFiller.fillBean(new TestSimpleBean(), 42);
        final String assertions = AssertionCodeGenerator.makeAssertions("bean", bean);
        // System.out.println(assertions);

        assertEquals("import static org.junit.Assert.*;\n" + "\n"
                + "assertEquals(bean + \"\\n\", 1159788095000L, bean.getAcalendar().getTimeInMillis());\n"
                + "assertEquals(bean + \"\\n\", 953325871000L, bean.getAdate().getTime());\n"
                + "assertEquals(bean + \"\\n\", Long.valueOf(4273966635L), bean.getAlong());\n"
                + "assertEquals(bean + \"\\n\", 860283939, bean.getAnint());\n"
                + "assertEquals(bean + \"\\n\", Integer.valueOf(388448704), bean.getAninteger());\n"
                + "assertEquals(bean + \"\\n\", \"Astring#42\", bean.getAstring());\n" + "assertFalse(bean + \"\\n\", bean.isAboolean());\n",
                assertions);

        // the actual generated code comes here:
        assertEquals(bean + "\n", 1159788095000L, bean.getAcalendar().getTimeInMillis());
        assertEquals(bean + "\n", 953325871000L, bean.getAdate().getTime());
        assertEquals(bean + "\n", Long.valueOf(4273966635L), bean.getAlong());
        assertEquals(bean + "\n", 860283939, bean.getAnint());
        assertEquals(bean + "\n", Integer.valueOf(388448704), bean.getAninteger());
        assertEquals(bean + "\n", "Astring#42", bean.getAstring());
        assertFalse(bean + "\n", bean.isAboolean());
    }

    @Test
    public void testEnum() {
        assertEquals("assertEquals(var + \"\\n\", java.lang.annotation.ElementType.FIELD, var.get())",
                AssertionCodeGenerator.makeStatement("var", "get", ElementType.FIELD, ElementType.class));
        final AtomicReference<ElementType> var = new AtomicReference<ElementType>(ElementType.FIELD);

        // the actual generated code comes here:
        assertEquals(var + "\n", java.lang.annotation.ElementType.FIELD, var.get());
    }

    /** Test der Assertions fuer uninstantiierte Attribute mit Generics. */
    @Test
    public void testGenericNull() {
        final TestSimpleBean2 bean = new TestSimpleBean2();
        final String assertions = AssertionCodeGenerator.makeAssertions("bean", bean);
        // System.out.println(assertions);
        assertEquals("import static org.junit.Assert.*;\n" + "\n" + "assertNull(bean + \"\\n\", bean.getList());\n"
                + "assertNull(bean + \"\\n\", bean.getMap());\n" + "assertNull(bean + \"\\n\", bean.getSet());\n", assertions);

        // the actual generated code comes here:
        assertNull(bean + "\n", bean.getList());
        assertNull(bean + "\n", bean.getMap());
        assertNull(bean + "\n", bean.getSet());
    }

    /** Test der Assertions fuer instantiierte Attribute mit Generics. */
    @Test
    public void testGenericFilled() {
        final TestSimpleBean2 bean = SimpleBeanFiller.fillBean(new TestSimpleBean2(), 42);
        final String assertions = AssertionCodeGenerator.makeAssertions("bean", bean);
        System.out.println(assertions);

        assertEquals(
                "import static org.junit.Assert.*;\n"
                        + "\n"
                        + "assertEquals(bean + \"\\n\", \"[List$0#42, List$1#42, List$2#42]\", String.valueOf(bean.getList()));\n"
                        + "assertEquals(bean + \"\\n\", \"{1337253675=Map$2#42, 514164281=Map$0#42, 1221775182=Map$1#42}\", String.valueOf(bean.getMap()));\n"
                        + "assertEquals(bean + \"\\n\", \"[A, H, Z]\", String.valueOf(bean.getSet()));\n", assertions);

        // the actual generated code comes here:
        assertEquals(bean + "\n", "[List$0#42, List$1#42, List$2#42]", String.valueOf(bean.getList()));
        assertEquals(bean + "\n", "{1337253675=Map$2#42, 514164281=Map$0#42, 1221775182=Map$1#42}", String.valueOf(bean.getMap()));
        assertEquals(bean + "\n", "[A, H, Z]", String.valueOf(bean.getSet()));
    }
}
