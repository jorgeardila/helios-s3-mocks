/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digitaslbi.helios.utils;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.digitaslbi.helios.constants.Constants;
import com.digitaslbi.helios.dto.File;
import com.digitaslbi.helios.dto.Folder;

/**
 *
 * @author sebpache
 */
public class S3Helper {

    private static Logger log = LogManager.getLogger(S3Helper.class);

    private static AmazonS3 s3Client;

    private static ListObjectsRequest listObjectsRequest;

    public static AmazonS3 connect() {
        BasicAWSCredentials awsCreds = new BasicAWSCredentials(S3Properties.getInstance().getAwsAccessKeyId(), S3Properties.getInstance().getAwsSecretAccessKey());
        s3Client = new AmazonS3Client(awsCreds);
        
        return s3Client;
    }

    private static ListObjectsRequest getRootFolders() {
    	connect();
        
        listObjectsRequest = new ListObjectsRequest();
        listObjectsRequest.withBucketName(S3Properties.getInstance().getBucketName());
        listObjectsRequest.withDelimiter(Constants.AWS_PARENT_DELIMITER.getValue());
        
        return listObjectsRequest;
    }

    private static ListObjectsRequest getContentByPreffix(String prefix) {
    	connect();
        
        listObjectsRequest = new ListObjectsRequest();
        listObjectsRequest.withBucketName(S3Properties.getInstance().getBucketName());
        listObjectsRequest.withDelimiter(Constants.AWS_PARENT_DELIMITER.getValue());
        listObjectsRequest.withPrefix(prefix);
        
        return listObjectsRequest;
    }
    
    public static Folder getFolder(String path) {
    	ListObjectsRequest listObjectsRequest;
    	Folder folder;
    	File aux;
        
        if(path == null || path.equals("/")) {
        	listObjectsRequest = getRootFolders();
        } else {
        	listObjectsRequest = getContentByPreffix(path);
        }
        
        ObjectListing objListing = s3Client.listObjects(listObjectsRequest);
        
        folder = new Folder();
        folder.setFiles(new ArrayList<File>());
        
        folder.setPath(path);
        folder.setParent(obtainParentPath(path));

		for (S3ObjectSummary objSummary : objListing.getObjectSummaries()) {
			aux = new File();
			aux.setPath(objSummary.getKey());
			aux.setParent(objListing.getPrefix());
			
			// if size is 0 is considered a folder
			aux.setIsFile((objSummary.getSize() == 0) ? false : true);

			if (!aux.getPath().equals(path)) {
				folder.getFiles().add(aux);
			}
		}

		for (String folderNames : objListing.getCommonPrefixes()) {
            aux = new File();
            aux.setPath(folderNames);
            aux.setParent(objListing.getPrefix());
            aux.setIsFile(false);
            
            folder.getFiles().add(aux);
        }

		log.info("[S3Helper][getFolder] Path: " + path + " items found: " + folder.getFiles().size());

        return folder;
    }

    private static String obtainParentPath(String path) { 
    	if(path == null || path.equals(Constants.AWS_PARENT_DELIMITER.getValue())) {
    		return null;
    	}
    	
    	// if last path's character is / its deleted
    	if(path.substring(path.length() - 1).equals(Constants.AWS_PARENT_DELIMITER.getValue())) {
    		path = path.substring(0, path.length() - 1);
		}

    	if(path.lastIndexOf(Constants.AWS_PARENT_DELIMITER.getValue()) > 0) {
        	return path.substring(0, path.lastIndexOf(Constants.AWS_PARENT_DELIMITER.getValue())+1);
    	} else {
    		return null;
    	}
    }

}
