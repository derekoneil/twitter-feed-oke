package com.example;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.stream.Stream;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ChunkedInput;
import org.glassfish.jersey.client.oauth1.AccessToken;
import org.glassfish.jersey.client.oauth1.ConsumerCredentials;
import org.glassfish.jersey.client.oauth1.OAuth1ClientSupport;
import org.glassfish.jersey.server.ChunkedOutput;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class SampleStreamExample {

	private static final String ENDPOINT = "https://stream.twitter.com/1.1/statuses/sample.json";
	private String consumerKey;
	private String consumerSecret;
	private String token;
	private String tokenSecret;
	private final ScriptEngine engine;
	private SampleStreamExample example;
	private final ChunkedOutput<String> output;

	public SampleStreamExample() {
		//System.setProperty("https.proxyHost", "adc-proxy.oracle.com");
		//System.setProperty("https.proxyPort", "80");
		output = new ChunkedOutput<String>(String.class);
		engine = new ScriptEngineManager().getEngineByName("javascript");
		StringBuilder sb = new StringBuilder(1024);
		//need to start from the classloader to get file when traversing classpath jars for resource
		URL fileurl = ClassLoader.getSystemResource("twitter-auth.json");
		requireNonNull(fileurl, "Could not find twitter-auth.json");
		//we ensure stream and underlying file closes using Java7 try w/ resources stmt
		try (Stream<String> stream = Files.lines(Paths.get(fileurl.toURI()), StandardCharsets.UTF_8)) {
			stream.forEach(sb::append);
			String script = "Java.asJSONCompatible(" + sb.toString() + ")"; //requires 8u60 or later
			Map<String, String> contents = (Map<String, String>) engine.eval(script);
			requireNonNull(contents, "Could not read contents of twitter-auth.json properly");
			consumerKey = contents.get("consumerKey");
			consumerSecret = contents.get("consumerSecret");
			token = contents.get("token");
			tokenSecret = contents.get("tokenSecret");
		} catch (URISyntaxException | IOException | ScriptException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
                
                
	}
	
	public void runStaticTwitterStream(final ChunkedOutput<String> output, final String queryString)
			throws IOException {
		StringBuilder sb = new StringBuilder(1024);
		StringBuilder sbOut = new StringBuilder(1024);
		URL fileurl = ClassLoader.getSystemResource("SampleTweets.json");
		requireNonNull(fileurl, "Could not find SampleTweets.json");
		// we ensure stream and underlying file closes using Java7 try w/
		// resources stmt

		int cnt = 0;

		try (Stream<String> stream = Files.lines(Paths.get(fileurl.toURI()), StandardCharsets.UTF_8)) {
			stream.forEach(sb::append);

			JsonParser parser = new JsonParser();
			JsonElement element = parser.parse(sb.toString());

			sbOut.append("{\"tweets\" : [");

			if (element.isJsonObject()) {
				JsonObject jsonObject = element.getAsJsonObject();
				JsonArray jsonArray = jsonObject.getAsJsonArray("tweets");

				for (int i = 0; i < jsonArray.size(); i++) {
					JsonObject tweet = jsonArray.get(i).getAsJsonObject();

					if (queryString == null) {

						if (cnt < 10) {
							System.out.println(tweet.toString());
						}
						if (cnt > 0) {
							sbOut.append(",\n");
						}
						sbOut.append(tweet.toString());
						cnt++;
						continue;
					}

					JsonElement text = tweet.get("text");

					// text == null on deleted tweets
					if (text == null) {
						continue;
					}

					if (tweet.toString().toUpperCase().contains(queryString.toUpperCase())) {

						if (cnt < 10) {

							System.out.println(tweet.toString());
						}
						if (cnt > 0) {
							sbOut.append(",\n");
						}
						sbOut.append(tweet.toString());
						cnt++;
						continue;
					}

					JsonObject entities = tweet.getAsJsonObject("entities");
					if (entities != null) {
						JsonArray hashtags = entities.getAsJsonArray("hashtags");
						for (int k = 0; hashtags != null && k < hashtags.size(); k++) {
							JsonObject hashtag = hashtags.get(k).getAsJsonObject();
							JsonElement hashtagText = hashtag.get("text");

							if (hashtagText != null) {

								if (hashtagText.toString().toUpperCase().contains(queryString.toUpperCase())) {
									if (cnt < 10) {
										System.out.println(tweet.toString());
									}
									if (cnt > 0) {
										sbOut.append(",\n");
									}
									sbOut.append(tweet.toString());
									cnt++;
									continue;
								}
							}
						}
					}
				}
			}
		} catch (URISyntaxException | IOException e) {
			e.printStackTrace();
			output.write("");
			;
		}

		System.out.println(cnt + " Tweets in StaticTweets");
		sbOut.append("]}");
		output.write(sbOut.toString());
	}
 

	public void runTwitterStream(final ChunkedOutput<String> output, final Integer i) throws IOException {
		final ConsumerCredentials consumerCredentials = new ConsumerCredentials(consumerKey, consumerSecret);
		final AccessToken storedToken = new AccessToken(token, tokenSecret);
		final Feature filterFeature = OAuth1ClientSupport
				.builder(consumerCredentials)
				.feature()
				.accessToken(storedToken)
				.build();
		// create a new Jersey client and register filter feature that will add OAuth signatures and
		// JacksonFeature that will process returned JSON data.
		final Client client = ClientBuilder.newBuilder()
				.register(filterFeature)
				.build();

		// make requests to protected resources
		// (no need to care about the OAuth signatures)
		final Response response = client.target(ENDPOINT).request().get();
		final ChunkedInput<String> chunkedInput = response.readEntity(new GenericType<ChunkedInput<String>>() {});
		if (response.getStatus() != 200) {
			String errorEntity = null;
			if (response.hasEntity()) {
				errorEntity = response.readEntity(String.class);
			}
			throw new RuntimeException("Request to Twitter was not successful. Response code: "
					+ response.getStatus() + ", reason: " + response.getStatusInfo().getReasonPhrase()
					+ ", entity: " + errorEntity);
		}
                output.write("{\"tweets\":[");
                // System.out.println("{\"tweets\":[");
		String chunk;
		final int max = i.intValue()-1;
		for (int msgRead = 0; msgRead < max; msgRead++) {
			if ((chunk = chunkedInput.read()) != null) {
				output.write(chunk + ",\n");
				// System.out.println("RAW MSG: " + chunk);
                                System.out.println(chunk + ",");
				//		Object result = engine.eval("Java.asJSONCompatible(" + msg + ")");
				//		assert (result instanceof Map);
				//		Map<String, Object> contents = (Map<String, Object>) result;
				//		System.out.print("PARSED: {");
				//		contents.forEach((t, u) -> {
				//			System.out.print("\"" + t + "\":\"" + u + "\",");
				//		});
				//		System.out.println("}");
			}
		}
		if ((chunk = chunkedInput.read()) != null) {
			output.write(chunk);	//we write the last line w/o the trailing comma
			System.out.println(chunk);

		}
		chunkedInput.close();
                output.write("]}");
                // System.out.println("]}");
		System.out.printf("The client read %d messages!\n", i);
	}

	public static void main(String[] args) throws IOException {
		SampleStreamExample example = new SampleStreamExample();
		example.runTwitterStream(example.output, 100);
	}
}
