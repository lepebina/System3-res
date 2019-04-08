package br.com.softbox.atest_bdd.stepdefs.combined;

import br.com.softbox.atest_bdd.pages.*;
import br.com.softbox.core.*;
import br.com.softbox.utils.Utils;
import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;



public class MultiplusSteps {
    private AtestEvidence _evidence = AtestEvidence.getInstance();
    private Scenario _scenario = null;
    private String _initialSite = "";
    private ProductPage buyMultiplusProduct = new ProductPage();
    private PaymentConfirmationPage confirmPayment = new PaymentConfirmationPage();
    private AtestLog _log = new AtestLog("MultiplusSteps");
    private MenuUserAccountPage chooseMenu = new MenuUserAccountPage();

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

    @Given("^I am at \"(.*)\" <multiplus>")
    public void accessNetshoes(String url) throws Throwable {
        _evidence.startScenario(_log, _scenario.getName());
        TestStepData step = new TestStepData(_scenario.getName()
                , "I am at %URL% <multiplus>")
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
    @When ("^I log in with \"([^\"]*)\" and \"([^\"]*)\" to finish my payment with a \"([^\"]*)\"$")
    public void buyMultiplus(String userName, String password, String productUrl) throws Throwable {
        TestStepData step = new TestStepData(_scenario.getName()
                , "I log in with %USERNAME% and %PASSWORD% to finish my payment with a %PRODUCT_URL%")
                .add("USERNAME", userName)
                .add("PASSWORD", password)
                .add("PRODUCT_URL", productUrl);
        _evidence.startStep(_log, step);
        String prod = Utils.getURL(_initialSite, productUrl);
        Utils.isTrue.go(buyMultiplusProduct.BuyMultiplus(prod, userName, password), _log.me(), "Multiplus product bought");
        _evidence.passStep(step);
    }


    @And("^I click to receive multiplus$")
    public void receiveMultiplus() throws Throwable {
        TestStepData step = new TestStepData(_scenario.getName(), "I click to receive multiplus");
        _evidence.startStep(_log, step);
        Utils.isTrue.go(confirmPayment.GetMultiplusPoints(), _log.me(), "Multiplus product requested");
        _evidence.passStep(step);
    }

    @Then("^a successful message is display$")
    public void isMultiplusReceived() throws Throwable {
        TestStepData step = new TestStepData(_scenario.getName(), "a successful message is display");
        _evidence.startStep(_log, step);
        Utils.isTrue.go(confirmPayment.VerifyMultiplusPointsWasRequested(), _log.me(), "Multiplus points received");
        _evidence.passStep(step);
        _evidence.screenshotAtEnd("Multiplus_requested");
    }

    @And("^when I click on multiplus policies$")
    public void accessMultiplusRules() throws Throwable {
        TestStepData step = new TestStepData(_scenario.getName(), "when I click on multiplus policies");
        _evidence.startStep(_log, step);
        Utils.isTrue.go(confirmPayment.AccessMultiplusRulesPage(), _log.me(), "Multiplus rules link found");
        _evidence.passStep(step);
    }

    @Then("^a new page is shown with multiplus policies$")
    public void isMultiplusRules() throws Throwable {
        TestStepData step = new TestStepData(_scenario.getName(), "a new page is shown with multiplus policies");
        _evidence.startStep(_log, step);
        Utils.isTrue.go(confirmPayment.VerifyIfIsMultiplusRulesPage(), _log.me(), "Multiplus rules accessed");
        _evidence.passStep(step);
        _evidence.finishScenario(_scenario.getName(), "passed");
        _evidence.screenshotAtEnd("Multiplus_rules");
        chooseMenu.chooseOptionSair();
    }

}

