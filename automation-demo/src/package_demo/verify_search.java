package package_demo;


import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;

import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.safari.SafariDriver;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.gdata.util.ServiceException;

import util.ErrorUtil;
import util.TestUtil;


public class verify_search extends visual_tests{
	@Test(dataProvider="getTestData")
	public void verify_search(String URL, 
										String term, 
										String browser, 
										String height, 
										String width, 
										String expected_copy) throws InterruptedException{
		count++;
		
		WebDriver driver;
        Capabilities cap;
        //System.out.println(URL);
        System.out.println(browser);
       
      //The following should work for OS X and Windows
        System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "//webdrivers//chromedriver");
      
		if(!runmodes[count].equalsIgnoreCase("Y")){
			skip = true;
			APP_LOGS.debug("Skipping this row of data" + count);
			throw new SkipException("Runmode for test data set set to no. "+count);
			
		}
		 
        		
		else{
			switch (browser){
    			case ("Firefox"):
    				//driver = new FirefoxDriver();
    				cap = DesiredCapabilities.firefox();
    				//ProfilesIni profile = new ProfilesIni();
    		        //FirefoxProfile ffprofile = profile.getProfile("Staging");
    		        //driver = new FirefoxDriver(ffprofile);
    				driver = new FirefoxDriver();
    				break;
    			case ("Chrome"):
    				driver = new ChromeDriver();
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
			
			
	        // This is your api key, make sure you use it in all your tests.
	       
	        	   
	     
			//FailureReports report = new FailureReports();
			try {
				
			
				driver.get(URL);
				Thread.sleep(PAUSE+2000L);
				System.out.println(term);
				driver.findElement(By.xpath(".//*[@id='lst-ib']")).sendKeys(term); // search field box
				driver.findElement(By.xpath(".//*[@id='lst-ib']")).sendKeys(Keys.RETURN);
				Thread.sleep(PAUSE+2000L);
				System.out.println(expected_copy);
				
				Thread.sleep(PAUSE+2000L);
				
				
				WebElement temp = driver.findElement(By.linkText(expected_copy));
				
				System.out.println(temp.getText());
				
				actual_copy = temp.getText();
				System.out.println(actual_copy);
				
				
				
				
				if (expected_copy.toUpperCase().equals(actual_copy.toUpperCase()) == true)
					Assert.assertEquals(expected_copy.toUpperCase(), actual_copy.toUpperCase()); 
				else{
					actual_copy = "Errors";
					APP_LOGS.debug("Error");
					APP_LOGS.debug("expected = " + expected_copy);
					APP_LOGS.debug("actual = " + "Error");
					
					fail = true;
					
				}
				
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
					
					Assert.assertEquals(expected_copy.toUpperCase(), actual_copy.toUpperCase());
				}
			}
			finally {
				
		
	            // Abort test in case of an unexpected error.
	            
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
		
		return TestUtil.getDataVerifySearch(DemoSuite, this.getClass().getSimpleName());
	}
}



