package br.com.softbox.atest_bdd.pages;

import br.com.softbox.core.*;
import br.com.softbox.utils.Utils;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ThreadGuard;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the payment page.
 */
public class PaymentPage {
    private AtestEvidence _evidence = AtestEvidence.getInstance();
    private AtestLog _log = new AtestLog("PaymentPage");
    private final String AGENDADA = "agendada";

    
    public PaymentPage() {
    }
    /**
     * Returns whether the current page is the payment page.
     * @return true is the payment page, otherwise false.
     */
    private void makeSureThisIsThePaymentPage() throws AtestNoSuchWebElementException, AtestWebDriverException {
        AtestWebElement.getInstance().findElement("PaymentPage.makeSureThisIsThePaymentPage.payment_title").isDisplayed();
    }
    /**
     * Proceed with the payment.
     * @param cardHolder The card holder (visa, mastercard, amex, etc)
     * @param cardUserName The user name on the card.
     * @param cardNumber The card number.
     * @param cardValidity the card expiration date.
     * @param cardCVV The security card number.
     * @return true the payment was succeeded, otherwise false.
     */
    public boolean payWithCreditCard( String cardHolder
                                    , String cardUserName
                                    , String cardNumber
                                    , String cardValidity
                                    , String cardCVV) {
        try {
            makeSureThisIsThePaymentPage();
            fillCardNumber(cardNumber, "PaymentPage.payWithCreditCard.card_number");
            waitCreditCardHolderIcon(cardHolder);
            fillCardUserName(cardUserName, "PaymentPage.payWithCreditCard.card_user_name");
            fillCardValidity(cardValidity, "PaymentPage.payWithCreditCard.card_expiration");
            fillCardCVV(cardCVV, "PaymentPage.payWithCreditCard.card_cvv");
            if (hasOps("payWithCreditCard")) { return false; }

            _evidence.screenshot("payment_with_credit_card");
            if (!submitPayment(false /*is not for two cards*/)) { return false; }
            return true;
        } catch (AtestNoSuchWebElementException e) {
            _log.oops(e.getMessage());
            _evidence.screenshotOnError("PaymentPage_payWithCreditCard");
        } catch (AtestWebDriverException e) {
            _log.oops(e.getMessage());
        }
        return false;
    }
    /**
     * Proceed with the payment using the number of installments.
     * @param numInstallments The number of installments
     * @param cardHolder The card holder (visa, mastercard, amex, etc)
     * @param cardUserName The user name on the card.
     * @param cardNumber The card number.
     * @param cardValidity the card expiration date.
     * @param cardCVV The security card number.
     * @return true the payment was succeeded, otherwise false.
     */
    public boolean payWithCreditCardAndInstallments(String numInstallments
                                                   , String cardHolder
                                                   , String cardUserName
                                                   , String cardNumber
                                                   , String cardValidity
                                                   , String cardCVV) {
        try {
            makeSureThisIsThePaymentPage();
            fillCardNumber(cardNumber, "PaymentPage.payWithCreditCardAndInstallments.card_number");
            waitCreditCardHolderIcon(cardHolder);
            fillCardUserName(cardUserName, "PaymentPage.payWithCreditCardAndInstallments.card_user_name");
            fillCardValidity(cardValidity, "PaymentPage.payWithCreditCardAndInstallments.card_expiration");
            fillCardCVV(cardCVV, "PaymentPage.payWithCreditCardAndInstallments.card_cvv");
            if (!chooseNumInstallments(numInstallments, "PaymentPage.payWithCreditCardAndInstallments.installments_combo")) { return false; }
            if (hasOps("payWithCreditCardAndInstallments")) { return false; }
            _evidence.screenshot("payment_with_installments");
            if (!submitPayment(false /*is not for two cards*/)) { return false; }
            return true;
        } catch (AtestNoSuchWebElementException e) {
            _log.oops( e.getMessage());
            _evidence.screenshotOnError("PaymentPage_payWithCreditCardAndInstallments");
        } catch (AtestWebDriverException e) {
            _log.oops(e.getMessage());
        }
        return false;
    }
    /**
     *
     * @param numInstallments
     * @param id
     * @return
     */
    private boolean chooseNumInstallments(String numInstallments, String id) {
        boolean retCode = false;
        AtestWebElement awe = AtestWebElement.getInstance();
        try {
            WebElement comboInstallments = awe.findElement(id);
            Select select = new Select(comboInstallments);
            if (select == null) {
                _log.trace("ERROR installments element not found (dropdown list), id[" + id + "]");
                _evidence.screenshotOnError("dropdown_list_of_installments_element_not_found");
                return retCode;
            }
            List<String> availableInstallments = new ArrayList<String>();
            List<WebElement> opts = select.getOptions();
            for (WebElement opt : opts) {
                availableInstallments.add(opt.getText().trim());
                if (opt.getAttribute("value").equals(numInstallments)) {
                    _log.trace("found the number of installments [" + opt.getText().trim() + "]");
                    try {
                        select.selectByValue(numInstallments);
                        retCode = true;
                    } catch (NoSuchElementException e) {
                        _log.trace("ERROR: No number of expected installments, just these are available: " + availableInstallments.toString());
                        _evidence.screenshotOnError("PaymentPage_could_not_select_installments");
                    }
                    break;
                }
            }
        } catch (AtestNoSuchWebElementException e) {
            _log.oops( e.getMessage());
            _evidence.screenshotOnError("PaymentPage_chooseNumInstallments");
        } catch (AtestWebDriverException e) {
            _log.oops(e.getMessage());
        }
        return retCode;
    }
    /**
     *
     * @return
     */
    public boolean submitPayment(boolean usingTwoCards) {
        boolean retCode = false;
        final String selectorId = (usingTwoCards) ? "PaymentPage.submit_payment_for_2_cards" : "PaymentPage.submit_payment";
        try {
            AtestWebElement awe = AtestWebElement.getInstance();
            awe.findElement(selectorId).click();
            retCode = true;
            awe.waitForPageLoaded();
        } catch (AtestNoSuchWebElementException e) {
            _log.oops( e.getMessage());
            _evidence.screenshotOnError("PaymentPage_submitPayment_payment_page_missing_finish_order_button");
        } catch (AtestPageLoadedException e) {
            _log.info("WARNING: page not loaded after clicking on 'submit payment': " + e.getMessage());
        } catch (AtestWebDriverException e) {
            _log.oops(e.getMessage());
        }
        return retCode;
    }
    /**
     * Fills the card CVV code (security code)
     * @param cardCVV
     * @param id
     * @return
     */
    private void fillCardCVV(String cardCVV, String id) throws AtestNoSuchWebElementException, AtestWebDriverException {
        AtestWebElement.getInstance().findElement(id).sendKeys(cardCVV);
    }

