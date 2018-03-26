package model;

import java.util.List;

public class FacebookPage {
	
	private String facebookID;
	private String name;
	private String category;
	private List<Float> vector;
	

	public FacebookPage(String facebookID, String name, String category) {
		super();
		this.facebookID = facebookID;
		this.name = name;
		this.category = category;
	}
	
	public String getFacebookID() {
		return facebookID;
	}
	public void setFacebookID(String facebookID) {
		this.facebookID = facebookID;
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
		return "FacebookPage [id=" + facebookID + ", name=" + name + ", category=" + category + ", vector=" + vector + "]";
	}



	
}
