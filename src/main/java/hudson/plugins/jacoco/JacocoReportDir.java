package hudson.plugins.jacoco;

import hudson.FilePath;
import hudson.model.TaskListener;

import java.io.File;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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

    public Entry<FilePath, FilePath> symlinkClassesFrom(@Nonnull FilePath source, @Nonnull String fileMask, @Nonnull TaskListener listener) throws IOException, InterruptedException {
        FilePath destination = new FilePath(getClassesDir());
        destination.mkdirs();

        source = this.removeMaskFromPath(source, fileMask);
        String child = "";
        if (!destination.getBaseName().equals(source.getBaseName())) {
            child = source.getBaseName();
        }
        destination = new FilePath(new File(destination.getRemote(), child));
        destination.symlinkTo(source.getRemote(), listener);

        return new AbstractMap.SimpleEntry<FilePath, FilePath>(source, destination);
    }

    /**
     * Where we store *.java files, honoring package names as directories.
     * @return Directory to which we store *.java files, honoring package names as directories.
     */
    public File getSourcesDir() {
        return new File(root,"sources");
    }

    public int saveSourcesFrom(@Nonnull FilePath dir, @Nonnull String fileMask) throws IOException, InterruptedException {
        FilePath d = new FilePath(getSourcesDir());
        d.mkdirs();
        return dir.copyRecursiveTo(fileMask, d);
    }

    public Entry<FilePath, FilePath> symlinkSourcesFrom(@Nonnull FilePath source, @Nonnull String fileMask, @Nonnull TaskListener listener) throws IOException, InterruptedException {
        FilePath destination = new FilePath(getSourcesDir());
        destination.mkdirs();

        source = this.removeMaskFromPath(source, fileMask);
        destination = new FilePath(new File(destination.getRemote(), source.getBaseName()));
        destination.symlinkTo(source.getRemote(), listener);

        return new AbstractMap.SimpleEntry<FilePath, FilePath>(source, destination);
    }

    protected FilePath removeMaskFromPath(@Nonnull FilePath path, @Nonnull String fileMask) {
        String[] fileMasks;
        if (fileMask.contains(",")) {
            fileMasks = fileMask.split(", ");
        } else {
            fileMasks = new String[] { fileMask };
        }

        for (String mask : fileMasks) {
            FilePath deductedPath = new FilePath(new File(path.getRemote()));
            if (mask.contains("*")) {
                String suffix = mask.substring(mask.lastIndexOf("*") + 1);
                String splitPattern = Pattern.quote("/");

                if (path.getRemote().contains("\\")) {
                    suffix = suffix.replace("/", "\\");
                    splitPattern = Pattern.quote("\\");
                }

                final FilePath fpSuffix = new FilePath(new File(suffix));

                // Suffix doesn't match this path
                if (!path.getRemote().endsWith(fpSuffix.getRemote())) {
                    continue;
                }

                final List<String> children = Arrays.stream(suffix.split(splitPattern))
                        .filter(child -> !child.equals(""))
                        .collect(Collectors.toList());

                for (int i = 0; i < children.size(); i++) {
                    deductedPath = deductedPath.getParent();
                }
            }

            // We are going to assume that the first mask
            if (!path.equals(deductedPath)) {
                return deductedPath;
            }
        }

        return path;
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
     * @throws InterruptedException if thread is interrupted
     */
    public ExecutionFileLoader parse(String[] includes, String... excludes) throws IOException, InterruptedException {
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
