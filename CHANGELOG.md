# Change Log

## 1.0.11 and later versions
- Changes are no longer tracked in this file; they are now tracked in [GitHub releases](https://github.com/jenkinsci/compuware-ispw-operations-plugin/releases) instead.

## 1.0.10

 🚀 Improvements
- Support for removal of JAXB and Java 11 requirement (JENKINS-68457).

## 1.0.9

 🚀 New features and improvements
- Support for built-in node names and labels as part of the Jenkins terminology cleanup work.
- Added the ability to pass a certificate to the Workbench CLI.
- Fixed security vulnerabilities.

## 1.0.8

 - Support webhook to produce intelligent test case change set for Build, Generate, Promote, Deploy and Regress
 - Fixed a bug affecting some Bitbucket repository URLs not being saved correctly during a Git to Code Pipeline synchronization
 - Assure CES connection not null while performing any Code Pipeline actions
 - Added support to automatically generate and deploy components synchronized through the Git to Code Pipeline integration
 
##  1.0.7

 - Support intelligent test case execution
 - Enforce valid level check for Git/Code Pipeline sync 

## 1.0.6

-   Add support for flexible YAML location for Code Pipeline/GIT integration
-   Make logs consistent between Jenkins operations and Workbench actions 
-   Auto clean up a task if the task is in failed status while loading source from GIT into Code Pipeline
-   Build process re-runnable for a failed Code Pipeline/git synchronization

## Version 1.0.5

-   Add support for building components. The build functionality generates impacted components of one or more selected tasks at the same level in the life cycle. Additionally, the build functionality can generate impacted components of tasks at a selected level within a selected assignment or release container.
-   Add support for synchronizing between GIT and Code Pipeline
-   Add support for CloudBees Folder plug-in
-   Add a 'skip polling' option for pipeline scripts

## Version 1.0.4

-   Add support for extra 10+ actions. See action list above.
-   Skip polling for the set completion if no webhook defined

## Version 1.0.3

-   Add support for retrieving the list of tasks for a given Code Pipeline Set.
-   Changes to the logging to be more consistent with other plugins
    logging.
-   Fix bad error message when selected host connection doesn't have a
    valid CES URL.
-   Fixed a bug in GetReleaseTaskList.
-   Pre-populate help text in request field.

## Version 1.0.2

-   The plugin now integrates with the [BMC AMI Common
    Configuration](https://plugins.jenkins.io/compuware-common-configuration) plugin
    which allows the Host Connection configurations to be defined
    centrally for other BMC AMI Jenkins plugins instead of needing to
    be specified in each Jenkins project's configuration.  Host
    Connection configuration is now defined in the Jenkins/Manage
    Jenkins/Configure System screen. 
-   Jenkins console logs produced by the plugin have been streamlined to
    improve readability.
-   Support for the Jenkins Pipeline Syntax.
-   Support Credentials secret text to store CES token.
-   Support most Code Pipeline build operations.
-   Provide Docker script to build Docker image for CES server and
    Docker image for Jenkins with BMC AMI plugins pre-installed.