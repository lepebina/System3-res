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

public class ShoppingCartSteps {
    private AtestEvidence _evidence = AtestEvidence.getInstance();
    private Scenario _scenario = null;
    private String _initialSite = "";
    private ProductPage accessProduct = new ProductPage();
    private AtestLog _log = new AtestLog("ShoppingCartSteps");
    private MenuUserAccountPage menu = new MenuUserAccountPage();
    private LoginPage doLogin = new LoginPage();
    private CartPage checkCart = new CartPage();
    private HomePage accessCart = new HomePage();
    private Double price = 0.00;
    private String cep = "";
    
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

    @Given("^I am at \"(.*)\" <shopping_cart>")
    public void accessNetshoes(String url) throws Throwable {
        _evidence.startScenario(_log, _scenario.getName());
        TestStepData step = new TestStepData(_scenario.getName()
                , "I am at %URL% <shopping_cart>")
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

    @When("^I login with \"([^\"]*)\" and \"([^\"]*)\"$")
    public void doLoginAndCleanCart(String email, String password) throws Throwable {
    	try{
    		

        TestStepData step = new TestStepData(_scenario.getName()
                , "I login with %EMAIL% and %PASSWORD%")
                .add("EMAIL", email)
                .add("PASSWORD", password);
        _evidence.startStep(_log, step);
        Utils.isTrue.go(menu.chooseOptionLogin(), _log.me(), "Menu login");
        
        Utils.isTrue.go(doLogin.signIn(email, password), _log.me(), "sign in");
        _log.step("Clean shopping cart");
        Utils.isTrue.go(accessCart.goShoppingCart(), _log.me(), "Access shopping cart");
        _evidence.passStep(step);
        checkCart.cleanUp();
    	} catch(Exception e) {
    		_log.info("Problem found" + e.getMessage());
    	}
    }

    @And("^navigate to the following product \"([^\"]*)\"$")
    public void navigateToAProduct(String productUrl) throws Throwable {
        TestStepData step = new TestStepData(_scenario.getName()
                , "navigate to the following product %PRODUCT_URL%")
                .add("PRODUCT_URL", productUrl);
         String prod = Utils.getURL(_initialSite, productUrl);
        _evidence.startStep(_log, step);
        _log.step("Navigate to Product");
        Utils.isTrue.go(accessProduct.navigateTo(prod), _log.me(), "Navigate to product");
        _evidence.passStep(step);
    }

    @Then("^I add the product successfully to the shopping cart$")
    public void addAndValidateProductAddedToCart() throws Throwable {
        TestStepData step = new TestStepData(_scenario.getName()
                , "I add the product successfully to the shopping cart");
        _evidence.startStep(_log, step);
        Utils.isTrue.go(checkCart.isRightProductAdded(), _log.me(), "Right product added");
        _evidence.screenshotAtEnd("Product_added_to_the_cart");
        _evidence.passStep(step);
        _evidence.finishScenario(_scenario.getName(), "passed");
    }

    
    @Given("^free shipping price is \"([^\"]*)\"$")
    public void freeShippingPriceIs(String price) throws Throwable {
    	TestStepData step = new TestStepData(_scenario.getName()
                , "free shipping price is %PRICE%")
                .add("PRICE", price);
    	_evidence.startStep(_log, step);
    	_evidence.screenshot("HomePage");
    	
    	CartPage cart = new CartPage();
    	this.price = cart.validateStringToDouble(price);
    	
    	_evidence.passStep(step);
    }
    
    @When("^I calculate the shipping value for product \"([^\"]*)\" and CEP \"([^\"]*)\"$")
    public void iCalculateTheShippingValueforProductAndCEP(String product, String cep) throws Throwable {

    	TestStepData step = new TestStepData(_scenario.getName()
                , "I calculate the shipping value for product %PRODUCT% and CEP %CEP%")
                .add("PRODUCT", product)
    			.add("CEP", cep);
    	_evidence.startStep(_log, step);
    	
    	if (cep.length() != 8) {
    		Utils.isTrue.go(false, _log.me(), "Invalid CEP, these CEP must contain 8 characters ");
    	}
    	String prodURL = Utils.getURL(_initialSite, product);
    	 try {
             
             CartPage cart = new CartPage();
             cart.cleanUp();
             
             _evidence.getWebDriver().navigate().to(prodURL);
             
             ProductPage p = new ProductPage();
             Utils.isTrue.go(p.buy(), _log.me(), "buy product");
             
             boolean retCode =  cart.setCepFieldAndClickButton(cep);
             _evidence.screenshot("CartPageFreight");
             Utils.isTrue.go(retCode, _log.me(), "Doesn't set fields");
             _evidence.passStep(step);

         } catch (AtestWebDriverException e) {
             step.error = "WebDriver not valid (Shutdown??)";
             _evidence.failStep(step);
             _evidence.screenshotOnError("CartPageFreightError");
             Utils.isTrue.go(false, _log.me(), "loading product page. " + e.getMessage());
         }
    }
    
    @Then("^verify if the shipping value is \"([^\"]*)\"$")
    public void verifyIfTheShippingValueIs(String shippingType) throws Throwable {

    	TestStepData step = new TestStepData(_scenario.getName()
                , "verify if the shipping value is %SHIPPINGTYPE%")
                .add("SHIPPINGTYPE", shippingType);
    	_evidence.startStep(_log, step);
    	
    	CartPage cart = new CartPage();
    	AtestWebElement awe = AtestWebElement.getInstance();
    	Double priceProduct = cart.getPriceProduct(awe,"new");
    	if (shippingType.equals("FREE")) {
    		
    		Utils.isTrue.go(priceProduct >= this.price, _log.me(), "To get the free shipping, the product price must be greater than: "+this.price);
    		String freightMsg = cart.getMsgFreigth(awe);
    		Utils.isTrue.go(freightMsg.equalsIgnoreCase("FRETE GRÁTIS"), _log.me(), "Doesn't exist frete message");
    		_log.info("Is Free freight");
    		
    	} else if (shippingType.equals("NOT FREE")) {
    		Double  freightPrice = cart.getFreightPrice(awe);
    		if (freightPrice != null) {
   			 	_log.info("The price of freight is "+ freightPrice);
   			 	Utils.isTrue.go(freightPrice > 0, _log.me(), "Doesn't exist frete message");
   			 	
    		}
    	} else {
    		Double  freightPrice = cart.getFreightPrice(awe);
    		Double shippingTypeValid = cart.validateStringToDouble(shippingType);
    				
    		Utils.isTrue.go(freightPrice.equals(shippingTypeValid), _log.me(), "These freights are different, freight feature: "+freightPrice+
    				"freight site:" + shippingTypeValid);
    		_log.info("These freights are equals!");
    	}
    	_evidence.passStep(step);
    }

    
    @Then("^Delivery date is displayed: \"([^\"]*)\"$")
    public void delivery_date_is_displayed(String deliveryDate) throws Throwable {
    	TestStepData step = new TestStepData(_scenario.getName()
                , "free shipping price is %DELIVERYDATE%")
                .add("DELIVERYDATE", deliveryDate);
    	_evidence.startStep(_log, step);
    	
    	AtestWebElement element = AtestWebElement.getInstance();
    	CartPage cart = new CartPage();
    	String dateMsg = cart.getDateMessage(element);
    	_log.info("The delivery date is "+deliveryDate+ " dias úteis");
    	Utils.isTrue.go(dateMsg.equals(deliveryDate), _log.me(), "These delivery date are different");
    	_log.info("The delivery date are equals");
    	_evidence.screenshotAtEnd("DeliveryDateIsDisplayed");
    	_evidence.passStep(step);
    	_evidence.finishScenario(_scenario.getName(), "passed");
    }
    
}

