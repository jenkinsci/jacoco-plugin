package hudson.plugins.jacoco;

import hudson.FilePath;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.Assert.assertTrue;

public class WorkspaceBuilder {

    private String workspacePrefix = "workspace";
    private String workspaceSuffix = ".tst";
    private List<String> files = new ArrayList<>();

    FilePath build() throws IOException {
        // create a test workspace of Jenkins job
        File wksp = File.createTempFile("workspace", ".tst");
        assertTrue(wksp.delete());
        assertTrue(wksp.mkdir());
        wksp.deleteOnExit();

        final FilePath workspace = new FilePath(wksp);
        for (String file : files) {
            File f = new File(workspace.child(file).getRemote());
            if (!f.getParentFile().exists()) {
                assertTrue("Failed creating: " + f.getParentFile(), f.getParentFile().mkdirs());
            }
            assertTrue(f.createNewFile());
            f.deleteOnExit();
        }
        Logger.getLogger(WorkspaceBuilder.class.getName()).info("Created workspace: " + wksp.getAbsolutePath());
        return workspace;
    }

    public WorkspaceBuilder name(String workspacePrefix, String workspaceSuffix) {
        this.workspacePrefix = workspacePrefix;
        this.workspaceSuffix = workspaceSuffix;
        return this;
    }

    public WorkspaceBuilder file(String relativeFile) {
        this.files.add(relativeFile);
        return this;
    }
}
