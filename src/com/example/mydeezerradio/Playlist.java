package com.example.mydeezerradio;

import java.util.List;

import android.util.Log;

/**
 * A playlist in the simple Deezer API. This class will be
 * serialized/deserialized using gson json API in this format :
 * 
 * <pre>
 *     {
 *       "id": "64087426",
 *       "title": "classik",
 *       "link": "http://www.deezer.com/music/playlist/64087426",
 *       "picture": "http://api.deezer.com/2.0/playlist/64087426/image",
 *       "creator": {
 *         "id": "17861542"
 *       },
 *       "type": "playlist"
 *     }
 * </pre>
 * 
 * @author Deezer
 */
public class Playlist implements Thumbnailable {
	/** Id of the playlist. */
	private long id;
	/** Title of the playlist. */
	private String title;
	/** The link on Deezer of the playlist. */
	private String link;
	/** Thumbnail url of the playlist. */
	private String picture;
	/** If the playlist is the love tracks playlist */
	private boolean is_loved_track;
	/** list of track */
	private List<Track> tracks;

	public boolean isIs_loved_track() {
		return is_loved_track;
	}

	public void setIs_loved_track(boolean is_loved_track) {
		this.is_loved_track = is_loved_track;
	}

	public List<Track> getTracks() {
		return tracks;
	}

	public void setTracks(List<Track> tracks) {
		this.tracks = tracks;
		Log.i("Playlist / setTracks", "tracks set : " + tracks);
	}

	@Override
	public String getThumbnailUrl() {
		return picture;
	}// met

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

	public String getLink() {
		return link;
	}// met

	public void setLink(String link) {
		this.link = link;
	}// met

	public String getPicture() {
		return picture;
	}// met

	public void setPicture(String picture) {
		this.picture = picture;
	}// met

	@Override
	public int compareTo(Object another) {
		// TODO Auto-generated method stub
		return 0;
	}
}// class
