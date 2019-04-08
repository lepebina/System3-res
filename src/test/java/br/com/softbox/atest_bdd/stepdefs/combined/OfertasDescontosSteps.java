package br.com.softbox.atest_bdd.stepdefs.combined;

import br.com.softbox.atest_bdd.pages.*;
import br.com.softbox.core.*;
import br.com.softbox.utils.Utils;
import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class OfertasDescontosSteps {
    private AtestEvidence _evidence = AtestEvidence.getInstance();
    private Scenario _scenario = null;
    private String _initialSite = "";
    private HomePage subcribe = new HomePage();
    private AtestLog _log = new AtestLog("OfertasDescontosSteps");

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

    @Given("^I am at \"(.*)\" <registerOfertasDescontos>")
    public void accessNetshoes(String url) throws Throwable {
        _evidence.startScenario(_log, _scenario.getName());
        TestStepData step = new TestStepData(_scenario.getName()
                , "I am at %URL% <registerOfertasDescontos>")
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
    @When("^I subscribe with a new random \"([^\"]*)\"$")
    public void doSubscription(String email) throws Throwable {
        TestStepData step = new TestStepData(_scenario.getName()
                , "I subscribe with a new random %EMAIL%")
                .add("EMAIL", email);
        _evidence.startStep(_log, step);
        String randomEmail = subcribe.getEmail();
        Utils.isTrue.go(subcribe.registerOfertasDescontos(randomEmail+email), _log.me(), "Registration done");
        _evidence.passStep(step);
    }

    @Then("^a successful message will be displayed$")
    public void isSubscriptionDoneSuccessfully() throws Throwable {
        TestStepData step = new TestStepData(_scenario.getName()
                , "a successful message will be displayed");
        _evidence.startStep(_log, step);
        Utils.isTrue.go(subcribe.AssertRegisteredOfertasDescontos(), _log.me(), "User was registered successfully");
        _evidence.passStep(step);
        _evidence.finishScenario(_scenario.getName(), "passed");
        _evidence.screenshotAtEnd("subscription_done");
    }


}

