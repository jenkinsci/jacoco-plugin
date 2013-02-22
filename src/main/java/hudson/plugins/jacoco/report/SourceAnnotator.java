package hudson.plugins.jacoco.report;

import org.jacoco.core.analysis.ICounter;
import org.jacoco.core.analysis.ISourceNode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * Parses source file and annotates that with the coverage information.
 *
 * @author Kohsuke Kawaguchi
 */
public class SourceAnnotator {
    private final File src;

    public SourceAnnotator(File src) {
        this.src = src;
    }

    /**
     * Parses the source file into individual lines.
     */
    private List<String> readLines() throws IOException {
        ArrayList<String> aList = new ArrayList<String>();

        BufferedReader br = null;

        try {
            br = new BufferedReader(new FileReader(src));
            String line;
            while ((line = br.readLine()) != null) {
                aList.add(line.replaceAll("\\t", "&nbsp&nbsp&nbsp&nbsp").replaceAll("<", "&lt").replaceAll(">", "&gt"));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return aList;
    }

    public void printHighlightedSrcFile(ISourceNode cov, Writer output) {
        StringBuilder buf = new StringBuilder();
        try {
            List<String> sourceLines = readLines();
            output.write("<code style=\"white-space:pre;\">");
            for (int i = 1; i <= sourceLines.size(); ++i) {
                buf.setLength(0);
                int status = cov.getLine(i).getInstructionCounter().getStatus();
                if ((status == ICounter.FULLY_COVERED) || (status == ICounter.PARTLY_COVERED)) {
                    buf.append(i + ": ").append("<SPAN style=\"BACKGROUND-COLOR: #32cd32\">" + sourceLines.get(i - 1)).append("</SPAN>").append("<br>");
                } else {
                    buf.append(i + ": ").append(sourceLines.get(i - 1)).append("<br>");
                }
                output.write(buf.toString());
            }
            output.write("</code>");

            //logger.log(Level.INFO, "lines: " + buf);
        } catch (FileNotFoundException e) {
            buf.append("ERROR: Sourcefile does not exist!");
        } catch (IOException e) {
            buf.append("ERROR: Error while reading the sourcefile!");
        }
    }
}
