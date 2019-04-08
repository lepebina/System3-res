
package br.com.softbox.atest_bdd.pages;

import br.com.softbox.core.*;
import br.com.softbox.utils.Utils;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;

import java.util.ArrayList;
import java.util.List;

/**
 * Page object for ProductPage page.
 */
public class ProductPage {
    private List<ProductConfigEntry> _configProducts = null;
    private List<WebElement> _sizesOutOfStock;
    private AtestEvidence _evidence = AtestEvidence.getInstance();
    private ProductConfigEntry _currentProductConfig = null;

    private enum ProductType { NONE, BALL, T_SHIRT, TEAM_T_SHIRT, CAP, JACKET};
    private ProductType _currProductType = ProductType.NONE;
    private AtestLog _log = new AtestLog("ProductPage");

   
    public ProductPage() {
    }

    public boolean showVideo() {
        boolean retCode = false;
        AtestWebElement awe = AtestWebElement.getInstance();
        try {
            Utils.wait(1000); // due refresh
            WebElement videoButton = awe.findElement("ProductPage.showVideo.video_button");
            if(videoButton.getText().equalsIgnoreCase("t")){
                videoButton.click();
                Utils.wait(1000);
                try {
                    awe.findElement("ProductPage.showVideo.missing_flash_plugin");
                } catch (AtestNoSuchWebElementException e) {
                    retCode = true;
                }
            } else {
                _log.oops("find a button with selector, but it is not video button.");
                _evidence.screenshotOnError("ProductPage_showVideo");
            }
        } catch (AtestNoSuchWebElementException | AtestWebDriverException e) {
            _log.oops("could not find out video button. " + e.getMessage());
            _evidence.screenshotOnError("ProductPage_showVideo");
        }
        return retCode;
    }

    public boolean isVideoPresent() {
        boolean retCode = false;
        AtestWebElement awe = AtestWebElement.getInstance();
        try {
            Utils.wait(1000);
            String youtubeURL = awe.findAttributeByElementIdWithJS("ProductPage.isVideoPresent.video", "data");
            retCode = youtubeURL.contains("youtube");
        } catch (AtestNoSuchWebElementException | AtestWebDriverException e) {
            _log.oops("video is not present" + e.getMessage());
            _evidence.screenshotOnError("ProductPage_isVideoPresent");
        }
        return retCode;
    }

    /**
     * Verify whether the product has a size that is out of stock.
     * @return true there is size out of stock, otherwise false.
     */
    private List<WebElement> discoverSizesOutOfStock() throws AtestNoSuchWebElementException, AtestWebDriverException {
        try {
            Utils.wait(3000); // due refresh
            return AtestWebElement.getInstance()
                    .findElements("ProductPage.discoverSizesOutOfStock.list_of_unavailables");
        } catch (AtestNoSuchWebElementException e) {
            throw new AtestNoSuchWebElementException("could not find out of stock sizes. " + e.getMessage());
        }
    }
    
    private List<WebElement> findSize() {
    	
    	List<WebElement> elements= new ArrayList<>();
    	try {
    		  Utils.wait(2000); // due refresh
    		  elements  = AtestWebElement.getInstance()
                      .findElements("ProductPage.findSize.size");
    		  
    	} catch (AtestNoSuchWebElementException e) {
    		_log.trace("Element 'Lista de tamanhos' not found. " + e.getMessage());
    	} catch (AtestWebDriverException d) {
    		_log.oops("Problem with webDriver " + d.getMessage());
    	}
    	return  elements;
    }
    /**
     * Clicks on the first size that is out of stock.
     * @return True if clicked and the button turns from "Comprar" to "Avise-me", otherwise false.
     */
    public boolean clickSizeOutOfStock() {
        try {
            boolean tryAgain = false;
            do {
                List<WebElement> sizes = discoverSizesOutOfStock();
                if (!sizes.isEmpty()) {
                    try {
                        if (TestController.WebBrowser.IE == _evidence.getBrowserInUse()) {
                            ((JavascriptExecutor) _evidence.getWebDriver()).executeScript("scroll(0,200)");
                        }
                        sizes.get(0).click();
                    } catch (StaleElementReferenceException e) {
                        if (!tryAgain) {
                            tryAgain = true;
                            continue;
                        } else {
                            tryAgain = false;
                            _log.oops("Page refresh invalidades selectors. " + e.getMessage());
                            continue;
                        }
                    }
                    Utils.wait(2000);
                    AtestWebElement.getInstance().findElement("ProductPage.clickSizeOutOfStock.button_text_avise_me").isEnabled();
                    _evidence.screenshot("button_aviseme");
                    return true;
                } else {
                    _log.oops("no size of stock");
                }
            } while (tryAgain);

        } catch (AtestNoSuchWebElementException e) {
            _log.oops("Exception: Size Of Stock. " + e.getMessage());
            _evidence.screenshotOnError("ProductPage_clickSizeOutOfStock");
        } catch (AtestWebDriverException e) {
            _log.oops(e.getMessage());
        }
        return false;
    }
    
