package br.com.softbox.atest_bdd.stepdefs.search;

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

public class SearchSteps {
    private AtestEvidence _evidence = AtestEvidence.getInstance();
    private Scenario _scenario = null;
    private String _initialSite = "";
    private AtestLog _log = new AtestLog("SearchSteps");
    private HomePage dosearch = new HomePage();
    private SearchDonePage searchPage = new SearchDonePage();
    private boolean _continueScenarioPagination = true;
    private String _productSearchedTerm;
    private boolean _isRunningPagination = false;


    @Before
    public void before(Scenario scenario) {
        _scenario = scenario;
    }

    @After
    public void after() {
        if (_evidence.isCurrentScenarioInZombieMode() && !_isRunningPagination) {  // Something goes wrong within this scenario.
            _evidence.finishScenario(_scenario.getName(), "fail");
        } else {
            _evidence.finishScenario(_scenario.getName(), "passed");
        }
    }

    @Given("^I am at \"(.*)\"")
    public void accessNetshoes(String url) throws Throwable {
        _evidence.startScenario(_log, _scenario.getName());
        TestStepData step = new TestStepData(_scenario.getName()
                , "I am at %URL% <search>")
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
    @When("^I search for a valid \"([^\"]*)\"$")
    public void makeValidSearch(String searchFor) throws Throwable {
        TestStepData step = new TestStepData(_scenario.getName()
                , "I search for a valid %SEARCH_FOR%")
                .add("SEARCH_FOR", searchFor);
        _evidence.startStep(_log, step);
        Utils.isTrue.go(dosearch.SearchOk(searchFor),_log.me(), "Search for valid products");
        _evidence.passStep(step);
    }

    @Then("^a successful results will be displayed$")
    public void isValidSearchDoneSuccessfully() throws Throwable {
        TestStepData step = new TestStepData(_scenario.getName()
                , "a successful results will be displayed");
        _evidence.startStep(_log, step);
        Utils.isTrue.go(searchPage.isSearchOk(),_log.me(), "The search for valid products was done successfully");
        _evidence.passStep(step);
        _evidence.finishScenario(_scenario.getName(), "passed");
        _evidence.screenshotAtEnd("Valid_products_shown_after_search");
    }

    @When("^I search for an invalid \"([^\"]*)\"$")
    public void makeInvalidSearch(String searchForInvalid) throws Throwable {
        TestStepData step = new TestStepData(_scenario.getName()
                , "I search for an invalid %SEARCH_FOR_INVALID%")
                .add("SEARCH_FOR_INVALID", searchForInvalid);
        _evidence.startStep(_log, step);
        Utils.isTrue.go(dosearch.SearchNOk(searchForInvalid), _log.me(), "Searching for invalid products");
        _evidence.passStep(step);
    }

    @Then("^a message is shown warning the item does not exist$")
    public void isInvalidSearchDoneSuccessfully() throws Throwable {
        TestStepData step = new TestStepData(_scenario.getName()
                , "a message is shown warning the item does not exist");
        _evidence.startStep(_log, step);
        Utils.isTrue.go(searchPage.isSearchNotOk(), _log.me(), "The search for invalid products was done successfully");
        _evidence.passStep(step);
        _evidence.finishScenario(_scenario.getName(), "passed");
        _evidence.screenshotAtEnd("Ops_message_for_invalid_search");
    }

    @When("^I make a valid \"([^\"]*)\" and scroll down the page$")
    public void searchAndScrollDown(String search) throws Throwable {
        TestStepData step = new TestStepData(_scenario.getName()
                , "I make a valid %SEARCH% and scroll down the page")
                .add("SEARCH", search);
        _productSearchedTerm = search;
        _evidence.startStep(_log, step);
        Utils.isTrue.go(dosearch.SearchOk(search), _log.me(), "Searching for products and scrolling down the result page");
        _evidence.passStep(step);
    }


    @And("^click on Back to Top button$")
    public void clickBacktoTop() throws Throwable {
        TestStepData step = new TestStepData(_scenario.getName(), "click on Back to Top button");
        _evidence.startStep(_log, step);
        Utils.isTrue.go(searchPage.BacktoTop(),_log.me(), "Click on 'back to top'");
        _evidence.passStep(step);
    }

    @Then("^the page scrolls up automatically$")
    public void isBacktoTopDoneSuccessfully() throws Throwable {
        TestStepData step = new TestStepData(_scenario.getName(), "the page scrolls up automatically");
        _evidence.startStep(_log, step);
        Utils.isTrue.go(searchPage.isBackToTopOk(), _log.me(), "Back to the top of the page");
        _evidence.passStep(step);
        _evidence.screenshotAtEnd("back_to_top");
    }

    @When("^I make a valid \"([^\"]*)\"$")
    public void makeValidSearchforFilter(String search) throws Throwable {
        TestStepData step = new TestStepData(_scenario.getName(), "I make a valid %SEARCH%")
                .add("SEARCH", search);
        _evidence.startStep(_log, step);
        Utils.isTrue.go(dosearch.SearchOk(search),_log.me(), "Searching for valid products");
        _evidence.passStep(step);
    }

    @And("^select the first available filter$")
    public void selectFirstFilterFound() throws Throwable {
        TestStepData step = new TestStepData(_scenario.getName(), "select the first available filter");
        _evidence.startStep(_log, step);
        searchPage.SearchFilter(1);
        _evidence.passStep(step);
    }

    @Then("^the result is filtrated and I remove it$")
    public void isFirstFIlterOK() throws Throwable {
        TestStepData step = new TestStepData(_scenario.getName(), "the result is filtrated and I remove it");
        _evidence.startStep(_log, step);
        Utils.isTrue.go(searchPage.IsFilterSelected(),_log.me(), "Filter selected");
        _evidence.passStep(step);
        _evidence.screenshotAtEnd("result_filtered");
        _log.step("Remove filter");
        Utils.isTrue.go(searchPage.RemoveFilter(),_log.me(), "Is filter removed?");
    }

    @And("^select the second available filter$")
    public void selectSecondtFilterFound() throws Throwable {
        TestStepData step = new TestStepData(_scenario.getName(), "select the second available filter");
        _evidence.startStep(_log, step);
        searchPage.SearchFilter(2);
        _evidence.passStep(step);
    }

    @When("^I type part of the \"([^\"]*)\" that I'm looking for$")
    public void makePartOfASearch(String partOfword) throws Throwable {
        TestStepData step = new TestStepData(_scenario.getName(), "I type part of the %PART_OF_WORD% that I'm looking for")
                .add("PART_OF_WORD", partOfword);
        _evidence.startStep(_log, step);
        dosearch.makeSearchForAutoComplete(partOfword);
        _evidence.passStep(step);
    }

    @And("^click on one of the link suggestions$")
    public void clickOnSuggestion() throws Throwable {
        TestStepData step = new TestStepData(_scenario.getName(), "click on one of the link suggestions");
        _evidence.startStep(_log, step);
        dosearch.selectAutoComplete();
        _evidence.passStep(step);
    }

    @Then("^I'll be redirected successfully to the search page$")
    public void isSuggestionWorkingCorrectly() throws Throwable {
        TestStepData step = new TestStepData(_scenario.getName(), "I'll be redirected successfully to the search page");
        _evidence.startStep(_log, step);
        searchPage.searchDoneAutoComplete();
        _evidence.passStep(step);
        _evidence.finishScenario(_scenario.getName(), "passed");
        _evidence.screenshotAtEnd("Search_auto_complete_shown_valid_products");
    }

    @Then("^listing page should be correctly displayed$")
    public void checkList() {
        TestStepData step = new TestStepData(_scenario.getName()
                , "listing page should be correctly displayed");
        _evidence.startStep(_log, step);
        ProductListPage products = new ProductListPage(_productSearchedTerm);
        Utils.isTrue.go(products.hasProducts(), _log.me(), "no products found");
        _evidence.passStep(step);
    }

    @And("^check for page controls \"([^\"]*)\"$")
    public void checkForPageControls(String shouldHavePageControls) {
        _isRunningPagination = true;
        TestStepData step = new TestStepData(_scenario.getName(), "check for page controls %SHOULD_HAVE_PAGE_CONTROLS%")
                .add("SHOULD_HAVE_PAGE_CONTROLS", shouldHavePageControls);
        _evidence.startStep(_log, step);
        _continueScenarioPagination = shouldHavePageControls.toLowerCase().equals("yes") ? true : false;
        PaginationPage pagination = new PaginationPage();
        final boolean hasControls = pagination.hasControls();
        if (_continueScenarioPagination) {
            Utils.isTrue.go(hasControls, _log.me(), "no pagination controls");
            _evidence.passStep(step);
        } else {
            Utils.isFalse.go(hasControls, _log.me(), "has pagination controls (not expected)");
            _evidence.passStep(step);
        }
    }

    @And("^navigate forwards$")
    public void navigateForwards() {
        TestStepData step = new TestStepData(_scenario.getName(), "navigate forwards");
        _evidence.startStep(_log, step);
        if (_continueScenarioPagination) {
            PaginationPage pagination = new PaginationPage();
            Utils.isTrue.go(pagination.forward(), _log.me(), "move page forwards");
            ProductListPage products = new ProductListPage(_productSearchedTerm);
            Utils.isTrue.go(products.hasProducts(), _log.me(), "no products found");
            _evidence.passStep(step);
        } else {
            _evidence.passStep(step);
        }
    }

    @And("^navigate to the last page, if exist$")
    public void navigateToTheLastPageIfExist() {
        TestStepData step = new TestStepData(_scenario.getName(), "navigate to the last page, if exist");
        _evidence.startStep(_log, step);
        if (_continueScenarioPagination) {
            PaginationPage pagination = new PaginationPage();
            Utils.isTrue.go(pagination.last(), _log.me(), "move to the last page");
            ProductListPage products = new ProductListPage(_productSearchedTerm);
            Utils.isTrue.go(products.hasProducts(), _log.me(), "no products found");
            _evidence.passStep(step);
        } else {
            _evidence.passStep(step);
        }
    }

    @And("^navigate backwards$")
    public void navigateBackwards() {
        TestStepData step = new TestStepData(_scenario.getName(), " navigate backwards");
        _evidence.startStep(_log, step);
        if (_continueScenarioPagination) {
            PaginationPage pagination = new PaginationPage();
            Utils.isTrue.go(pagination.backward(), _log.me(), "move backwards page");
            ProductListPage products = new ProductListPage(_productSearchedTerm);
            Utils.isTrue.go(products.hasProducts(), _log.me(), "no products found");
            _evidence.passStep(step);
        } else {
            _evidence.passStep(step);
        }
    }

    @And("^navigate to the first page, if exist$")
    public void navigateToTheFirstPageIfExist() {
        TestStepData step = new TestStepData(_scenario.getName(), "navigate to the first page, if exist");
        _evidence.startStep(_log, step);
        if (_continueScenarioPagination) {
            PaginationPage pagination = new PaginationPage();
            Utils.isTrue.go(pagination.first(), _log.me(), "move to the first page");
            ProductListPage products = new ProductListPage(_productSearchedTerm);
            Utils.isTrue.go(products.hasProducts(), _log.me(), "no products found");
            _evidence.passStep(step);
        } else {
            _evidence.passStep(step);
        }
    }
}

