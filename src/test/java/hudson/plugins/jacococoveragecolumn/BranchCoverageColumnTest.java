package hudson.plugins.jacococoveragecolumn;

import hudson.model.Descriptor.FormException;
import hudson.model.Job;
import hudson.model.Run;
import hudson.plugins.jacoco.JacocoBuildAction;
import hudson.plugins.jacoco.model.Coverage;
import hudson.plugins.jacoco.model.CoverageElement;
import hudson.plugins.jacoco.model.CoverageElement.Type;
import hudson.search.QuickSilver;
import hudson.util.StreamTaskListener;
import net.sf.json.JSONObject;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.kohsuke.stapler.export.Exported;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class BranchCoverageColumnTest {

	private BranchCoverageColumn sut;

    @Before
	public void setUp() {
		sut = new BranchCoverageColumn();
	}

	@Test
	public void testGetPercentWithBuildAndAction() {
		ServletContext context = EasyMock.createNiceMock(ServletContext.class);
		
		EasyMock.replay(context);

		final Job<?, ?> mockJob = new MyJob("externaljob") {
			@Override
			@Exported
			@QuickSilver
			public MyRun getLastSuccessfulBuild() {
				try {
				    MyRun newBuild = newBuild();
					Map<CoverageElement.Type, Coverage> ratios = new HashMap<>();
					ratios.put(Type.BRANCH, new Coverage(100, 200));
					newBuild.addAction(new JacocoBuildAction(ratios, null, StreamTaskListener.fromStdout(), null, null));
					assertEquals(1, newBuild.getAllActions().size());
					return newBuild;
				} catch (IOException e) {
					throw new IllegalStateException(e);
				}
			}

			@Override
			protected synchronized void saveNextBuildNumber() {
			}
		};
		assertEquals("66.67", sut.getPercent(mockJob));

		EasyMock.verify(context);
	}

	@Test
	public void testDescriptor() throws FormException {
		assertNotNull(sut.getDescriptor());
		assertNotNull(sut.getDescriptor().newInstance(null, JSONObject.fromObject("{\"key\":\"value\"}")));
		assertNotNull(sut.getDescriptor().getDisplayName());
	}

	private class MyJob extends Job<MyJob,MyRun> {

        public MyJob(String name) {
            super(null, name);
        }

        @Override
        public boolean isBuildable() {
            return false;
        }

        @Override
        protected SortedMap<Integer, MyRun> _getRuns() {
            return null;
        }

        @Override
        protected void removeRun(MyRun run) {
        }

        protected synchronized MyRun newBuild() throws IOException {
            return new MyRun(this);
        }
	}
	
	private class MyRun extends Run<MyJob,MyRun> {

        public MyRun(MyJob job) throws IOException {
            super(job);
        }
	}
}
