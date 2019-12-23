package com.example.wallet.sdk.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Masoud Taghavian (https://github.com/mtaghavian)
 */

public class User {

	private String id, firstname, lastname, username, password;

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFirstname() {
		return firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String toJson() throws JSONException {
		JSONObject json = new JSONObject();
		json.put("id", id);
		json.put("firstname", firstname);
		json.put("lastname", lastname);
		json.put("username", username);
		json.put("password", password);
		return json.toString();
	}

	public void fromJson(String json) throws JSONException {
		JSONObject reader = new JSONObject(json);
		setId(reader.getString("id"));
		setFirstname(reader.getString("firstname"));
		setLastname(reader.getString("lastname"));
		setUsername(reader.getString("username"));
		setPassword(reader.getString("password"));
	}
}
