package hudson.plugins.jacoco.e2e;

import java.util.ArrayList;
import java.util.List;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.RealJenkinsRule;

import hudson.Functions;
import hudson.model.Descriptor;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.plugins.jacoco.JacocoBuildAction;
import hudson.plugins.jacoco.JacocoPublisher;
import hudson.plugins.jacoco.model.Coverage;
import hudson.tasks.BatchFile;
import hudson.tasks.Builder;
import hudson.tasks.Publisher;
import hudson.tasks.Shell;
import hudson.util.DescribableList;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

import static hudson.plugins.jacoco.e2e.E2ETest.CoverageMatcher.withCoverage;

public class E2ETest {

    @Rule
    public RealJenkinsRule rjr = new RealJenkinsRule();

    @Test
    public void simpleTest() throws Throwable {
        rjr.then(r -> {
            FreeStyleProject project = r.createFreeStyleProject();
            project.getBuildersList().addAll(createJacocoProjectBuilders());
            project.getPublishersList().add(new JacocoPublisher());
            
            FreeStyleBuild build = r.buildAndAssertSuccess(project);

            assertThat("plugin collected data", build.getLog(), containsString("Collecting JaCoCo coverage data"));

            JacocoBuildAction action = build.getAction(JacocoBuildAction.class);
            assertThat("Build has the Jacoco Action", action, notNullValue());
            
            assertThat("incorrect branch coverage reported", action.getBranchCoverage(), withCoverage(0, 621, 621));
            assertThat("incorrect class coverage reported", action.getClassCoverage(), withCoverage(7, 59, 66));
            assertThat("incorrect complexity coverage reported", action.getComplexityScore(), withCoverage(19, 835, 854));
            // different compilers can generate different instructions (e.g. java8 vs java 11.
            // so just skip this for now as it seems brittle
            // assertThat("incorrect instruction coverage reported", action.getInstructionCoverage(), withCoverage(229, 9013, 9242)); /* java 8* /
            // assertThat("incorrect instruction coverage reported", action.getInstructionCoverage(), withCoverage(229, 9010 , 9239)); /* java 11 */
            assertThat("incorrect line coverage reported", action.getLineCoverage(), withCoverage(53, 1860, 1913));
        }
        );
    }

    private static List<Builder> createJacocoProjectBuilders() {
        String[] commands = { "git clone --branch jacoco-3.2.0 https://github.com/jenkinsci/jacoco-plugin.git .",
                "mvn -P enable-jacoco -B -Dtest=ClassReportTest test" };
        List<Builder> builders = new ArrayList<>();
        if (Functions.isWindows()) {
            for (String command : commands) {
                builders.add(new BatchFile(command));
            }
        } else {
            for (String command : commands) {
                builders.add(new Shell(command));
            }
        }
        return builders;
    }
    
    public static class CoverageMatcher extends TypeSafeDiagnosingMatcher<Coverage> {

        private final int covered;
        private final int missed;
        private final int total;

        private CoverageMatcher(int covered, int missed, int total) {
            this.covered = covered;
            this.missed = missed;
            this.total = total;
        }
        @Override
        public void describeTo(Description description) {
            description.appendText(" with covered="+ covered);
            description.appendText(" and missed="+ missed);
            description.appendText(" and total="+ total);
            
        }

        @Override
        protected boolean matchesSafely(Coverage coverage, Description mismatchDescription) {
            mismatchDescription.appendText("Coverage with covered="+ coverage.getCovered());
            mismatchDescription.appendText(" and missed="+ coverage.getMissed());
            mismatchDescription.appendText(" and total="+ coverage.getTotal());

            return coverage.getCovered() == covered && 
                    coverage.getMissed() == missed &&
                    coverage.getTotal() == total;
        }
    
        public static CoverageMatcher withCoverage(int covered, int missed, int total) {
            return new CoverageMatcher(covered, missed, total);
        }
    }
}
