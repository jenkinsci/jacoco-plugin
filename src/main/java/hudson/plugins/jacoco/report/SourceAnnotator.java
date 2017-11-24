package hudson.plugins.jacoco.report;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.jacoco.core.analysis.ICounter;
import org.jacoco.core.analysis.ILine;
import org.jacoco.core.analysis.ISourceNode;

/**
 * Parses source file and annotates that with the coverage information.
 *
 * @author Kohsuke Kawaguchi
 * @author Marcus Bauer
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
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(src), StandardCharsets.UTF_8))) {
            ArrayList<String> aList = new ArrayList<>();
            String line;
            while ((line = br.readLine()) != null) {
                aList.add(line.replaceAll("\\t", "&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp").replaceAll("<", "&lt").replaceAll(">", "&gt"));
            }
            return aList;
        }
    }

    public void printHighlightedSrcFile(ISourceNode cov, Writer output) {
        try {
            StringBuilder buf = new StringBuilder();
            List<String> sourceLines;
            try {
                sourceLines = readLines();
            } catch (IOException e) {
                e.printStackTrace();
                output.write("ERROR: Error while reading the sourcefile!");
                return;
            }
            output.write("<code style=\"white-space:pre;\">");
            for (int i = 1; i <= sourceLines.size(); ++i) {
                buf.setLength(0);

                ILine line = cov.getLine(i);
                ICounter branches = line.getBranchCounter();
                int status = line.getStatus();
                if (status != ICounter.EMPTY) {
                    printHighlightedLine(buf, i, branches, sourceLines.get(i - 1), status);
                } else {
                    buf.append(i).append(": ").append(sourceLines.get(i - 1)).append("<br>");
                }
                output.write(buf.toString());
            }
            output.write("</code>");

            //logger.log(Level.INFO, "lines: " + buf);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Formats a source code line
     * 
     * @param buf
     *            source to write to.
     * @param lineNumber
     *            line number to output
     * @param cov
     *            branch coverage data for this line
     * @param sourceLine
     *            source code line
     * @param status
     *            coverage status of this line
     */
    private void printHighlightedLine(StringBuilder buf, int lineNumber, ICounter cov, String sourceLine, int status) {
        buf.append(lineNumber).append(":");

        String tooltip = getTooltip(cov);
        if (tooltip != null) {
            buf.append("•<SPAN title=\"").append(tooltip).append("\"");
        } else {
            buf.append(" <SPAN");
        }

        buf.append(" style=\"BACKGROUND-COLOR: ").append(getStatusColor(status)).append("\">").append(sourceLine).append("</SPAN>").append("<br>");
    }

    /**
     * Returns a tooltip for the branch coverage data.
     * 
     * @param cov
     *            branch coverage data
     * @return Tooltip if branch coverage data exists for the given line,
     *         otherwise <code>null</code>
     */
    private String getTooltip(ICounter cov) {
        switch (cov.getStatus()) {
        case ICounter.FULLY_COVERED:
            return "All " + cov.getTotalCount() + " branches covered.";

        case ICounter.PARTLY_COVERED:
            return cov.getMissedCount() + " of " + cov.getTotalCount() + " branches missed.";

        case ICounter.NOT_COVERED:
            return "All " + cov.getTotalCount() + " branches missed.";

        default:
            return null;
        }
    }

    /**
     * Returns a HTML color for each line status
     * 
     * @param status
     *            Status of the line
     * @return HTML color code for the background of the line, "none" if none
     * @see ICounter#getStatus()
     */
    private String getStatusColor(int status) {
        switch (status) {
        case ICounter.FULLY_COVERED:
            return "#ccffcc";

        case ICounter.PARTLY_COVERED:
            return "#ffff80";

        case ICounter.NOT_COVERED:
            return "#ffaaaa";

        default:
            return "none";
        }
    }
}
