package com.digitaslbi.helios.utils;

import java.io.IOException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.digitaslbi.helios.constants.Constants;

public class S3Properties {

	private static Logger log = LogManager.getLogger(S3Helper.class);

	private static Properties prop;
	
	private static S3Properties instance;

	public static S3Properties getInstance() {
		if(instance == null) {
			instance = new S3Properties();
		}
		
		return instance;
	}

	private S3Properties() {
		loadProperties();
	}
	
    private static void loadProperties() {
        try {
            prop = new Properties();
            prop.load(S3Properties.class.getResourceAsStream(Constants.PROPERTIES_PATH.getValue()));
        } catch (IOException ex) {
            log.error("[S3Properties][loadProperties] The configuration file was not found.");
            System.exit(0);
        }
    }

    public String getAwsAccessKeyId() {
    	return prop.getProperty(Constants.AWS_ACCESS_KEY_ID.getValue());
    }
    
    public String getAwsSecretAccessKey() {
    	return prop.getProperty(Constants.AWS_SECRET_ACCESS_KEY.getValue());
    }
    
    public String getBucketName() {
    	return prop.getProperty(Constants.AWS_BUCKET_NAME.getValue());
	}
}
