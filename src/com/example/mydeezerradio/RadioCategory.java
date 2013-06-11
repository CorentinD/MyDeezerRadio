package com.example.mydeezerradio;

import java.util.List;

/**
 * A radio category / genre. It contains a set of radios. 
 * <pre>
 {
      "id": "2010",
      "title": "Partenaires",
      "radios": [
        {
          "id": "567",
          "title": "Les couleurs d'inter",
          "picture": "http://api.deezer.com/2.0/radio/567/image",
          "type": "radio"
        },
        {
          "id": "234",
          "title": "Inrocks",
          "picture": "http://api.deezer.com/2.0/radio/234/image",
          "type": "radio"
        },
      
      ]
    }
 * </pre>
 * @author Deezer
 */
public class RadioCategory {
	/** Id of the radio. */
	private long id;
	/** Title of the radio. */
	private String title;
	/** The lilst of radios. */
	private List<Radio> radios;
	
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

	public List<Radio> getRadios() {
		return radios;
	}//met

	public void setRadios(List<Radio> radios) {
		this.radios = radios;
	}//met
}//class
