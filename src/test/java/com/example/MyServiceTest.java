package com.example;

import static org.junit.Assert.assertNotNull;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.glassfish.grizzly.http.server.HttpServer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MyServiceTest {

    private HttpServer server;
    private WebTarget target;

    @Before
    public void setUp() throws Exception {
        // start the server
        server = Main.startServer();
        // create the client
        Client c = ClientBuilder.newClient();

        // uncomment the following line if you want to enable
        // support for JSON in the client (you also have to uncomment
        // dependency on jersey-media-json module in pom.xml and Main.startServer())
        // --
        // c.configuration().enable(new org.glassfish.jersey.media.json.JsonJaxbFeature());

        target = c.target(Main.BASE_URI);
    }

    @After
    public void tearDown() throws Exception {
        server.shutdown();
    }

    /**
     * Test to see that the message "Got it!" is sent in the response.
     */
     @Test
    public void testGetStaticTweets() {
        String responseMsg = target.path("statictweets").request().get(String.class);
        assertNotNull(responseMsg);
    } 
    /*
    @Test
    public void testGetStaticSearchTweets() {
        String responseMsg = target.path("statictweets/alpha").request().get(String.class);
        assertNotNull(responseMsg);
    } 
    */
    /*
    @Test
    public void testGetTweets() {
        String responseMsg = target.path("tweets").request().get(String.class);
        assertNotNull(responseMsg);
    }
    */
    
    @Test
    public void testGetTimeseriesIndex() {
        String responseMsg = target.path("timeseriesIndex").request().get(String.class);
        assertNotNull(responseMsg);
    }
    @Test
    public void testGetTimeseries() {
        String responseMsg = target.path("timeseries/1").request().get(String.class);
        assertNotNull(responseMsg);
    }
    
}
