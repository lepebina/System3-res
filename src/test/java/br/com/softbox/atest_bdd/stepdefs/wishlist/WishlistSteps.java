package br.com.softbox.atest_bdd.stepdefs.wishlist;

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


public class WishlistSteps {
    private AtestEvidence _evidence = AtestEvidence.getInstance();
    private Scenario _scenario = null;
    private String _initialSite = "";
    private LoginPage dologin = new LoginPage();
    private MenuUserAccountPage chooseMenu = new MenuUserAccountPage();
    private WishlistPage wishlist = new WishlistPage();
    private ProductPage _accessProduct = new ProductPage();
    private AtestLog _log = new AtestLog("WishlistSteps");

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

    @Given("^I am at \"(.*)\" <wishlist>")
    public void accessNetshoes(String url) throws Throwable {
        _evidence.startScenario(_log, _scenario.getName());
        TestStepData step = new TestStepData(_scenario.getName()
                , "I am at %URL% <wishlist>")
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

    @When("^I log in with \"([^\"]*)\" and \"([^\"]*)\"$")
    public void doLogin(String username, String password) throws Throwable {
        TestStepData step = new TestStepData(_scenario.getName()
                , "I log in with %USER% and %PASSWORD%")
                .add("USER", username)
                .add("PASSWORD", password);
        _evidence.startStep(_log, step);
        chooseMenu.chooseOptionLogin();
        Utils.isTrue.go( dologin.signIn(username, password), _log.me(), "Login done");
        _evidence.passStep(step);
    }

    @And("^I access my empty wishlist$")
    public void cleanWishlist() throws Throwable {
        TestStepData step = new TestStepData(_scenario.getName(), "I access my empty wishlist");
        _evidence.startStep(_log, step);
        chooseMenu.chooseOptionListadeDesejos();
        wishlist.CleanWishList();
        _evidence.passStep(step);
    }

    @And("^I wish a product through its \"([^\"]*)\"$")
    public void addProductToWishlist(String productUrl) throws Throwable {
        TestStepData step = new TestStepData(_scenario.getName()
                , "I wish a product through its %PRODUCT_URL%")
                .add("PRODUCT_URL", productUrl);
        _evidence.startStep(_log, step);
        String prod = Utils.getURL(_initialSite, productUrl);
        _accessProduct.navigateTo(prod);
        _log.step("Add product to wishlist");
        wishlist.AddToWishList();
        _evidence.passStep(step);
    }

    @Then("^my products will be displayed successfully in the wishlist$")
    public void isProductAddedSuccesfully() throws Throwable {
        TestStepData step = new TestStepData(_scenario.getName()
                , "my products will be displayed successfully in the wishlist");
        _evidence.startStep(_log, step);
        Utils.isTrue.go(wishlist.VerifyIfProductAdded(), _log.me(), "Product added successfully");
        _log.step("Add product to wishlist");
        _evidence.passStep(step);
        _evidence.finishScenario(_scenario.getName(), "passed");
        _evidence.screenshotAtEnd("Product_added_to_wishlist_already_logged");
    }

    @And("^I remove it from wishlist$")
    public void removeProductFromWIshlist() throws Throwable {
        TestStepData step = new TestStepData(_scenario.getName()
                , "I remove it from wishlist");
        _evidence.startStep(_log, step);
        Utils.isTrue.go(wishlist.CleanWishList(), _log.me(), "Wishlist was clean");
        _evidence.passStep(step);
    }

    @Then("^my wishlist will be empty$")
    public void isWishlistEmpty() throws Throwable {
        TestStepData step = new TestStepData(_scenario.getName(), "my wishlist will be empty");
        _evidence.startStep(_log, step);
        Utils.isTrue.go(wishlist.WishlistEmpty(), _log.me(), "Wishlist is empty");
        _evidence.screenshotAtEnd("Wishlist empty");
        chooseMenu.chooseOptionSair();
        _evidence.passStep(step);
        _evidence.finishScenario(_scenario.getName(), "passed");
    }

    @When("^I add to the wishlist a product through its \"([^\"]*)\"$")
    public void addAproductToWishlistBeforeLogin(String productUrl) throws Throwable {
        TestStepData step = new TestStepData(_scenario.getName()
                , "I add to the wishlist a product through its ")
                .add("PRODUCT_URL", productUrl);
        _evidence.startStep(_log, step);
        String prod = Utils.getURL(_initialSite, productUrl);
        _accessProduct.navigateTo(prod);
        _log.step("Add product to wishlist not logged");
        Utils.isTrue.go(wishlist.AddToWishListNotLogged(), _log.me(), "Adding to wishlist not logged");
        _evidence.passStep(step);
    }

    @And("^I put my \"([^\"]*)\" and \"([^\"]*)\" in the login page$")
    public void doLoginAfterChooseProduct(String userName, String password) throws Throwable {
        TestStepData step = new TestStepData(_scenario.getName()
                , "I put my %USERNAME% and %PASSWORD% in the login page")
                .add("USERNAME", userName)
                .add("PASSWORD", password);
        _evidence.startStep(_log, step);
        dologin.signIn(userName, password);
        _evidence.passStep(step);
    }

    @Then("^my product will be displayed successfully in the wishlist$")
    public void isProductAddedAfterLogin() throws Throwable {
        TestStepData step = new TestStepData(_scenario.getName()
                , "my product will be displayed successfully in the wishlist");
        _evidence.startStep(_log, step);
        Utils.isTrue.go(wishlist.VerifyIfProductAdded(), _log.me(), "Product added");
        _evidence.passStep(step);
        _evidence.finishScenario(_scenario.getName(), "passed");
        _evidence.screenshotAtEnd("Product_added_wishlist_after_log_in");
    }

}

