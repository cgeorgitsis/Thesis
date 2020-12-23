package com.github.examples;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import com.github.fairsearch.Fair;
import com.github.fairsearch.Simulator;
import com.github.fairsearch.lib.FairTopK;
import com.github.fairsearch.utils.FairScoreDoc;

import metrics.Ndcg;
import metrics.Utility;

public class HelloWorld {
    public static void main(String[] args) {
        // number of topK elements returned (value should be between 10 and 400)
        int k = 5; 
        // proportion of protected candidates in the topK elements (value should be between 0.02 and 0.98)
        double p = 0.8;  
        // significance level (value should be between 0.01 and 0.15)
        double alpha = 0.1; 
        
        //create the Fair object 
        Fair fair = new Fair(k, p, alpha);
        
        //create an mtable using alpha unadjusted
        int[] unadjustedMTable = fair.createUnadjustedMTable();
        System.out.println(unadjustedMTable.toString());
        //unadjustedMTable -> [0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 3]
        
        //analytically calculate the fail probability
        double analytical = fair.computeFailureProbability(unadjustedMTable);
        //analytical -> 0.14688718869911077
        System.out.println("analytical = " + analytical);
        //create an mtable using alpha adjusted
        int[] adjustedMTable = fair.createAdjustedMTable();
        //adjustedMTable -> [0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2]
        
        //analytically calculate the fail probability
        analytical = fair.computeFailureProbability(adjustedMTable);
        //analytical -> 0.10515247355215218

        System.out.println("analytical2 = " + analytical);
        //let's manually create an unfair ranking (False -> unprotected, True -> protected)
       //in this example the first document (docid=20) has a score of 20, the last document (docid=1) a score of 1
  
       FairScoreDoc[] docs = readFromFile("test_adult.csv");
       List<ScoreDoc> npQueue = new ArrayList<>();
       List<ScoreDoc> pQueue = new ArrayList<>();
       splitCategories(docs, npQueue, pQueue);
      
       List<ScoreDoc> before = new ArrayList<ScoreDoc>();
       for(int i = 0; i < docs.length; i++) {
    	   before.add(docs[i].clone());
       }
       List<ScoreDoc> idealTopK = new ArrayList<ScoreDoc>();
       for(int i = 0; i < k; i++) {
    	   idealTopK.add(docs[i].clone());
       }
       FairTopK topK = new FairTopK();
       TopDocs reRanked = topK.fairTopK(npQueue, pQueue, k, p, alpha);
 
     
       for(int i=0; i<k; i++) {
           FairScoreDoc current = (FairScoreDoc)reRanked.scoreDocs[i];
           //System.out.println(current.doc + " "+current.score + " "+current.isProtected);
          
       }
       //fair = new Fair(k, p, alpha);
       List<ScoreDoc> after = new ArrayList<ScoreDoc>();
       ScoreDoc[] t = new ScoreDoc[k];
       for(int i = 0; i < k; i++) {
    	   t[i] = reRanked.scoreDocs[i];
    	   after.add(t[i]);
       }
      TopDocs topKres = new TopDocs(k, t, k);
      boolean isFair = fair.isFair(topKres );
      // isFair -> true
      System.out.println("isFair = " + isFair);
      Ndcg ndcg = new Ndcg();
      double value = ndcg.compute(before, after);
      System.out.println("value = "+value);
      double idealValue = ndcg.compute(idealTopK, idealTopK);
      System.out.println("valueIdeal = "+idealValue);
      System.out.println("NDCGvalue = "+value/idealValue);
      
      Utility utility = new Utility();
      value = utility.compute(before, after);
      System.out.println("Utilityvalue = "+value);
      idealValue = utility.compute(before, before);
      System.out.println("valueIdeal = "+idealValue);
      //System.out.println("NDCGvalue = "+value/idealValue);
      /*fair
       * 
       * TopDocs unfairRanking = new TopDocs(docs.length, docs, Float.NaN);
       

      // let's check if the ranking is fair
      boolean isFair = fair.isFair(unfairRanking);
      // isFair -> false
      for(int i=0; i<unfairRanking.scoreDocs.length; i++) {
          FairScoreDoc current = (FairScoreDoc)unfairRanking.scoreDocs[i];
          System.out.println(current.doc + " "+current.score + " "+current.isProtected);
          
      }
      System.out.println("\n\n\n\n\n"+"isFair = " + isFair+"\n\n\n\n\n");
      //now re-rank the unfair ranking  
      TopDocs reRanked = fair.reRank(unfairRanking);
      for(int i=0; i<reRanked.scoreDocs.length; i++) {
          FairScoreDoc current = (FairScoreDoc)reRanked.scoreDocs[i];
          System.out.println(current.doc + " "+current.score + " "+current.isProtected);
          
      }

      // now let's see if the ranking is fair
      isFair = fair.isFair(reRanked);
      // isFair -> true
      System.out.println("isFair = " + isFair);*/
    }

	private static void splitCategories(FairScoreDoc[] docs, List<ScoreDoc> npQueue, List<ScoreDoc> pQueue) {
		// TODO Auto-generated method stub
		
		for(int i = 0; i < docs.length; i++) {
			if(docs[i].isProtected)
				pQueue.add(docs[i]);
			else
				npQueue.add(docs[i]);
		}
	}

	private static FairScoreDoc[] readFromFile(String csvFile) {
		// TODO Auto-generated method stub
		
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
        ArrayList<FairScoreDoc> docs = new ArrayList<FairScoreDoc>();

        try {

            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {
            	
                // use comma as separator
                String[] tokens = line.split(cvsSplitBy);
                int docId = Integer.parseInt(tokens[0]);
                float score = Float.parseFloat(tokens[1]);
                boolean isProtected = Boolean.parseBoolean(tokens[2].trim());
                
                FairScoreDoc doc = new FairScoreDoc(docId, score, isProtected);
                docs.add(doc);
                
                

                //System.out.println("Country [code= " + country[4] + " , name=" + country[5] + "]");

            }
            FairScoreDoc[] docArray = new FairScoreDoc[docs.size()];
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