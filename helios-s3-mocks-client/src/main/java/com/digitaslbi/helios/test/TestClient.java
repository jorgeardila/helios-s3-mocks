package com.digitaslbi.helios.test;

import java.io.ByteArrayInputStream;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;

import com.digitaslbi.helios.client.HeliosS3MocksClient;
import com.digitaslbi.helios.dto.File;
import com.digitaslbi.helios.dto.Folder;


public class TestClient {

	public static void main(String[] args) {
		try {
			String URL = "http://127.0.0.1:8080/helios-s3-mocks-web-1.0-SNAPSHOT";
			HeliosS3MocksClient s3Client = new HeliosS3MocksClient(URL);
			
			Folder folder = s3Client.getFolder("tryme/");
			System.out.println("Folder: " + folder.getPath() + " number of objects: " + folder.getFiles().size() + "\n");
			
			//-------------------
			
			File file = s3Client.downloadFile("os/v1/leads/categories.json");
			byte[] contentBytes = Base64.decodeBase64(file.getContent());
		    String content = IOUtils.toString(new ByteArrayInputStream(contentBytes));
		    System.out.println("File: " + file.getPath());
		    System.out.println("Content: " + content + "\n");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
