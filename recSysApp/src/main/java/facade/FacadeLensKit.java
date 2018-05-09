package facade;

import java.util.List;
import java.util.Map;

public interface FacadeLensKit {
	
	void saveRatings(Long userId, Map<String,Double> imdbIdScoreList);
	List<String> getLogPopularityEntropyFilms();

}
