jacoco-plugin
=============


[![Build Status](https://jenkins.ci.cloudbees.com/buildStatus/icon?job=plugins/jacoco-plugin)](https://jenkins.ci.cloudbees.com/job/plugins/job/jacoco-plugin/)
[![Build Status](https://travis-ci.org/jenkinsci/jacoco-plugin.svg?branch=master)](https://travis-ci.org/jenkinsci/jacoco-plugin)

Jenkins JaCoCo plugin.
More information can be found on the Wiki page https://wiki.jenkins-ci.org/display/JENKINS/JaCoCo+Plugin

Note: Version 2.0.0 and higher requires using JaCoCo 0.7.5 or newer, if your projects still use JaCoCo 0.7.4, 
the plugin will not display any code-coverage numbers any more! In this case please use version 1.0.19 until you can update jacoco in your codebase.

Looking for ways to contribute?
===============================

When looking for things to work at there are [pull requests](https://github.com/jenkinsci/jacoco-plugin/pulls) and a 
list of [Jenkins Issues](https://issues.jenkins-ci.org/secure/IssueNavigator.jspa?reset=true&jqlQuery=project+%3D+JENKINS+AND+status+in+%28Open%2C+%22In+Progress%22%2C+Reopened%29+AND+%28component+%3D+jacoco-plugin%29&tempMax=1000).

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
copy, and submit a pull request (see [here](https://github.com/jenkinsci/jacoco-plugin/pulls) for open pull requests). Your pull request will be evaluated
by the [Cloudbees Jenkins job](https://jenkins.ci.cloudbees.com/job/plugins/job/jacoco-plugin/)
and you should receive e-mail with the results of the evaluation.

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
