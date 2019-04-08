package br.com.softbox.atest_bdd.pages;

import br.com.softbox.core.*;
import org.openqa.selenium.WebElement;

import java.util.List;


public class ProductListPage {
    private String _productSearchedTerm;
    private AtestLog _log = new AtestLog("ProductListPage");
    private AtestEvidence _evidence = AtestEvidence.getInstance();

    public ProductListPage(String productSearchedTerm) {
        _productSearchedTerm = productSearchedTerm;
    }
    public boolean hasProducts() {
        try {
            List<WebElement> products = AtestWebElement.getInstance().findElements("ProductListPage.isProductListPage.product_list");
            _evidence.screenshot("ProductListPage_products");
            return !products.isEmpty();
        } catch (AtestNoSuchWebElementException | AtestWebDriverException e) {
            _log.oops(e.getMessage());
            _evidence.screenshotOnError("ProductListPage_no_products_found");
        }
        return false;
    }

}
