package com.phonegap;

import java.io.*;

import android.webkit.WebView;                            
import android.util.Log; 

import java.util.ArrayList;               
import java.io.IOException;   
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.UUID;

public class FileUtils {

	private static final String LOG_TAG = "PhoneGapFileUtils";
	
	WebView mView;
	DirectoryManager fileManager;
	FileReader f_in;
	FileWriter f_out;
	
	FileUtils(WebView view)
	{
		mView = view;
	}
	
    public int testSaveLocationExists(){
        if (fileManager.testSaveLocationExists())
            return 0;
        else
            return 1;
    }
    
    public long getFreeDiskSpace(){
        long freeDiskSpace=fileManager.getFreeDiskSpace();
        return freeDiskSpace;
    }

    public int testFileExists(String file){
        if (fileManager.testFileExists(file))
            return 0;
        else
            return 1;
    }
    
    public int testDirectoryExists(String file){
        if (fileManager.testFileExists(file))
            return 0;
        else
            return 1;
    } 

    /**
	 * Delete a specific directory. 
	 * Everyting in side the directory would be gone.
	 * TODO: JavaScript Call backs for success and error handling 
	 */
    public int deleteDirectory (String dir){
        if (fileManager.deleteDirectory(dir))
            return 0;
        else
            return 1;
    }
    
    public int deleteFile (String file){
        try{
            (new File(file)).delete();
         }catch(Exception e){
           Log.e(LOG_TAG, "deleteFile exception file: " + file + " exception: " + e.getMessage());
           return -1;
         }
         return 0;   
    }
       
    public int createDirectory(String dir) { 
      try { 
        boolean success = (new File(dir)).mkdirs(); 
      } catch (Exception e) {
        Log.e(LOG_TAG, "createDirectory exception dir: " + dir + " exception: " + e.getMessage());
        return -1; 
      } 
        return 0; 
    }
	
    public String read(String filename)
    {
    	String data = null;
    	try {
    		FileInputStream fstream = new FileInputStream(filename);
			  DataInputStream in = new DataInputStream(fstream);      
			  data = "";
			  while (in.available() !=0)
				{                 
					data += in.readLine();
				}			  
  		} catch (FileNotFoundException e) {
  		  Log.e(LOG_TAG, "file read FileNotFoundException exception file: " + filename + " exception: " + e.getMessage());
  		} catch (IOException e) {                                                                   
  		  Log.e(LOG_TAG, "file read IOException exception file: " + filename + " exception: " + e.getMessage());
  		} catch (Exception e){
  		  Log.e(LOG_TAG, "file read exception file: " + filename + " exception: " + e.getMessage());
  		}	
    	return data;
    }
    
    public int write(String filename, String data, boolean append)
    {
    		String FilePath= filename;
    		try {                                                              
    		  try{
    		    createDirectory(filename.substring(0, filename.lastIndexOf("/")));
  		    }catch(Exception e){}
    		  
				  byte [] rawData = data.getBytes();
    			ByteArrayInputStream in = new ByteArrayInputStream(rawData);    			    			
    			FileOutputStream out= new FileOutputStream(FilePath, append);
    			byte buff[] = new byte[rawData.length];
    			in.read(buff, 0, buff.length);
    			out.write(buff, 0, rawData.length);
    			out.flush();
    			out.close();    			
    		} catch (Exception e) {
    		  Log.e(LOG_TAG, "file write exception file: " + filename + " exception: " + e.getMessage()); 
    			return -1;
    		}
		return 0;
    }
    
    public String readLogs(){ // ArrayList<String>... params)
      try{             
        StringBuilder log = new StringBuilder();                    
        Process process = Runtime.getRuntime().exec("logcat -v time -d PhoneGapClientLog:V PhoneGapDroidGap:I PhoneGapFileUtils:I PhoneGap:I PhoneGapSQLiteStorage:I *:S");// commandLine.toArray(new String[0]));
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

        String line;
        while ((line = bufferedReader.readLine()) != null){ 
            log.append(line);
            log.append("\n"); 
        }                     
        return log.toString();        
      }       
      catch (Exception e){
        Log.e(LOG_TAG, "startLogging", e);
        return "";
      }
    }
    
    public String uuid(){      
      return UUID.randomUUID().toString();
    }
    

}
