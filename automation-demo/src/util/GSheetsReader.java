package util;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.gdata.client.spreadsheet.CellQuery;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.CellEntry;
import com.google.gdata.data.spreadsheet.CellFeed;
import com.google.gdata.data.spreadsheet.ListEntry;
import com.google.gdata.data.spreadsheet.ListFeed;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.SpreadsheetFeed;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.data.spreadsheet.WorksheetFeed;
import com.google.gdata.util.ServiceException;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;

public class GSheetsReader{
	public static URL SPREADSHEET_FEED_URL;
	public static File p12;
	public static HttpTransport httpTransport;
	public static JacksonFactory jsonFactory;
	public static String[] SCOPESArray = {"https://spreadsheets.google.com/feeds", "https://spreadsheets.google.com/feeds/spreadsheets/private/full", "https://docs.google.com/feeds"};
	public static final List SCOPES = Arrays.asList(SCOPESArray);
	public static GoogleCredential credential = null;
	public static SpreadsheetService service = null;
	public SpreadsheetEntry spreadsheet =  null;
  
	/*
	 
	01) Register at https://console.developers.google.com
	02) Create new project
	03) Under APIs & Auth -> Credential -> Create New Client ID for Service Account ID (email address)
	04) When the Client ID is generated You have to generate P12 key.
	05) Service Account ID (email address) will be needed in code below, Email address is the address You have to share Your spreadsheet
	
	And the p12 key will have to be kept locally.
	
	*/
	
	// Add the following jar files to your project
	/*
	 * 	gdata-core-1.0.jar
		gdata-maps-2.0.jar
		gdata-spreadsheet-3.0.jar
		google-api-client-1.20.0.jar
		google-http-client-1.20.0.jar
		google-http-client-jackson-1.20.0.jar
		google-http-client-jackson2-1.20.0.jar
		google-oauth-client-1.20.0.jar
		guava-13.0.1.jar
		jackson-core-2.1.3.jar
		jackson-core-asl-1.9.11.jar
	 * 
	*/
	
    public GSheetsReader(String sheetName) throws MalformedURLException, GeneralSecurityException, IOException, ServiceException, URISyntaxException{
    
    	
    	SPREADSHEET_FEED_URL = new URL("https://spreadsheets.google.com/feeds/spreadsheets/private/full");
        p12 = new File (System.getProperty("user.dir") + "/Oauth2Automation-8ec13840a39c.p12");
        httpTransport = new NetHttpTransport();
        jsonFactory = new JacksonFactory();
       
        credential = new GoogleCredential.Builder()
                .setTransport(httpTransport)
                .setJsonFactory(jsonFactory)
                .setServiceAccountId("313758812798-5eoqaqls7fvsf77h9lnqdr1hgjtf7ghq@developer.gserviceaccount.com")
                
                .setServiceAccountScopes(SCOPES)
                .setServiceAccountPrivateKeyFromP12File(p12)
                .build();
        service = new SpreadsheetService(sheetName);
      
        service.setOAuth2Credentials(credential);
        spreadsheet = getSpreadsheet(sheetName);
      
        
    }
    
    public SpreadsheetEntry getSpreadsheet(String sheetName) {
        try {
        	
       
            
            SpreadsheetFeed spreadsheetfeed = service.getFeed(SPREADSHEET_FEED_URL, SpreadsheetFeed.class);
            
            
            List<SpreadsheetEntry> spreadsheets = spreadsheetfeed.getEntries();
            for (SpreadsheetEntry sheetEntry : spreadsheets){
            	String wktName = sheetEntry.getTitle().getPlainText();
                
                if (wktName.equals(sheetName)) {
               	 	return sheetEntry;
                }

            }
            
            
	        } catch (Exception ex) {
	            System.out.println("exception thrown " + ex.toString());
	        }
        
        return null;
    }
    
