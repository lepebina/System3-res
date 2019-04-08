package br.com.softbox.atest_bdd.pages;


import br.com.softbox.core.*;
import br.com.softbox.utils.Utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.internal.WebElementToJsonConverter;

import java.util.List;
import static org.junit.Assert.*;


public class WishlistPage {

    private final String _EVID_HEADER = "[WishlistPage] ";
    private AtestEvidence _evidence = AtestEvidence.getInstance();
    private WebDriver _webdriver = null;
    private ProductPage getSize = new ProductPage();
    private AtestLog _log = new AtestLog("WishlistSteps");


    public boolean AddToWishList() {

        boolean retCode = false;

        AtestWebElement awe = AtestWebElement.getInstance();

        getSize.clickAnyProductSize();

        String _productName = null;
        try {
            _productName = _evidence.getWebDriver().getCurrentUrl();
        } catch (AtestWebDriverException e) {
            _log.oops(e.getMessage());
        }

        Utils.wait(1000);


        try {
            awe.waitForPageLoaded();
            awe.findElement("Details.AddToWishlist").click();
            _evidence.screenshot("adding_to_wishlist");
            Thread.sleep(2);

            List<WebElement> _allElementsAdded = awe.findElements("Wishlist.ProductAdded");
            int num = 0;

            for (WebElement check : _allElementsAdded) {

                String _newUrl = check.getAttribute("href");

                if (_productName.contains(_newUrl)) {

                    num = 1;

                    break;
                }

            }

            if (num == 1) {
                retCode = true;
            }



        } catch (AtestNoSuchWebElementException e) {
            _log.oops("Unable to find the link to add the product to Wishlist" + e.getMessage());
            _evidence.screenshotOnError("adding_to_wishlist");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (AtestWebDriverException e) {
            _log.oops(e.getMessage());
        } catch (AtestPageLoadedException e) {
            _log.info("WARNING: page not loaded: " + e.getMessage());
        }

        return retCode;
    }

    public boolean AddToWishListNotLogged() {

        boolean retCode = false;

        Utils.wait(1000);
        getSize.clickAnyProductSize();
        AtestWebElement awe = AtestWebElement.getInstance();
        Utils.wait(1000);

        try {
            awe.waitForPageLoaded();
            awe.findElement("Details.AddToWishlist").click();
            retCode = true;

        } catch (AtestNoSuchWebElementException e) {
            _log.oops("Unable to find the element related to add products to Wishlist" + e.getMessage());
        } catch (AtestWebDriverException e) {
            _log.oops(e.getMessage());
        } catch (AtestPageLoadedException e) {
            _log.info("WARNING: page not loaded: " + e.getMessage());
        }

        return retCode;
    }


    public boolean VerifyIfProductAdded() {

        boolean retCode = false;

        AtestWebElement awe = AtestWebElement.getInstance();
        try {
            awe.waitForPageLoaded();
            awe.findElement("Wishlist.ProductAdded");

            if(!awe.findElements("Wishlist.ProductAdded").isEmpty()){
                retCode = true;
                _evidence.screenshot("product_added_successfully");

            }
            _evidence.screenshot("Product Added To Wishlist");
        } catch (AtestNoSuchWebElementException e) {
            _log.oops("Unable to find products added to Wishlist" + e.getMessage());
            _evidence.screenshotOnError("product_added_successfully");
        } catch (AtestWebDriverException e) {
            _log.oops(e.getMessage());
        } catch (AtestPageLoadedException e) {
            _log.info("WARNING: page not loaded: " + e.getMessage());
        }

        return retCode;

    }


    public boolean CleanWishList() {

        boolean retCode = false;

        AtestWebElement awe = AtestWebElement.getInstance();


        try {
            awe.waitForPageLoaded();
            List<WebElement> deletebutton = awe.findElements("Wishlist.DeletarProduto");
            int num = deletebutton.size();


            for (WebElement check : deletebutton) {


                if (num > 0) {

                    check.click();

                } else {

                    break;
                }
            }

            _evidence.screenshot("Wishlist_clean");
            retCode = true;

        } catch (AtestNoSuchWebElementException e) {
            _log.info("No delete button found. Maybe there's no item to be deleted" + e.getMessage());
            _evidence.screenshot("Wishlist_already_clean");
        } catch (AtestWebDriverException e) {
            _log.oops(e.getMessage());
        } catch (AtestPageLoadedException e) {
            _log.info("WARNING: page not loaded: " + e.getMessage());
        }

        return retCode;

    }

    public boolean WishlistEmpty(){

        boolean retCode = false;

        AtestWebElement awe = AtestWebElement.getInstance();
        try {
            awe.waitForPageLoaded();
            awe.findElement("Wishlist.WishlistVazia").isDisplayed();
            retCode = true;
        } catch (AtestNoSuchWebElementException e) {
            _log.oops("Unable to find the element that assures that Wishlist is empty." + e.getMessage());
        } catch (AtestWebDriverException e) {
            _log.oops(e.getMessage());
        } catch (AtestPageLoadedException e) {
            _log.info("WARNING: page not loaded: " + e.getMessage());
        }
        return retCode;
    }
}













