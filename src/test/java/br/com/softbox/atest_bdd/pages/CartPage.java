package br.com.softbox.atest_bdd.pages;

import br.com.softbox.core.*;
import br.com.softbox.utils.Utils;

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Controls the CartControls the CartPage
 */
public class CartPage {
    private AtestEvidence _evidence = AtestEvidence.getInstance();
    private AtestLog _log = new AtestLog("CartPage");
    private ProductPage addToCart = new ProductPage();

    public CartPage() {
    }

    private boolean isEmpty() {
        boolean retCode = false;
        AtestWebElement awe = AtestWebElement.getInstance();
        try {
            if (awe.findElement("CartPage.isEmpty.minicart_total").getText().trim().toLowerCase().equals("0")) {
                retCode = true;
            }
        } catch (AtestNoSuchWebElementException e) {
            _log.oops("Unable to find mini cart total of items");
        } catch (AtestWebDriverException e) {
            _log.oops(e.getMessage());
        }
        return retCode;
    }

    public void cleanUp() {
        try {
            if (isEmpty()) { return; }
            goToCartPage();
            _log.info("Removing all items from the cart...");
            AtestWebElement awe = AtestWebElement.getInstance();
            Utils.wait(1000);
            awe.findElement("CartPage.cleanUp.button_remove").click();
            try {
                awe.waitForPageLoaded();
            } catch (AtestPageLoadedException e) {
                _log.oops(e.getMessage());
            }
        } catch (AtestNoSuchWebElementException e) {
            _log.oops(e.getMessage());
        } catch (AtestWebDriverException e) {
            _log.oops(e.getMessage());
        }
    }

    private void goToCartPage() throws AtestNoSuchWebElementException, AtestWebDriverException {
        AtestWebElement awe = AtestWebElement.getInstance();
        awe.findElement("CartPage.goToCartPage.click_mini_cart_button").click();
        try {
            awe.waitForPageLoaded();
        } catch (AtestPageLoadedException e) {
            _log.oops(e.getMessage());
        }
        String cartTitle = awe.findElement("CartPage.goToCartPage.cart_title").getText();
        if (!cartTitle.equalsIgnoreCase("carrinho")) {
            throw new AtestNoSuchWebElementException("this is not the Cart page");
        }
    }

    public boolean finalizeOrder() {
        boolean retCode = false;
        AtestWebElement awe = AtestWebElement.getInstance();
        try {
            Utils.wait(2000);
            if (TestController.WebBrowser.IE == _evidence.getBrowserInUse()) {
                awe.findElement("CartPage.finalizeOrder.button_finalize").sendKeys(Keys.ENTER);
            }else {
                awe.findElement("CartPage.finalizeOrder.button_finalize").click();
            }
            try {
                awe.waitForPageLoaded();
                _log.trace("clicked on \"CONCLUIR COMPRA\"");
                retCode = true;
            } catch (AtestPageLoadedException e) {
                _log.oops("page not loaded after clicking on 'concluir compra (carrinho): " + e.getMessage());
            }
            retCode = true;
        } catch (AtestNoSuchWebElementException e) {
            _log.trace(e.getMessage());
            _evidence.screenshotOnError("cart_CONCLUIR_COMPRA");
        } catch (AtestWebDriverException e) {
            _log.oops(e.getMessage());
        }
        return retCode;
    }
    /**
     * Verifies whether the additional value for personalization is present.
     * @return
     */
    public boolean checkAdditionalValueForPersonalization() {
        boolean retCode = false;
        AtestWebElement awe = AtestWebElement.getInstance();
        try {
            WebElement we = awe.findElement("CartPage.checkAdditionalValueForPersonalization.additional_price_persona");
            if (we.isDisplayed()) {
                _log.trace("OK - customization. Price [" + we.getText() + "]");
                _evidence.screenshot("cart_additional_value_for_customization");
                retCode = true;
            } else {
                _log.trace("No additional value is visible for the \"personalizacao\"");
            }
        } catch (AtestNoSuchWebElementException e) {
            _log.oops(e.getMessage());
            _evidence.screenshotOnError("CartPage_checkAdditionalValueForPersonalization");
        } catch (AtestWebDriverException e) {
            _log.oops(e.getMessage());
        }
        return retCode;
    }

