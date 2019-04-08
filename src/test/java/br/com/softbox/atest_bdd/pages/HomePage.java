package br.com.softbox.atest_bdd.pages;

import br.com.softbox.core.*;
import br.com.softbox.utils.Utils;

import org.apache.bcel.generic.Visitor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import static org.junit.Assert.*;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.List;


public class HomePage {
    private final String _EVID_HEADER = "[HomePage] ";
    private AtestEvidence _evidence = AtestEvidence.getInstance();
    private WebDriver _webdriver = null;
    private AtestLog _log = new AtestLog("HomePage");


    public boolean SearchOk (String searchFor){

        boolean retCode = false;

        AtestWebElement awe = AtestWebElement.getInstance();


        try {
            awe.waitForPageLoaded();
            awe.findElement("Search.searchbar").sendKeys(searchFor);
            _evidence.screenshot("searching_for_valid_product");
            awe.findElement("Search.searchbarbutton").click();


           retCode = true;
        }  catch (AtestNoSuchWebElementException e) {
            _log.oops("Unable to search bar elements " + e.getMessage());
            _evidence.screenshotOnError("searching_for_valid_product");
        } catch (AtestWebDriverException e) {
            _log.oops(e.getMessage());
        } catch (AtestPageLoadedException e) {
            _log.info( "WARNING: page not loaded: " + e.getMessage());
        }
        return retCode;
    }

    public boolean SearchNOk (String searchForNok){
        boolean retCode = false;

        AtestWebElement awe = AtestWebElement.getInstance();


        try {
            awe.waitForPageLoaded();
            awe.findElement("Search.searchbar").sendKeys(searchForNok);
            _evidence.screenshot("Searching_for_invalid_product");
            awe.findElement("Search.searchbarbutton").click();


           retCode = true;
        }   catch (AtestNoSuchWebElementException e) {
            _log.oops("Unable to find SearchBar elements " + e.getMessage());
            _evidence.screenshotOnError("Searching_for_invalid_product");
        } catch (AtestWebDriverException e) {
            _log.oops(e.getMessage());
        }  catch (AtestPageLoadedException e) {
            _log.info("WARNING: page not loaded: " + e.getMessage());
        }

        return retCode;
    }

    public boolean isSearchOk(){
        boolean retCode = false;

        AtestWebElement awe = AtestWebElement.getInstance();


        try {
            awe.waitForPageLoaded();
            awe.findElement("Search.resultOK");
            _evidence.screenshot("Valid_search_done");
           retCode = true;

        }   catch (AtestNoSuchWebElementException e) {
            _log.oops("Unable to find any product result. It means the search wasn't valid " + e.getMessage());
            _evidence.screenshotOnError("Valid_search_done");
        } catch (AtestWebDriverException e) {
            _log.oops(e.getMessage());
        } catch (AtestPageLoadedException e) {
            _log.info("WARNING: page not loaded: " + e.getMessage());
        }

        return retCode;
    }


    public boolean isSearchNotOk(){

        boolean retCode = false;

        AtestWebElement awe = AtestWebElement.getInstance();

        try {
            awe.waitForPageLoaded();
            awe.findElement("Search.resultNOK");
            _evidence.screenshot("Invalid_search_done");
           retCode = true;
        }   catch (AtestNoSuchWebElementException e) {
            _log.oops("Unable to find the OPS message. It means the search wasn't invalid " + e.getMessage());
            _evidence.screenshotOnError("Invalid_search_done");
        } catch (AtestWebDriverException e) {
            _log.oops(e.getMessage());
        } catch (AtestPageLoadedException e) {
            _log.info("WARNING: page not loaded: " + e.getMessage());
        }

        return retCode;
    }


    public final class EmailGenerator {
        private SecureRandom random = new SecureRandom();

        public String nextSessionId() {
            return new BigInteger(30, random).toString(15);
        }
    }

    public String getEmail() {
        EmailGenerator gtEmail = new EmailGenerator();
        return gtEmail.nextSessionId();

    }


