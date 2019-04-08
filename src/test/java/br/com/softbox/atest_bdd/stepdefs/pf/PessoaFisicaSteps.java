package br.com.softbox.atest_bdd.stepdefs.pf;

import br.com.softbox.atest_bdd.pages.*;
import br.com.softbox.atest_bdd.models.PessoaFisica;
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
import org.openqa.selenium.WebDriver;

public class PessoaFisicaSteps {
    private AtestEvidence _evidence = AtestEvidence.getInstance();
    private Scenario _scenario = null;
    private String _initialSite = "";
    private PessoaFisica _pessoa = new PessoaFisica();
    private MenuUserAccountPage menuAccount = new MenuUserAccountPage();
    private boolean _checkDataChanging = false;
    private boolean _checkPasswordChanging = false;
    private boolean _checkBuyProduct = false;
    private boolean _checkDeleteAddress = false;
    private boolean _checkAddAddress = false;
    private AtestLog _log = new AtestLog("PessoaFisicaSteps");

    @Before
    public void beforePF(Scenario scenario) {
        _scenario = scenario;
    }

    @After
    public void after() {
        if (_evidence.isCurrentScenarioInZombieMode()) {                 // Something goes wrong within this scenario.
            _evidence.finishScenario(_scenario.getName(), "fail");
        }
    }

    @Given("^I am at \"(.*)\" <PF>$")
    public void visitInitialSitePF(String url) throws Throwable {
        _evidence.startScenario(_log, _scenario.getName());
        TestStepData step = new TestStepData(_scenario.getName()
                , "I am at %URL% <PF>")
                .add("URL", url);
        _initialSite = url;
        try {
            _evidence.startStep(_log, step);
            _evidence.getWebDriver().get(url);
            _evidence.setInitialSiteURL(url);
            _evidence.setCurrentHost(url);
            AtestWebElement.getInstance().waitForPageLoaded();
            _evidence.passStep(step);
        } catch (AtestPageLoadedException e) {
            _log.info("WARNING: the initial page was not fully loaded.");
        }
    }

    @When("^I create a user with name \"([^\"]*)\" and surname \"([^\"]*)\"$")
    public void createPF(String name, String surname) {
        TestStepData step = new TestStepData(_scenario.getName()
                , "I create a user with name %NAME% and surname %SURNAME%")
                .add("NAME", name)
                .add("SURNAME", surname);
        _evidence.startStep(_log, step);
        _pessoa.born(name, surname);

        Utils.isTrue.go(menuAccount.chooseOptionLogin(), _log.me(), "could not find option 'Login' on 'account menu'");
        LoginPage loginPage = new LoginPage();
        loginPage.register(_pessoa.getEmail());

        RegisterPage registerPage = new RegisterPage();
        registerPage.registerPF(_pessoa);

        final String dump = _pessoa.dumpData();
        _log.info("created a pessoa fisica: " + dump);

        step.result("NOME", _pessoa.getNome())
            .result("SOBRENOME", _pessoa.getSobreNome())
            .result("SEXO", (_pessoa.getSexo() == PessoaFisica.Sexo.FEMININO) ? "F" : "M")
            .result("NASCIMENTO", _pessoa.getNascDDMMYYYY())
            .result("CPF", _pessoa.getCPFasPresentation())
            .result("EMAIL", _pessoa.getEmail())
            .result("SENHA", _pessoa.getSenha())
            .result("CEP", _pessoa.getCEP())
            .result("TELEFONE", _pessoa.getTelFixoPresentation())
            .result("CELULAR", _pessoa.getTelCellFull());

        Utils.isTrue.go(menuAccount.isLogged(name), _log.me(), "Verify if [" + name + "] is logged");
        _evidence.passStep(step);
    }

    @And("^change my name to \"([^\"]*)\", surname to \"([^\"]*)\", gender to \"([^\"]*)\", phone to \"([^\"]*)\" and cellphone to \"([^\"]*)\"$")
    public void changeDataPF(String name, String surname, String gender, String phone, String cell) {
        TestStepData step = new TestStepData(_scenario.getName()
                , "change my name to  %NAME%, surname %SURNAME%, gender to %GENDER%, phone to %PHONE% and cellphone to %CELL%")
                .add("NAME", name)
                .add("SURNAME", surname)
                .add("GENDER", gender)
                .add("PHONE", phone)
                .add("CELL", cell);
        _evidence.startStep(_log, step);

        _checkDataChanging = menuAccount.chooseOptionMinhaConta();
        if (_checkDataChanging) {
            updatePFData(name, surname, gender, phone, cell);
            UserAccountPage userPage = new UserAccountPage();
            _checkDataChanging = userPage.updatePFData(_pessoa);
            if (_checkDataChanging) {
                _evidence.passStep(step);
            } else {
                step.error = "Updating PF data.";
                _evidence.failStep(step);
            }
        } else {
            step.error = "Unable to select menu 'Minha Conta'";
            _evidence.failStep(step);
        }
    }   
     

