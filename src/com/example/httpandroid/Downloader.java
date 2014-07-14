package com.example.httpandroid;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import android.util.Log;

public class Downloader {

	public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
	    Reader reader = null;
	    reader = new InputStreamReader(stream, "UTF-8");        
	    char[] buffer = new char[len];
	    reader.read(buffer);
	    return new String(buffer);
	}
	
	public static boolean downloadSingleFile(FTPClient ftpClient,
	        String remoteFilePath, String savePath) throws IOException {
	    File downloadFile = new File(savePath);
	     
	    File parentDir = downloadFile.getParentFile();
	    if (!parentDir.exists()) {
	        parentDir.mkdir();
	    }
	         
	    OutputStream outputStream = new BufferedOutputStream(
	            new FileOutputStream(downloadFile));
	    try {
	        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
	        return ftpClient.retrieveFile(remoteFilePath, outputStream);
	    } catch (IOException ex) {
	        throw ex;
	    } finally {
	        if (outputStream != null) {
	            outputStream.close();
	        }
	    }
	}
	
	public void downloadDirectory(FTPClient ftpClient, String parentDir,
	        String currentDir, String saveDir) throws IOException {
	    String dirToList = parentDir;
	    if (!currentDir.equals("")) {
	        dirToList += "/" + currentDir;
	    }
	 
	    FTPFile[] subFiles = ftpClient.listFiles(dirToList);
	 
	    if (subFiles != null && subFiles.length > 0) {
	        for (FTPFile aFile : subFiles) {
	            String currentFileName = aFile.getName();
	            if (currentFileName.equals(".") || currentFileName.equals("..")) {
	                // skip parent directory and the directory itself
	                continue;
	            }
	            String filePath = parentDir + "/" + currentDir + "/"
	                    + currentFileName;
	            if (currentDir.equals("")) {
	                filePath = parentDir + "/" + currentFileName;
	            }
	 
	            String newDirPath = saveDir + currentDir + File.separator + currentFileName;
	            if (currentDir.equals("")) {
	                newDirPath = saveDir + parentDir + File.separator
	                          + currentFileName;
	            }
	            if (aFile.isDirectory()) {
	                File newDir = new File(newDirPath);
	                boolean created = newDir.mkdirs();
	                if (created) {
	                    System.out.println("CREATED the directory: " + newDirPath);
	                } else {
	                    System.out.println("COULD NOT create the directory: " + newDirPath);
	                }
	 
	                // download the sub directory
	                downloadDirectory(ftpClient, dirToList, currentFileName,
	                        saveDir);
	            } else {
	                // download the file
	                boolean success = downloadSingleFile(ftpClient, filePath,
	                        newDirPath);
	                if (success) {
	                    System.out.println("DOWNLOADED the file: " + filePath);
	                } else {
	                    System.out.println("COULD NOT download the file: "
	                            + filePath);
	                }
	            }
	        }
	    }
	}
	
	public FTPClient connectFTP(String server, int port, String user, String pass) throws IOException{
		FTPClient ftpClient = new FTPClient();
		ftpClient.connect(server, port);
        ftpClient.login(user, pass);
        ftpClient.enterLocalPassiveMode();
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        return ftpClient;
	}
	
	public void downloadFTP(String server, int port, String user, String pass){
			FTPClient ftpClient = new FTPClient();
	        try {
	 
	            ftpClient.connect(server, port);
	            ftpClient.login(user, pass);
	            ftpClient.enterLocalPassiveMode();
	            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
	            int replyCode = ftpClient.getReplyCode();
	            System.out.println("Reply Code: " + replyCode);
	            String remoteFile1 = "/ftproot/teste/icon-256.png";
	            File downloadFile1 = new File("C:\\Documents and Settings\\Augusto\\Desktop\\icon-256.png");
	            OutputStream outputStream1 = new BufferedOutputStream(new FileOutputStream(downloadFile1));
	            boolean success = ftpClient.retrieveFile(remoteFile1, outputStream1);
	            outputStream1.close();
	            System.out.println(ftpClient.getReplyString());
	            if (success) {
	                System.out.println("File #1 has been downloaded successfully.");
	            }	 
	        } catch (IOException ex) {
	            System.out.println("Error: " + ex.getMessage());
	            ex.printStackTrace();
	        } finally {
	            try {
	                if (ftpClient.isConnected()) {
	                    ftpClient.logout();
	                    ftpClient.disconnect();
	                }
	            } catch (IOException ex) {
	                ex.printStackTrace();
	            }
	        }
	    }
	
	
	private String downloadUrl(String myurl) throws IOException {
	    InputStream is = null;
	    String[] path = myurl.split("/");
	    String filename = path[path.length - 1];
	    try { 
	        URL url = new URL(myurl);
	        is = url.openStream();
	        // Convert the InputStream into a string
	        //String contentAsString = readIt(is, len);
	        byte[] b = new byte[2048];
			int length;
			FileOutputStream fout = new FileOutputStream("C:\\Documents and Settings\\Augusto\\Desktop\\"+filename);
			while ((length = is.read(b)) != -1) {
				fout.write(b, 0, length);
			}
	        return "";
	        
	    } finally {
	        if (is != null) {
	            is.close();
	        } 
	    }
	}
	
	public static void main(String args[]){
		Downloader downloader = new Downloader();
		
		 String server = "10.7.172.249";
	     int port = 2100;
	     String user = "Firzen";
	     String pass = "tyranitar392";
	     FTPClient ftpClient = null;
		try {
			ftpClient = downloader.connectFTP(server, port, user, pass);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	     String parentDir = "ftproot";
	     String currentDir = "teste";
 	
		try {
			downloader.downloadDirectory(ftpClient, parentDir, currentDir, "C:\\Documents and Settings\\Augusto\\Desktop\\");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
