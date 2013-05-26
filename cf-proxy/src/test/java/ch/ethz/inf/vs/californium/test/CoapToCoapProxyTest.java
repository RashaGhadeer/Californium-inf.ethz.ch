/*******************************************************************************
 * Copyright (c) 2012, Institute for Pervasive Computing, ETH Zurich.
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the Institute nor the names of its contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE INSTITUTE AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE INSTITUTE OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * 
 * This file is part of the Californium (Cf) CoAP framework.
 ******************************************************************************/

package ch.ethz.inf.vs.californium.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Calendar;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import ch.ethz.inf.vs.californium.coap.DELETERequest;
import ch.ethz.inf.vs.californium.coap.EndpointAddress;
import ch.ethz.inf.vs.californium.coap.GETRequest;
import ch.ethz.inf.vs.californium.coap.Option;
import ch.ethz.inf.vs.californium.coap.POSTRequest;
import ch.ethz.inf.vs.californium.coap.PUTRequest;
import ch.ethz.inf.vs.californium.coap.Request;
import ch.ethz.inf.vs.californium.coap.Response;
import ch.ethz.inf.vs.californium.coap.TokenManager;
import ch.ethz.inf.vs.californium.coap.registries.CodeRegistry;
import ch.ethz.inf.vs.californium.coap.registries.MediaTypeRegistry;
import ch.ethz.inf.vs.californium.coap.registries.OptionNumberRegistry;
import ch.ethz.inf.vs.californium.util.CoapTranslator;

/**
 * The Class CoapProxyTest.
 * Before to start this test, both server and proxy should be running.
 * 
 * @author Francesco Corazza
 */
public class CoapToCoapProxyTest {
    private static final boolean isStandAlone = true;
    
    private static final int PROXY_PORT = 5683;
    private static final int SERVER_PORT = 5684;
    
    private static final String PROXY_LOCATION = "coap://localhost:" + PROXY_PORT;
    private static final String SERVER_LOCATION = "coap://localhost:" + SERVER_PORT;
    
    private static final String SERVER_JAR_NAME = "cf-test-server-0.8.4-SNAPSHOT.jar";
    private static final String PROXY_JAR_NAME = "cf-proxy-0.8.4-SNAPSHOT.jar";
    
    private static Process SERVER_PROCESS;
    private static Process PROXY_PROCESS;
    
    /**
     * Sets the up before class.
     */
//    @BeforeClass
    public static void setUpBeforeClass() {
        if (isStandAlone) {
            try {
                // get the current path
                String path = new java.io.File("").getAbsolutePath();
                path = path.substring(0, path.lastIndexOf(File.separator));
                path = path + File.separator + "run" + File.separator;
                
                // Run the proxy in a separate system process
                ProcessBuilder processBuilder =
                                new ProcessBuilder("java", "-jar", path + PROXY_JAR_NAME,
                                                Integer.toString(PROXY_PORT));
                processBuilder.redirectErrorStream(true);
                PROXY_PROCESS = processBuilder.start();
                
                // Run the server in a separate system process
                processBuilder =
                                new ProcessBuilder("java", "-jar", path + SERVER_JAR_NAME,
                                                Integer.toString(SERVER_PORT));
                processBuilder.redirectErrorStream(true);
                SERVER_PROCESS = processBuilder.start();
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(-1);
            }
        }
    }
    
    /**
     * Tear down after class.
     */
//    @AfterClass
    public static void tearDownAfterClass() {
        if (isStandAlone && SERVER_PROCESS != null) {
            SERVER_PROCESS.destroy();
        }
        
        if (isStandAlone && PROXY_PROCESS != null) {
            PROXY_PROCESS.destroy();
        }
    }
    
