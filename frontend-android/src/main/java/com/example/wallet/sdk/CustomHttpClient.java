package com.example.wallet.sdk;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyStore;
import java.util.concurrent.locks.ReentrantLock;

import javax.net.ssl.HostnameVerifier;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;

import android.content.Context;

import com.example.wallet.util.StreamUtils;

/**
 * @author Masoud Taghavian (https://github.com/mtaghavian)
 */

public class CustomHttpClient extends DefaultHttpClient {

	private final Context context;
	private final ReentrantLock lock;
	private String prefix;

	public CustomHttpClient(Context context, String prefix) {
		this.context = context;
		this.prefix = prefix;
		lock = new ReentrantLock();
	}

	@Override
	protected ClientConnectionManager createClientConnectionManager() {
		SchemeRegistry registry = new SchemeRegistry();
		registry.register(new Scheme("http", PlainSocketFactory
				.getSocketFactory(), 80));
		registry.register(new Scheme("https", newSslSocketFactory(), 443));
		return new SingleClientConnManager(getParams(), registry);
	}

	private SSLSocketFactory newSslSocketFactory() {
		try {
			KeyStore trusted = KeyStore.getInstance("BKS");
			InputStream in = context.getAssets().open("mystore.bks");
			try {
				trusted.load(in, "ez24get".toCharArray());
			} finally {
				in.close();
			}
			SSLSocketFactory sslSocketFactory = new SSLSocketFactory(trusted);
			HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
			sslSocketFactory
					.setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);
			return sslSocketFactory;
		} catch (Exception e) {
			throw new AssertionError(e);
		}
	}

	public String get(String url) throws IOException, URISyntaxException {
		lock.lock();
		try {
			HttpGet request = new HttpGet();
			request.setURI(new URI(prefix + url));
			HttpResponse response = execute(request);
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			StreamUtils.copy(response.getEntity().getContent(), os, true, true);
			return new String(os.toByteArray(), "UTF-8");
		} finally {
			lock.unlock();
		}
	}

	public String post(String url, String msg) throws IOException,
			URISyntaxException {
		lock.lock();
		try {
			HttpPost request = new HttpPost();
			request.setHeader("Content-Type", "application/json");
			request.setURI(new URI(prefix + url));
			BasicHttpEntity entity = new BasicHttpEntity();
			request.setEntity(entity);
			entity.setContent(new ByteArrayInputStream(msg.getBytes("UTF-8")));
			HttpResponse response = execute(request);
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			StreamUtils.copy(response.getEntity().getContent(), os, true, true);
			return new String(os.toByteArray(), "UTF-8");
		} finally {
			lock.unlock();
		}
	}

}