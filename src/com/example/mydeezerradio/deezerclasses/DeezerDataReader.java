package com.example.mydeezerradio.deezerclasses;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * A reader of a single deezer object.
 * 
 * @author Deezer
 * @param <T>
 *            can be either Track, Artist or Album or User.
 */
public class DeezerDataReader<T extends Object> {

	/** Class to pass to the Gson parser to create POJOs. */
	private Class<T> clazz = null;

	/**
	 * Creates a reader.
	 * 
	 * @param clazz
	 *            class to pass to the Gson parser to create POJOs.
	 * */
	public DeezerDataReader(Class<T> clazz) {
		if (clazz == null) {
			throw new IllegalArgumentException("Clazz can't be null.");
		}// if
		this.clazz = clazz;
	}// cons

	/**
	 * DAO method to read (deserialize) data from a json string.
	 * 
	 * @param json
	 *            the json string to deserialize.
	 * @return a list of typed data from Deezer. The list can't be null, but may
	 *         be empty.
	 * @throws IllegalStateException
	 *             if the parser encounters an error in json format.
	 */
	public T read(String json) throws IllegalStateException {
		Gson gson = new Gson();
		JsonObject object = new JsonParser().parse(json).getAsJsonObject();
		return gson.fromJson(object, clazz);
	}// met

}// met
