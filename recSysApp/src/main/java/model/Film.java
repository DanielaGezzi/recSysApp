package model;

import java.util.List;

public class Film {
	
	private String imdbId;
	private String title;
	private String genre;
	private List<String> filmingLocation;
	private List<Float> vector;
	private double similarity;
	

	public Film() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Film(String title, List<String> filmingLocation) {
		super();
		this.title = title;
		this.filmingLocation = filmingLocation;
	}

	public Film(String imdbId, String title) {
		super();
		this.imdbId = imdbId;
		this.title = title;
	}

	public String getImdbId() {
		return imdbId;
	}

	public void setImdbId(String imdbId) {
		this.imdbId = imdbId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}

	public List<String> getFilmingLocation() {
		return filmingLocation;
	}

	public void setFilmingLocation(List<String> filmingLocation) {
		this.filmingLocation = filmingLocation;
	}

	public List<Float> getVector() {
		return vector;
	}

	public void setVector(List<Float> vector) {
		this.vector = vector;
	}
	
	public double getSimilarity() {
		return similarity;
	}

	public void setSimilarity(double similarity) {
		this.similarity = similarity;
	}
	
	@Override
	public String toString() {
		return "Film [imdbId=" + imdbId + ", title=" + title + ", genre=" + genre + ", filmingLocation="
				+ filmingLocation + ", vector=" + vector + ", similarity=" + similarity
				+ "]";
	}
	
	
}
