package com.example.mydeezerradio.deezerclasses;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

/**
 * A reader of list of deezer objects.
 * 
 * @author Deezer
 * @param <T>
 *            can be either Track, Artist or Album.
 */
public class ListDeezerDataReader<T extends Object> {

	/** Tag used by Deezer json array structures. */
	private static final String DATA_JSON_TAG = "data";
	/** Class to pass to the Gson parser to create POJOs. */
	private Class<T> clazz = null;

	/**
	 * Creates a reader.
	 * 
	 * @param clazz
	 *            class to pass to the Gson parser to create POJOs.
	 * */
	public ListDeezerDataReader(Class<T> clazz) {
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
	public List<T> readList(String json) throws IllegalStateException {
		Gson gson = new Gson();
		JsonArray array = new JsonParser().parse(json).getAsJsonObject()
				.get(DATA_JSON_TAG).getAsJsonArray();
		List<T> result = new ArrayList<T>();

		for (int i = 0; i < array.size(); i++) {
			result.add(gson.fromJson(array.get(i), clazz));
		}// for
		return result;
	}// met

}// met
