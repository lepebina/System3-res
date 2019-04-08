package br.com.softbox.atest_bdd.pages;

import br.com.softbox.core.*;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;


public class PaymentConfirmationPage {
    private AtestEvidence _evidence = AtestEvidence.getInstance();
    private AtestLog _log = new AtestLog("PaymentConfirmationPage");

    public PaymentConfirmationPage() {
    }

    public boolean isConfirmationPageForBoleto() {
        try {
            return _evidence.getWebDriver().getPageSource().contains("imprimir boleto");
        } catch (AtestWebDriverException e) {
            _log.oops(e.getMessage());
            return false;
        }
    }

    public String getOrderNumber() {
        AtestWebElement awe = AtestWebElement.getInstance();
        String order = "";
        try {
            order = awe.findElement("PaymentConfirmationPage.getOrderNumber.order_number").getText().trim();
            _evidence.screenshot("payment_confirmation_page");
        } catch (AtestNoSuchWebElementException e) {
            _evidence.screenshotOnError("PaymentConfirmationPage_order_number_not_found");
            _log.oops("order number not found. " + e.getMessage());
        } catch (AtestWebDriverException e) {
            _log.oops(e.getMessage());
        }
        return order;
    }

    public boolean GetMultiplusPoints() {

        boolean rc = false;

        AtestWebElement awe = AtestWebElement.getInstance();
        JavascriptExecutor jse = null;
        try {
            awe.waitForPageLoaded();
            jse = (JavascriptExecutor) _evidence.getWebDriver();
          WebElement multiplusPoints = awe.findElement("PaymentConfirmationPage.GetMultiplus");
            jse.executeScript("arguments[0].scrollIntoView(true);", multiplusPoints);
            multiplusPoints.click();
            rc = true;
        } catch (AtestNoSuchWebElementException e) {
            _log.oops("Unable to find the option 'Get Multiplus Points'" + e.getMessage());
        } catch (AtestWebDriverException e) {
            _log.oops(e.getMessage());
        } catch (AtestPageLoadedException e) {
            _log.info("WARNING: page not loaded: " + e.getMessage());
        }


        return rc;
    }

    public boolean VerifyMultiplusPointsWasRequested() {

        boolean rc = false;

        AtestWebElement awe = AtestWebElement.getInstance();

        try {
            awe.waitForPageLoaded();
            awe.findElement("PaymentConfirmationPage.AssertMultiplus").isDisplayed();
            _evidence.screenshot("Multiplus Points Requested");
            rc = true;

        } catch (AtestNoSuchWebElementException e) {
            _evidence.screenshotOnError("Unable to find the message that assures that multiplus points was successfully requested" + e.getMessage());

        } catch (AtestWebDriverException e) {
            _log.oops(e.getMessage());
        } catch (AtestPageLoadedException e) {
            _log.info("WARNING: page not loaded: " + e.getMessage());
        }

        return rc;
    }

    public boolean AccessMultiplusRulesPage() {

        boolean rc = false;

        AtestWebElement awe = AtestWebElement.getInstance();

        try {
            awe.waitForPageLoaded();
            awe.findElement("PaymentConfirmationPage.MultiplusRules").click();

            rc = true;
        } catch (AtestNoSuchWebElementException e) {
            _log.oops("Unable to find the link to 'Multiplus Rules' page " + e.getMessage());
        } catch (AtestWebDriverException e) {
            _log.oops(e.getMessage());
        } catch (AtestPageLoadedException e) {
            _log.info("WARNING: page not loaded: " + e.getMessage());
        }
        return rc;
    }

    public boolean VerifyIfIsMultiplusRulesPage() {

        boolean rc = false;

        AtestWebElement awe = AtestWebElement.getInstance();

        try {
            awe.waitForPageLoaded();
            awe.findElement("PaymentConfirmationPage.IsMultiplusRulesPage").isDisplayed();
            _evidence.screenshot("Multiplus Rules Page");
            rc = true;
        } catch (AtestNoSuchWebElementException e) {
            _log.oops("Unable to validate that the current page is 'Multiplus Rules' page " + e.getMessage());
        } catch (AtestWebDriverException e) {
            _log.oops(e.getMessage());
        } catch (AtestPageLoadedException e) {
            _log.info("WARNING: page not loaded: " + e.getMessage());
        }
        return rc;
    }


}
