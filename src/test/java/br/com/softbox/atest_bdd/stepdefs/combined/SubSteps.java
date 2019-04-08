package br.com.softbox.atest_bdd.stepdefs.combined;

import br.com.softbox.atest_bdd.pages.ProductPage;
import br.com.softbox.atest_bdd.pages.SubPage;
import br.com.softbox.core.*;
import br.com.softbox.utils.Utils;
import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;


public class SubSteps {
    private AtestEvidence _evidence = AtestEvidence.getInstance();
    private Scenario _scenario = null;
    private String _initialSite = "";
    private ProductPage accessProduct = new ProductPage();
    private SubPage checkSub = new SubPage();
    private AtestLog _log = new AtestLog("SubSteps");


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

    @Given("^I am at \"(.*)\" <sub_page>")
    public void accessNetshoes(String url) throws Throwable {
        _evidence.startScenario(_log, _scenario.getName());
        TestStepData step = new TestStepData(_scenario.getName()
                , "I am at %URL% <sub_page>")
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
    @When("^I access \"([^\"]*)\"$")
    public void accessLink(String url) throws Throwable {
        TestStepData step = new TestStepData(_scenario.getName()
                , "I access %URL%")
                .add("URL", url);
        _evidence.startStep(_log, step);
        String prod = Utils.getURL(_initialSite, url);
        accessProduct.navigateTo(prod);
        _evidence.passStep(step);
    }

    @Then("^the page is loaded correctly$")
    public void isPageLoaded() throws Throwable {
        TestStepData step = new TestStepData(_scenario.getName(), "the page is loaded correctly");
        _evidence.startStep(_log, step);
        Utils.isTrue.go(checkSub.IsSubPageOK(), _log.me(), "Page banner loaded successfully");
        _evidence.passStep(step);
        _evidence.finishScenario(_scenario.getName(), "passed");
        _evidence.screenshotAtEnd("Banner_loaded_for_subpage");
    }
    
    @Then("^the page displays an 'ops' message$")
    public void the_page_displays_an_ops_message() throws Throwable {
    	 TestStepData step = new TestStepData(_scenario.getName(), "the page displays an 'ops' message.");
         _evidence.startStep(_log, step);
         
         Utils.isTrue.go(checkSub.getErrorMessage(), _log.me(), "Some problem occurred in error page");
         _evidence.passStep(step);
         _evidence.finishScenario(_scenario.getName(), "passed");
         _evidence.screenshotAtEnd("Banner_loaded_for_subpage");
    }
}

