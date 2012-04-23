package hudson.plugins.jacoco;

import hudson.FilePath;
import hudson.model.HealthReport;
import hudson.model.HealthReportingAction;
import hudson.model.Result;
import hudson.model.AbstractBuild;
import hudson.plugins.jacoco.Messages;
import hudson.plugins.jacoco.model.Coverage;
import hudson.plugins.jacoco.model.CoverageElement;
import hudson.plugins.jacoco.model.CoverageObject;
import hudson.plugins.jacoco.model.CoverageElement.Type;
import hudson.plugins.jacoco.report.CoverageReport;
import hudson.util.IOException2;
import hudson.util.NullStream;
import hudson.util.StreamTaskListener;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jvnet.localizer.Localizable;
import org.kohsuke.stapler.StaplerProxy;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

/**
 * Build view extension by Emma plugin.
 *
 * As {@link CoverageObject}, it retains the overall coverage report.
 *
 * @author Kohsuke Kawaguchi
 */
public final class EmmaBuildAction extends CoverageObject<EmmaBuildAction> implements HealthReportingAction, StaplerProxy {
	
    public final AbstractBuild<?,?> owner;

    private transient WeakReference<CoverageReport> report;

    /**
     * Non-null if the coverage has pass/fail rules.
     */
    private final Rule rule;

    /**
     * The thresholds that applied when this build was built.
     * @TODO add ability to trend thresholds on the graph
     */
    private final EmmaHealthReportThresholds thresholds;

    /**
     * 
     * @param owner
     * @param rule
     * @param ratios
     *            The available coverage ratios in the report. Null is treated
     *            the same as an empty map.
     * @param thresholds
     */
    public EmmaBuildAction(AbstractBuild<?,?> owner, Rule rule,
    		Map<CoverageElement.Type, Coverage> ratios,
    		EmmaHealthReportThresholds thresholds) {
        if (ratios == null) {
            ratios = Collections.emptyMap();
        }
        this.owner = owner;
        this.rule = rule;
        this.clazz = getOrCreateRatio(ratios, CoverageElement.Type.CLASS);
        this.method = getOrCreateRatio(ratios, CoverageElement.Type.METHOD);
        this.line = getOrCreateRatio(ratios, CoverageElement.Type.LINE);
        this.thresholds = thresholds;
        this.branch = getOrCreateRatio(ratios, CoverageElement.Type.BRANCH);
        this.instruction = getOrCreateRatio(ratios, CoverageElement.Type.INSTRUCTION);
        this.complexity = getOrCreateRatio(ratios, CoverageElement.Type.COMPLEXITY);
    }

    private Coverage getOrCreateRatio(Map<CoverageElement.Type, Coverage> ratios, CoverageElement.Type type) {
        Coverage r = ratios.get(type);
        if (r == null) {
            r = new Coverage();
        }
        return r;
    }

    public String getDisplayName() {
        return Messages.BuildAction_DisplayName();
    }

    public String getIconFileName() {
        return "graph.gif";
    }

    public String getUrlName() {
        return "jacoco";
    }

    /**
     * Get the coverage {@link hudson.model.HealthReport}.
     *
     * @return The health report or <code>null</code> if health reporting is disabled.
     * @since 1.7
     */
    public HealthReport getBuildHealth() {
        if (thresholds == null) {
            // no thresholds => no report
            return null;
        }
        thresholds.ensureValid();
        int score = 100, percent;
        ArrayList<Localizable> reports = new ArrayList<Localizable>(5);
        if (clazz != null && thresholds.getMaxClass() > 0) {
            percent = clazz.getPercentage();
            if (percent < thresholds.getMaxClass()) {
                reports.add(Messages._BuildAction_Classes(clazz, percent));
            }
            score = updateHealthScore(score, thresholds.getMinClass(),
                                      percent, thresholds.getMaxClass());
        }
        if (method != null && thresholds.getMaxMethod() > 0) {
            percent = method.getPercentage();
            if (percent < thresholds.getMaxMethod()) {
                reports.add(Messages._BuildAction_Methods(method, percent));
            }
            score = updateHealthScore(score, thresholds.getMinMethod(),
                                      percent, thresholds.getMaxMethod());
        }
        if (line != null && thresholds.getMaxLine() > 0) {
            percent = line.getPercentage();
            if (percent < thresholds.getMaxLine()) {
                reports.add(Messages._BuildAction_Lines(line, percent));
            }
            score = updateHealthScore(score, thresholds.getMinLine(),
                                      percent, thresholds.getMaxLine());
        }
        if (branch != null && thresholds.getMaxBranch() > 0) {
            percent = branch.getPercentage();
            if (percent < thresholds.getMaxBranch()) {
                reports.add(Messages._BuildAction_Branches(branch, percent));
            }
            score = updateHealthScore(score, thresholds.getMinBranch(),
                                      percent, thresholds.getMaxBranch());
        }
        if (instruction != null && thresholds.getMaxInstruction() > 0) {
            percent = instruction.getPercentage();
            if (percent < thresholds.getMaxInstruction()) {
                reports.add(Messages._BuildAction_Instructions(instruction, percent));
            }
            score = updateHealthScore(score, thresholds.getMinInstruction(),
                                      percent, thresholds.getMaxInstruction());
        }
        if (score == 100) {
            reports.add(Messages._BuildAction_Perfect());
        }
        // Collect params and replace nulls with empty string
        Object[] args = reports.toArray(new Object[5]);
        for (int i = 4; i >= 0; i--) if (args[i]==null) args[i] = ""; else break;
        return new HealthReport(score, Messages._BuildAction_Description(
                args[0], args[1], args[2], args[3], args[4]));
    }

