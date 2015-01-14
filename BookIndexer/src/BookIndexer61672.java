package book;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class BookIndexer61672 implements IBookIndexer {
	
		private final String code = "[^a-zA-Z0-9-]+";
	    ArrayList<String> officialKeywordsArray = new ArrayList<String>();
	    ArrayList<String> convertKeywordsToLowercase = new ArrayList<String>();
	    ArrayList<String> arrayList = new ArrayList<String>();
	    HashMap<String,ArrayList<Integer>> keysToPages = new HashMap<String,ArrayList<Integer>>();
	    
	    private void execute(String[] keywords, int n,String string){
	    	
	        String[] words = string.split(code);
	        
	        for (int i=0;i < words.length; i++) 
	        {
	            String singleWord = words[i];
	            singleWord = singleWord.toLowerCase();

	            if (arrayList.indexOf(singleWord)!=-1) 
	            {
	                ArrayList<Integer> pages = keysToPages.get(singleWord);

	                if (pages == null) pages = new ArrayList<Integer>();
	                else if (pages.indexOf(n) == -1) pages.add(n);
	                keysToPages.put(singleWord,pages);
	            }
	        }
	    }

	    @Override
		public void buildIndex(String bookFilePath, String[] keywords, String indexFilePath) {

	    	for (int i=0;i<keywords.length;i++) 
	        {
	            String keyword = keywords[i];            
	            officialKeywordsArray.add(keyword);
	            convertKeywordsToLowercase.add(keyword.toLowerCase());
	            arrayList.add(keyword.toLowerCase());
	        }
	    	
	    	//sort the arrayList
	        Collections.sort(arrayList);

	        try{            
	        	
	        	int numberOfPage = -1;
	            String singleLine;
	            BufferedReader reader = new BufferedReader(new FileReader(bookFilePath));

	            while ((singleLine = reader.readLine()) != null){
	            	
	                if (singleLine.endsWith(" ===") && singleLine.startsWith("=== Page ")) 
	                {
	                    String numString = singleLine.substring(9,singleLine.length() - 4);

	                    try 
	                    {
	                        int pageNumber = Integer.parseInt(numString);
	                        if (pageNumber != -1) {
	                            numberOfPage = pageNumber;
	                        }
	                    } 
	                    catch (NumberFormatException e) 
	                    {
	                        execute(keywords, numberOfPage,singleLine);
	                    }
	                    
	                } 
	                else if (singleLine.length() > 0) 
	                {
	                    execute(keywords, numberOfPage,singleLine);
	                }
	            }
	        }catch (IOException e) {
	            e.printStackTrace();
	        }

	        FileOutputStream fos = null;
	        OutputStreamWriter osw = null;
	        
	        try 
	        {
	            fos = new FileOutputStream(indexFilePath);
	            osw = new OutputStreamWriter(fos);

	            StringBuilder strungBuilder = new StringBuilder("INDEX");
	            for (int i=0;i<arrayList.size();i++) 
	            {
	                String keyword = arrayList.get(i);
	                int ind = convertKeywordsToLowercase.indexOf(keyword);
	                String official = officialKeywordsArray.get(ind);
	                String formatted = converting(keysToPages.get(keyword),official);
	                if (formatted != null) strungBuilder.append("\r\n" + formatted);
	            }

	            osw.write(strungBuilder.toString());

	        } 
	        catch (FileNotFoundException e) 
	        {
	            e.printStackTrace();
	        } 
	        catch (IOException e) 
	        {
	            e.printStackTrace();
	        } 
	        finally 
	        {
	            try 
	            {
	                osw.close();
	            } 
	            catch (IOException e) 
	            {
	                e.printStackTrace();
	            }
	        }
		}
	    
	    private String converting(ArrayList<Integer> list,String keyword) 
	    {
	        StringBuilder builder = new StringBuilder();

	        if (list != null) 
	        {
	            builder.append(keyword);

	            int n = list.size();
	            for (int i=0;i<n; i++) 
	            {
	                int i1=i;
	                int j=i;
	                if (i<n-1) 
	                {
	                    while (list.get(j+1) - list.get(j)==1) 
	                    {
	                        if (++j >= n-1){
	                        	break;
	                        }
	                    }
	                }
	                if(i1!=j) 
	                {
	                    builder.append("," + list.get(i1) + "-" + list.get(j));
	                    i = j;
	                }
	                else builder.append("," + list.get(i));
	            }
	            
	            return builder.toString();
	        }else{
	        	return null;
	        }
	    }
}
