package br.com.softbox.atest_bdd.pages;

import br.com.softbox.core.AtestNoSuchWebElementException;
import br.com.softbox.core.AtestWebDriverException;
import br.com.softbox.core.AtestWebElement;
import br.com.softbox.utils.Utils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 *
 */
public class ProductBundlePage {
    private enum ConfigTypes { COLOR, SIZE, FLAVOR };
    private WebElement _configRootWebElement = null;
    private int _productIndex = 0;

    public ProductBundlePage(int productIndex) {
        _productIndex = productIndex;
    }

    private void reindex() throws AtestNoSuchWebElementException, AtestWebDriverException {
        try {
            Utils.wait(1000);
            List<WebElement> webProducts = AtestWebElement.getInstance().findElements("BundlePage.configProducts.select_products");
            _configRootWebElement = webProducts.get(_productIndex);

        } catch (AtestNoSuchWebElementException | AtestWebDriverException e) {
            throw e;
        } catch (Exception e) {
            throw new AtestNoSuchWebElementException("ProductBundlePage::reindex() failed. " + e.getMessage()) ;
        }
    }

    public void config() throws AtestWebDriverException, AtestNoSuchWebElementException {
        try {
//            reindex();
//            tryToConfigColor();

            reindex();
            tryToConfigSize();

//            reindex();
//            tryToConfigFlavor();

        } catch (AtestNoSuchWebElementException | AtestWebDriverException e) {
            throw e;
        }
    }

    private void tryToConfigFlavor() {
        try {
            List<WebElement> availableFlavors =
                    AtestWebElement.getInstance().findElements(_configRootWebElement, "ProductBundlePage.tryToConfigFlavor.available_flavors");
            if (availableFlavors.size() > 0) {
                availableFlavors.get(0).click();
            }
        } catch (AtestNoSuchWebElementException e) {
            ; // maybe there is no flavor available
        } catch (Exception e) {
            String x = e.getMessage();
        }
    }

    private void tryToConfigSize() {
        try {
            List<WebElement> availableFlavors =
                    AtestWebElement.getInstance().findElements(_configRootWebElement, "ProductBundlePage.tryToConfigFlavor.available_sizes");
            if (availableFlavors.size() > 0) {
                availableFlavors.get(0).click();
            }

        } catch (AtestNoSuchWebElementException e) {
            ; // maybe there is no flavor available
        } catch (Exception e) {
            String x = e.getMessage();
        }
    }

    private void tryToConfigColor() {
        try {
            List<WebElement> availableFlavors =
                    AtestWebElement.getInstance().findElements(_configRootWebElement, "ProductBundlePage.tryToConfigFlavor.available_colors");
            if (availableFlavors.size() > 0) {
                availableFlavors.get(0).click();
            }
        } catch (AtestNoSuchWebElementException e) {
            ; // maybe there is no flavor available
        } catch (Exception e) {
            String x = e.getMessage();
        }
    }

}