    private static int updateHealthScore(int score, int min, int value, int max) {
        if (value >= max) return score;
        if (value <= min) return 0;
        assert max != min;
        final int scaled = (int) (100.0 * ((float) value - min) / (max - min));
        if (scaled < score) return scaled;
        return score;
    }

    public Object getTarget() {
        return getResult();
    }

    @Override
    public AbstractBuild<?,?> getBuild() {
        return owner;
    }
    
	protected static FilePath[] getEmmaReports(File file) throws IOException, InterruptedException {
		FilePath path = new FilePath(file);
		if (path.isDirectory()) {
			return path.list("*xml");
		} else {
			// Read old builds (before 1.11) 
			FilePath report = new FilePath(new File(path.getName() + ".xml"));
			return report.exists() ? new FilePath[]{report} : new FilePath[0];
		}
	}

    /**
     * Obtains the detailed {@link CoverageReport} instance.
     */
    public synchronized CoverageReport getResult() {

        if(report!=null) {
            final CoverageReport r = report.get();
            if(r!=null)     return r;
        }

        final File reportFolder = JacocoPublisher.getEmmaReport(owner);

        try {
        	
        	// Get the list of report files stored for this build
            FilePath[] reports = getEmmaReports(reportFolder);
            InputStream[] streams = new InputStream[reports.length];
            for (int i=0; i<reports.length; i++) {
            	streams[i] = reports[i].read();
            }
            
            // Generate the report
            CoverageReport r = new CoverageReport(this, streams);

            if(rule!=null) {
                // we change the report so that the FAILED flag is set correctly
                logger.info("calculating failed packages based on " + rule);
                rule.enforce(r,new StreamTaskListener(new NullStream()));
            }

            report = new WeakReference<CoverageReport>(r);
            return r;
        } catch (InterruptedException e) {
            logger.log(Level.WARNING, "Failed to load " + reportFolder, e);
            return null;
        } catch (IOException e) {
            logger.log(Level.WARNING, "Failed to load " + reportFolder, e);
            return null;
        }
    }

    @Override
    public EmmaBuildAction getPreviousResult() {
        return getPreviousResult(owner);
    }

    /**
     * Gets the previous {@link EmmaBuildAction} of the given build.
     */
    /*package*/ static EmmaBuildAction getPreviousResult(AbstractBuild<?,?> start) {
        AbstractBuild<?,?> b = start;
        while(true) {
            b = b.getPreviousBuild();
            if(b==null)
                return null;
            if(b.getResult()== Result.FAILURE)
                continue;
            EmmaBuildAction r = b.getAction(EmmaBuildAction.class);
            if(r!=null)
                return r;
        }
    }

    /**
     * Constructs the object from JaCoCo XML report files.
     * See <a href="https://github.com/jfuerth/emma-plugin/blob/jacoco/src/test/resources/hudson/plugins/emma/jacoco.xml">an example XML file</a>.
     *
     * @throws IOException
     *      if failed to parse the file.
     */
    public static EmmaBuildAction load(AbstractBuild<?,?> owner, Rule rule, EmmaHealthReportThresholds thresholds, FilePath... files) throws IOException {
        Map<CoverageElement.Type,Coverage> ratios = null;
        for (FilePath f: files ) {
            InputStream in = f.read();
            try {
                ratios = loadRatios(in, ratios);
            } catch (XmlPullParserException e) {
                throw new IOException2("Failed to parse " + f, e);
            } finally {
                in.close();
            }
        }
        return new EmmaBuildAction(owner, rule, ratios, thresholds);
    }

    public static EmmaBuildAction load(AbstractBuild<?,?> owner, Rule rule, EmmaHealthReportThresholds thresholds, InputStream... streams) throws IOException, XmlPullParserException {
        Map<CoverageElement.Type,Coverage> ratios = null;
        for (InputStream in: streams) {
          ratios = loadRatios(in, ratios);
        }
        return new EmmaBuildAction(owner, rule, ratios, thresholds);
    }

    /**
     * Extracts top-level coverage information from the JaCoCo report document.
     * 
     * @param in
     * @param ratios
     * @return
     * @throws IOException
     * @throws XmlPullParserException
     */
    private static Map<Type, Coverage> loadRatios(InputStream in, Map<Type, Coverage> ratios) throws IOException, XmlPullParserException {

        if (ratios == null) {
            ratios = new LinkedHashMap<CoverageElement.Type, Coverage>();
        }

        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser parser = factory.newPullParser();
        parser.setInput(in, null);
        
        int eventType = parser.getEventType();
        do {
            // this predicate matches the start tags of the elements selected by the XPath expression "/report/counter"
            if (eventType == XmlPullParser.START_TAG && parser.getName().equals("counter") && parser.getDepth() == 2) {
                Type type = Type.valueOf(parser.getAttributeValue("", "type"));
                int covered = Integer.parseInt(parser.getAttributeValue("", "covered"));
                int missed = Integer.parseInt(parser.getAttributeValue("", "missed"));

                Coverage ratio = ratios.get(type);
                if (ratio == null) {
                    ratio = new Coverage();
                    ratios.put(type, ratio);
                }
                ratio.accumulate(missed, covered);
            }
            eventType = parser.next();
        } while (eventType != XmlPullParser.END_DOCUMENT);
        
        return ratios;

    }

    private static final Logger logger = Logger.getLogger(EmmaBuildAction.class.getName());
}
