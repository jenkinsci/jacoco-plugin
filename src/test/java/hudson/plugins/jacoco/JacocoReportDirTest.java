package hudson.plugins.jacoco;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.lang.reflect.Method;

import org.junit.Test;

import hudson.FilePath;

public class JacocoReportDirTest extends AbstractJacocoTestBase {

    @Test
    public void testRemoveMaskFromPath() throws Exception {
        final JacocoReportDir reportDir = new JacocoReportDir(new File("/home/jacoco"));
        final Class<? extends JacocoReportDir> clazz = reportDir.getClass();
        final Method method = clazz.getDeclaredMethod("removeMaskFromPath", new Class[]{FilePath.class, String.class});

        // Unix paths
        assertEquals(new FilePath(new File("/home/jacoco/code/src/jacoco")),
                method.invoke(reportDir, new FilePath(new File("/home/jacoco/code/src/jacoco/src/main/java")),
                        "**/src/main/java"));

        assertEquals(new FilePath(new File("/home/jacoco/code/src/jacoco/src/main")),
                method.invoke(reportDir, new FilePath(new File("/home/jacoco/code/src/jacoco/src/main/java")),
                        "**/src/*/java"));

        assertEquals(new FilePath(new File("/home/jacoco/code/src/jacoco")),
                method.invoke(reportDir, new FilePath(new File("/home/jacoco/code/src/jacoco/src/main/java")),
                        "**/classes, **/src/main/java"));

        assertEquals(new FilePath(new File("/home/jacoco/code/src/jacoco/src/main")),
                method.invoke(reportDir, new FilePath(new File("/home/jacoco/code/src/jacoco/src/main/java")),
                        "**/classes, **/src/*/java"));

        // Windows paths
        assertEquals(new FilePath(new File("C:\\Users\\jacoco\\code\\src\\jacoco")),
                method.invoke(reportDir, new FilePath(new File("C:\\Users\\jacoco\\code\\src\\jacoco\\src\\main\\java")),
                        "**/src/main/java"));

        assertEquals(new FilePath(new File("C:\\Users\\jacoco\\code\\src\\jacoco\\src\\main")),
                method.invoke(reportDir, new FilePath(new File("C:\\Users\\jacoco\\code\\src\\jacoco\\src\\main\\java")),
                        "**/src/*/java"));

        assertEquals(new FilePath(new File("C:\\Users\\jacoco\\code\\src\\jacoco")),
                method.invoke(reportDir, new FilePath(new File("C:\\Users\\jacoco\\code\\src\\jacoco\\src\\main\\java")),
                        "**/classes, **/src/main/java"));

        assertEquals(new FilePath(new File("C:\\Users\\jacoco\\code\\src\\jacoco\\src\\main")),
                method.invoke(reportDir, new FilePath(new File("C:\\Users\\jacoco\\code\\src\\jacoco\\src\\main\\java")),
                        "**/classes, **/src/*/java"));

        // No expansion
        assertEquals(new FilePath(new File("/home/jacoco/code")),
                method.invoke(reportDir, new FilePath(new File("/home/jacoco/code")),
                        ""));

        assertEquals(new FilePath(new File("/home/jacoco/code")),
                method.invoke(reportDir, new FilePath(new File("/home/jacoco/code")),
                        " , **/src"));

        assertEquals(new FilePath(new File("/home/jacoco")),
                method.invoke(reportDir, new FilePath(new File("/home/jacoco/code")),
                        " , **/code"));

        assertEquals(new FilePath(new File("/home/jacoco/code")),
                method.invoke(reportDir, new FilePath(new File("/home/jacoco/code")),
                        "*"));

        // Not a valid pattern
        assertEquals(new FilePath(new File("/home/jacoco/code")),
                method.invoke(reportDir, new FilePath(new File("/home/jacoco/code")),
                        "nopattern"));
    }
}
