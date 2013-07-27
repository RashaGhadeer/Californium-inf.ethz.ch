/*******************************************************************************
 * Copyright (c) 2013, Institute for Pervasive Computing, ETH Zurich.
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

import java.net.SocketException;
import java.util.logging.Level;

import ch.ethz.inf.vs.californium.coap.Request;
import ch.ethz.inf.vs.californium.coap.Response;
import ch.ethz.inf.vs.californium.endpoint.LocalEndpoint;
import ch.ethz.inf.vs.californium.endpoint.ServerEndpoint;
import ch.ethz.inf.vs.californium.endpoint.resources.LocalResource;
import ch.ethz.inf.vs.californium.endpoint.resources.RDLookUpTopResource;
import ch.ethz.inf.vs.californium.endpoint.resources.RDResource;
import ch.ethz.inf.vs.californium.endpoint.resources.RDTagTopResource;
import ch.ethz.inf.vs.californium.util.Log;

/**
 * The class ResourceDirectory provides an experimental RD
 * as described in draft-shelby-core-resource-directory-05.
 * 
 * @author Matthias Kovatsch
 */
public class ResourceDirectory extends ServerEndpoint {
    
    // exit codes for runtime errors
    public static final int ERR_INIT_FAILED = 1;
    
    public static void main(String[] args) {
        
        Log.setLevel(Level.FINER);
    	Log.init();
        
        // create server
        try {
            
            LocalEndpoint server = new ResourceDirectory();
            server.start();
            
            System.out.printf(ResourceDirectory.class.getSimpleName()+" listening on port %d.\n", server.getPort());
            
        } catch (SocketException e) {
            
            System.err.printf("Failed to create "+ResourceDirectory.class.getSimpleName()+": %s\n", e.getMessage());
            System.exit(ERR_INIT_FAILED);
        }
        
    }
    
    // Logging /////////////////////////////////////////////////////////////////
    
    /**
     * Constructor for a new ResourceDirectory. Call {@code super(...)} to configure
     * the port, etc. according to the {@link LocalEndpoint} constructors.
     * <p>
     * Add all initial {@link LocalResource}s here.
     */
    public ResourceDirectory() throws SocketException {
        
        RDResource rdResource = new RDResource(); 

        // add resources to the server
		addResource(rdResource);
		addResource(new RDLookUpTopResource(rdResource));
		addResource(new RDTagTopResource(rdResource));
    }
    
    
    // Application entry point /////////////////////////////////////////////////
    
    @Override
    public void handleRequest(Request request) {
        
        // Add additional handling like special logging here.
		System.out.println();
        request.prettyPrint();
        
        // dispatch to requested resource
        super.handleRequest(request);
    }

	@Override
	protected void responseProduced(Response response) {
        // Add additional handling like special logging here.
		response.prettyPrint();
	}
    
}
