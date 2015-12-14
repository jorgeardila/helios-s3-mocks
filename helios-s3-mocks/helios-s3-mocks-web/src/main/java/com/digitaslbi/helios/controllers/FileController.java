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

import com.digitaslbi.helios.delegates.S3Delegate;
import com.digitaslbi.helios.dto.Folder;

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

}