    /**
     * Storage post delete test.
     */
//    @Test
    public final void deleteTest() {
        String postResource = "storage";
        String requestPayload = "subResource2";
        
        Request postRequest = new POSTRequest();
        postRequest.setPayload(requestPayload);
        postRequest.setContentType(MediaTypeRegistry.TEXT_PLAIN);
        
        Response postResponse = executeRequest(postRequest, postResource, true);
        assertNotNull(postResponse);
        assertEquals(postResponse.getCode(), CodeRegistry.RESP_CREATED);
        
        Request deleteRequest = new DELETERequest();
        Response deleteResponse =
                        executeRequest(deleteRequest, postResource + "/" + requestPayload, true);
        
        assertNotNull(deleteResponse);
        assertEquals(deleteResponse.getCode(), CodeRegistry.RESP_DELETED);
    }
    
//    @Test
    public final void getImageTest() {
        if (!isStandAlone) {
            String resource = "image";
            
            Request getRequest = new GETRequest();
            int acceptType = MediaTypeRegistry.IMAGE_PNG;
            getRequest.setAccept(acceptType);
            Response response = executeRequest(getRequest, resource, true);
            
            assertNotNull(response);
            assertEquals(response.getCode(), CodeRegistry.RESP_CONTENT);
            assertEquals(response.getContentType(), acceptType);
        }
    }
    
//    @Test
    public final void getLargeTest() {
        String resource = "large";
        
        Request getRequest = new GETRequest();
        Response response = executeRequest(getRequest, resource, true);
        
        assertNotNull(response);
        assertEquals(response.getCode(), CodeRegistry.RESP_CONTENT);
    }
    
//    @Test
    public final void getQueryTest() {
        String resource = "query?";
        String parameter0 = "a=1";
        String parameter1 = "b=2";
        
        Request getRequest = new GETRequest();
        Response response =
                        executeRequest(getRequest, resource + parameter0 + "&" + parameter1, true);
        
        assertNotNull(response);
        assertEquals(response.getCode(), CodeRegistry.RESP_CONTENT);
        String[] parameters = response.getPayloadString().split("\n");
        assertTrue(parameters[0].equalsIgnoreCase(parameter0)
                        && parameters[1].equalsIgnoreCase(parameter1));
    }
    
    /**
     * Hello world get test.
     */
//    @Test
    public final void getTest() {
        String resource = "helloWorld";
        
        Request getRequest = new GETRequest();
        Response response = executeRequest(getRequest, resource, true);
        
        assertNotNull(response);
        assertEquals(response.getCode(), CodeRegistry.RESP_CONTENT);
    }
    
    /**
     * Get the temperature.
     */
//    @Test
    public final void getTestExternal() {
        String resource = "seg1/seg2/seg3";
        
        Request getRequest = new GETRequest();
        Response response =
                        executeRequest(getRequest, resource, true, "coap://vs0.inf.ethz.ch:5683");
        
        assertNotNull(response);
        assertEquals(CodeRegistry.RESP_CONTENT, response.getCode());
    }
    
    /**
     * Local proxy resource test.
     */
//    @Test
    public final void localProxyResourceTest() {
        String resource = "stats/proxy";
        
        Request getRequest = new GETRequest();
        Response response = executeRequest(getRequest, resource, false);
        
        assertNotNull(response);
        assertEquals(response.getCode(), CodeRegistry.RESP_CONTENT);
    }
    
    /**
     * Long path get test.
     */
//    @Test
    public final void longPathGetTest() {
        String resource = "seg1/seg2/seg3";
        
        Request getRequest = new GETRequest();
        Response response = executeRequest(getRequest, resource, true);
        
        assertNotNull(response);
        assertEquals(response.getCode(), CodeRegistry.RESP_CONTENT);
    }
    
    /**
     * To upper post test.
     */
//    @Test
    public final void postTest() {
        String postResource = "toUpper";
        String requestPayload = "aaa";
        
        Request postRequest = new POSTRequest();
        postRequest.setPayload(requestPayload);
        postRequest.setContentType(MediaTypeRegistry.TEXT_PLAIN);
        
        Response response = executeRequest(postRequest, postResource, true);
        
        assertNotNull(response);
        assertEquals(response.getCode(), CodeRegistry.RESP_CONTENT);
        
        String responsePayload = response.getPayloadString();
        assertEquals(requestPayload.toUpperCase(), responsePayload);
    }
    
