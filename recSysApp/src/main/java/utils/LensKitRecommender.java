package utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.inject.Singleton;

import org.lenskit.LenskitConfiguration;
import org.lenskit.LenskitRecommender;
import org.lenskit.LenskitRecommenderEngine;
import org.lenskit.RecommenderConfigurationException;
import org.lenskit.data.dao.DataAccessObject;
import org.lenskit.data.dao.file.StaticDataSource;

import com.google.common.base.Throwables;

@Singleton
public class LensKitRecommender {
	
	private static LensKitRecommender instance = null;
	private LenskitRecommenderEngine engine = null;
	LenskitRecommender lkr;
			
	private LensKitRecommender() {	
		load();    
	}
	
	@SuppressWarnings("deprecation")
	public void load() {
		
		/*LenskitConfiguration config = new LenskitConfiguration();
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
		      .to(BaselineSubtractingUserVectorNormalizer.class);*/
		
		DataAccessObject dao;
		File file = new File(getClass().getClassLoader().getResource("movielens.yml").getFile());
		Path dataFile = Paths.get(file.getPath());
        try {
            StaticDataSource data = StaticDataSource.load(dataFile);
            // get the data from the DAO
            dao = data.get();
        } catch (IOException e) {
            System.out.println("cannot load data" + e);
            throw Throwables.propagate(e);
        }
        
        LenskitConfiguration dataConfig = new LenskitConfiguration();
        dataConfig.addComponent(dao);
        
        try {
        	
			this.engine = LenskitRecommenderEngine.newLoader()
			        			.addConfiguration(dataConfig)
			        			.load(new File("LKmodels/model20M.bin"));
			
		} catch (RecommenderConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        /*LenskitRecommenderEngine.newBuilder()
                							.addConfiguration(config)
                							.addConfiguration(dataConfig,
                											ModelDisposition.EXCLUDED)
                							.build();*/

        this.lkr = engine.createRecommender(dao);
	}
	
	public static synchronized LensKitRecommender getLensKitRecommender() {
		if (instance == null) {
			instance = new LensKitRecommender();
		}else {
		}
		return instance;
	}
	
	public LenskitRecommender getLkr() {
		return lkr;
	}
	
	public void setLkr(DataAccessObject dao) {
		this.lkr = engine.createRecommender(dao);

	}
	
	
	/*
	@SuppressWarnings("deprecation")
	public void TestCount(List<Film> films) throws IOException {
		File file = new File(getClass().getClassLoader().getResource("movielens.yml").getFile());
		Path dataFile = Paths.get(file.getPath());
		DataAccessObject dao;
        try {
            StaticDataSource data = StaticDataSource.load(dataFile);
            // get the data from the DAO
            dao = data.get();
        } catch (IOException e) {
            System.out.println("cannot load data" + e);
            throw Throwables.propagate(e);
        }
        Map<Film,Long> testMap = new LinkedHashMap<Film,Long>();
        for(Film film: films) {
	        List<Entity> movieData = dao.query(EntityType.forName("item-ids")).withAttribute(TypedName.create("imdbid", String.class), film.getImdbId().substring(2)).get();
	        if(!movieData.isEmpty()) {
	        	long id = (long) movieData.get(0).maybeGet("id");
	        	long count = dao.query(CommonTypes.RATING).withAttribute(CommonAttributes.ITEM_ID, id).count();
	        	System.out.format("\t%d [%s] [%s] (count): %d",id,film.getImdbId(),film.getTitle(),count);
	        	testMap.put(film, count);
	        }
	        else {
	        	System.out.format("\t%s %s is empty",film.getImdbId(), film.getTitle());
	        	testMap.put(film, (long) 0);
	        }
        }
            
        testMap = testMap.entrySet().stream().sorted(Entry.<Film,Long>comparingByValue().reversed())
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue,
				(e1, e2) -> e1, LinkedHashMap::new));
              
        FileWriter fileWriter = new FileWriter("test.txt");
        PrintWriter printWriter = new PrintWriter(fileWriter); 
        
        for( Film film :testMap.keySet()) {
        	System.out.format("\t[%s] %s COUNT: %d ", film.getImdbId(), film.getTitle(), testMap.get(film));
            printWriter.printf("\t[%s] %s COUNT: %d \r", film.getImdbId(), film.getTitle(), testMap.get(film));

        }
        printWriter.close();
		
	}
	*/

	
}
