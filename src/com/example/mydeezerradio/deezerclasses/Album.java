package com.example.mydeezerradio.deezerclasses;

import java.util.ArrayList;
import java.util.List;

/**
 * An album of an Artist. It contains a set of Tracks.
 * 
 * <pre>
 *      {
 *       "id": "936927",
 *       "title": "TRON Legacy: Reconfigured (2011)",
 *       "cover": "http://api.deezer.com/2.0/album/936927/image",
 *       "genre_id": "580",
 *       "nb_fan": "4335",
 *       "type": "album"
 *     }
 * </pre>
 * 
 * @author Deezer
 */
public class Album implements Thumbnailable {

	/** The list of tracks included in this album. */
	private List<Track> listTrack = new ArrayList<Track>();
	/** The id of the album. */
	private long id = -1;
	/** The name of the album. */
	private String title;
	/** The url of the music directory on Deezer for this album. */
	private String cover;

	/**
	 * Default constructor for Gson. Not needed syntactically speaking but
	 * present to emphasize its role in Gson use.
	 */
	public Album() {
	}// cons

	public long getId() {
		return id;
	}// met

	public void setId(long id) {
		this.id = id;
	}// met

	public String getTitle() {
		return title;
	}// met

	public void setTitle(String title) {
		this.title = title;
	}// met

	public String getCover() {
		return cover;
	}// met

	public void setCoverUrl(String cover) {
		this.cover = cover;
	}// met

	@Override
	public String getThumbnailUrl() {
		return cover;
	}// met

	public List<Track> getListTrack() {
		return listTrack;
	}// met

	public void setListTrack(List<Track> listTrack) {
		this.listTrack = listTrack;
	}// met

	@Override
	public int compareTo(Object another) {
		
		return 0;
	}
}// class
