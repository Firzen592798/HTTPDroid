package com.example.httpandroid;
//Firzen592798
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.example.httpandroid.exceptions.NoSDCardException;

import android.os.Environment;
import android.util.Log;

public class DirectoryManager {

	//List<File> files = getListFiles(new File(".")); 
	
	public static final int SD_CARD = 1;
	public static final int DEVICE = 2;
	private static String sdPath = "";
	public static void setSDPath(String path){
		DirectoryManager.sdPath = path;
	}
	public static List<File> getListFiles(File parentDir) {
	    ArrayList<File> inFiles = new ArrayList<File>();
	    File[] files = parentDir.listFiles();
	    for (File file : files) {
	        if (file.isDirectory()) {
	            inFiles.addAll(getListFiles(file));
	        } else {
	            if(file.getName().endsWith(".csv")){
	                inFiles.add(file);
	            }
	        }
	    }
	    Log.w("FILE", inFiles.toString());
	    return inFiles;
	}
	
	public static String[] readLogListSDCard(String filepath) throws NoSDCardException{
		String[] lista = null;
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) || Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED_READ_ONLY )){
	    File directory = Environment.getExternalStorageDirectory();
	    File folder = new File("/storage/extSdCard/" + filepath);

	    if ( !folder.exists() )
	    {
	        return null;
	    }
	    
	    if(folder.list() != null){
	    	lista = folder.list().clone();
	    }
		}else{
			throw new NoSDCardException();
		}
		return lista;
	}	
	
	public static String[] readLogList(int tipo, String filePath ) throws NoSDCardException
	{
		Log.w("Path", Environment.getExternalStorageDirectory().getAbsolutePath());
		File folder = null;
		if(tipo == DEVICE){
	    File directory = Environment.getExternalStorageDirectory();
	     ///storage/emulated/0 é o do tablet provavelmente
	    folder = new File( directory + "/" + filePath );
		}else if(tipo == SD_CARD){
			if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) || Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED_READ_ONLY )){
				folder = new File(sdPath + "/" + filePath);
			}else{
				throw new NoSDCardException();
			}
		}
	    if ( !folder.exists() || !folder.isDirectory())
	    {
	        return null;
	    }
	    
	    
	    String[] lista = null;
	    if(folder.list() != null){
	    	lista = folder.list().clone();
	    }
		return lista;
	}
	
	public static boolean createDefaultDirectory(){
	    File folder = new File(Environment.getExternalStorageDirectory().toString()+"/JogosHttpDroid");
	    boolean success = false;
	    if (!folder.exists()) {
	        success = folder.mkdirs();
	    }
	    Log.w("Sucesso", String.valueOf(success));
	    return success;
	}
	
	
	
}
