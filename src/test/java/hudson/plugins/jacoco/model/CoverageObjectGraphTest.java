package hudson.plugins.jacoco.model;

import static org.junit.Assert.assertArrayEquals;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.GregorianCalendar;
import java.util.Locale;

import org.apache.commons.io.FileUtils;
import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import hudson.plugins.jacoco.AbstractJacocoTestBase;
import hudson.plugins.jacoco.model.CoverageGraphLayout.CoverageType;
import hudson.plugins.jacoco.model.CoverageGraphLayout.CoverageValue;

/**
 * @author Martin Heinzerling
 */
public class CoverageObjectGraphTest extends AbstractJacocoTestBase
{
	public static final int WIDTH = 500;
	public static final int HEIGHT = 200;
	private static Font font;
	private IMocksControl ctl;
	private Locale localeBackup;

	@BeforeClass
	public static void loadFont() throws IOException, FontFormatException
	{
		// just a free font nobody has on their system, but different enough to default sans-serif,
		// that you will see missing system font replacement in the outbut. See #replaceFonts()
		InputStream is = new FileInputStream("resources/test/belligerent.ttf");
		font=Font.createFont(Font.TRUETYPE_FONT, is);
	}

	@Before
	public void setUp()
	{
		ctl = EasyMock.createControl();
		TestCoverageObject.setEasyMock(ctl);
		localeBackup=Locale.getDefault();
		Locale.setDefault(Locale.ENGLISH);
	}

	@After
	public void tearDown()
	{
		ctl.verify();
		TestCoverageObject.setEasyMock(null);
		Locale.setDefault(localeBackup);
	}

	@Test
	public void simpleLineCoverage() throws IOException
	{
		CoverageGraphLayout layout = new CoverageGraphLayout()
				/*.baseStroke(4f)*/
				.plot().type(CoverageType.LINE).value(CoverageValue.MISSED).color(Color.RED)
				.plot().type(CoverageType.LINE).value(CoverageValue.COVERED).color(Color.GREEN);

		JFreeChart chart = createTestCoverage().createGraph(new GregorianCalendar(), WIDTH, HEIGHT, layout).getGraph();
		assertGraph(chart, "simple.png");
	}

	@Test
	public void baseStroke() throws IOException
	{
		CoverageGraphLayout layout = new CoverageGraphLayout().
				baseStroke(2.0f)
				.plot().type(CoverageType.LINE).value(CoverageValue.MISSED).color(Color.RED)
				.plot().type(CoverageType.LINE).value(CoverageValue.COVERED).color(Color.GREEN);

		JFreeChart chart = createTestCoverage().createGraph(new GregorianCalendar(), WIDTH, HEIGHT, layout).getGraph();
		assertGraph(chart, "baseStroke.png");
	}

	@Test
	public void multipleAccessAndDifferentCoverageType() throws IOException
	{
		CoverageGraphLayout layout = new CoverageGraphLayout()
				.baseStroke(2f)
				.axis().label("M")
				.plot().type(CoverageType.LINE).value(CoverageValue.MISSED).color(Color.RED)
				.axis().label("C")
				.plot().type(CoverageType.LINE).value(CoverageValue.COVERED).color(Color.GREEN)
				.axis().label("%")
				.plot().type(CoverageType.BRANCH).value(CoverageValue.PERCENTAGE).color(Color.BLUE)
				.plot().type(CoverageType.LINE).value(CoverageValue.PERCENTAGE).color(Color.YELLOW);

		JFreeChart chart = createTestCoverage().createGraph(new GregorianCalendar(), WIDTH, HEIGHT, layout).getGraph();
		assertGraph(chart, "multiple.png");
	}