    private void updatePFData(String name, String surname, String gender, String phone, String cell) {
        _pessoa.setNome(name);
        _pessoa.setSobrenome(surname);
        _pessoa.setSexo(gender);
        _pessoa.setTelFixo(phone);
        _pessoa.setTelCel(cell);
    }

    @And("^change my password to \"([^\"]*)\" and do a logout and login again$")
    public void changePasswordPF(String newPassword) {
        TestStepData step = new TestStepData(_scenario.getName()
                , "change my password to %NEWPASSWORD% and do a logout and login again")
                .add("NEWPASSWORD", newPassword);
        _evidence.startStep(_log, step);

        if (!menuAccount.chooseOptionMinhaConta()) {
            final String error = "Unable to select 'Minha Conta' on account menu";
            _log.oops(error);
            step.error = error;
            _evidence.failStep(step);
            return;
        }
        UserAccountPage userPage = new UserAccountPage();
        if (!userPage.updatePFPassword(_pessoa.getSenha(), newPassword)) {
            final String error = "Failed to change password";
            _log.oops(error);
            step.error = error;
            _evidence.failStep(step);
            return;
        }
        _pessoa.setSenha(newPassword);
        if (!menuAccount.chooseOptionSair()) {
            final String error = "Failed to try access 'Sair' menu option";
            _log.oops(error);
            step.error = error;
            _evidence.failStep(step);
            return;
        }
        if (!menuAccount.chooseOptionLogin()) {
            final String error = "Failed to try access 'Login' menu option";
            _log.oops(error);
            step.error = error;
            _evidence.failStep(step);
            return;
        }
        LoginPage login = new LoginPage();
        if (login.signIn(_pessoa.getEmail(), _pessoa.getSenha())) {
            _checkPasswordChanging = menuAccount.isLogged(_pessoa.getNome());
            if (_checkPasswordChanging) {
                _evidence.passStep(step);
            } else {
                step.error = "Login did not work with new password";
                _evidence.failStep(step);
            }
        } else {
            step.error = "Could not login";
            _evidence.failStep(step);
        }
    }

    @And("^add shipping address to CEP \"([^\"]*)\"$")
    public void addShippingAddressPF(String cep) {
        TestStepData step = new TestStepData(_scenario.getName()
                , "add shipping address to CEP %CEP%")
                .add("CEP", cep);
        _evidence.startStep(_log, step);

        final String username = _pessoa.getNome();
        Utils.isTrue.go(menuAccount.isLogged(username), _log.me(), "Verify if [" + username + "] is logged");
        if (!menuAccount.chooseOptionMinhaConta()) {
            final String error = "Unable to select 'Minha Conta' on account menu";
            _log.oops(error);
            step.error = error;
            _evidence.failStep(step);
            return;
        }
        _pessoa.setCEP(cep);
        UserAccountPage userPage = new UserAccountPage();
        if (!userPage.addPFAddress(_pessoa, _initialSite)) {
            final String error = "Failed add address";
            _log.oops(error);
            step.error = error;
            _evidence.failStep(step);
            Utils.isTrue.go(false, _log.me(), error);
            return;
        }
        _checkAddAddress = true;
        _evidence.passStep(step);
    }

    @And("^put into the cart the product \"([^\"]*)\" choosing another shipping address for PF$")
    public void putIntoTheCartPF(String productURL) {
        TestStepData step = new TestStepData(_scenario.getName()
                , "put into the cart the product %PRODUCT_URL% choosing another shipping address for PF")
                .add("PRODUCT_URL", productURL);
        _evidence.startStep(_log, step);

        if (productURL.isEmpty()) {
            final String error = "product URL is EMPTY";
            _log.oops(error);
            step.error = error;
            _evidence.failStep(step);
            _log.oops(error);
        } else {
            ProductPage p = new ProductPage();
            String prod = Utils.getURL(_initialSite, productURL);

            Utils.isTrue.go(p.navigateTo(prod), _log.me(), "could not open product URL");
            Utils.isTrue.go(p.buy(), _log.me(), "buy product");

            CartPage cart = new CartPage();
            Utils.isTrue.go(cart.finalizeOrder(), _log.me(), "finalize order");

            PaymentPage pay = new PaymentPage();
            _checkBuyProduct = pay.chooseAnotherShippingAddress();
            if (_checkBuyProduct) {
                _evidence.passStep(step);
            } else {
                step.error = "Could not choose another address";
                _evidence.failStep(step);
            }
        }
    }

