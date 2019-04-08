package br.com.softbox.atest_bdd.stepdefs.combined.staticanalysis;
import br.com.softbox.atest_bdd.models.PessoaFisica;
import br.com.softbox.atest_bdd.models.PessoaJuridica;
import br.com.softbox.atest_bdd.pages.CartPage;
import br.com.softbox.atest_bdd.pages.LoginPage;
import br.com.softbox.atest_bdd.pages.MenuUserAccountPage;
import br.com.softbox.atest_bdd.pages.PaymentConfirmationPage;
import br.com.softbox.atest_bdd.pages.PaymentPage;
import br.com.softbox.atest_bdd.pages.ProductPage;
import br.com.softbox.atest_bdd.pages.RegisterPage;
import br.com.softbox.atest_bdd.pages.UserAccountPage;
import br.com.softbox.core.AtestEvidence;
import br.com.softbox.core.AtestLog;
import br.com.softbox.core.AtestPageLoadedException;
import br.com.softbox.core.AtestWebDriverException;
import br.com.softbox.core.AtestWebElement;
import br.com.softbox.core.TestStepData;
import br.com.softbox.utils.Utils;
import cucumber.api.Scenario;

public class CombinedStepsForStaticAnalysisPFTwoPJTwo {
    private AtestEvidence _evidence = AtestEvidence.getInstance();
    private PessoaFisica _pessoa = new PessoaFisica();
    private Scenario _scenario = null;
    private AtestLog _log = new AtestLog("PessoaFisicaSteps");
    private String _initialSite = "";
    private MenuUserAccountPage menuAccount = new MenuUserAccountPage();
    private boolean _checkDataChanging = false;
    private boolean _checkPasswordChanging = false;
    private boolean _checkAddAddress = false;
    private boolean _checkBuyProduct = false;
    private boolean _checkDeleteAddress = false;
    private PessoaJuridica _pj = new PessoaJuridica();
    
