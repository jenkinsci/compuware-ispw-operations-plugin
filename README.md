# Overview

The Compuware ISPW Operations plugin allows Jenkins users to execute
ISPW operations, such as Generate, Promote, Deploy or Regress on the
mainframe. Users can seamlessly integrate ISPW build process with
Jenkins. 

### Prerequisites

The following are required to use this plugin:

-   Jenkins
-   Jenkins Credentials plugin
-   Jenkins Plain Credentials plugin
-   Compuware Common Configuration plugin - latest
-   Compuware mainframe ISPW
-   Compuware CES

### Additional Information for the Git to ISPW Sync Functionality
-   Refer to the following [guide](https://devops.api.compuware.com/guidelines/ispw/GIT_to_ISPW_Integration_Tutorial.html) for more information
-   Required: minimum version of Topaz Workbench CLI version 20.01.01 [installed](https://devops.api.compuware.com/tool_configuration/plugins.html#installing-the-topaz-workbench-cli)

### Installing in a Jenkins Instance

-   Install the Compuware ISPW Operations plugin according to the
    Jenkins instructions for installing plugins. Dependent plugins will
    automatically be installed.

  

### Configuring Host Connections and CES Tokens

-   If no host connection appears in the **Host Connections** section,
    click **Add Host Connection **and add a host connection by entering
    the following information:

    -   In the **Description **field, enter a description of the
        connection.

    -   In the **Host:port **field, enter the z/OS host to connect to.

    -   From the **Code page** list, select the desired code page to be
        used for this connection. The code page is used to translate
        data sent to and from the host. The default is 1047.

    -   In the **Read/write timeout (minutes)** field, enter the number
        of minutes for the plugin to wait for a response from the host
        before timing out.

    -   In the **CES URL** field, enter the CES server URL. The default
        is empty. It is not required for other Compuware plugins but is
        required to use this plugin. Please do NOT attach any context
        path, it should be in the format:
        [http://host:port](http://hostport/).

    **Note:** Click **Delete Host Connection** to delete an existing
    connection.

-   If you intend to use web hook callback
    -   Jenkins URL in section Jenkins Location must be defined with the
        Jenkins server IP address (not localhost or 127.0.0.1)
    -   If you do NOT want the CES callback to provide Jenkins crumb,
        you need to go to Manage Jenkins \| Configure Global Security
        page, then turn off 'Prevent Cross Request Forgery exploits' in
        CSRF Protection section. Otherwise, you have to acquire the
        Jenkins crumb from CES server host and provide the Jenkins crumb
        as property **events.httpHeaders=Jenkins-Crumb:{Jenkins
        crumb}** in the **Request** body in the job. To acquire a
        Jenkins crumb, please reference
        - <https://wiki.jenkins.io/display/JENKINS/Remote+access+API>
-   If no CES token appears in the CES secret token section, 
    -   Click Credentials \| System \| Global credentials \| Add
        Credentials, select Kind to 'Secret text'
    -   Provide the CES token as Secret
    -   Leave ID as blank (it will generated by Jenkins)
    -   Give a a meaningful description, then Add

  

### Executing ISPW Operations

-   On the **Configuration** page of the job or project,
    select **Execute a Compuware ISPW Operation** from
    the **Build** section.

-   From the **Host connection** list, select the host connection to be
    used to connect to CES host. Alternatively, to add a connection,
    click **Manage connections**. The **Host connections** section of
    the Jenkins configuration page appears so a connection can be added.

-   In the **CES secret token** list, select the CES token configured in
    the CES host for the ISPW. Alternatively, click **Add** to add
    secret text as token using the Plain Credentials plugin. Refer to
    the Jenkins documentation for the Plain Credentials plugin.
-   In the **Action** section to define what ISPW operation to perform
    -   AddTask
    -   BuildAssignment
    -   BuildRelease
    -   BuildTask
    -   CancelAssignment
    -   CancelDeployment
    -   CancelRelease
    -   CloseAssignment
    -   CloseRelease
    -   CreateAssignment
    -   CreateRelease
    -   DeployAssignment
    -   DeployRelease
    -   FallbackAssignment
    -   FallbackRelease
    -   GenerateTasksInAssignment
    -   GenerateTasksInRelease
    -   GetAssignmentInfo
    -   GetAssignmentTaskList
    -   GetContainerList
    -   GetReleaseInfo
    -   GetReleaseTaskGenerateListing
    -   GetReleaseTaskInfo
    -   GetReleaseTaskList
    -   GetSetInfo
    -   GetSetTaskList
    -   GetWorkList
    -   PromoteAssignment
    -   PromoteRelease
    -   RegressAssignment
    -   RegressRelease
    -   RemoveFromRelease
    -   SetOperation
    -   TaskLoad
    -   TransferTask
-   In the **Request** section, please specify additional request
    parameters, click the question mark for more details. Each of the
    action may have different set of properties, if the job support web
    hook callback, additional event related properties must be provided.
    The web hook callback only works for pipeline build.

### Using Pipeline Syntax to Generate Pipeline Script

1.  Do one of the following:

    -   When working with an existing Pipeline job, click the **Pipeline
        Syntax** link in the left panel. The **Snippet
        Generator** appears.

    -   When configuring a Pipeline job, click the **Pipeline
        Syntax** link at the bottom of the **Pipeline** configuration
        section. The **Snippet Generator** appears.

2.  From the **Sample Step** list, select **ispwOperation: Perform a
    Compuware ISPW Rest API request and return a JSON object**.

3.  From the **Host connection** list, select the host connection that
    contains a valid CES URL.

4.  From the **CES secret token**, select the corresponding CES token
    for the CES server.

5.  From the **Action**, select the ISPW operation to be performed.
6.  From the **Request** body, enter the corresponding properties for
    the specific action, click question mark help for more detail for
    each action
7.  Click **Generate Pipeline Script**. The Groovy script to invoke the
    Compuware ISPW Operations plugin appears. The script can be added to
    the Pipeline section when configuring a Pipeline job. A sample
    script is shown below:

```
ispwOperation connectionId: 'e0fbb6eb-b01d-4d55-b18b-2f321c174474', credentialsId: 'f1d2762b-9a40-46ad-a9df-b982147acc85', ispwAction: 'GenerateTasksInAssignment', ispwRequestBody: '''assignmentId=PLAY000313
level=DEV2
runtimeConfiguration=TPZP'''
```

For web hook callback, a sample script is shown below

```
hook = ispwRegisterWebhook()
echo "...creating ISPW Jenkins web hook - ${hook.getURL()}"

ispwOperation connectionId: 'e0fbb6eb-b01d-4d55-b18b-2f321c174474', credentialsId: 'f1d2762b-9a40-46ad-a9df-b982147acc85', ispwAction: 'GenerateTasksInAssignment', ispwRequestBody: '''assignmentId=PLAY000313
level=DEV2
runtimeConfiguration=TPZP
events.name=Completed
events.body=Generated
events.httpHeaders=Jenkins-Crumb:no-crumb
events.credentials=admin:library'''

echo "...waiting ISPW Jenkins web hook callback - ${hook.getURL()}"

data = ispwWaitForWebhook hook
echo "...CES called back with message: ${data}"
```

**Note:** If the **Response body in** **console **option is checked, then debug
message will be printed within the Jenkins log. 

&nbsp;

### Pipeline Build Requirement

In order to use pipeline build, your ISPW, CMSC and CES have to be
configured properly in order to receive web hook notification. See
explanation in the following figure.

![](https://wiki.jenkins.io/download/attachments/138446036/ispwPipelineOpRequirement.png?version=1&modificationDate=1515162494000&api=v2)

&nbsp;


## GIT to ISPW Integration Features

![GIT to ISPW design](https://raw.githubusercontent.com/jenkinsci/compuware-ispw-operations-plugin/CWE-150569-ISPW-Git-Integration---Pipeline-build/ispw%20git%20integration.png)

### Pipeline build example

```
pipeline {

    agent any

    triggers {
        GenericTrigger(
            genericVariables: [
                [key: 'ref', value: '$.changes[0].ref.displayId', expressionType: 'JSONPath', regexpFilter: '^(refs/heads/\\|refs/remotes/origin/)'],
                [key: 'toHash', value: '$.changes[0].toHash', expressionType: 'JSONPath', regexpFilter: '^(refs/heads/\\|refs/remotes/origin/)'],
                [key: 'fromHash', value: '$.changes[0].fromHash', expressionType: 'JSONPath', regexpFilter: '^(refs/heads/\\|refs/remotes/origin/)'],
                [key: 'refId', value: '$.changes[0].ref.id', expressionType: 'JSONPath', regexpFilter: '^(refs/heads/\\|refs/remotes/origin/)'],
            ],
     
            causeString: 'Triggered on $ref',
            token: 'mytokenPipeline',
            printContributedVariables: true,
            printPostContent: true,
            silentResponse: false
        )
    }

    stages {
        stage("git to ispw") {
            steps {
                gitToIspwIntegration app: 'PLAY', branchMapping: '''**/dev1 => DEV1, per-commit
                **/dev2 => DEV2, per-branch
                **/dev3 => DEV3, custom, description

                ''', connectionId: '94d914d9-ea8d-472c-90e4-4b5c007c64d4', credentialsId: '702482ac-de07-4e55-92b3-fcfecbd4fcd7', gitCredentialsId: '6d38ac8e-2d78-446d-9c84-f6072d896013', gitRepoUrl: 'http://10.211.55.3:7990/bitbucket/scm/proj/gitrepo2.git', runtimeConfig: 'TPZP', stream: 'PLAY'

            }
        }
    }
}
```

&nbsp;


# Product Assistance

Compuware provides assistance for customers with its documentation, the
FrontLine support web site, and telephone customer support.

## FrontLine Support Web Site

You can access online information for Compuware products via our
FrontLine support site
at [https://go.compuware.com](https://go.compuware.com/).
FrontLine provides access to critical information about your Compuware
products. You can review frequently asked questions, read or download
documentation, access product fixes, or e-mail your questions or
comments. The first time you access FrontLine, you must register and
obtain a password. Registration is free.

Compuware also offers User Communities, online forums to collaborate,
network, and exchange best practices with other Compuware solution users
worldwide. Go to <http://groups.compuware.com/> to join.

## Contacting Customer Support

At Compuware, we strive to make our products and documentation the best
in the industry. Feedback from our customers helps us maintain our
quality standards. If you need support services, please obtain the
following information before calling Compuware's 24-hour telephone
support:

-   The name, release number, and build number of your product. This
    information is displayed in the About dialog box.

-   Installation information including installed options, whether the
    product uses local or network databases, whether it is installed in
    the default directories, whether it is a standalone or network
    installation, and whether it is a client or server installation.

-   Environment information, such as the operating system and release on
    which the product is installed, memory, hardware and network
    specification, and the names and releases of other applications that
    were running when the problem occurred.

-   The location of the problem within the running application and the
    user actions taken before the problem occurred.

-   The exact application, licensing, or operating system error
    messages, if any.

You can contact Compuware in one of the following ways:

### Phone

-   USA and Canada: 1-800-538-7822 or 1-313-227-5444.
-   All other countries: Contact your local Compuware office. Contact
    information is available
    at [https://go.compuware.com](https://go.compuware.com/).

### Web

You can report issues via FrontLine.

**Note:** Please report all high-priority issues by phone.

## Corporate Web Site

To access Compuware's site on the Web, go
to [https://www.compuware.com](https://www.compuware.com/).
The Compuware site provides a variety of product and support
information.

# Change Log
### Version 1.0.6

-   Add support for flexible YAML location for ISPW/GIT integration
-   Make logs consistent between Jenkins operations and Topaz actions 
-   Auto clean up a task if the task is in failed status while loading source from GIT into ISPW
-   Build process re-runnable for a failed ispw/git synchronization

### Version 1.0.5

-   Add support for building components. The build functionality generates impacted components of one or more selected tasks at the same level in the life cycle. Additionally, the build functionality can generate impacted components of tasks at a selected level within a selected assignment or release container.
-   Add support for synchronizing between GIT and ISPW
-   Add support for CloudBees Folder plug-in
-   Add a 'skip polling' option for pipeline scripts

### Version 1.0.4

-   Add support for extra 10+ actions. See action list above.
-   Skip polling for the set completion if no web hook defined

### Version 1.0.3

-   Add support for retrieving the list of tasks for a given ISPW Set.
-   Changes to the logging to be more consistent with other plugins
    logging.
-   Fix bad error message when selected host connection doesn't have a
    valid CES URL.
-   Fix a bug in GetReleaseTaskList.
-   Pre-populate help text in request field.

### Version 1.0.2

-   The plugin now integrates with the [Compuware Common
    Configuration](https://plugins.jenkins.io/compuware-common-configuration) plugin
    which allows the Host Connection configurations to be defined
    centrally for other Compuware Jenkins plugins instead of needing to
    be specified in each Jenkins project's configuration.  Host
    Connection configuration is now defined in the Jenkins/Manage
    Jenkins/Configure System screen. 
-   Jenkins console logs produced by the plugin have been streamlined to
    improve readability.
-   Support for the Jenkins Pipeline Syntax.
-   Support Credentials secret text to store CES token.
-   Support most ISPW build operations.
-   Provide Docker script to build Docker image for CES server and
    Docker image for Jenkins with Compuware plugins pre-installed.
