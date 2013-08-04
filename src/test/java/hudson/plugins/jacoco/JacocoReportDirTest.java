package hudson.plugins.jacoco;

import static org.junit.Assert.assertFalse;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.codehaus.plexus.util.FileUtils;
import org.jacoco.maven.FileFilter;
import org.junit.Test;

import edu.emory.mathcs.backport.java.util.Arrays;

public class JacocoReportDirTest {

	@Test
	public void test() throws IOException {
		File jacocoRoot = new File("/opt/jenkins/builds/JaCoCo-local-git-update+commons-dost/2013-08-04_10-14-23/jacoco");

		final FileFilter fileFilter = new FileFilter(Arrays.asList(new String[] {"**"}), Arrays.asList(new String[] {"org/jacoco/agent/rt/RT.class"}));
		final List<File> filesToAnalyze = FileUtils.getFiles(new File(jacocoRoot, "classes"), fileFilter.getIncludes(), fileFilter.getExcludes());
		assertFalse(filesToAnalyze.contains(
				new File("/opt/jenkins/builds/JaCoCo-local-git-update+commons-dost/2013-08-04_10-14-23/jacoco/classes/org/jacoco/agent/rt/RT.class")));
		
		JacocoReportDir dir =  new JacocoReportDir(jacocoRoot);
		dir.parse(null, new String[] {"org/jacoco/agent/rt/RT.class", "com/vladium/emma/rt/RT.class"});
		
//		[JaCoCo plugin] Collecting JaCoCo coverage data...
		//[JaCoCo plugin] */target/jacoco.exec;*/target/classes;**/src; locations are configured
	/*	[JaCoCo plugin] Number of found exec files: 5
		[JaCoCo plugin] Saving matched execfiles:  /opt/JaCoCo/git/org.jacoco.agent.rt.test/target/jacoco.exec /opt/JaCoCo/git/org.jacoco.agent.test/target/jacoco.exec /opt/JaCoCo/git/org.jacoco.ant.test/target/jacoco.exec /opt/JaCoCo/git/org.jacoco.core.test/target/jacoco.exec /opt/JaCoCo/git/org.jacoco.report.test/target/jacoco.exec
		[JaCoCo plugin] Saving matched class directories:  /opt/JaCoCo/git/jacoco-maven-plugin.test/target/classes /opt/JaCoCo/git/jacoco-maven-plugin/target/classes /opt/JaCoCo/git/org.jacoco.agent.rt.test/target/classes /opt/JaCoCo/git/org.jacoco.agent.rt/target/classes /opt/JaCoCo/git/org.jacoco.agent.test/target/classes /opt/JaCoCo/git/org.jacoco.agent/target/classes /opt/JaCoCo/git/org.jacoco.ant.test/target/classes /opt/JaCoCo/git/org.jacoco.ant/target/classes /opt/JaCoCo/git/org.jacoco.core.test/target/classes /opt/JaCoCo/git/org.jacoco.core/target/classes /opt/JaCoCo/git/org.jacoco.doc/target/classes /opt/JaCoCo/git/org.jacoco.examples/target/classes /opt/JaCoCo/git/org.jacoco.report.test/target/classes /opt/JaCoCo/git/org.jacoco.report/target/classes
		[JaCoCo plugin] Saving matched source directories:  /opt/JaCoCo/git/jacoco-maven-plugin.test/it/it-check-fails-halt/src /opt/JaCoCo/git/jacoco-maven-plugin.test/it/it-check-fails-no-halt/src /opt/JaCoCo/git/jacoco-maven-plugin.test/it/it-check-passes/src /opt/JaCoCo/git/jacoco-maven-plugin.test/it/it-includes-excludes/src /opt/JaCoCo/git/jacoco-maven-plugin.test/it/it-multi-module/child/src /opt/JaCoCo/git/jacoco-maven-plugin.test/it/it-multi-module/skip-child/src /opt/JaCoCo/git/jacoco-maven-plugin.test/it/it-offline-instrumentation/child-without-main-classes/src /opt/JaCoCo/git/jacoco-maven-plugin.test/it/it-offline-instrumentation/child/src /opt/JaCoCo/git/jacoco-maven-plugin.test/it/it-report-without-debug/src /opt/JaCoCo/git/jacoco-maven-plugin.test/it/it-site/src /opt/JaCoCo/git/jacoco-maven-plugin/src /opt/JaCoCo/git/org.jacoco.agent.rt.test/src /opt/JaCoCo/git/org.jacoco.agent.rt/src /opt/JaCoCo/git/org.jacoco.agent.test/src /opt/JaCoCo/git/org.jacoco.agent/src /opt/JaCoCo/git/org.jacoco.ant.test/src /opt/JaCoCo/git/org.jacoco.ant/src /opt/JaCoCo/git/org.jacoco.core.test/src /opt/JaCoCo/git/org.jacoco.core/src /opt/JaCoCo/git/org.jacoco.examples/build/src /opt/JaCoCo/git/org.jacoco.examples/src /opt/JaCoCo/git/org.jacoco.examples/target/archive-tmp/fileSetFormatter.1676605753.tmp/src /opt/JaCoCo/git/org.jacoco.report.test/src /opt/JaCoCo/git/org.jacoco.report/src
		[JaCoCo plugin] Loading inclusions files..
		[JaCoCo plugin] inclusions: []
		[JaCoCo plugin] exclusions: [org/jacoco/agent/rt/RT]
		ERROR: Publisher hudson.plugins.jacoco.JacocoPublisher aborted due to exception
		java.io.IOException: Error while analyzing class /opt/jenkins/builds/JaCoCo-local-git-update+commons-dost/2013-08-04_10-14-23/jacoco/classes/org/jacoco/agent/rt/RT.class.
		*/
	}
}
