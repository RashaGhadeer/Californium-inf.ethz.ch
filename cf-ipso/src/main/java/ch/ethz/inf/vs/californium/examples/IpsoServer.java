/*******************************************************************************
 * Copyright (c) 2012, Institute for Pervasive Computing, ETH Zurich.
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the Institute nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE INSTITUTE AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE INSTITUTE OR CONTRIBUTORS BE LIABLE
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
package ch.ethz.inf.vs.californium.examples;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;

import ch.ethz.inf.vs.californium.coap.LinkFormat;
import ch.ethz.inf.vs.californium.coap.POSTRequest;
import ch.ethz.inf.vs.californium.coap.Request;
import ch.ethz.inf.vs.californium.coap.Response;
import ch.ethz.inf.vs.californium.coap.registries.MediaTypeRegistry;
import ch.ethz.inf.vs.californium.endpoint.LocalEndpoint;
import ch.ethz.inf.vs.californium.endpoint.ServerEndpoint;
import ch.ethz.inf.vs.californium.endpoint.resources.LocalResource;
import ch.ethz.inf.vs.californium.examples.ipso.*;
import ch.ethz.inf.vs.californium.util.Log;
import ch.ethz.inf.vs.californium.util.Properties;

/**
 * The class IpsoServer provides an example of the IPSO Profile specification.
 * The server registers its resources at the SensiNode Resource Directory.
 * 
 * @author Matthias Kovatsch
 */
public class IpsoServer extends ServerEndpoint {

    // exit codes for runtime errors
    public static final int ERR_INIT_FAILED = 1;
    

    public IpsoServer(int port) throws SocketException {
    	super(port);
        
        // add resources to the server
        addResource(new DeviceName());
        addResource(new DeviceManufacturer());
        addResource(new DeviceModel());
        addResource(new DeviceSerial());
        addResource(new DeviceBattery());

        addResource(new PowerInstantaneous());
        addResource(new PowerCumulative());
        addResource(new PowerRelay());
        addResource(new PowerDimmer());
    }
    
    /**
     * Constructor for a new PlugtestServer. Call {@code super(...)} to configure
     * the port, etc. according to the {@link LocalEndpoint} constructors.
     * <p>
     * Add all initial {@link LocalResource}s here.
     */
    public IpsoServer() throws SocketException {
        this(Properties.std.getInt("DEFAULT_PORT"));
    }
    
    // Application entry point /////////////////////////////////////////////////
    
    public static void main(String[] args) {

        Log.setLevel(Level.INFO);
        Log.init();
        
        // create server
        try {
            
            LocalEndpoint server = new IpsoServer(); // give a custom port number here or edit Californium.properties
            
            server.start();
            
            System.out.printf(IpsoServer.class.getSimpleName()+" listening on port %d.\n", server.getPort());
            
            Request register = new POSTRequest() {
                    @Override
                    protected void handleResponse(Response response) {
                        // specific handling for this request
                        // here: response received, output a pretty-print
                        System.out.println("Successfully regeistered");
                        response.prettyPrint();
                    }
                };
                
            // RD location
            String rd = "coap://vs0.inf.ethz.ch:5683/rd";
            
            if (args.length>0 && args[0].startsWith("coap://")) {
                rd = args[0];
            } else {
                System.out.println("Hint: You can give the RD URI as first argument.");
                System.out.println("Fallback to ETH Zurich RD");
            }
                
            // Individual hostname
            String endpoint = Double.toString(Math.round(Math.random()*1000));
            if (args.length>1 && args[1].matches("\\w+")) {
                endpoint = args[1];
            } else {
                System.out.println("Hint: You can give an alphanumeric (plus '-' and '_') string as second argument to specify a custom hostname.");
                System.out.println("Fallback to hostname");
                try {
                    endpoint = InetAddress.getLocalHost().getHostName();
                } catch (UnknownHostException e1) {
                    System.out.println("Unable to retrieve hostname for registration");
                    System.out.println("Fallback to random");
                }
            }
            
            register.setURI(rd+"?ep=Cf-"+endpoint+"&lt=60");
            register.setPayload(LinkFormat.serialize(server.getRootResource(), null, true), MediaTypeRegistry.APPLICATION_LINK_FORMAT);

            // execute the request
            try {
                System.out.println("Registering at "+rd+" as Cf-"+endpoint);
                register.execute();
            } catch (Exception e) {
                System.err.println("Failed to execute request: " + e.getMessage());
                System.exit(ERR_INIT_FAILED);
            }
            
        } catch (SocketException e) {

            System.err.printf("Failed to create "+IpsoServer.class.getSimpleName()+": %s\n", e.getMessage());
            System.exit(ERR_INIT_FAILED);
        }
        
    }

}
