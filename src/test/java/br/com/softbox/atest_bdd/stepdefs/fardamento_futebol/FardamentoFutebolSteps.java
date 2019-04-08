package br.com.softbox.atest_bdd.stepdefs.fardamento_futebol;

import br.com.softbox.atest_bdd.models.FardamentoFutebol;
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


public class FardamentoFutebolSteps {
    private AtestEvidence _evidence = AtestEvidence.getInstance();
    private ProductPage _product = new ProductPage();
    private Scenario _scenario = null;
    private String _initialSite = "";
    public AtestLog _log = new AtestLog("FardamentoFutebolSteps");
    private FardamentoFutebol _fardamento = new FardamentoFutebol();

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

    @Given("^I am at \"(.*)\"$")
    public void visitFinishOrdersInitialSite(String url) {
        _evidence.startScenario(_log, _scenario.getName());
        TestStepData step = new TestStepData(_scenario.getName()
                , "I am at %URL%")
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

    @And("^set Futsal team name as \"([^\"]*)\"$")
    public void setTeamNameAs(String teamName) {
        TestStepData step = new TestStepData(_scenario.getName()
                , "set Futsal team name as %TEAMNAME%")
                .add("TEAMNAME", teamName);
        _evidence.startStep(_log, step);
        _fardamento.initFutsal(teamName);
        TopMenuPage topMenu = new TopMenuPage();
        Utils.isTrue.go(topMenu.chooseEsporteFutebolFardamento(), _log.me(), "select 'Esportes > Crie seu Uniforme'");
        FardamentoFutebolPage fardamentoPage = new FardamentoFutebolPage(_fardamento);
        Utils.isTrue.go(fardamentoPage.isStartingPage(), _log.me(), "this is not the 'Fardamento Futebol' initial page");

        Utils.isTrue.go(fardamentoPage.configTeam(), _log.me(), "Configuring 'fardamanto' for team [" + teamName + "]");

        CartPage cart = new CartPage();
        Utils.isTrue.go(cart.finalizeOrder(), _log.me(), "finalize order for the 'fardamento'");

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
            step.result("Order number", confirmationNumber.substring(confirmationNumber.indexOf(":") + 1));

            _evidence.passStep(step);
            _evidence.finishScenario(_scenario.getName(), "passed");
            _log.trace("confirmation page for boleto");
            _evidence.screenshotAtEnd("confirmation_page_boleto");
        } else {
            confirmationNumber = confirmation.getOrderNumber();
            Utils.isFalse.go(confirmationNumber.isEmpty(), _log.me(), "could not find order number on confirmation page");
            step.result("Order number", confirmationNumber.substring(confirmationNumber.indexOf(":") + 1));
            _evidence.passStep(step);
            _evidence.finishScenario(_scenario.getName(), "passed");
            _log.info("PaymentPage confirmed.");
            _log.info(confirmationNumber);
            _evidence.screenshotAtEnd("confirmation_page");
        }
    }

}