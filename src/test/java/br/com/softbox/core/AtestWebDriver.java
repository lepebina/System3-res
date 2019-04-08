package br.com.softbox.core;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages the Selenium webdriver creation.
 */
public class AtestWebDriver {
    public WebDriver _driver = null;
    private AtestSettingsLoader _settings = null;
    private boolean _isShutdown = false;

    public AtestWebDriver(AtestSettingsLoader settings) {
        _settings = settings;
    }

    /**
     * Returns the URL instance
     * @param serverURL the server string representation.
     * @return The URL
     */
    private URL getURL(String serverURL) {
        URL url = null;
        try {
            url = new URL(serverURL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    // TODO: ler do arquivo de settings os browsers habilitados
    public List<TestController.WebBrowser> getAvailableWebBrowsers() {
        List<TestController.WebBrowser> wb = new ArrayList<>();

        wb.add(TestController.WebBrowser.FIREFOX);
        wb.add(TestController.WebBrowser.CHROME);
        wb.add(TestController.WebBrowser.IE);
        return wb;
    }

    public WebDriver getWebDriver() throws AtestExecutionException {
        checkShutdown();
        return _driver;
    }

    private void checkShutdown() throws AtestExecutionException {
        if (_isShutdown) {
            throw new AtestExecutionException("Shutdown in progress...");
        }
    }
    /**
     * Returns a FIREFOX webdriver instance
     * @return Firefox instance
     */
    public WebDriver firefox() throws AtestWebDriverException, AtestExecutionException {
        checkShutdown();
        if (_settings.isSeleniumLocalhost()) {
            FirefoxProfile fp = new FirefoxProfile();
            fp.setAcceptUntrustedCertificates(true);
            fp.setPreference("browser.cache.disk.enable", false);
            fp.setPreference("browser.cache.memory.enable", false);
            fp.setPreference("browser.cache.offline.enable", false);
            fp.setPreference("network.http.use-cache", false);
            _driver = new FirefoxDriver(fp);
        } else {
            firefox(_settings.getSeleniumGridLocation());
        }
        if (_driver != null) {
            _driver.manage().window().maximize();
        }
        return _driver;
    }

    /**
     * Returns a CHROME webdriver instance
     *
     * @return Chrome instance
     */
    public WebDriver chrome() throws AtestWebDriverException, AtestExecutionException {
        checkShutdown();
        if (_settings.isSeleniumLocalhost()) {

//            ChromeOptions opts = new ChromeOptions();
//            opts.addArguments("");
            String chromeBinName = "chromedriver";
            if (System.getProperty("os.name").startsWith("Windows")) {
                chromeBinName = chromeBinName + ".exe";
            }
            String chromeLocation = _settings.getWorkspaceDir() + File.separator + "bin" + File.separator + chromeBinName;
            System.setProperty("webdriver.chrome.driver", chromeLocation);
            _driver = new ChromeDriver();
        } else {
            // TODO: set where is the binary of Chrome
            chrome(_settings.getSeleniumGridLocation());
        }
        if (_driver != null) {
            _driver.manage().window().maximize();
        }
        return _driver;
    }

    /**
     * Returns a INTERNET EXPLORER webdriver instance
     *
     * @return IE instance
     */
    public WebDriver ie() throws AtestWebDriverException, AtestExecutionException {
        checkShutdown();
        if (_settings.isSeleniumLocalhost()) {
            String ieLocation = _settings.getWorkspaceDir() + File.separator + "bin" + File.separator + "iedriverserver.exe";
            System.setProperty("webdriver.ie.driver", ieLocation);
            _driver = new InternetExplorerDriver();
        } else {
            ie(_settings.getSeleniumGridLocation());
        }
        if (_driver != null) {
            _driver.manage().window().maximize();
        }
        return _driver;
    }

    /**
     * Convenience method to return a Firefox webdriver instance for a remote Selenium Grid.
     *
     * @param server The Selenium grid.
     * @return The Firefox web driver.
     */
    private WebDriver firefox(String server) throws AtestWebDriverException, AtestExecutionException {
        checkShutdown();
        URL url = getURL(server);
        if (url == null) {
            throw new AtestWebDriverException("URL is invalid for Firefox WebDriver");
        }
        FirefoxProfile fp = new FirefoxProfile();
        fp.setAcceptUntrustedCertificates(true);
        fp.setPreference("browser.cache.disk.enable", false);
        fp.setPreference("browser.cache.memory.enable", false);
        fp.setPreference("browser.cache.offline.enable", false);
        fp.setPreference("network.http.use-cache", false);
//        fp.setPreference("webdriver.load.strategy", "unstable");  Tentativa para nao aguardar a pagina carregar
        DesiredCapabilities dcapab = new DesiredCapabilities();
        if (dcapab != null) {
            dcapab.setCapability(FirefoxDriver.PROFILE, fp);
            _driver = new RemoteWebDriver(url, dcapab.firefox());
        }
        return _driver;
    }

    /**
     * Convenience method to return a Chrome webdriver instance for a remote Selenium Grid.
     *
     * @param server The Selenium grid.
     * @return The Chrome web driver.
     */
    private WebDriver chrome(String server) throws AtestWebDriverException, AtestExecutionException {
        checkShutdown();
        URL url = getURL(server);
        if (url == null) {
            throw new AtestWebDriverException("URL is invalid for Chrome WebDriver");
        }
        DesiredCapabilities dcapab = new DesiredCapabilities();
        if (dcapab != null) {
            _driver = new RemoteWebDriver(url, dcapab.chrome());
            _driver.manage().window().maximize();
        }
        return _driver;
    }

    /**
     * Convenience method to return a Internet Explorer webdriver instance for a remote Selenium Grid.
     *
     * @param server The Selenium grid.
     * @return The Internet Explorer web driver.
     */
    private WebDriver ie(String server) throws AtestWebDriverException, AtestExecutionException {
        checkShutdown();
        URL url = getURL(server);
        if (url == null) {
            throw new AtestWebDriverException("URL is invalid for IE WebDriver");
        }
        DesiredCapabilities dcapab = DesiredCapabilities.internetExplorer();
        if (dcapab != null) {
            _driver = new RemoteWebDriver(url, dcapab);
            _driver.manage().window().maximize();
        }
        return _driver;
    }

    public void shutdown() {
        _isShutdown = true;
    }

    public boolean isShutdown() {
        return _isShutdown;
    }

    public void quit() {
        if (_driver != null) {
            _driver.quit();
        }
        _isShutdown = false;
    }

    public boolean isIE() throws AtestExecutionException {
        checkShutdown();
        return _driver instanceof InternetExplorerDriver;

    }
}
