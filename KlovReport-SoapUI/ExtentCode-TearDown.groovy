/*
	* Author			:	Akshay Sharma
	* Created Date		:
	* Last Updated Date	:
	* Last Updated By	:
	* Project			:
*/

log.info "------- Starting Executing Teardown Script -------------------"
import com.aventstack.extentreports.reporter.KlovReporter;
import com.aventstack.extentreports.AnalysisStrategy;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;

try {
	Date d = new Date();
	// Get the project path
	def projectPath = new com.eviware.soapui.support.GroovyUtils(context).projectPath
	def relativeFilePath = projectPath.replace("\\","//");
	def reportPath = relativeFilePath + "//Reports"
	def authorName = "NULL";
	def categoryName = "NULL";
	
	ExtentHtmlReporter htmlReporter = new ExtentHtmlReporter(reportPath + "//soapuiExecutionReport.html");

	ExtentReports extent = new ExtentReports ();
	KlovReporter klov = new KlovReporter();
	
	extent.setSystemInfo("Host Name", "Automation Reporter");
	extent.setSystemInfo("Environment", "Automation");
	extent.setSystemInfo("Executer", "Akshay Sharma");

	htmlReporter.config().setDocumentTitle("SOAPUI Automation Report");
	htmlReporter.config().setReportName("Automation Testing Reporter");
	htmlReporter.config().setChartVisibilityOnOpen(true);

	
	klov.initMongoDbConnection("localhost",27017);
    	klov.setProjectName("klov");
	klov.setReportName("SOAPUI-Execution" + d.toString());
    	klov.setKlovUrl("http://localhost:80");	
	
	extent.attachReporter(htmlReporter, klov);

	def tsPassCount = 0
	def tsFailCount = 0
	def tcPassCount = 0
	def tcFailCount = 0

	for ( testSuiteResult in runner.results ) {
		if (testSuiteResult.status.toString() == "FINISHED") 
			tsPassCount++
		else 
			tsFailCount++
		
		for ( testCaseResult in testSuiteResult.getResults() ){
			if (testCaseResult.status.toString() == "FINISHED") 
				tcPassCount++
			else 
				tcFailCount++
		}
	}
	log.info "Total TS passed= " + tsPassCount + " Total TS Failed = " + tsFailCount
	log.info "Total TC passed= " + tcPassCount + " Total TC Failed = " + tcFailCount
		
	// Actual logic for Reports update
	ArrayList<String> failedMessages = new ArrayList<String>();
	runner.getResults().each { testSuiteRunner ->
		
		def testSuiteName = testSuiteRunner.getTestSuite().getName();
		
		if("null" == testSuiteRunner.getTestSuite().getPropertyValue("Author").toString() && "null" == testSuiteRunner.getTestSuite().getPropertyValue("Category").toString()){
			authorName = "NULL"
			categoryName = "NULL"
		} else if ("null" == testSuiteRunner.getTestSuite().getPropertyValue("Author").toString() && testSuiteRunner.getTestSuite().getPropertyValue("Category").toString() != "null"){
			authorName = "NULL"
			categoryName = testSuiteRunner.getTestSuite().getPropertyValue("Category")
		} else if (testSuiteRunner.getTestSuite().getPropertyValue("Author") != "NULL" && "null" == testSuiteRunner.getTestSuite().getPropertyValue("Category").toString()){
			authorName = testSuiteRunner.getTestSuite().getPropertyValue("Author")
			categoryName = "NULL"
		} else {
			authorName = testSuiteRunner.getTestSuite().getPropertyValue("Author")
			categoryName = testSuiteRunner.getTestSuite().getPropertyValue("Category")
		}
		
		ExtentTest parentTest = extent.createTest(testSuiteRunner.getTestSuite().getName());
		//log.info('Test Suite ' + testSuiteRunner.getTestSuite().getName() + ' TestRunner.status = ' + testSuiteRunner.getStatus());
		
		testSuiteRunner.getResults().each { testCaseRunner ->
		
			def testCaseName = testCaseRunner.getTestCase().getName()
		
			ExtentTest test = parentTest.createNode(testCaseRunner.getTestCase().getName(), testCaseRunner.getTestCase().getDescription());
			//log.info('Test Case ' + testCaseRunner.getTestCase().getName() + ' TestRunner.status = ' + testCaseRunner.getStatus());

			test.assignAuthor(authorName)
			test.assignCategory(categoryName)
			
			testCaseRunner.getResults().each { testStepResult ->
				//log.info('Test Step ' + testStepResult.getTestStep().getName() + ' TestRunner.status = ' + testStepResult.getStatus());

				def testStepStatus = testStepResult.getStatus();
				def testStepName = testStepResult.getTestStep().getName();
				def Request = "NULL"
				def Response = "NULL"
				def GroovyScript = "NULL"
				def Req = "NULL"
				def Res = "NULL"
				def actualData
				
				def tempTCName = testCaseName.replace('/','');
				def tempStpName = testStepName.replace('/','');
				
				def ResFile = reportPath+"//ReqRespPairs//"+tempTCName+"_"+tempStpName+"_FAILED.txt"
				def hrefLocation = "./ReqRespPairs/" + tempTCName + "_" + tempStpName + "_FAILED.txt"
					
				def getTestStepDetails = project.testSuites[testSuiteName].testCases[testCaseName].getTestStepByName(testStepName)
				
				if(testStepStatus.toString().equalsIgnoreCase('OK') || testStepStatus.toString().equalsIgnoreCase('PASS')) {

					Request = getTestStepDetails.getProperty("RawRequest")
					Response = getTestStepDetails.getProperty("Response")
					
					if(Request && Response) {
						log.info "Test Log Started for TestStep " + testStepName + " of TestCase " + tempTCName + " of TestSuite " + testSuiteName				
						test.pass(testStepName + "<b> PASSED </b>");				
						log.info "Test Log Completed for " + testStepName
					} else {
						test.pass(testStepName + "<b> PASSED </b>");
					}
				} else {
					testStepResult.getMessages().each { message ->
					failedMessages.add(message);	
					}
				
					if(getTestStepDetails.getProperty("Request") || getTestStepDetails.getProperty("Response") || getTestStepDetails.getProperty("Result")) {
						//log.info "Test Log Started for TestStep " + testStepName + " of TestCase " + tempTCName + " of TestSuite " + testSuiteName
						
						Request = getTestStepDetails.getProperty("RawRequest")
						Response = getTestStepDetails.getProperty("Response")
						GroovyScript = getTestStepDetails.getProperty("Result")
							
						if(GroovyScript.toString().equalsIgnoreCase("NULL")) {
							Req = Request.getValue();
							Res = Response.getValue();
							
							actualData = "------------- REQUEST DATA ---------------\n\n" + Req + "\n\n------------- RESPONSE DATA ---------------\n\n" + Res + "\n\n------------- REQUEST DATA ---------------\n\n" + failedMessages.toString()
							
							def data = new File(ResFile)
							data.write(actualData,"UTF-8")
							
							def appendReqAndRespPair = '<a href="' + hrefLocation + '" target="_blank">Failed Step Details </a>'
							
							test.fail(testStepName +" <b>Failed. Check Details | <b> " + "<span style='font-weight:bold;'></span>" + appendReqAndRespPair);							
							log.info "Test Log Completed for " + testStepName
							failedMessages.clear();
						} else if (Request.toString().equalsIgnoreCase("NULL")) {
							log.info "Test Log Started for TestStep " + testStepName + " of TestCase " + tempTCName + " of TestSuite " + testSuiteName
							
							Req = "No Request Found. Check below error details"
							Res = "No Response Found. Check below error details"
							
							actualData = "------------- REQUEST DATA ---------------\n\n" + Req + "\n\n------------- RESPONSE DATA ---------------\n\n" + Res + "\n\n------------- REQUEST DATA ---------------\n\n" + failedMessages.toString()
							
							def data = new File(ResFile)
							data.write(actualData,"UTF-8")
							
							def appendReqAndRespPair = '<a href="' + hrefLocation + '" target="_blank">Failed Step Details </a>'
							
							test.fail(testStepName +" <b>Failed. Check Details | <b> " + "<span style='font-weight:bold;'></span>" + appendReqAndRespPair);							
							log.info "Test Log Completed for " + testStepName
							failedMessages.clear();
						}
					} else {
						log.info "Test Log Started for TestStep " + testStepName + " of TestCase " + tempTCName + " of TestSuite " + testSuiteName
						
						Req = "No Request Found. Check below error details"
						Res = "No Response Found. Check below error details"
						
						actualData = "------------- REQUEST DATA ---------------\n\n" + Req + "\n\n------------- RESPONSE DATA ---------------\n\n" + Res + "\n\n------------- REQUEST DATA ---------------\n\n" + failedMessages.toString()
							
						def data = new File(ResFile)
						data.write(actualData,"UTF-8")
						
						def appendReqAndRespPair = '<a href="' + hrefLocation + '" target="_blank">Failed Step Details </a>'
						
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
} catch (IOException e) {
	e.printStackTrace();
}