package com.healthtrack.healthtrack_platform.functional.config;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.time.Duration;

/**
 * Configuración base para tests funcionales con Selenium
 * Proporciona factory methods para diferentes navegadores y configuraciones
 */
public class TestConfig {
    
    public static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(10);
    public static final Duration LONG_TIMEOUT = Duration.ofSeconds(30);
    public static final String BROWSER_PROPERTY = "test.browser";
    public static final String HEADLESS_PROPERTY = "test.headless";
    
    /**
     * Crea una instancia de WebDriver basada en las propiedades del sistema
     */
    public static WebDriver createWebDriver() {
        String browser = System.getProperty(BROWSER_PROPERTY, "chrome").toLowerCase();
        boolean headless = Boolean.parseBoolean(System.getProperty(HEADLESS_PROPERTY, "true"));
        
        return switch (browser) {
            case "firefox" -> createFirefoxDriver(headless);
            case "chrome" -> createChromeDriver(headless);
            default -> throw new IllegalArgumentException("Browser no soportado: " + browser);
        };
    }
    
    /**
     * Crea un ChromeDriver con opciones optimizadas para testing
     */
    public static WebDriver createChromeDriver(boolean headless) {
        WebDriverManager.chromedriver().setup();
        
        ChromeOptions options = new ChromeOptions();
        
        if (headless) {
            options.addArguments("--headless");
        }
        
        // Opciones comunes para estabilidad en CI/CD
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-web-security");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--start-maximized");
        
        // Para mejor performance en CI
        options.addArguments("--disable-background-networking");
        options.addArguments("--disable-background-timer-throttling");
        options.addArguments("--disable-renderer-backgrounding");
        options.addArguments("--disable-backgrounding-occluded-windows");
        
        WebDriver driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(DEFAULT_TIMEOUT);
        driver.manage().timeouts().pageLoadTimeout(LONG_TIMEOUT);
        
        return driver;
    }
    
    /**
     * Crea un FirefoxDriver con opciones optimizadas para testing
     */
    public static WebDriver createFirefoxDriver(boolean headless) {
        WebDriverManager.firefoxdriver().setup();
        
        FirefoxOptions options = new FirefoxOptions();
        
        if (headless) {
            options.addArguments("--headless");
        }
        
        options.addArguments("--width=1920");
        options.addArguments("--height=1080");
        
        WebDriver driver = new FirefoxDriver(options);
        driver.manage().timeouts().implicitlyWait(DEFAULT_TIMEOUT);
        driver.manage().timeouts().pageLoadTimeout(LONG_TIMEOUT);
        
        return driver;
    }
    
    /**
     * Obtiene la URL base para los tests
     */
    public static String getBaseUrl() {
        String baseUrl = System.getProperty("test.base.url");
        
        if (baseUrl != null && !baseUrl.isEmpty()) {
            return baseUrl;
        }
        
        // URL por defecto para archivo local
        String userDir = System.getProperty("user.dir");
        return "file://" + userDir + "/src/test/resources/index.html";
    }
    
    /**
     * Configuraciones para diferentes entornos
     */
    public enum Environment {
        LOCAL("file://"),
        DEV("http://localhost:8080"),
        TEST("http://test.healthtrack.com"),
        STAGING("http://staging.healthtrack.com");
        
        private final String baseUrl;
        
        Environment(String baseUrl) {
            this.baseUrl = baseUrl;
        }
        
        public String getBaseUrl() {
            return baseUrl;
        }
        
        public static Environment fromProperty() {
            String env = System.getProperty("test.environment", "LOCAL").toUpperCase();
            return Environment.valueOf(env);
        }
    }
}