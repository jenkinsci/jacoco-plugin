package hudson.plugins.jacoco.report;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class PackageReportTest {

    @Test
    public void testName() {
        PackageReport report = new PackageReport();

        report.setName("");
        assertEquals("(default)", report.getName());

        report.setName("testname");
        assertEquals("testname", report.getName());

        report.setName("test/name/1");
        assertEquals("test.name.1", report.getName());

        report.setName("myname/&:<>2%;");
        assertEquals("myname.____2__", report.getName());
        assertEquals("myname.____2__", report.getDisplayName());
    }

    @Test
    public void testChildren() {
        PackageReport report = new PackageReport();
        report.setName("pkg");

        assertEquals(0, report.getChildren().size());
        ClassReport child = new ClassReport();
        child.setName("testname");
        report.add(child);
        assertEquals("testname", child.getName());
        assertEquals(1, report.getChildren().size());
        assertEquals("testname", report.getChildren().values().iterator().next().getName());
    }

    @Test
    public void testChildrenRemovePkgName() {
        PackageReport report = new PackageReport();
        report.setName("pkg");

        assertEquals(0, report.getChildren().size());
        ClassReport child = new ClassReport();
        child.setName("pkg.testname");
        report.add(child);
        assertEquals("testname", child.getName());
        assertEquals(1, report.getChildren().size());
        assertEquals("testname", report.getChildren().values().iterator().next().getName());
    }
}
