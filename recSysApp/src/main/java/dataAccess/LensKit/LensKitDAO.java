package dataAccess.LensKit;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.lenskit.LenskitConfiguration;
import org.lenskit.LenskitRecommender;
import org.lenskit.LenskitRecommenderEngine;
import org.lenskit.api.ItemRecommender;
import org.lenskit.api.ItemScorer;
import org.lenskit.api.RecommenderBuildException;
import org.lenskit.api.Result;
import org.lenskit.api.ResultList;
import org.lenskit.baseline.BaselineScorer;
import org.lenskit.baseline.ItemMeanRatingItemScorer;
import org.lenskit.baseline.UserMeanBaseline;
import org.lenskit.baseline.UserMeanItemScorer;
import org.lenskit.data.dao.DataAccessObject;
import org.lenskit.data.dao.file.StaticDataSource;
import org.lenskit.data.entities.CommonAttributes;
import org.lenskit.data.entities.Entity;
import org.lenskit.data.entities.EntityType;
import org.lenskit.data.entities.TypedName;
import org.lenskit.data.ratings.Rating;
import org.lenskit.knn.item.ItemItemScorer;
import org.lenskit.transform.normalize.BaselineSubtractingUserVectorNormalizer;
import org.lenskit.transform.normalize.UserVectorNormalizer;
import org.lenskit.util.io.ObjectStream;

import com.google.common.base.Throwables;

import utils.CsvFileWriter;
import utils.LensKitRecommender;

public class LensKitDAO implements LensKitRepository {
	
	private DataAccessObject dao;

	@SuppressWarnings("deprecation")
	public void loadData() {
			
			File file = new File(getClass().getClassLoader().getResource("movielens.yml").getFile());
			Path dataFile = Paths.get(file.getPath());
	        try {
	            StaticDataSource data = StaticDataSource.load(dataFile);
	            // get the data from the DAO
	            this.dao = data.get();
	        } catch (IOException e) {
	            System.out.println("cannot load data" + e);
	            throw Throwables.propagate(e);
	        }
			
		}
	
	public void saveRating(Long userId, String imdbId, double rating, Long timestamp){
	    
        long movieId = -1;
		List<Entity> movieData = this.dao.query(EntityType.forName("item-ids")).withAttribute(TypedName.create("imdbid", String.class), imdbId).get();
		if(!movieData.isEmpty()) {
        	movieId = (long) movieData.get(0).maybeGet("id");
        }
		
		File csvFile = new File(getClass().getClassLoader().getResource("ratings.csv").getFile());
		Path csvDataFile = Paths.get(csvFile.getPath());
		CsvFileWriter.writeCsvFile(csvDataFile.toString(), userId + "," + movieId + "," + rating + "," + timestamp);
	}
	
	public List<String> getRecommendations(long userId, int n, List<String> imdbIdList) {
		List<String> recommendationList = new ArrayList<String>();
        Map<String, Double> mapFilmScore = new LinkedHashMap<String, Double>(); 
		
		LenskitConfiguration config = new LenskitConfiguration();
		// Use item-item CF to score items
		config.bind(ItemScorer.class).to(ItemItemScorer.class);
		// let's use personalized mean rating as the baseline/fallback predictor.
		// 2-step process:
		// First, use the user mean rating as the baseline scorer
		config.bind(BaselineScorer.class, ItemScorer.class)
		      .to(UserMeanItemScorer.class);
		// Second, use the item mean rating as the base for user means
		config.bind(UserMeanBaseline.class, ItemScorer.class)
		      .to(ItemMeanRatingItemScorer.class);
		// and normalize ratings by baseline prior to computing similarities
		config.bind(UserVectorNormalizer.class)
		      .to(BaselineSubtractingUserVectorNormalizer.class);
		
		LenskitRecommenderEngine engine = LenskitRecommenderEngine.build(config, this.dao);
        try (LenskitRecommender rec = engine.createRecommender(this.dao)) {
        	
    		Map<Long, String> temp = new HashMap<Long, String>();
        	for(String imdbid : imdbIdList) {
    	        List<Entity> movieData = this.dao.query(EntityType.forName("item-ids")).withAttribute(TypedName.create("imdbid", String.class), imdbid).get();
    	        if(!movieData.isEmpty()) {
    	        	long id = (long) movieData.get(0).maybeGet("id");
    	        	temp.put(id, imdbid);
    	        }
    		}	
        	
        	ItemRecommender irec = rec.getItemRecommender();
        	assert irec != null; // not null because we configured one
        	ResultList recs = irec.recommendWithDetails(userId, n, temp.keySet(), null);
        	//System.out.format("Recommendations for %d:\n", userId);
            for (Result item : recs) {   
                mapFilmScore.put(temp.get(item.getId()), item.getScore());
                //System.out.format("\t%s -----> %.2f\n", temp.get(item.getId()), item.getScore());
            }
		} catch (RecommenderBuildException e) {
			e.printStackTrace();
		}
        
		recommendationList.addAll(mapFilmScore.keySet());
		return recommendationList;
        
	}
	
