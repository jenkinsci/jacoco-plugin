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
        f.getInputByName("emmaHealthReports.maxBlock").setValueAttribute("");
        f.getInputByName("emmaHealthReports.maxLine").setValueAttribute("");
        submit(f);

        EmmaPublisher publisher = (EmmaPublisher) fp.getPublisher(EmmaPublisher.DESCRIPTOR);
        EmmaHealthReportThresholds thresholds = publisher.healthReports;
        
        assertEquals(100, thresholds.getMaxClass());
        assertEquals(70, thresholds.getMaxMethod());
        assertEquals(80, thresholds.getMaxBlock());
        assertEquals(80, thresholds.getMaxLine());
    }    

    public void testHealthReportMaxValue() throws Exception {
        WebClient client = new WebClient();
        client.setThrowExceptionOnFailingStatusCode(false);

        FreeStyleProject fp = createFreeStyleProject();
        HtmlPage p = client.goTo(fp.getUrl() + "/configure");
        HtmlForm f = p.getFormByName("config");

        f.getInputByName("hudson-plugins-emma-EmmaPublisher").setChecked(true);
        f.getInputByName("emmaHealthReports.maxClass").setValueAttribute("10");
        f.getInputByName("emmaHealthReports.maxMethod").setValueAttribute("10");
        f.getInputByName("emmaHealthReports.maxBlock").setValueAttribute("10");
        f.getInputByName("emmaHealthReports.maxLine").setValueAttribute("10");
        submit(f);

        EmmaPublisher publisher = (EmmaPublisher) fp.getPublisher(EmmaPublisher.DESCRIPTOR);
        EmmaHealthReportThresholds thresholds = publisher.healthReports;
        
        assertEquals(10, thresholds.getMaxClass());
        assertEquals(10, thresholds.getMaxMethod());
        assertEquals(10, thresholds.getMaxBlock());
        assertEquals(10, thresholds.getMaxLine());
    }    

    public void testHealthReportMinValue() throws Exception {
        WebClient client = new WebClient();
        client.setThrowExceptionOnFailingStatusCode(false);

        FreeStyleProject fp = createFreeStyleProject();
        HtmlPage p = client.goTo(fp.getUrl() + "/configure");
        HtmlForm f = p.getFormByName("config");

        f.getInputByName("hudson-plugins-emma-EmmaPublisher").setChecked(true);
        f.getInputByName("emmaHealthReports.minClass").setValueAttribute("10");
        f.getInputByName("emmaHealthReports.minMethod").setValueAttribute("10");
        f.getInputByName("emmaHealthReports.minBlock").setValueAttribute("10");
        f.getInputByName("emmaHealthReports.minLine").setValueAttribute("10");
        submit(f);

        EmmaPublisher publisher = (EmmaPublisher) fp.getPublisher(EmmaPublisher.DESCRIPTOR);
        EmmaHealthReportThresholds thresholds = publisher.healthReports;
        
        assertEquals(10, thresholds.getMinClass());
        assertEquals(10, thresholds.getMinMethod());
        assertEquals(10, thresholds.getMinBlock());
        assertEquals(10, thresholds.getMinLine());
    }    
    
}