    /**
     * Storage post get test.
     */
//    @Test
    public final void postTest2() {
        String postResource = "storage";
        String requestPayload = Long.toString(Calendar.getInstance().getTimeInMillis());
        
        Request postRequest = new POSTRequest();
        postRequest.setPayload(requestPayload);
        postRequest.setContentType(MediaTypeRegistry.TEXT_PLAIN);
        
        Response postResponse = executeRequest(postRequest, postResource, true);
        assertNotNull(postResponse);
        assertEquals(postResponse.getCode(), CodeRegistry.RESP_CREATED);
        
        Request getRequest = new GETRequest();
        Response getResponse =
                        executeRequest(getRequest, postResource + "/" + requestPayload, true);
        
        assertNotNull(getResponse);
        assertEquals(getResponse.getCode(), CodeRegistry.RESP_CONTENT);
        
        String responsePayload = getResponse.getPayloadString();
        assertEquals(requestPayload, responsePayload);
    }
    
    /**
     * Storage put get test.
     */
//    @Test
    public final void putTest() {
        String putResource = "storage";
        String requestPayload = "aaa";
        
        Request putRequest = new PUTRequest();
        putRequest.setPayload(requestPayload);
        putRequest.setContentType(MediaTypeRegistry.TEXT_PLAIN);
        
        Response putResponse = executeRequest(putRequest, putResource, true);
        assertNotNull(putResponse);
        assertEquals(putResponse.getCode(), CodeRegistry.RESP_CHANGED);
        
        Request getRequest = new GETRequest();
        Response getResponse = executeRequest(getRequest, putResource, true);
        
        assertNotNull(getResponse);
        assertEquals(getResponse.getCode(), CodeRegistry.RESP_CONTENT);
        
        String responsePayload = getResponse.getPayloadString();
        assertEquals(requestPayload, responsePayload);
    }
    
//    @Test
    public final void separateTest() {
        // TODO
        String resource = "helloWorld";
        
        Request getRequest = new GETRequest();
        Response response = executeRequest(getRequest, resource, true);
        
        assertNotNull(response);
        assertEquals(response.getCode(), CodeRegistry.RESP_CONTENT);
    }
    
//    @Test
    public final void wrongAcceptContentGetTest() {
        String resource = "image";
        
        Request getRequest = new GETRequest();
        int acceptType = MediaTypeRegistry.VIDEO_RAW;
        getRequest.setAccept(acceptType);
        Response response = executeRequest(getRequest, resource, true);
        
        assertNotNull(response);
        assertEquals(response.getCode(), CodeRegistry.RESP_NOT_ACCEPTABLE);
    }
    
    /**
     * To upper post wrong content type test.
     */
//    @Test
    public final void wrongContentTypePostTest() {
        String postResource = "toUpper";
        String requestPayload = "aaa";
        
        Request postRequest = new POSTRequest();
        postRequest.setPayload(requestPayload.getBytes());
        postRequest.setContentType(MediaTypeRegistry.IMAGE_JPEG);
        
        Response response = executeRequest(postRequest, postResource, true);
        
        assertNotNull(response);
        assertEquals(response.getCode(), CodeRegistry.RESP_UNSUPPORTED_MEDIA_TYPE);
    }
    
