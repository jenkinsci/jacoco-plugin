package hudson.plugins.jacococoveragecolumn;

import hudson.model.Job;
import hudson.plugins.jacoco.JacocoBuildAction;
import hudson.search.QuickSilver;
import hudson.util.StreamTaskListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.jelly.Jelly;
import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.Script;
import org.apache.commons.jelly.XMLOutput;
import org.junit.Test;
import org.kohsuke.stapler.export.Exported;

import static org.junit.Assert.assertEquals;

public class JaCoCoColumn_column_jelly_Test
{
	@Test
	public void test() throws Exception
	{

		Jelly jelly = new Jelly();
		File tmp = File.createTempFile("jelly_test", "no_loc");
		File org = new File("src/main/resources/hudson/plugins/jacoco/JacocoPublisher/config.jelly"/*"src/main/resources/hudson/plugins/jacococoveragecolumn/JaCoCoColumn/column.jelly"*/);

		FileInputStream fis = new FileInputStream(org);
		byte[] data = new byte[(int) org.length()];
		fis.read(data);
		fis.close();
		String content = new String(data).replaceAll("/$\\{\\%(.*?)\\}", "$1");

		new FileOutputStream(tmp).write(content.getBytes());


		final Job<?, ?> mockJob = new JaCoCoColumnTest.MyJob("externaljob") {
			@Override
			@Exported
			@QuickSilver
			public JaCoCoColumnTest.MyRun getLastSuccessfulBuild() {
				try {
					JaCoCoColumnTest.MyRun newBuild = newBuild();
					newBuild.addAction(new JacocoBuildAction(null, null, StreamTaskListener.fromStdout(), null, null, null));
					assertEquals(1, newBuild.getActions().size());
					return newBuild;
				} catch (IOException e) {
					throw new IllegalStateException(e);
				}
			}

			@Override
			protected synchronized void saveNextBuildNumber() throws IOException {
			}
		};

		jelly.setUrl(tmp.toURL());
		Script script = jelly.compileScript();
		JellyContext context = new JellyContext();
		context.setVariable("it", new JaCoCoColumn());
		context.setVariable("job", mockJob);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		XMLOutput xmlOutput = XMLOutput.createXMLOutput(baos);
		script.run(context, xmlOutput);

		System.out.println(new String(baos.toByteArray()));
	}
}
