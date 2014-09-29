package net.stoerr.devtools.miscjavadevtools.testutils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

/**
 * Test for {@link SimpleBeanFiller}.
 * 
 * @author hps
 */
public class TestSimpleBeanFiller {

    @Test
    public void testFillBean() {
        final TestSimpleBean b1 = makeFilledBean(15);
        assertEquals(
                "TestSimpleBean [astring=Astring#15, anint=1102451004, aninteger=630615769, along=4073800890, adate=Mon Dec 25 15:41:24 CET 2000, acalendar=1127621030000, aboolean=true]",
                b1.toString());

        final TestSimpleBean b2 = makeFilledBean(42);
        assertEquals(
                "TestSimpleBean [astring=Astring#42, anint=860283939, aninteger=388448704, along=4273966635, adate=Fri Mar 17 21:44:31 CET 2000, acalendar=1159788095000, aboolean=false]",
                b2.toString());
        assertEquals("net.stoerr.devtools.miscjavadevtools.testutils.TestSimpleBeanFiller$TestSimpleBean[\n" + "  astring=Astring#42\n" + "  anint=860283939\n"
                + "  aninteger=388448704\n" + "  along=4273966635\n" + "  adate=Fri Mar 17 21:44:31 CET 2000\n"
                + "  acalendar=java.util.GregorianCalendar[time=1159788095000]\n" + "  aboolean=false\n]", SimpleBeanFiller.stringRep(b2));
    }

    private TestSimpleBean makeFilledBean(final int i) {
        final SimpleBeanFiller filler = new SimpleBeanFiller(i);
        final TestSimpleBean bean = new TestSimpleBean();
        filler.fillBean(bean);
        return bean;
    }

    @Test
    public void testGenericFiller() {
        final TestSimpleBean2 bean = new TestSimpleBean2();
        final SimpleBeanFiller filler = new SimpleBeanFiller(42);
        filler.fillBean(bean);

        assertNotNull(bean.getList());
        assertTrue(bean.getList() instanceof ArrayList);
        assertEquals("[List$0#42, List$1#42, List$2#42]", bean.getList().toString());

        assertNotNull(bean.getMap());
        assertTrue(bean.getMap() instanceof HashMap);
        assertEquals("{1337253675=Map$2#42, 514164281=Map$0#42, 1221775182=Map$1#42}", bean.getMap().toString());

        assertNotNull(bean.getSet());
        assertTrue(bean.getSet() instanceof HashSet);
        assertEquals("[A, H, Z]", bean.getSet().toString());
    }

    public static class TestSimpleBean {
        private String astring;
        private int anint;
        private Integer aninteger;
        private Long along;
        private Date adate;
        private GregorianCalendar acalendar;
        private boolean aboolean;

        public String getAstring() {
            return astring;
        }

        public void setAstring(final String astring) {
            this.astring = astring;
        }

        public int getAnint() {
            return anint;
        }

        public void setAnint(final int anint) {
            this.anint = anint;
        }

        public Integer getAninteger() {
            return aninteger;
        }

        public void setAninteger(final Integer aninteger) {
            this.aninteger = aninteger;
        }

        public Long getAlong() {
            return along;
        }

        public void setAlong(final Long along) {
            this.along = along;
        }

        public Date getAdate() {
            return adate;
        }

        public void setAdate(final Date adate) {
            this.adate = adate;
        }

        public GregorianCalendar getAcalendar() {
            return acalendar;
        }

        public void setAcalendar(final GregorianCalendar acalendar) {
            this.acalendar = acalendar;
        }

        public boolean isAboolean() {
            return aboolean;
        }

        public void setAboolean(final boolean aboolean) {
            this.aboolean = aboolean;
        }

        @Override
        public String toString() {
            return "TestSimpleBean [" + (astring != null ? "astring=" + astring + ", " : "") + "anint=" + anint + ", "
                    + (aninteger != null ? "aninteger=" + aninteger + ", " : "") + (along != null ? "along=" + along + ", " : "")
                    + (adate != null ? "adate=" + adate + ", " : "") + (acalendar != null ? "acalendar=" + acalendar.getTimeInMillis() + ", " : "")
                    + "aboolean=" + aboolean + "]";
        }

    }

    public static class TestSimpleBean2 {
        private List<String> list;
        private Map<Integer, String> map;
        private Set<Character> set;

        public void setList(final List<String> list) {
            this.list = list;
        }

        public List<String> getList() {
            return list;
        }

        public void setMap(final Map<Integer, String> map) {
            this.map = map;
        }

        public Map<Integer, String> getMap() {
            return map;
        }

        public void setSet(final Set<Character> set) {
            this.set = set;
        }

        public Set<Character> getSet() {
            return set;
        }
    }

}
