/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digitaslbi.helios.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;
import javax.servlet.http.Part;
import java.io.InputStream;
import java.io.IOException;

import com.digitaslbi.helios.delegates.S3Delegate;
import com.digitaslbi.helios.dto.Folder;
import com.digitaslbi.helios.dto.File;

/**
 *
 * @author sebpache
 */
@RestController
public class FileController {

	private static Logger log = LogManager.getLogger(FileController.class);

    private S3Delegate delegate;

    @RequestMapping(value = "/getfolder", method = RequestMethod.GET)
    public Folder getFolder(@RequestParam("path") String path) {
        log.info("[FileController][getfolder] Received path: " + path);
        
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
    public void uploadObject(@RequestParam("fileName") String fileName,@RequestParam("file") Part file){
    	
    	try{
    		delegate = new S3Delegate();
	       	InputStream inputStream = file.getInputStream();
	       	delegate.uploadObject(fileName,inputStream);
    	}
    	catch (IOException e) {
    		log.error(e);
		}
    }
    @RequestMapping(value = "/createFolder",method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.CREATED)
    public void createFolder(@RequestParam("folderName") String folderName) {
    	delegate = new S3Delegate();
    	delegate.createFolder(folderName);
    }
	
}
