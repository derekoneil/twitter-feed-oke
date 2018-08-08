package com.example;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;
import javax.json.stream.JsonGenerator;

/**
 * Root resource (exposed at "timeseries" path)
 */
@Path("timeseries")
public class TimeseriesService {
    
    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as an application/json response.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getIt() {
        JsonObject jolist = Json.createObjectBuilder()
        		.add("data", Json.createArrayBuilder()
        		.add(Json.createObjectBuilder()
                        .add("value", "1")
                        .add("label", "Dataset 1"))
        		.add(Json.createObjectBuilder()
                        .add("value", "2")
                        .add("label", "Dataset 2"))
        		.add(Json.createObjectBuilder()
                        .add("value", "3")
                        .add("label", "Dataset 3"))).build();
            Map<String, Boolean> config = new HashMap<>();
            config.put(JsonGenerator.PRETTY_PRINTING, true);
            JsonWriterFactory jwf = Json.createWriterFactory(config);
            StringWriter sw = new StringWriter();
            try (JsonWriter jsonWriter = jwf.createWriter(sw)) {
                jsonWriter.writeObject(jolist);
            } 
            catch (Exception e) {
            	e.printStackTrace();
            }
        	return Response.ok(sw.toString())
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
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    public Response getItWithCount(@PathParam("c") final Integer i) {
		JsonObject jsonResponse;
        Map<String, Boolean> config;
        JsonWriterFactory jwf;
        StringWriter sw;

		switch(i) {
		case 1:
	        jsonResponse = Json.createObjectBuilder()
            	.add("id", "1")
                .add("name", "Dataset 1")
    			.add("timeSeriesData", Json.createArrayBuilder()
    					.add(Json.createObjectBuilder()
    							.add("name", "Series 1")
    			    			.add("items", Json.createArrayBuilder()
    			    					.add("74")
    			    					.add("42")
    			    					.add("70")
    			    					.add("46")))
    					.add(Json.createObjectBuilder()
    							.add("name", "Series 2")
    			    			.add("items", Json.createArrayBuilder()
    			    					.add("50")
    			    					.add("58")
    			    					.add("46")
    			    					.add("54")))
    					.add(Json.createObjectBuilder()
    							.add("name", "Series 3")
    			    			.add("items", Json.createArrayBuilder()
    			    					.add("34")
    			    					.add("22")
    			    					.add("30")
    			    					.add("32")))
    					.add(Json.createObjectBuilder()
    							.add("name", "Series 4")
    			    			.add("items", Json.createArrayBuilder()
    			    					.add("18")
    			    					.add("6")
    			    					.add("14")
    			    					.add("22"))))
    			.add("timeSeriesGroups", Json.createArrayBuilder()
    					.add("Group A")
    					.add("Group B")
    					.add("Group C")
    					.add("Group D")).build();
	        config = new HashMap<>();
	        config.put(JsonGenerator.PRETTY_PRINTING, true);
	        jwf = Json.createWriterFactory(config);
	        sw = new StringWriter();
	        try (JsonWriter jsonWriter = jwf.createWriter(sw)) {
	            jsonWriter.writeObject(jsonResponse);
	        } 
	        catch (Exception e) {
	        	e.printStackTrace();
	        }
	    	return Response.ok()
	    			.entity(sw.toString())
	    			.header("Access-Control-Allow-Origin", "*")
	    			.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
	    			.build();
		case 2:
	        jsonResponse = Json.createObjectBuilder()
        	.add("id", "2")
            .add("name", "Dataset 2")
			.add("timeSeriesData", Json.createArrayBuilder()
					.add(Json.createObjectBuilder()
							.add("name", "Series 5")
			    			.add("items", Json.createArrayBuilder()
			    					.add("50")
			    					.add("58")
			    					.add("46")
			    					.add("54")))
					.add(Json.createObjectBuilder()
							.add("name", "Series 6")
			    			.add("items", Json.createArrayBuilder()
			    					.add("5")
			    					.add("88")
			    					.add("4")
			    					.add("6")))
					.add(Json.createObjectBuilder()
							.add("name", "Series 7")
			    			.add("items", Json.createArrayBuilder()
			    					.add("18")
			    					.add("48")
			    					.add("2")
			    					.add("9")))
					.add(Json.createObjectBuilder()
							.add("name", "Series 8")
			    			.add("items", Json.createArrayBuilder()
			    					.add("34")
			    					.add("22")
			    					.add("30")
			    					.add("32"))))
			.add("timeSeriesGroups", Json.createArrayBuilder()
					.add("Group E")
					.add("Group F")
					.add("Group G")
					.add("Group H")).build();
	        config = new HashMap<>();
	        config.put(JsonGenerator.PRETTY_PRINTING, true);
	        jwf = Json.createWriterFactory(config);
	        sw = new StringWriter();
	        try (JsonWriter jsonWriter = jwf.createWriter(sw)) {
	            jsonWriter.writeObject(jsonResponse);
	        } 
	        catch (Exception e) {
	        	e.printStackTrace();
	        }
	    	return Response.ok()
	    			.entity(sw.toString())
	    			.header("Access-Control-Allow-Origin", "*")
	    			.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
	    			.build();
		case 3:
	        jsonResponse = Json.createObjectBuilder()
        	.add("id", "3")
            .add("name", "Dataset 3")
			.add("timeSeriesData", Json.createArrayBuilder()
					.add(Json.createObjectBuilder()
							.add("name", "Series 9")
			    			.add("items", Json.createArrayBuilder()
			    					.add("32")
			    					.add("5")
			    					.add("64")
			    					.add("4")))
					.add(Json.createObjectBuilder()
							.add("name", "Series 10")
			    			.add("items", Json.createArrayBuilder()
			    					.add("45")
			    					.add("8")
			    					.add("44")
			    					.add("88")))
					.add(Json.createObjectBuilder()
							.add("name", "Series 11")
			    			.add("items", Json.createArrayBuilder()
			    					.add("12")
			    					.add("1")
			    					.add("88")
			    					.add("32")))
					.add(Json.createObjectBuilder()
							.add("name", "Series 12")
			    			.add("items", Json.createArrayBuilder()
			    					.add("1")
			    					.add("25")
			    					.add("3")
			    					.add("88"))))
			.add("timeSeriesGroups", Json.createArrayBuilder()
					.add("Group I")
					.add("Group J")
					.add("Group K")
					.add("Group L")).build();
	        config = new HashMap<>();
	        config.put(JsonGenerator.PRETTY_PRINTING, true);
	        jwf = Json.createWriterFactory(config);
	        sw = new StringWriter();
	        try (JsonWriter jsonWriter = jwf.createWriter(sw)) {
	            jsonWriter.writeObject(jsonResponse);
	        } 
	        catch (Exception e) {
	        	e.printStackTrace();
	        }
	    	return Response.ok()
	    			.entity(sw.toString())
	    			.header("Access-Control-Allow-Origin", "*")
	    			.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
	    			.build();
	    default:
	    	return Response.status(Status.BAD_REQUEST)
	    			.entity("You must enter a number between 1 and 3")
	    			.header("Access-Control-Allow-Origin", "*")
	    			.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
	    			.build();
		}
	}
}
