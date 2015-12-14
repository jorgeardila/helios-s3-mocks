/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digitaslbi.helios.constants;

/**
 *
 * @author sebpache
 */
public enum Constants {
    
    AWS_ACCESS_KEY_ID ("aws_access_key_id"),
    AWS_SECRET_ACCESS_KEY ("aws_secret_access_key"),
    AWS_BUCKET_NAME ("bucket_name"),
    AWS_PARENT_DELIMITER ("/"),
    PROPERTIES_PATH("/helios-mock-utils.properties");
	
    private final String value;
    
    private Constants(String value){
        this.value = value;
    }
    
    public String getValue(){
        return value;
    }
    
}
