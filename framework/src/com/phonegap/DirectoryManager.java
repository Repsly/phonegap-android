package com.phonegap;

import java.io.File;

import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

public class DirectoryManager {

  private static final String LOG_TAG = "PhoneGapDirectoryManager";
	
	protected static boolean testFileExists (String name){
		boolean status;
		if ((testSaveLocationExists())&&(!name.equals(""))){
    		File path = Environment.getExternalStorageDirectory();
            File newPath = constructFilePaths(path.toString(), name);
            status = newPath.exists();
    	}else{
    		status = false;
    	}
		return status;
	}
	
	protected static long getFreeDiskSpace(){
		/*
		 * gets the available SD card free space or returns -1 if the SD card is not mounted.
		 */
		String status = Environment.getExternalStorageState();
		long freeSpace = 0;
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			try {
				File path = Environment.getExternalStorageDirectory();
				StatFs stat = new StatFs(path.getPath());
				long blockSize = stat.getBlockSize();
				long availableBlocks = stat.getAvailableBlocks();
				freeSpace = availableBlocks*blockSize/1024;
			} catch (Exception e) {e.printStackTrace(); }
		} else { return -1; }
		return (freeSpace);
	}	
	
	protected static boolean createDirectory(String directoryName){
		boolean status;
		if ((testSaveLocationExists())&&(!directoryName.equals(""))){
			File path = Environment.getExternalStorageDirectory();
            File newPath = constructFilePaths(path.toString(), directoryName);
			status = newPath.mkdir();
			status = true;
		}else
			status = false;
		return status;
	}
	
	protected static boolean testSaveLocationExists(){
		String sDCardStatus = Environment.getExternalStorageState();
		boolean status;
		if (sDCardStatus.equals(Environment.MEDIA_MOUNTED)){
			status = true;
		}else
			status = false;
		return status;
	}
	
	protected static boolean deleteDirectory(String fileName){
		boolean status;
		SecurityManager checker = new SecurityManager();
			
		if ((testSaveLocationExists())&&(!fileName.equals(""))){
		
			File path = Environment.getExternalStorageDirectory();
            File newPath = constructFilePaths(path.toString(), fileName);
			checker.checkDelete(newPath.toString());
			if(newPath.isDirectory()){
				String[] listfile = newPath.list();
				// delete all files within the specified directory and then delete the directory
				try{
					for (int i=0; i < listfile.length; i++){
						File deletedFile = new File (newPath.toString()+"/"+listfile[i].toString());
						deletedFile.delete();
					}
					newPath.delete();
					Log.i(LOG_TAG, "DirectoryManager deleteDirectory " + fileName);
					status = true;
				}catch (Exception e){
					e.printStackTrace();
					status = false;
				}
				
			}else
				status = false;
		}else
			status = false;
		return status;
	}
	
	protected static boolean deleteFile(String fileName){
		boolean status;
		SecurityManager checker = new SecurityManager();
			
		if ((testSaveLocationExists())&&(!fileName.equals(""))){
		
			File path = Environment.getExternalStorageDirectory();
            File newPath = constructFilePaths(path.toString(), fileName);
			checker.checkDelete(newPath.toString());
			if (newPath.isFile()){
				try {
  				Log.i(LOG_TAG, "DirectoryManager deleteFile " + fileName);
					newPath.delete();
					status = true;
				}catch (SecurityException se){
					Log.i(LOG_TAG, "DirectoryManager deleteFile SecurityException");	
					se.printStackTrace();
					status = false;
				}
			}else{				
				Log.i(LOG_TAG, "DirectoryManager deleteFile newPath.isFile() returned false");
				status = false;
			}
		}else{		
			Log.i(LOG_TAG, "DirectoryManager deleteFile testSaveLocationExists() returned false");
			status = false;
		}
		return status;
	}
	
	private static File constructFilePaths (String file1, String file2){
		//Log.d(LOG_TAG, "DirectoryManager constructFilePaths file1:" + file1 + " file2:" + file2);	
		File newPath;
		newPath = new File(file1+"/"+file2);
		return newPath;
	}

}