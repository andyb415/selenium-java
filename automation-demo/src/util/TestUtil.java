package util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import com.google.gdata.data.spreadsheet.CellEntry;
import com.google.gdata.data.spreadsheet.CellFeed;
import com.google.gdata.data.spreadsheet.ListFeed;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.util.ServiceException;

public class TestUtil {

	
		public static String GetRandomAlphaString(){
			String temp;
			temp = UUID.randomUUID().toString().replaceAll("\\d", "");
			return temp.replaceAll("-", "");
		}
		
		
		public static String getCurrentDateTime(){
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Date date = new Date();
			return dateFormat.format(date);
		}
		
		
		// GSheets version - finds if the test suite is runnable
		public static boolean isSuiteRunnable (String sheetName, String suiteName) throws MalformedURLException, GeneralSecurityException, IOException, ServiceException, URISyntaxException{
			boolean isExecutable = false;
			
			
			GSheetsReader reader = new GSheetsReader(sheetName);
			
			for (int i = 0; i < reader.getRowCount("Test Suites")-1;i++){
						
				String temp = reader.getCellData("Test Suites", "SuiteName", i);
				String runmode = reader.getCellData("Test Suites", "Runmode", i);
				//System.out.println(temp);
				
				if (temp.equalsIgnoreCase(suiteName)){
					
					if (runmode.equalsIgnoreCase("Y")){
						
						isExecutable = true;
					}else{
						isExecutable = false;
					}
				}		
			}
			return isExecutable;
		}
		
		
		
		
		// GSheets version  **********
		public static String[] getDataSetRunmodes(String sheetName, String worksheetName) {
			
			try {
				String[] runmodes = null;
				GSheetsReader reader = new GSheetsReader(sheetName);
				//reader.setupAccessToGSheets(sheetName);
				if (reader.doesWorksheetExist(worksheetName) == false){
					runmodes = new String[1];
					runmodes[0]="Y";
					return runmodes;
				}
				runmodes = new String[reader.getRowCount(worksheetName)-1];
	
				for (int i = 0;i<runmodes.length;i++){
					//System.out.println(reader.getCellData(sheetName,worksheetName, "Runmode",i));
					runmodes[i] = reader.getCellData(worksheetName, "Runmode",i);
				}
				return runmodes;
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			
			return null;
		}
		
		
		// GSheets version  **********
		// Returns cell data for given worksheet, colName and rowNum
		/*public static String getCellData (String sheetName, String worksheetName, String colName, int rowNum) throws MalformedURLException, GeneralSecurityException, IOException, ServiceException, URISyntaxException{
			
				
			GSheetsReader reader = new GSheetsReader(sheetName);
			
			return reader.getCellData(worksheetName, colName, rowNum);
		}*/
		
		
		
		
		
		// GSheets version  **********
		// finds if the test case is runnable
		public static boolean isTestCaseRunnable (String sheetName, String testcaseName) throws MalformedURLException, GeneralSecurityException, IOException, ServiceException, URISyntaxException{
				boolean isExecutable = false;
				//System.out.println("In isTestCaseRunnable");
				GSheetsReader reader = new GSheetsReader(sheetName);
				for (int i = 0; i < reader.getRowCount("TestcaseList")-1;i++){
						
					String testcase = reader.getCellData("TestcaseList", "TCID", i);
					//System.out.println(testcase);
						
					String runmode = reader.getCellData("TestcaseList", "Runmode", i);
					//System.out.println(testcaseName + " " + runmode);
					if (testcase.equalsIgnoreCase(testcaseName)){
						if (runmode.equalsIgnoreCase("Y")){
							isExecutable = true;
						}else{
							isExecutable = false;
						}
						//System.out.println(testcaseName + " " + runmode);
					}
				}
				return isExecutable;
		}
		
		
		
		
		
		
		
	
		// GSheets version
		public static boolean blankOutColumn(String sheetName, String worksheetName, String colName) throws MalformedURLException, GeneralSecurityException, IOException, ServiceException, URISyntaxException{
			GSheetsReader reader = new GSheetsReader(sheetName);
			
			WorksheetEntry worksheet = reader.getWorkSheet(worksheetName);
			
			int rows = reader.getRowCount(worksheetName);
			for (int i = 0; i< rows-1;i++){
				
				reader.resetTestData(worksheetName, i, colName);
			}
			
			return true;
		}
		
		
		public static Object[][] getDataVerifyMobileBreakPoint(String sheetName, String worksheetName) throws MalformedURLException, GeneralSecurityException, IOException, ServiceException, URISyntaxException{
			GSheetsReader reader = new GSheetsReader(sheetName);
			WorksheetEntry worksheet = reader.getWorkSheet(worksheetName);
    		URL listFeedUrl = worksheet.getListFeedUrl();
    	    ListFeed listFeed = GSheetsReader.service.getFeed(listFeedUrl, ListFeed.class);
    	    
    	    
			if (reader.doesWorksheetExist(worksheetName) == false){  // sheet doesn't exist 
				return new Object[1][0];
				
			}
			int rows = reader.getRowCount(worksheetName);
			int cols = reader.getColCount(worksheetName);
			
			
			
			// this section is specific to the data that corresponds to a particular testcase
			URL cellFeedUrl = new URI(worksheet.getCellFeedUrl().toString()
			        + "?min-row=2&min-col=1&max-col=4").toURL();
			CellFeed cellFeed = GSheetsReader.service.getFeed(cellFeedUrl, CellFeed.class);
			
			// load up cell values into a 1 dimensional array - ready to feed into the data 2 dimensional array
			Object[] list = new Object[cellFeed.getEntries().size()];
			int a = 0;
			for (CellEntry cell : cellFeed.getEntries()){
				list[a++] = cell.getCell().getValue();
			}
			int listCounter = 0;
			
			// set 2 dimensional array 
			Object[][] data = new Object[rows-1][cols-4]; //cols minus however columns including the Runmode col
			//System.out.println("setting up 2 dimensional array up of "+(rows-1)+" rows and "+(cols-3)+" cols.");
			for (int rowNum = 0; rowNum<rows-1;rowNum++){  // loop through each row
				
				for (int colNum = 0;colNum < cols-4;colNum++){	
										
					data[rowNum][colNum] = list[listCounter++]; 
					//System.out.println(data[rowNum][colNum]);
				}
				
			}
			return data;
		}
		
		public static Object[][] getDataVerifyClickToCopy(String sheetName, String worksheetName) throws MalformedURLException, GeneralSecurityException, IOException, ServiceException, URISyntaxException{
			GSheetsReader reader = new GSheetsReader(sheetName);
			WorksheetEntry worksheet = reader.getWorkSheet(worksheetName);
    		URL listFeedUrl = worksheet.getListFeedUrl();
    	    ListFeed listFeed = GSheetsReader.service.getFeed(listFeedUrl, ListFeed.class);
    	    
    	    
			if (reader.doesWorksheetExist(worksheetName) == false){  // sheet doesn't exist 
				return new Object[1][0];
				
			}
			int rows = reader.getRowCount(worksheetName);
			int cols = reader.getColCount(worksheetName);
			
			
			
			// this section is specific to the data that corresponds to a particular testcase
			URL cellFeedUrl = new URI(worksheet.getCellFeedUrl().toString()
			        + "?min-row=2&min-col=1&max-col=2").toURL();
			CellFeed cellFeed = GSheetsReader.service.getFeed(cellFeedUrl, CellFeed.class);
			
			// load up cell values into a 1 dimensional array - ready to feed into the data 2 dimensional array
			Object[] list = new Object[cellFeed.getEntries().size()];
			int a = 0;
			for (CellEntry cell : cellFeed.getEntries()){
				list[a++] = cell.getCell().getValue();
			}
			int listCounter = 0;
			
			// set 2 dimensional array 
			Object[][] data = new Object[rows-1][cols-4]; //cols minus however columns including the Runmode col
			//System.out.println("setting up 2 dimensional array up of "+(rows-1)+" rows and "+(cols-3)+" cols.");
			for (int rowNum = 0; rowNum<rows-1;rowNum++){  // loop through each row
				
				for (int colNum = 0;colNum < cols-4;colNum++){	
										
					data[rowNum][colNum] = list[listCounter++]; 
					//System.out.println(data[rowNum][colNum]);
				}
				
			}
			
			return data;
		}
		
		
		public static Object[][] getDataVerifyLayout(String sheetName, String worksheetName) throws MalformedURLException, GeneralSecurityException, IOException, ServiceException, URISyntaxException{
			System.out.println("in getDataVerifyLayout");
			GSheetsReader reader = new GSheetsReader(sheetName);
			WorksheetEntry worksheet = reader.getWorkSheet(worksheetName);
    		URL listFeedUrl = worksheet.getListFeedUrl();
    	    ListFeed listFeed = GSheetsReader.service.getFeed(listFeedUrl, ListFeed.class);
    	    
    	    
			if (reader.doesWorksheetExist(worksheetName) == false){  // sheet doesn't exist 
				return new Object[1][0];
				
			}
			int rows = reader.getRowCount(worksheetName);
			int cols = reader.getColCount(worksheetName);
			
			
			
			// this section is specific to the data that corresponds to a particular testcase
			URL cellFeedUrl = new URI(worksheet.getCellFeedUrl().toString()
			        + "?min-row=2&min-col=1&max-col=7").toURL();
			CellFeed cellFeed = GSheetsReader.service.getFeed(cellFeedUrl, CellFeed.class);
			
			// load up cell values into a 1 dimensional array - ready to feed into the data 2 dimensional array
			Object[] list = new Object[cellFeed.getEntries().size()];
			int a = 0;
			for (CellEntry cell : cellFeed.getEntries()){
				list[a++] = cell.getCell().getValue();
			}
			int listCounter = 0;
			
			// set 2 dimensional array 
			Object[][] data = new Object[rows-1][cols-4]; //cols minus however columns including the Runmode col
			//System.out.println("setting up 2 dimensional array up of "+(rows-1)+" rows and "+(cols-3)+" cols.");
			for (int rowNum = 0; rowNum<rows-1;rowNum++){  // loop through each row
				
				for (int colNum = 0;colNum < cols-4;colNum++){	
										
					data[rowNum][colNum] = list[listCounter++]; 
					
				}
				
			}
			
			return data;
		}
		
		
		public static Object[][] getDataVerifySearch(String sheetName, String worksheetName) throws MalformedURLException, GeneralSecurityException, IOException, ServiceException, URISyntaxException{
			GSheetsReader reader = new GSheetsReader(sheetName);
			WorksheetEntry worksheet = reader.getWorkSheet(worksheetName);
    		URL listFeedUrl = worksheet.getListFeedUrl();
    	    ListFeed listFeed = GSheetsReader.service.getFeed(listFeedUrl, ListFeed.class);
    	    
    	    
			if (reader.doesWorksheetExist(worksheetName) == false){  // sheet doesn't exist 
				return new Object[1][0];
				
			}
			int rows = reader.getRowCount(worksheetName);
			int cols = reader.getColCount(worksheetName);
			
			
			
			// this section is specific to the data that corresponds to a particular testcase
			URL cellFeedUrl = new URI(worksheet.getCellFeedUrl().toString()
			        + "?min-row=2&min-col=1&max-col=6").toURL();
			CellFeed cellFeed = GSheetsReader.service.getFeed(cellFeedUrl, CellFeed.class);
			
			// load up cell values into a 1 dimensional array - ready to feed into the data 2 dimensional array
			Object[] list = new Object[cellFeed.getEntries().size()];
			int a = 0;
			for (CellEntry cell : cellFeed.getEntries()){
				list[a++] = cell.getCell().getValue();
			}
			int listCounter = 0;
			
			// set 2 dimensional array 
			Object[][] data = new Object[rows-1][cols-4]; //cols minus however columns including the Runmode col
			//System.out.println("setting up 2 dimensional array up of "+(rows-1)+" rows and "+(cols-3)+" cols.");
			for (int rowNum = 0; rowNum<rows-1;rowNum++){  // loop through each row
				
				for (int colNum = 0;colNum < cols-4;colNum++){	
										
					data[rowNum][colNum] = list[listCounter++]; 
					//System.out.println(data[rowNum][colNum]);
				}
				
			}
			
			return data;
		}
		
		
		
		
		// return test data (values) 
		// GSheets version
		public static Object[][] getDataForSearch(String sheetName, String worksheetName) throws MalformedURLException, GeneralSecurityException, IOException, ServiceException, URISyntaxException{
			
			
			GSheetsReader reader = new GSheetsReader(sheetName);
			WorksheetEntry worksheet = reader.getWorkSheet(worksheetName);
    		URL listFeedUrl = worksheet.getListFeedUrl();
    	    ListFeed listFeed = GSheetsReader.service.getFeed(listFeedUrl, ListFeed.class);
    	    
    	    
			if (reader.doesWorksheetExist(worksheetName) == false){  // sheet doesn't exist 
				return new Object[1][0];
				
			}
			int rows = reader.getRowCount(worksheetName);
			int cols = reader.getColCount(worksheetName);
			
			
			
			// this section is specific to the data that corresponds to a particular testcase
			URL cellFeedUrl = new URI(worksheet.getCellFeedUrl().toString()
			        + "?min-row=2&min-col=1&max-col=2").toURL();
			CellFeed cellFeed = GSheetsReader.service.getFeed(cellFeedUrl, CellFeed.class);
			
			// load up cell values into a 1 dimensional array - ready to feed into the data 2 dimensional array
			Object[] list = new Object[cellFeed.getEntries().size()];
			int a = 0;
			for (CellEntry cell : cellFeed.getEntries()){
				list[a++] = cell.getCell().getValue();
			}
			int listCounter = 0;
			
			// set 2 dimensional array 
			Object[][] data = new Object[rows-1][cols-4]; //cols minus however columns including the Runmode col
			//System.out.println("setting up 2 dimensional array up of "+(rows-1)+" rows and "+(cols-3)+" cols.");
			for (int rowNum = 0; rowNum<rows-1;rowNum++){  // loop through each row
				
				for (int colNum = 0;colNum < cols-4;colNum++){	
										
					data[rowNum][colNum] = list[listCounter++]; 
					//System.out.println(data[rowNum][colNum]);
				}
				
			}
			return data;
		}
		
		public static Object[][] getDataForWinnerName(String sheetName, String worksheetName) throws MalformedURLException, GeneralSecurityException, IOException, ServiceException, URISyntaxException{
			
			
			GSheetsReader reader = new GSheetsReader(sheetName);
			WorksheetEntry worksheet = reader.getWorkSheet(worksheetName);
    		URL listFeedUrl = worksheet.getListFeedUrl();
    	    ListFeed listFeed = GSheetsReader.service.getFeed(listFeedUrl, ListFeed.class);
    	    
    	    
			if (reader.doesWorksheetExist(worksheetName) == false){  // sheet doesn't exist 
				return new Object[1][0];
				
			}
			int rows = reader.getRowCount(worksheetName);
			int cols = reader.getColCount(worksheetName);
			
			
			
			// this section is specific to the data that corresponds to a particular testcase
			URL cellFeedUrl = new URI(worksheet.getCellFeedUrl().toString()
			        + "?min-row=2&min-col=1&max-col=2").toURL();
			CellFeed cellFeed = GSheetsReader.service.getFeed(cellFeedUrl, CellFeed.class);
			
			// load up cell values into a 1 dimensional array - ready to feed into the data 2 dimensional array
			Object[] list = new Object[cellFeed.getEntries().size()];
			int a = 0;
			for (CellEntry cell : cellFeed.getEntries()){
				list[a++] = cell.getCell().getValue();
			}
			int listCounter = 0;
			
			// set 2 dimensional array 
			Object[][] data = new Object[rows-1][cols-4]; //cols minus however columns including the Runmode col
			//System.out.println("setting up 2 dimensional array up of "+(rows-1)+" rows and "+(cols-3)+" cols.");
			for (int rowNum = 0; rowNum<rows-1;rowNum++){  // loop through each row
				
				for (int colNum = 0;colNum < cols-4;colNum++){	
										
					data[rowNum][colNum] = list[listCounter++]; 
					//System.out.println(data[rowNum][colNum]);
				}
				
			}
			return data;
		}
		
		
		
		// GSheetes version - return test data (values) from a test case worksheet
		public static Object[][] getDataForQuestionairre(String sheetName, String worksheetName) throws MalformedURLException, GeneralSecurityException, IOException, ServiceException, URISyntaxException{
			
			
			GSheetsReader reader = new GSheetsReader(sheetName);
			WorksheetEntry worksheet = reader.getWorkSheet(worksheetName);
    		URL listFeedUrl = worksheet.getListFeedUrl();
    	    ListFeed listFeed = GSheetsReader.service.getFeed(listFeedUrl, ListFeed.class);
    	    
    	    
			if (reader.doesWorksheetExist(worksheetName) == false){  // sheet doesn't exist 
				return new Object[1][0];
				
			}
			int rows = reader.getRowCount(worksheetName);
			int cols = reader.getColCount(worksheetName);
			
			
			
			// this section is specific to the data that corresponds to a particular testcase
			URL cellFeedUrl = new URI(worksheet.getCellFeedUrl().toString()
			        + "?min-row=2&min-col=1&max-col=11").toURL();
			CellFeed cellFeed = GSheetsReader.service.getFeed(cellFeedUrl, CellFeed.class);
			
			// load up cell values into a 1 dimensional array - ready to feed into the data 2 dimensional array
			Object[] list = new Object[cellFeed.getEntries().size()];
			int a = 0;
			for (CellEntry cell : cellFeed.getEntries()){
				list[a++] = cell.getCell().getValue();
				//System.out.println(cell.getCell().getValue());
			}
			int listCounter = 0;
			
			// set 2 dimensional array 
			Object[][] data = new Object[rows-1][cols-4]; //cols minus however columns including the Runmode col
			//System.out.println("setting up 2 dimensional array up of "+(rows-1)+" rows and "+(cols-3)+" cols.");
			for (int rowNum = 0; rowNum<rows-1;rowNum++){  // loop through each row
				
				for (int colNum = 0;colNum < cols-4;colNum++){	
										
					data[rowNum][colNum] = list[listCounter++]; 
					
					//System.out.println(data[rowNum][colNum]);
				}
				
			}
			return data;
		
							
		}
		
		
		//GSheets version
		public static void reportDataSetResult(String sheetName, String worksheetName, String colName, int rowNum, String Results) throws MalformedURLException, GeneralSecurityException, IOException, ServiceException, URISyntaxException{
			
			GSheetsReader reader = new GSheetsReader(sheetName);
			reader.setCellData(worksheetName, colName, rowNum, Results);
			
		}
			
		//GSheets version
		public static int getRowNum(String sheetName, String worksheetName, String colName, String testCaseName) throws MalformedURLException, GeneralSecurityException, IOException, ServiceException, URISyntaxException{
			
			GSheetsReader reader = new GSheetsReader(sheetName);
			//System.out.println("in getRowNum");
			WorksheetEntry worksheet = reader.getWorkSheet(worksheetName);
    		URL listFeedUrl = worksheet.getListFeedUrl();
    		if (reader.doesWorksheetExist(worksheetName) == false){  // sheet doesn't exist 
				System.out.println("can't find worksheet");
    			return -1;
				
			}
			
    		for (int i=0;i<reader.getRowCount(worksheetName);i++){
    			String tcid = reader.getCellData(worksheetName, colName, i);
    			if (tcid.equals(testCaseName)){
    				return i;
    			}
    		}
    		
			return 1;
		}
		
		
	
}