    public void tagertPFWithoutEmailForPFTwoPJTwo(String url, String name, String surname, String gender, String phone, String cell, String newPassword, String cep, String productURL) throws Throwable {
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
        
        
        step = new TestStepData(_scenario.getName()
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
        
        
        step = new TestStepData(_scenario.getName()
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
        
        
        step = new TestStepData(_scenario.getName()
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
        
        
        step = new TestStepData(_scenario.getName()
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
        userPage = new UserAccountPage();
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
        
        
        step = new TestStepData(_scenario.getName()
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
        
        
        step = new TestStepData(_scenario.getName()
                , "buy with 'boleto' PF");
        _evidence.startStep(_log, step);
        PaymentPage pay = new PaymentPage();
        Utils.isTrue.go(pay.payWithBoleto(), _log.me(), "buy with boleto PF");

        PaymentConfirmationPage confirmation = new PaymentConfirmationPage();
        Utils.isTrue.go(confirmation.isConfirmationPageForBoleto(), _log.me(), "is not the confirmation page for boleto");
        _evidence.screenshotAtEnd("confirmation_page_boleto");
        _evidence.passStep(step);
        
        
        step = new TestStepData(_scenario.getName()
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
        userPage = new UserAccountPage();
        if (!userPage.removeJustOnePFAddress()) {
            final String error = "Failed add address";
            step.error = error;
            _evidence.failStep(step);
            _log.oops(error);
            return;
        }
        _checkDeleteAddress = true;
        _evidence.passStep(step);
        
        
        step = new TestStepData(_scenario.getName()
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
    
    
    public void tagertPJTwoWithoutEmailForPFTwoPJTwo(String url, String company, String fantasyName, String newPassword, String cep, String productURL) {
        _evidence.startScenario(_log, _scenario.getName());
        TestStepData step = new TestStepData(_scenario.getName()
                , "I am at %URL% <PJ>")
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
        } catch (AtestWebDriverException e) {
            _evidence.failStep(step);
            Utils.isTrue.go(false, _log.me(), "initial page loading: " + e.getMessage());
        }
        
        
        step = new TestStepData(_scenario.getName()
                , "I create a company with 'razao social' as %COMPANY% and 'nome fantasia' as  %FANTASY%")
                .add("COMPANY", company)
                .add("FANTASY", fantasyName);
        _evidence.startStep(_log, step);
        _pj.create(company, fantasyName);

        Utils.isTrue.go(menuAccount.chooseOptionLogin(), _log.me(), "could not find option 'Login' on 'account menu'");
        LoginPage loginPage = new LoginPage();
        loginPage.register(_pj.getEmail());

        RegisterPage registerPage = new RegisterPage();
        registerPage.registerPJ(_pj);
        final String dump = _pj.dumpData();
        _log.info("created a 'pessoa juridica': " + dump);
        step.result("RAZAO SOCIAL", _pj.getRazaoSocial())
        .result("NOME FANTASIA", _pj.getNomeFantasia())
        .result("CNPJ", _pj.getCNPJAsPresentation())
        .result("EMAIL", _pj.getEmail())
        .result("SENHA", _pj.getSenha())
        .result("CEP", _pj.getCEP())
        .result("TELEFONE", _pj.getTelFixoPresentation())
        .result("CELULAR", _pj.getTelCellFull());

        Utils.isTrue.go(menuAccount.isLogged(company), _log.me(), "Verify if [" + company + "] is logged");
        _evidence.passStep(step);
        
        
        step = new TestStepData(_scenario.getName()
                , "change the password to %NEWPASSWORD% and do a logout and login again")
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
        if (!userPage.updatePJPassword(_pj.getSenha(), newPassword)) {
            final String error = "Failed to change password";
            _log.oops(error);
            step.error = error;
            _evidence.failStep(step);
            return;
        }
        _pj.setSenha(newPassword);
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
        if (login.signIn(_pj.getEmail(), _pj.getSenha())) {
            _checkPasswordChanging = menuAccount.isLogged(_pj.getRazaoSocial());
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
        
        step = new TestStepData(_scenario.getName()
                , "add shipping address to CEP %CEP%")
                .add("CEP", cep);
        _evidence.startStep(_log, step);

        final String username = _pj.getRazaoSocial();
        Utils.isTrue.go(menuAccount.isLogged(username), _log.me(), "Verify if [" + username + "] is logged");

        if (!menuAccount.chooseOptionMinhaConta()) {
            final String error = "Unable to select 'Minha Conta' on account menu";
            _log.oops(error);
            step.error = error;
            _evidence.failStep(step);
            return;
        }
        _pj.setCEP(cep);
        userPage = new UserAccountPage();
        if (!userPage.addPJAddress(_pj, _initialSite)) {
            final String error = "Failed add address";
            _log.oops(error);
            step.error = error;
            _evidence.failStep(step);
            Utils.isTrue.go(false, _log.me(), error);
            return;
        }
        _checkAddAddress = true;
        _evidence.passStep(step);
        
        
        step = new TestStepData(_scenario.getName()
                , "put into the cart the product %PRODUCT_URL% choosing another shipping address for PJ")
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
        
        step = new TestStepData(_scenario.getName()
                , "buy with 'boleto' PJ");
        _evidence.startStep(_log, step);

        PaymentPage pay = new PaymentPage();
        Utils.isTrue.go(pay.payWithBoleto(), _log.me(), "buy with boleto PJ");

        PaymentConfirmationPage confirmation = new PaymentConfirmationPage();
        Utils.isTrue.go(confirmation.isConfirmationPageForBoleto(), _log.me(), "is not the confirmation page for boleto");

        _evidence.passStep(step);
        _evidence.screenshotAtEnd("confirmation_page_boleto");
        
        step = new TestStepData(_scenario.getName()
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
        userPage = new UserAccountPage();
        if (!userPage.removeJustOnePJAddress()) {
            final String error = "Failed remove address";
            step.error = error;
            _evidence.failStep(step);
            _log.oops(error);
            return;
        }
        _checkDeleteAddress = true;
        _evidence.passStep(step);
        
        step = new TestStepData(_scenario.getName()
                , "all of the previous steps were done successfully");
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
        _evidence.screenshotAtEnd("Pessoa_juridica_OK");
    }
    
    
    private void updatePFData(String name, String surname, String gender, String phone, String cell) {
        _pessoa.setNome(name);
        _pessoa.setSobrenome(surname);
        _pessoa.setSexo(gender);
        _pessoa.setTelFixo(phone);
        _pessoa.setTelCel(cell);
    }
}
