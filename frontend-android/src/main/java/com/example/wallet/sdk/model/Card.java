package com.example.wallet.sdk.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Masoud Taghavian (https://github.com/mtaghavian)
 */

public class Card {

	private String id, name, number;

	public void setId(String id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getNumber() {
		return number;
	}

	public String toJson() throws JSONException {
		JSONObject json = new JSONObject();
		json.put("id", id);
		json.put("name", name);
		json.put("number", number);
		return json.toString();
	}

	public void fromJson(String json) throws JSONException {
		JSONObject reader = new JSONObject(json);
		setId(reader.getString("id"));
		setName(reader.getString("name"));
		setNumber(reader.getString("number"));
	}
	
	public static void copy(Card from, Card to) {
        to.setId(from.getId());
        to.setName(from.getName());
        to.setNumber(from.getNumber());
    }
}
