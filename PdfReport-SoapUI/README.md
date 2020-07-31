# ExtentReport-SoapUI
This repository have the teardown script for generating extent report along with Custom PDF Report from SOAPUI project.

# What is Extent Report?
Extent Report is a HTML based reporting library which is used for making excellent execution reports and simple to understand. This can be used with TestNG automation framework when using Selenium WebDriver for JAVA projects. This provide good detailed execution report with graphical representation of Pass and Failed testcases.

As an Automation Test Engineer we are supposed to find the defects and report it to the team in a simple way which should be easy to understand. In Automation Testing, importance of reporting is so high. Extent Report provides the easy way to report the Pass and Failed test cases.

# Pre-requisite for Extent Report:
1.  Java should be installed (by default it comes with SOAPUI installation)

2.  Extent Reporting jars
	-	extentreports-3.1.5.jar
	-	freemarker-2.3.28.jar
	-	bson-3.3.0.jar

3.  PDF Reporting jars
	-	commons-io-2.6.jar
	-	itextpdf-5.5.13.jar
	-	jcommon-1.0.16.jar
	-	jd-gui-1.6.6.jar
	-	jfreechart-1.0.13.jar
	-	pdfreporter-2.1.jar

* Don't worry you don't need to download these all jar files. I have also added .zip file in this repo which have all the above jar files in once place.

4.  You should have the admin access of SOAPUI installation folder.

# Extent Report with SOAPUI
SOAPUI doesn’t support reporting by default. We need to write custom code to project tear down scripts and therefore an overall HTML based project report can be generated.

This custom code and functions calls are then used to create HTML based project report. The best feature of this reporting is that it records the failed API’s request and response XMLs and attach that to extent report to get clear view as with what reason the API got failed. If a groovy script is failed, the code is capable enough to handle as to what reason the script got failed.

# PDF Report with SOAPUI
Once we have all the jars in place. These custom code and functions calls are also capable to generate the PDF report. We need to have config.properties updated with parameters to get the proper PDF report.

# Steps to Integrate Extent Report along PDF Report with SOAPUI:
Below are the steps required to integrate Extent Report with SOAPUI,

1.	Download the required jars mention in pre-requisite section of this article and paste it in the ${SOAPUI_HOME}\bin\ext folder.
2.	Download the config.properties file and update below parameters inside in the file,
	-	logopath 	:	*provide the path of SOAPUI Logo file
	-	pdfFilePath :	*provide the path, where you want to generate PDF file
	-	authorname	:	*provide the author information
	-	reporttitle	:	*provide PDF report title
	-	piechartPath:	*provide the path where pieChart will generate. Usually i will be same as pdfFilePath
	-	bargraphPath:	*provide the path where barGraph will generate. Usually i will be same as pdfFilePath
3.	Place this config file at the root location where your project is saved.
4.	Create a SOAPUI project and add TestSuite and TestCase as required.
5.	Add custom property to TestSuite as,
	-	“Author” - the value for it is “Author name of this testsuite”
	-	“Category” - the value for it is “Category for this testsuite”
6.	Once this is finished, download the groovy code from this project and paste it in the “Teardown Script” section of Project.
7.	Run the project and verify the logs.
8. 	Once the execution is completed, verify the reports folder, it should have a pdf file and Extent Report HTML file.

