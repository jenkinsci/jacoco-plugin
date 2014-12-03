package hudson.plugins.jacoco.model;


import static org.junit.Assert.assertArrayEquals;

import hudson.plugins.jacoco.AbstractJacocoTestBase;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.GregorianCalendar;
import org.apache.commons.io.FileUtils;
import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Martin Heinzerling
 */
public class CoverageObjectGraphTest extends AbstractJacocoTestBase
{
	public static final int WIDTH = 500;
	public static final int HEIGHT = 200;
	private IMocksControl ctl;

	@Before
	public void prepareMock()
	{
		ctl = EasyMock.createControl();
		TestCoverageObject.setEasyMock(ctl);
	}

	@After
	public void verifyMock()
	{
		ctl.verify();
		TestCoverageObject.setEasyMock(null);
	}

	@Test
	public void simpleLineCoverage() throws IOException
	{
		TestCoverageObject t5 = new TestCoverageObject().line(5000, 19000);
		TestCoverageObject t4 = new TestCoverageObject().line(5000, 19000).previous(t5);
		TestCoverageObject t3 = new TestCoverageObject().line(5000, 19000).previous(t4);
		TestCoverageObject t2 = new TestCoverageObject().line(10000, 15000).previous(t3);
		TestCoverageObject t1 = new TestCoverageObject().line(12000, 18000).previous(t2);
		TestCoverageObject t0 = new TestCoverageObject().previous(t1);
		ctl.replay();

		JFreeChart chart = t0.createGraph(new GregorianCalendar(), WIDTH, HEIGHT).getGraph();
		assertGraph(chart, "simple.png");

	}

	private void assertGraph(JFreeChart chart, String file) throws IOException
	{
		byte[] expected = FileUtils.readFileToByteArray(new File("resources/test/" + file));
		byte[] actual;

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try
		{
			ChartUtilities.writeChartAsPNG(out, chart, WIDTH, HEIGHT, null);
			actual = out.toByteArray();
		}
		finally
		{
			out.close();
		}
		try
		{
			assertArrayEquals(expected, actual);
		}
		catch (AssertionError e)
		{
			File f = new File(file);
			ChartUtilities.saveChartAsPNG(f, chart, WIDTH, HEIGHT);
			System.err.println("Stored wrong graph file to " + f.getAbsolutePath());
			throw e;
		}

	}
}