    private boolean hasOps(String msg) {
        boolean retCode = false;
        try {
            WebElement ops = AtestWebElement.getInstance().findElement("PaymentPage.ops");
            if (ops.isDisplayed()) {
                _log.trace("OOPS: filling payment page [" + msg + "]");
                _evidence.screenshotOnError("OOPS_fill_payment_page");
                retCode = true;
            }
        } catch (AtestNoSuchWebElementException e) {
            _log.trace("Cool, no OOPS found, filling the card form. " + msg);
        } catch (AtestWebDriverException e) {
            _log.oops(e.getMessage());
        }
        return retCode;
    }

    private void fillCardValidity(String cardValidity, String id) throws AtestNoSuchWebElementException, AtestWebDriverException {
        AtestWebElement.getInstance().findElement(id).sendKeys(cardValidity);
    }

    private void fillCardUserName(String cardUserName, String id) throws AtestNoSuchWebElementException, AtestWebDriverException {
        AtestWebElement.getInstance().findElement(id).sendKeys(cardUserName);
    }

    private void fillCardNumber(String cardNumber, String id) throws AtestNoSuchWebElementException, AtestWebDriverException {
        AtestWebElement.getInstance().findElement(id).sendKeys(cardNumber);
    }

    /**
     * Waits until the credit card holder is visible.
     * @param cardHolder the credit card holder name
     * @return true the icon is visible, otherwise false.
     */
    private void waitCreditCardHolderIcon(String cardHolder) throws AtestNoSuchWebElementException, AtestWebDriverException {
        AtestWebElement awe = AtestWebElement.getInstance();
        if (cardHolder.equalsIgnoreCase("visa")) {
            awe.findElement("PaymentPage.waitCreditCardHolderIcon.visa_icon");
            _log.trace("detected Visa card");
        } else if (cardHolder.equalsIgnoreCase("master")) {
            awe.findElement("PaymentPage.waitCreditCardHolderIcon.master_icon");
            _log.trace("detected Mastercard");
        } else if (cardHolder.equalsIgnoreCase("elo")) {
            awe.findElement("PaymentPage.waitCreditCardHolderIcon.elo_icon");
            _log.trace("detected Elo card");
        } else if (cardHolder.equalsIgnoreCase("hiper")) {
            awe.findElement("PaymentPage.waitCreditCardHolderIcon.hiper_icon");
            _log.trace("detected Hipercard");
        } else if (cardHolder.equalsIgnoreCase("diners")) {
            awe.findElement("PaymentPage.waitCreditCardHolderIcon.diners_icon");
            _log.trace("detected Diners card");
        } else if (cardHolder.equalsIgnoreCase("amex")) {
            awe.findElement("PaymentPage.waitCreditCardHolderIcon.amex_icon");
            _log.trace("detected American Express card");
        }
    }
    /**
     * Selects a shipping type (Normal, Expressa, Super Expressa, etc)
     * @param shippingType Normal, Expressa, Super Expressa
     * @return
     */
    public boolean selectShipping(String shippingType) {
        boolean retCode = false;
        try {
            makeSureThisIsThePaymentPage();
            Utils.wait(2000);
            WebElement tableShipping = AtestWebElement.getInstance().findElement("PaymentPage.selectShipping.shipping");
            selectShippingFromAvailableList(tableShipping, shippingType);
            retCode = true;
        } catch (AtestNoSuchWebElementException e) {
            _log.oops( e.getMessage());
            _evidence.screenshotOnError("PaymentPage_select_shipping");
        } catch (AtestWebDriverException e) {
            _log.oops(e.getMessage());
        }
        return retCode;
    }

