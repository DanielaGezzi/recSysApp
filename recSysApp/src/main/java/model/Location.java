package model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Location {
	
	String name;
	String city;
	String state;
	String country;
	String latitude;
	String longitude;
	
	public Location() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Location(String name, String city, String state, String country, String latitude, String longitude) {
		super();
		this.name = name;
		this.city = city;
		this.state = state;
		this.country = country;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	@Override
	public String toString() {
		return "Location [name=" + name + ", city=" + city + ", state=" + state + ", country=" + country + ", latitude="
				+ latitude + ", longitude=" + longitude + "]";
	}
	
	

}
