package hudson.plugins.jacococoveragecolumn;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import hudson.model.Job;

import java.math.BigDecimal;

import org.easymock.classextension.EasyMock;
import org.junit.Before;
import org.junit.Test;

public class JaCoCoColumnTest {

	protected Float percentFloat;
	private JaCoCoColumn jacocoColumn;

	@Before
	public void setUp() {
		jacocoColumn = new JaCoCoColumn();
	}

	@Test
	public void testGetPercentWithNoLastSuccessfulBuild() {
		final Job<?, ?> mockJob = EasyMock.createMock(Job.class);
		EasyMock.expect(mockJob.getLastSuccessfulBuild()).andReturn(null);
		EasyMock.replay(mockJob);
		assertEquals("N/A", jacocoColumn.getPercent(mockJob));
	}

	@Test
	public void testGetLineColorWithNull() throws Exception {
		assertNull(jacocoColumn.getLineColor(null));
	}

	@Test
	public void testGetLineColor() throws Exception {
		assertEquals("EEEEEE", jacocoColumn.getLineColor(BigDecimal.valueOf(100)));

	}

	@Test
	public void testGetFillColorWithNull() throws Exception {
		assertNull(jacocoColumn.getFillColor(null));
	}

	@Test
	public void testGetFillColor100() throws Exception {
		assertEquals("008B00", jacocoColumn.getFillColor(BigDecimal.valueOf(100)));

	}

}
