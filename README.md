jacoco-plugin
=============

[![Build Status](https://ci.jenkins.io/buildStatus/icon?job=Plugins/jacoco-plugin/master)](https://ci.jenkins.io/blue/organizations/jenkins/Plugins%2Fjacoco-plugin/activity) 
[![Build Status](https://travis-ci.org/jenkinsci/jacoco-plugin.svg?branch=master)](https://travis-ci.org/jenkinsci/jacoco-plugin)
[![Release](https://img.shields.io/github/release/jenkinsci/jacoco-plugin.svg)](https://github.com/jenkinsci/jacoco-plugin/releases)
<!--[![Join the chat at https://gitter.im/jenkinsci/jacoco-plugin](https://badges.gitter.im/jenkinsci/jacoco-plugin.svg)](https://gitter.im/jenkinsci/jacoco-plugin?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)-->
[![Jenkins Plugin](https://img.shields.io/jenkins/plugin/v/jacoco.svg)](https://plugins.jenkins.io/jacoco)
[![GitHub release](https://img.shields.io/github/release/jenkinsci/jacoco-plugin.svg?label=changelog)](https://github.com/jenkinsci/jacoco-plugin/releases/latest)
[![Jenkins Plugin Installs](https://img.shields.io/jenkins/plugin/i/jacoco.svg?color=blue)](https://plugins.jenkins.io/jacoco)


:warning::warning::rescue_worker_helmet:
This plugin is up for adoption, the current maintainers are either gone MIA or do not use the plugin any more, thus soemone who actively uses the plugin is needed to do better decisions on bug reports, pull requests and feature work.
:rescue_worker_helmet::warning::warning:


A plugin for Jenkins to capture and visualize code coverage testing results for projects using 
[JaCoCo](https://www.eclemma.org/jacoco/) for code-coverage analysis.

More information can be found on the Wiki page https://wiki.jenkins-ci.org/display/JENKINS/JaCoCo+Plugin

Available options are described at https://www.jenkins.io/doc/pipeline/steps/jacoco/

Note: Version 2.0.0 and higher requires using JaCoCo 0.7.5 or newer, if your projects still use JaCoCo 0.7.4, 
the plugin will not display any code-coverage numbers any more! In this case please use version 1.0.19 until you can update jacoco in your codebase.

Using as part of a pipeline via Jenkinsfile
===========================================

The following is an example snippet that can be used in a `Jenkinsfile`

```
    post {
        success {
            jacoco(
                execPattern: '**/build/jacoco/*.exec',
                classPattern: '**/build/classes/java/main',
                sourcePattern: '**/src/main'
            )
        }
    }
```

See https://www.jenkins.io/doc/pipeline/steps/jacoco/ for the list of available options.

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

* Run with Java 11 to not push code compiled with a newer version of Java
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
* Apache Maven 3.8.1 or newer is required
* Check that all tests pass ("mvn findbugs:check" and "mvn checkstyle::check" report 
violations but are not blocking releases for now...)

  `mvn clean package && mvn validate`

* Manually test the plugin 

  `mvn hpi:run`

  Go to http://localhost:8080/jenkins/ and perform some testing

* Prepare the release 
  
  `mvn release:prepare -DskipTests -Dmaven.test.skip=true`

  This will ask for the release numbers and the tag in the source control system.

* Roll the release 
  
  `mvn release:perform -DskipTests -Darguments="-DskipTests" -Dmaven.test.skip=true`

  This should perform the actual uploads of the resulting binary packages.

* Update release notes at https://github.com/jenkinsci/jacoco-plugin/tags

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

_For current versions see the changelog in the release-area at https://github.com/jenkinsci/jacoco-plugin/releases_

## Historic Change Log

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
