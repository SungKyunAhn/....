package com.aimir.fep.util;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.MalformedInputException;
import java.nio.file.DirectoryStream;
import java.nio.file.DirectoryStream.Filter;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * FMP properties manager
 * 
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 */
public class FMPProperty {
	private static Log log = LogFactory.getLog(FMPProperty.class);
	private static final Properties properties;

	static {
		DirectoryStream<Path> ds;
		Properties result = new Properties();
		boolean isAimirWeb = true;

		/*
		 * fmp[xxx].properties or schedule[xxx].properties loading for aimir-fep or aimir-schedule
		 */
		Path fmpPath = null;
		try {
			fmpPath = Paths.get(FMPProperty.class.getResource("/config").toURI());
			ds = Files.newDirectoryStream(fmpPath, getPropertiesFilter());
			log.debug("Properties file path : " + fmpPath.toAbsolutePath().toString());

			for (Path path : ds) {
				log.debug("Properties loding... => [" + path.toString() + "]");
				result.load(Files.newBufferedReader(path, Charset.forName("UTF-8")));
			}
			isAimirWeb = false;
		} catch (FileSystemNotFoundException fsnf) {
			log.error("[fmp.properties] There is no filesystem.");
		} catch (NoSuchFileException nfe) {
			log.error("[" + (fmpPath != null ? fmpPath.toString() : "fmp.properties") + "] There is no properties files.");
		} catch (MalformedInputException e) {
			log.error("There is invalid froperties files. => " + e);
		} catch (Exception e) {
			log.error("Properties file loading error - " + e, e);
		}

		log.debug("isAimirWeb = " + isAimirWeb);

		/*
		 *  command[xxx].properties loding for aimir-web
		 */
		if (isAimirWeb) {
			Path commandPath = null;
			try {
				commandPath = Paths.get(FMPProperty.class.getResource("/").toURI());
				ds = Files.newDirectoryStream(commandPath, getPropertiesFilter());
				log.debug("Command Properties file path : " + commandPath.toAbsolutePath().toString());

				for (Path path : ds) {
					result.load(Files.newBufferedReader(path, Charset.forName("UTF-8")));
					log.debug("Properties loding... => [" + path.getFileName().toString() + "]");
				}

			} catch (FileSystemNotFoundException fsnf) {
				log.error("[" + commandPath.toString() + "] There is no filesystem.");
			} catch (NoSuchFileException nfe) {
				log.error("[" + commandPath.toString() + "] There is no command[XXX].properties files.");
			} catch (MalformedInputException e) {
				log.error("There is invalid command[XXX].properties files.");
			} catch (Exception e) {
				log.error("command[XXX].properties file loading error - " + e, e);
			}
		}

		//        String it = "/config/fmp.properties";
		//        try
		//        {
		//            InputStream is = 
		//                FMPProperty.class.getResourceAsStream(it);
		//            result.load(is);
		//            is.close();
		//
		//        } catch(Exception e) {
		//        	log.error("Cannot find fmp.properties");
		//        	it = "/config/schedule.properties";
		//        	try {
		//        		InputStream is = FMPProperty.class.getResourceAsStream(it);
		//        		result.load(is);
		//        		is.close();
		//        	} catch (Exception e1) {
		//            	log.error("Cannot find schedule.properties");
		//            	it = "/command.properties";
		//        		try {
		//            		InputStream is = FMPProperty.class.getResourceAsStream(it);
		//            		result.load(is);
		//            		is.close();
		//				} catch (Exception e2) {
		//					log.error("Cannot find command.properties");
		//					log.error(e2,e2);
		//				}
		//        	}
		//        }

		properties = result;
	}

	private FMPProperty() {
		super();
	}

	/**
	 * get PropertyURL
	 *
	 * @param key
	 *            <code>String</code>
	 * @return url <code>URL</code>
	 */
	public static URL getPropertyURL(String key) {
		String val = properties.getProperty(key);

		URL url = null;
		try {
			url = new URL(val);
		} catch (Exception ex) {
		}

		if (url == null) {
			url = FMPProperty.class.getResource(val);
		}

		return url;

	}

	/**
	 * get property
	 *
	 * @param key
	 *            <code>String</code> key
	 * @return value <code>String</code>
	 */
	public static String getProperty(String key) {
		return properties.getProperty(key);
	}

	/**
	 * get property
	 *
	 * @param key
	 *            <code>String</code> key
	 * @param key
	 *            <code>String</code> value
	 * @return value <code>String</code>
	 */
	public static String getProperty(String key, String value) {
		return properties.getProperty(key, value);
	}

	/**
	 * get property names
	 *
	 * @return enumeration <code>Enumeration</code>
	 */
	public static Enumeration<?> propertyNames() {
		return properties.propertyNames();
	}

	/**
	 * get properties filter - fmp.properties - fmp___.properties -
	 * command.properties - command___.properties - schedule.properties -
	 * schedule___.properties
	 * 
	 * @return
	 */
	private static Filter<Path> getPropertiesFilter() {
		Filter<Path> filter = new Filter<Path>() {
			@Override
			public boolean accept(Path entry) throws IOException {
				if (Files.isRegularFile(entry) && FilenameUtils.isExtension(entry.getFileName().toString(), "properties") && (entry.getFileName().toString().startsWith("fmp") || entry.getFileName().toString().startsWith("command") || entry.getFileName().toString().startsWith("schedule"))) {
					return true;
				}
				return false;
			}
		};
		return filter;
	}

	public static Properties getProperties() {
		return properties;
	}

}
