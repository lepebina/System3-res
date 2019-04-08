package br.com.softbox.atest_bdd.pages;
import br.com.softbox.core.*;
import br.com.softbox.utils.Utils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * The page object to control the signIn.
 */
public class LoginPage {
    private AtestEvidence _evidence = AtestEvidence.getInstance();
    private AtestLog _log = new AtestLog("LoginPage");
    private MenuUserAccountPage checklogin = new MenuUserAccountPage();
    private enum SocialType { NONE, FACEBOOK, TWITTER, GOOGLE, LINKEDIN };
    private SocialType _socialType = SocialType.NONE;
    private String _parentWindowHandle;
    /**
     * Ctor.
     */
    public LoginPage() {
    }
    /**
     * Perform the signIn process.
     *
     * @param email the user email
     * @param password the user password.
     * @return true on success, otherwise false.
     */
    public boolean signIn(String email, String password) {
        boolean retCode = false;
        AtestWebElement awe = AtestWebElement.getInstance();
        try {
            if (!isLoginPage()) {
                _log.oops("This is not the login page.");
            } else {
            	awe.findElement("LoginPage.signIn.email_cpf").clear();
                awe.findElement("LoginPage.signIn.email_cpf").sendKeys(email);
                awe.findElement("LoginPage.signIn.password").sendKeys(password);
                awe.findElement("LoginPage.signIn.password_show").click();
                _evidence.screenshot("login_page");
                awe.findElement("LoginPage.signIn.btn_entrar").click();
                awe.waitForPageLoaded();
                retCode = true;
            }
        } catch(AtestNoSuchWebElementException e) {
            _log.oops(e.getMessage());
            _evidence.screenshotOnError("LoginPage_signIn");
        } catch (AtestWebDriverException e) {
            _log.oops(e.getMessage());
        } catch (AtestPageLoadedException e) {
            _log.oops("Main page not fully loaded. " + e.getMessage());
        }
        return retCode;
    }

    public boolean isLoginPage() {
        try {
            Utils.wait(2000);
            AtestWebElement.getInstance().findElement("LoginPage.signIn.email_cpf");
            return true;
        } catch (AtestNoSuchWebElementException e) {
            ;
        } catch (AtestWebDriverException e) {
            ;
        }
        return false;
    }

    public boolean signInFacebook(String fbEmail, String fbPassword, String netShoesEmail, String netShoesPassword) {

        String parentHandle = null;
        WebDriver webdriver = null;

        try {
            webdriver = _evidence.getWebDriver();
        } catch (AtestWebDriverException e) {
            _log.oops(e.getMessage());
        }


        parentHandle = webdriver.getWindowHandle();
        int position = 1;

        AtestWebElement awe = AtestWebElement.getInstance();

        try {

            awe.waitForPageLoaded();

            List<WebElement> Gigya = awe.findElements("LoginGigya.ChooseLogin");
            Gigya.get(position - 1).click();


            webdriver.getWindowHandles();
            for (String winHandle : webdriver.getWindowHandles()) {
                webdriver.switchTo().window(winHandle);
            }


            awe.findElement("FacebookPage.user").sendKeys(fbEmail);
            awe.findElement("FacebookPage.senha").sendKeys(fbPassword);
            _evidence.screenshot("Facebook Login");
            awe.findElement("FacebookPage.senha").submit();

            try {
                awe.findElement("FacebookPage.confirmAccess").click();
                            } catch(AtestNoSuchWebElementException e) {
                _log.info("This facebook account does not need to allow the association with Netshoes/Zattini.");
            }

            webdriver.switchTo().window(parentHandle);

            awe.findElement("LoginPage.AfterLoginGigya").isDisplayed();

            awe.findElement("LoginPage.signIn.email_cpf").sendKeys(netShoesEmail);
            WebElement pass = awe.findElement("LoginPage.signIn.password");
            pass.sendKeys(netShoesPassword);
            pass.submit();


            return true;
        } catch (AtestNoSuchWebElementException e) {
            _log.oops("Unable to find Log In elements " + e.getMessage());

        } catch (AtestWebDriverException e) {
            _log.oops(e.getMessage());
        } catch (AtestPageLoadedException e) {
            _log.info("WARNING: page not loaded: " + e.getMessage());
        }


        return false;
    }

    public boolean register(String email) {
        boolean firstStep = false;
        AtestWebElement awe = AtestWebElement.getInstance();
        try {
            awe.findElement("LoginPage.signIn.email_new_client").sendKeys(email);
            _evidence.screenshot("LoginPage_create_new_user");
            awe.findElement("LoginPage.signIn.button_new_client").click();
            firstStep = true;
        } catch (AtestNoSuchWebElementException e) {
            _log.oops(e.getMessage());
            _evidence.screenshotOnError("LoginPage_register");
        } catch (AtestWebDriverException e) {
            _log.oops(e.getMessage());
        }
        if (firstStep) {
            try {
                Utils.wait(2000);
                awe.findElement("LoginPage.signIn.error_email_already_in_use").isDisplayed();
                _log.oops("Impossible to register new 'Pessoa Fisica', this email is already in use: [" + email + "]");
            } catch (AtestNoSuchWebElementException | AtestWebDriverException e) {
                return true;  // there is no error
            }
        }
        return false;
    }

