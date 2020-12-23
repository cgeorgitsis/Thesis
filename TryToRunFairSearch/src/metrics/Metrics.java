package metrics;
import java.util.List;

import org.apache.lucene.search.ScoreDoc;
public interface Metrics {
	public double compute(List<ScoreDoc> before, List<ScoreDoc> after);
}
