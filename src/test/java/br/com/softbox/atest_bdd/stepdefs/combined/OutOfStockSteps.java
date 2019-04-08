package br.com.softbox.atest_bdd.stepdefs.combined;

import br.com.softbox.atest_bdd.pages.ProductPage;
import br.com.softbox.core.*;
import br.com.softbox.utils.Utils;
import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import cucumber.runtime.junit.FeatureRunner;

import java.util.Collection;

import static org.junit.Assert.assertTrue;


/**
 * Verify if selecting an out of stock sku replaces comprar button with avise-me on Produt Page
 */
public class OutOfStockSteps {
    private ProductPage _product = null;
    private AtestEvidence _evidence = AtestEvidence.getInstance();
    private Scenario _scenario = null;
    private String _initialSite = "";
    private AtestLog _log = new AtestLog("OutOfStockSteps");

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

    @Given("^I am at \"(.*)\" <Out Of Stock>")
    public void visitOutOfStockInitialSite(String url) {
        _evidence.startScenario(_log, _scenario.getName());
        TestStepData step = new TestStepData(_scenario.getName()
                , "I am at %URL% <Out Of Stock>")
                .add("URL", url);
        try {
            _evidence.startStep(_log, step);
            _evidence.getWebDriver().get(url);
            _evidence.setInitialSiteURL(url);
            _initialSite = url;
            _evidence.setCurrentHost(url);
            _evidence.passStep(step);
        } catch (AtestWebDriverException e) {
            step.error = "WebDriver not valid (Shutdown??)";
            _evidence.failStep(step);
            Utils.isTrue.go(false, _log.me(), "loading initial page. " + e.getMessage());
        }
    }

    @When("^visit a product with size out of stock at this relative path: \"(.*)\"$")
    public void visitProductOutOfStockProduct(String relativePathProd) {
        TestStepData step = new TestStepData(_scenario.getName()
                , "visit a product with size out of stock at this relative path: %PRODUCT%")
                .add("PRODUCT", relativePathProd);
        _evidence.startStep(_log, step);
        String prodURL = Utils.getURL(_initialSite, relativePathProd);
        try {
            _evidence.getWebDriver().navigate().to(prodURL);
            _evidence.passStep(step);
        } catch (AtestWebDriverException e) {
            step.error = "WebDriver not valid (Shutdown??)";
            _evidence.failStep(step);
            Utils.isTrue.go(false, _log.me(), "loading initial page. " + e.getMessage());
        }
    }

    @And("^select first size out of stock$")
    public void selectFirstSizeOutOfStock() {
        TestStepData step = new TestStepData(_scenario.getName(), "select first size out of stock");
        _evidence.startStep(_log, step);
        _product = new ProductPage();
        Utils.isTrue.go(_product.clickSizeOutOfStock(), _log.me(), "Could not select the first out of stock size");
        _evidence.passStep(step);
    }

    @Then("^Contact me as \"(.*)\" at \"(.*)\" when it is available again$")
    public void ContactMeWhenItIsAvailableAgain(String name, String email) {
        TestStepData step = new TestStepData(_scenario.getName()
                , "Contact me as %NAME% at %EMAIL% when it is available again")
                .add("NAME", name)
                .add("EMAIL", email);

        _evidence.startStep(_log, step);
        Utils.isTrue.go(_product.tellMe(email, name), _log.me(), "inform the name and email");
        _evidence.passStep(step);
        _evidence.screenshotAtEnd("Out_of_stock_after_tell_me");
        _evidence.finishScenario(_scenario.getName(), "passed");
    }


}
