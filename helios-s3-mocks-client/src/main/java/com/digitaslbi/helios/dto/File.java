/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digitaslbi.helios.dto;

/**
 *
 * @author sebpache
 */
public class File implements Comparable<File>{
        
    private String path;
    
    private String parent;
    
    private boolean isFile;
    
    private String content;

    public int compareTo(File o) {
        if(this.getPath().equals(o.getPath())){
            return 0;
        }else{
            return 1;
        }
    }

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
     * @return the isFile
     */
    public boolean isIsFile() {
        return isFile;
    }

    /**
     * @param isFile the isFile to set
     */
    public void setIsFile(boolean isFile) {
        this.isFile = isFile;
    }
    
    /**
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * @param content the content to set
     */
    public void setContent(String content) {
        this.content = content;
    }

}
