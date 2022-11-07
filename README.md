jacoco-plugin
=============

[![Build Status](https://ci.jenkins.io/buildStatus/icon?job=Plugins/jacoco-plugin/master)](https://ci.jenkins.io/blue/organizations/jenkins/Plugins%2Fjacoco-plugin/activity) 
[![Build Status](https://travis-ci.org/jenkinsci/jacoco-plugin.svg?branch=master)](https://travis-ci.org/jenkinsci/jacoco-plugin)
[![Release](https://img.shields.io/github/release/jenkinsci/jacoco-plugin.svg)](https://github.com/jenkinsci/jacoco-plugin/releases)
<!--[![Join the chat at https://gitter.im/jenkinsci/jacoco-plugin](https://badges.gitter.im/jenkinsci/jacoco-plugin.svg)](https://gitter.im/jenkinsci/jacoco-plugin?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)-->
[![Jenkins Plugin](https://img.shields.io/jenkins/plugin/v/jacoco.svg)](https://plugins.jenkins.io/jacoco)
[![GitHub release](https://img.shields.io/github/release/jenkinsci/jacoco-plugin.svg?label=changelog)](https://github.com/jenkinsci/jacoco-plugin/releases/latest)
[![Jenkins Plugin Installs](https://img.shields.io/jenkins/plugin/i/jacoco.svg?color=blue)](https://plugins.jenkins.io/jacoco)


A plugin for Jenkins to capture and visualize code coverage testing results for projects using the 
JaCoCo for code-coverage analysis.

More information can be found on the Wiki page https://wiki.jenkins-ci.org/display/JENKINS/JaCoCo+Plugin

Note: Version 2.0.0 and higher requires using JaCoCo 0.7.5 or newer, if your projects still use JaCoCo 0.7.4, 
the plugin will not display any code-coverage numbers any more! In this case please use version 1.0.19 until you can update jacoco in your codebase.

Looking for ways to contribute?
===============================

When looking for things to work at there are [pull requests](https://github.com/jenkinsci/jacoco-plugin/pulls) and a 
list of [Jenkins Issues](https://issues.jenkins-ci.org/issues/?jql=project%20%3D%20JENKINS%20AND%20status%20not%20in%20(Closed%2C%20Resolved%2C%20%22Fixed%20but%20Unreleased%22)%20AND%20component%20%3D%20jacoco-plugin%20ORDER%20BY%20key%20DESC).

In general the plugin is currently in "minimal maintenance" mode, i.e. no 
larger development is done due to the small number of active contributors and general time constraints. 

Please speak up if you are interested in helping to maintain this plugin!

Discussion list
===============

There is a developer list at https://groups.google.com/forum/#!forum/jenkins-jacoco-plugin-mailing-list, 
it is usually very low volume.

Contributing to the Plugin
==========================

Plugin source code is hosted on [GitHub](https://github.com/jenkinsci/jacoco-plugin).

New feature proposals and bug fix proposals should be submitted as
[GitHub pull requests](https://help.github.com/articles/creating-a-pull-request).

Fork the repository on GitHub, prepare your change on your forked
copy, and submit a pull request (see [here](https://github.com/jenkinsci/jacoco-plugin/pulls) for open pull requests).
Your pull request will be evaluated by [this job](https://ci.jenkins.io/job/Plugins/job/jacoco-plugin/).

Before submitting your change, please assure that you've added a test
which verifies your change.  There have been many developers involved
in the jacoco plugin and there are many, many users who depend on the
jacoco-plugin. Tests help us assure that we're delivering a reliable
plugin, and that we've communicated our intent to other developers in
a way that they can detect when they run tests.

Code coverage reporting is available as a maven target and is actively
monitored. Please try your best to improve code coverage with tests
when you submit.

Before submitting your change, please review the output of the checks 
that run in CI at https://ci.jenkins.io/job/Plugins/job/jacoco-plugin/ 
to assure that you haven't introduced new warnings.

How to build and test
=====================

* Build the plugin:

`mvn package`

* Test locally (invokes a local Jenkins instance with the plugin installed):

`mvn hpi:run`

See https://jenkinsci.github.io/maven-hpi-plugin/ for details.

### How to release a new version

Rolling a release requires you to set up a few additional things:

* Run with Java 8 to not push code compiled with a newer version of Java
* Github authentication should work via SSH, username used should be "git",
it should use one of the local private SSH keys which should be uploaded to 
Github, see https://github.com/settings/keys, test via
 
  `ssh -T git@github.com`

You should get back a line containing `You've successfully authenticated`
 
* Jenkins-CI authentication should work via settings in 
`~/.m2/settings.xml`, see http://maven.apache.org/guides/mini/guide-encryption.html 
for details
* The mvn-calls below should not require "username" or "password", if they
do ask for it, then some setup is incorrect
* Check that all tests pass ("mvn findbugs:check" and "mvn checkstyle::check" report 
violations but are not blocking releases for now...)

  `mvn clean package && mvn validate`

* Manually test the plugin 

  `mvn hpi:run`

  Go to http://localhost:8080/jenkins/ and perform some testing

* Prepare the release 
  
  `mvn release:prepare -DskipTests`

  This will ask for the release numbers and the tag in the source control system.

* Roll the release 
  
  `mvn release:perform -DskipTests -Darguments="-DskipTests"`

  This should perform the actual uploads of the resulting binary packages.

* Update release notes at https://github.com/jenkinsci/jacoco-plugin/releases

* Release should be visible immediately at 
https://repo.jenkins-ci.org/releases/org/jenkins-ci/plugins/jacoco/
* Release is published after some time at 
https://mvnrepository.com/artifact/org.jenkins-ci.plugins/jacoco?repo=jenkins-releases
 
See also general documentation about releasing Jenkins plugins:
* Current documentation: https://www.jenkins.io/doc/developer/publishing/requesting-hosting/
* Maven Release Plugin: http://maven.apache.org/maven-release/maven-release-plugin/usage.html
* The following are slightly outdated, but still contain some useful information
  - https://wiki.jenkins.io/display/JENKINS/Hosting+Plugins#HostingPlugins-Requestuploadpermissions
  - https://wiki.jenkins.io/display/JENKINS/Hosting+Plugins#HostingPlugins-Releasingtojenkins-ci.org

## Basic information

-   Repository address: <https://github.com/jenkinsci/jacoco-plugin/>
-   Mailing list:
    <http://groups.google.com/group/jenkins-jacoco-plugin-mailing-list>
-   Issue
    tracking: [https://issues.jenkins-ci.org/browse/JENKINS/](https://issues.jenkins-ci.org/secure/IssueNavigator.jspa?reset=true&jqlQuery=project+%3D+JENKINS+AND+status+in+%28Open%2C+%22In+Progress%22%2C+Reopened%29+AND+%28component+%3D+jacoco-plugin%29&tempMax=1000)
-   Build and test
    results: <https://jenkins.ci.cloudbees.com/job/plugins/job/jacoco-plugin/>

This plugin allows you to capture code coverage report from JaCoCo.
Jenkins will generate the trend report of coverage and some other
statistics.

It also includes functionality to include columns in Dashboards which
displays the latest overall coverage numbers and links to the coverage
report.

## Getting Started

The plugin provides two things, a build-publisher to record and display
coverage data as part of builds as well as a new column-type for dashboard views which can display coverage data in
Dashboards.

### Recording coverage for builds

##### Get coverage data as part of your build

First you need to get coverage calculated as part of your build/tests,
see the [JaCoCo
documentation](http://www.eclemma.org/jacoco/trunk/doc/)  
for details. You need at least one or more \*.exec file available after
tests are executed. Usually this means adjusting  
your Maven pom.xml or Ant build.xml file..

##### Set up coverage retrieval and publishing

In order to get the coverage data published to Jenkins, you need to add
a JaCoCo publisher and configure it so it will  
find all the necessary information. Use the help provided via the
question-mark links for more information. Basically  
you specify where the \*.exec files are, where compiled code can be
found and where the corresponding source code is  
located after the build is finished to let the plugin gather all
necessary pieces of information..

![](https://wiki.jenkins.io/download/attachments/60918960/screenshot_039.png?version=1&modificationDate=1383160909000&api=v2)

##### Run the job

After the job executed, the build-output will show that the
JaCoCo-publisher is executed and collects the data. This  
output can also give hints if something goes wrong at this stage:

``` syntaxhighlighter-pre
Zeichne Testergebnisse auf.
[JaCoCo plugin] Collecting JaCoCo coverage data...
[JaCoCo plugin] build/*.exec;build/*-classes;src/java,src/*/java,src/*/src; locations are configured
[JaCoCo plugin] Number of found exec files: 5
[JaCoCo plugin] Saving matched execfiles:  .../build/jacoco-excelant.exec .../build/jacoco-main.exec .../build/jacoco-ooxml-lite.exec
[JaCoCo plugin] Saving matched class directories:  .../build/examples-classes .../build/excelant-classes .../build/excelant-test-classes
[JaCoCo plugin] Saving matched source directories:  .../src/contrib/src .../src/examples/src .../src/excelant/java .../src/java
[JaCoCo plugin] Loading inclusions files..
[JaCoCo plugin] inclusions: []
[JaCoCo plugin] exclusions: [**/Test*]
[JaCoCo plugin] Thresholds: JacocoHealthReportThresholds [minClass=0, maxClass=0, minMethod=0, maxMethod=0, minLine=0, maxLine=0,
minBranch=0, maxBranch=0, minInstruction=0, maxInstruction=0, minComplexity=0, maxComplexity=0]
[JaCoCo plugin] Publishing the results..
[JaCoCo plugin] Loading packages..
[JaCoCo plugin] Done.
```

##### Look at results

If data gathering is successful, the build will include a link to the
coverage results similar to the HTML report of  
JaCoCo itself. The job page will be enhanced with a chart with the trend
of code coverage over the last builds.

![](https://wiki.jenkins.io/download/attachments/60918960/screenshot_041.png?version=1&modificationDate=1383161321000&api=v2)

### Coverage column

This part of the JaCoCo plugin allows you to add a new type of column to
a project-table in the Dashboard view which  
will show the coverage number of the last build for ajob together with
some color coding which allows to quickly see  
projects with low coverage.

![](https://wiki.jenkins.io/download/attachments/60918960/screenshot_042.png?version=1&modificationDate=1383161521000&api=v2)

![](https://wiki.jenkins.io/download/attachments/60918960/screenshot_043.png?version=1&modificationDate=1383162838000&api=v2)

The fill-color in use by the column is a continuous color-range with the
following points:

-   PERFECT = 100.0
-   EXCELLENT at 97.0
-   GOOD at 92.0
-   SUFFICIENT at 85.0
-   FAIR at 75.0
-   POOR at 50.0
-   TRAGIC at 25.0
-   ABYSSMAL at 0.0
-   NA = No coverage configured

## Open Tickets (bugs and feature requests)

See the 
[Jenkins JIRA](https://issues.jenkins-ci.org/browse/JENKINS-42420?jql=project%20%3D%20JENKINS%20AND%20status%20in%20(Open%2C%20%22In%20Progress%22%2C%20Reopened%2C%20%22In%20Review%22)%20AND%20component%20%3D%20jacoco-plugin)

## Build Status

[![](https://jenkins.ci.cloudbees.com/buildStatus/icon?job=plugins/jacoco-plugin)](https://jenkins.ci.cloudbees.com/job/plugins/job/jacoco-plugin/)  
[![](https://travis-ci.org/jenkinsci/jacoco-plugin.svg?branch=master)](https://travis-ci.org/jenkinsci/jacoco-plugin)

## Troubleshooting

-   Unfortunately JaCoCo 0.7.5 breaks compatibility to previous binary
    formats of the jacoco.exec files. The JaCoCo plugin up to version
    1.0.19 is based on JaCoCo 0.7.4, thus you cannot use this version
    with projects which already use JaCoCo 0.7.5 or newer. JaCoCo plugin
    starting with version 2.0.0 uses JaCoCo 0.7.5 and thus requires also
    this version to be used in your projects. Please stick to JaCoCo
    plugin 1.0.19 or lower if you still use JaCoCo 0.7.4 or lower

## Change Log

#### Newer versions

For newer newer versions see the changelog in the release-area at https://github.com/jenkinsci/jacoco-plugin/releases

#### Version 3.3.0 (2021-06-17)

- Bump dashboard-view from 2.13 to 2.17 (#153)
- [JENKINS-65757](https://issues.jenkins-ci.org/browse/JENKINS-65757): Remove some unused dependencies to avoid jar-hell in Jenkins itself (#151)
- Stop using deprecated Util#join (#150) 

#### Version 3.2.0 (2021-05-13)

- Update to JaCoCo 0.8.7, this should add support for Java 16 (and preliminary support for Java 17) and Kotlin 1.5 (#146)
- Require at least Jenkins LTS 2.263.4 (#148)
- Update to Maven 3.8.1 (#148), Ant 1.10.9, httpclient 4.5.13 (#143), commons-lang3 to 3.12.0 (#142) 
- Add pipeline help (#141) 

#### Version 3.1.1 (Jan 23, 2021)

- Updated the overall coverage reporting also as a float percentage

#### Version 3.1.0 (Nov 13, 2020)

- Update to JaCoco 0.8.6 to add support for JDK 15
- Require minimum Jenkins version 2.164.3
- Update some other third-party dependencies

#### Version 3.0.8 (Sep 17, 2020)

- Add LICENSE file
- JENKINS-58184 - Do not check coverage increase against the configured threshold
- Bump Ant to 1.9.15

#### Version 3.0.7 (Jun 22, 2020)

- Do not fail if tests are run without argLine
- Update dependencies

#### Version 3.0.6 (May 29, 2020)

- Add wiki content to main README
- Adjust URLs in pom.xml
- Ignore one test which is depending on the exact version of Java

#### Version 3.0.5 (January 25, 2020)

- Initial support for JDK 11
- [JENKINS-49351](https://issues.jenkins-ci.org/browse/JENKINS-49351): Added runAlways user paramater to allow Jacoco to run even if build FAILED or was ABORTED
- [JENKINS-56918](https://issues.jenkins-ci.org/browse/JENKINS-56918): Adjust default source-includes to include *.groovy, *.kt and *.kts (Kotlin)
- [JENKINS-55166](https://issues.jenkins-ci.org/browse/JENKINS-55166): Replace non-ASCII characters in properties for German and Spanish to not depend on the encoding of file

#### Version 3.0.4 (November 4, 2018)

-   PR [\#100](https://github.com/jenkinsci/jacoco-plugin/pull/100)/PR
    [\#102](https://github.com/jenkinsci/jacoco-plugin/pull/102)/[JENKINS-49823](https://issues.jenkins-ci.org/browse/JENKINS-49823) -
    Ignore non existing class directory
-   PR [\#101](https://github.com/jenkinsci/jacoco-plugin/pull/101) -
    Add Branch Coverage Column

#### Version 3.0.3 (August 31, 2018)

-   `Update JaCoCo to 0.8.2 to support Java 10 (``#99)`
-   `Update some other plugins`
-   ` JENKINS-43310 add source file inclusions and exclusions (#85 /`
    [JENKINS-43310](https://issues.jenkins-ci.org/browse/JENKINS-43310))

#### Version 3.0.2 (February 18, 2018)

-   ` No change upload due to problems with the release-workflow`

#### Version 3.0.1 (February 18, 2018)

-   ` No change upload due to problems with the release-workflow`

#### Version 3.0 (February 18, 2018)

-   `Use the formatted date instead of the Calendar as the key to get the `
    `correct coverage trend per day `(
    [\#88](https://github.com/jenkinsci/jacoco-plugin/pull/88))

-   ` Handle build abort (by not catching InterruptedException) (#86) `

-   ` Improve exception handling (#89) `

-   ` Update min Java version to Java 8, update Maven dependencies and plugins (#92) `

-   ` Fix Javadoc warnings (#93) `

#### Version 2.2.1 (May 5, 2017)

  

-   [JENKINS-43103](https://issues.jenkins-ci.org/browse/JENKINS-43103)
    Removed dependency on Joda Time 
-   [\#83](https://github.com/jenkinsci/jacoco-plugin/pull/83) Added
    symbol name for better pipeline integration
-   [JENKINS-41515](https://issues.jenkins-ci.org/browse/JENKINS-41515) 
     Update Jenkins parent to 1.625.3 to be able to test with current
    pipeline-plugin
-   [JENKINS-41515](https://issues.jenkins-ci.org/browse/JENKINS-41515) Adjust
    dependencies and add some exclusions to remove a number of
    unnecessary jars from the resulting hpi file

#### Version 2.2.0 (Mar 22, 2017)

-   Configuration: Put checkboxes on top of threshold values in the
    configuration UI
-   [\#81](https://github.com/jenkinsci/jacoco-plugin/pull/81) New flag
    to enable/disable display of source files with line coverage
    highlights
-   [\#80](https://github.com/jenkinsci/jacoco-plugin/pull/80) Allow to
    fail the build if coverage degrades to much compared to previous
    build
-   [\#79](https://github.com/jenkinsci/jacoco-plugin/pull/79)/[JENKINS-25076](https://issues.jenkins-ci.org/browse/JENKINS-25076)/[JENKINS-29117](https://issues.jenkins-ci.org/browse/JENKINS-29117)
    Report 100% for empty Coverage objects
-   [\#78](https://github.com/jenkinsci/jacoco-plugin/pull/78)/[JENKINS-26254](https://issues.jenkins-ci.org/browse/JENKINS-26254)
    Use BufferedInputStream to read .exec files
-   [\#76](https://github.com/jenkinsci/jacoco-plugin/pull/76) Allow
    usage of environment variables in threshold fields.
-   [JENKINS-38604](https://issues.jenkins-ci.org/browse/JENKINS-38604):
    Limit copying of files to types \*.java and \*.class
-   [JENKINS-36571](https://issues.jenkins-ci.org/browse/JENKINS-36571):
    Update to Java 1.7
-   [JENKINS-36536](https://issues.jenkins-ci.org/browse/JENKINS-36536):
    Put a trend graph on the pipeline project page
-   [JENKINS-32847](https://issues.jenkins-ci.org/browse/JENKINS-32847):
    Add warning for sub directory filter
-   [JENKINS-16787](https://issues.jenkins-ci.org/browse/JENKINS-16787):
    Add shape and center plot for single-build-graphs

#### Version 2.1.0 (Sep 29, 2016)

-   [JENKINS-16580](https://issues.jenkins-ci.org/browse/JENKINS-16580)
    Display more than only line coverage in graph,
    [\#50](https://github.com/jenkinsci/jacoco-plugin/pull/50)
-   [JENKINS-27120](https://issues.jenkins-ci.org/browse/JENKINS-27120)
    Adding Workflow support for JaCoCo publisher, make JacocoPublisher a
    SimpleBuildStep,
    [\#63](https://github.com/jenkinsci/jacoco-plugin/pull/63),
    [\#66](https://github.com/jenkinsci/jacoco-plugin/pull/66),
    [\#70](https://github.com/jenkinsci/jacoco-plugin/pull/70),
    [\#72](https://github.com/jenkinsci/jacoco-plugin/pull/72),
    [\#73](https://github.com/jenkinsci/jacoco-plugin/pull/73)
-   Handle Jenkins publisher case when classes directory is empty,
    [\#67](https://github.com/jenkinsci/jacoco-plugin/pull/67)
-   [JENKINS-32717](https://issues.jenkins-ci.org/browse/JENKINS-32717)
    Multiple class directories and source directories not parsed if
    there is a space after comma delimiter,
    [\#68](https://github.com/jenkinsci/jacoco-plugin/pull/68)
-   Use full precision when comparing coverage to thresholds,
    [\#71](https://github.com/jenkinsci/jacoco-plugin/pull/71)
-   Expand the inclusion and exclusion inputs,
    [\#74](https://github.com/jenkinsci/jacoco-plugin/pull/74)

#### Version 2.0.1 (Jan 15, 2016)

-   Fix the m2e lifecycle-mapping,
    [\#64](https://github.com/jenkinsci/jacoco-plugin/pull/64)
-   Integrate automated builds via travis-ci and show the build-state on
    the github page
-   Fix for
    [JENKINS-31751](https://issues.jenkins-ci.org/browse/JENKINS-31751)
    JaCoCo 2.0.0 plugin shows html instead of coverage report chart

#### Version 2.0.0 (Nov 23, 2015)

-   Major version change because the jacoco.exec file from the newer
    JaCoCo is binary incompatible with previous builds
-   Update to JaCoCo 0.7.5, this causes binary incompatibility! See
    [\#55](https://github.com/jenkinsci/jacoco-plugin/pull/55)
-   Add coverage summary on build status/result page. Thanks to Felipe
    Brandão for the patch, see
    [\#61](https://github.com/jenkinsci/jacoco-plugin/pull/61)

  

  Older changelog

#### Version 1.0.19 (Apr 7, 2015)

-   Update used version of JaCoCo to 0.7.4

#### Version 1.0.18 (Dec 7, 2014)

-   Fix
    [JENKINS-23708](https://issues.jenkins-ci.org/browse/JENKINS-23708)
    NullPointerException if older JaCoCo reports are opened

#### Version 1.0.17 (Nov 25, 2014)

-   Fix [\[JENKINS-24450\] JacocoPublisher serializes concurrent builds
    waiting for
    checkpoint](https://issues.jenkins-ci.org/browse/JENKINS-24450) -
    [\#45 - JacocoPublisher serializes concurrent builds waiting for
    checkpoint](https://github.com/jenkinsci/jacoco-plugin/pull/45)
-   Fix [\[JENKINS-21529\] add raw number metrics to the
    API](https://issues.jenkins-ci.org/browse/JENKINS-21529) - [\#42 -
    add raw number metrics to the
    API](https://github.com/jenkinsci/jacoco-plugin/pull/42)
-   Fix [\#35 - Fix minor glitches in coverage
    table](https://github.com/jenkinsci/jacoco-plugin/pull/35)
-   Fix [\[JENKINS-23623\] Fix URL in JaCoCo Coverage Column in
    non-default
    views](https://issues.jenkins-ci.org/browse/JENKINS-23623)
-   Set License to MIT License

#### Version 1.0.16 (Jun 24, 2014)

-   Fix [\[JENKINS-23426\] - Crash publishing jacoco report across all
    projects](https://issues.jenkins-ci.org/browse/JENKINS-23426) -
    [\#44 - set dependency of asm to
    5.0.1](https://github.com/jenkinsci/jacoco-plugin/pull/44)

#### Version 1.0.15 (Jun 11, 2014)

-   Fix [\[JENKINS-20440\] Inspector
    problem](https://issues.jenkins-ci.org/browse/JENKINS-20440)
-   Fix [\[JENKINS-22716\] - Update to JaCoCo 0.7.x to support Java
    8](https://issues.jenkins-ci.org/browse/JENKINS-22716)
-   Fix [\#40 - Project Dashboard chart having data cut
    off](https://github.com/jenkinsci/jacoco-plugin/issues/40)

#### Version 1.0.14 (Nov 9, 2013)

Core JaCoCo plugin

-   Fix [\[JENKINS-19526\] Display Branch Coverage
    Information](https://issues.jenkins-ci.org/browse/JENKINS-19526)
-   Fix [\[JENKINS-19539\] Do not (try to) generate coverage information
    if build was
    aborted](https://issues.jenkins-ci.org/browse/JENKINS-19539)
-   Fix [\[JENKINS-17027\] Red/green ratio is
    wrong](https://issues.jenkins-ci.org/browse/JENKINS-17027)
-   Fix [\[JENKINS-19661\] Inconsistent complexity score on the
    dashboard
    portlet](https://issues.jenkins-ci.org/browse/JENKINS-19661)
-   Fix [\[JENKINS-19662\] Removed (always-empty) block coverage column
    from the dashboard
    portlet](https://issues.jenkins-ci.org/browse/JENKINS-19662)
-   Fix [\[JENKINS-19789\] Method parameters in the class summary
    table](https://issues.jenkins-ci.org/browse/JENKINS-19789)

#### Version 1.0.13 (Aug 22, 2013)

Core JaCoCo plugin

-   Fix [JENKINS-16948: Support for changing build status if expected
    coverage is not
    reached](https://wiki.jenkins.io/display/JENKINS/JaCoCo+Plugin)
-   Fix [JENKINS-15539: /jacoco.exec now serves the (possibly combined)
    jacoco.exec
    file](https://wiki.jenkins.io/display/JENKINS/JaCoCo+Plugin)
-   Fix [JENKINS-15571: plugin crashes if jacoco didn't
    run](https://wiki.jenkins.io/display/JENKINS/JaCoCo+Plugin)
-   Stream output to avoid holding large amounts of data in memory

Coverage column:

-   Fix [JENKINS-18894: Do not include coverage column in new Dashboards
    by default](https://wiki.jenkins.io/display/JENKINS/JaCoCo+Plugin)
-   Fix [JENKINS-16790: Link from Dashboard to coverage
    report](https://wiki.jenkins.io/display/JENKINS/JaCoCo+Plugin)
-   Fix [JENKINS-16786: make coverage column show N/A as white, not
    black](https://wiki.jenkins.io/display/JENKINS/JaCoCo+Plugin)
-   Fix [JENKINS-16788: Update German
    translation](https://wiki.jenkins.io/display/JENKINS/JaCoCo+Plugin)
-   Add some help-items for configuration items

#### Version 1.0.12 (Feb 22, 2013)

-   Fix [JENKINS-16935: The default no-name package breaks the
    rendering](https://wiki.jenkins.io/display/JENKINS/JaCoCo+Plugin)
-   Fix [JENKINS-15538: Show source highlighting on the whole
    class](https://wiki.jenkins.io/display/JENKINS/JaCoCo+Plugin)

#### Version 1.0.11 (Feb 21, 2013)

-   Fix [JENKINS-16777: ArrayIndexOutOfBoundsException when trying to
    publish the Jacoco
    report](https://issues.jenkins-ci.org/browse/JENKINS-16777)
-   Fix
    [JENKINS-16837](https://issues.jenkins-ci.org/browse/JENKINS-16837)[:
    Don't change build
    status](https://issues.jenkins-ci.org/browse/JENKINS-16837)

#### Version 1.0.10 (Feb 05, 2013)

-   Fix [JENKINS-16096: Support for inclusions,
    exclusions](https://issues.jenkins-ci.org/browse/JENKINS-16096)
-   Fix [JENKINS-15011: Jacoco Plugin 1.0.3 - no threshold config and
    displays broken graphic
    link ](https://issues.jenkins-ci.org/browse/JENKINS-15011)
-   Fix [JENKINS-15366: Problem displaying Jacoco coverage data in
    Jenkins for very large number of classes and
    methods](https://issues.jenkins-ci.org/browse/JENKINS-15366).
-   Fix [JENKINS-15570: Coverage report includes classes that have been
    excluded from Jacoco
    analysis](https://issues.jenkins-ci.org/browse/JENKINS-15570)
-   Fix [JENKINS-15180: should be able to configure "coverage
    targets"](https://issues.jenkins-ci.org/browse/JENKINS-15180)
-   Fix [JENKINS-16310: Source code loses spacing/indenting due to html
    white space
    collapsing. ](https://issues.jenkins-ci.org/browse/JENKINS-16310)

#### Version 1.0.9 (Nov 18, 2012) 

-   Fix [JENKINS-15217: Wrong vertical scale in coverage report
    graph](https://issues.jenkins-ci.org/browse/JENKINS-15217)
-   Fix [JENKINS-15831: Switch line colors in
    graph](https://issues.jenkins-ci.org/browse/JENKINS-15831)
-   Fix [JENKINS-15366: Problem displaying Jacoco coverage data in
    Jenkins for very large number of classes and
    methods.](https://issues.jenkins-ci.org/browse/JENKINS-15366)
-   Fix [JENKINS-15177: main project coverage trend graph has wrong y
    axis ](https://issues.jenkins-ci.org/browse/JENKINS-15177)

#### Version 1.0.8 (Okt 15, 2012) 

-   Fix [JENKINS-15463: JaCoCo Jenkins plugin does not work on a linux
    slave](https://issues.jenkins-ci.org/browse/JENKINS-15463)
-   Fix [JENKINS-15479: Base dir not
    found](https://issues.jenkins-ci.org/browse/JENKINS-15479)
-   Fix [JENKINS-15182:improve formatting of coverage
    reports](https://issues.jenkins-ci.org/browse/JENKINS-15182)
-   Fix [JENKINS-14928: Sourcefile highlighting loses the
    indentation](https://issues.jenkins-ci.org/browse/JENKINS-14928)

#### Version 1.0.7 (Okt 8, 2012) 

-   Fix [JENKINS-15242: org.objectweb.asm.ClassReader.accept
    error](https://issues.jenkins-ci.org/browse/JENKINS-15242)

#### Version 1.0.6 (Okt 2, 2012) 

-   Refactoring: There are no separate modules anymore.
-   Fix
    [JENKINS-14939](https://issues.jenkins-ci.org/browse/JENKINS-14939):[Support
    single jacoco.exec + multiple modules for sources and classes (based
    on patterns)](https://issues.jenkins-ci.org/browse/JENKINS-14939)
-   Fix
    [JENKINS-15366:](https://issues.jenkins-ci.org/browse/JENKINS-15366)[Problem
    displaying Jacoco coverage data in Jenkins for very large number of
    classes and
    methods.](https://issues.jenkins-ci.org/browse/JENKINS-15366)

#### Version 1.0.5 (Sep 09, 2012) 

-   Fixed JENKINS-15088: When the missed count is bigger than the
    covered, the redbar becomes too long

#### Version 1.0.4 (Sep 03, 2012) 

-   Fixed JENKINS-14896: [java.io](http://java.io).FileNotFoundException
    when saving module data

#### Version 1.0.3 (Aug 20, 2012) 

-   **New concept implemented** JENKINS-14927: Use the EXEC files
    instead the XMLs
-   Fixed JENKINS-14279: Report "missed items" rather that coverage
    percentage
-   Fixed JENKINS-14159: 404 when trying to see coverage on java code
    (implementation of sourcefile highlighting)
-   New feature JENKINS-14274: Multi module reporting

#### Version 1.0.2 (Jul 01, 2012) 

-   Fixed JENKINS-14270: The report does not show the covered/all, it
    shows something different

#### Version 1.0.1 (Jun 25, 2012) 

-   Fixed testcases to have all the tests passed for a release
-   Fixed JENKINS-14203: The total summary is double counted

#### Version 1.0.0 (Jun 14, 2012) 

-   Fixed 3 testcases to have all the tests passed for a release
-   Fixed POM: making it releasabl
