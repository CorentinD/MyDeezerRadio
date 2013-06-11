package com.example.mydeezerradio;

/**
 * A user in the simple Deezer API. This class will be serialized/deserialized
 * using gson json API in this format :
 * 
 * <pre>
 *      {
 *   		"id": "17861522",
 *   		"name": "steffn",
 *   		"link": "http://www.deezer.com/profile/17861522",
 *   		"picture": "http://api.deezer.com/2.0/user/17861522/image",
 *   		"country": "CA",
 *   		"type": "user"
 * }
 * </pre>
 * 
 * @author Deezer
 */
public class User implements Thumbnailable {
	/** The lastname of the user. */
	private String lastname;
	/** The firstname of the user. */
	private String firstname;
	/** The birthday of the user. */
	private String birthday;
	/** The picture url of the user. */
	private String picture;

	public String getLastname() {
		return lastname;
	}// met

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}// met

	public String getFirstname() {
		return firstname;
	}// met

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}// met

	public String getBirthday() {
		return birthday;
	}// met

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}// met

	public String getPicture() {
		return picture;
	}// met

	@Override
	public String getThumbnailUrl() {
		return picture;
	}// met

	public void setPicture(String picture) {
		this.picture = picture;
	}// met
}// class