    public boolean productWithSize()  {
    	boolean rtCode = false;
    	 try {
    		 AtestWebElement awe = AtestWebElement.getInstance();
	    	 List<WebElement> sizes = this.findSize();
             if (!sizes.isEmpty()) {
	        		 for (int i = 0; i < sizes.size(); i++) {
	        			 WebElement el = sizes.get(i);
	        			 String cssClassName = el.getAttribute("class");
	        			 if (!cssClassName.contains("unavailable")) {
	        				 try{
	        					 el.click();
	        				 }	catch (StaleElementReferenceException e) {
	        	            	 _log.oops("Page refresh invalidades selectors. " + e.getMessage());
	       		             } 
	        				 break;
	        			 }
					}
	         } else {
	        	 _log.trace("Product without sizes.");
	         }
             rtCode = true;
             Utils.wait(2000);
             awe.findElement("ProductPage.button_buy").click();
             if(closeAppBanner()){
                 awe.findElement("ProductPage.button_buy").click();
             }
    	 } catch (AtestNoSuchWebElementException | AtestWebDriverException e) {
    		 _log.oops(e.getMessage()+ "ProductPage");
    		 _evidence.screenshotOnError("ProductPage_buy");
    	 } 
         return rtCode;
    }
    
    /**
     * Fill the data to be alerted when the size is in stock again.
     * @param email the user email.
     * @param name the name to the greetings.
     * @return true the alert has been registered successfully, otherwise false.
     */
    public boolean tellMe(String email, String name) {
        AtestWebElement awe = AtestWebElement.getInstance();
        try {
            awe.findElement("ProductPage.button_buy").click();
            awe.findElement("ProductPage.tellMe.name").sendKeys(name);
            awe.findElement("ProductPage.tellMe.email").sendKeys(email);
            _evidence.screenshot("form_filled");
            awe.findElement("ProductPage.tellMe.button_register").click();
            return true;
        } catch (AtestNoSuchWebElementException e) {
            _log.oops(e.getMessage());
            _evidence.screenshotOnError("ProductPage_tellMe");
        } catch (AtestWebDriverException e) {
            _log.oops(e.getMessage());
        }
        return false;
    }
    /**
     * Clicks on the buy button.
     * @return true on success, otherwise the buy button was not found.
     */
    public boolean buy() {
        boolean retCode = false;
            _evidence.screenshot("product");
            if(this.productWithSize()) {
            	retCode = true;
            }
        return retCode;
    }
    /**
     * Set the product to work for now on.
     * @param productURL the product page.
     * @param productType the product type.
     * @return true this is a valid product, otherwise false.
     */
    public boolean setProduct(String productURL, String productType) {
        boolean retCode = false;
        _currentProductConfig = null;
        _log.trace("product [" + productURL + "] type[" + productType + "]");
        try {
            if (validateProductType(productType)) {
                try {
                    _evidence.getWebDriver().navigate().to(productURL);
                    final String title = productType.replaceAll(" ", "_");
                    _evidence.screenshot("product_" + title);
                    retCode = true;
                } catch (AtestWebDriverException e) {
                    _log.oops(e.getMessage());
                }
            } else {
                _log.trace("Error: invalid product type");
            }
        } catch (AtestCoreException e) {
            _log.oops("failed to load product.json: " + e.getMessage());
        }
        return retCode;
    }
    /**
     * Returns the product type based on an official name (product.json)
     * @param productType
     * @return
     */
    private ProductType getProductType(String productType) {
        ProductType type = ProductType.NONE;
        final String pt = productType.toLowerCase().trim();
        if (pt.equals("ball")) {
            type = ProductType.BALL;
        } else if (pt.equals("t-shirt")) {
            type = ProductType.T_SHIRT;
        } else if (pt.equals("cap")) {
            type = ProductType.CAP;
        } else if (pt.equals("team t-shirt")) {
            type = ProductType.TEAM_T_SHIRT;
        } else if (pt.equals("jacket")) {
            type = ProductType.JACKET;
        }
        return type;
    }
    /**
     * Checks whether the product type is a valid one.
     * @param productType the product type
     * @return true the type is valid, otherwise false.
     */
    private boolean validateProductType(String productType) throws AtestCoreException {
        if (_configProducts == null) {
            ProductConfigLoader loader = new ProductConfigLoader();
            _configProducts = loader.getProductEntries();
        }
        boolean found = false;
        for (ProductConfigEntry e : _configProducts) {
            if (e.type_name.equalsIgnoreCase(productType)) {
                found = true;
                _currentProductConfig = e;
                break;
            } else {
                final String alternatives = String.join(" ", e.alternative_type_names);
                if (alternatives.contains(productType)) {
                    found = true;
                    _currentProductConfig = e;
                    break;
                }
            }
        }
        if (found) {
            // The official product type name is represented by: _currentProductConfig.type_name
            _currProductType = getProductType(_currentProductConfig.type_name);
        }
        return found;
    }
    /**
     * Customizes a product putting on it the Name and/or Number
     * @return true The product was customized successfully, otherwise false.
     */
    public boolean customize() {
        boolean retCode = false;
        if (_currentProductConfig != null) {
            try {
                switch (_currProductType) {
                    case BALL:
                        retCode = customizeBall();
                        break;
                    case T_SHIRT:
                        retCode = this.productWithSize();
                        break;
                    case CAP:
                        retCode = customizeCap();
                        break;
                    case TEAM_T_SHIRT:
                        retCode = customizeTeamTShirt();
                        break;
                    case JACKET:
                        retCode = customizeJacket();
                    case NONE:
                        break;
                }
            } catch (AtestWebDriverException e) {
                _log.oops(e.getMessage());
            }
        } else {
            _log.trace("invalid current product configuration instance. Validate the product type first (use validateProductType function)");
        }
        return retCode;
    }
    /**
     * Customizes a jacket
     * @return
     */
    private boolean customizeJacket() {
        boolean retCode = false;
        try {
            _evidence.screenshot("jacket_without_customizations");
            if (!clickOnFirstAvailableSizeForJacket()) {
                _log.trace("unable to choose a jacket size");
                return retCode;
            }
            AtestWebElement awe = AtestWebElement.getInstance();
            awe.waitUntilVisible("ProductPage.customizeJacket.name");
            awe.findElement("ProductPage.customizeJacket.name").sendKeys(_currentProductConfig.custom_name);
            _evidence.screenshot("customized_jacket");
            retCode = true;
        } catch (AtestNoSuchWebElementException e) {
            _log.oops(e.getMessage());
            _evidence.screenshotOnError("ProductPage_customizeJacket");
        } catch (AtestWebDriverException e) {
            _log.oops(e.getMessage());
        }
        return retCode;
    }
    /**
     *
     * @return
     */
    private boolean clickOnFirstAvailableSizeForJacket() throws AtestWebDriverException {
        boolean retCode = false;
        try {
            AtestWebElement awe = AtestWebElement.getInstance();
            List<WebElement> sizes = awe.findElements("ProductPage.clickOnFirstAvailableSizeForJacket.availableSize");
            WebElement size = sizes.get(0);
            _log.trace("selecting the first available size [" + size.getText().trim() + "] for a jacket");
            size.click();
            retCode = true;
        } catch (AtestNoSuchWebElementException e) {
            _log.oops(e.getMessage());
        }
        return retCode;
    }

