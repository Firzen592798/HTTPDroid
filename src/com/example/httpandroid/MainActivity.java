package com.example.httpandroid;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.net.ftp.FTPClient;

import com.example.httpandroid.NanoHTTPD.Response.Status;
import com.example.httpandroid.exceptions.NoSDCardException;

import android.support.v4.app.Fragment;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;

public class MainActivity extends Activity {

	private static final int PORT = 8080;
	  private TextView hello;
	  private Context ctx;
	  private Button buttonIniciar;
	  private WebServer server;
	  private Handler handler = new Handler();
	 private FileListAdapter adapter;
	  
	 private void download(){
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
				downloader.downloadDirectory(ftpClient, parentDir, currentDir, "storage//emulated//0//");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	 }
	 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_main);
        ctx = this.getApplicationContext();
        String[] diretorios = null;
        DirectoryManager.createDefaultDirectory();
		try {
			diretorios = DirectoryManager.readLogList(DirectoryManager.DEVICE, "/JogosHttpDroid");
		} catch (NoSDCardException e1) {
			
		}
        List<String> dir = new ArrayList();
        
        for (int i=0; i < diretorios.length; i++){
        	//Log.w("Diretorio", diretorios[i]);
        	dir.add(diretorios[i]);
        }
        
        ListView lv = (ListView)findViewById(R.id.listPastas);
        ImageButton deviceButton = (ImageButton)findViewById(R.id.device);
        ImageButton sdcardButton = (ImageButton)findViewById(R.id.sdcard);
        
        download();
        
		adapter = new FileListAdapter(this, dir);
		if(lv != null){
			lv.setAdapter(adapter);
			lv.setTextFilterEnabled(true);
		}
		
		deviceButton.setOnClickListener(new View.OnClickListener() {
	        public void onClick(View v) {
	        	 String[] diretoriosDevice = null;
				try {
					diretoriosDevice = DirectoryManager.readLogList(DirectoryManager.DEVICE, ".");
				} catch (NoSDCardException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        	 //Log.w("Diretorios Device", String.valueOf(diretoriosDevice.length));
	        	
	        	
	        	adapter.device = DirectoryManager.DEVICE;
	        	adapter.basePath = "";
	        	adapter.parentDirectory = "";
	        	adapter.refresh(diretoriosDevice);
	        }
	    });
		                                             
		sdcardButton.setOnClickListener(new View.OnClickListener() {
	        public void onClick(View v) {
	        	
	        	 String[] diretoriosSD = null;
				try {
					diretoriosSD = DirectoryManager.readLogList(DirectoryManager.SD_CARD, "");
					adapter.device = DirectoryManager.SD_CARD;
		        	 adapter.basePath = "/storage/extSdCard";
		        	 adapter.parentDirectory = "";
		        	 adapter.refresh(diretoriosSD);
				} catch (NoSDCardException e) {
					Toast.makeText(ctx, "Cart�o SD n�o est� presente ou n�o est� habilitado nesse dispositivo", 3);
				}
	        	 
	        }
	    });
		
		startServer("");   
    }
    
    public void startServer(String path){
    	
    	server = new WebServer();
		    try {
		        server.start();
		        ctx = this.getApplicationContext();
		    } catch(IOException ioe) {
		        Log.d("HttpServer", "Server deu pau");
		    }
		    Log.d("HttpServer", "Server iniciado");
		    /*buttonIniciar.setOnClickListener(new View.OnClickListener() {
		        public void onClick(View v) {
		        	String url = "http://localhost:8080";
		        	Intent i = new Intent(Intent.ACTION_VIEW);
		        	i.setData(Uri.parse(url));
		        	startActivity(i);	
		        }
		    });*/
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if (server != null)
            server.stop();
    }


    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    public void onBackPressed() {
    	adapter.back();
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }
    
    
    
    private class WebServer extends NanoHTTPD {

    	public static final String
        MIME_PLAINTEXT = "text/plain",
        MIME_HTML = "text/html",
        MIME_JS = "application/javascript",
        MIME_CSS = "text/css",
        MIME_PNG = "image/png",
        MIME_DEFAULT_BINARY = "application/octet-stream",
        MIME_XML = "text/xml",
    	MIME_MP3 = "audio/mpeg";
    	private String path;
        public WebServer()
        {
            super(8080);
        }

        public void setPath(String path){
        	this.path = path;
        }
        
        @Override
        public Response serve(String uri, Method method, 
                              Map<String, String> header,
                              Map<String, String> parameters,
                              Map<String, String> files) {
            String answer = "";

            InputStream mbuffer = null;
            String html = "";
            String url  = "/storage/extSdCard/"+uri.substring(1);
            Log.w("Url", url);
            try {
            	mbuffer = null;
            	if(uri!=null){
                    if(uri.contains(".js")){
                    	mbuffer = new FileInputStream(url);
                            return new NanoHTTPD.Response(Status.OK, MIME_JS, mbuffer);
                    }else if(uri.contains(".css")){
                    	mbuffer = new FileInputStream(url);
                            return new NanoHTTPD.Response(Status.OK, MIME_CSS, mbuffer);

                    }else if(uri.contains(".png")){
                    	mbuffer = new FileInputStream(url);     
                            // HTTP_OK = "200 OK" or HTTP_OK = Status.OK;(check comments)
                            return new NanoHTTPD.Response(Status.OK, MIME_PNG, mbuffer);        
                    }else if(uri.contains(".mp3")){
                    	mbuffer = new FileInputStream(url);
                        // HTTP_OK = "200 OK" or HTTP_OK = Status.OK;(check comments)
                        return new NanoHTTPD.Response(Status.OK, MIME_MP3, mbuffer);        
                    }
                    else if(uri.contains(".html")){
                    	 mbuffer = new FileInputStream(url);
                         Log.w("Buffer", mbuffer.toString());
                                return new NanoHTTPD.Response(Status.OK, MIME_HTML, mbuffer);
                    }
                    
                }
            }
            catch(IOException ioe) {
                Log.w("Httpd", ioe.toString());
            }

            	

            return new NanoHTTPD.Response(answer);
        }
    }
    
    public class StackOverflowMp3Server extends NanoHTTPD {

        public StackOverflowMp3Server() {
             super(8089);
        }

        @Override
        public Response serve(String uri, Method method,
            Map<String, String> header, Map<String, String> parameters,
            Map<String, String> files) {
        String answer = "";

        FileInputStream fis = null;
        String url  = "/storage/extSdCard/www/yumesekai.mp3";
        try {
        	
            fis = new FileInputStream(url);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return new NanoHTTPD.Response(Status.OK, "audio/mpeg", fis);
      }
    }
}
