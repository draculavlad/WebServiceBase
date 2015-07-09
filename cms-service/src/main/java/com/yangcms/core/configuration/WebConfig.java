package com.yangcms.core.configuration;

import java.io.*;
import java.util.*;
import javax.activation.MimetypesFileTypeMap;
import org.apache.commons.configuration.ConfigurationException;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * <p>TODO</p>
 *
 * @author <a href="mailto:ysu2@cisco.com">Yu Su</a>
 * @version 1.0
 */
@Configuration
@EnableAsync
public class WebConfig {
    private final Logger logger = LoggerFactory.getLogger(AppConfig.class);


   	@Value("classpath:configuration.properties")
   	private Resource configurationResource;

   	@Bean(name="applicationConfiguration")
   	public Properties appConfig() throws IOException, org.apache.commons.configuration.ConfigurationException {
   		Properties properties = new Properties();
   		if(!configurationResource.exists()){
   			logger.error("Could not find root configuration file.");
   			throw new FileNotFoundException("Root configuration file.");
   		}

   		properties.load(configurationResource.getInputStream());
   		String configurationLocation = properties.getProperty("configuration.location");

   		Properties result = null;
   		logger.info("Will use configuration file {} to override the default.", configurationLocation);
   		PropertiesConfiguration cfg = new PropertiesConfiguration();
   		cfg.setDelimiterParsingDisabled(true);

   		// First, load default configuration from classpath as fallback
   		cfg.load(openConfiguration("classpath:app-config.properties"));

   		result.putAll(convertToProperties(cfg));

   		return result;
   	}

   	private Properties convertToProperties(PropertiesConfiguration cfg) {
   		Properties properties = new Properties();
   		Iterator<String> keyIter = cfg.getKeys();
   		while (keyIter.hasNext()) {
   			String key = keyIter.next();
   			properties.put(key,  cfg.getProperty(key));
   		}
   		return properties;
   	}


   	private InputStream openConfiguration(String location) throws IOException {
   		logger.info("Loading configuration from: {}", location);
   		DefaultResourceLoader resourceLoader = new DefaultResourceLoader();
   		Resource resource = resourceLoader.getResource(location);
   		if (!resource.exists()) {
   			throw new FileNotFoundException(location);
   		}

   		return resource.getInputStream();
   	}
}
