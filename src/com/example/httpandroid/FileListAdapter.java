package com.example.httpandroid;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FileListAdapter extends BaseAdapter{
	private Context mContext;  
	   private List<String> mArquivos;
	   public String parentDirectory = "";
	   public String currentFolder = "";
	   public FileListAdapter(Context context, List<String> arquivos) {  
		   this.mContext = context;  
	      this.mArquivos = arquivos;  
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
		   String[] subDirectories = DirectoryManager.readLogList(parentDirectory);
		   refresh(subDirectories);
	   }
	   
	   public int getCount() {  
	      return mArquivos.size(); // Retorna o n�mero de linhas  
	   }  
	  
	   public String getItem(int position) {  
	      return mArquivos.get(position); // Retorna o item daquela posi��o  
	   }  
	  
	   public long getItemId(int position) {  
	      return position;  
	   }  
	  
	   public View getView(final int position, View convertView, ViewGroup p) {  
		   
		  View view = convertView;
	      if (view == null) {  
	    	  Log.w("ERRO", "View Nula");
	    	  view = LayoutInflater.from(mContext).inflate(R.layout.listlayout, p, false);  
	    	 view.setTag("Arquivo nulo");
	      }
	      final String pItem = mArquivos.get(position);
	      
	      if(pItem != null){
	    	  currentFolder = pItem;
	    	  String[] subDirectories = DirectoryManager.readLogList(parentDirectory+pItem);
	    	  if(subDirectories != null){
	    		  for(int i = 0; i < subDirectories.length; i++){
	    			  Log.w("Subdiretorio", subDirectories[i]);
	    		  }
	    	  }
	    	  TextView pasta = (TextView) view.findViewById(R.id.textNomePastas);
	    	  ImageView image = (ImageView) view.findViewById(R.id.imgPastas);
	    	  if(pasta != null){
	    		  pasta.setText(pItem);
	    		  if(subDirectories != null){
	    			  image.setImageResource(R.drawable.folder);
	    		  }
	    		  else{
	    			  image.setImageResource(R.drawable.play);
	    		  }
	    	  }
	      }
	      view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				parentDirectory = parentDirectory + pItem + "/";
				String[] subDirectories = DirectoryManager.readLogList(parentDirectory);
				if(subDirectories != null){
					refresh(subDirectories);
				}
			}		 
	      });
	   
	      return view;  
	   }  
	  

}