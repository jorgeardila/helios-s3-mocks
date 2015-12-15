/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digitaslbi.helios.delegates;

import com.digitaslbi.helios.dto.Folder;
import com.digitaslbi.helios.utils.S3Helper;
import java.io.InputStream;

/**
 *
 * @author sebpache
 */
public class S3Delegate {
    
	public Folder getFolder(String path) {
        return S3Helper.getFolder(path);
    }
	
	public InputStream getS3Object(String path) {
    	return S3Helper.getObject(path);
    }
    public void deleteS3Object(String path){
    	S3Helper.deleteFolder(path);
    }
    public void uploadObject(String folderName, InputStream filePath){
    	S3Helper.uploadFile(folderName,filePath);
    }
    public void createFolder(String folderName){
    	S3Helper.createFolder(folderName);
    }
    
}
