package com.github.fairsearch.utils;

public class MultipleFairScoreDoc extends FairScoreDoc {

	private int category;
	
	public MultipleFairScoreDoc(int doc, float score, int category) {
		super(doc, score);
		// TODO Auto-generated constructor stub
		this.category = category;
	}
	
	public MultipleFairScoreDoc(int doc, float score, int category, boolean isProtected) {
		super(doc, score, isProtected);
		// TODO Auto-generated constructor stub
		this.category = category;
	}
	public int getCategory() {
		return category;
	}
	public MultipleFairScoreDoc clone() {
		return new MultipleFairScoreDoc(doc, score, category, isProtected);
	}

}
