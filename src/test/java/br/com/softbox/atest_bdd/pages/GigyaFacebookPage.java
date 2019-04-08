package br.com.softbox.atest_bdd.pages;

import br.com.softbox.core.*;


public class GigyaFacebookPage {
    private AtestEvidence _evidence = AtestEvidence.getInstance();
    private AtestLog _log = new AtestLog("GigyaFacebookPage");
    private String _user;
    private String _password;

    public GigyaFacebookPage(String user, String password) {
        _user = user;
        _password = password;
    }

    public boolean allow() {
        AtestWebElement awe = AtestWebElement.getInstance();

        try {
            awe.findElement("GigyaFacebookPage.allow.user").sendKeys(_user);
            awe.findElement("GigyaFacebookPage.allow.password").sendKeys(_password);
            awe.findElement("GigyaFacebookPage.allow.submit").click();
            return true;
        } catch (AtestNoSuchWebElementException e) {
            _log.oops("Unable to fill Facebook sign in form.");
            _evidence.screenshotOnError("Facebook_sign_in");
        } catch (AtestWebDriverException e) {
            ;
        }
        return false;
    }}
