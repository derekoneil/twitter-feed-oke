package com.example;

import java.io.IOException;
import java.io.OutputStream;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.StreamingOutput;

import org.glassfish.jersey.server.ChunkedOutput;

/**
 * Root resource (exposed at "tweets" path)
 */
@Path("tweets")
public class TwitterService {

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
    	runTask(output, 10);
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
	@Path("{c}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getItWithCount(@PathParam("c") final Integer i) {
		if (i != null && i > 0 && i < 10000) {
	    	final ChunkedOutput<String> output = new ChunkedOutput<String>(String.class);
	    	runTask(output, i);
	    	return Response.ok()
	    			.entity(output)
	    			.header("Access-Control-Allow-Origin", "*")
	    			.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
	    			.build();
		}
		else {
	    	return Response.status(Status.BAD_REQUEST)
	    			.entity("You must enter a number between 1 and 10000")
	    			.header("Access-Control-Allow-Origin", "*")
	    			.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
	    			.build();
		}
	}
	
	private void runTask(ChunkedOutput<String> output, final Integer i) {
        new Thread(() -> {
            try {
            	example.runTwitterStream(output, i); 
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
