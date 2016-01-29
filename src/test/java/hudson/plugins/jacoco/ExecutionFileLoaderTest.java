package hudson.plugins.jacoco;

import static org.junit.Assert.*;
import hudson.FilePath;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.jacoco.core.analysis.IBundleCoverage;
import org.jacoco.core.analysis.IPackageCoverage;
import org.jacoco.core.internal.analysis.BundleCoverageImpl;
import org.junit.Test;


public class ExecutionFileLoaderTest {

	@Test
	public void test() throws IOException {
		ExecutionFileLoader loader = new ExecutionFileLoader();
		loader.addExecFile(new FilePath(new File(".")));
		
		assertNull(loader.getBundleCoverage());
		loader.setBundleCoverage(new BundleCoverageImpl("bundle", new ArrayList<IPackageCoverage>()));
		assertNotNull(loader.getBundleCoverage());
		
		assertNull(loader.getName());
		loader.setName("somename");
		assertEquals("somename", loader.getName());
		
		assertNull(loader.getGeneratedHTMLsDir());
		loader.setGeneratedHTMLsDir(new FilePath(new File("html")));
		assertNotNull(loader.getGeneratedHTMLsDir());
		
		assertNull(loader.getSrcDir());
		loader.setSrcDir(new FilePath(new File("src")));
		assertNotNull(loader.getSrcDir());
		
		assertNull(loader.getClassDir());
		loader.setClassDir(new FilePath(new File("class")));
		assertNotNull(loader.getClassDir());
		
		loader.setIncludes(new String[] {});
		loader.setExcludes(new String[] {});
	}
	
	@Test
	public void testLoadBundleCoverageNoIncludes() throws IOException {
		ExecutionFileLoader loader = new ExecutionFileLoader();
		
		loader.setClassDir(new FilePath(new File("target/classes")));
		IBundleCoverage coverage = loader.loadBundleCoverage();
		assertNotNull(coverage);
		assertNotNull(coverage.getClassCounter());
	}
	
	@Test
	public void testLoadBundleCoverageNoIncludes2() throws IOException {
		ExecutionFileLoader loader = new ExecutionFileLoader();
		
		loader.setClassDir(new FilePath(new File("target/classes")));
		loader.setIncludes(new String[] {});
		loader.setExcludes(new String[] {"excludme.test", "excludeme2.test"});

		IBundleCoverage coverage = loader.loadBundleCoverage();
		assertNotNull(coverage);
		assertNotNull(coverage.getClassCounter());
	}

	@Test
	public void testLoadBundleCoverageNoIncludes3() throws IOException {
		ExecutionFileLoader loader = new ExecutionFileLoader();
		
		loader.setClassDir(new FilePath(new File("target/classes")));
		loader.setIncludes(new String[] {""});
		loader.setExcludes(new String[] {});
		IBundleCoverage coverage = loader.loadBundleCoverage();
		assertNotNull(coverage);
		assertNotNull(coverage.getClassCounter());
		assertTrue("Expect to have at least some lines found, but had: " + coverage.getClassCounter(), 
				coverage.getClassCounter().getMissedCount() > 0 ||
				coverage.getClassCounter().getCoveredCount() > 0);
	}

	@Test
	public void testLoadBundleCoverageWithIncludes() throws IOException {
		ExecutionFileLoader loader = new ExecutionFileLoader();
		
		loader.setClassDir(new FilePath(new File("target/classes")));
		loader.setIncludes(new String[] {"noexisting.test"});
		loader.setExcludes(new String[] {""});		
		IBundleCoverage coverage = loader.loadBundleCoverage();
		assertNotNull(coverage);
		assertEquals(0, coverage.getClassCounter().getCoveredCount());
	}
	
	@Test
	public void testLoadBundleWithExecFile() throws IOException {
		ExecutionFileLoader loader = new ExecutionFileLoader();
		
		assertTrue("This test requires that a jacoco.exec file exists in the target-directory", 
				new File("target/jacoco.exec").exists());
		loader.setClassDir(new FilePath(new File("target/classes")));
		loader.setExcludes(new String[] {"excludme.test"});
		loader.addExecFile(new FilePath(new File("target/jacoco.exec")));
		
		// handles invalid files gracefully
		loader.addExecFile(new FilePath(new File("somenonexistingfile")));

		IBundleCoverage coverage = loader.loadBundleCoverage();
		assertNotNull(coverage);
		assertTrue("Expect to have at least some lines found, but had: " + coverage.getClassCounter(), 
				coverage.getClassCounter().getMissedCount() > 0 ||
				coverage.getClassCounter().getCoveredCount() > 0);
	}
	
	@Test
	public void testLoadBundleWithoutClasses() throws IOException {
		// Special Jenkins publisher case to handle empty classes dir
		ExecutionFileLoader loader = new ExecutionFileLoader();
		
		assertTrue("This test requires that a jacoco.exec file exists in the target-directory", 
				new File("target/jacoco.exec").exists());
		File noClasses = new File("target/noclasses");
		noClasses.mkdir();
		assertTrue("This test requires that noclasses dir exists", 
				noClasses.exists() && noClasses.isDirectory());
		loader.setClassDir(new FilePath(noClasses));
		loader.setExcludes(new String[] {"excludme.test"});
		loader.addExecFile(new FilePath(new File("target/jacoco.exec")));
		
		// handles empty classes dir gracefully
		IBundleCoverage coverage = loader.loadBundleCoverage();
		assertNotNull(coverage);
		assertTrue("Expect to have no lines found, but had: " + coverage.getClassCounter(), 
				coverage.getClassCounter().getMissedCount() == 0 ||
				coverage.getClassCounter().getCoveredCount() == 0);
	}
}
