package hudson.plugins.jacocofile;

import hudson.FilePath;
import hudson.model.AbstractBuild;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates the directory structure in $JENKINS_HOME where we store jacoco related files.
 *
 * @author Kohsuke Kawaguchi
 */
public class JacocoReportFilesDir {
    private final File root;

    public JacocoReportFilesDir(AbstractBuild<?,?> build) {
        root = new File(build.getRootDir(), "jacoco");
    }

    /**
     * Where we store *.class files, honoring package names as directories.
     */
    public File getReportDir() {
        return new File(root,"report");
    }

    public void saveReportFrom(FilePath dir) throws IOException, InterruptedException {
        FilePath d = new FilePath(getReportDir());
        d.mkdirs();
        dir.copyRecursiveTo(d);
    }

    /**
     * Root directory that stores jacoco.exec files.
     * Each exec file is stored in its own directory.
     *
     * @see #getExecFiles()
     */
    public File getExecFilesDir() {
        return new File(root,"execFiles");
    }

    /**
     * Lists up existing jacoco.exec files.
     */
    public List<File> getExecFiles() {
        List<File> r = new ArrayList<File>();
        int i = 0;
        File root = getExecFilesDir();
        File checkPath;
        while ((checkPath = new File(root, "exec" + i)).exists()) {
            r.add(new File(checkPath,"jacoco.exec"));
            i++;
        }

        return r;
    }

    public void addExecFiles(Iterable<FilePath> execFiles) throws IOException, InterruptedException {
        FilePath root = new FilePath(getExecFilesDir());
        int i=0;
        for (FilePath file : execFiles) {
            FilePath separateExecDir;
            do {
                separateExecDir = new FilePath(root, "exec"+(i++));
            } while (separateExecDir.exists());

        	FilePath fullExecName = separateExecDir.child("jacoco.exec");
        	file.copyTo(fullExecName);
        }
    }

    @Override
    public String toString() {
        return root.toString();
    }
}
