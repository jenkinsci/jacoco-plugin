package hudson.plugins.jacoco;

import hudson.EnvVars;
import hudson.model.Action;
import hudson.model.Result;
import hudson.model.Run;
import hudson.model.TaskListener;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

import org.easymock.IAnswer;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.getCurrentArguments;
import static org.easymock.EasyMock.mock;
import static org.easymock.EasyMock.niceMock;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertTrue;

public class RunBuilder {
    private TaskListener taskListener;

    Run build() throws IOException, InterruptedException {
        final TaskListener usedTaskListener = taskListener == null ? niceMock(TaskListener.class) : taskListener;

        final Run run = mock(Run.class);
        expect(run.getEnvironment(usedTaskListener)).andReturn(new EnvVars()).anyTimes();
        expect(run.getResult()).andReturn(Result.SUCCESS).anyTimes();
        expect(run.getParent()).andReturn(null).anyTimes();

        // create a test build directory
        File rootDir = File.createTempFile("BuildTest", ".tst");
        assertTrue(rootDir.delete());
        assertTrue(rootDir.mkdirs());

        expect(run.getRootDir()).andReturn(rootDir).anyTimes();

        Action action = anyObject();
        run.addAction(action);
        final AtomicReference<JacocoBuildAction> buildAction = new AtomicReference<>();
        expectLastCall().andAnswer((IAnswer<Void>) () -> {
            buildAction.set((JacocoBuildAction) getCurrentArguments()[0]);
            buildAction.get().onAttached(run);
            return null;
        });

        replay(usedTaskListener, run);
        Logger.getLogger(RunBuilder.class.getName()).info("Created build dir: " + rootDir.getAbsolutePath());
        return run;

    }

    public RunBuilder taskListener(TaskListener taskListener) {
        this.taskListener = taskListener;
        return this;
    }
}
