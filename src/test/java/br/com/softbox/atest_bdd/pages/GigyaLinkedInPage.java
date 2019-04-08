package br.com.softbox.atest_bdd.pages;

import br.com.softbox.core.*;
import br.com.softbox.utils.Utils;
import org.openqa.selenium.WebElement;


public class GigyaLinkedInPage {
    private AtestEvidence _evidence = AtestEvidence.getInstance();
    private AtestLog _log = new AtestLog("GigyaLinkedInPage");
    private String _user;
    private String _password;

    public GigyaLinkedInPage(String user, String password) {
        _user = user;
        _password = password;
    }

    public boolean allow() {
        AtestWebElement awe = AtestWebElement.getInstance();

        try {
            awe.findElement("GigyaLinkedInPage.allow.user").sendKeys(_user);
            awe.findElement("GigyaLinkedInPage.allow.password").sendKeys(_password);
            awe.findElement("GigyaLinkedInPage.allow.submit").click();
            return true;
        } catch (AtestNoSuchWebElementException e) {
            _log.oops("Unable to fill LinkedIn sign in form.");
            _evidence.screenshotOnError("LinkedIn_sign_in");
        } catch (AtestWebDriverException e) {
            ;
        }
        return false;
    }

}
