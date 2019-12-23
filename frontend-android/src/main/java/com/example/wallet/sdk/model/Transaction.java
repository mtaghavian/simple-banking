package com.example.wallet.sdk.model;

import org.json.JSONException;
import org.json.JSONObject;
import com.example.wallet.sdk.model.enums.TransactionStatus;

/**
 * @author Masoud Taghavian (https://github.com/mtaghavian)
 */

public class Transaction {

	private String sourceCard, targetCard, password;
	private Double amount;

	private String targetName, msg;
	private TransactionStatus status;
	private String id;
	private Long date;
	private Boolean canceled;

	public void setCanceled(Boolean canceled) {
		this.canceled = canceled;
	}

	public Boolean getCanceled() {
		return canceled;
	}

	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}

	public String getTargetName() {
		return targetName;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public void setDate(Long date) {
		this.date = date;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setSourceCard(String sourceCard) {
		this.sourceCard = sourceCard;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setStatus(TransactionStatus status) {
		this.status = status;
	}

	public void setTargetCard(String targetCard) {
		this.targetCard = targetCard;
	}

	public Double getAmount() {
		return amount;
	}

	public Long getDate() {
		return date;
	}

	public String getMsg() {
		return msg;
	}

	public String getPassword() {
		return password;
	}

	public String getSourceCard() {
		return sourceCard;
	}

	public TransactionStatus getStatus() {
		return status;
	}

	public String getTargetCard() {
		return targetCard;
	}

	public String getId() {
		return id;
	}

	public String toJson() throws JSONException {
		JSONObject json = new JSONObject();
		json.put("id", id);
		json.put("sourceCard", sourceCard);
		json.put("targetCard", targetCard);
		json.put("msg", msg);
		json.put("password", password);
		json.put("date", date);
		json.put("amount", amount);
		json.put("status", status);
		json.put("targetName", targetName);
		json.put("canceled", canceled);
		return json.toString();
	}

	public void fromJson(String json) throws JSONException {
		JSONObject reader = new JSONObject(json);
		setId(reader.getString("id"));
		setSourceCard(reader.getString("sourceCard"));
		setTargetCard(reader.getString("targetCard"));
		if (reader.isNull("msg")) {
			setMsg(null);
		} else {
			setMsg(reader.getString("msg"));
		}
		if (reader.isNull("password")) {
			setPassword(null);
		} else {
			setPassword(reader.getString("password"));
		}
		setDate(reader.getLong("date"));
		setAmount(reader.getDouble("amount"));
		setStatus(TransactionStatus.valueOf(reader.getString("status")));
		if (reader.isNull("targetName")) {
			setTargetName(null);
		} else {
			setTargetName(reader.getString("targetName"));
		}
		setCanceled(reader.getBoolean("canceled"));
	}

	public static void copy(Transaction from, Transaction to) {
		to.setId(from.getId());
		to.setSourceCard(from.getSourceCard());
		to.setTargetCard(from.getTargetCard());
		to.setMsg(from.getMsg());
		to.setPassword(from.getPassword());
		to.setDate(from.getDate());
		to.setAmount(from.getAmount());
		to.setStatus(from.getStatus());
		to.setTargetName(from.getTargetName());
		to.setCanceled(from.getCanceled());
	}
}
