package com.example.mydeezerradio;

/**
 * A track in an Album.
 * 
 * @author Deezer
 */
public class Track {

	/** The id of the track. */
	private long id;
	/** The title of the track. */
	private String title;
	/** The url of the web page on Deezer for the track. */
	private String link;
	/** The duration of the track (in seconds). */
	private int duration;
	/**
	 * Url of an exerpt from the track, can be passed to the player. If user is
	 * using a premium deezer account, this has to be passed to the player.
	 */
	private String stream;
	/**
	 * Url of preview, can be passed to the player. If user is using a free
	 * deezer account, this has to be passed to the player.
	 */
	private String preview;
	/** The author of the track. */
	private Artist artist;
	/** The album of the track. */
	private Album album;

	public Album getAlbum() {
		return album;
	}

	public void setAlbum(Album album) {
		this.album = album;
	}

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

	public int getDuration() {
		return duration;
	}// met

	public void setDuration(int duration) {
		this.duration = duration;
	}// met

	public String getStream() {
		return stream;
	}// met

	public void setStream(String stream) {
		this.stream = stream;
	}// met

	@Override
	public String toString() {
		return title + " by " + artist;
	}// met

	public boolean hasStream() {
		return (stream != "false") && (stream != null);
	}// met

	public String getPreview() {
		return preview;
	}// met

	public void setPreview(String preview) {
		this.preview = preview;
	}// met

	public Artist getArtist() {
		return artist;
	}// met

	public void setArtist(Artist artist) {
		this.artist = artist;
	}// met

	public int compareTo(Object o) {
		return (int) (this.id - ((Track) o).id);
	}

}// class
