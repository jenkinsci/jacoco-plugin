package hudson.plugins.emma;

/**
 * @author Manuel Carrasco
 */
public class CoverageObjectTest extends AbstractEmmaTestBase {
	
    public void testPrintColumnt() throws Exception {
    	
    	Ratio r = null;
    	StringBuilder b = new StringBuilder();
    	CoverageObject.printColumn(true, null, b);
    	assertEquals("", b.toString());
    	
    	r = new Ratio(0,100);
    	b = new StringBuilder();
    	CoverageObject.printColumn(true, r, b);
    	assertEquals("<td bgcolor=red data='000.00' style='white-space: nowrap;'>&nbsp;&nbsp;0% (0/100)</td>", b.toString());
    	
    	r = new Ratio(0,100);
    	b = new StringBuilder();
    	CoverageObject.printColumn(false, r, b);
    	assertEquals("<td data='000.00' style='white-space: nowrap;'>&nbsp;&nbsp;0% (0/100)</td>", b.toString());
    	
    	r = new Ratio(51,200);
    	b = new StringBuilder();
    	CoverageObject.printColumn(false, r, b);
    	assertEquals("<td data='025.50' style='white-space: nowrap;'>&nbsp;26% (51/200)</td>", b.toString());
    	
    	r = new Ratio(100,100);
    	b = new StringBuilder();
    	CoverageObject.printColumn(false, r, b);
    	assertEquals("<td data='100.00' style='white-space: nowrap;'>100% (100/100)</td>", b.toString());
    }
 
}