    /**
     * Customize the team t-shirt
     * @return true on success, otherwise false
     */
    private boolean customizeTeamTShirt() throws AtestWebDriverException {
        boolean retCode = false;
        try {
            _evidence.screenshot("team_shirt_without_customizations");
            if (!clickOnFirstAvailableSizeForTeamTShirt()) {
                _log.trace("unable to choose a team t-shirt size");
                return retCode;
            }
            AtestWebElement awe = AtestWebElement.getInstance();
            awe.waitUntilVisible("ProductPage.customizeTeamTShirt.name");
            awe.findElement("ProductPage.customizeTeamTShirt.name").sendKeys(_currentProductConfig.custom_name);
            awe.findElement("ProductPage.customizeTeamTShirt.number").sendKeys(_currentProductConfig.custom_number);
            awe.findElement("ProductPage.customizeTeamTShirt.button_ver_como_ficou").click();
            awe.findElement("ProductPage.closeImageZoom").click();
            try {
                awe.waitForPageLoaded();
            } catch (AtestPageLoadedException e) {
                _log.info("WARNING: page not loaded after clicking on 'buy product': " + e.getMessage());
            }
            waitForCustomImageToShow(_currentProductConfig.custom_name);
            retCode = true;
        } catch (AtestNoSuchWebElementException e) {
            _log.oops(e.getMessage());
            _evidence.screenshotOnError("ProductPage_customizeTeamTShirt");
        }
        return retCode;
    }
    /**
     *
     * @param customName the name to looking for inside the image link
     */
    private void waitForCustomImageToShow(String customName) throws AtestNoSuchWebElementException, AtestWebDriverException {
        AtestWebElement awe = AtestWebElement.getInstance();

        List<WebElement> wes = awe.findElements("ProductPage.customizeTeamTShirt.image_after_customization");
        for (WebElement e : wes) {
            if (e.getAttribute("data-zoom").contains(customName)) {
                break;
            }
        }
    }

