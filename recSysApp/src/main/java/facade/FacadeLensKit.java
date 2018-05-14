package facade;

import java.util.List;
import java.util.Map;

import model.Film;

public interface FacadeLensKit {
	
	void saveRatings(long userId, Map<String,String> imdbIdScoreList);
	List<String> getLogPopularityEntropyFilms();
	List<String> getRecommendations(long userId, int n, List<Film> filmList);

}