    public WorksheetEntry getWorkSheet(String workSheetName) {
    	
    	try {
    		
           
            if (spreadsheet != null) {
            	
            	
                WorksheetFeed worksheetFeed = service.getFeed(
                      spreadsheet.getWorksheetFeedUrl(), WorksheetFeed.class);
               
                List<WorksheetEntry> worksheets = worksheetFeed.getEntries();
                
                for (WorksheetEntry worksheetEntry : worksheets) {
                     String wktName = worksheetEntry.getTitle().getPlainText();
                     
                     if (wktName.equals(workSheetName)) {
                    	 return worksheetEntry;
                     }
                }
                //
                System.out.println("can't find workSheetName: " + workSheetName);
             }
           
         } catch (Exception ex) {
        	 System.out.println("exception thrown " + ex.toString());
        }

        return null;
    }
    
 
    public boolean doesWorksheetExist(String workSheetName){
    	
    	try {
    		
            //SpreadsheetEntry spreadsheet = getSpreadsheet(sheetName);
            //System.out.println("success creating SpreadsheetEntry");
            if (spreadsheet != null) {
            	
            
                WorksheetFeed worksheetFeed = service.getFeed(
                      spreadsheet.getWorksheetFeedUrl(), WorksheetFeed.class);
               
                List<WorksheetEntry> worksheets = worksheetFeed.getEntries();
                
                for (WorksheetEntry worksheetEntry : worksheets) {
                     String wktName = worksheetEntry.getTitle().getPlainText();
                    // System.out.println(wktName);
                     if (wktName.equals(workSheetName)) {
                    	 return true;
                     }

                }
             }
           
         } catch (Exception ex) {
        	 System.out.println("exception thrown " + ex.toString());
        	
        }
    	// not found.
    	return false;
    }
    public boolean resetTestData(String worksheetName, int rowNum, String colName){
    	//System.out.println("in resetTestData ");
    	try{
    		if (!doesWorksheetExist(worksheetName)){
    			
    			return false;
    		}
    		
    		WorksheetEntry worksheet = getWorkSheet(worksheetName);
    		URL listFeedUrl = worksheet.getListFeedUrl();
    	    ListFeed listFeed = service.getFeed(listFeedUrl, ListFeed.class);
    	    
    	    ListEntry row = listFeed.getEntries().get(rowNum);
    	    
    	    // update cell with blank.
    	    row.getCustomElements().setValueLocal(colName, "");
    	    //System.out.println("");
    	    //System.out.println("Changing " + row.getCustomElements().getValue(colName) + " to " + data);
    	    row.update();
    	    return true;
    	    
    	   
    	}catch(Exception e){
    		System.out.println("resetTestData - " + e.toString());
    		return false;
    	}
    	
    	
    }
    public boolean setCellData(String worksheetName, String colName, int rowNum, String data){
    	try{
    		//System.out.println("In setCellData");
    		if (!doesWorksheetExist(worksheetName)){
    			//System.out.println("getCellData - worksheetName "+worksheetName+" doesn't exist.");
    			return false;
    		}
    		//System.out.println("In setCellData - rowNum = "+ rowNum);
    		WorksheetEntry worksheet = getWorkSheet(worksheetName);
    		URL listFeedUrl = worksheet.getListFeedUrl();
    	    ListFeed listFeed = service.getFeed(listFeedUrl, ListFeed.class);
    	    
    	    ListEntry row = listFeed.getEntries().get(rowNum);
    	    
    	    row.getCustomElements().setValueLocal(colName, data);
    	    
    	    //System.out.println("Changing " + row.getCustomElements().getValue(colName) + " to " + data);
    	    row.update();
    	    return true;
    	    
    	   
    	}catch(Exception e){
    		System.out.println("setCellData - " + e.toString());
    		return false;
    	}	
    }
      
    
    public String getCellData(String worksheetName, int rowNum, int colNum){
    	try{
    		
    		if (!doesWorksheetExist(worksheetName)){
    			//System.out.println("getCellData - worksheetName "+worksheetName+" doesn't exist.");
    			return "";
    		}
    		
    		WorksheetEntry worksheet = getWorkSheet(worksheetName);
    		URL listFeedUrl = worksheet.getListFeedUrl();
    	    ListFeed listFeed = service.getFeed(listFeedUrl, ListFeed.class);
    	    
    	    ListEntry row = listFeed.getEntries().get(rowNum);
    	    
    	    int rows = this.getRowCount(worksheetName);
			int cols = this.getColCount(worksheetName);
    	    //System.out.println(rows);
    	   // System.out.println(cols);
			URL cellFeedUrl = worksheet.getCellFeedUrl();
			
			CellFeed cellFeed = GSheetsReader.service.getFeed(cellFeedUrl, CellFeed.class);
			
			Object[] list = new Object[cellFeed.getEntries().size()];
			int a = 0;
			for (CellEntry cell : cellFeed.getEntries()){
				list[a++] = cell.getCell().getValue();
			}
			int listCounter = 0;
			
			
			Object[][] data = new Object[rows][cols];
			for (int rNum = 0;rNum< rows;rNum++){
				for (int cNum= 0;cNum<cols;cNum++){
					data[rNum][cNum] = list[listCounter++]; 
				}
				
				
			}
			
    	    return  (String) data[rowNum][colNum];
    	   
    	}catch(Exception e){
    		System.out.println("getCellData - " + e.toString());
    		return "";
    	}
    	
    }
    
    
    public String getCellData(String worksheetName, String colName, int rowNum){
    	try{
    		
    		if (!doesWorksheetExist(worksheetName)){
    			return "";
    		}
    		
    		WorksheetEntry worksheet = getWorkSheet(worksheetName);
    		URL listFeedUrl = worksheet.getListFeedUrl();
    	    ListFeed listFeed = service.getFeed(listFeedUrl, ListFeed.class);
    	    
    	    ListEntry row = listFeed.getEntries().get(rowNum);
    	    
    	    return row.getCustomElements().getValue(colName);
    	   
    	}catch(Exception e){
    		System.out.println("getCellData - " + e.toString());
    		return "";
    	}
    	
    }
   
    
    public int getRowCount(String worksheetName){
    	try{
    		
    		WorksheetEntry worksheet = this.getWorkSheet(worksheetName);
    		URL cellFeedUrl = new URI(worksheet.getCellFeedUrl().toString()).toURL();
    		CellQuery cellQuery = new CellQuery(cellFeedUrl);
    		CellFeed cellFeed = service.query(cellQuery, CellFeed.class);
    		return cellFeed.getRowCount();
        
       
    	} catch (Exception e){
    		System.out.println("getRowCount " + e.toString());
    		return -1;  // don't think this is needed.
    	}
    	
    }
    
    public int getCellRowNum(String worksheetName, String colName, String cellValue){
    	
    	for (int i = 0;i<this.getRowCount(worksheetName);i++){
    		if(this.getCellData(worksheetName, colName, i).equalsIgnoreCase(cellValue)){
	    		return i;
    		}
    			
    		
    	}
    	return -1; // something went wrong
    	
    	
    	
    
    }
    public int getColCount(String worksheetName){
    	try{
    		
    		WorksheetEntry worksheet = this.getWorkSheet(worksheetName);
    		URL cellFeedUrl = new URI(worksheet.getCellFeedUrl().toString()).toURL();
    		CellQuery cellQuery = new CellQuery(cellFeedUrl);
    		CellFeed cellFeed = service.query(cellQuery, CellFeed.class);
    		return cellFeed.getColCount();
        
       
    	} catch (Exception e){
    		System.out.println("getColCount " + e.toString());
    		return -1;  // don't think this is needed.
    	}
    	
    }
 
    

}