    /**
     * Choose the first available size
     */
    private boolean clickOnFirstAvailableSizeForTeamTShirt() throws AtestWebDriverException {
        boolean retCode = false;
        try {
            AtestWebElement awe = AtestWebElement.getInstance();
            List<WebElement> sizes = awe.findElements("ProductPage.clickOnFirstAvailableSizeForTeamTShirt.availableSize");
            WebElement size = sizes.get(0);
            _log.trace("selecting the first available size [" + size.getText().trim() + "]");
            size.click();
            retCode = true;
        } catch (AtestNoSuchWebElementException e) {
            _log.oops(e.getMessage());
        }
        return retCode;
    }

    public boolean clickOnlyProductsInStock() {
        boolean retCode = false;
        try {
            AtestWebElement.getInstance().findElement("ProductPage.clickOnlyInStockSize").click();
            _evidence.screenshot("Choose product size in stock");
            retCode = true;

        } catch (AtestNoSuchWebElementException e) {
            _log.oops("Unable to find 'in stock' sizes" + e.getMessage());
        } catch (AtestWebDriverException e) {
            _log.oops(e.getMessage());
        }
        return retCode;
    }

    public boolean clickAnyProductSize() {

        boolean retCode = false;
        try {
            AtestWebElement.getInstance().findElement("ProductPage.clickAnySize").click();
            _evidence.screenshot("Choose product size");
            retCode = true;

        } catch (AtestNoSuchWebElementException e) {
            _log.oops("Unable to find sizes for this product" + e.getMessage());
        } catch (AtestWebDriverException e) {
            _log.oops(e.getMessage());
        }
        return retCode;
    }

    public String customizeCapFront(AtestWebElement awe) {
    	String front = "";
    	try {
    			WebElement persFront  = awe.findElement("ProductPage.customizeCap.persFront");
    			front = _currentProductConfig.custom_name_front;
    			persFront.sendKeys(front);
    		 
    	} catch(Exception e) {
    		_log.info("This product don't have front customization " + e.getMessage());
    	}
    	return front;
    }
    
    public String customizeCapLeft(AtestWebElement awe) {
    	String left = "";
    	try {
    		WebElement persLeft  = awe.findElement("ProductPage.customizeCap.persLeft");
    		left = _currentProductConfig.custom_name_left;
    		persLeft.sendKeys(left);
    		 
    	} catch(Exception e) {
    		 _log.info("This product don't have left customization " + e.getMessage());
    	}
    	return left;
    }
    
    public String customizeCapRigth(AtestWebElement awe) {
    	String right = "";
    	try {
    		WebElement persRight = awe.findElement("ProductPage.customizeCap.persRight");
    		right = _currentProductConfig.custom_name_right;
    		persRight.sendKeys(right);
    		 
    	} catch(Exception e) {
    		 _log.info("This product don't have right customization " + e.getMessage());
    	}
    	return right;
    }

    /**
     * Customize a Cap (bone). This kind of product has customization for the both sides (left and right).
     * @return true on success, otherwise false
     */
    private boolean customizeCap() throws AtestWebDriverException {
        boolean retCode = false;
        try {
            AtestWebElement awe = AtestWebElement.getInstance();
            final String front = this.customizeCapFront(awe);
            final String left = this.customizeCapLeft(awe);
            final String right = this.customizeCapRigth(awe);
            _log.trace("custom name front["+ front +"] left[" + left + "] right[" + right + "]");
            _evidence.screenshot("custom_cap_" +front+"_"+ left + "_" + right);
            retCode = true;
        } catch (Exception e) {
            _log.oops(e.getMessage());
            _evidence.screenshotOnError("ProductPage_customizeCap");
        }
        return retCode;
    }

