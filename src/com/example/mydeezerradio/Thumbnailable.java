package com.example.mydeezerradio;

/**
 * An interface used to declare the common behavior of data structures that use
 * thumbnails (namely Artist and Album). It will allow polymorphism inside the
 * model to retrieve thumbnails images for both data types.
 * 
 * @author Deezer
 */
public interface Thumbnailable extends Comparable{

	/** @return the http url of the thumbnail associated to this data. */
	public String getThumbnailUrl();
}// interface
