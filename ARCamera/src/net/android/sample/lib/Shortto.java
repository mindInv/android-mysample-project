package net.android.sample.lib;


public class Shortto {
	private static final String SHORTTO_URL = "http://short.to/s.txt?url=";

	public static String getShortUrl(String url) {		
		try {
			String shortUrl = RestfulClient.Get(SHORTTO_URL + url, null);
			return shortUrl;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