    private void selectShippingFromAvailableList(WebElement tableShipping, String shippingType) throws AtestNoSuchWebElementException {
        try {
            List<WebElement> shippingLabels = AtestWebElement.getInstance()
                    .findElements(tableShipping, "PaymentPage.selectShipping.shipping_labels");
            if (shippingLabels.isEmpty()) {
                throw new AtestNoSuchWebElementException("ERROR: shipping labels not found");
            }
            // Get the rows
            List<String> validShippingTypes = new ArrayList<String>();
            for (WebElement e : shippingLabels) {
                String type = e.getText().trim();
                validShippingTypes.add(type);
                if (type.contains(shippingType)) {
                    String checkboxShippingID = e.getAttribute("for");
                    try {
                        WebElement checkBox = (new WebDriverWait(_evidence.getWebDriver(), 10))
                                .until((WebDriver wd) -> {
                                    return wd.findElement(By.id(checkboxShippingID));
                                });
                        if (checkBox != null) {
                            _log.info("Selecting shipping: " + shippingType);
                            checkBox.click();
                            if (shippingType.toLowerCase().equals(AGENDADA)) {
                                if (!selectFirstAvailableDate()) {
                                    throw new AtestNoSuchWebElementException("Unable to choose shipping schedule");
                                }
                            }
                            return;
                        } else {
                            throw new AtestNoSuchWebElementException("ERROR: could not get the checkbox for shipping ["
                                    + shippingType + "] using id[" + checkboxShippingID + "]");
                        }
                    } catch (Exception ex) {
                        throw new AtestNoSuchWebElementException(ex.getMessage());
                    }
                }
            }
            throw new AtestNoSuchWebElementException("Invalid shippingType[" + shippingType
                    + "]. The valid ones are: [" + validShippingTypes.toString() + "]");
        } catch (AtestNoSuchWebElementException e) {
            throw e;
        }
    }

    private boolean selectFirstAvailableDate() {
        Utils.wait(2000);
        AtestWebElement awe = AtestWebElement.getInstance();
        try {
            Utils.wait(2000);
            awe.findElement("PaymentPage.selectFirstAvailableDate.open_schedule").click();
            Utils.wait(2000);
            awe.findElement("PaymentPage.selectFirstAvailableDate.current_day").click();
            awe.findElement("PaymentPage.selectFirstAvailableDate.radio_delivery_shift").click();
            awe.findElement("PaymentPage.selectFirstAvailableDate.choose_and_close").click();
            Utils.wait(2000);
            final String date = awe.findElement("PaymentPage.selectFirstAvailableDate.open_schedule").getText().trim().toLowerCase();
            return date.contains("escolher") ? false : true;  // must show the choosen date instead of "escolher data"
        } catch (AtestNoSuchWebElementException e) {
            _log.oops(e.getMessage());
            _evidence.screenshotOnError("scheduled_shipping");
        } catch (AtestWebDriverException e) {
            ;
        }
        return false;
    }

