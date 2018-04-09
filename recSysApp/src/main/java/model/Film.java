package model;

import java.util.List;

public class Film {
	
	private String id;
	private String title;
	private String genre;
	private List<String> filmingLocation;
	private List<Float> vector;
	
	public Film() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Film(String title, List<String> filmingLocation) {
		super();
		this.title = title;
		this.filmingLocation = filmingLocation;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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
	
	@Override
	public String toString() {
		return "Film [id=" + id + ", title=" + title + ", genre=" + genre + ", filmingLocation=" + filmingLocation
				+ "]";
	}
	
}
