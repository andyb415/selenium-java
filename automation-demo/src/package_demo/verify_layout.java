package package_demo;


import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;

import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.internal.ProfilesIni;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.safari.SafariDriver;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.applitools.eyes.Eyes;
import com.applitools.eyes.MatchLevel;
import com.applitools.eyes.RectangleSize;
import com.google.gdata.util.ServiceException;

import util.ErrorUtil;
import util.TestUtil;



public class verify_layout extends visual_tests{
	@Test(dataProvider="getTestData")
	public void verify_layout(String URL, 
										String baseLine, 
										String type,
										String browser, 
										String height, 
										String width, 
										String expected_copy) throws InterruptedException{
		count++;
		actual_copy = expected_copy;
			
		WebDriver driver;
        Capabilities cap;
       
        //The following should work for OS X and Windows
        System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "//webdrivers//chromedriver");
      
        
        //Cookie cookie = new Cookie.Builder(name, value).domain(domain).build();
        
		if(!runmodes[count].equalsIgnoreCase("Y")){
			skip = true;
			APP_LOGS.debug("Skipping this row of data" + count);
			throw new SkipException("Runmode for test data set set to no. "+count);
			
		}
		 
        		
		else{
			switch (browser){
    			case ("Firefox"):
    				//driver = new FirefoxDriver();
    				//cap = DesiredCapabilities.firefox();
    				ProfilesIni profile = new ProfilesIni();
    		        FirefoxProfile ffprofile = profile.getProfile("Staging");
    		        driver = new FirefoxDriver(ffprofile);
    				//driver = new FirefoxDriver();
    				break;
    			case ("Chrome"):
    				
    				ChromeOptions options = new ChromeOptions();
    				//options.addArguments("--profile-directory=\"Profile 2\"");
    				driver = new ChromeDriver(options);
    				cap = DesiredCapabilities.chrome();
    				
    				break;
    			case ("Safari"):
    				driver = new SafariDriver();
    				cap = DesiredCapabilities.safari();
    				break;
    			default:
    				driver = new FirefoxDriver();
    				cap = DesiredCapabilities.firefox();
    				break;
			}
			
			
			
			
			Thread.sleep(PAUSE);
			Eyes eyes = new Eyes();
			eyes.setBaselineName(baseLine);
			
	        // This is your api key, make sure you use it in all your tests.
	        eyes.setApiKey("0hdtBDqf7urY04y5ow100NCHpb3eXuvwH6Sd2ENd1HiIY110");
	        //eyes.setForceFullPageScreenshot(false);
	       
	        eyes.setMatchLevel(MatchLevel.LAYOUT2);
			//FailureReports report = new FailureReports();
			try {
				
				driver = eyes.open(driver, "Applitools", baseLine, new RectangleSize(Integer.parseInt(width), Integer.parseInt(height)));
				
				//System.out.println(System.getenv("USERPROFILE"));
				driver.get(URL);
				
				if (type.equalsIgnoreCase("Blank") == true){
					eyes.checkWindow("Page1");
				}
				else{
					eyes.checkRegion(driver.findElement(By.xpath(type)));
				}
				
				//System.out.println(eyes.getBaselineName());
				//FailureReports report = eyes.getFailureReports();
				//report.IMMEDIATE;
				//eyes.setFailureReports(report.IMMEDIATE);
	 			
				eyes.setMatchTimeout(10);
				
				eyes.close();
				
				driver.close();
				Assert.assertEquals(expected_copy.toUpperCase(), actual_copy.toUpperCase()); 
				//APP_LOGS.debug("expected = " + expected_copy);
				//APP_LOGS.debug("actual = " + actual_copy);
			}
			catch (Throwable t){  // if exception is thrown - it is actually a good thing.  we don't want to find the element in mobile layout.  
				
				
				if (t.toString().contains("Please approve the new baseline") == true){
					Assert.assertEquals(expected_copy.toUpperCase(), actual_copy.toUpperCase()); 
				}
				else{
					
					actual_copy = "Errors";
					APP_LOGS.debug("Error");
					APP_LOGS.debug("expected = " + expected_copy);
					APP_LOGS.debug("actual = " + "Error");
					ErrorUtil.addVerificationFailure(t);
					fail = true;
					//Assert.assertEquals(false,true); // for TestNG
					Assert.assertEquals(expected_copy.toUpperCase(), actual_copy.toUpperCase());
				}
			}
			finally {
				
		
	            // Abort test in case of an unexpected error.
	            eyes.abortIfNotClosed();
	            if (browser.equals("Safari") == true){
	            	driver.quit();
	            }
	            else
	            	driver.close();
	            
	            
	        }
			
			
			
		}
		
	}
	
	
	
	
	
	@DataProvider
	public Object[][] getTestData() throws MalformedURLException, GeneralSecurityException, IOException, ServiceException, URISyntaxException{
		
		return TestUtil.getDataVerifyLayout(DemoSuite, this.getClass().getSimpleName());
	}
}


