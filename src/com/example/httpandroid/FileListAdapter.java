package com.example.httpandroid;

import java.util.List;

import com.example.httpandroid.exceptions.NoSDCardException;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class FileListAdapter extends BaseAdapter{
	private Context mContext;  
	   private List<String> mArquivos;
	   public int device = 2;
	   public String parentDirectory = "";
	   public String currentFolder = "";
	   public String basePath = "";
	   public FileListAdapter(Context context, List<String> arquivos) {  
		   parentDirectory = "JogosHttpDroid/";
		   this.mContext = context;  
	       this.mArquivos = arquivos;  
	       DirectoryManager.setSDPath("/storage/extSdCard");
	   }  
	   
	   public void refresh(String[] novaLista){
		   mArquivos.clear();
		   for(int i = 0; i < novaLista.length; i++){
			   mArquivos.add(novaLista[i]); 
		   }
		   notifyDataSetChanged();
	   }
	  
	   public void back(){
		   String[] pastas = parentDirectory.split("/");
		   parentDirectory = "";
		   for(int i = 0; i < pastas.length - 1; i++){
			   parentDirectory = parentDirectory + pastas[i] + "/";
		   }
		   String[] subDirectories;
		try {
			subDirectories = DirectoryManager.readLogList(device, parentDirectory);
			refresh(subDirectories);
		} catch (NoSDCardException e) {
			Toast.makeText(mContext, "Cartão SD não está presente ou não está habilitado nesse dispositivo", 3).show();
		}
		   
	   }
	   
	   public int getCount() {  
	      return mArquivos.size(); // Retorna o número de linhas  
	   }  
	  
	   public String getItem(int position) {  
	      return mArquivos.get(position); // Retorna o item daquela posição  
	   }  
	  
	   public long getItemId(int position) {  
	      return position;  
	   }  
	  
	   public View getView(final int position, View convertView, ViewGroup p) {  
		   
		  View view = convertView;
	      if (view == null) {  
	    	  view = LayoutInflater.from(mContext).inflate(R.layout.listlayout, p, false);  
	    	  view.setTag("Arquivo nulo");
	      }
	      final String pItem = mArquivos.get(position);
	      if(pItem != null){
	    	  currentFolder = pItem;
	    	  
	    	  String[] subDirectories = null;
				try {
					Log.w("Diretorio Buscado", parentDirectory+pItem);
					subDirectories = DirectoryManager.readLogList(device, parentDirectory+pItem);
				} catch (NoSDCardException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    	 
	    	  TextView pastaTextView = (TextView) view.findViewById(R.id.textNomePastas);
	    	  ImageView image = (ImageView) view.findViewById(R.id.imgPastas);
	    	  Log.w("Arquivo", pItem);
	    	  if(pastaTextView != null){
	    		  pastaTextView.setText(pItem);
	    		  if(subDirectories != null){ //ISSO AKI TA VINDO NULO
	    			  int correctFiles = 0;
	    			  for(int i = 0; i < subDirectories.length; i++){
		    			  if(subDirectories[i].equals("index.html") || subDirectories[i].equals("images") ||subDirectories[i].equals("c2runtime.js") ||subDirectories[i].equals("loading-logo.png")){
		    				  correctFiles++;
		    			  }
		    		  }
	    			  if(correctFiles < 4){
	    				  image.setImageResource(R.drawable.folder);
	    				  view.setTag("NotPlayable");
	    			  }else{
	    				  image.setImageResource(R.drawable.play);
	    				  view.setTag("Playable");
	    			  }
	    		  }
	    		  else{
	    			  view.setTag("NotPlayable");
	    			  image.setImageResource(R.drawable.file);
	    		  }
	    	  }
	    	  
	      }
	      view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(v.getTag().equals("NotPlayable")){
					parentDirectory = parentDirectory + pItem + "/";
					String[] subDirectories = null;
					try {
						subDirectories = DirectoryManager.readLogList(device, parentDirectory);
					} catch (NoSDCardException e) {
						Toast.makeText(mContext, "Cartão SD não está presente ou não está habilitado nesse dispositivo", 3).show();
					}
					if(subDirectories != null){
						refresh(subDirectories);
					}
				}else {
					  ((MainActivity)mContext).startServer("");
					  String url = "http://localhost:8080" +"/"+  parentDirectory + pItem + "/index.html";
					  Log.w("URL", url);
					  Log.w("PatternDirecotry", parentDirectory);
					  Log.w("PItem", pItem);
		        	  Intent i = new Intent(Intent.ACTION_VIEW);
		        	  i.setData(Uri.parse(url));
		        	  mContext.startActivity(i);	
				}
			}		 
	      });
	   
	      return view;  
	   }  
	  

}
