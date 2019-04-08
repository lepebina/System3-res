package br.com.softbox.atest_bdd.stepdefs.combined;


import br.com.softbox.atest_bdd.pages.ProductPage;
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

public class PdpSteps {
    private AtestEvidence _evidence = AtestEvidence.getInstance();
    private Scenario _scenario = null;
    private String _initialSite = "";
    private AtestLog _log = new AtestLog("PdpSteps");

    private ProductPage _accessProduct = new ProductPage();

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

    @Given("^I am at \"(.*)\" <pdp>")
    public void accessNetShoes(String url) throws Throwable {
        _evidence.startScenario(_log, _scenario.getName());
        TestStepData step = new TestStepData(_scenario.getName()
                , "I am at %URL% <pdp>")
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

    @When("^I access a shoes through its url \"([^\"]*)\"$")
    public void accessProduct(String productUrl) throws Throwable {
        TestStepData step = new TestStepData(_scenario.getName()
                , "I access a shoes through its url %PRODUCT_URL%");
        _evidence.startStep(_log, step);
        String prod = Utils.getURL(_initialSite, productUrl);
        Utils.isTrue.go(_accessProduct.navigateTo(prod), _log.me(), "Navigate to product");
        _evidence.passStep(step);
    }

    @When("^navigate through the size table options$")
    public void navigateThroughSizeTable() throws Throwable {
        TestStepData step = new TestStepData(_scenario.getName()
                , "navigate through the size table option");
        _evidence.startStep(_log, step);
        Utils.isTrue.go(_accessProduct.tableSize(),_log.me(), "Access table size");
        _evidence.passStep(step);
    }

    @Then("^all selected brands should be displayed correctly$")
    public void displayBrandsCorrectly() throws Throwable {
        TestStepData step = new TestStepData(_scenario.getName()
                , "all selected brands should be displayed correctly");
        _evidence.startStep(_log, step);
        Utils.isTrue.go(_accessProduct.isTableSizeOk(), _log.me(), "Validate table size");
        _evidence.passStep(step);
        _evidence.finishScenario(_scenario.getName(), "passed");
        _evidence.screenshotAtEnd("size_table_shown");
    }


    @When("^I access a product with power reviews comments through its url \"([^\"]*)\"$")
    public void accessPowerReviewersProduct(String productUrl) throws Throwable {
        TestStepData step = new TestStepData(_scenario.getName()
                , "I access a product with power reviews comments through its url %PRODUCT_URL%")
                .add("PRODUCT_URL", productUrl);
        _evidence.startStep(_log, step);
        String prod = Utils.getURL(_initialSite, productUrl);
        Utils.isTrue.go(_accessProduct.navigateTo(prod), _log.me(), "Navigate to product");
        _evidence.passStep(step);
    }

    @And("^go to the power reviews section$")
    public void goToPowerReviewersSection() throws Throwable {
        TestStepData step = new TestStepData(_scenario.getName()
                , "go to the power reviews section");
        _evidence.startStep(_log, step);
        Utils.isTrue.go(_accessProduct.goToPowerReviewers(), _log.me(), "Go to Power Reviewer section");
        _evidence.passStep(step);
    }

    @Then("^power reviews information should be displayed correctly$")
    public void verifyPowerReviewers() throws Throwable {
        TestStepData step = new TestStepData(_scenario.getName()
                , "power reviews information should be displayed correctly");
        _evidence.startStep(_log, step);
        Utils.isTrue.go(_accessProduct.isPowerReviewersOK(), _log.me(), "Power reviews checked");
        _evidence.screenshotAtEnd("Power_reviewer_shown");
        _evidence.passStep(step);
        _evidence.finishScenario(_scenario.getName(), "passed");
    }

    @When("^I access the product that contains video at this relative path: \"([^\"]*)\"$")
    public void iAccessTheProductThatContainsVideoAtThisRelativePath(String productURL) {
        TestStepData step = new TestStepData(_scenario.getName()
                , "I access the product that contains video at this relative path: %PRODUCT_URL%")
                .add("PRODUCT_URL", productURL);
        _evidence.startStep(_log, step);
        String prod = Utils.getURL(_initialSite, productURL);
        Utils.isTrue.go(_accessProduct.navigateTo(prod), _log.me(), "Navigate to product");
        _evidence.passStep(step);
    }

    @And("^click in the video icon$")
    public void clickInTheVideoIcon() {
        TestStepData step = new TestStepData(_scenario.getName()
                , "click in the video icon");
        _evidence.startStep(_log, step);
        Utils.isTrue.go(_accessProduct.showVideo(), _log.me(), "this product seems not have a video");
        _evidence.passStep(step);
    }

    @Then("^video frame will be displayed$")
    public void videoFrameWillBeDisplayed() {
        TestStepData step = new TestStepData(_scenario.getName()
                , "video frame will be displayed");
        _evidence.startStep(_log, step);
        Utils.isTrue.go(_accessProduct.isVideoPresent(), _log.me(), "video does not seems to be from YOUTUBE");
        _evidence.passStep(step);
        _evidence.finishScenario(_scenario.getName(), "passed");
    }
}