	public List<String> getLogPopularityEntropyFilms() {
		List<String> orderedFilmList = new ArrayList<String>();        
        Map<String, Double> filmLogPopEntr = new LinkedHashMap<String, Double>(); 
		try (ObjectStream<Entity> movies = this.dao.query(EntityType.forName("item-ids")).stream()) {
			for(Entity e: movies) {
				double logPopEntr = getLogPopularityEntropyScore((Long) e.maybeGet("id"));
				filmLogPopEntr.put(e.maybeGet("imdbid").toString(), logPopEntr);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}				
		filmLogPopEntr = filmLogPopEntr.entrySet().stream().sorted(Entry.<String,Double>comparingByValue().reversed())
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue,
				(e1, e2) -> e1, LinkedHashMap::new));
		
		orderedFilmList.addAll(filmLogPopEntr.keySet());
		return orderedFilmList;        
	}
	
	public double getLogPopularityEntropyScore(Long movieId) {
		double entropy = 0;
		int popularity = this.dao.query(Rating.class).withAttribute(CommonAttributes.ITEM_ID, movieId).count(); //n of user who rated film
		Map<Double, Integer> ratingValuesFrequency = new HashMap<Double, Integer>();
		List<Rating> ratings = this.dao.query(Rating.class).withAttribute(CommonAttributes.ITEM_ID, movieId).get();
		for (Rating r: ratings) {
			if(r.getItemId() == movieId) {
				if(!ratingValuesFrequency.containsKey(r.getValue())) {
					ratingValuesFrequency.put((double) r.getValue(), 1);
				}else {
					ratingValuesFrequency.put((double) r.getValue(), ratingValuesFrequency.get(r.getValue()) + 1);
				}
			}
		}
		if(!ratingValuesFrequency.isEmpty()) {
			for(double value: ratingValuesFrequency.keySet()) {
				double proportion = value/popularity;
				entropy += proportion*Math.log(proportion);
			}
		}
		if(popularity != 0 & entropy != 0) {		
			entropy = - entropy;
			return (Math.log(popularity) * entropy);
		}
		
		return 0;
	}
	
	public double getEntropyScore(Long movieId) {
		double entropy = 0;
		Map<Double, Integer> ratingValuesFrequency = new HashMap<Double, Integer>();
		int numberOfUsers = 0; //n of user who rated film		
		List<Rating> ratings = this.dao.query(Rating.class).withAttribute(CommonAttributes.ITEM_ID, movieId).get();
		for (Rating r: ratings) {
			if(r.getItemId() == movieId) {
				numberOfUsers = numberOfUsers + 1;
				if(!ratingValuesFrequency.containsKey(r.getValue())) {
					ratingValuesFrequency.put((double) r.getValue(), 1);
				}else {
					ratingValuesFrequency.put((double) r.getValue(), ratingValuesFrequency.get(r.getValue()) + 1);
				}				
			}
		}
		if(!ratingValuesFrequency.isEmpty()) {
			for(double value: ratingValuesFrequency.keySet()) {
				double proportion = value/numberOfUsers;
				entropy += proportion*Math.log(proportion);
			}
		}else {
			
		}	
		return - entropy;
	}
	
	public double getPopularityScore(Long movieId) {
		//number of ratings for a film
		return this.dao.query(Rating.class).withAttribute(CommonAttributes.ITEM_ID, movieId).count();
	}

	public List<String> getRecommendationsTest(long userId, int n, List<String> imdbIdList) {
		List<String> recommendationList = new ArrayList<String>();
        Map<String, Double> mapFilmScore = new LinkedHashMap<String, Double>(); 
        
		LensKitRecommender lkr = LensKitRecommender.getLensKitRecommender();
        try (LenskitRecommender rec = lkr.getLkr()) {
        	
    		Map<Long, String> temp = new HashMap<Long, String>();
        	for(String imdbid : imdbIdList) {
    	        List<Entity> movieData = this.dao.query(EntityType.forName("item-ids")).withAttribute(TypedName.create("imdbid", String.class), imdbid).get();
    	        if(!movieData.isEmpty()) {
    	        	long id = (long) movieData.get(0).maybeGet("id");
    	        	temp.put(id, imdbid);
    	        }
    		}	
        	
        	ItemRecommender irec = rec.getItemRecommender();
        	assert irec != null; // not null because we configured one
        	ResultList recs = irec.recommendWithDetails(userId, n, temp.keySet(), null);
        	//System.out.format("Recommendations for %d:\n", userId);
            for (Result item : recs) {   
                mapFilmScore.put(temp.get(item.getId()), item.getScore());
                //System.out.format("\t%s -----> %.2f\n", temp.get(item.getId()), item.getScore());
            }
		} catch (RecommenderBuildException e) {
			e.printStackTrace();
		}
        
        recommendationList.addAll(mapFilmScore.keySet());
		return recommendationList;
	
	}
	
}
	/*	
	public double getEntropyZero(Long id, DataAccessObject dao) {
		double entropy = 0;
		Map<Double, Integer> ratingValuesFrequency = new HashMap<Double, Integer>();
		int numberOfUsers = 0; //n of user who rated film		
		List<Rating> ratings = dao.query(Rating.class).withAttribute(CommonAttributes.ITEM_ID, id).get();
		for (Rating r: ratings) {
			if(r.getItemId() == id) {
				numberOfUsers = numberOfUsers + 1;
				if(!ratingValuesFrequency.containsKey(r.getValue())) {
					ratingValuesFrequency.put((double) r.getValue(), 1);
				}else {
					ratingValuesFrequency.put((double) r.getValue(), ratingValuesFrequency.get(r.getValue()) + 1);
				}				
			}
		}
		if(!ratingValuesFrequency.isEmpty()) {
			for(double value: ratingValuesFrequency.keySet()) {
				double proportion = value/numberOfUsers;
				entropy += proportion*Math.log(proportion);
			}
		}	
		return - entropy;
	}*/
	
	/*old one
	public double getEntropyOld(Long id, DataAccessObject dao) {
		double entropy = 0;
		int numberOfUsers = 0; //n of user who rated film
		try (ObjectStream<Rating> ratings = dao.query(Rating.class).stream()) {
			Map<Double, Integer> ratingValuesFrequency = new HashMap<Double, Integer>();
			for (Rating r: ratings) {
				if(r.getItemId() == id) {
					numberOfUsers = numberOfUsers + 1;
					if(!ratingValuesFrequency.containsKey(r.getValue())) {
						ratingValuesFrequency.put((double) r.getValue(), 1);
					}else {
						ratingValuesFrequency.put((double) r.getValue(), ratingValuesFrequency.get(r.getValue()) + 1);
					}
					
				}
			}
			for(double value: ratingValuesFrequency.keySet()) {
				double proportion = value/numberOfUsers;
				entropy += proportion*Math.log(proportion);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		double popularity = dao.query(Rating.class).withAttribute(CommonAttributes.ITEM_ID, id).count();
		System.out.println(popularity == numberOfUsers);
		return - entropy;
	}
	
		public void getRatingsTest(long userId) {
		loadData();
		List<Rating> ratings = this.dao.query(Rating.class).withAttribute(CommonAttributes.USER_ID, userId).get();
		if(ratings.isEmpty()) {
			System.out.println("EMPTY");
			}else {
			for(Rating r : ratings) {
				System.out.println(r.getItemId() + " --- " + r.getValue());
			}
		}
	}
	*/
	


