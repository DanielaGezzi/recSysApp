package recSysApp.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;

import model.FacebookPage;
import model.Film;
import model.User;

public class RankingExec {

	public RankingExec() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public List<Film> rankFilms(List<Film> filmInputList, List<FacebookPage> facebookPageInputList){
		
		List<Film> resultList = new ArrayList<Film>();
		
		//per ogni film nella lista resultList calcolo la similarità con tutte le pagine
		//associate all'utente calcolando poi una media che rappresenterà quanto ogni film
		//si avvicina ai gusti dell'utente.
		
		Map<Film, Double> rankedFilms = new LinkedHashMap<Film, Double>();
		
		for(Film film : filmInputList) {
			int i = 0;
			double averageNum = 0;
			float[] floatFilmArray = ArrayUtils.toPrimitive(film.getVector().toArray(new Float[film.getVector().size()]), 0.0F);
			for(FacebookPage fbPage : facebookPageInputList) {
				float[] floatFilmfbPage = ArrayUtils.toPrimitive(fbPage.getVector().toArray(new Float[fbPage.getVector().size()]), 0.0F);
				averageNum +=  cosineSimilarity(floatFilmArray, floatFilmfbPage);
				i++;
			}
			rankedFilms.put(film,averageNum/i);
		}
		
		rankedFilms = rankedFilms.entrySet().stream().sorted(Entry.<Film,Double>comparingByValue().reversed())
						.collect(Collectors.toMap(Entry::getKey, Entry::getValue,
						(e1, e2) -> e1, LinkedHashMap::new));
		
		System.out.println(rankedFilms);
		resultList.addAll(rankedFilms.keySet());
		
		return resultList;
		
		
	}
	
	private double cosineSimilarity(float[] vectorA, float[] vectorB) {
	    double dotProduct = 0.0;
	    double normA = 0.0;
	    double normB = 0.0;
	    for (int i = 0; i < vectorA.length; i++) {
	        dotProduct += vectorA[i] * vectorB[i];
	        normA += Math.pow(vectorA[i], 2);
	        normB += Math.pow(vectorB[i], 2);
	    }   
	    return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
	}
}
