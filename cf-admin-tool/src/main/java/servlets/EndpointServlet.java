package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.TreeSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ch.ethz.inf.vs.californium.endpoint.VirtualNode;
import ch.ethz.inf.vs.californium.examples.AdminTool;

public class EndpointServlet extends HttpServlet {
	
	private AdminTool main;
	
	
	public EndpointServlet(){
		
		main = AdminTool.getInstance();
		
	}
	
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
    	boolean alive=true;
    	
    	String[] idsArray = request.getParameterValues("id");
    	
        if(idsArray==null || idsArray.length!=1){
        	System.out.println(idsArray[1]);
        	return;
        }
        String id = idsArray[0];
        
        String[] aliveArray = request.getParameterValues("alive");
        
        if(aliveArray!=null && aliveArray.length==1){
        	alive=Boolean.parseBoolean(aliveArray[0]);
        }
        
        response.setContentType("text/html");
        response.setHeader("Cache-control", "no-cache, no-store");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "-1");
        
            
        PrintWriter out = response.getWriter();
        ArrayList<String> subResources = main.getCoapServer().getAllEndpointSubResourcesId(id);
        VirtualNode node = main.getCoapServer().getVirtualNode(id);
        if(node==null){
        	return;
        }
        
		if(!alive){
			out.write("<div class=\"warning\" >This sensor is dead!</div>");        	
		}
        
        
        TreeSet<String> epSensors = new TreeSet<String>();
        TreeSet<String> epConfig = new TreeSet<String>();
        TreeSet<String> epSet = new TreeSet<String>();
        TreeSet<String> epDebug =  new TreeSet<String>();
        TreeSet<String> epUnsorted = new TreeSet<String>();
         /*
         * General Endpoint show all Resources
         * Search for different Type of Resources and Build corresponding Tab
         * 
         */
        
        if (node.getEndpointType().equalsIgnoreCase("honeywell")){
        	/*
        	 * Special Dialog for Honeywell
        	 * 
        	 * 
        	 */       	

	        for(String sub : subResources){
	        	if(sub.contains("/sensor")){
	        		epSensors.add(sub);
	        	}
	        	else if (sub.contains("/debug")){
	        		epDebug.add(sub);
	        	}
	        	else if (sub.contains("/config")){
	        		if (sub.contains("predefined")){
	        			continue;
	        		}
	        		epConfig.add(sub);
	        	}
	        	else if (sub.contains("/set")){
	        		if (sub.contains("slot")){
	        			continue;
	        		}
	           		epSet.add(sub);
	        	}
	        	else{
	        		continue;
	        		//epUnsorted.add(subres);
	        	}
	        }      	
          	
        }
        else{
        
	        for(String sub : subResources){
	        	if(sub.contains("/sensor")){
	        		epSensors.add(sub);
	        	}
	        	else if (sub.contains("/config")){
	        		epConfig.add(sub);
	        	}
	        	else if (sub.contains("/set")){
	        		epSet.add(sub);
	        	}
	        	else if (sub.contains(".well-known")){
	        		continue;
	        	}
	        	else if (sub.contains("/debug")){
	        		epDebug.add(sub);
	        	}
	        	else{
	        		epUnsorted.add(sub);
	        	}
	        }
        }

        if(epSensors.isEmpty() && epConfig.isEmpty() && epDebug.isEmpty() && epSet.isEmpty()){
        	out.write("<div>This endpoint does not have any resource registred at the RD</div>");
        	out.flush();
        	out.close();
        	return;
        	
        }
        StringBuilder sensorTab = null;
        StringBuilder configTab = null;
        StringBuilder setTab = null;
        StringBuilder debugTab = null;
        StringBuilder unsortedTab = null;


       	        
        if (!epDebug.isEmpty()){

        	debugTab = new StringBuilder("<div id=\"debugtab\">");
        	
        	if(alive){
	        	debugTab.append("<div class=\"tabouter\" id=\"");
	        	debugTab.append(node.getEndpointIdentifier());
	        
	        	debugTab.append("\"><div class=\"reregister\" onclick=\"setValue(this);\">Restart Observing</div>");
        	
	        	debugTab.append("</div>");
        	}
        	
	        debugTab.append("<div class=\"tabouter\" id=\"");
	        debugTab.append(node.getEndpointIdentifier());
	        debugTab.append("\"><div class=\"tableft\">Version</div><div class=\"versionvalue\">Fetching</div>");
	        if(alive){
	        	debugTab.append("<div class=\"tabrefresh\" onclick=\"refreshValue(this);\">Refresh</div>");
	        }
	        debugTab.append("</div>");
	        debugTab.append("<div class=\"tabouter\" id=\"");
	        debugTab.append(node.getEndpointIdentifier());
	        debugTab.append("\"><div class=\"tableft\">Last Seen</div><div class=\"lastseenvalue\">Fetching</div>");
	        if(alive){
	        	debugTab.append("<div class=\"tabrefresh\" onclick=\"refreshValue(this);\">Refresh</div>");
	        }
	        debugTab.append("</div>");
	        debugTab.append("<div class=\"tabouter\" id=\"");
	        debugTab.append(node.getEndpointIdentifier());
	        debugTab.append("\"><div class=\"tableft\">Last RSSI</div><div class=\"lastrssivalue\">Fetching</div>");
	        if(alive){
	        	debugTab.append("<div class=\"tabrefresh\" onclick=\"refreshValue(this);\">Refresh</div>");
	        }
	        debugTab.append("</div>");
	        debugTab.append("<div class=\"tabouter\" id=\"");
	        debugTab.append(node.getEndpointIdentifier());
	        debugTab.append("\"><div class=\"tableft\">Loss Rate</div><div class=\"lossratevalue\">Fetching</div>");
	        if(alive){
	        	debugTab.append("<div class=\"tabrefresh\" onclick=\"refreshValue(this);\">Refresh</div>");
	        }
	        debugTab.append("</div>");
	        debugTab.append("<div class=\"tabouter\" id=\"");
	        debugTab.append(node.getEndpointIdentifier());
	        debugTab.append("\"><div class=\"tableft\">Uptime</div><div class=\"uptimevalue\">Fetching</div>");
	        if(alive){
	        	debugTab.append("<div class=\"tabrefresh\" onclick=\"refreshValue(this);\">Refresh</div>");
	        }
	        debugTab.append("</div>");
	        debugTab.append("</div>");
        }
                
        if (!epSensors.isEmpty()){
        	sensorTab = new StringBuilder("<div id=\"sensortab\">");

        	for(String res : epSensors){
        		
        		sensorTab.append("<div class=\"tabouter\" id=\"");
        		sensorTab.append(node.getEndpointIdentifier()+res);
        		sensorTab.append("\"><div class=\"tableft\">");
        		sensorTab.append(res.substring(res.indexOf("/sensors/")+9));
        		sensorTab.append("</div><div class=\"sensorvalue\">Fetching..</div>");
        		if(alive){
     	        	sensorTab.append("<div class=\"tabrefresh\" onclick=\"refreshValue(this);\">Refresh</div>");
     	        }
        		else{
        			sensorTab.append("<div style=\"float:left\">&nbsp;</div>");
        		}
        		sensorTab.append("<div class=\"tabgraph\" onclick=\"openGraphDialog(this);\">Graph</div></div>");
        	}
            sensorTab.append("</div>");       	
        }
        
        if (!epConfig.isEmpty()){
        	configTab = new StringBuilder("<div id=\"configtab\">");
        	for(String res : epConfig){
        		
        		configTab.append("<div class=\"tabouter\" id=\"");
        		configTab.append(node.getEndpointIdentifier()+res);
        		configTab.append("\"><div class=\"tableft\">");
        		configTab.append(res.substring(res.indexOf("/config/")+8));
        		configTab.append("</div><div class=\"configvalue\">Fetching..</div>" +
        				"<div class=\"tabrefresh\" onclick=\"refreshValue(this);\">Refresh</div>" +
        				"<div class=\"tabsend\" onclick=\"setValue(this);\">Set</div>" +
        				"</div>");
        	}
            configTab.append("</div>");       	
        }
        
        if (!epSet.isEmpty()){
        	setTab = new StringBuilder("<div id=\"settab\">");
        	for(String res : epSet){
        		
        		setTab.append("<div class=\"tabouter\" id=\"");
        		setTab.append(node.getEndpointIdentifier()+res);
        		setTab.append("\"><div class=\"tableft\">");
        		setTab.append(res.substring(res.indexOf("/set/")+5));
        		setTab.append("</div><div class=\"setvalue\">Fetching..</div>" +
        				"<div class=\"tabrefresh\" onclick=\"refreshValue(this);\">Refresh</div>" +
        				"<div class=\"tabsend\" onclick=\"setValue(this);\">Set</div>" +
        				"</div>");
        	}
            setTab.append("</div>");       	
        }
        
        
        if (!epUnsorted.isEmpty()){
        	unsortedTab = new StringBuilder("<div id=\"unsortedtab\">");
        	for(String res : epUnsorted){
        		
        		unsortedTab.append("<div class=\"tabouter\" id=\"");
        		unsortedTab.append(node.getEndpointIdentifier()+res);
        		unsortedTab.append("\"><div class=\"tableft\">");
        		unsortedTab.append(res);
        		unsortedTab.append("</div><div class=\"unsortedvalue\">Fetching..</div><div class=\"tabrefresh\" onclick=\"refreshValue(this);\">Refresh</div></div>");
           	}
            unsortedTab.append("</div>");       	
        }
        
    
        StringBuilder tabBar = new StringBuilder("<div class=\"eptabbar\"><ul>");
        if (sensorTab != null){
        	tabBar.append("<li><a href=\"#sensortab\">Sensor</a></li>");
        }
        if (configTab !=null && alive){
        	tabBar.append("<li><a href=\"#configtab\">Config</a></li>");
        }
        if (setTab !=null && alive){
        	tabBar.append("<li><a href=\"#settab\">Set</a></li>");
        }
        
        if (unsortedTab != null && alive){
        	tabBar.append("<li><a href=\"#unsortedtab\">Unsorted</a></li>");
        }
        if (debugTab != null){
        	tabBar.append("<li><a href=\"#debugtab\">Debug</a></li>");
        }
            
        tabBar.append("</ul>");
        
        StringBuilder tabEnd  = new StringBuilder("</div>");
       
        out.write(tabBar.toString());
        if (sensorTab != null){
        	out.write(sensorTab.toString());
        }
        if (configTab !=null && alive){
        	out.write(configTab.toString());
        }
        if (setTab !=null && alive){
        	out.write(setTab.toString());
        }
        
        if (unsortedTab != null && alive){
        	out.write(unsortedTab.toString());
        }
        if (debugTab != null){
        	out.write(debugTab.toString());
        }

        out.write(tabEnd.toString());
          
        
        out.flush();
        out.close();
        
    }
 

}
