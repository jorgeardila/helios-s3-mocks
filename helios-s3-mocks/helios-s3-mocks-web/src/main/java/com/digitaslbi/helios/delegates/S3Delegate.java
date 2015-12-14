/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digitaslbi.helios.delegates;

import com.digitaslbi.helios.dto.Folder;
import com.digitaslbi.helios.utils.S3Helper;

/**
 *
 * @author sebpache
 */
public class S3Delegate {
    
	public Folder getFolder(String path) {
        return S3Helper.getFolder(path);
    }
    
}
