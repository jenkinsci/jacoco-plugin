package hudson.plugins.jacoco.report;

import hudson.model.AbstractBuild;
import hudson.plugins.jacoco.ExecutionFileLoader;
import hudson.plugins.jacoco.JacocoBuildAction;
import hudson.plugins.jacoco.JacocoHealthReportThresholds;
import hudson.plugins.jacoco.model.Coverage;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import org.jacoco.core.analysis.IClassCoverage;
import org.jacoco.core.analysis.IMethodCoverage;
import org.jacoco.core.analysis.IPackageCoverage;

/**
 * Root object of the coverage report.
 * 
 * @author Kohsuke Kawaguchi
 * @author Ognjen Bubalo
 */
public final class CoverageReport extends AggregatedReport<CoverageReport/*dummy*/,CoverageReport,PackageReport> {
	private final JacocoBuildAction action;

	private CoverageReport(JacocoBuildAction action) {
		this.action = action;
		setName("Jacoco");
	}
	
	private String instructionColor;
	private String classColor;
	private String branchColor;
	private String complexityColor;
	private String lineColor;
	private String methodColor;
	public JacocoHealthReportThresholds healthReports;

	/**
	 * Loads the exec files using JaCoCo API. Creates the reporting objects and the report tree.
	 * 
	 * @param action
	 * @param reports
	 * @throws IOException
	 */
	public CoverageReport(JacocoBuildAction action, ExecutionFileLoader executionFileLoader ) throws IOException {
		this(action);
		try {

			action.logger.println("[JaCoCo plugin] Loading packages..");

			if (executionFileLoader.getBundleCoverage() !=null ) {
				setAllCovTypes(this, executionFileLoader.getBundleCoverage());
				
				ArrayList<IPackageCoverage> packageList = new ArrayList<IPackageCoverage>(executionFileLoader.getBundleCoverage().getPackages());
				for (IPackageCoverage packageCov: packageList) {
					PackageReport packageReport = new PackageReport();
					packageReport.setName(packageCov.getName());
					packageReport.setParent(this);
					this.setCoverage(packageReport, packageCov);

					ArrayList<IClassCoverage> classList = new ArrayList<IClassCoverage>(packageCov.getClasses());
					for (IClassCoverage classCov: classList) {
						ClassReport classReport = new ClassReport();
						classReport.setName(classCov.getName());
						classReport.setParent(packageReport);
                        classReport.setSrcFileInfo(classCov, executionFileLoader.getSrcDir() + "/" + packageCov.getName() + "/" + classCov.getSourceFileName());

						packageReport.setCoverage(classReport, classCov);

						ArrayList<IMethodCoverage> methodList = new ArrayList<IMethodCoverage>(classCov.getMethods());
						for (IMethodCoverage methodCov: methodList) {
							MethodReport methodReport = new MethodReport();
							methodReport.setName(methodCov.getName());
							methodReport.setParent(classReport);
							classReport.setCoverage(methodReport, methodCov);
							methodReport.setSrcFileInfo(methodCov);

							classReport.add(methodReport);
						}

						packageReport.add(classReport);
					}

					this.add(packageReport);
				}
			}
			action.logger.println("[JaCoCo plugin] Done.");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static NumberFormat dataFormat = new DecimalFormat("000.00", new DecimalFormatSymbols(Locale.US));
	static NumberFormat percentFormat = new DecimalFormat("0.0", new DecimalFormatSymbols(Locale.US));
	static NumberFormat intFormat = new DecimalFormat("0", new DecimalFormatSymbols(Locale.US));
	
	@Override
	protected void printRatioCell(boolean failed, Coverage ratio, StringBuilder buf) {
		if (ratio != null && ratio.isInitialized()) {
			//String className = "nowrap" + (failed ? " red" : "");
			String bgColor = "#FFFFFF";
			
			if (JacocoHealthReportThresholds.RESULT.BETWEENMINMAX == healthReports.getResultByTypeAndRatio(ratio)) {
				bgColor = "#FF8000";
			} else if (JacocoHealthReportThresholds.RESULT.BELLOWMINIMUM == healthReports.getResultByTypeAndRatio(ratio)) {
				bgColor = "#FF0000";
			}
			buf.append("<td bgcolor=\" "+ bgColor +" \" class='").append("").append("'");
			buf.append(" data='").append(dataFormat.format(ratio.getPercentageFloat()));
			buf.append("'>\n");
			printRatioTable(ratio, buf);
			buf.append("</td>\n");
		}
	}
	
	@Override
	protected void printRatioTable(Coverage ratio, StringBuilder buf){
		String percent = percentFormat.format(ratio.getPercentageFloat());
		String numerator = intFormat.format(ratio.getMissed());
		String denominator = intFormat.format(ratio.getCovered());

		buf.append("<table class='percentgraph' cellpadding='0px' cellspacing='0px'><tr class='percentgraph'>")
		.append("<td width='40px' class='data'>").append(ratio.getPercentage()).append("%</td>")
		.append("<td class='percentgraph'>")
		.append("<div class='percentgraph' style='width: ").append(100).append("px;'>")
		.append("<div class='redbar' style='width: ").append(ratio.getMissed() > ratio.getCovered() ? 100 :  ((float)ratio.getMissed()/(float)ratio.getCovered())*100).append("px;'>")
		.append("</div></div></td></tr>" +
				"<tr>").append("<span class='text'>").append("<b>M:</b> "+numerator).append(" ").append("<b>C:</b> "+ denominator).append("</span></tr>").append("</table>");
	}



	@Override
	public CoverageReport getPreviousResult() {
		JacocoBuildAction prev = action.getPreviousResult();
		if(prev!=null)
			return prev.getResult();
		else
			return null;
	}

	@Override
	public AbstractBuild<?,?> getBuild() {
		return action.owner;
	}


	public void setThresholds(JacocoHealthReportThresholds healthReports) {
		this.healthReports = healthReports;
		/*if (healthReports.getMaxBranch() < branch.getPercentage()) {
			branchColor = "#000000";
		} else if (healthReports.getMinBranch() < branch.getPercentage()) {
			branchColor = "#FF8000";
		} else {
			branchColor = "#FF0000";
		}
		*/
	}


}
