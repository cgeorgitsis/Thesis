package com.github.fairsearch.utils;

import java.util.List;

import org.apache.lucene.search.ScoreDoc;

public interface IReader {
	public ScoreDoc[] readFromFile(String filename);
}
