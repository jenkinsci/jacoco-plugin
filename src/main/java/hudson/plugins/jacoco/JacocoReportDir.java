package hudson.plugins.jacoco;

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
public class JacocoReportDir {
    private final File root;

    /**
     * Where we store *.class files, honoring package names as directories.
     */
    private final FilePath classesDir;

    /**
     * Where we store *.java files, honoring package names as directories.
     */
    private final FilePath sourcesDir;

    /**
     * Root directory that stores jacoco.exec files.
     * Each exec file is stored in its own directory.
     *
     * @see #getExecFiles()
     */
    private final FilePath execFilesDir;

    public JacocoReportDir(AbstractBuild<?,?> build) {
        root = new File(build.getRootDir(), "jacoco");
        classesDir = new FilePath(new File(root,"classes"));
        sourcesDir = new FilePath(new File(root,"sources"));
        execFilesDir = new FilePath(new File(root,"execFiles"));
    }

    public void createDirs() throws IOException, InterruptedException {
        classesDir.mkdirs();
        sourcesDir.mkdirs();
        execFilesDir.mkdirs();
    }

    public void saveClassesFrom(FilePath dir) throws IOException, InterruptedException {
        dir.copyRecursiveTo(classesDir);
    }

    public void saveSourcesFrom(FilePath dir) throws IOException, InterruptedException {
        dir.copyRecursiveTo(sourcesDir);
    }

    /**
     * Lists up existing jacoco.exec files.
     */
    public List<File> getExecFiles() {
        List<File> r = new ArrayList<File>();
        int i = 0;
        File root = new File(execFilesDir.getRemote());
        File checkPath;
        while ((checkPath = new File(root, "exec" + i)).exists()) {
            r.add(new File(checkPath,"jacoco.exec"));
            i++;
        }

        return r;
    }

    public void addExecFiles(Iterable<FilePath> execFiles) throws IOException, InterruptedException {
        int i=0;
        for (FilePath file : execFiles) {
            FilePath separateExecDir;
            do {
                separateExecDir = new FilePath(execFilesDir, "exec"+(i++));
            } while (separateExecDir.exists());

        	FilePath fullExecName = separateExecDir.child("jacoco.exec");
        	file.copyTo(fullExecName);
        }
    }

    /**
     * Parses the saved "jacoco.exec" files into an {@link ExecutionFileLoader}.
     */
    public ExecutionFileLoader parse(String[] includes, String[] excludes) throws IOException {
        ExecutionFileLoader efl = new ExecutionFileLoader();
        for (File exec : getExecFiles()) {
            efl.addExecFile(new FilePath(exec));
        }

        efl.setIncludes(includes);
        efl.setExcludes(excludes);
        efl.setClassDir(classesDir);
        efl.setSrcDir(sourcesDir);
        efl.loadBundleCoverage();

        return efl;
    }

    @Override
    public String toString() {
        return root.toString();
    }
}
