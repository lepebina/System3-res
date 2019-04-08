package br.com.softbox.atest_bdd.stepdefs.combined;

import br.com.softbox.atest_bdd.pages.LoginPage;
import br.com.softbox.atest_bdd.pages.MenuUserAccountPage;
import br.com.softbox.atest_bdd.pages.UserAccountPage;
import br.com.softbox.core.*;
import br.com.softbox.utils.Utils;
import cucumber.api.PendingException;
import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class LoginSteps {
    private AtestEvidence _evidence = AtestEvidence.getInstance();
    private Scenario _scenario = null;
    private String _initialSite = "";
    private LoginPage dologin = new LoginPage();
    private MenuUserAccountPage checklogin = new MenuUserAccountPage();
    private UserAccountPage checkGigyalogin = new UserAccountPage();
    private AtestLog _log = new AtestLog("LoginSteps");
    private String _storeUsername;
    private String _storeEmail;
    private String _storePassword;
    private String _socialType;

    @Before
    public void before(Scenario scenario) {
        _scenario = scenario;
    }

    @After
    public void after() {
        if (_evidence.isCurrentScenarioInZombieMode()) {                 // Something goes wrong within this scenario.
            _evidence.finishScenario(_scenario.getName(), "fail");
        }
    }

    @Given("^I am at \"([^\"]*)\" <login>$")
    public void accessNetShoes(String url) throws Throwable {
        _evidence.startScenario(_log, _scenario.getName());
        TestStepData step = new TestStepData(_scenario.getName()
                , "I am at %URL% <login>")
                .add("URL", url);
        _initialSite = url;
        try {
            _evidence.startStep(_log, step);
            _evidence.getWebDriver().get(url);
            _evidence.setInitialSiteURL(url);
            _evidence.setCurrentHost(url);
            AtestWebElement.getInstance().waitForPageLoaded();
            _evidence.passStep(step);
        } catch (AtestPageLoadedException ex) {
            _log.info("WARNING: The initial page was not fully loaded.");
        } catch (AtestWebDriverException e) {
            _evidence.failStep(step);
            Utils.isTrue.go(false, _log.me(), "loading initial page. " + e.getMessage());
        }
    }

    @When("^I log in with facebook \"([^\"]*)\" and \"([^\"]*)\" and with Netshoes \"([^\"]*)\" and \"([^\"]*)\"$")
    public void logInFacebook(String fbEmail, String fbPass, String nsEmail, String nsPass) throws Throwable {
        TestStepData step = new TestStepData(_scenario.getName()
                , "I log in with facebook %FB_EMAIL% and %FB_PASSWORD% and with Netshoes %NS_EMAIL% and %NS_PASSWORD%")
                .add("FB_EMAIL", fbEmail)
                .add("FB_PASSWORD", fbPass)
                .add("NS_EMAIL", nsEmail)
                .add("NS_PASSWORD", nsPass);
        _evidence.startStep(_log, step);

        checklogin.chooseOptionLogin();
        dologin.signIn(nsEmail, nsPass);
        checklogin.chooseOptionMinhaConta();
        checkGigyalogin.socialNetworkDisconnect();
        checklogin.chooseOptionSair();
        checklogin.chooseOptionLogin();

        Utils.isTrue.go(dologin.signInFacebook(fbEmail, fbPass, nsEmail, nsPass), _log.me(), "Logged successfully");
        _evidence.passStep(step);
    }

    @Then("^I will be redirected to homepage logged successfully with facebook under the name \"([^\"]*)\"$")
    public void isLoggedDoneSuccessfully(String username) throws Throwable {
        TestStepData step = new TestStepData(_scenario.getName()
                , "I will be redirected to homepage logged successfully with facebook under the name %USER%")
                .add("USER", username);
        _evidence.startStep(_log, step);

        String socialName = "facebook";
        _log.step("Logged under the username: " + username);
        checklogin.chooseOptionMinhaConta();
        Utils.isTrue.go(checkGigyalogin.socialNetworkValidation("facebook"), _log.me(), "Verify if the user is connected with social network");
        _evidence.passStep(step);
        _evidence.screenshotAtEnd("Logged_with_facebook");

        checkGigyalogin.socialNetworkDisconnect();
        checklogin.chooseOptionSair();
        _evidence.finishScenario(_scenario.getName(), "passed");
    }

    @When("^remove every social account from \"([^\"]*)\", \"([^\"]*)\" and \"([^\"]*)\"$")
    public void removeSocialAccounts(String storeUsername, String storeEmail, String storePassword) {
        TestStepData step = new TestStepData(_scenario.getName()
                , "remove every social account from %STORE_USERNAME%, %STORE_EMAIL% and %STORE_PASSWORD%")
                .add("STORE_USERNAME", storeUsername)
                .add("STORE_EMAIL", storeEmail)
                .add("STORE_PASSWORD", storePassword);
        _evidence.startStep(_log, step);
        _storeUsername = storeUsername;
        _storeEmail = storeEmail;
        _storePassword = storePassword;
        Utils.isTrue.go(checklogin.chooseOptionLogin(), _log.me(), "menu option login");
        Utils.isTrue.go(dologin.signIn(storeEmail, storePassword), _log.me(), "log in");
        Utils.isTrue.go(checklogin.chooseOptionMinhaConta(), _log.me(), "menu option minha conta");
        Utils.isTrue.go(checkGigyalogin.removeSocialAccounts(), _log.me(), "remove social networks");
        Utils.isTrue.go(checklogin.chooseOptionSair(), _log.me(), "log out");
        _evidence.passStep(step);
    }

    @And("^log in using \"([^\"]*)\", \"([^\"]*)\" and \"([^\"]*)\"$")
    public void logInGigya(String socialType, String socialEmail, String socialPassword) {
        TestStepData step = new TestStepData(_scenario.getName()
                , "log in using %SOCIAL_TYPE%, %SOCIAL_ACCOUNT% and %SOCIAL_PASSWORD%")
                .add("SOCIAL_TYPE", socialType)
                .add("SOCIAL_ACCOUNT", socialEmail)
                .add("SOCIAL_PASSWORD", socialPassword);
        _evidence.startStep(_log, step);
        Utils.isTrue.go(checklogin.chooseOptionLogin(), _log.me(), "menu option login - prepare for gigya");
        Utils.isTrue.go(dologin.signInGigya(socialType
                                           , socialEmail
                                           , socialPassword), _log.me(), "log in gigya");
        Utils.isTrue.go(dologin.signIn(_storeEmail, _storePassword), _log.me(), "log in");
        _socialType = socialType;
        _evidence.passStep(step);
    }

    @Then("^expect 'Minha Conta' page shows it is connect via social account$")
    public void checkLogInSocial() {
        TestStepData step = new TestStepData(_scenario.getName()
                , "expect 'Minha Conta' page shows it is connect via social account");
        _evidence.startStep(_log, step);
        Utils.isTrue.go(checklogin.chooseOptionMinhaConta(), _log.me(), "menu option minha conta");
        Utils.isTrue.go(checkGigyalogin.checkSocialExists(_socialType), _log.me(), "check for social account [" + _socialType + "]");
        _evidence.passStep(step);
        _evidence.finishScenario(_scenario.getName(), "passed");
        _evidence.screenshotAtEnd("Login_social_" + _socialType);
    }

}

