package base;


import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

import util.*;
//import com.util.Xls_Reader;


public class Tester {
	
	public static Logger APP_LOGS=null;
	public static Properties CONFIG=null;
	public static Properties OR=null;
	
	
	public static String AutomationSuites = null;
	public static String AutomationSuitesURL = null;
	
	public static String DemoSuite = null;
	public static String DemoSuiteURL = null;
	public static String DemoSuiteEmailSubject = null;
	
	
	public static boolean isInitialized = false;
	public static long start;
	public static long stop;

		
	public static long registerStartSplit(){
		return System.currentTimeMillis();
	}
	public static long registerStopSplit(){
		return System.currentTimeMillis();
	}
	
	public static void registerStart(){
		start = System.currentTimeMillis();
	}
	public static void registerStop(){
		stop = System.currentTimeMillis();
	}
	public static String displayElapsedTime(){
		return ("Time taken to run tests: "+(stop-start)/1000 +"seconds.");
	}
	public void initialize() throws IOException {
		
		if (!isInitialized){
			
			// logs
			APP_LOGS = Logger.getLogger("devpinoyLogger");
				
			// property files
			APP_LOGS.debug("Loading property files");
			CONFIG = new Properties();
			FileInputStream ip = new FileInputStream(System.getProperty("user.dir") + "//src//com//martinagency//config//config.properties");
			CONFIG.load(ip);
							
			OR = new Properties();
			FileInputStream ip2 = new FileInputStream(System.getProperty("user.dir") + "//src//com//martinagency//config//OR.properties");
			OR.load(ip2);
			
			APP_LOGS.debug("Loading property complete");
			// xls file
			APP_LOGS.debug("Initializing xlsx files");
		
			APP_LOGS.debug("Initializing Google sheets test data");
			//BenMooresheet = 
			//FTSsheet = 
			AutomationSuites = "Automation Suites";
			AutomationSuitesURL = "https://docs.google.com/spreadsheets/d/1ykAItw-bQOM79AWJol4bsjLMxcO1YhU-HnQT_aqrmPk/edit?usp=sharing";
			DemoSuite = "package_demo";
			DemoSuiteURL = "https://docs.google.com/spreadsheets/d/1nu5WMCUleFW360pqJb_YIz3RErWiGgXq7QsQA4SQ2ec/edit?usp=sharing";  // need to set
			DemoSuiteEmailSubject = "Test Automation Results";
			
			APP_LOGS.debug("Initializing google sheets results files complete");
			isInitialized = true;
		}
	}

}
