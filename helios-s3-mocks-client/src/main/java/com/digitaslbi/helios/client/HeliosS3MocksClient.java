package com.digitaslbi.helios.client;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.digitaslbi.helios.dto.File;
import com.digitaslbi.helios.dto.Folder;
import com.google.gson.Gson;

public class HeliosS3MocksClient {
	final static String GET_FOLDER_SERVICE = "getFolder";
	final static String DOWNLOAD_OBJECT_SERVICE = "downloadObject";
	
	final static String PATH_PARAM = "path";
	final static String FILE_NAME_PARAM = "fileName";
	final static int OK_CODE = 200;
	
	private String url;
	
	public HeliosS3MocksClient(String url) {
		this.url = url;
	}
	
	public Folder getFolder(String path) {
		Folder folder = null;
		
		Client client = ClientBuilder.newClient();
		WebTarget webTarget = client.target(this.url);
		
		WebTarget serviceWebTarget = webTarget.path(GET_FOLDER_SERVICE);
		WebTarget serviceWebTargetWithParam = serviceWebTarget.queryParam(PATH_PARAM, path);
		
		Invocation.Builder invocationBuilder = serviceWebTargetWithParam.request(MediaType.APPLICATION_JSON_TYPE);
		
		Response response = invocationBuilder.get();
		
		if(response.getStatus() == OK_CODE) {
			String responseBody = response.readEntity(String.class);
			
			Gson gson = new Gson();
		    folder = gson.fromJson(responseBody, Folder.class);
		}
		
		return folder;
	}

	public File downloadFile(String fileName) {
		File file = null;
		
		Client client = ClientBuilder.newClient();
	    WebTarget webTarget = client.target(this.url);
	    
	    WebTarget serviceWebTarget = webTarget.path(DOWNLOAD_OBJECT_SERVICE);
	    WebTarget serviceWebTargetWithParam = serviceWebTarget.queryParam(FILE_NAME_PARAM, fileName);
		
	    Form form = new Form();
	    form.param(FILE_NAME_PARAM, fileName);
	    
	    Invocation.Builder invocationBuilder = serviceWebTargetWithParam.request(MediaType.APPLICATION_JSON_TYPE);
		
	    Response response = invocationBuilder.post(Entity.entity(form, MediaType.APPLICATION_JSON_TYPE));
	    
	    if(response.getStatus() == OK_CODE) {
			String responseBody = response.readEntity(String.class);
			Gson gson2 = new Gson();
		    file = gson2.fromJson(responseBody, File.class);
	    }
	    
	    return file;
	}
}
