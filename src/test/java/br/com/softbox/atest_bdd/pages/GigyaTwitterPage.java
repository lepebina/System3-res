package br.com.softbox.atest_bdd.pages;

import br.com.softbox.core.*;


public class GigyaTwitterPage {
    private AtestEvidence _evidence = AtestEvidence.getInstance();
    private AtestLog _log = new AtestLog("GigyaTwitterPage");
    private String _user;
    private String _password;

    public GigyaTwitterPage(String user, String password) {
        _user = user;
        _password = password;
    }

    public boolean allow() {
        AtestWebElement awe = AtestWebElement.getInstance();

        try {
            awe.findElement("GigyaTwitterPage.allow.user").sendKeys(_user);
            awe.findElement("GigyaTwitterPage.allow.password").sendKeys(_password);
            _evidence.screenshot("Twitter_sign_in");
            awe.findElement("GigyaTwitterPage.allow.submit").click();
            return true;
        } catch (AtestNoSuchWebElementException e) {
            _log.oops("Unable to fill Twitter sign in form.");
            _evidence.screenshotOnError("Twitter_sign_in");
        } catch (AtestWebDriverException e) {
            ;
        }
        return false;
    }
}
