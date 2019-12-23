package com.example.wallet.sdk.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.wallet.sdk.model.enums.ResponseStatus;

/**
 * @author Masoud Taghavian (https://github.com/mtaghavian)
 */

public class BaseResponse {

	private String content, msg;
	private ResponseStatus status;

	public void setContent(String content) {
		this.content = content;
	}

	public String getContent() {
		return content;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getMsg() {
		return msg;
	}

	public void setStatus(ResponseStatus status) {
		this.status = status;
	}

	public ResponseStatus getStatus() {
		return status;
	}

	public String toJson() throws JSONException {
		JSONObject json = new JSONObject();
		json.put("content", content);
		json.put("msg", msg);
		json.put("status", "" + status);
		return json.toString();
	}

	public void fromJson(String json) throws JSONException {
		JSONObject reader = new JSONObject(json);
		if (reader.get("status") != null) {
			setStatus(ResponseStatus.valueOf(reader.getString("status")));
		} else {
			status = null;
		}
		if (!reader.isNull("content")) {
			if (reader.optJSONArray("content") != null) {
				setContent(reader.getJSONArray("content").toString());
			} else {
				setContent(reader.getJSONObject("content").toString());
			}
		} else {
			content = null;
		}
		if (reader.get("msg") != null) {
			setMsg(reader.getString("msg"));
		} else {
			msg = null;
		}
	}

}