    @And("^delete one of my shipping addresses$")
    public void deleteShippingAddressPF() {
        TestStepData step = new TestStepData(_scenario.getName()
                , "delete one of my shipping addresses");
        _evidence.startStep(_log, step);
        try {
            _evidence.getWebDriver().navigate().to(_initialSite);
            AtestWebElement.getInstance().waitForPageLoaded();
        } catch (AtestPageLoadedException e) {
            _log.info("WARNING: initial page not fully loaded.");
        } catch (AtestWebDriverException e) {
            final String error = "could not delete shipping address for PF";
            step.error = error;
            _evidence.failStep(step);
            Utils.isTrue.go(false, _log.me(), error);
        }
        if (!menuAccount.chooseOptionMinhaConta()) {
            final String error = "Unable to select 'Minha Conta' on account menu";
            step.error = error;
            _evidence.failStep(step);
            _log.oops(error);
            return;
        }
        UserAccountPage userPage = new UserAccountPage();
        if (!userPage.removeJustOnePFAddress()) {
            final String error = "Failed add address";
            step.error = error;
            _evidence.failStep(step);
            _log.oops(error);
            return;
        }
        _checkDeleteAddress = true;
        _evidence.passStep(step);
    }

    @Then("^all of my previous steps were done successfully$")
    public void thenPF() {
        TestStepData step = new TestStepData(_scenario.getName()
                , "all of my previous steps were done successfully");
        _evidence.startStep(_log, step);
        step.results.put("CHECK ADDRESS", String.valueOf(_checkAddAddress));
        step.results.put("CHECK BUY PRODUCT", String.valueOf(_checkBuyProduct));
        step.results.put("CHECK DATA CHANGING", String.valueOf(_checkDataChanging));
        step.results.put("CHECK PASSWORD CHANGING", String.valueOf(_checkPasswordChanging));
        step.results.put("CHECK DELETE ADDRESS", String.valueOf(_checkDeleteAddress));

        Utils.isTrue.go(_checkAddAddress
                        && _checkBuyProduct
                        && _checkDataChanging
                        && _checkPasswordChanging
                        && _checkDeleteAddress, _log.me(), "Every steps should be succeeded");
        _evidence.passStep(step);
        _evidence.finishScenario(_scenario.getName(), "passed");
        _evidence.screenshotAtEnd("Pessoa_fisica_OK");
    }

    @When("^I create a user with name \"([^\"]*)\" and surname \"([^\"]*)\" and \"([^\"]*)\"$")
    public void createPFUsingThisEmail(String name, String surname, String email) throws Throwable {
        TestStepData step = new TestStepData(_scenario.getName()
                , "I create a user with name %NAME% and surname %SURNAME% and %EMAIL%")
                .add("NAME", name)
                .add("SURNAME", surname)
                .add("EMAIL", email);
        _evidence.startStep(_log, step);

        _pessoa.born(name, surname, email);

        Utils.isTrue.go(menuAccount.chooseOptionLogin(), _log.me(), "could not find option 'Login' on 'account menu'");
        LoginPage loginPage = new LoginPage();
        loginPage.register(_pessoa.getEmail());

        RegisterPage registerPage = new RegisterPage();
        registerPage.registerPF(_pessoa);
        final String dump = _pessoa.dumpData();
        _log.info("created a pessoa fisica: " + dump);

        step.result("NOME", _pessoa.getNome())
            .result("SOBRENOME", _pessoa.getSobreNome())
            .result("SEXO", (_pessoa.getSexo() == PessoaFisica.Sexo.FEMININO) ? "F" : "M")
            .result("NASCIMENTO", _pessoa.getNascDDMMYYYY())
            .result("CPF", _pessoa.getCPFasPresentation())
            .result("EMAIL", _pessoa.getEmail())
            .result("SENHA", _pessoa.getSenha())
            .result("CEP", _pessoa.getCEP())
            .result("TELEFONE", _pessoa.getTelFixoPresentation())
            .result("CELULAR", _pessoa.getTelCellFull());

        Utils.isTrue.go(menuAccount.isLogged(name), _log.me(), "Verify if [" + name + "] is logged");
        _evidence.passStep(step);
    }

    @And("^buy with boleto PF$")
    public void buyWithBoletoPJ() throws Throwable {
        TestStepData step = new TestStepData(_scenario.getName()
                , "buy with 'boleto' PF");
        _evidence.startStep(_log, step);
        PaymentPage pay = new PaymentPage();
        Utils.isTrue.go(pay.payWithBoleto(), _log.me(), "buy with boleto PF");

        PaymentConfirmationPage confirmation = new PaymentConfirmationPage();
        Utils.isTrue.go(confirmation.isConfirmationPageForBoleto(), _log.me(), "is not the confirmation page for boleto");
        _evidence.screenshotAtEnd("confirmation_page_boleto");
        _evidence.passStep(step);
    }
}