    public boolean signInGigya(String socialType, String socialEmail, String socialPassword) {
        figureOutTheSocialType(socialType);
        AtestWebElement awe = AtestWebElement.getInstance();
        try {
            awe.findElement(getGigyaSelectorIcon()).click();
            Utils.wait(1000);
            return doSignInGigya(socialEmail, socialPassword);
        } catch (AtestNoSuchWebElementException | AtestWebDriverException e) {
            _log.oops("Unable to find the login icon for [" + socialType + "]");
            _evidence.screenshotOnError("LoginPage_signInGigya");
        }
        return false;
    }

    private boolean doSignInGigya(String socialEmail, String socialPassword) {
        if (_socialType == SocialType.TWITTER) return doSignInTwitter(socialEmail, socialPassword);
        if (_socialType == SocialType.GOOGLE) return doSignInGoogle(socialEmail, socialPassword);
        if (_socialType == SocialType.LINKEDIN) return doSignInLinkedin(socialEmail, socialPassword);
        if (_socialType == SocialType.FACEBOOK) return doSignInFacebook(socialEmail, socialPassword);
        return false;
    }

    private boolean doSignInFacebook(String socialEmail, String socialPassword) {
        if (switchToGigyaSignPage()) {
            GigyaFacebookPage face = new GigyaFacebookPage(socialEmail, socialPassword);
            boolean retCode = face.allow();
            backToParentPage();
            return retCode;
        }
        return false;
    }

    private boolean doSignInLinkedin(String socialEmail, String socialPassword) {
        if (switchToGigyaSignPage()) {
            GigyaLinkedInPage linkedin = new GigyaLinkedInPage(socialEmail, socialPassword);
            boolean retCode = linkedin.allow();
            backToParentPage();
            return retCode;
        }
        return false;
    }

    private boolean doSignInGoogle(String socialEmail, String socialPassword) {
        if (switchToGigyaSignPage()) {
            GigyaGooglePlusPage twitter = new GigyaGooglePlusPage(socialEmail, socialPassword);
            boolean retCode = twitter.allow();
            backToParentPage();
            return retCode;
        }
        return false;
    }

    private boolean doSignInTwitter(String socialEmail, String socialPassword) {
        if (switchToGigyaSignPage()) {
            GigyaTwitterPage twitter = new GigyaTwitterPage(socialEmail, socialPassword);
            boolean retCode = twitter.allow();
            backToParentPage();
            return retCode;
        }
        return false;
    }

    private void backToParentPage() {
        try {
            if (!_parentWindowHandle.isEmpty()) {
                _evidence.getWebDriver().switchTo().window(_parentWindowHandle);
            }
        } catch (AtestWebDriverException e) {
            ;
        }
    }

    private boolean switchToGigyaSignPage() {
        try {
            WebDriver wd = _evidence.getWebDriver();
            _parentWindowHandle = wd.getWindowHandle();
            for (String h : wd.getWindowHandles()) {
                if (!h.equals(_parentWindowHandle)) {
                    wd.switchTo().window(h);
                }
            }
            return true;
        } catch (AtestWebDriverException e) {
            ;
        }
        _parentWindowHandle = "";
        return false;
    }

    private void figureOutTheSocialType(String socialType) {
        if (socialType.equalsIgnoreCase("twitter")) {
            _socialType = SocialType.TWITTER;
        } else if (socialType.equalsIgnoreCase("googleplus")) {
            _socialType = SocialType.GOOGLE;
        } else if (socialType.equalsIgnoreCase("linkedin")) {
            _socialType = SocialType.LINKEDIN;
        } else if (socialType.equalsIgnoreCase("facebook")) {
            _socialType = SocialType.FACEBOOK;
        } else {
            _socialType = SocialType.NONE;
        }
    }

    private String getGigyaSelectorIcon() {
        if (_socialType == SocialType.TWITTER) return "LoginPage.getGigyaSelectorIcon.twitter";
        if (_socialType == SocialType.GOOGLE) return "LoginPage.getGigyaSelectorIcon.g+";
        if (_socialType == SocialType.LINKEDIN) return "LoginPage.getGigyaSelectorIcon.linkedin";
        if (_socialType == SocialType.FACEBOOK) return "LoginPage.getGigyaSelectorIcon.facebook";
        return "";
    }
}
