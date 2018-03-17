package hudson.plugins.jacoco;

import hudson.FilePath;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;

/**
 * Encapsulates the directory structure in $JENKINS_HOME where we store jacoco related files.
 *
 * @author Kohsuke Kawaguchi
 */
public class JacocoReportDir {
    private final File root;

    public JacocoReportDir(File rootDir) {
        root = new File(rootDir, "jacoco");
    }

    /**
     * Where we store *.class files, honoring package names as directories.
     * @return Directory to which we store *.class files, honoring package names as directories.
     */
    public File getClassesDir() {
        return new File(root,"classes");
    }

    public int saveClassesFrom(@Nonnull FilePath dir, @Nonnull String fileMask) throws IOException, InterruptedException {
        FilePath d = new FilePath(getClassesDir());
        d.mkdirs();
        return dir.copyRecursiveTo(fileMask, d);
    }

    /**
     * Where we store *.java files, honoring package names as directories.
     * @return Directory to which we store *.java files, honoring package names as directories.
     */
    public File getSourcesDir() {
        return new File(root,"sources");
    }

    public int saveSourcesFrom(@Nonnull FilePath dir, @Nonnull String inclusionMask, @Nonnull String exclusionMask) throws IOException, InterruptedException {
        FilePath d = new FilePath(getSourcesDir());
        d.mkdirs();
        return dir.copyRecursiveTo(inclusionMask, exclusionMask, d);
    }

    /**
     * Root directory that stores jacoco.exec files.
     * Each exec file is stored in its own directory.
     * @return Directory that stores jacoco.exec files.
     *
     * @see #getExecFiles()
     */
    public File getExecFilesDir() {
        return new File(root,"execFiles");
    }

    /**
     * Lists up existing jacoco.exec files.
     * @return List of existing jacoco.exec files.
     */
    public List<File> getExecFiles() {
        List<File> r = new ArrayList<>();
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

    /**
     * Parses the saved "jacoco.exec" files into an {@link ExecutionFileLoader}.
     * @param includes see {@link ExecutionFileLoader#setIncludes}
     * @param excludes see {@link ExecutionFileLoader#setExcludes}
     * @return the configured {@code ExecutionFileLoader}
     * @throws IOException if any I/O error occurs
     */
    public ExecutionFileLoader parse(String[] includes, String... excludes) throws IOException {
        ExecutionFileLoader efl = new ExecutionFileLoader();
        for (File exec : getExecFiles()) {
            efl.addExecFile(new FilePath(exec));
        }

        efl.setIncludes(includes);
        efl.setExcludes(excludes);
        efl.setClassDir(new FilePath(getClassesDir()));
        efl.setSrcDir(new FilePath(getSourcesDir()));
        efl.loadBundleCoverage();

        return efl;
    }

    @Override
    public String toString() {
        return root.toString();
    }
}