    public boolean registerOfertasDescontos(String userEmail){

        boolean retCode = false;
        AtestWebElement awe = AtestWebElement.getInstance();
        try {
            awe.findElement("Subscribe.Email").sendKeys(userEmail);
            awe.findElement("Subscribe.GenderM").click();
            _evidence.screenshot("Subscription_information");
            try {
                awe.findElement("Subscribe.OkBtn").click();
                Select teamList = new Select(awe.findElement("Subscribe.Team"));
                teamList.selectByVisibleText("São Paulo");
                Select sportList = new Select(awe.findElement("Subscribe.Sport"));
                sportList.selectByVisibleText("Skate");
                _evidence.screenshot("Subscription_information_additional_Netshoes");
            } catch(AtestNoSuchWebElementException e) {
                _log.info("This site doesn't contains Sport and Team selection. We're presuming it'Zattini.");
            }

            awe.findElement("Subscribe.FinishBtn").click();
            retCode = true;
        } catch (AtestNoSuchWebElementException e) {
            _log.oops("Unable to find the subscription elements" + e.getMessage());
            _evidence.screenshotOnError("Subscription_information");
        } catch (AtestWebDriverException e) {
            _log.oops(e.getMessage());
        }
        return retCode;
    }

    public boolean AssertRegisteredOfertasDescontos() {
        boolean retCode = false;
        AtestWebElement awe = AtestWebElement.getInstance();
        try {
            awe.waitForPageLoaded();
            awe.findElement("Subscribe.AssertRegistered");
            String subscribeMsg = awe.findElement("Subscribe.AssertRegistered").getText();
            if (subscribeMsg.contains("E-mail cadastrado com sucesso")) {
                _evidence.screenshot("User_subscribed");
                retCode = true;
            }
            if(subscribeMsg.contains("E-mail já cadastrado.")) {
                _log.oops("User is already subscribed");
                _evidence.screenshot("User_already_subscribed");
            }
        } catch (AtestNoSuchWebElementException e) {
            _log.oops("Unable to find the result message that verifies if the subscription was successfully or not" + e.getMessage());
            _evidence.screenshotOnError("User_subscription_message");
        } catch (AtestWebDriverException e) {
            _log.oops(e.getMessage());
        } catch (AtestPageLoadedException e) {
            _log.info( "WARNING: page not loaded: " + e.getMessage());
        }
        return retCode;
    }



    public boolean makeSearchForAutoComplete(String partialSearch) {
       boolean retCode = false;
        AtestWebElement awe = AtestWebElement.getInstance();


        try {
            awe.waitForPageLoaded();
            awe.findElement("Search.searchbar").sendKeys(partialSearch);
            Utils.wait(1000);
            _evidence.screenshot("search_suggestion");

            retCode = true;

        }  catch (AtestNoSuchWebElementException e) {
            _log.oops("Unable to find search bar" + e.getMessage());
            _evidence.screenshotOnError("search_suggestion");
        } catch (AtestWebDriverException e) {
            _log.oops(e.getMessage());

        } catch (AtestPageLoadedException e) {
            _log.info("WARNING: page not loaded: " + e.getMessage());
        }

        return retCode;

    }

    public boolean selectAutoComplete() {

        boolean retCode = false;
        AtestWebElement awe = AtestWebElement.getInstance();


        try {

            awe.waitForPageLoaded();

            List<WebElement> autoCompleteSuggestions = awe.findElements("HomePage.AutoCompleteSearch");




            Actions action = new Actions(_evidence.getWebDriver());
            action.moveToElement(autoCompleteSuggestions.get(1)).click().perform();


            retCode = true;

        }  catch (AtestNoSuchWebElementException e) {
        _log.oops("Unable to find the auto-complete item" + e.getMessage());

    } catch (AtestWebDriverException e) {
        _log.oops(e.getMessage());
    } catch (AtestPageLoadedException e) {
            _log.info("WARNING: page not loaded: " + e.getMessage());
        }

        return retCode;
    }


    public boolean goShoppingCart(){
        boolean retCode = false;

        AtestWebElement awe = AtestWebElement.getInstance();

        try {
            awe.findElement("CartPage.AccessCart").click();
            retCode = true;
        } catch (AtestNoSuchWebElementException e) {
            e.printStackTrace();
        } catch (AtestWebDriverException e) {
            e.printStackTrace();
        }

        return retCode;
    }
}
