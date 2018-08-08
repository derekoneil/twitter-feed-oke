package com.example;

import java.io.IOException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.Charset;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import org.glassfish.jersey.server.ChunkedOutput;

/**
 * Root resource (exposed at "statictweets" path)
 */
@Path("statictweets")
public class StaticTweets {

    private static SampleStreamExample example = new SampleStreamExample();

    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as an application/json response.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getIt() {

    	final ChunkedOutput<String> output = new ChunkedOutput<String>(String.class);

    	runTask(output,null);
    	return Response.ok()
    			.entity(output)
    			.header("Access-Control-Allow-Origin", "*")
    			.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
    			.build();
    }

    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/json" media type.
     *
     * @return String that will be returned as an application/json response.
     */
    @Path("{search}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getItWithCount(@PathParam("search") String search) {
	    	System.out.println("Searching for tweets containing: " + search);
	    	final ChunkedOutput<String> output = new ChunkedOutput<String>(String.class);
	    	search = ""; //---Remove this line to enable searching
	    	runTask(output, search);
	    	return Response.ok()
	    			.entity(output)
	    			.header("Access-Control-Allow-Origin", "*")
	    			.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
	    			.build();


    }


	@Path("color")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getColor() throws IOException {
			System.out.println("Checking color");
			String color = "yellow";
			byte[] encoded = Files.readAllBytes(Paths.get("/tmp/labels"));
		  color = new String(encoded, Charset.defaultCharset());
			Pattern p = Pattern.compile("(?<=color=.)(\\w+)");
			Matcher m = p.matcher(color);

			if (m.find()) {
			    color = m.group(1);
			}

			return Response.ok()
					.entity(color)
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
					.build();


	}




	private void runTask(ChunkedOutput<String> output, String s) {
        new Thread(() -> {
            try {
            	example.runStaticTwitterStream(output, s);
            } catch (IOException e) {
				e.printStackTrace();
            } finally {
            	if (output != null) {
	            	try {
						output.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
            	}
            }
        }).start();
    	// the output will be probably returned even before
        // a first chunk is written by the new thread
	}


}