    private boolean customizeBall() throws AtestWebDriverException {
        String customName = _currentProductConfig.custom_name.isEmpty() ? "SEM NOME" : _currentProductConfig.custom_name;
        try {
            AtestWebElement.getInstance().findElement("ProductPage.customizeBall.custom_name").sendKeys(customName);
            return true;
        } catch (AtestNoSuchWebElementException e) {
            _evidence.screenshotOnError("ProductPage_customizeBall");
            _log.oops(e.getMessage());
        }
        return false;
    }

    /**
     * Navigate to the product page
     * @param productURL the product page
     * @return true the product page was loaded, otherwise false.
     */
    public boolean navigateTo(String productURL) {
        try {
            //_evidence.getWebDriver().navigate().to(productURL);
            AtestWebElement.getInstance().waitForPageLoaded();
        } catch (AtestPageLoadedException e) {
            _log.info("WARNING: the product page was not fully loaded...");
        } catch (AtestWebDriverException e) {
            _log.oops(e.getMessage());
        }
        return true;
    }
    /**
     * Clicks on button "comprar junto" which is a indication for buying two products (bundle)
     * @return true on success, otherwise false.
     */
    public boolean buyBundle() {
        boolean retCode = false;
        try {
            AtestWebElement awe = AtestWebElement.getInstance();
            WebElement btnComprarJunto = awe.findElement("ProductPage.buyBundle.button_comprar_junto");
            Actions action = new Actions(_evidence.getWebDriver());
            action.moveToElement(btnComprarJunto).perform();
            _evidence.screenshot("mouse_over_button_comprar_junto");
            btnComprarJunto.click();
            if(closeAppBanner()){
                btnComprarJunto.click();
            }
            awe.waitUntilVisible("ProductPage.buyBundle.popup_title");
            retCode = true;
        } catch (AtestNoSuchWebElementException ex) {
            _log.oops(ex.getMessage());
            _evidence.screenshotOnError("ProductPage_buyBundle");
        } catch (AtestWebDriverException e) {
            _log.oops(e.getMessage());
        }
        return retCode;
    }

    public boolean BuyMultiplus(String productURL, String userName, String Password) {

        boolean retCode = false;

        ProductPage getProduct = new ProductPage();


        PaymentPage payProduct = new PaymentPage();
        LoginPage dologin = new LoginPage();
        MenuUserAccountPage chooseOptionMenu = new MenuUserAccountPage();

        chooseOptionMenu.chooseOptionLogin();
        dologin.signIn(userName, Password);


        getProduct.navigateTo(productURL);
        getProduct.clickOnlyProductsInStock();
        _evidence.screenshot("Choose_product");
        AtestWebElement awe = AtestWebElement.getInstance();

        Utils.wait(2000);
        try {


            awe.findElement("ProductPage.button_buy").click();

            try {

                awe.findElement("CartPage.CheckIfMultiplus");
                _evidence.screenshot("is Multiplus");

            } catch (AtestNoSuchWebElementException e) {
                _log.oops("Unable to find the element that determines that the product has Multiplus' points " + e.getMessage());


            }

            awe.findElement("CartPage.finalizeOrder.button_finalize").click();

            awe.findElement("PaymentPage.submit_payment").click();

            payProduct.payWithBoleto();
            _evidence.screenshot("Multiplus_Product_bought");
            PaymentConfirmationPage getOrder = new PaymentConfirmationPage();
            String orderNum = getOrder.getOrderNumber();
            _log.info(orderNum);




            retCode = true;


        } catch (AtestNoSuchWebElementException e) {
            _log.oops("Unable to find element related to the payment finalization " + e.getMessage());
            _evidence.screenshotOnError("Multiplus_product");

        } catch (AtestWebDriverException e) {
            _log.oops(e.getMessage());
        }
        return retCode;
    }

