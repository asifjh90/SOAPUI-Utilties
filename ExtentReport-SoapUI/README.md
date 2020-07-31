# ExtentReport-SoapUI
This repository have the teardown script for generating extent report from SOAPUI project.

What is Extent Report?
Extent Report is a HTML based reporting library which is used for making excellent execution reports and simple to understand. This can be used with TestNG automation framework when using Selenium WebDriver for JAVA projects. This provide good detailed execution report with graphical representation of Pass and Failed testcases.

As an Automation Test Engineer we are supposed to find the defects and report it to the team in a simple way which should be easy to understand. In Automation Testing, importance of reporting is so high. Extent Report provides the easy way to report the Pass and Failed test cases.

Pre-requisite for Extent Report:
1.    Java should be installed (by default it comes with SOAPUI installation)

2.    Extent Reporting jars

·       extentreports-3.1.3.jar

·       freemarker-2.3.23.jar

·       bson-3.3.0.jar

3.    You should have the admin access of SOAPUI installation folder.

Direct link to download all the above jars is as follows,
https://jar-download.com/artifacts/com.aventstack/extentreports/3.1.3/source-code

Extent Report with SOAPUI
SOAPUI doesn’t support reporting by default. We need to write custom code to project tear down scripts and therefore an overall HTML based project report can be generated.

This custom code and functions calls are then used to create HTML based project report. The best feature of this reporting is that it records the failed API’s request and response XMLs and attach that to extent report to get clear view as with what reason the API got failed. If a groovy script is failed, the code is capable enough to handle as to what reason the script got failed.

Steps to Integrate Extent Report with SOAPUI:
Below are the steps required to integrate Extent Report with SOAPUI,

1.    Download the required jars mention in pre-requisite section of this article and paste it in the ${SOAPUI_HOME}\bin\ext folder.

2.    Create a SOAPUI project and add TestSuite and TestCase as required.

3.    Add custom property to TestSuite as,

a.    “Author” - the value for it is “Author name of this testsuite”

b.    “Category” - the value for it is “Category for this testsuite”

4.    Once this is finished, download the groovy code from this project and paste it in the “Teardown Script” section of Project.


5.    Run the project and verify the logs.