	@Test
	public void crop5() throws IOException
	{
		CoverageGraphLayout layout = new CoverageGraphLayout()
				.baseStroke(2f)
				.axis().crop(5).skipZero()
				.plot().type(CoverageType.BRANCH).value(CoverageValue.PERCENTAGE).color(Color.RED);

		JFreeChart chart = createTestCoverage().createGraph(new GregorianCalendar(), WIDTH, HEIGHT, layout).getGraph();
		assertGraph(chart, "crop5.png");
	}

	@Test
	public void crop100() throws IOException
	{
		CoverageGraphLayout layout = new CoverageGraphLayout()
				.baseStroke(2f)
				.axis().crop(100).skipZero()
				.plot().type(CoverageType.BRANCH).value(CoverageValue.PERCENTAGE).color(Color.RED);

		JFreeChart chart = createTestCoverage().createGraph(new GregorianCalendar(), WIDTH, HEIGHT, layout).getGraph();
		assertGraph(chart, "crop100.png");
	}

	@Test
	public void skipZero() throws IOException
	{
		CoverageGraphLayout layout = new CoverageGraphLayout()
				.skipZero()
				.plot().type(CoverageType.BRANCH).value(CoverageValue.PERCENTAGE).color(Color.RED);

		JFreeChart chart = createTestCoverage().createGraph(new GregorianCalendar(), WIDTH, HEIGHT, layout).getGraph();
		assertGraph(chart, "skipzero.png");
	}

	private TestCoverageObject createTestCoverage()
	{
		TestCoverageObject t5 = new TestCoverageObject().branch(6, 30).line(5000, 19000);
		TestCoverageObject t4 = new TestCoverageObject().branch(6, 0).line(5000, 19000).previous(t5);
		TestCoverageObject t3 = new TestCoverageObject().branch(6, 35).line(5000, 19000).previous(t4);
		TestCoverageObject t2 = new TestCoverageObject().branch(15, 23).line(10000, 15000).previous(t3);
		TestCoverageObject t1 = new TestCoverageObject().branch(27, 13).line(12000, 18000).previous(t2);
		TestCoverageObject t0 = new TestCoverageObject().previous(t1);
		ctl.replay();
		return t0;
	}

	private void assertGraph(JFreeChart chart, String file, boolean writeFile) throws IOException
	{
		replaceFonts(chart);
		if (writeFile)
		{
			File f = new File(file);
			ChartUtilities.saveChartAsPNG(f, chart, WIDTH, HEIGHT);
			System.out.println("Stored graph file to " + f.getAbsolutePath());
		}
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

	private void assertGraph(JFreeChart chart, String file) throws IOException
	{
		assertGraph(chart, file, !new File("resources/test/" + file).exists());
	}

	private void replaceFonts(JFreeChart chart)
	{
		int i=0;
		while (chart.getLegend(i)!=null)
		{
			chart.getLegend(i).setItemFont(font.deriveFont(chart.getLegend(i).getItemFont().getStyle(), chart.getLegend(i).getItemFont().getSize()));
			i++;
		}
		i=0;
		while (chart.getCategoryPlot().getDomainAxis(i)!=null)
		{
			chart.getCategoryPlot().getDomainAxis(i).setTickLabelFont(font.deriveFont(chart.getCategoryPlot().getDomainAxis(i).getTickLabelFont().getStyle(), chart.getCategoryPlot().getDomainAxis(i).getTickLabelFont().getSize()));
			i++;
		}
		i=0;
		while (chart.getCategoryPlot().getRangeAxis(i)!=null)
		{
			chart.getCategoryPlot().getRangeAxis(i).setTickLabelFont(font.deriveFont(chart.getCategoryPlot().getRangeAxis(i).getTickLabelFont().getStyle(), chart.getCategoryPlot().getRangeAxis(i).getTickLabelFont().getSize()));
			chart.getCategoryPlot().getRangeAxis(i).setLabelFont(font.deriveFont(chart.getCategoryPlot().getRangeAxis(i).getLabelFont().getStyle(), chart.getCategoryPlot().getRangeAxis(i).getLabelFont().getSize()));
			i++;
		}
	}

}
