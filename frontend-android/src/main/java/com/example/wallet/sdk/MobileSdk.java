package com.example.wallet.sdk;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.example.wallet.sdk.model.*;
import com.example.wallet.sdk.model.enums.ResponseStatus;
import com.scottyab.rootbeer.RootBeer;

/**
 * @author Masoud Taghavian (https://github.com/mtaghavian)
 */

public class MobileSdk {

	private Context context;
	private CustomHttpClient httpClient;
	private boolean checkOk = false;
	private static final String prefix = "https://192.168.43.210:62700";

	public void checkDevice(){
		RootBeer rootBeer = new RootBeer(context);
		if (rootBeer.isRooted()) {
			throw new NotOkResponseException("Application can not run on rooted devices");
		}else{
			checkOk = true;
		}
	}

	public MobileSdk(Context context) {
		httpClient = new CustomHttpClient(context, prefix);
		this.context = context;
	}

	public User signin(User user) throws IOException, URISyntaxException,
			JSONException {
		if(!checkOk){
			checkDevice();
		}
		String result = httpClient.post("/user/signin", user.toJson());
		BaseResponse baseResponse = new BaseResponse();
		baseResponse.fromJson(result);
		if (!baseResponse.getStatus().equals(ResponseStatus.Ok)) {
			throw new NotOkResponseException(baseResponse.getMsg());
		}
		User ru = new User();
		ru.fromJson(baseResponse.getContent());
		return ru;
	}

	public User signup(User user) throws IOException, URISyntaxException,
			JSONException {
		if(!checkOk){
			checkDevice();
		}
		String result = httpClient.post("/user/signup", user.toJson());
		BaseResponse baseResponse = new BaseResponse();
		baseResponse.fromJson(result);
		if (!baseResponse.getStatus().equals(ResponseStatus.Ok)) {
			throw new NotOkResponseException(baseResponse.getMsg());
		}
		User ru = new User();
		ru.fromJson(baseResponse.getContent());
		return ru;
	}

	public List<Card> listCards() throws IOException, URISyntaxException,
			JSONException {
		if(!checkOk){
			checkDevice();
		}
		String result = httpClient.get("/card/list");
		BaseResponse baseResponse = new BaseResponse();
		baseResponse.fromJson(result);
		if (!baseResponse.getStatus().equals(ResponseStatus.Ok)) {
			throw new NotOkResponseException(baseResponse.getMsg());
		}
		List<Card> list = new ArrayList<Card>();
		JSONArray array = new JSONObject(result).getJSONArray("content");
		for (int i = 0; i < array.length(); i++) {
			Card card = new Card();
			card.fromJson(array.getJSONObject(i).toString());
			list.add(card);
		}
		return list;
	}

	public void addCard(Card c) throws IOException, URISyntaxException,
			JSONException {
		if(!checkOk){
			checkDevice();
		}
		String result = httpClient.post("/card/create", c.toJson());
		BaseResponse baseResponse = new BaseResponse();
		baseResponse.fromJson(result);
		if (!baseResponse.getStatus().equals(ResponseStatus.Ok)) {
			throw new NotOkResponseException(baseResponse.getMsg());
		}
	}

	public void deleteCard(Card c) throws IOException, URISyntaxException,
			JSONException {
		if(!checkOk){
			checkDevice();
		}
		String result = httpClient.post("/card/delete", c.toJson());
		BaseResponse baseResponse = new BaseResponse();
		baseResponse.fromJson(result);
		if (!baseResponse.getStatus().equals(ResponseStatus.Ok)) {
			throw new NotOkResponseException(baseResponse.getMsg());
		}
	}

	public void editCard(Card c) throws IOException, URISyntaxException,
			JSONException {
		if(!checkOk){
			checkDevice();
		}
		String result = httpClient.post("/card/update", c.toJson());
		BaseResponse baseResponse = new BaseResponse();
		baseResponse.fromJson(result);
		if (!baseResponse.getStatus().equals(ResponseStatus.Ok)) {
			throw new NotOkResponseException(baseResponse.getMsg());
		}
	}

	public List<Transaction> listTransactions() throws IOException,
			URISyntaxException, JSONException {
		if(!checkOk){
			checkDevice();
		}
		String result = httpClient.get("/transaction/list");
		BaseResponse baseResponse = new BaseResponse();
		baseResponse.fromJson(result);
		if (!baseResponse.getStatus().equals(ResponseStatus.Ok)) {
			throw new NotOkResponseException(baseResponse.getMsg());
		}
		List<Transaction> list = new ArrayList<Transaction>();
		JSONArray array = new JSONObject(result).getJSONArray("content");
		for (int i = 0; i < array.length(); i++) {
			Transaction trans = new Transaction();
			trans.fromJson(array.getJSONObject(i).toString());
			list.add(trans);
		}
		return list;
	}

	public void addTransaction(Transaction t) throws IOException,
			URISyntaxException, JSONException {
		if(!checkOk){
			checkDevice();
		}
		String result = httpClient.post("/transaction/create", t.toJson());
		BaseResponse baseResponse = new BaseResponse();
		baseResponse.fromJson(result);
		if (!baseResponse.getStatus().equals(ResponseStatus.Ok)) {
			throw new NotOkResponseException(baseResponse.getMsg());
		}
	}

	public void cancelTransaction(Transaction t) throws IOException,
			URISyntaxException, JSONException {
		if(!checkOk){
			checkDevice();
		}
		String result = httpClient.post("/transaction/cancel", t.toJson());
		BaseResponse baseResponse = new BaseResponse();
		baseResponse.fromJson(result);
		if (!baseResponse.getStatus().equals(ResponseStatus.Ok)) {
			throw new NotOkResponseException(baseResponse.getMsg());
		}
	}

	public void performTransaction(Transaction t) throws IOException,
			URISyntaxException, JSONException {
		if(!checkOk){
			checkDevice();
		}
		String result = httpClient.post("/transaction/perform", t.toJson());
		BaseResponse baseResponse = new BaseResponse();
		baseResponse.fromJson(result);
		if (!baseResponse.getStatus().equals(ResponseStatus.Ok)) {
			throw new NotOkResponseException(baseResponse.getMsg());
		}
	}

	public void signout() {
		try {
			httpClient.get("/user/signout");
		} catch (IOException e) {
		} catch (URISyntaxException e) {
		}
	}
}
