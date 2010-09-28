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
	FileReader f_in;
	FileWriter f_out;
	
	public FileUtils(WebView view, DroidGap gap)
	{
		mView = view;
	}
	
    public int testSaveLocationExists(){
        if (DirectoryManager.testSaveLocationExists())
            return 0;
        else
            return 1;
    }
    
    public long getFreeDiskSpace(){
        long freeDiskSpace=DirectoryManager.getFreeDiskSpace();
        return freeDiskSpace;
    }

    public int testFileExists(String file){
        if (DirectoryManager.testFileExists(file))
            return 0;
        else
            return 1;
    }
    
    public int testDirectoryExists(String file){
        if (DirectoryManager.testFileExists(file))
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
        if (DirectoryManager.deleteDirectory(dir))
            return 0;
        else
            return 1;
    }
    
    public int deleteFile (String fileName){
		  int status = 1;
					
			File file = new File(fileName);
			if (file.isFile()){
				try {  				
					file.delete();
					Log.d(LOG_TAG, "deleteFile file deleted " + fileName);
					status = 0;
				}catch (SecurityException se){
					Log.d(LOG_TAG, "deleteFile SecurityException");	
					se.printStackTrace();
					status = 1;
				}
			}else{				
				Log.d(LOG_TAG, "deleteFile file.isFile() returned false");
				status = 1;
			}		
		  return status;

		/*
				Log.d(LOG_TAG, "going to delete file: " + file);
        if (DirectoryManager.deleteFile(file))
            return 0;
        else
            return 1;
		*/
    }
    

    /**
	 * Create a new directory. 
	 * TODO: JavaScript Call backs for success and error handling 
	 */
    public void createDirectory(String dir){
				File file = new File(dir);
				file.mkdir();				
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
    
    public String readLogs(){
      try{             
        StringBuilder log = new StringBuilder();                    
        Process process = Runtime.getRuntime().exec("logcat -v time -d PhoneGapLog:V PhoneGapDirectoryManager:V PhoneGapDroidGap:V PhoneGapFileUtils:V PhoneGap:V PhoneGapCameraLauncher:V *:S");
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
