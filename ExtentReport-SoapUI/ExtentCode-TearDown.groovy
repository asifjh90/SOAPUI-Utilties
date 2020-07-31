/*
 *	Author : Akshay Sharma
*/

log.info "------- Starting Executing Teardown Script -------------------"
import com.aventstack.extentreports.AnalysisStrategy;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;

// Get the project path
def projectPath = new com.eviware.soapui.support.GroovyUtils(context).projectPath
def relativeFilePath = projectPath.replace("\\","//");
def actualProjectPath = relativeFilePath+"//Reports//soapuiExecutionReport.html"

ExtentHtmlReporter htmlReporter = new ExtentHtmlReporter(actualProjectPath);
ExtentReports extent = new ExtentReports ();

extent.attachReporter(htmlReporter);
extent.setSystemInfo("Host Name", "Automation Reporter");
extent.setSystemInfo("Environment", "Automation");
extent.setSystemInfo("Executer", "Akshay Sharma");

htmlReporter.config().setDocumentTitle("SOAPUI Automation Report");
htmlReporter.config().setReportName("Automation Testing Reporter");
htmlReporter.config().setChartVisibilityOnOpen(true);

// Actual logic for Reports update
ArrayList<String> failedMessages = new ArrayList<String>();
runner.getResults().each { testSuiteRunner ->
	ExtentTest parentTest = extent.createTest(testSuiteRunner.getTestSuite().getName());
	//log.info('Test Suite ' + testSuiteRunner.getTestSuite().getName() + ' TestRunner.status = ' + testSuiteRunner.getStatus());
	
	testSuiteRunner.getResults().each { testCaseRunner ->
		ExtentTest test = parentTest.createNode(testCaseRunner.getTestCase().getName(), testCaseRunner.getTestCase().getDescription());
		//log.info('Test Case ' + testCaseRunner.getTestCase().getName() + ' TestRunner.status = ' + testCaseRunner.getStatus());
		def authorName = testCaseRunner.getTestCase().getPropertyValue("Author")
		def categoryName = testCaseRunner.getTestCase().getPropertyValue("Category")
		if(authorName != null && categoryName != null){
			test.assignAuthor(authorName)
			test.assignCategory(categoryName)
		}
		testCaseRunner.getResults().each { testStepResult ->
			//log.info('Test Step ' + testStepResult.getTestStep().getName() + ' TestRunner.status = ' + testStepResult.getStatus());

			def testStepStatus = testStepResult.getStatus();
			
			if(testStepStatus.toString().equalsIgnoreCase('OK') || testStepStatus.toString().equalsIgnoreCase('PASS')) {
				//log.info "Start Loging " + testStepResult.getTestStep().getName() + " As Passed"
				test.pass(testStepResult.getTestStep().getName());
			} else {
				testStepResult.getMessages().each { message ->
					failedMessages.add(message);					
				}
				//log.info "Start Loging " + testStepResult.getTestStep().getName() + " As Failed"
				
				def testSuiteName = testSuiteRunner.getTestSuite().getName()
				def testCaseName = testCaseRunner.getTestCase().getName()
				def testStepName = testStepResult.getTestStep().getName()				
				def Request = "NULL"
				def Response = "NULL"
				def GroovyScript = "NULL"
				def Req = "NULL"
				def Res = "NULL"
				def actualData
				
				def tempTCName = testCaseName.replace('/','');
				def testTStpName = testStepName.replace('/','');
				
				def ResFile = relativeFilePath+"//ReqRespPairs//"+tempTCName+"_"+testTStpName+"_Failed.txt"
				
				def getTestStepDetails = project.testSuites[testSuiteName].testCases[testCaseName].getTestStepByName(testStepName)
				
				if(getTestStepDetails.getProperty("Request") || getTestStepDetails.getProperty("Response") || getTestStepDetails.getProperty("Result")) {
					Request = getTestStepDetails.getProperty("RawRequest")
					Response = getTestStepDetails.getProperty("Response")
					GroovyScript = getTestStepDetails.getProperty("Result")
					
					if(GroovyScript.toString().equalsIgnoreCase("NULL")) {
						Req = Request.getValue();
						Res = Response.getValue();
						
						actualData = "------------- REQUEST DATA ---------------\n\n" + Req + "\n\n------------- RESPONSE DATA ---------------\n\n" + Res + "\n\n------------- REQUEST DATA ---------------\n\n" + failedMessages.toString()
						
						def data = new File(ResFile)
						data.write(actualData,"UTF-8")
						
						def appendReqAndRespPair = '<a href="' + ResFile + '" target="_blank">Failed Step Details </a>'
						
						test.fail(testStepName +" <b>Failed. Check Details | <b> " + "<span style='font-weight:bold;'></span>" + appendReqAndRespPair);
						
						log.info "Test Log Completed for " + testStepName
						failedMessages.clear();
					} else if (Request.toString().equalsIgnoreCase("NULL")) {
						Req = "No Request Found. Check below error details"
						Res = "No Response Found. Check below error details"
						
						actualData = "------------- REQUEST DATA ---------------\n\n" + Req + "\n\n------------- RESPONSE DATA ---------------\n\n" + Res + "\n\n------------- REQUEST DATA ---------------\n\n" + failedMessages.toString()
						
						def data = new File(ResFile)
						data.write(actualData,"UTF-8")
						
						def appendReqAndRespPair = '<a href="' + ResFile + '" target="_blank">Failed Step Details </a>'
						
						test.fail(testStepName +" <b>Failed. Check Details | <b> " + "<span style='font-weight:bold;'></span>" + appendReqAndRespPair);
						
						log.info "Test Log Completed for " + testStepName
						failedMessages.clear();
					}
				} else {
					Req = "No Request Found. Check below error details"
					Res = "No Response Found. Check below error details"
					
					actualData = "------------- REQUEST DATA ---------------\n\n" + Req + "\n\n------------- RESPONSE DATA ---------------\n\n" + Res + "\n\n------------- REQUEST DATA ---------------\n\n" + failedMessages.toString()
						
					def data = new File(ResFile)
					data.write(actualData,"UTF-8")
					
					def appendReqAndRespPair = '<a href="' + ResFile + '" target="_blank">Failed Step Details </a>'
					
					test.fail(testStepName +" <b>Failed. Check Details | <b> " + "<span style='font-weight:bold;'></span>" + appendReqAndRespPair);
					
					log.info "Test Log Completed for " + testStepName
					failedMessages.clear();
				}
			}
		}
	}
}
extent.flush();
log.info "------- Completed Executing Teardown Script ------------------"