package hudson.plugins.emma;

import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import hudson.model.FreeStyleProject;
import org.jvnet.hudson.test.HudsonTestCase;

/**
 * Test for Project configuration.
 * @author Seiji Sogabe
 */
public class EmmaConfigSubmitTest extends HudsonTestCase {

    public void testIncludeIsEmpty() throws Exception {
        WebClient client = new WebClient();
        client.setThrowExceptionOnFailingStatusCode(false);

        FreeStyleProject fp = createFreeStyleProject();
        HtmlPage p = client.goTo(fp.getUrl() + "/configure");
        HtmlForm f = p.getFormByName("config");

        f.getInputByName("hudson-plugins-emma-EmmaPublisher").setChecked(true);
        // includes is empty
        submit(f);

        EmmaPublisher publisher = (EmmaPublisher) fp.getPublisher(EmmaPublisher.DESCRIPTOR);

        assertEquals("", publisher.includes);
    }
    
    public void testIncludeIsSet() throws Exception {
        WebClient client = new WebClient();
        client.setThrowExceptionOnFailingStatusCode(false);

        FreeStyleProject fp = createFreeStyleProject();
        HtmlPage p = client.goTo(fp.getUrl() + "/configure");
        HtmlForm f = p.getFormByName("config");

        f.getInputByName("hudson-plugins-emma-EmmaPublisher").setChecked(true);
        f.getInputByName("emma.includes").setValueAttribute("**/*");
        submit(f);

        EmmaPublisher publisher = (EmmaPublisher) fp.getPublisher(EmmaPublisher.DESCRIPTOR);

        assertEquals("**/*", publisher.includes);
    }

    public void testHealthReportDefaultMaxValue() throws Exception {
        WebClient client = new WebClient();
        client.setThrowExceptionOnFailingStatusCode(false);

        FreeStyleProject fp = createFreeStyleProject();
        HtmlPage p = client.goTo(fp.getUrl() + "/configure");
        HtmlForm f = p.getFormByName("config");

        f.getInputByName("hudson-plugins-emma-EmmaPublisher").setChecked(true);
        f.getInputByName("emmaHealthReports.maxClass").setValueAttribute("");
        f.getInputByName("emmaHealthReports.maxMethod").setValueAttribute("");
        f.getInputByName("emmaHealthReports.maxLine").setValueAttribute("");
        f.getInputByName("emmaHealthReports.maxBranch").setValueAttribute("");
        f.getInputByName("emmaHealthReports.maxInstruction").setValueAttribute("");
        f.getInputByName("emmaHealthReports.maxComplexity").setValueAttribute("");
        submit(f);

        EmmaPublisher publisher = (EmmaPublisher) fp.getPublisher(EmmaPublisher.DESCRIPTOR);
        EmmaHealthReportThresholds thresholds = publisher.healthReports;
        
        assertEquals(100, thresholds.getMaxClass());
        assertEquals(70, thresholds.getMaxMethod());
        assertEquals(80, thresholds.getMaxLine());
        assertEquals(70, thresholds.getMaxBranch());
        assertEquals(70, thresholds.getMaxInstruction());
        assertEquals(70, thresholds.getMaxComplexity());
    }    

    public void testHealthReportMaxValue() throws Exception {
        WebClient client = new WebClient();
        client.setThrowExceptionOnFailingStatusCode(false);

        FreeStyleProject fp = createFreeStyleProject();
        HtmlPage p = client.goTo(fp.getUrl() + "/configure");
        HtmlForm f = p.getFormByName("config");

        f.getInputByName("hudson-plugins-emma-EmmaPublisher").setChecked(true);
        f.getInputByName("emmaHealthReports.maxClass").setValueAttribute("8");
        f.getInputByName("emmaHealthReports.maxMethod").setValueAttribute("9");
        f.getInputByName("emmaHealthReports.maxLine").setValueAttribute("10");
        f.getInputByName("emmaHealthReports.maxBranch").setValueAttribute("11");
        f.getInputByName("emmaHealthReports.maxInstruction").setValueAttribute("12");
        f.getInputByName("emmaHealthReports.maxComplexity").setValueAttribute("13");
        submit(f);

        EmmaPublisher publisher = (EmmaPublisher) fp.getPublisher(EmmaPublisher.DESCRIPTOR);
        EmmaHealthReportThresholds thresholds = publisher.healthReports;
        
        assertEquals(8, thresholds.getMaxClass());
        assertEquals(9, thresholds.getMaxMethod());
        assertEquals(10, thresholds.getMaxLine());
        assertEquals(11, thresholds.getMaxBranch());
        assertEquals(12, thresholds.getMaxInstruction());
        assertEquals(13, thresholds.getMaxComplexity());
    }    

    public void testHealthReportMinValue() throws Exception {
        WebClient client = new WebClient();
        client.setThrowExceptionOnFailingStatusCode(false);

        FreeStyleProject fp = createFreeStyleProject();
        HtmlPage p = client.goTo(fp.getUrl() + "/configure");
        HtmlForm f = p.getFormByName("config");

        f.getInputByName("hudson-plugins-emma-EmmaPublisher").setChecked(true);
        f.getInputByName("emmaHealthReports.minClass").setValueAttribute("1");
        f.getInputByName("emmaHealthReports.minMethod").setValueAttribute("2");
        f.getInputByName("emmaHealthReports.minLine").setValueAttribute("3");
        f.getInputByName("emmaHealthReports.minBranch").setValueAttribute("11");
        f.getInputByName("emmaHealthReports.minInstruction").setValueAttribute("12");
        f.getInputByName("emmaHealthReports.minComplexity").setValueAttribute("13");
        submit(f);

        EmmaPublisher publisher = (EmmaPublisher) fp.getPublisher(EmmaPublisher.DESCRIPTOR);
        EmmaHealthReportThresholds thresholds = publisher.healthReports;
        
        assertEquals(1, thresholds.getMinClass());
        assertEquals(2, thresholds.getMinMethod());
        assertEquals(3, thresholds.getMinLine());
        assertEquals(11, thresholds.getMinBranch());
        assertEquals(12, thresholds.getMinInstruction());
        assertEquals(13, thresholds.getMinComplexity());
    }    
    
}
