package model;


public class User {

	private String id;
	private String facebookID;
	private String name;
	private String surname;
	
	public User() {
		super();	
	}
	
	public User(String facebookID, String name, String surname) {
		super();
		this.facebookID = facebookID;
		this.name = name;
		this.surname = surname;
	}
	
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getFacebookID() {
		return facebookID;
	}
	public void setFacebookID(String facebookID) {
		this.facebookID = facebookID;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", facebookID=" + facebookID + ", name=" + name + ", surname=" + surname + "]";
	}


	
}
