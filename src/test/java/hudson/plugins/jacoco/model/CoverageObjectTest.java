package hudson.plugins.jacoco.model;

import hudson.plugins.jacoco.AbstractJacocoTestBase;
import org.junit.Test;

/**
 * @author Manuel Carrasco
 */
public class CoverageObjectTest extends AbstractJacocoTestBase {
	@Test
    public void testConstruct() {
	    // no test-content for now...
    }

	/*@Test
    public void testPrintRatioTable() throws Exception {

    	Coverage r = null;
    	StringBuilder b = new StringBuilder();

    	r = new Coverage(0,100);
    	b = new StringBuilder();
    	CoverageObject.printRatioTable(r, b);
    	
    	assertEquals("<table class='percentgraph' cellpadding='0px' cellspacing='0px'><tr class='percentgraph'><td width='64px' class='data'>100.0%</td><td class='percentgraph'><div class='percentgraph'><div class='greenbar' style='width: 55px;'><span class='text'>M:0 C: 100</span></div></div></td></tr></table>", b.toString());
    }
    
	@Test
    public void testPrintRatioTable2() throws Exception {
    	Coverage r = null;
    	StringBuilder b = new StringBuilder();
    	r = new Coverage(51,200);
    	b = new StringBuilder();
    	
    	CoverageObject.printRatioTable(r, b);
    	assertEquals("<table class='percentgraph' cellpadding='0px' cellspacing='0px'><tr class='percentgraph'><td width='64px' class='data'>79.7%</td><td class='percentgraph'><div class='percentgraph'><div class='greenbar' style='width: 55px;'><span class='text'>M:51 C: 200</span></div></div></td></tr></table>", b.toString());
    }


	@Test
    public void testPrintColumnt() throws Exception {

    	Coverage r = null;
    	StringBuilder b = new StringBuilder();
    	CoverageObject.printRatioCell(true, null, b);
    	assertEquals("", b.toString());

    	r = new Coverage(0,100);
    	b = new StringBuilder();
    	CoverageObject.printRatioCell(true, r, b);
    	assertTrue(b.toString().contains("'nowrap red'"));

    	r = new Coverage(0,100);
    	b = new StringBuilder();
    	CoverageObject.printRatioCell(false, r, b);
    	assertTrue(b.toString().contains("'nowrap'"));

    	r = new Coverage(51,200);
    	b = new StringBuilder();
    	CoverageObject.printRatioCell(false, r, b);
    	System.out.println(b.toString());
    	
    	assertEquals("<td class='nowrap' data='079.68'>\n" +
    			"<table class='percentgraph' cellpadding='0px' cellspacing='0px'><tr class='percentgraph'><td width='64px' class='data'>79.7%</td><td class='percentgraph'><div class='percentgraph'><div class='greenbar' style='width: 55px;'><span class='text'>M:51 C: 200</span></div></div></td></tr></table></td>\n", b.toString());

    }*/
 
}
