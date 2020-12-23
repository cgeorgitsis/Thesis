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
import com.github.fairsearch.lib.FairTopK;
import com.github.fairsearch.utils.CategoryReader;
import com.github.fairsearch.utils.FairScoreDoc;
import com.github.fairsearch.utils.MultipleFairScoreDoc;

import metrics.Ndcg;
import metrics.Utility;

public class MultipleCategoriesTest {
	public static void main(String[] args) {
	        // number of topK elements returned (value should be between 10 and 400)
		int k = 20; 
		// proportion of protected candidates in the topK elements (value should be between 0.02 and 0.98)
		double p = 0.2;  
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
		
		
        CategoryReader reader = new CategoryReader();
        MultipleFairScoreDoc[] docs = (MultipleFairScoreDoc[]) reader.readFromFile("4_Groups_db.csv");
	   
	    List<ScoreDoc> before = new ArrayList<ScoreDoc>();
	    for(int i = 0; i < docs.length; i++) {
	    	before.add(docs[i].clone());
	    }
	    List<ScoreDoc> idealTopK = new ArrayList<ScoreDoc>();
       for(int i = 0; i < k; i++) {
    	   idealTopK.add(docs[i].clone());
       }
	    //k = 10;
	    double p1 = 0.05;
	    double p2 = 0.1;
	    double p3 = 0.8;
	    
	   
	    Fair fair1 = new Fair(k, p1, alpha);
	    Fair fair2 = new Fair(k, p2, alpha);
	    Fair fair3 = new Fair(k, p3, alpha);
	    int[] adjustedMTable1 = fair1.createAdjustedMTable();
	    int[] adjustedMTable2 = fair2.createAdjustedMTable();
	    int[] adjustedMTable3 = fair3.createAdjustedMTable();
	    for(int i = 0; i < adjustedMTable1.length; i++) {
	    	System.out.print(adjustedMTable1[i] + " ");
	    }
	    System.out.println();
	    TopDocs topCategory1 = fairTopK(docs, 1, k, p1, alpha);
	    List<ScoreDoc> afterCategory1 = new ArrayList<ScoreDoc>();
	    FairScoreDoc[] t = new FairScoreDoc[k];
	    for(int i = 0; i < k; i++) {
	    	t[i] = ((FairScoreDoc)topCategory1.scoreDocs[i]);
	    	afterCategory1.add(t[i].clone());
	    }
	    /*for(int i = 0; i < afterCategory1.size(); i++) {
	    	//((FairScoreDoc)afterCategory1.get(i)).isProtected
	        System.out.println(((FairScoreDoc)afterCategory1.get(i)).doc + " "+((FairScoreDoc)afterCategory1.get(i)).score + " "+((FairScoreDoc)afterCategory1.get(i)).isProtected);
	    }*/
	    TopDocs topCategory2 = fairTopK(docs, 2, k, p2, alpha);
	    List<ScoreDoc> afterCategory2 = new ArrayList<ScoreDoc>();
	    for(int i = 0; i < k; i++) {
	    	t[i] = ((FairScoreDoc)topCategory2.scoreDocs[i]);
	    	afterCategory2.add(t[i].clone());
	    }
	    
	    TopDocs topCategory3 = fairTopK(docs, 3, k, p3, alpha);
	    List<ScoreDoc> afterCategory3 = new ArrayList<ScoreDoc>();
	    for(int i = 0; i < k; i++) {
	    	t[i] = ((FairScoreDoc)topCategory3.scoreDocs[i]);
	    	afterCategory3.add(t[i].clone());
	    }
	    //fair = new Fair(k, p, alpha);
	    
	    resetScore(before, afterCategory1);    
	    resetScore(before, afterCategory2);
	    resetScore(before, afterCategory3);
  
	    for(int i = 0; i < afterCategory1.size(); i++) {
	    	//((FairScoreDoc)afterCategory1.get(i)).isProtected
	        System.out.println(((FairScoreDoc)afterCategory1.get(i)).doc + " "+((FairScoreDoc)afterCategory1.get(i)).score + " "+((FairScoreDoc)afterCategory1.get(i)).isProtected);
	    }
	    System.out.println();
	    
	    for(int i = 0; i < afterCategory2.size(); i++) {
	    	//((FairScoreDoc)afterCategory1.get(i)).isProtected
	        System.out.println(((FairScoreDoc)afterCategory2.get(i)).doc + " "+((FairScoreDoc)afterCategory2.get(i)).score + " "+((FairScoreDoc)afterCategory2.get(i)).isProtected);
	    }
	    System.out.println();
	    for(int i = 0; i < afterCategory3.size(); i++) {
	    	//((FairScoreDoc)afterCategory1.get(i)).isProtected
	        System.out.println(((FairScoreDoc)afterCategory3.get(i)).doc + " "+((FairScoreDoc)afterCategory3.get(i)).score + " "+((FairScoreDoc)afterCategory3.get(i)).isProtected);
	    }
	    
	    System.out.println();
	    
	    int maxElements1 = adjustedMTable1[k-1];
	    int maxElements2 = adjustedMTable2[k-1];
	    int maxElements3 = adjustedMTable3[k-1];
	    System.out.println(maxElements1 +" "+maxElements2 +" "+maxElements3);
	    ArrayList<ScoreDoc> result = mergeTopK(afterCategory1, afterCategory2, afterCategory3, k, maxElements1, maxElements2, maxElements3 );
	    for(int i = 0; i < result.size(); i++) {
	    	//((FairScoreDoc)afterCategory1.get(i)).isProtected
	        System.out.println(((FairScoreDoc)result.get(i)).doc + " "+((FairScoreDoc)result.get(i)).score + " "+((FairScoreDoc)result.get(i)).isProtected + " "+((MultipleFairScoreDoc)result.get(i)).getCategory());
	    }
	    
	    MultipleFairScoreDoc[] resultDocs = new MultipleFairScoreDoc[k];
	    for(int i = 0; i < k; i++) {
	    	resultDocs[i] = (MultipleFairScoreDoc) result.get(i);
	    }
	    setProtected(resultDocs, 1);
    	TopDocs top = new TopDocs(k, resultDocs, k);
    	
    	boolean isFair1 = fair1.isFair(top);
    	
    	setProtected(resultDocs, 2);
    	top = fairTopK(resultDocs, 2, k, p2, alpha);
    	boolean isFair2 = fair2.isFair(top);
    	
    	setProtected(resultDocs, 3);
    	top = fairTopK(resultDocs, 3, k, p3, alpha);
    	boolean isFair3 = fair3.isFair(top);
    	
    	boolean isFair = isFair1&&isFair2&&isFair3;
	    /*********/
	    
	    afterCategory1.clear();
	    afterCategory1.addAll(result);
	    TopDocs topKres = new TopDocs(k, t, k);
	    //boolean isFair = fair.isFair(topKres );
	    // isFair -> true
	    System.out.println("isFair = " + isFair);
	    Ndcg ndcg = new Ndcg();
	    double value = ndcg.compute(before, afterCategory1);
	    System.out.println("value = "+value);
	    double idealValue = ndcg.compute(idealTopK, idealTopK);
	    System.out.println("valueIdeal = "+idealValue);
	    System.out.println("NDCGvalue = "+value/idealValue);
  
	    Utility utility = new Utility();
	    value = utility.compute(before, afterCategory1);
	    System.out.println("Utilityvalue = "+value);
	    idealValue = utility.compute(before, before);
	    System.out.println("valueIdeal = "+idealValue);
	    //System.out.println("NDCGvalue = "+value/idealValue);
	    
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

	private static void setProtected(MultipleFairScoreDoc[] docs, int category) {
		for(int i = 0; i < docs.length; i++) {
			if(docs[i].getCategory() == category) {
				docs[i].isProtected = true;
			}
			else {
				docs[i].isProtected = false;
			}
		}
	}
	
	private static TopDocs fairTopK(MultipleFairScoreDoc[] docs, int category, int k, double p, double alpha) {
		List<ScoreDoc> npQueue = new ArrayList<>();
	    List<ScoreDoc> pQueue = new ArrayList<>();
	   
	    setProtected(docs, category);
	    splitCategories(docs, npQueue, pQueue);
	      
	     
	    FairTopK topK = new FairTopK();
	    TopDocs reRanked = topK.fairTopK(npQueue, pQueue, k, p, alpha);
	    return reRanked;
	}
	
	private static void resetScore(List<ScoreDoc> before, List<ScoreDoc> after) {
		// TODO Auto-generated method stub
		for(int i = 0; i < after.size(); i++) {
			int docId = after.get(i).doc;
			for(int j = 0; j < before.size(); j++) {
				if(before.get(j).doc == docId) {
					after.get(i).score = before.get(j).score;
					break;
				}
			}
		}
	}
	
	private static ArrayList<ScoreDoc> mergeTopK(List<ScoreDoc> topCategory1, List<ScoreDoc> topCategory2, List<ScoreDoc> topCategory3, int k, int maxElements1, int maxElements2, int maxElements3){
		ArrayList<ScoreDoc> result = new ArrayList<ScoreDoc>();
		
		insertProtected(result, topCategory1, maxElements1);
		for(int i = 0; i < result.size(); i++) {
	    	//((FairScoreDoc)afterCategory1.get(i)).isProtected
	        System.out.println(((FairScoreDoc)result.get(i)).doc + " "+((FairScoreDoc)result.get(i)).score + " "+((FairScoreDoc)result.get(i)).isProtected + " "+((MultipleFairScoreDoc)result.get(i)).getCategory());
	    }
		System.out.println("*******************************");
		
		insertProtected(result, topCategory2, maxElements2);
		for(int i = 0; i < result.size(); i++) {
	    	//((FairScoreDoc)afterCategory1.get(i)).isProtected
	        System.out.println(((FairScoreDoc)result.get(i)).doc + " "+((FairScoreDoc)result.get(i)).score + " "+((FairScoreDoc)result.get(i)).isProtected + " "+((MultipleFairScoreDoc)result.get(i)).getCategory());
	    }
		System.out.println("*******************************");
		insertProtected(result, topCategory3, maxElements3);
		for(int i = 0; i < result.size(); i++) {
	    	//((FairScoreDoc)afterCategory1.get(i)).isProtected
	        System.out.println(((FairScoreDoc)result.get(i)).doc + " "+((FairScoreDoc)result.get(i)).score + " "+((FairScoreDoc)result.get(i)).isProtected + " "+((MultipleFairScoreDoc)result.get(i)).getCategory());
	    }
		System.out.println("*******************************");
		
		clearList(topCategory1);
		clearList(topCategory2);
		clearList(topCategory3);
		
		ScoreDoc doc1 = nextNotProtected(topCategory1);
		ScoreDoc doc2 = nextNotProtected(topCategory2);
		ScoreDoc doc3 = nextNotProtected(topCategory3);
		while(result.size() < k) {
			if(doc1 != null && doc2 != null && doc3 != null) {

				if(doc1.score <= doc2.score && doc1.score <= doc3.score) {
					insertNotProtected(result, doc1);
					doc1 = nextNotProtected(topCategory1);
				}
				else if(doc2.score <= doc1.score && doc2.score <= doc3.score) {
					insertNotProtected(result, doc2);
					doc2 = nextNotProtected(topCategory2);
				}
				else {
					insertNotProtected(result, doc3);
					doc3 = nextNotProtected(topCategory3);
				}
			}
			else if(doc1 != null && doc2 != null) {
				if(doc1.score <= doc2.score) {
					insertNotProtected(result, doc1);
					doc1 = nextNotProtected(topCategory1);
				}
				else {
					insertNotProtected(result, doc2);
					doc2 = nextNotProtected(topCategory2);
				}
			}
			else if(doc1 != null) {
				insertNotProtected(result, doc1);
				doc1 = nextNotProtected(topCategory1);
			}
			else {
				break;
			}
			
			if(doc1 == null && doc3 != null) {
				doc1 = doc3;
				doc3 = null;
				topCategory1.addAll(topCategory3);
			}
			if(doc2 == null && doc3 != null) {
				doc2 = doc3;
				doc3 = null;
				topCategory2.addAll(topCategory3);
			}
			if(doc1 == null && doc2 == null && doc3 != null) {
				doc1 = doc3;
				doc3 = null;
				topCategory1.addAll(topCategory3);
			}

			if(doc1 == null && doc2 != null && doc3 == null) {
				doc1 = doc2;
				doc2 = null;
				topCategory1.addAll(topCategory2);
			}
			
		}
		return result;
	}

	private static void insertNotProtected(ArrayList<ScoreDoc> result, ScoreDoc doc1) {
		// TODO Auto-generated method stub
		boolean inserted = false;
		if(isInList(result, doc1))
			return;
		for(int j = result.size() - 1; j >= 0; j--) {
			if(result.get(j).score >= doc1.score) {
				result.add(j+1, doc1);
				inserted = true;
				break;
			}
		}
		if(inserted == false) {
			result.add(0, doc1);
		}
	}

	private static boolean isInList(ArrayList<ScoreDoc> result, ScoreDoc doc1) {
		// TODO Auto-generated method stub
		for(int i = 0; i < result.size(); i++) {
			if(result.get(i).doc == doc1.doc)
				return true;
		}
		return false;
	}

	private static void insertProtected(List<ScoreDoc> result, List<ScoreDoc> topCategory, int maxElements) {
		// TODO Auto-generated method stub
		System.out.println("from inp: "+maxElements);
		int counter = 0;
		if(maxElements == 0)
			return;
		for(int i = 0; i < topCategory.size(); i++) {
			FairScoreDoc fair = (FairScoreDoc)topCategory.get(i);
			if(fair.isProtected == true) {
				counter++;
				boolean inserted = false;
				for(int j = result.size() - 1; j >= 0; j--) {
					if(result.get(j).score >= fair.score) {
						result.add(j+1, fair);
						inserted = true;
						break;
					}
				}
				if(inserted == false) {
					result.add(0, fair);
				}
				if(counter == maxElements)
					return;
			}
			
		}
	}
	
	private static void clearList(List<ScoreDoc> docs) {
		for(int i = 0; i < docs.size(); i++) {
			FairScoreDoc fair = (FairScoreDoc)docs.get(i);
			if(fair.isProtected == true) {
				docs.remove(i);
			}
			
		}
	}
	private static ScoreDoc nextNotProtected(List<ScoreDoc> docs) {
		if(docs.isEmpty() == true)
			return null;
		ScoreDoc doc = docs.remove(0);
		return doc;
		
	}
}