    /**
     * Selects the option to pay using two credit cards.
     * @return
     */
    public boolean selectPayUsingTwoCreditCards() {
        try {
            AtestWebElement awe = AtestWebElement.getInstance();
            awe.findElement("PaymentPage.selectPayUsingTwoCreditCards.more_cards").click();
            awe.waitForPageLoaded();
            return awe.findElement("PaymentPage.selectPayUsingTwoCreditCards.card_2_form").isDisplayed();
        } catch (AtestNoSuchWebElementException | AtestWebDriverException e) {
            _log.oops( e.getMessage());
            _evidence.screenshot("ERROR_missing_link_pay_with_two_cards");
        } catch (AtestPageLoadedException e) {
            _log.info("WARNING: page not loaded after clicking on 'pay with two credit cards': " + e.getMessage());
            _evidence.screenshotOnError("PaymentPage_page_not_loaded_selectPayUsingTwoCreditCards");
        }
        return false;
    }
    /**
     * Fill the credit card form for the first Credit Card.
     * @note Do not finish the order.
     * @param amountToPay The value pay with the first card
     * @param numInstallments The number of installments
     * @param cardUserName The user name on the card.
     * @param cardNumber The card number.
     * @param cardValidity the card expiration date.
     * @param cardCVV The security card number.
     * @return true the payment was succeeded, otherwise false.
     */
    public boolean fill1stCard( String amountToPay
                              , String numInstallments
                              , String cardUserName
                              , String cardNumber
                              , String cardValidity
                              , String cardCVV) {
        try {
            makeSureThisIsThePaymentPage();
            fillAmountForThe1stCard(amountToPay);
            AtestWebElement.getInstance().waitUntilClickable("PaymentPage.fill1stCard.card_1_number");
            fillCardNumber(cardNumber, "PaymentPage.fill1stCard.card_1_number");
            fillCardUserName(cardUserName, "PaymentPage.fill1stCard.card_1_user_name");
            fillCardValidity(cardValidity, "PaymentPage.fill1stCard.card_1_expiration");
            fillCardCVV(cardCVV, "PaymentPage.fill1stCard.card_1_cvv");
            if (!chooseNumInstallments(numInstallments, "PaymentPage.fill1stCard.installments_combo_card_1")) { return false; }
            if (hasOps("fill1stCard")) { return false; }
            return true;
        } catch (AtestNoSuchWebElementException | AtestWebDriverException e) {
            _log.oops(e.getMessage());
        }
        return false;
    }
    /**
     *
     * @param amountToPay
     * @return
     */
    private boolean fillAmountForThe1stCard(String amountToPay) {
        AtestWebElement awe = AtestWebElement.getInstance();
        try {
            WebElement amount = awe.findElement("PaymentPage.fillAmountForThe1stCard.card_1_value");
            amount.sendKeys(amountToPay);
            amount.sendKeys(Keys.TAB);
            return true;
        } catch (AtestNoSuchWebElementException | AtestWebDriverException e) {
            _log.oops( e.getMessage());
        }
        return false;
    }
    /**
     * * Fill the credit card form for the second Credit Card.
     * @note Do not finish the order.
     * @param numInstallments The number of installments
     * @param cardUserName The user name on the card.
     * @param cardNumber The card number.
     * @param cardValidity the card expiration date.
     * @param cardCVV The security card number.
     */
    public boolean fill2ndCard(String numInstallments
                              , String cardUserName
                              , String cardNumber
                              , String cardValidity
                              , String cardCVV) {
        try {
            makeSureThisIsThePaymentPage();
            fillCardNumber(cardNumber, "PaymentPage.fill2ndCard.card_2_number");
            fillCardUserName(cardUserName, "PaymentPage.fill2ndCard.card_2_user_name");
            fillCardValidity(cardValidity, "PaymentPage.fill2ndCard.card_2_expiration");
            fillCardCVV(cardCVV, "PaymentPage.fill2ndCard.card_2_cvv");
            if (!chooseNumInstallments(numInstallments, "PaymentPage.fill2ndCard.installments_combo_card_2")) { return false; }
            if (hasOps("fill2ndCard")) { return false; }
            return true;
        } catch (AtestNoSuchWebElementException | AtestWebDriverException e) {
            _log.oops( e.getMessage());
        }
        return false;
    }

    public boolean payWithBoleto() {
        boolean retCode = false;
        try {
            AtestWebElement awe = AtestWebElement.getInstance();
            awe.findElement("PaymentPage.payWithBoleto.submit").click();
            retCode = true;
            awe.waitForPageLoaded();
        } catch (AtestNoSuchWebElementException | AtestWebDriverException e) {
            _log.oops( e.getMessage());
        } catch (AtestPageLoadedException e) {
            _log.info("WARNING: page not loaded after clicking on 'pay with boleto'" + e.getMessage());
        }
        return retCode;
    }

