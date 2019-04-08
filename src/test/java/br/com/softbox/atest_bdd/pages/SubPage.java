package br.com.softbox.atest_bdd.pages;

import br.com.softbox.core.*;
import br.com.softbox.utils.Utils;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;


public class SubPage {
    private final String _EVID_HEADER = "[HomePage] ";
    private AtestEvidence _evidence = AtestEvidence.getInstance();
    private WebDriver _webdriver = null;
    private AtestLog _log = new AtestLog("SubPage");

    public SubPage() {
    	
    }
    
    public boolean IsSubPageOK () {
        boolean retCode = false;

        AtestWebElement awe = AtestWebElement.getInstance();

        try {
        	Utils.wait(1000);
            awe.findElement("SubPage.SubBanner").isDisplayed();
            _evidence.screenshot("Banner_displayed");
            retCode = true;
            Utils.wait(1000);

        } catch (AtestNoSuchWebElementException e) {
            _log.oops("No banner was loaded" + e.getMessage());
            _evidence.screenshotOnError("Banner_displayed");
        } catch (AtestWebDriverException e) {
            _log.oops(e.getMessage());
        }


        return retCode;
    }

    
    public boolean getErrorMessage() {
        boolean retMsg = false;

        AtestWebElement awe = AtestWebElement.getInstance();
    	try {
    		retMsg = awe.findElement("SubPage.SubPageErrorMessage").isDisplayed();
    		_evidence.screenshot("OpsMessage");
        } catch (AtestNoSuchWebElementException | AtestWebDriverException e) {
        	_log.oops("Ops message not found" + e.getMessage());
            _evidence.screenshotOnError("OpsMessage");
        }
    	return retMsg;
    }
}
