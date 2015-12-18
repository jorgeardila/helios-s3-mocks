/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digitaslbi.helios.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
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
    

    public static void createFolder(String folderName) {
    	connect();
    	
    	// create meta-data for your folder and set content-length to 0
    	ObjectMetadata metadata = new ObjectMetadata();
    	metadata.setContentLength(0);
    	
    	// create empty content
    	InputStream emptyContent = new ByteArrayInputStream(new byte[0]);
    	
    	// create a PutObjectRequest passing the folder name suffixed by /
		PutObjectRequest putObjectRequest = new PutObjectRequest(S3Properties.getInstance().getBucketName(),
				folderName + Constants.AWS_PARENT_DELIMITER.getValue(), emptyContent, metadata);
    	
    	// send request to S3 to create folder
    	try{
    		s3Client.putObject(putObjectRequest);
	    } catch (AmazonServiceException ase) {
    		log.error("[S3Helper][createFolder] Caught an AmazonServiceException, which " +
	        		"means your request made it " +
	                "to Amazon S3, but was rejected with an error response" +
	                " for some reason.");
    		log.error("Error Message:    " + ase.getMessage());
    		log.error("HTTP Status Code: " + ase.getStatusCode());
    		log.error("AWS Error Code:   " + ase.getErrorCode());
    		log.error("Error Type:       " + ase.getErrorType());
    		log.error("Request ID:       " + ase.getRequestId());
	    } catch (AmazonClientException ace) {
	    	log.error("[S3Helper][createFolder] Caught an AmazonClientException, which " +
	        		"means the client encountered " +
	                "an internal error while trying to " +
	                "communicate with S3, " +
	                "such as not being able to access the network.");
	    	log.error("Error Message: " + ace.getMessage());
	    }
    }

    public static void uploadFile(String fileName, byte[] content) {
    	connect();

    	ObjectMetadata metadata = new ObjectMetadata();
    	metadata.setContentLength(content.length);

    	try {
    		log.info("[S3Helper][uploadFile] Uploading a new object to S3: " + fileName);
    		
			PutObjectRequest putObjectRequest = new PutObjectRequest(S3Properties.getInstance().getBucketName(),
					fileName, new ByteArrayInputStream(content), metadata);
    		putObjectRequest.withCannedAcl(CannedAccessControlList.PublicRead);
    		
			s3Client.putObject(putObjectRequest);
         } catch (AmazonServiceException ase) {
        	 log.error("[S3Helper][uploadFile] Caught an AmazonServiceException, which " +
            		"means your request made it " +
                    "to Amazon S3, but was rejected with an error response" +
                    " for some reason.");
        	 log.error("Error Message:    " + ase.getMessage());
        	 log.error("HTTP Status Code: " + ase.getStatusCode());
        	 log.error("AWS Error Code:   " + ase.getErrorCode());
        	 log.error("Error Type:       " + ase.getErrorType());
        	 log.error("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
        	log.error("[S3Helper][uploadFile] Caught an AmazonClientException, which " +
            		"means the client encountered " +
                    "an internal error while trying to " +
                    "communicate with S3, " +
                    "such as not being able to access the network.");
        	log.error("Error Message: " + ace.getMessage());
        }
    }

	public static void deleteFolder(String folderName) {
		connect();
		List<S3ObjectSummary> fileList = s3Client.listObjects(S3Properties.getInstance().getBucketName(), folderName).getObjectSummaries();
		
		try{
			for (S3ObjectSummary file : fileList) {
				s3Client.deleteObject(S3Properties.getInstance().getBucketName(), file.getKey());
			}
			
			s3Client.deleteObject(S3Properties.getInstance().getBucketName(), folderName);
			
		} catch (AmazonServiceException ase) {
			log.error("[S3Helper][deleteFolder] Caught an AmazonServiceException.");
			log.error("Error Message:    " + ase.getMessage());
			log.error("HTTP Status Code: " + ase.getStatusCode());
			log.error("AWS Error Code:   " + ase.getErrorCode());
			log.error("Error Type:       " + ase.getErrorType());
			log.error("Request ID:       " + ase.getRequestId());
	    } catch (AmazonClientException ace) {
	    	log.error("[S3Helper][deleteFolder] Caught an AmazonClientException.");
	    	log.error("Error Message: " + ace.getMessage());
	    }
	}

    public static File getObject(String key) {
    	connect();
    	
    	try {
			log.info("[S3Helper][getObject] Downloading an object");

			S3Object s3object = s3Client.getObject(new GetObjectRequest(S3Properties.getInstance().getBucketName(), key));
			byte[] contentBytes = IOUtils.toByteArray(s3object.getObjectContent());
			
			log.info("Content-Type: "  + s3object.getObjectMetadata().getContentType());
			
			File aux = new File();
			aux.setPath(s3object.getKey());
			aux.setIsFile(true);
			aux.setContent(new String(Base64.encodeBase64String(contentBytes)));
			
            return aux;            
        } catch (AmazonServiceException ase) {
        	log.error("[S3Helper][getObject] Caught an AmazonServiceException, which" +
            		" means your request made it " +
                    "to Amazon S3, but was rejected with an error response" +
                    " for some reason.");
        	log.error("Error Message:    " + ase.getMessage());
        	log.error("HTTP Status Code: " + ase.getStatusCode());
        	log.error("AWS Error Code:   " + ase.getErrorCode());
        	log.error("Error Type:       " + ase.getErrorType());
        	log.error("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
        	log.error("[S3Helper][getObject] Caught an AmazonClientException, which means"+
            		" the client encountered " +
                    "an internal error while trying to " +
                    "communicate with S3, " +
                    "such as not being able to access the network.");
        	log.error("Error Message: " + ace.getMessage());
        } catch (IOException e) {
    		log.error("[S3Helper][getObject] Error: " + e);
		}
		
		return null;
	}

}
