package br.com.softbox.atest_bdd.pages;

import br.com.softbox.core.*;
import br.com.softbox.utils.Utils;


public class GigyaGooglePlusPage {
    private AtestEvidence _evidence = AtestEvidence.getInstance();
    private AtestLog _log = new AtestLog("GigyaGooglePlusPage");
    private String _user;
    private String _password;

    public GigyaGooglePlusPage(String user, String password) {
        _user = user;
        _password = password;
    }

    public boolean allow() {
        AtestWebElement awe = AtestWebElement.getInstance();

        try {
            final String windowHandle = _evidence.getWebDriver().getWindowHandle();
            awe.findElement("GigyaGooglePlusPage.allow.user").sendKeys(_user);
            awe.findElement("GigyaGooglePlusPage.allow.user_next").click();
            Utils.wait(2000);
            awe.findElement("GigyaGooglePlusPage.allow.password").sendKeys(_password);
            awe.findElement("GigyaGooglePlusPage.allow.submit").click();
            Utils.wait(2000);
            for (String h : _evidence.getWebDriver().getWindowHandles()) {
                if (h.equals(windowHandle)) {
                    // The window was not closed automatically after clicking on last submit.
                    // So the window still alive and waiting for the "allowance"
                    _evidence.screenshot("GooglePlus_sign_in");
                    awe.findElement("GigyaGooglePlusPage.allow.approve_access").click();
                    break;
                }
            }
            return true;
        } catch (AtestNoSuchWebElementException e) {
            _log.oops("Unable to fill GooglePlus sign in form.");
            _evidence.screenshotOnError("GooglePlus_sign_in");
        } catch (AtestWebDriverException e) {
            ;
        }
        return false;
    }
}
