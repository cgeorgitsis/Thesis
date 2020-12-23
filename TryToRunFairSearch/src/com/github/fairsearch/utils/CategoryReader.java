package com.github.fairsearch.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.lucene.search.ScoreDoc;

public class CategoryReader implements IReader {

	private HashMap<String, Integer> categories;
	
	public CategoryReader() {
		categories = new HashMap<String, Integer>();
		categories.put("White", 0);
		categories.put("Black", 1);
		categories.put("Asian-Pac-Islander", 2);
		categories.put("Amer-Indian-Eskimo", 3);
	}
	@Override
	public ScoreDoc[] readFromFile(String filename) {
		// TODO Auto-generated method stub
		BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
        ArrayList<MultipleFairScoreDoc> docs = new ArrayList<MultipleFairScoreDoc>();

        try {

            br = new BufferedReader(new FileReader(filename));
            while ((line = br.readLine()) != null) {
            	
                // use comma as separator
                String[] tokens = line.split(cvsSplitBy);
                int docId = Integer.parseInt(tokens[0]);
                float score = Float.parseFloat(tokens[1]);
                //boolean isProtected = Boolean.parseBoolean(tokens[2].trim());
                System.out.println(tokens[2].trim());
                int category = categories.get(tokens[2].trim());
                MultipleFairScoreDoc doc = new MultipleFairScoreDoc(docId, score, category);
                docs.add(doc);

                //System.out.println("Country [code= " + country[4] + " , name=" + country[5] + "]");

            }
            MultipleFairScoreDoc[] docArray = new MultipleFairScoreDoc[docs.size()];
            for(int i = 0; i < docs.size(); i++) {
            	docArray[i] = docs.get(i);
            }
            //System.out.println(docs.size());
            return docArray;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

		return null;
	}

}
