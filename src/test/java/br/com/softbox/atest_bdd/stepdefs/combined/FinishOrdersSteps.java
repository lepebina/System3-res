package br.com.softbox.atest_bdd.stepdefs.combined;

import br.com.softbox.atest_bdd.pages.*;
import br.com.softbox.core.*;
import br.com.softbox.utils.Utils;
import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;
import cucumber.api.java.en.Then;
import org.junit.Rule;

import java.util.Collection;


public class FinishOrdersSteps {
    private AtestEvidence _evidence = AtestEvidence.getInstance();
    private ProductPage _product = new ProductPage();
    private Scenario _scenario = null;
    private String _initialSite = "";
    public AtestLog _log = new AtestLog("FinishOrdersSteps");

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

    @Given("^I am at \"(.*)\" <Finish Orders>$")
    public void visitFinishOrdersInitialSite(String url) {
        _evidence.startScenario(_log, _scenario.getName());
        TestStepData step = new TestStepData(_scenario.getName()
                , "I am at %URL% <Finish Orders>")
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

    @When("^I am logged as \"(.*)\" with \"(.*)\" and \"(.*)\"$")
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

    @And("^I customized a \"(.*)\" of type \"(.*)\"$")
    public void customizeProduct(String productURL, String productType) {
        TestStepData step = new TestStepData(_scenario.getName()
                , "I customized a %PRODUCT_URL% of type %PRODUCT_TYPE%")
                .add("PRODUCT_URL", productURL)
                .add("PRODUCT_TYPE", productType);
        _evidence.startStep(_log, step);
        if (productURL.isEmpty() || productType.isEmpty()) {
            Utils.isTrue.go(false, _log.me(), "empty product");
        }
        String prod = Utils.getURL(_initialSite, productURL);

        Utils.isFalse.go(prod.isEmpty(), _log.me(), "could not build the URL for the product");
        Utils.isTrue.go(_product.setProduct(prod, productType), _log.me(), "set product and product type");
        Utils.isTrue.go(_product.customize(), _log.me(), "product customize");
        Utils.isTrue.go(_product.buy(), _log.me(), "product buy");
        _evidence.passStep(step);
    }

    @And("^an additional value related to its personalization is shown$")
    public void checkAdditionalValueForPersonalizedItem() {
        TestStepData step = new TestStepData(_scenario.getName()
                , "an additional value related to its personalization is shown");
        _evidence.startStep(_log, step);
        CartPage cart = new CartPage();
        Utils.isTrue.go(cart.checkAdditionalValueForPersonalization(), _log.me(), "check additional value for personalization");
        Utils.isTrue.go(cart.finalizeOrder(), _log.me(), "finalize order");
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

    @And("^buy the \"(.*)\"$")
    public void buyTheProduct(String productURL) throws Throwable {
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

    @And("^on payment page select \"(.*)\"$")
    public void onPaymentPageSelectDeliveryType(String type) {
        TestStepData step = new TestStepData(_scenario.getName()
                , "on payment page select %TYPE%")
                .add("TYPE", type);
        _evidence.startStep(_log, step);
        PaymentPage pay = new PaymentPage();
        Utils.isTrue.go(pay.selectShipping(type), _log.me(), "selecting shipping type");
        _evidence.passStep(step);
    }

    @And("^pay in \"(.*)\" with card: \"(.*)\", \"(.*)\", \"(.*)\", \"(.*)\" and \"(.*)\"$")
    public void payWithCreditCardInstallments(
            String numInstallments
            , String cardHolder
            , String cardUserName
            , String cardNum
            , String cardValidity
            , String cardCVV) {
        TestStepData step = new TestStepData(_scenario.getName()
                , "pay in %NUM_INSTALL% with card: %CARD_HOLDER%, %CARD_USERNAME%, %CARD_NUM%, %CARD_VALIDITY% and %CARD_CVV%")
                .add("NUM_INSTALL", numInstallments)
                .add("CARD_HOLDER", cardHolder)
                .add("CARD_USERNAME", cardUserName)
                .add("CARD_NUM", cardNum)
                .add("CARD_VALIDITY", cardValidity)
                .add("CARD_CVV", cardCVV);
        _evidence.startStep(_log, step);
        PaymentPage pay = new PaymentPage();
        Utils.isTrue.go(pay.payWithCreditCardAndInstallments(numInstallments, cardHolder, cardUserName
                , cardNum, cardValidity, cardCVV), _log.me(), "pay with credit card and installments");
        _evidence.passStep(step);
    }

    @And("^on payment page choose pay with two cards$")
    public void payWithTwoCreditCards() {
        TestStepData step = new TestStepData(_scenario.getName()
                , "on payment page choose pay with two cards");
        _evidence.startStep(_log, step);
        PaymentPage pay = new PaymentPage();
        Utils.isTrue.go(pay.selectPayUsingTwoCreditCards(), _log.me(), "pay using two credit cards");
        _evidence.passStep(step);
    }

    @And("^pay R\\$ \"([^\"]*)\" in \"(.*)\" with card ONE: \"(.*)\", \"(.*)\", \"(.*)\", \"(.*)\" and \"(.*)\"$")
    public void fill1stCreditCard(
            String amount
            , String numInstallments
            , String cardHolder
            , String cardUserName
            , String cardNum
            , String cardValidity
            , String cardCVV) {
        TestStepData step = new TestStepData(_scenario.getName()
                , "pay R$ %AMOUNT% in %NUM_INSTALL% with card ONE: %CARD_HOLDER%, %CARD_USERNAME%, %CARD_NUM%, %CARD_VALIDITY% and %CARD_CVV%")
                .add("AMOUNT", amount)
                .add("NUM_INSTALL", numInstallments)
                .add("CARD_HOLDER", cardHolder)
                .add("CARD_USERNAME", cardUserName)
                .add("CARD_NUM", cardNum)
                .add("CARD_VALIDITY", cardValidity)
                .add("CARD_CVV", cardCVV);
        _evidence.startStep(_log, step);
        PaymentPage pay = new PaymentPage();
        Utils.isTrue.go(pay.fill1stCard(amount, numInstallments, cardUserName, cardNum, cardValidity, cardCVV), _log.me(), "fill 1st card");
        _evidence.passStep(step);
    }

    @And("^pay in \"(.*)\" with card TWO: \"(.*)\", \"(.*)\", \"(.*)\", \"(.*)\" and \"(.*)\"$")
    public void fill2ndCreditCard(String numInstallments
            , String cardHolder
            , String cardUserName
            , String cardNum
            , String cardValidity
            , String cardCVV) {
        TestStepData step = new TestStepData(_scenario.getName()
                , "pay in %NUM_INSTALL% card TWO: %CARD_HOLDER%, %CARD_USERNAME%, %CARD_NUM%, %CARD_VALIDITY% and %CARD_CVV%")
                .add("NUM_INSTALL", numInstallments)
                .add("CARD_HOLDER", cardHolder)
                .add("CARD_USERNAME", cardUserName)
                .add("CARD_NUM", cardNum)
                .add("CARD_VALIDITY", cardValidity)
                .add("CARD_CVV", cardCVV);
        _evidence.startStep(_log, step);
        PaymentPage pay = new PaymentPage();
        Utils.isTrue.go(pay.fill2ndCard(numInstallments, cardUserName, cardNum, cardValidity, cardCVV), _log.me(), "fill 2nd card");
        Utils.isTrue.go(pay.submitPayment(true/*is a payment for two cards*/), _log.me(), "submit payment");
        _evidence.passStep(step);
    }

    @And("^pay with 'boleto'")
    public void payWithBoleto() {
        TestStepData step = new TestStepData(_scenario.getName(), "pay with 'boleto'");
        _evidence.startStep(_log, step);
        PaymentPage pay = new PaymentPage();
        Utils.isTrue.go(pay.payWithBoleto(), _log.me(), "pay with boleto");
        _evidence.passStep(step);
    }

    @And("^buy the bundle for the product: \"([^\"]*)\"$")
    public void buyTheProductBundle(String productURL) {
        TestStepData step = new TestStepData(_scenario.getName()
                , "buy the bundle for the product: %PRODUCT_URL%")
                .add("PRODUCT_URL", productURL);
        _evidence.startStep(_log, step);
        ProductPage p = new ProductPage();
        String prod = Utils.getURL(_initialSite, productURL);

        Utils.isFalse.go(prod.isEmpty(), _log.me(), "could not build the URL for the product");
        Utils.isTrue.go(p.navigateTo(prod), _log.me(), "could not open product URL");
        Utils.isTrue.go(p.buyBundle(), _log.me(), "could not buy bundle");

        BundlePage bundle = new BundlePage();
        //Utils.isTrue.go(bundle.chooseFirstSizeForBundleItems(), _log.me(), "could not select size for bundle items");
        Utils.isTrue.go(bundle.buy(), _log.me(), "buy bundle");

        CartPage cart = new CartPage();
        Utils.isTrue.go(cart.finalizeOrder(), _log.me(), "finalize order for the bundle products");
        _evidence.passStep(step);
    }

    @Then("^my order will be finish with a successful message$")
    public void checkForSuccessfullOrder() {
        TestStepData step = new TestStepData(_scenario.getName()
                , "my order will be finish with a successful message");
        _evidence.startStep(_log, step);
        PaymentConfirmationPage confirmation = new PaymentConfirmationPage();
        String confirmationNumber = "";
        if (_scenario.getName().contains("boleto")) {
            Utils.isTrue.go(confirmation.isConfirmationPageForBoleto(), _log.me(), "is not the confirmation page for boleto");

            confirmationNumber = confirmation.getOrderNumber();
            Utils.isFalse.go(confirmationNumber.isEmpty(), _log.me(), "could not find order number on confirmation page");
            step.result("Order number", confirmationNumber.substring(confirmationNumber.indexOf(":")+1));

            _evidence.passStep(step);
            _evidence.finishScenario(_scenario.getName(), "passed");
            _log.trace("confirmation page for boleto");
            _evidence.screenshotAtEnd("confirmation_page_boleto");
        } else {
            confirmationNumber = confirmation.getOrderNumber();
            Utils.isFalse.go(confirmationNumber.isEmpty(), _log.me(), "could not find order number on confirmation page");
            step.result("Order number", confirmationNumber.substring(confirmationNumber.indexOf(":")+1));
            _evidence.passStep(step);
            _evidence.finishScenario(_scenario.getName(), "passed");
            _log.info("PaymentPage confirmed.");
            _log.info(confirmationNumber);
            _evidence.screenshotAtEnd("confirmation_page");
        }
    }
    
    @When("^I put in the cart the product with rateio promotion \"([^\"]*)\"$")
    public void cartProductWithRateioPromotion(String product) throws Throwable {
    	TestStepData step = new TestStepData(_scenario.getName()
                , "I put in the cart the product with rateio promotion %PRODUCT%").add("PRODUCT", product);
    	_evidence.startStep(_log, step);
    	String prodURL = Utils.getURL(_initialSite, product);
    	try{
    		_evidence.getWebDriver().navigate().to(prodURL);
            ProductPage p = new ProductPage();
            Utils.isTrue.go(p.buy(), _log.me(), "buy product");
            _evidence.passStep(step);
            _evidence.screenshot("ProductPage");
            
        } catch (AtestWebDriverException e) {
            step.error = "WebDriver not valid (Shutdown??)";
            _evidence.failStep(step);
            _evidence.screenshotOnError("ProductPageError");
            Utils.isTrue.go(false, _log.me(), "loading product page. " + e.getMessage());
        }
    }

    @Then("^the promotion message is displayed in the header$")
    public void promotionMessageDisplayedInTheHeader() throws Throwable {
    	TestStepData step = new TestStepData(_scenario.getName()
                , "the promotion message is displayed in the header");
    	_evidence.startStep(_log, step);
    	
        CartPage cart = new CartPage();
        Utils.isTrue.go(cart.getBannerMsg("inicial"), _log.me(), "Banner not found in cart page.");
        _evidence.passStep(step);
        _evidence.screenshot("CartPage");
    	
    }

    @Then("^when I change up to 3 units$")
    public void whenIChangeUpToUnits() throws Throwable {
    	TestStepData step = new TestStepData(_scenario.getName()
                , "When I change up to 3 units");
    	_evidence.startStep(_log, step);
    	
    	CartPage cart = new CartPage();
    	Double  min = 119.75;
    	Double max = 119.80;
    	cart.buyTwoGetThreePromotion(min,max);
    	_evidence.passStep(step);
    	_evidence.screenshot("CartPageFreight");
    }

    @Then("^the promotion images are applied and displayed successfully$")
    public void promotionImagesAreAppliedAndDisplayedSuccessfully() throws Throwable {
    	TestStepData step = new TestStepData(_scenario.getName()
                , "the promotion images are applied and displayed successfully");
    	_evidence.startStep(_log, step); 
    	
    	CartPage cart = new CartPage();
    	Utils.isTrue.go(cart.getBannerMsg("final"), _log.me(), "Banner not found in cart page.");
    	Utils.isTrue.go(cart. getMsgPromotion(), _log.me(), "Flag promotion not found.");
    	 
     	_evidence.screenshotAtEnd("CartPage");
     	_evidence.passStep(step);
     	_evidence.finishScenario(_scenario.getName(), "passed");
    }
    
    @When("^I choose my discount method \"([^\"]*)\", \"([^\"]*)\"$")
    public void chooseMyDiscountMethod(String discount, String discountCode) throws Throwable {
    	TestStepData step = new TestStepData(_scenario.getName()
                , "I choose my discount method %DISCOUNT%, %DISCOUNT_CODE%")
    			.add("DISCOUNT", discount)
    			.add("DISCOUNT_CODE", discountCode);
    	_evidence.startStep(_log, step); 
    	PaymentPage pay = new PaymentPage();
    	Utils.isTrue.go(pay.discountBlock(), _log.me(), "Discount not found.");
    	_evidence.passStep(step);
    }

    @Then("^my order will be finished with a successful message$")
    public void orderFinishedSuccessful() throws Throwable {
    	TestStepData step = new TestStepData(_scenario.getName()
                , "My order will be finished with a successful message%");
    	
    	PaymentPage pay = new PaymentPage();
    	Utils.isTrue.go(pay.validateFinalStep(), _log.me(), "Error in order process.");
    	_evidence.finishScenario(_scenario.getName(), "passed");
    	_evidence.passStep(step);
    }
}
