package com.example.mydeezerradio;

//
//public class User {
//
//	// attributes
//	int id;
//	String name;
//	String lastname;
//	String firstname;
//	String email;
//	Date birthday;
//	Date inscription_date;
//	String gender;
//	String link;
//	String picture;
//	String country;
//	String lang;
//
//	// constructor
//	public User(int i, String n, String l, String f, String e, int d1, int m1,
//			int y1, int d2, int m2, int y2, String g, String li, String p,
//			String c, String la) {
//
//		id = i;
//		name = n;
//		lastname = l;
//		firstname = f;
//		email = e;
//		birthday = new Date(d1, m1, y1);
//		inscription_date = new Date(d2, m2, y2);
//		gender = g;
//		link = li;
//		picture = p;
//		country = c;
//		lang = la;
//
//	}
//
//	// methods
//
//	// inner class date :
//	public class Date {
//		// attributes
//		int day;
//		int month;
//		int year;
//
//		// constructor
//		public Date(int d, int m, int y) {
//			day = d;
//			month = m;
//			year = y;
//		}
//
//		// methods
//		/**
//		 * Return number of days between the 2 dates (365 days each year and 30
//		 * days each month)
//		 */
//		@SuppressWarnings("unused")
//		public int date_difference(Date autre) {
//
//			int nb_years = Math.abs(autre.year - this.year);
//			int nb_months = Math.abs(autre.month - this.month);
//			int nb_days = Math.abs(autre.day - this.day);
//
//			return (nb_days + 365 * nb_years + nb_months * 30);
//
//		}
//
//	}
//
//}

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

