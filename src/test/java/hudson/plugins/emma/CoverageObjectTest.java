package hudson.plugins.emma;

/**
 * @author Manuel Carrasco
 */
public class CoverageObjectTest extends AbstractEmmaTestBase {
	
    public void testPrintRatioTable() throws Exception {

    	Ratio r = null;
    	StringBuilder b = new StringBuilder();

    	r = new Ratio(0,100);
    	b = new StringBuilder();
    	CoverageObject.printRatioTable(r, b);
    	assertEquals("<table class='percentgraph' cellpadding='0px' cellspacing='0px'><tr class='percentgraph'><td width='64px' class='data'>0.0%</td><td class='percentgraph'><div class='percentgraph'><div class='greenbar' style='width: 0.0px;'><span class='text'>0/100</span></div></div></td></tr></table>", b.toString());

    	r = new Ratio(51,200);
    	b = new StringBuilder();
    	CoverageObject.printRatioTable(r, b);
    	assertEquals("<table class='percentgraph' cellpadding='0px' cellspacing='0px'><tr class='percentgraph'><td width='64px' class='data'>25.5%</td><td class='percentgraph'><div class='percentgraph'><div class='greenbar' style='width: 25.5px;'><span class='text'>51/200</span></div></div></td></tr></table>", b.toString());

    }


    public void testPrintColumnt() throws Exception {

    	Ratio r = null;
    	StringBuilder b = new StringBuilder();
    	CoverageObject.printRatioCell(true, null, b);
    	assertEquals("", b.toString());

    	r = new Ratio(0,100);
    	b = new StringBuilder();
    	CoverageObject.printRatioCell(true, r, b);
    	assertTrue(b.toString().contains("'nowrap red'"));

    	r = new Ratio(0,100);
    	b = new StringBuilder();
    	CoverageObject.printRatioCell(false, r, b);
    	assertTrue(b.toString().contains("'nowrap'"));

    	r = new Ratio(51,200);
    	b = new StringBuilder();
    	CoverageObject.printRatioCell(false, r, b);
    	assertEquals("<td class='nowrap' data='025.50'>\n" +
    			"<table class='percentgraph' cellpadding='0px' cellspacing='0px'><tr class='percentgraph'><td width='64px' class='data'>25.5%</td><td class='percentgraph'><div class='percentgraph'><div class='greenbar' style='width: 25.5px;'><span class='text'>51/200</span></div></div></td></tr></table></td>\n", b.toString());

    }
 
}
