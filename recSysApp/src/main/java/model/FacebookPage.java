package model;

import java.util.List;

public class FacebookPage {
	
	private String id;
	private String name;
	private String category;
	private List<Float> vector;
	

	public FacebookPage(String id, String name, String category) {
		super();
		this.id = id;
		this.name = name;
		this.category = category;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
	
	public List<Float> getVector() {
		return vector;
	}

	public void setVector(List<Float> vector) {
		this.vector = vector;
	}

	@Override
	public String toString() {
		return "FacebookPage [id=" + id + ", name=" + name + ", category=" + category + ", vector=" + vector + "]";
	}



	
}
