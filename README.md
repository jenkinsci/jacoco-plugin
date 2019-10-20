jacoco-plugin
=============

[![Build Status](https://ci.jenkins.io/buildStatus/icon?job=Plugins/jacoco-plugin/master)](https://ci.jenkins.io/blue/organizations/jenkins/Plugins%2Fjacoco-plugin/activity) 
[![Build Status](https://travis-ci.org/jenkinsci/jacoco-plugin.svg?branch=master)](https://travis-ci.org/jenkinsci/jacoco-plugin)
[![Release](https://img.shields.io/github/release/jenkinsci/jacoco-plugin.svg)](https://github.com/jenkinsci/jacoco-plugin/releases)

A plugin for Jenkins to capture and visualize code coverage testing results for projects using the 
JaCoCo for code-coverage analysis.

More information can be found on the Wiki page https://wiki.jenkins-ci.org/display/JENKINS/JaCoCo+Plugin

Note: Version 2.0.0 and higher requires using JaCoCo 0.7.5 or newer, if your projects still use JaCoCo 0.7.4, 
the plugin will not display any code-coverage numbers any more! In this case please use version 1.0.19 until you can update jacoco in your codebase.

Looking for ways to contribute?
===============================

When looking for things to work at there are [pull requests](https://github.com/jenkinsci/jacoco-plugin/pulls) and a 
list of [Jenkins Issues](https://issues.jenkins-ci.org/issues/?jql=project%20%3D%20JENKINS%20AND%20status%20not%20in%20(Closed%2C%20Resolved%2C%20%22Fixed%20but%20Unreleased%22)%20AND%20component%20%3D%20jacoco-plugin%20ORDER%20BY%20key%20DESC).

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

Before submitting your change, please review the findbugs output to
assure that you haven't introduced new findbugs warnings.

How to build and test
=====================

* Build the plugin:

`mvn package`

* Test locally (invokes a local Jenkins instance with the plugin installed):

`mvn hpi:run`

See https://jenkinsci.github.io/maven-hpi-plugin/ for details.