    public boolean isRightProductAdded(){
        boolean retCode = false;

        AtestWebElement awe = AtestWebElement.getInstance();


        String _productName = null;


        try {
            _productName = _evidence.getWebDriver().getCurrentUrl();
        } catch (AtestWebDriverException e) {
            _log.oops(e.getMessage());
        }

        Utils.wait(1500);

        try {
            awe.waitForPageLoaded();
            addToCart.clickOnlyProductsInStock();
             Thread.sleep(2);
            addToCart.buy();


            Thread.sleep(2);

            List<WebElement> _allElementsAdded = awe.findElements("CartPage.verifyIfProductAdded");
            int num = 0;

            for (WebElement check : _allElementsAdded) {

                String _newUrl = check.getAttribute("href");

                if (_productName.contains(_newUrl)) {

                    num = 1;

                    break;
                }

            }

            if (num == 1) {
                retCode = true;
            }
            _evidence.screenshot("Product_in_the_cart");
            awe.findElement("CartPage.RemoveFromCart").click();

        } catch (AtestNoSuchWebElementException e) {
            _log.oops("Unable to find the link to add the product to CartPage" + e.getMessage());
            _evidence.screenshotOnError("Product_in_the_cart");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (AtestWebDriverException e) {
            _log.oops(e.getMessage());
        } catch (AtestPageLoadedException e) {
            _log.info("WARNING: page not loaded: " + e.getMessage());
        }

        return retCode;
    }
    
    public String getMsgFreigth(AtestWebElement element) {
    	String  freightMsg = "";
    	try {
    		freightMsg = element.findElement("CartPage.isFreeFreight.msg_freight").getText();
    	 } catch (AtestNoSuchWebElementException | AtestWebDriverException e) {
    		 _log.info("Doesn't exist free freight message.");
    	}
		return freightMsg;
    }
    
    public Double getFreightPrice(AtestWebElement element) {
    	Double  freightPrice = 0.00;
    	String freightReturn = "";
    	try {
    		freightReturn = element.findElement("CartPage.isFreeFreight.freight_price").getText();
    		freightPrice = this.validateStringToDouble(freightReturn);
    	 } catch (AtestNoSuchWebElementException | AtestWebDriverException e) {
    		 _log.info("Doesn't exist price freight");
    	}
		return freightPrice;
    }
    
    public Double validateStringToDouble(String price) {
    	Double priceProduct = 0.00;
    	String priceResult = "";
    	priceResult = price.substring(2);
    	priceResult = priceResult.trim();
		priceProduct = Double.parseDouble(priceResult.replace(',', '.'));
		return priceProduct;
    }
    
    public Double getPriceProduct(AtestWebElement element, String type) {
    	Double priceProduct = 0.00;
    	String priceResult = "";
    	try {
    		if (type == "new") {
    			priceResult = element.findElement("CartPage.isFreeFreight.new_price").getText();
    		}
    		
    		if (type == "final") {
    			priceResult = element.findElement("CartPage.isFreeFreight.final_price").getText();    			
    		}
    		priceProduct = this.validateStringToDouble(priceResult);
    		
    	 } catch (AtestNoSuchWebElementException | AtestWebDriverException e) {
    		 _log.info("Doesn't exist price");
    	}
		return priceProduct;
    }
      
    
    public boolean setCepFieldAndClickButton(String cep1) {
    	boolean retCode = false;
    	
    	AtestWebElement awe = AtestWebElement.getInstance();
    	try {
    		awe.findElement("CartPage.isFreeFreight.Cep1_field").sendKeys(cep1);
    		awe.findElement("CartPage.isFreeFreight.calculate_freight").click();
    		retCode = true;
    		Utils.wait(2000);
    		//validate ops message
		
    	 } catch (AtestNoSuchWebElementException | AtestWebDriverException e) {
             _log.oops(e.getMessage() +"cartPageError " );
             _evidence.screenshotOnError("CartPageFreight");
		}
    	return retCode;
    }
    
    public boolean getBannerMsg(String type) {
    	boolean retCode = false;
    	String msg = "";
    	
    	if(type == "inicial") {
    		msg = "Adicione 2 produtos para você participar da promoção Compre 3 pelo preço de 2";
    	}
    	
    	if(type == "final"){
    		msg = "Promoção Compre 3 pelo preço de 2 aplicada com sucesso!";
    	}
    	AtestWebElement awe = AtestWebElement.getInstance();
    	try {
    		String msgBanner = awe.findElement("CartPage.PromotionBanner.msg1").getText();
    		
    		retCode = msg.equals(msgBanner);
    	
    	} catch (AtestNoSuchWebElementException | AtestWebDriverException e) {
            _log.oops(e.getMessage() +"CartPageError Banner not found" );
            _evidence.screenshotOnError("CartPageBanner");
    	}
    	return retCode;
    }
    public boolean increaseAmout(int qtd){
    	boolean retCode = false;
    	AtestWebElement awe = AtestWebElement.getInstance();
    	try {
    		for(int i=1; i<qtd; i++) {
    			awe.findElement("CartPage.increaseAmount").click();
    		}
    		retCode = true;
    	
    	} catch (AtestNoSuchWebElementException | AtestWebDriverException e) {
            _log.oops(e.getMessage() +"CartPageError - Increase amount button Not found" );
            _evidence.screenshotOnError("CartPageBanner");
    	}
    	return retCode;
    }
    
    public boolean buyTwoGetThreePromotion(Double min, Double max) {
    	boolean retCode = false;
    	AtestWebElement awe = AtestWebElement.getInstance();
    	Double unitPrice = this.getPriceProduct(awe, "new");
    	if(this.increaseAmout(3)) {
    		 _evidence.screenshot("CartPageWith3Itens");
    		Double expectedPrice = unitPrice * 2;
    		if(expectedPrice >= min && expectedPrice <= max ){
    			retCode = true;
    		}
    	}
    	return retCode;
    }
    
    public String getDateMessage(AtestWebElement element) {
    	
    	String dateMessage = "";
    	try {
    		dateMessage = element.findElement("CartPage.isFreeFreight.dateMessage").getText();
    		dateMessage = dateMessage.substring(0, dateMessage.length() - 11);
    		
    	 } catch (AtestNoSuchWebElementException | AtestWebDriverException e) {
             _log.oops(e.getMessage());
             _evidence.screenshotOnError("CarPageFreightDate");
		}
		return dateMessage;
    }
    
    public boolean getMsgPromotion() {
    	boolean retCode = false;
    	AtestWebElement awe = AtestWebElement.getInstance();
    	try {
    		String flagPromotionText = awe.findElement("CartPage.flagPromotion").getText();
    		retCode = flagPromotionText.equals("Compre 2\nLeve 3");
    	} catch (AtestNoSuchWebElementException | AtestWebDriverException e) {
    	    _log.oops(e.getMessage() +"CartPageError - Flag Promotion not found" );
    	    _evidence.screenshotOnError("CartPageBanner");
    	}
    	return retCode;
    }
}
