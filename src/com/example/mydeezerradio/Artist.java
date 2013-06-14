package com.example.mydeezerradio;

import java.util.ArrayList;
import java.util.List;

/**
 * An artist in the simple Deezer API. This class will be
 * serialized/deserialized using gson json API in this format :
 * 
 * <pre>
 *       {
 *          "id":"13",
 *          "name":"Eminem",
 *          "picture":"http:\/\/api.deezer.com\/2.0\/artist\/13\/image",
 *          "type":"artist"
 *       }
 * </pre>
 * 
 * @author Deezer
 */
public class Artist implements Thumbnailable {

	/** The id of the artist. */
	private long id = -1;
	/** The name of the artist. */
	private String name;
	/** The url of a thumb icon for the artist. */
	private String picture;
	private long nb_fan;

	// @SerializedName("data")
	private List<Album> listAlbum = new ArrayList<Album>();

	public long getId() {
		return id;
	}// met

	public void setId(long id) {
		this.id = id;
	}// met

	public String getName() {
		return name;
	}// met

	public long getNbFan() {
		return nb_fan;
	}// met

	public void setName(String name) {
		this.name = name;
	}// met

	@Override
	public String getThumbnailUrl() {
		return picture;
	}// met

	public void setUrlImage(String urlImage) {
		this.picture = urlImage;
	}// met

	public List<Album> getListAlbum() {
		return listAlbum;
	}// met

	public void setListAlbum(List<Album> listAlbum) {
		this.listAlbum = listAlbum;
	}// met

	public void setNbFan(long fans) {
		this.nb_fan = fans;
	}// met

	@Override
	public int hashCode() {
		int result = (int) (id ^ (id >>> 32));
		return result;
	}// met

	@Override
	public String toString() {
		return name;
	}// met

	public int compareTo(Object other) {
		if (nb_fan > ((Artist) other).getNbFan()) {
			return -1;
		} else if (nb_fan < ((Artist) other).getNbFan()) {
			return 1;
		} else {
			return 0;
		}
	}

}// class
