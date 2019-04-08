package br.com.softbox.atest_bdd.pages;

import br.com.softbox.core.*;
import br.com.softbox.utils.Utils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class BundlePage {
   private AtestEvidence _evidence = AtestEvidence.getInstance();
   private AtestLog _log = new AtestLog("BundlePage");

    /**
     *
     */
    public BundlePage() {
    }
    /**
     * @return
     */
    public boolean buy() {
        try {
            if (configProducts()) {
                Utils.wait(2000);
                AtestWebElement.getInstance().findElement("BundlePage.buy.button_comprar").click();
                return !hasError();
            }
        } catch (AtestNoSuchWebElementException | AtestWebDriverException ex) {
            _log.oops(ex.getMessage());
        }
        return false;
    }

    private boolean hasError() {
        try {
            Utils.wait(1500);
            AtestWebElement.getInstance().findElement("BundlePage.buy.error");
            _log.oops("It's not possible to select size of any product.");
            _evidence.screenshotOnError("bundle_product_size_selection");
            return true;
        } catch (AtestNoSuchWebElementException e) {
            return false;  // does not have an error message
        } catch (AtestWebDriverException e) {
            return false;
        }
    }

    private boolean configProducts() {
        try {
            List<ProductBundlePage> products = new ArrayList<>();
            List<WebElement> webProducts = AtestWebElement.getInstance().findElements("BundlePage.configProducts.select_products");
            for (int i = 0; i < webProducts.size(); ++i) {
                ProductBundlePage product = new ProductBundlePage(i);
                products.add(product);
                product.config();
            }
            return true;
        } catch (AtestNoSuchWebElementException | AtestWebDriverException e) {
            ;
        }
        return false;
    }
}

