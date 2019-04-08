package br.com.softbox.core;

import br.com.softbox.utils.Utils;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

/**
 *
 */
public class
AtestWebElement {
    private static AtestEvidence _evidence = AtestEvidence.getInstance();
    private static SelectorEntry _selectorEntry = null;
    private static HashMap<String, SelectorEntry.Bundle> _selectorEntries = new HashMap<>();
    private static HashMap<String, String> _hosts = new HashMap<>();
    private static String _currentHostAlias;
    private static AtestWebElement _instance;
    private static AtestLog _log = new AtestLog("AtestWebElement");

    static {
        _instance = new AtestWebElement();
    }

    public static AtestWebElement getInstance() {
        return _instance;
    }

    private AtestWebElement() {
    }

    public void setSelectors(SelectorEntry entry) {
        _selectorEntry = entry;
        if (_selectorEntry != null) {
            mapHosts();
            mapSelectorEntries();
        }
    }

    private void mapSelectorEntries() {
        String currentKey = "";
        for (SelectorEntry.Bundle e : _selectorEntry.bundle) {
            try {
                currentKey = e.id;
                _selectorEntries.put(e.id, e);
            } catch (Exception ex) {
                _log.oops("EXCEPTION: parsing 'seletors.json'. [" + ex.getMessage() + "]. Reading selector [" + currentKey + "]");
            }
        }
    }

    private void mapHosts() {
        String currentKey = "";
        for (SelectorEntry.Host h : _selectorEntry.hosts_alias) {
            try {
                currentKey = removeProtocolAndSlashes(h.url);
                _hosts.put(currentKey, h.alias);
            } catch (Exception ex) {
                _log.oops("EXCEPTION: parsing 'seletors.json'. [" + ex.getMessage() + "]. Reading host [" + currentKey + "]");
            }
        }
    }

    private String removeProtocolAndSlashes(String url) {
        String justHostname = url.replace("http://", "");
        justHostname = justHostname.replace("/", "");
        justHostname = justHostname.replace("www.", "");
        justHostname = justHostname.replace("https://", "");
        return justHostname;
    }

    public WebElement findElement(String selectorId) throws AtestNoSuchWebElementException, AtestWebDriverException {
        checkSelector(selectorId);
        SelectorEntry.Bundle bundle = _selectorEntries.get(selectorId);
        SelectorEntry.Data data = getBundleData(bundle.data);
        final By by = getBy(data.selenium_by, data.selector_text);
        WebElement we = null;
        try {
            Utils.wait(1000);
            we = (new WebDriverWait(_evidence.getWebDriver(), bundle.timeout)).until((WebDriver wd) -> wd.findElement(by));
        } catch (AtestWebDriverException e) {
            throw e;
        } catch (Exception e) {
            String msg = "NOT FOUND - selector id[" + selectorId + "] description[" + bundle.description
                    + "] host alias[" + data.alias + "] selector text [" + data.selector_text + "]";
            throw new AtestNoSuchWebElementException(msg);
        }
        return we;
    }

    /**
     * Waits for a page to be loaded. It should be called right after a 'click' event like as a button click.
     * @throws AtestPageLoadedException
     */
    public void waitForPageLoaded() throws AtestPageLoadedException, AtestWebDriverException {
        WebDriver webdriver = null;
        try {
            webdriver = _evidence.getWebDriver();
        } catch (AtestWebDriverException e) {
            throw e;
        }
        try {
            Utils.wait(1500);
            (new WebDriverWait(webdriver, 30)).until(new ExpectedCondition<Boolean>() {
                @Override
                public Boolean apply(WebDriver wd) {
                    return ((JavascriptExecutor) wd).executeScript("return document.readyState").equals("complete");
                }
            });
        } catch (Exception e) {
            throw new AtestPageLoadedException(e.getMessage());
        }
    }

    private SelectorEntry.Data getBundleData(List<SelectorEntry.Data> data) throws AtestNoSuchWebElementException {
        SelectorEntry.Data defaultData = null;
        for (SelectorEntry.Data d : data) {
            if (d.alias.equals("default")) {
                defaultData = d;
            }
            if (d.alias.equals(_currentHostAlias)) {
                return d;
            }
        }
        if (defaultData == null) {
            throw new AtestNoSuchWebElementException("could not get selector.bundle.alias (selectors.json)");
        }
        // If it reached this point means that there isn't a specific data configuration for the _currentAlias, so
        // let's return the default value
        return defaultData;
    }

    private String setCurrentHostAlias() {
        final String currentURL = removeProtocolAndSlashes(_evidence.getInstance().getCurrentHost());
        _currentHostAlias = _hosts.containsKey(currentURL) ? _hosts.get(currentURL) : "default";
        return _currentHostAlias;
    }

    public List<WebElement> findElements(String selectorId) throws AtestNoSuchWebElementException, AtestWebDriverException {
        checkSelector(selectorId);
        SelectorEntry.Bundle bundle = _selectorEntries.get(selectorId);
        SelectorEntry.Data data = getBundleData(bundle.data);
        final By by = getBy(data.selenium_by, data.selector_text);
        Utils.wait(1000);
        List<WebElement> wes = new ArrayList<>();
        try {
            wes = (new WebDriverWait(_evidence.getWebDriver(), bundle.timeout)).until((WebDriver wd) -> wd.findElements(by));
            if (wes.isEmpty()) {
                throw new AtestNoSuchWebElementException("there is not selector text [" + selectorId
                        + "] in selectors.json file.");
            }
            String msg = "found selector id[" + selectorId + "] description[" + bundle.description + "] host alias["
                    + data.alias + "]  selector text [" + data.selector_text + "] number of found elements[" + wes.size() + "]";
            _log.trace(msg);
        } catch (TimeoutException te) {
            throw new AtestWebDriverException("Timeout for selector [" + selectorId + "], maybe the page still loading. " + te.getMessage());
        } catch (NullPointerException npe) {
            throw new AtestWebDriverException("The browser was closed incorrectlly" + npe.getMessage());
        }
        return wes;
    }

    public String findAttributeByElementIdWithJS(String selectorId, String attribute) throws AtestNoSuchWebElementException, AtestWebDriverException  {
        checkSelector(selectorId);
        SelectorEntry.Bundle bundle = _selectorEntries.get(selectorId);
        SelectorEntry.Data data = getBundleData(bundle.data);
        String id = data.selector_text;
        String jsCode = "return  window.document.getElementById('" + id + "')."+ attribute;
        JavascriptExecutor jse = null;
        jse = (JavascriptExecutor) _evidence.getWebDriver();
        return (String)jse.executeScript(jsCode);
    }

    private void checkSelector(String selectorId) throws AtestNoSuchWebElementException {
        if (_selectorEntry == null) {
            throw new AtestNoSuchWebElementException("FATAL: invalid SelectorEntry");
        }
        if (setCurrentHostAlias().isEmpty()) {
            throw new AtestNoSuchWebElementException("unable to set the current host. Current host ["
                    + _evidence.getCurrentHost() + "]");
        }
        if (!_selectorEntries.containsKey(selectorId)) {
            throw new AtestNoSuchWebElementException("there is not selector text ["
                    + selectorId + "] in selectors.json file.");
        }
    }

    private By getBy(String selenium_by, String selector_text) {
        if (selenium_by.equals("id")) {
            return By.id(selector_text);
        }
        if (selenium_by.equals("class")) {
            return By.className(selector_text);
        }
        if (selenium_by.equals("css")) {
            return By.cssSelector(selector_text);
        }
        if (selenium_by.equals("xpath")) {
            return By.xpath(selector_text);
        }
        if (selenium_by.equals("linkText")) {
            return By.linkText(selector_text);
        }
        if (selenium_by.equals("name")) {
            return By.name(selector_text);
        }
        if (selenium_by.equals("name")) {
            return By.name(selector_text);
        }
        if (selenium_by.equals("tagName")) {
            return By.tagName(selector_text);
        }
        if (selenium_by.equals("partialLinkText")) {
            return By.partialLinkText(selector_text);
        }
        return By.id(selector_text);
    }

    public void selectOption(String selectorId, String itemValue, String comboName) throws AtestNoSuchWebElementException, AtestWebDriverException {
        WebElement combo = findElement(selectorId);
        Select select = new Select(combo);
        if (select == null) {
            final String msg = "Unable to set SELECT for combo represented by selector ID ["
                    + selectorId + "] and combo [" + comboName + "]";
            throw new AtestNoSuchWebElementException(msg);
        }
        List<WebElement> opts = select.getOptions();
        for (WebElement opt : opts) {
            String itemText = opt.getAttribute("value").trim().toLowerCase();
            if (itemText.equalsIgnoreCase(itemValue)) {
                try {
                    select.selectByValue(itemValue);
                    break;
                } catch (org.openqa.selenium.NoSuchElementException e) {
                    final String msg = "ERROR: No option [" + itemValue + "] found in combo ["
                            + comboName + "]";
                    throw new AtestNoSuchWebElementException(msg);
                }
            }
        }
    }

    public Select selectOptionByIndex(String selectorId, int idx, String comboName) throws AtestNoSuchWebElementException, AtestWebDriverException {
        WebElement combo = findElement(selectorId);
        Select select = new Select(combo);
        if (select == null) {
            final String msg = "Unable to set SELECT for combo represented by selector ID ["
                    + selectorId + "] and combo [" + comboName + "]";
            throw new AtestNoSuchWebElementException(msg);
        }
        List<WebElement> opts = select.getOptions();
        if (opts != null && opts.size() >= idx) {
            try {
                select.selectByIndex(idx);
            } catch (org.openqa.selenium.NoSuchElementException e) {
                final String msg = "ERROR: No option index [" + idx + "] found in combo ["
                        + comboName + "]";
                throw new AtestNoSuchWebElementException(msg);
            }
        }
        return select;
    }

    public Select selectLastOption(WebElement combo, String comboName) throws AtestNoSuchWebElementException, AtestWebDriverException {
        Select select = new Select(combo);

        int idx = select.getOptions().size();
        if (idx == 0) {
            throw new AtestNoSuchWebElementException("Empy combo [" + comboName + "]");
        }
        --idx;
        if (select == null) {
            final String msg = "Unable to instantiate SELECT for combo [" + comboName + "]";
            throw new AtestNoSuchWebElementException(msg);
        }
        List<WebElement> opts = select.getOptions();
        if (opts != null && opts.size() >= idx) {
            try {
                select.selectByIndex(idx);
            } catch (org.openqa.selenium.NoSuchElementException e) {
                final String msg = "ERROR: No option index [" + idx + "] found in combo ["
                        + comboName + "]";
                throw new AtestNoSuchWebElementException(msg);
            }
        }
        return select;
    }
    public boolean hasText(String text) throws AtestWebDriverException {
        boolean retCode = true;
        try {
            final String pat = "//*[contains(.,'" + text + "')]";
            Utils.wait(1000);
            (new WebDriverWait(_evidence.getWebDriver(), 20)).until((WebDriver wd) -> wd.findElement(By.xpath(pat)));
        } catch (AtestWebDriverException e) {
            throw e;
        } catch (Exception e) {
            retCode = false;
        }
        return retCode;
    }

    public boolean hasText(String text, Integer timeout) throws AtestWebDriverException {
        boolean retCode = true;
        final String pat = "//*[contains(.,'" + text + "')]";
        try {
            Wait<WebDriver> wait = new FluentWait<WebDriver>(_evidence.getWebDriver())
                    .withTimeout(timeout, TimeUnit.SECONDS)
                    .pollingEvery(3, TimeUnit.SECONDS)
                    .ignoring(NoSuchElementException.class);
            wait.until((WebDriver wd) -> {
                Utils.wait(1000);
                return wd.findElement(By.xpath(pat));
            });

        } catch (AtestWebDriverException e) {
            throw e;
        } catch (Exception e) {
            // Give a second chance!
            try {
                Utils.wait(1000);
                WebElement we = (new WebDriverWait(_evidence.getWebDriver(), timeout))
                        .until(ExpectedConditions.presenceOfElementLocated(By.xpath(pat)));
                return we != null;
            } catch (Exception ex) {
                throw new AtestWebDriverException("could not find text [" + text + "] " + ex.getMessage());
            }
        }
        return retCode;
    }

    public WebElement findElement(WebElement parent, String selectorId) throws AtestNoSuchWebElementException {
        checkSelector(selectorId);
        SelectorEntry.Bundle bundle = _selectorEntries.get(selectorId);
        SelectorEntry.Data data = getBundleData(bundle.data);
        final By by = getBy(data.selenium_by, data.selector_text);
        WebElement we = null;
        try {
            Utils.wait(1000);
            we = parent.findElement(by);
        } catch (Exception e) {
            String msg = "NOT FOUND - selector id[" + selectorId + "] description[" + bundle.description
                    + "] host alias[" + data.alias + "]  selector text [" + data.selector_text + "]";
            throw new AtestNoSuchWebElementException(msg);
        }
        return we;
    }

    public List<WebElement> findElements(WebElement parent, String selectorId) throws AtestNoSuchWebElementException {
        checkSelector(selectorId);
        SelectorEntry.Bundle bundle = _selectorEntries.get(selectorId);
        SelectorEntry.Data data = getBundleData(bundle.data);
        final By by = getBy(data.selenium_by, data.selector_text);
        List<WebElement> wes = null;
        try {
            Utils.wait(1000);
            wes = parent.findElements(by);
        } catch (Exception e) {
            String msg = "NOT FOUND - selector id[" + selectorId + "] description[" + bundle.description
                    + "] host alias[" + data.alias + "]  selector text [" + data.selector_text + "]";
            throw new AtestNoSuchWebElementException(msg);
        }
        return wes;
    }

    public List<WebElement> findElementsAfterCallingAjax(String selectorId) throws AtestNoSuchWebElementException, AtestWebDriverException {
        checkSelector(selectorId);
        SelectorEntry.Bundle bundle = _selectorEntries.get(selectorId);
        SelectorEntry.Data data = getBundleData(bundle.data);
        final By by = getBy(data.selenium_by, data.selector_text);
        List<WebElement> wes = null;
        try {
            Wait<WebDriver> wait = new FluentWait<WebDriver>(_evidence.getWebDriver())
                    .withTimeout(bundle.timeout, TimeUnit.SECONDS)
                    .pollingEvery(3, TimeUnit.SECONDS)
                    .ignoring(NoSuchElementException.class);

            Utils.wait(1000);
            wes = wait.until((WebDriver wd) -> {
                return wd.findElements(by);
            });
        } catch (AtestWebDriverException e) {
            throw e;
        } catch (Exception e) {
            String msg = "NOT FOUND - selector id[" + selectorId + "] description[" + bundle.description
                    + "] host alias[" + data.alias + "]  selector text [" + data.selector_text + "]";
            throw new AtestNoSuchWebElementException(msg);
        }
        return wes;
    }

    public void waitUntilVisible(WebElement we, int timeout, String errorMsg) throws AtestNoSuchWebElementException, AtestWebDriverException {
        try {
            Utils.wait(1000);
            Wait<WebDriver> wait = new FluentWait<WebDriver>(_evidence.getWebDriver())
                    .withTimeout(timeout, TimeUnit.SECONDS)
                    .pollingEvery(3, TimeUnit.SECONDS)
                    .ignoring(NoSuchElementException.class);
            wait.until(ExpectedConditions.visibilityOf(we));
        } catch (AtestWebDriverException e) {
            throw e;
        } catch (Exception e) {
            // Giving a second chance...
            try {
                Utils.wait(1000);
                WebElement element = (new WebDriverWait(_evidence.getWebDriver(), timeout))
                    .until(ExpectedConditions.visibilityOf(we));
                if (element == null) {
                    throw new AtestNoSuchWebElementException("element is not visible: " + errorMsg);
                }
            } catch (Exception ex) {
                throw new AtestNoSuchWebElementException("element is not visible: " + errorMsg);
            }
        }
    }

    public void waitUntilVisible(String selectorId) throws AtestNoSuchWebElementException, AtestWebDriverException {
        checkSelector(selectorId);
        SelectorEntry.Bundle bundle = _selectorEntries.get(selectorId);
        SelectorEntry.Data data = getBundleData(bundle.data);
        final By by = getBy(data.selenium_by, data.selector_text);
        try {
            Utils.wait(1000);
            new WebDriverWait(_evidence.getWebDriver(), bundle.timeout).until(ExpectedConditions.visibilityOfElementLocated(by));
        }  catch (AtestWebDriverException e) {
            throw e;
        } catch (Exception e) {
            // Giving a second chance...
            try {
                WebElement element = (new WebDriverWait(_evidence.getWebDriver(), bundle.timeout))
                        .until(ExpectedConditions.visibilityOfElementLocated(by));
                if (element == null) {
                    throw new AtestNoSuchWebElementException("waitUntilVisible failed for element "
                            + data.selector_text + " by " + data.selenium_by + " with timeout " + bundle.timeout);
                }
            } catch (Exception ex) {
                throw new AtestNoSuchWebElementException("waitUntilVisible failed for element "
                        + data.selector_text + " by " + data.selenium_by + " with timeout " + bundle.timeout);
            }
        }
    }

    public void waitUntilClickable(String selectorId) throws AtestNoSuchWebElementException, AtestWebDriverException {
        checkSelector(selectorId);
        SelectorEntry.Bundle bundle = _selectorEntries.get(selectorId);
        SelectorEntry.Data data = getBundleData(bundle.data);
        final By by = getBy(data.selenium_by, data.selector_text);
        try {
            Utils.wait(1000);
            new WebDriverWait(_evidence.getWebDriver(), bundle.timeout).until(ExpectedConditions.elementToBeClickable(by));
        }  catch (AtestWebDriverException e) {
            throw e;
        } catch (Exception e) {
            throw new AtestNoSuchWebElementException("waitUntilClickable failed for element "
                    + data.selector_text + " by " + data.selenium_by + " with timeout " + bundle.timeout);
        }
    }

    public void waitUntilClickable(WebElement we, int timeout, String errorMsg) throws AtestNoSuchWebElementException, AtestWebDriverException {
        try {
            Utils.wait(1000);
            Wait<WebDriver> wait = new FluentWait<WebDriver>(_evidence.getWebDriver())
                    .withTimeout(timeout, TimeUnit.SECONDS)
                    .pollingEvery(3, TimeUnit.SECONDS)
                    .ignoring(NoSuchElementException.class);
            wait.until(ExpectedConditions.elementToBeClickable(we));
        }  catch (AtestWebDriverException e) {
            throw e;
        } catch (Exception e) {
            throw new AtestNoSuchWebElementException("element is not visible: " + errorMsg);
        }
    }



    public boolean checkAlert() {
        try {
            Utils.wait(1000);
            WebDriverWait wait = new WebDriverWait(_evidence.getWebDriver(), 1000);
            wait.until(ExpectedConditions.alertIsPresent());
            Alert alert = _evidence.getWebDriver().switchTo().alert();
            return true;
        } catch (Exception e) {
            //exception handling
        }
        return false;
    }
}
