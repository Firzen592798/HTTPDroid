package com.example.httpandroid;
//Firzen592798
import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
	
	public static String[] readLogListSDCard(String filepath){
	    File directory = Environment.getExternalStorageDirectory();

	    File folder = new File("/storage/extSdCard/" + filepath);

	    if ( !folder.exists() )
	    {
	        return null;
	    }
	    String[] lista = null;
	    if(folder.list() != null){
	    	lista = folder.list().clone();
	    }
		return lista;
	}
	
	public static String[] readLogList(int tipo, String filePath )
	{
		File folder = null;
		if(tipo == DEVICE){
	    File directory = Environment.getExternalStorageDirectory();
	    Log.w("Path absoluto", directory.getAbsolutePath());
	    folder = new File( directory + "/" + filePath );
		}else if(tipo == SD_CARD){
			folder = new File(sdPath + "/" + filePath);
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
	
}