    public boolean tableSize() {
        boolean retCode = false;

        AtestWebElement awe = AtestWebElement.getInstance();

        try {
            awe.waitForPageLoaded();

            awe.findElement("ProductPage.sizetable").click();
            _evidence.screenshot("open_size_table");

            awe.findElement("ProductPage.sizetableContentTitle").isDisplayed();
            awe.findElement("ProductPage.sizetableContent").isDisplayed();

            WebElement brand = awe.findElement("ProductPage.SizeTableBrand");


            new Select(brand).selectByIndex(4);
            awe.findElement("ProductPage.sizetableContentTitle").isDisplayed();
            awe.findElement("ProductPage.sizetableContent").isDisplayed();
            _evidence.screenshot("Brand_4");
            new Select(brand).selectByIndex(7);
            awe.findElement("ProductPage.sizetableContentTitle").isDisplayed();
            awe.findElement("ProductPage.sizetableContent").isDisplayed();
            _evidence.screenshot("Brand_7");
            new Select(brand).selectByIndex(6);
            awe.findElement("ProductPage.sizetableContentTitle").isDisplayed();
            awe.findElement("ProductPage.sizetableContent").isDisplayed();
            _evidence.screenshot("Brand_6");
            new Select(brand).selectByIndex(3);
            awe.findElement("ProductPage.sizetableContentTitle").isDisplayed();
            awe.findElement("ProductPage.sizetableContent").isDisplayed();
            _evidence.screenshot("Brand_3");
            retCode = true;

        } catch (AtestNoSuchWebElementException e) {
            _log.oops("The element related to the size table link was not found" + e.getMessage());
            _evidence.screenshotOnError("Table_size");
        } catch (AtestWebDriverException e) {
            e.printStackTrace();
        } catch (AtestPageLoadedException e) {
            _log.info("WARNING: page not loaded: " + e.getMessage());
        }


        return retCode;

    }

    public boolean isTableSizeOk(){
        boolean retCode = false;

        AtestWebElement awe = AtestWebElement.getInstance();

        try {
            awe.findElement("ProductPage.SizeTableGender").isDisplayed();
             retCode = true;
        }  catch (AtestNoSuchWebElementException e) {
            _log.oops("The element related to the genders was not found" + e.getMessage());
        } catch (AtestWebDriverException e) {
            e.printStackTrace();
        }

        return retCode;
    }

    public boolean goToPowerReviewers() {
        boolean retCode = false;

        AtestWebElement awe = AtestWebElement.getInstance();

        try {
            awe.findElement("ProductPage.isPowerReview").click();
            _evidence.screenshot("Power_reviewers");
            retCode = true;
        } catch (AtestNoSuchWebElementException e) {
            _evidence.screenshotOnError("Power_reviewers");
            _log.oops("The element related to the power reviewers was not found" + e.getMessage());
        } catch (AtestWebDriverException e) {
            e.printStackTrace();
        }


        return retCode;
    }


    public boolean isPowerReviewersOK() {
        boolean retCode = false;

        AtestWebElement awe = AtestWebElement.getInstance();

        try {

            awe.findElement("ProductPage.PowerReviewEvaluateButton").isDisplayed();
            WebElement mainComments = awe.findElement("ProductPage.PowerReviewMainDescription");
            mainComments.isDisplayed();
            String textMainComments = mainComments.getText();
            System.out.println(textMainComments);
            textMainComments.trim().equalsIgnoreCase("Prós");
            textMainComments.trim().equalsIgnoreCase("Contras");
            textMainComments.trim().equalsIgnoreCase("Melhores Aplicações");

           WebElement mainBuyerInfo =  awe.findElement("ProductPage.PowerReviewBuyerInfo");
            mainBuyerInfo.isDisplayed();
            String textMainBuyer = mainBuyerInfo.getText();
            System.out.println(textMainBuyer);
            textMainBuyer.trim().equalsIgnoreCase("Por");
            textMainBuyer.trim().equalsIgnoreCase("de");
            textMainBuyer.trim().equalsIgnoreCase("Sobre minha pessoa");




            retCode = true;
        } catch (AtestNoSuchWebElementException e) {
            _log.oops("The element related to the power reviewers section was not found" + e.getMessage());
        } catch (AtestWebDriverException e) {
            e.printStackTrace();
        }


        return retCode;
    }

    private boolean closeAppBanner(){
        AtestWebElement awe = AtestWebElement.getInstance();
        boolean hasAppBanner = false;
        try{
            hasAppBanner = awe.findElement("Netshoes.closeAppBanner").isDisplayed();
            _log.trace("AppBanner is displayed.");
        if(TestController.WebBrowser.IE == _evidence.getBrowserInUse() && hasAppBanner){
            awe.findElement("Netshoes.closeAppBanner").click();
        }
        }catch (AtestNoSuchWebElementException | AtestWebDriverException ex){
            _log.trace("AppBanner is not displayed.");
        }
        return hasAppBanner;
    }



}
