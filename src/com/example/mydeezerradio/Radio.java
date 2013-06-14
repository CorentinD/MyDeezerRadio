package com.example.mydeezerradio;
/**
 * A radio. It contains a set of Tracks. 
 * <pre>
{
  "id": "6",
  "title": "Electro",
  "description": "Electro",
  "picture": "http://api.deezer.com/2.0/radio/6/image",
  "type": "radio"
}
 * </pre>
 * @author Deezer
 */
public class Radio implements Thumbnailable {
	/** Id of the radio. */
	private long id;
	/** Title of the radio. */
	private String title;
	/** Thumbnail url of the radio. */
	private String picture;
	
	@Override
	public String getThumbnailUrl() {
		return picture;
	}//met

	public long getId() {
		return id;
	}//met

	public void setId(long id) {
		this.id = id;
	}//met

	public String getTitle() {
		return title;
	}//met

	public void setTitle(String title) {
		this.title = title;
	}//met

	public String getPicture() {
		return picture;
	}//met

	public void setPicture(String picture) {
		this.picture = picture;
	}//met

	@Override
	public int compareTo(Object another) {
		
		return 0;
	}
}//class
