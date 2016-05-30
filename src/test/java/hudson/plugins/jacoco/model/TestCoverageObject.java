package hudson.plugins.jacoco.model;

import hudson.model.AbstractBuild;
import hudson.model.FreeStyleBuild;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.easymock.IMocksControl;

/**
 * @author Martin Heinzerling
 */
public class TestCoverageObject extends CoverageObject<TestCoverageObject>
{
	private TestCoverageObject previous = null;
	private AbstractBuild<?, ?> build = null;
	private int buildNumber = 1;
	private static IMocksControl mocksControl;

	public static void setEasyMock(IMocksControl mocksControl)
	{
		TestCoverageObject.mocksControl = mocksControl;
	}

	public TestCoverageObject()
	{
		super();
		build = mocksControl.createMock("build", FreeStyleBuild.class);
		EasyMock.expect(build.getDisplayName()).andAnswer(new IAnswer<String>()
		{
			public String answer() throws Throwable
			{
				return "#" + buildNumber;
			}
		}).anyTimes();
	}

	@Override
	public AbstractBuild<?, ?> getBuild()
	{
		return build;
	}

	@Override
	public TestCoverageObject getPreviousResult()
	{
		return previous;
	}

	public TestCoverageObject clazz(int missed, int covered)
	{
		clazz = new Coverage(missed, covered);
		return this;
	}

	public TestCoverageObject method(int missed, int covered)
	{
		method = new Coverage(missed, covered);
		return this;
	}

	public TestCoverageObject line(int missed, int covered)
	{
		line = new Coverage(missed, covered);
		return this;
	}

	public TestCoverageObject complexity(int missed, int covered)
	{
		complexity = new Coverage(missed, covered);
		return this;
	}

	public TestCoverageObject instruction(int missed, int covered)
	{
		instruction = new Coverage(missed, covered);
		return this;
	}

	public TestCoverageObject branch(int missed, int covered)
	{
		branch = new Coverage(missed, covered);
		return this;
	}

	public TestCoverageObject previous(TestCoverageObject previous)
	{
		this.previous = previous;
		int i = 1;
		TestCoverageObject t = this;
		while (t != null)
		{
			t.buildNumber = i;
			i++;
			t = t.previous;
		}
		return this;
	}
}