    public boolean chooseAnotherShippingAddress() {
        boolean retCode = false;
        AtestWebElement awe = AtestWebElement.getInstance();
        try {
            WebElement currShippingAddress = awe.findElement("PaymentPage.chooseAnotherShippingAddress.current_shipping_address");
            awe.findElement("PaymentPage.chooseAnotherShippingAddress.ship_to_another_address").click();

            awe.waitUntilVisible("PaymentPage.chooseAnotherShippingAddress.list_of_available_addresses");

            List<WebElement> addresses = awe.findElementsAfterCallingAjax("PaymentPage.chooseAnotherShippingAddress.list_of_available_addresses");
            if (addresses.isEmpty()) throw new AtestNoSuchWebElementException("No list of available shipping address found.");
            String newAddress = "";
            for (WebElement address : addresses) {
                WebElement addressInfo = awe.findElement(address, "PaymentPage.chooseAnotherShippingAddress.available_shipping_address");
                newAddress = addressInfo.getText().trim();
                if (!newAddress.equals(currShippingAddress.getText().trim())) {
                    awe.findElement(address, "PaymentPage.chooseAnotherShippingAddress.button_change_address").click();
                    awe.waitForPageLoaded();
                    retCode = true;
                    awe.waitUntilVisible("PaymentPage.chooseAnotherShippingAddress.current_shipping_address");
                }
            }
            if(retCode) {
                retCode = awe.hasText(newAddress, 20);
                _evidence.screenshot("choose_new_address");
            }
        } catch (AtestNoSuchWebElementException e) {
            _log.oops(e.getMessage());
            _evidence.screenshotOnError("PaymentPage_chooseAnotherShippingAddress");
        } catch (AtestPageLoadedException e) {
            _log.info("WARNING: page not loaded after clicking on 'utilizar este' address: " + e.getMessage());
            _evidence.screenshotOnError("LoginPage_page_not_loaded");
        } catch (AtestWebDriverException e) {
            _log.oops(e.getMessage());
        }
        return retCode;
    }

    public boolean payWithStelo() {
        AtestWebElement awe = AtestWebElement.getInstance();
        try {
            awe.findElement("PaymentPage.payWithStelo.stelo").click();
            Utils.wait(1000);
            _evidence.screenshot("choose_stelo_payment");
            awe.findElement("PaymentPage.payWithStelo.popup_stelo").click();
        } catch (AtestNoSuchWebElementException | AtestWebDriverException  e) {
            _evidence.screenshotOnError("Stelo_payment");
            return false;
        }
        try {
            Utils.wait(1000);
            awe.findElement("PaymentPage.payWithStelo.popup_error");
            _evidence.screenshotOnError("Stelo_payment_communication_error");
            return false;
        } catch (AtestNoSuchWebElementException | AtestWebDriverException  e) {
            ; // no error message was displayed.
        }
        return true;
    }
    public boolean discountBlock() {
    	AtestWebElement awe = AtestWebElement.getInstance();
    	boolean retCode = true;
    	try {
    		awe.findElement("PaymentPage.discountBlock.discount").sendKeys("123");
    		awe.findElement("PaymentPage.discountBlock.BtnDiscount").click();
    		String errorMsg = awe.findElement("PaymentPage.discountBlock.errorDiscountMsg").getText();
    		
    		if(errorMsg.equals("Código inválido ou expirado.")){
    			_log.oops("Código inválido ou expirado.");
    			retCode = false;
    		} 
    		
    	} catch (AtestNoSuchWebElementException | AtestWebDriverException e) {
    		 retCode = false;
            _log.oops(e.getMessage());
            _evidence.screenshotOnError("PaymentPage");
            
        }
    	return retCode;
    }
    
    public boolean validateFinalStep() {
    	AtestWebElement awe = AtestWebElement.getInstance();
    	boolean retCode = true;
    	String errorMsg = "";
    	try {
    		
    		boolean isMsgError = awe.findElement("PaymentPage.discountBlock.finalErrorMsg").isDisplayed();
    		if(isMsgError) {
    			errorMsg = awe.findElement("PaymentPage.discountBlock.finalErrorMsg").getText();
    			_log.oops(errorMsg);
    			retCode = false;
    		}
    		
    	} catch (AtestNoSuchWebElementException | AtestWebDriverException e) {
    		retCode = false;
            _log.oops(e.getMessage());
            _evidence.screenshotOnError("PaymentPage");
        }	
    	return retCode;
    }
}
