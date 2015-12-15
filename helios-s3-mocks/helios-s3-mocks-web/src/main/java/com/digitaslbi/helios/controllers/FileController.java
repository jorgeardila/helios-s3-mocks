/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digitaslbi.helios.controllers;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.digitaslbi.helios.delegates.S3Delegate;
import com.digitaslbi.helios.dto.File;
import com.digitaslbi.helios.dto.Folder;

/**
 *
 * @author sebpache
 */
@RestController
public class FileController {

	private static Logger log = LogManager.getLogger(FileController.class);

    private S3Delegate delegate;

    @RequestMapping(value = "/getFolder", method = RequestMethod.GET)
    public Folder getFolder(@RequestParam("path") String path) {
        log.info("[FileController][getFolder] Received path: " + path);
        
        delegate = new S3Delegate();        
        Folder selectedFolder = delegate.getFolder(path);

        return selectedFolder;
    }

    @RequestMapping(value = "/downloadObject", method = RequestMethod.POST)
    public File downloadObject(@RequestParam("fileName") String fileName) {
    	delegate = new S3Delegate();
	    File inputStream = delegate.getS3Object(fileName);
	    
	    return inputStream;	        
    }

    @RequestMapping(value = "/deleteFolder",method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteObject(@RequestParam("fileName") String fileName){
    	delegate = new S3Delegate();
    	delegate.deleteS3Object(fileName);
    }

    @RequestMapping(value = "/uploadObject",method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public void uploadObject(@RequestParam("fileName") String fileName, @RequestParam("file") MultipartFile file) {
    	try {
    		delegate = new S3Delegate();
	       	byte[] content = file.getBytes();
	       	delegate.uploadObject(fileName, content);
	       	
	       	log.info("[FileController][uploadObject] File: " + fileName + " created.");
    	} catch (IOException e) {
    		log.error("[FileController][uploadObject] Error creating file: " + e);
		}
    }

    @RequestMapping(value = "/createFolder",method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.CREATED)
    public void createFolder(@RequestParam("folderName") String folderName) {
    	delegate = new S3Delegate();
    	delegate.createFolder(folderName);
    }
	
}
