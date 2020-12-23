package metrics;

import java.util.List;

import org.apache.lucene.search.ScoreDoc;

public class Ndcg implements Metrics {

	@Override
	public double compute(List<ScoreDoc> before, List<ScoreDoc> after) {
		// TODO Auto-generated method stub
		double sum = 0;
		for(int i = 0; i < after.size(); i++) {
			int docId = after.get(i).doc;
			for(int j = 0; j < before.size(); j++) {
				if(before.get(j).doc == docId) {

					double q = before.get(j).score;
					sum = sum + (q/log2(i+1.0 + 1.0));
					//System.out.println(i+" "+j);
					//System.out.println("q =  "+q);
					//System.out.println(i+" "+Math.log(i+1+1.0));
					//System.out.println("sum =  "+sum);
					//System.out.println("1/ = "+(q/Math.log(1+i+1.0)));
					//System.out.println(i+" "+before.get(j).score);
					break;
				}
			}
		}
		return sum;
	}

	private double log2(double d) {
		// TODO Auto-generated method stub
		return Math.log(d)/Math.log(2.0);
	}

}
