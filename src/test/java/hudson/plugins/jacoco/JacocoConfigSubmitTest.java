package hudson.plugins.jacoco;

import org.junit.Test;

/**
 * Test for Project configuration.
 * @author Seiji Sogabe
 */
public class JacocoConfigSubmitTest /*extends HudsonTestCase*/ {

	@Test
    public void testIncludeIsEmpty() throws Exception {
       /*WebClient client = new WebClient();
        client.setThrowExceptionOnFailingStatusCode(false);

        FreeStyleProject fp = createFreeStyleProject();
        HtmlPage p = client.goTo(fp.getUrl() + "/configure");
        HtmlForm f = p.getFormByName("config");

        f.getInputByName("hudson-plugins-jacoco-JacocoPublisher").setChecked(true);
        // includes is empty
        submit(f);

        JacocoPublisher publisher = (JacocoPublisher) fp.getPublisher(JacocoPublisher.DESCRIPTOR);

        assertEquals("", publisher.includes);*/		
    }
    
	@Test
    public void testIncludeIsSet() throws Exception {
        /*WebClient client = new WebClient();
        client.setThrowExceptionOnFailingStatusCode(false);

        FreeStyleProject fp = createFreeStyleProject();
        HtmlPage p = client.goTo(fp.getUrl() + "/configure");
        HtmlForm f = p.getFormByName("config");

        f.getInputByName("hudson-plugins-jacoco-JacocoPublisher").setChecked(true);
        f.getInputByName("jacoco.includes").setValue("***");
        submit(f);

        JacocoPublisher publisher = (JacocoPublisher) fp.getPublisher(JacocoPublisher.DESCRIPTOR);

        assertEquals("***", publisher.includes);*/
    }

	@Test
    public void testHealthReportDefaultMaxValue() throws Exception {
        /*WebClient client = new WebClient();
        client.setThrowExceptionOnFailingStatusCode(false);

        FreeStyleProject fp = createFreeStyleProject();
        HtmlPage p = client.goTo(fp.getUrl() + "/configure");
        HtmlForm f = p.getFormByName("config");

        f.getInputByName("hudson-plugins-jacoco-JacocoPublisher").setChecked(true);
        f.getInputByName("jacocoHealthReports.maxClass").setValue("");
        f.getInputByName("jacocoHealthReports.maxMethod").setValue("");
        f.getInputByName("jacocoHealthReports.maxLine").setValue("");
        f.getInputByName("jacocoHealthReports.maxBranch").setValue("");
        f.getInputByName("jacocoHealthReports.maxInstruction").setValue("");
        f.getInputByName("jacocoHealthReports.maxComplexity").setValue("");
        submit(f);

        JacocoPublisher publisher = (JacocoPublisher) fp.getPublisher(JacocoPublisher.DESCRIPTOR);
        JacocoHealthReportThresholds thresholds = publisher.healthReports;
        
        assertEquals(100, thresholds.getMaxClass());
        assertEquals(70, thresholds.getMaxMethod());
        assertEquals(70, thresholds.getMaxLine());
        assertEquals(70, thresholds.getMaxBranch());
        assertEquals(70, thresholds.getMaxInstruction());
        assertEquals(70, thresholds.getMaxComplexity());*/
    }    

	@Test
    public void testHealthReportMaxValue() throws Exception {
        /*WebClient client = new WebClient();
        client.setThrowExceptionOnFailingStatusCode(false);

        FreeStyleProject fp = createFreeStyleProject();
        HtmlPage p = client.goTo(fp.getUrl() + "/configure");
        HtmlForm f = p.getFormByName("config");

        f.getInputByName("hudson-plugins-jacoco-JacocoPublisher").setChecked(true);
        f.getInputByName("jacocoHealthReports.maxClass").setValue("8");
        f.getInputByName("jacocoHealthReports.maxMethod").setValue("9");
        f.getInputByName("jacocoHealthReports.maxLine").setValue("10");
        f.getInputByName("jacocoHealthReports.maxBranch").setValue("11");
        f.getInputByName("jacocoHealthReports.maxInstruction").setValue("12");
        f.getInputByName("jacocoHealthReports.maxComplexity").setValue("13");
        submit(f);

        JacocoPublisher publisher = (JacocoPublisher) fp.getPublisher(JacocoPublisher.DESCRIPTOR);
        JacocoHealthReportThresholds thresholds = publisher.healthReports;
        
        assertEquals(8, thresholds.getMaxClass());
        assertEquals(9, thresholds.getMaxMethod());
        assertEquals(10, thresholds.getMaxLine());
        assertEquals(11, thresholds.getMaxBranch());
        assertEquals(12, thresholds.getMaxInstruction());
        assertEquals(13, thresholds.getMaxComplexity());*/
    }    

	@Test
    public void testHealthReportMinValue() throws Exception {
        /*WebClient client = new WebClient();
        client.setThrowExceptionOnFailingStatusCode(false);

        FreeStyleProject fp = createFreeStyleProject();
        HtmlPage p = client.goTo(fp.getUrl() + "/configure");
        HtmlForm f = p.getFormByName("config");

        f.getInputByName("hudson-plugins-jacoco-JacocoPublisher").setChecked(true);
        f.getInputByName("jacocoHealthReports.minClass").setValue("1");
        f.getInputByName("jacocoHealthReports.minMethod").setValue("2");
        f.getInputByName("jacocoHealthReports.minLine").setValue("3");
        f.getInputByName("jacocoHealthReports.minBranch").setValue("11");
        f.getInputByName("jacocoHealthReports.minInstruction").setValue("12");
        f.getInputByName("jacocoHealthReports.minComplexity").setValue("13");
        submit(f);

        JacocoPublisher publisher = (JacocoPublisher) fp.getPublisher(JacocoPublisher.DESCRIPTOR);
        JacocoHealthReportThresholds thresholds = publisher.healthReports;
        
        assertEquals(1, thresholds.getMinClass());
        assertEquals(2, thresholds.getMinMethod());
        assertEquals(3, thresholds.getMinLine());
        assertEquals(11, thresholds.getMinBranch());
        assertEquals(12, thresholds.getMinInstruction());
        assertEquals(13, thresholds.getMinComplexity());*/
    }    
    
}
