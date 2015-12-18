package com.digitaslbi.helios.dto;

import java.util.List;

public class Folder {

    private String path;
    
    private String parent;
    
    private List<File> files;

    /**
     * @return the path
     */
    public String getPath() {
		return path;
	}

    /**
     * @param path the path to set
     */
	public void setPath(String path) {
		this.path = path;
	}

	/**
     * @return the parent
     */
    public String getParent() {
		return parent;
	}

    /**
     * @param parent the parent to set
     */
    public void setParent(String parent) {
		this.parent = parent;
	}

	/**
     * @return the files
     */
    public List<File> getFiles() {
		return files;
	}

    /**
     * @param files the files to set
     */
    public void setFiles(List<File> files) {
		this.files = files;
	}

}