    /**
     * Time resource delete fail test.
     */
//    @Test
    public final void wrongDeleteTest() {
        String resource = "image";
        
        Request getRequest = new DELETERequest();
        Response response = executeRequest(getRequest, resource, true);
        
        assertNotNull(response);
        assertEquals(response.getCode(), CodeRegistry.RESP_METHOD_NOT_ALLOWED);
    }
    
//    @Test
    public final void wrongGetQueryTest() {
        String resource = "query?";
        String parameter0 = "a=1";
        String parameter1 = "%";
        
        Request getRequest = new GETRequest();
        Response response =
                        executeRequest(getRequest, resource + parameter0 + "&" + parameter1, true);
        
        assertNotNull(response);
        // TODO check
        assertEquals(response.getCode(), CodeRegistry.RESP_BAD_OPTION);
    }
    
//    @Test
    public final void wrongGetTest() {
        String resource = "toUpper";
        
        Request getRequest = new GETRequest();
        Response response = executeRequest(getRequest, resource, true);
        
        assertNotNull(response);
        assertEquals(response.getCode(), CodeRegistry.RESP_METHOD_NOT_ALLOWED);
    }
    
//    @Test
    public final void wrongLocalResourceTest() {
        String resource = "inexistent";
        
        Request getRequest = new GETRequest();
        Response response = executeRequest(getRequest, resource, false);
        
        assertNotNull(response);
        assertEquals(response.getCode(), CodeRegistry.RESP_NOT_FOUND);
    }
    
//    @Test
    public final void wrongMalformedResourceTest() {
        String resource = " 53!\"£$%&/()=";
        
        Request getRequest = new GETRequest();
        Response response = executeRequest(getRequest, resource, true);
        
        assertNotNull(response);
        assertEquals(response.getCode(), CodeRegistry.RESP_BAD_OPTION);
    }
    
    /**
     * Time resource post fail test.
     */
//    @Test
    public final void wrongPostTest() {
        String resource = "helloWorld";
        
        Request getRequest = new POSTRequest();
        Response response = executeRequest(getRequest, resource, true);
        
        assertNotNull(response);
        assertEquals(response.getCode(), CodeRegistry.RESP_METHOD_NOT_ALLOWED);
    }
    
    /**
     * Time resource put fail test.
     */
//    @Test
    public final void wrongPutTest() {
        String resource = "helloWorld";
        
        Request getRequest = new PUTRequest();
        Response response = executeRequest(getRequest, resource, true);
        
        assertNotNull(response);
        assertEquals(response.getCode(), CodeRegistry.RESP_METHOD_NOT_ALLOWED);
    }
    
//    @Test
    public final void wrongResourceTest() {
        String resource = "inexistent";
        
        Request getRequest = new GETRequest();
        Response response = executeRequest(getRequest, resource, true);
        
        assertNotNull(response);
        assertEquals(response.getCode(), CodeRegistry.RESP_NOT_FOUND);
    }
    
//    @Test
    public final void wrongServerTest() {
        String resource = "resource";
        String coapServer = "coap://localhost:5685";
        
        Request getRequest = new GETRequest();
        Response response = executeRequest(getRequest, resource, true, coapServer);
        
        assertNotNull(response);
        assertEquals(response.getCode(), CoapTranslator.STATUS_TIMEOUT);
    }
    
    private Response executeRequest(Request request, String resource, boolean enableProxying) {
        return executeRequest(request, resource, enableProxying, SERVER_LOCATION);
    }
    
    /**
     * Execute request.
     * 
     * @param request the request
     * @param resource the resource
     * @param enableProxying the enable proxying
     * @return the response
     */
    private Response executeRequest(Request request, String resource, boolean enableProxying,
                    String serverLocation) {
        // String proxyResource = "proxy";
        
        // set the resource desired in the proxy-uri option or in the uri-path
        // depending if the proxying is enabled or not
        if (enableProxying) {
            Option proxyUriOption =
                            new Option(serverLocation + "/" + resource,
                                            OptionNumberRegistry.PROXY_URI);
            request.setOption(proxyUriOption);
            
            try {
                request.setPeerAddress(new EndpointAddress(new URI(PROXY_LOCATION)));
            } catch (URISyntaxException e) {
                System.err.println(e.getMessage());
            }
        } else {
            // else {
            // proxyResource = resource;
            // }
            
            request.setURI(PROXY_LOCATION + "/" + resource);
        }
        request.setToken(TokenManager.getInstance().acquireToken());
        
        // enable response queue for synchronous I/O
        request.enableResponseQueue(true);
        
        // execute the request
        try {
            request.execute();
        } catch (IOException e) {
            System.err.println("Failed to execute request: " + e.getMessage());
        }
        
        // receive response
        Response response = null;
        try {
            response = request.receiveResponse();
        } catch (InterruptedException e) {
            System.err.println("Receiving of response interrupted: " + e.getMessage());
        }
        
        return response;
    }
    
}
