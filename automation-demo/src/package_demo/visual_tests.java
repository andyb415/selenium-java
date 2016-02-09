package package_demo;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.testng.SkipException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;

import base.Tester;
import util.SendMail;
import util.TestUtil;
import com.google.gdata.util.ServiceException;
public class visual_tests extends Tester{
	String URL = "";
	FirefoxProfile profile = new FirefoxProfile();	
	
	public static WebDriver driver;
	String runmodes[]=null;
	static int count=-1;
	static boolean pass = false;
	static boolean fail = false;
	static boolean skip = false;
	static boolean isTestPass = true;
	static final long PAUSE = 1000L;
	static int testnum = 1;
	String actual_copy = "";
	String expected_copy = "";
	static long start_split;
	static long stop_split;
	static float total_room_render_time = 0;
	static float ave_split = 0;
	static int split_counter = 1;
	public static String CurrentDate = null;
	//
	
	
	@BeforeTest
	public void checkTestSkip() throws Exception{
		
		CurrentDate = TestUtil.getCurrentDateTime();
		
		if (!TestUtil.isTestCaseRunnable(DemoSuite, this.getClass().getSimpleName())){
			APP_LOGS.debug("Skipping");
			throw new SkipException("Runmode of Search set to NO, so skippping.");
		}
		else{
			start_split = 0;
			stop_split = 0;
			
			// Load the runmodes of the tests and blank out previous results
			runmodes = TestUtil.getDataSetRunmodes(DemoSuite, this.getClass().getSimpleName());
			TestUtil.blankOutColumn(DemoSuite, this.getClass().getSimpleName(), "Actual");
			TestUtil.blankOutColumn(DemoSuite, this.getClass().getSimpleName(), "Results");
			TestUtil.blankOutColumn(DemoSuite, this.getClass().getSimpleName(), "Date");
		}
		
	}
	@BeforeSuite
	public void SetupSuite() throws Exception{
		
		initialize();
		Tester.registerStart();
	
		APP_LOGS.debug("Visual Tests Suite started at "+ (Tester.start));
		APP_LOGS.debug("Checking runmode of Visual Tests Suite");
		
		// GSheets version
		
		if (TestUtil.isSuiteRunnable(AutomationSuites, DemoSuite ) == false ){
			APP_LOGS.debug("Runmode set to N, skipping all VisualSuite testcases");
			
			throw new SkipException("Runmode set to N, skipping all VisualSuite testcases");
		}
		else{ // is runnable, blank out results column
			TestUtil.blankOutColumn(DemoSuite, "TestcaseList", "Results");
			TestUtil.blankOutColumn(DemoSuite, "TestcaseList", "Date");
		
		}
		
		
		
		
		
		
		
	}

	@AfterSuite(alwaysRun=true)
	public void BreakDown() throws Exception{
		
		
		Tester.registerStop();
		APP_LOGS.debug(Tester.displayElapsedTime());
		
		
		// Send email with results
		String[] to={"selenium.autobot@gmail.com"};
		
        String[] cc={};
        String[] bcc={};
        if (TestUtil.isSuiteRunnable(AutomationSuites, DemoSuite) == true ){
        	SendMail.sendMail("selenium.autobot@gmail.com",
        		            "4ut0b0T123",
        		            "smtp.gmail.com",
        		            "465",
        		            "true",
        		            "true",
        		            true,
        		            "javax.net.ssl.SSLSocketFactory",
        		            "false",
        		            to,
        		            cc,
        		            bcc,
        		            DemoSuiteEmailSubject,
        		            "Click here for the latest automated visual test results:\n" +DemoSuiteURL + "\n\n" + "");
        }
		
		
	}
	@AfterTest
	public void reportTestResult() throws MalformedURLException, GeneralSecurityException, IOException, ServiceException, URISyntaxException{
		
		// GSheets Version
		if (isTestPass){
			TestUtil.reportDataSetResult(DemoSuite, "TestcaseList", "Results", TestUtil.getRowNum(DemoSuite,"TestcaseList","TCID",this.getClass().getSimpleName()), "Pass");
			TestUtil.reportDataSetResult(DemoSuite, "TestcaseList", "Date", TestUtil.getRowNum(DemoSuite,"TestcaseList","TCID",this.getClass().getSimpleName()), CurrentDate);

		}
		else{
			TestUtil.reportDataSetResult(DemoSuite, "TestcaseList", "Results", TestUtil.getRowNum(DemoSuite,"TestcaseList","TCID",this.getClass().getSimpleName()), "Fail");
			TestUtil.reportDataSetResult(DemoSuite, "TestcaseList", "Date", TestUtil.getRowNum(DemoSuite,"TestcaseList","TCID",this.getClass().getSimpleName()), CurrentDate);

		}
		
		APP_LOGS.debug("Average final room render = " + ave_split + " seconds");
		count = -1;
	}
	@AfterMethod
	public void reporter() throws MalformedURLException, GeneralSecurityException, IOException, ServiceException, URISyntaxException{
		
		//GSheets Version
		if (skip){
			TestUtil.reportDataSetResult(DemoSuite, this.getClass().getSimpleName(), "Results", count, "Skip");
			TestUtil.reportDataSetResult(DemoSuite, this.getClass().getSimpleName(), "Actual", count, "--");
			
		}else if (fail){
			isTestPass = false;
			split_counter++;
			TestUtil.reportDataSetResult(DemoSuite, this.getClass().getSimpleName(), "Results", count, "Fail");
			TestUtil.reportDataSetResult(DemoSuite, this.getClass().getSimpleName(), "Actual", count, actual_copy);
			TestUtil.reportDataSetResult(DemoSuite, this.getClass().getSimpleName(), "Date", count, CurrentDate);
			
		}else{
			split_counter++;
			TestUtil.reportDataSetResult(DemoSuite, this.getClass().getSimpleName(), "Results", count, "Pass");
			TestUtil.reportDataSetResult(DemoSuite, this.getClass().getSimpleName(), "Actual", count, actual_copy);
			TestUtil.reportDataSetResult(DemoSuite, this.getClass().getSimpleName(), "Date", count, CurrentDate);
			
		}
		skip = false;
		fail = false;
		pass = false;

	}
	
	public static void compute_average_time(long time){
		total_room_render_time = total_room_render_time + time;
		ave_split = total_room_render_time / split_counter;
		System.out.println("ave split = " + ave_split);
	
	}
	
	public static void ClickOnXPath(String xpath) throws InterruptedException{
		Thread.sleep(500L);
		try{
			driver.findElement(By.xpath(xpath)).click();
			
			
		}catch (Throwable t){
			APP_LOGS.debug(xpath + " not found");
		}
		
	}

	
}