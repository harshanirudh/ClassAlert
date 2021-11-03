package com.harsha;

import java.io.IOException;
import java.util.Properties;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class ApplicationConfig {
	private static Properties prop= new Properties();
	private static void initProperties() {
		try {
			prop.load(ApplicationConfig.class.getResourceAsStream("/application.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public  static void setBroswerConfig() {
		initProperties();
		String driverName=prop.getProperty("driver.name");
		String driverPath=prop.getProperty("driver.path");
		System.setProperty(driverName,driverPath);
	}
	
}
