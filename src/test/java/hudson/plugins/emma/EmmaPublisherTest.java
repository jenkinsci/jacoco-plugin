package hudson.plugins.emma;

import hudson.FilePath;

import junit.framework.Assert;

import java.io.File;

/**
 * 
 * @autor manuel_carrasco
 */
public class EmmaPublisherTest extends AbstractEmmaTestBase {
	
	public void testLocateReports() throws Exception {

		// Create a temporary workspace in the system 
		File w = File.createTempFile("workspace", ".test");
		w.delete();
		w.mkdir();
		w.deleteOnExit();
		FilePath workspace = new FilePath(w);

		// Create 4 files in the workspace
		File f1 = File.createTempFile("coverage", ".xml", w);
		f1.deleteOnExit();
		File f2 = File.createTempFile("anyname", ".xml", w);
		f2.deleteOnExit();
		File f3 = File.createTempFile("coverage", ".xml", w);
		f3.deleteOnExit();
		File f4 = File.createTempFile("anyname", ".xml", w);
		f4.deleteOnExit();


		// Create a folder and move there 2 files
		File d1 = new File(workspace.child("subdir").getRemote());
		d1.mkdir();
		d1.deleteOnExit();

		File f5 = new File(workspace.child(d1.getName()).child(f3.getName()).getRemote());
		File f6 = new File(workspace.child(d1.getName()).child(f4.getName()).getRemote());
		f3.renameTo(f5);
		f4.renameTo(f6);
		f5.deleteOnExit();
		f6.deleteOnExit();
		
		// Look for files in the entire workspace recursively without providing 
		// the includes parameter
		FilePath[] reports = EmmaPublisher.locateCoverageReports(workspace, "**/coverage*.xml");
		Assert.assertEquals(2 , reports.length);

		// Generate a includes string and look for files 
		String includes = f1.getName() + "; " + f2.getName() + "; " + d1.getName();
		reports = EmmaPublisher.locateCoverageReports(workspace, includes);
		Assert.assertEquals(3, reports.length);

		// Save files in local workspace
		FilePath local = workspace.child("coverage_localfolder");
		EmmaPublisher.saveCoverageReports(local, reports);
		Assert.assertEquals(3, local.list().size());
		local.deleteRecursive();

	}

}