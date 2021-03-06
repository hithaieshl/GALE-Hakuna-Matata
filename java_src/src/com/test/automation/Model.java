package com.test.automation;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;


public class Model{
public static File file;
public synchronized static void triggerSelenium(String ucid,String browser){
	
	System.out.println("in model");
    Statement stmt=null;
    Statement stmt1=null;
    PreparedStatement pStmt=null;
	ResultSet rs=null;
	ResultSet rs1=null;
	String useCaseName=null;
	WebDriver driver=null;
	ExtentReports eReport = null;
	ExtentTest testReport;
	String dateVar = null;
	String path = null;
	int usecase_id = 0;
	
	//Browser triggereded on browser var value
    if(browser.equalsIgnoreCase("firefox")){
	driver = new FirefoxDriver();
	}
	else if(browser.equalsIgnoreCase("ie")){
		System.setProperty("webdriver.ie.driver",Property.getPropertyValue("IEDRIVERPATH"));
		driver = new InternetExplorerDriver();
	}
	else if(browser.equalsIgnoreCase("Chrome")){
		System.setProperty("webdriver.chrome.driver",Property.getPropertyValue("CHROMEDRIVERPATH"));
		driver = new ChromeDriver();
	}
	
	//db connection
	Helper h=new Helper();
	Connection c=h.controller();
	
	//getting usecase id
	String[] useCaseSplit=ucid.split("=");
	String[] ucidl=useCaseSplit[1].split(",");
	
	//no of usecaseid : no of times selenium performs actions
	try {
		for(String id:ucidl){
			usecase_id=Integer.parseInt(id);
		stmt1=c.createStatement();
		String query1 = "SELECT use_case_name FROM test_cases_usecase Where id="+usecase_id+"; ";
	    rs1=stmt1.executeQuery(query1);
	   
	    while(rs1.next()){
	    	useCaseName=rs1.getString("use_case_name");
	    	System.out.println(useCaseName);
	    }
	    
		dateVar = new Model().getDateTime();
		System.out.println(dateVar);
		
		//for each usecase new folder for reports is created and html report will be present in it
		file = new File(Property.getPropertyValue("REPORTFOLDER")+dateVar+usecase_id);
		file.mkdir();
		
		eReport=new ExtentReports(Property.getPropertyValue("REPORTFOLDER")+dateVar+usecase_id+"//"+useCaseName+usecase_id+".html");

		path=Property.getPropertyValue("REPORTFOLDER")+dateVar+usecase_id+"//"+useCaseName+usecase_id+".html";
		testReport=eReport.startTest(useCaseName);
		    
	    	stmt=c.createStatement(); 
	    	String query = "Select description,action,locators,element_identifier,element_value from test_cases_action where use_case_id="+usecase_id+" Order by seq";
	    	System.out.println("after connection1");
	    	rs = stmt.executeQuery(query);
	    	
			driver.manage().window().maximize();
			driver.manage().timeouts().implicitlyWait(20,TimeUnit.SECONDS) ;
			
			
			ActionClass ac=new ActionClass();
			while(rs.next()){
				
				String desc=rs.getString("description");
				String action=rs.getString("action");
				String locators=rs.getString("locators");
				String locatorName=rs.getString("element_identifier");
				String testData=rs.getString("element_value");
				String msg=desc+action+locators+locatorName+testData;
				testReport.log(LogStatus.INFO,msg);
				ac.callActionMethods(driver,action,locators,locatorName,testData,c,testReport);
				
				
			}
			eReport.flush(); 
		}  
	} catch (SQLException e1) {
		e1.printStackTrace();
	}
	 
     finally{
    	 try {
     		// inserting file pathe, usecase id and timstamp to db
    		 driver.close();
 			 pStmt=c.prepareStatement("INSERT INTO test_cases_reports (report,use_case_id,time) VALUES (?,?,?)");
 			 pStmt.setString(1,path);
 			 pStmt.setInt(2,usecase_id);
 			 pStmt.setString(3,dateVar); 
 			 pStmt.executeUpdate();
 		     pStmt.close();
 		} catch (SQLException e) {
 			e.printStackTrace();
 		}
    	 
    	 if(c!=null){
    		 try {
				c.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
    	 }
    	 if(stmt!=null){
    		 try {
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
    		 if(stmt1!=null){
        		 try {
    				stmt.close();
    			} catch (SQLException e) {
    				e.printStackTrace();
    			}
    	 }
    	 if(rs!=null){
 				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
 				 if(rs1!=null){
 	 				try {
 						rs.close();
 					} catch (SQLException e) {
 						e.printStackTrace();
 					}		
    	 }
    	 
     }
    	 }
     }
     
   
}

public String getDateTime(){
 	
	 // Create object of SimpleDateFormat class and decide the format
	 DateFormat dateFormat = new SimpleDateFormat("MM_dd_yyyy HH.mm.ss");
	 Date date = new Date();
	 String currentDate= dateFormat.format(date);
	 return currentDate;
	
}
}
