package br.com.softbox.atest_bdd.stepdefs.stelo;

import br.com.softbox.atest_bdd.pages.*;
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


public class SteloSteps {
    private AtestEvidence _evidence = AtestEvidence.getInstance();
    private Scenario _scenario = null;
    private String _initialSite = "";
    public AtestLog _log = new AtestLog("SteloSteps");

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

    @Given("^I am at \"(.*)\"$")
    public void visitSteloInitialSite(String url) {
        _evidence.startScenario(_log, _scenario.getName());
        TestStepData step = new TestStepData(_scenario.getName()
                , "I am at %URL%")
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
            _evidence.failStep(step);
        } catch (AtestWebDriverException e) {
            _evidence.failStep(step);
            Utils.isTrue.go(false, _log.me(), "loading initial page. " + e.getMessage());
        }
    }

    @And("^I am logged as \"(.*)\" with \"(.*)\" and \"(.*)\"$")
    public void doLogin(String userName, String email, String password) {
        TestStepData step = new TestStepData(_scenario.getName()
                , "I am logged as %USERNAME% with %EMAIL% and %PASSWORD%")
                .add("USERNAME", userName)
                .add("EMAIL", email)
                .add("PASSWORD", password);
        _evidence.startStep(_log, step);
        MenuUserAccountPage menuAccount = new MenuUserAccountPage();
        menuAccount.chooseOptionLogin();
        LoginPage login = new LoginPage();
        Utils.isTrue.go(login.signIn(email, password), _log.me(), "sign in");
        Utils.isTrue.go(menuAccount.isLogged(userName), _log.me(), "verify whether the user is logged as [" + userName + "]");
        CartPage cart = new CartPage();
        cart.cleanUp();
        _evidence.passStep(step);
    }


    @And("^pay with card: \"(.*)\", \"(.*)\", \"(.*)\", \"(.*)\" and \"(.*)\"$")
    public void payWithCreditCard(String cardHolder, String cardUserName, String cardNum, String cardValidity, String cardCVV) {
        TestStepData step = new TestStepData(_scenario.getName()
                , "pay with credit card data: %CARD_HOLDER%, %CARD_USERNAME%, %CARD_NUM%, %CARD_VALIDITY% and %CARD_CVV%")
                .add("CARD_HOLDER", cardHolder)
                .add("CARD_USERNAME", cardUserName)
                .add("CARD_NUM", cardNum)
                .add("CARD_VALIDITY", cardValidity)
                .add("CARD_CVV", cardCVV);
        _evidence.startStep(_log, step);
        PaymentPage pay = new PaymentPage();
        Utils.isTrue.go(pay.payWithCreditCard(cardHolder, cardUserName, cardNum, cardValidity, cardCVV), _log.me(), "pay with credit card");
        _evidence.passStep(step);
    }

    @And("^I buy the \"(.*)\"$")
    public void buyTheProduct(String productURL) {
        TestStepData step = new TestStepData(_scenario.getName()
                , "buy the %PRODUCT_URL%")
                .add("PRODUCT_URL", productURL);
        _evidence.startStep(_log, step);
        if (productURL.isEmpty()) {
            _log.trace("ERROR: invalid PRODUCT_URL");
            Utils.isTrue.go(false, _log.me(), "empty product");
        }
        ProductPage p = new ProductPage();
        String prod = Utils.getURL(_initialSite, productURL);

        Utils.isFalse.go(prod.isEmpty(), _log.me(), "could not build the URL for the product");
        Utils.isTrue.go(p.navigateTo(prod), _log.me(), "could not open product URL");
        Utils.isTrue.go(p.buy(), _log.me(), "buy product");

        CartPage cart = new CartPage();

        Utils.isTrue.go(cart.finalizeOrder(), _log.me(), "finalize order");
        _evidence.passStep(step);
    }

    @Then("^I will be redirected to the Stelo login page$")
    public void checkIsSteloLoginPage() {
        TestStepData step = new TestStepData(_scenario.getName(), "I will be redirected to the Stelo login page");
        _evidence.startStep(_log, step);
        PaymentPage pay = new PaymentPage();
        Utils.isTrue.go(pay.payWithStelo(), _log.me(), "selecting pay with stelo");

        SteloPage sp = new SteloPage();
        Utils.isTrue.go(sp.isHomePage(), _log.me(), "check for Stelo page");

        _evidence.passStep(step);
        _evidence.finishScenario(_scenario.getName(), "passed");
    }
}
