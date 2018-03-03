package model;

import java.util.List;

public class Film {
	
	private String id;
	private String title;
	private String genre;
	private List<String> filmingLocation;
	
	public Film() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Film(String id, String title, String genre, List<String> filmingLocation) {
		super();
		this.id = id;
		this.title = title;
		this.genre = genre;
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

	@Override
	public String toString() {
		return "Film [id=" + id + ", title=" + title + ", genre=" + genre + ", filmingLocation=" + filmingLocation
				+ "]";
	}
	
	
}
