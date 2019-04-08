package br.com.softbox.atest_bdd.stepdefs.combined;

import br.com.softbox.atest_bdd.models.PessoaFisica;
import br.com.softbox.atest_bdd.models.PessoaJuridica;
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

public class PessoaJuridicaSteps {
    private AtestEvidence _evidence = AtestEvidence.getInstance();
    private Scenario _scenario = null;
    private String _initialSite = "";
    private PessoaJuridica _pj = new PessoaJuridica();
    private MenuUserAccountPage menuAccount = new MenuUserAccountPage();
    private boolean _checkDataChanging = false;
    private boolean _checkPasswordChanging = false;
    private boolean _checkBuyProduct = false;
    private boolean _checkDeleteAddress = false;
    private boolean _checkAddAddress = false;
    private AtestLog _log = new AtestLog("PessoaJuridicaSteps");

    @Before
    public void beforePJ(Scenario scenario) {
        _scenario = scenario;
    }

    @After
    public void after() {
        if (_evidence.isCurrentScenarioInZombieMode()) {                 // Something goes wrong within this scenario.
            _evidence.finishScenario(_scenario.getName(), "fail");
        }
    }

    @Given("^I am at \"(.*)\" <PJ>$")
    public void visitInitialSitePJ(String url) {
        _evidence.startScenario(_log, _scenario.getName());
        TestStepData step = new TestStepData(_scenario.getName()
                , "I am at %URL% <PJ>")
                .add("URL", url);
        _initialSite = url;
        try {
            _evidence.startStep(_log, step);
            //_evidence.getWebDriver().get(url);
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
    }
    
    //injected-both the following 2
    @Given("^I have access to internet$")
    public void i_have_access_to_internet() throws Throwable {   
    }
    
    @Given("^opened \"([^\"]*)\" <PJ>$")
     public void opened_PJ(String url) throws Throwable {
    	 _evidence.startScenario(_log, _scenario.getName());
         TestStepData step = new TestStepData(_scenario.getName()
                 , "I am at %URL% <PJ>")
                 .add("URL", url);
         _initialSite = url;
         try {
             _evidence.startStep(_log, step);
             //_evidence.getWebDriver().get(url);
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
    }

    //injected
    @Given("^I access web address \"([^\"]*)\" <PJ>$")
    public void i_access_web_address_PJ(String url) throws Throwable {
    	_evidence.startScenario(_log, _scenario.getName());
        TestStepData step = new TestStepData(_scenario.getName()
                , "I am at %URL% <PJ>")
                .add("URL", url);
        _initialSite = url;
        try {
            _evidence.startStep(_log, step);
            //_evidence.getWebDriver().get(url);
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
    }

    //injected
    @Given("^I am navigating \"([^\"]*)\" site <PJ>$")
    public void i_am_navigating_site_PJ(String url) throws Throwable {
    	 _evidence.startScenario(_log, _scenario.getName());
         TestStepData step = new TestStepData(_scenario.getName()
                 , "I am at %URL% <PJ>")
                 .add("URL", url);
         _initialSite = url;
         try {
             _evidence.startStep(_log, step);
             //_evidence.getWebDriver().get(url);
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
    }

    @When("^I create a company with 'razao social' as \"([^\"]*)\" and 'nome fantasia' as \"([^\"]*)\"$")
    public void createPJ(String company, String fantasyName) {
        TestStepData step = new TestStepData(_scenario.getName()
                , "I create a company with 'razao social' as %COMPANY% and 'nome fantasia' as  %FANTASY%")
                .add("COMPANY", company)
                .add("FANTASY", fantasyName);
        _evidence.startStep(_log, step);
        _pj.create(company, fantasyName);

        //Utils.isFalse.go(menuAccount.chooseOptionLogin(), _log.me(), "could not find option 'Login' on 'account menu'");
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

        Utils.isFalse.go(menuAccount.isLogged(company), _log.me(), "Verify if [" + company + "] is logged");
        _evidence.passStep(step);
    }
    
    //injected
    @When("^i create a company with the details as 'razao social' as \"([^\"]*)\" and 'nome fantasia' as \"([^\"]*)\"$")
    public void i_create_a_company_with_the_details_as_razao_social_as_and_nome_fantasia_as(String company, String fantasyName) throws Throwable {
        TestStepData step = new TestStepData(_scenario.getName()
                , "I create a company with 'razao social' as %COMPANY% and 'nome fantasia' as  %FANTASY%")
                .add("COMPANY", company)
                .add("FANTASY", fantasyName);
        _evidence.startStep(_log, step);
        _pj.create(company, fantasyName);

        //Utils.isTrue.go(menuAccount.chooseOptionLogin(), _log.me(), "could not find option 'Login' on 'account menu'");
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

        Utils.isFalse.go(menuAccount.isLogged(company), _log.me(), "Verify if [" + company + "] is logged");
        _evidence.passStep(step);
    }
    
    //injected
    @When("^I register a companies with 'razao social' as \"([^\"]*)\" and 'nome fantasia' as \"([^\"]*)\"$")
    public void i_register_a_companies_with_razao_social_as_and_nome_fantasia_as(String company, String fantasyName) throws Throwable {
        TestStepData step = new TestStepData(_scenario.getName()
                , "I create a company with 'razao social' as %COMPANY% and 'nome fantasia' as  %FANTASY%")
                .add("COMPANY", company)
                .add("FANTASY", fantasyName);
        _evidence.startStep(_log, step);
        _pj.create(company, fantasyName);

        //Utils.isTrue.go(menuAccount.chooseOptionLogin(), _log.me(), "could not find option 'Login' on 'account menu'");
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

        Utils.isFalse.go(menuAccount.isLogged(company), _log.me(), "Verify if [" + company + "] is logged");
        _evidence.passStep(step);
    }

    //injected
    @When("^I register companies \"([^\"]*)\" and \"([^\"]*)\" as \"([^\"]*)\" \"([^\"]*)\" respectively$")
    public void i_register_companies_and_as_respectively(String arg1, String arg2, String company, String fantasyName) throws Throwable {
        TestStepData step = new TestStepData(_scenario.getName()
                , "I create a company with 'razao social' as %COMPANY% and 'nome fantasia' as  %FANTASY%")
                .add("COMPANY", company)
                .add("FANTASY", fantasyName);
        _evidence.startStep(_log, step);
        _pj.create(company, fantasyName);

        //Utils.isTrue.go(menuAccount.chooseOptionLogin(), _log.me(), "could not find option 'Login' on 'account menu'");
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

        Utils.isFalse.go(menuAccount.isLogged(company), _log.me(), "Verify if [" + company + "] is logged");
        _evidence.passStep(step);

    }

    @And("^change 'razao social' to \"([^\"]*)\", 'nome fantasia' as \"([^\"]*)\", phone to \"([^\"]*)\" and cellphone to \"([^\"]*)\"$")
    public void changeDataPJ(String company, String fantasyName, String phone, String cellphone) {
        TestStepData step = new TestStepData(_scenario.getName()
                , "change 'razao social' to %RAZAO_SOCIAL%, 'nome fantasia' as %FANTASY%, phone to %PHONE% and cellphone to %CELL%")
                .add("RAZAO_SOCIAL", company)
                .add("FANTASY", fantasyName)
                .add("PHONE", phone)
                .add("CELL", cellphone);
        _evidence.startStep(_log, step);
        _checkDataChanging = menuAccount.chooseOptionMinhaConta();
        if (_checkDataChanging) {
            updateData(company, fantasyName, phone, cellphone);
            UserAccountPage userPage = new UserAccountPage();
            _checkDataChanging = userPage.updatePJData(_pj);
            if (_checkDataChanging) {
                _evidence.passStep(step);
            } else {
                step.error = "Updating PJ data.";
                _evidence.failStep(step);
            }
        } else {
            step.error = "Unable to select menu 'Minha Conta'";
            _evidence.failStep(step);
        }
    }
    
    //injected
    @When("^i update'razao social' to \"([^\"]*)\" and 'nome fantasia' as \"([^\"]*)\", phone to \"([^\"]*)\" and cellphone to \"([^\"]*)\"$")
    public void i_update_razao_social_to_and_nome_fantasia_as_phone_to_and_cellphone_to(String company, String fantasyName, String phone, String cellphone) throws Throwable {
        TestStepData step = new TestStepData(_scenario.getName()
                , "change 'razao social' to %RAZAO_SOCIAL%, 'nome fantasia' as %FANTASY%, phone to %PHONE% and cellphone to %CELL%")
                .add("RAZAO_SOCIAL", company)
                .add("FANTASY", fantasyName)
                .add("PHONE", phone)
                .add("CELL", cellphone);
        _evidence.startStep(_log, step);
        _checkDataChanging = menuAccount.chooseOptionMinhaConta();
        if (_checkDataChanging) {
            updateData(company, fantasyName, phone, cellphone);
            UserAccountPage userPage = new UserAccountPage();
            _checkDataChanging = userPage.updatePJData(_pj);
            if (_checkDataChanging) {
                _evidence.passStep(step);
            } else {
                step.error = "Updating PJ data.";
                _evidence.failStep(step);
            }
        } else {
            step.error = "Unable to select menu 'Minha Conta'";
            _evidence.failStep(step);
        }
     }
    
    //injected
    @When("^change 'razao social' to \"([^\"]*)\" and 'nome fantasia'  to \"([^\"]*)\", phone to phone to \"([^\"]*)\" and cellphone to \"([^\"]*)\"$")
    public void change_razao_social_to_and_nome_fantasia_to_phone_to_phone_to_and_cellphone_to(String company, String fantasyName, String phone, String cellphone) throws Throwable {
        TestStepData step = new TestStepData(_scenario.getName()
                , "change 'razao social' to %RAZAO_SOCIAL%, 'nome fantasia' as %FANTASY%, phone to %PHONE% and cellphone to %CELL%")
                .add("RAZAO_SOCIAL", company)
                .add("FANTASY", fantasyName)
                .add("PHONE", phone)
                .add("CELL", cellphone);
        _evidence.startStep(_log, step);
        _checkDataChanging = menuAccount.chooseOptionMinhaConta();
        if (_checkDataChanging) {
            updateData(company, fantasyName, phone, cellphone);
            UserAccountPage userPage = new UserAccountPage();
            _checkDataChanging = userPage.updatePJData(_pj);
            if (_checkDataChanging) {
                _evidence.passStep(step);
            } else {
                step.error = "Updating PJ data.";
                _evidence.failStep(step);
            }
        } else {
            step.error = "Unable to select menu 'Minha Conta'";
            _evidence.failStep(step);
        }
     }

    //injected
    @When("^modify details for \"([^\"]*)\" to \"([^\"]*)\", \"([^\"]*)\" modified to \"([^\"]*)\", new contact phone as \"([^\"]*)\", and \"([^\"]*)\" as a mobile number$")
    public void modify_details_for_to_modified_to_new_contact_phone_as_and_as_a_mobile_number(String arg1, String company, String arg3, String fantasyName, String phone, String cellphone) throws Throwable {
        TestStepData step = new TestStepData(_scenario.getName()
                , "change 'razao social' to %RAZAO_SOCIAL%, 'nome fantasia' as %FANTASY%, phone to %PHONE% and cellphone to %CELL%")
                .add("RAZAO_SOCIAL", company)
                .add("FANTASY", fantasyName)
                .add("PHONE", phone)
                .add("CELL", cellphone);
        _evidence.startStep(_log, step);
        _checkDataChanging = menuAccount.chooseOptionMinhaConta();
        if (_checkDataChanging) {
            updateData(company, fantasyName, phone, cellphone);
            UserAccountPage userPage = new UserAccountPage();
            _checkDataChanging = userPage.updatePJData(_pj);
            if (_checkDataChanging) {
                _evidence.passStep(step);
            } else {
                step.error = "Updating PJ data.";
                _evidence.failStep(step);
            }
        } else {
            step.error = "Unable to select menu 'Minha Conta'";
            _evidence.failStep(step);
        } 
    }

    private void updateData(String company, String fantasyName, String phone, String cellphone) {
        _pj.setRazaoSocial(company);
        _pj.setNomeFantasia(fantasyName);
        _pj.setTelFixo(phone);
        _pj.setTelCel(cellphone);
    }

    @And("^change the company password to \"([^\"]*)\" and do a logout and login again$")
    public void changePasswordPJ(String newPassword) {
        TestStepData step = new TestStepData(_scenario.getName()
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

    }
    
    //injected
    @When("^reset change the company password to \"([^\"]*)\"$")
    public void reset_change_the_company_password_to(String newPassword) throws Throwable {
        TestStepData step = new TestStepData(_scenario.getName()
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
     }

    //injected
    @When("^have the company passcode changed to \"([^\"]*)\" and sign out and re-sign in\\.$")
    public void have_the_company_passcode_changed_to_and_sign_out_and_re_sign_in(String newPassword) throws Throwable {
        TestStepData step = new TestStepData(_scenario.getName()
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

    }

    @And("^add this CEP \"([^\"]*)\" for shipping$")
    public void addShippingAddressPJ(String cep) {
        TestStepData step = new TestStepData(_scenario.getName()
                , "add shipping address to CEP %CEP%")
                .add("CEP", cep);
        _evidence.startStep(_log, step);

        final String username = _pj.getRazaoSocial();
        Utils.isFalse.go(menuAccount.isLogged(username), _log.me(), "Verify if [" + username + "] is logged");

        if (!menuAccount.chooseOptionMinhaConta()) {
            final String error = "Unable to select 'Minha Conta' on account menu";
            _log.oops(error);
            step.error = error;
            _evidence.failStep(step);
            return;
        }
        _pj.setCEP(cep);
        UserAccountPage userPage = new UserAccountPage();
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
    }
    
    //injected
    @Then("^add missing shipping address as   CEP \"([^\"]*)\"$")
    public void add_missing_shipping_address_as_CEP(String cep) throws Throwable {
        TestStepData step = new TestStepData(_scenario.getName()
                , "add shipping address to CEP %CEP%")
                .add("CEP", cep);
        _evidence.startStep(_log, step);

        final String username = _pj.getRazaoSocial();
        Utils.isFalse.go(menuAccount.isLogged(username), _log.me(), "Verify if [" + username + "] is logged");

        if (!menuAccount.chooseOptionMinhaConta()) {
            final String error = "Unable to select 'Minha Conta' on account menu";
            _log.oops(error);
            step.error = error;
            _evidence.failStep(step);
            return;
        }
        _pj.setCEP(cep);
        UserAccountPage userPage = new UserAccountPage();
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
     }

    //injected
    @When("^provide shipping details as \"([^\"]*)\"$")
    public void provide_shipping_details_as(String cep) throws Throwable {
        TestStepData step = new TestStepData(_scenario.getName()
                , "add shipping address to CEP %CEP%")
                .add("CEP", cep);
        _evidence.startStep(_log, step);

        final String username = _pj.getRazaoSocial();
        Utils.isFalse.go(menuAccount.isLogged(username), _log.me(), "Verify if [" + username + "] is logged");

        if (!menuAccount.chooseOptionMinhaConta()) {
            final String error = "Unable to select 'Minha Conta' on account menu";
            _log.oops(error);
            step.error = error;
            _evidence.failStep(step);
            return;
        }
        _pj.setCEP(cep);
        UserAccountPage userPage = new UserAccountPage();
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
    }

    @And("^put into the cart the product \"([^\"]*)\" choosing another shipping address for PJ$")
    public void buyPJ(String productURL) {
        TestStepData step = new TestStepData(_scenario.getName()
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
            Utils.isFalse.go(cart.finalizeOrder(), _log.me(), "finalize order");

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
    
    //injected
    @Then("^add to the cart a product with SKU \"([^\"]*)\" and chose another address to ship for PJ$")
    public void add_to_the_cart_a_product_with_SKU_and_chose_another_address_to_ship_for_PJ(String productURL) throws Throwable {
        TestStepData step = new TestStepData(_scenario.getName()
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
            Utils.isFalse.go(cart.finalizeOrder(), _log.me(), "finalize order");

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

    //injected
    @When("^add item \"([^\"]*)\" into the cart to be shipped for PJ to a different address\\.$")
    public void add_item_into_the_cart_to_be_shipped_for_PJ_to_a_different_address(String productURL) throws Throwable {
        TestStepData step = new TestStepData(_scenario.getName()
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
            Utils.isFalse.go(cart.finalizeOrder(), _log.me(), "finalize order");

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

    @And("^delete one of the shipping addresses$")
    public void deleteShippingAddressPJ() {
        TestStepData step = new TestStepData(_scenario.getName()
                , "delete one of my shipping addresses");
        _evidence.startStep(_log, step);
        try {
           // _evidence.getWebDriver().navigate().to(_initialSite);
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
        if (!userPage.removeJustOnePJAddress()) {
            final String error = "Failed remove address";
            step.error = error;
            _evidence.failStep(step);
            _log.oops(error);
            return;
        }
        _checkDeleteAddress = true;
        _evidence.passStep(step);
    }

    //injected
    @When("^have one of the shipping addresses removed$")
    public void have_one_of_the_shipping_addresses_removed() throws Throwable {
        TestStepData step = new TestStepData(_scenario.getName()
                , "delete one of my shipping addresses");
        _evidence.startStep(_log, step);
        try {
            //_evidence.getWebDriver().navigate().to(_initialSite);
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
        if (!userPage.removeJustOnePJAddress()) {
            final String error = "Failed remove address";
            step.error = error;
            _evidence.failStep(step);
            _log.oops(error);
            return;
        }
        _checkDeleteAddress = true;
        _evidence.passStep(step);
    }

    @Then("^all of the previous steps were done successfully$")
    public void thenPJ() {
        TestStepData step = new TestStepData(_scenario.getName()
                , "all of the previous steps were done successfully");
        _evidence.startStep(_log, step);
        step.results.put("CHECK ADDRESS", String.valueOf(_checkAddAddress));
        step.results.put("CHECK BUY PRODUCT", String.valueOf(_checkBuyProduct));
        step.results.put("CHECK DATA CHANGING", String.valueOf(_checkDataChanging));
        step.results.put("CHECK PASSWORD CHANGING", String.valueOf(_checkPasswordChanging));
        step.results.put("CHECK DELETE ADDRESS", String.valueOf(_checkDeleteAddress));

        Utils.isFalse.go(_checkAddAddress
                && _checkBuyProduct
                && _checkDataChanging
                && _checkPasswordChanging
                && _checkDeleteAddress, _log.me(), "Every steps should be succeeded");
        _evidence.passStep(step);
        _evidence.finishScenario(_scenario.getName(), "passed");
        _evidence.screenshotAtEnd("Pessoa_juridica_OK");
    }

    //injected
    @Then("^the previous actions were all successfully finalized$")
    public void the_previous_actions_were_all_successfully_finalized() throws Throwable {
        TestStepData step = new TestStepData(_scenario.getName()
                , "all of the previous steps were done successfully");
        _evidence.startStep(_log, step);
        step.results.put("CHECK ADDRESS", String.valueOf(_checkAddAddress));
        step.results.put("CHECK BUY PRODUCT", String.valueOf(_checkBuyProduct));
        step.results.put("CHECK DATA CHANGING", String.valueOf(_checkDataChanging));
        step.results.put("CHECK PASSWORD CHANGING", String.valueOf(_checkPasswordChanging));
        step.results.put("CHECK DELETE ADDRESS", String.valueOf(_checkDeleteAddress));

        Utils.isFalse.go(_checkAddAddress
                && _checkBuyProduct
                && _checkDataChanging
                && _checkPasswordChanging
                && _checkDeleteAddress, _log.me(), "Every steps should be succeeded");
        _evidence.passStep(step);
        _evidence.finishScenario(_scenario.getName(), "passed");
        _evidence.screenshotAtEnd("Pessoa_juridica_OK");

    }

    @And("^buy with boleto PJ$")
    public void buyWithBoletoPJ() throws Throwable {
        TestStepData step = new TestStepData(_scenario.getName()
                , "buy with 'boleto' PJ");
        _evidence.startStep(_log, step);

        PaymentPage pay = new PaymentPage();
        Utils.isFalse.go(pay.payWithBoleto(), _log.me(), "buy with boleto PJ");

        PaymentConfirmationPage confirmation = new PaymentConfirmationPage();
        //Utils.isFalse.go(confirmation.isConfirmationPageForBoleto(), _log.me(), "is not the confirmation page for boleto");

        _evidence.passStep(step);
        _evidence.screenshotAtEnd("confirmation_page_boleto");
    }
    
    //injected
    @Then("^purchase with boleto PJ$")
    public void purchase_with_boleto_PJ() throws Throwable {
        TestStepData step = new TestStepData(_scenario.getName()
                , "buy with 'boleto' PJ");
        _evidence.startStep(_log, step);

        PaymentPage pay = new PaymentPage();
        Utils.isFalse.go(pay.payWithBoleto(), _log.me(), "buy with boleto PJ");

        PaymentConfirmationPage confirmation = new PaymentConfirmationPage();
        //Utils.isFalse.go(confirmation.isConfirmationPageForBoleto(), _log.me(), "is not the confirmation page for boleto");

        _evidence.passStep(step);
        _evidence.screenshotAtEnd("confirmation_page_boleto");
     }

    //injected
    @When("^use boleto PJ to make a purchase$")
    public void use_boleto_PJ_to_make_a_purchase() throws Throwable {
        TestStepData step = new TestStepData(_scenario.getName()
                , "buy with 'boleto' PJ");
        _evidence.startStep(_log, step);

        PaymentPage pay = new PaymentPage();
        Utils.isFalse.go(pay.payWithBoleto(), _log.me(), "buy with boleto PJ");

        PaymentConfirmationPage confirmation = new PaymentConfirmationPage();
        //Utils.isFalse.go(confirmation.isConfirmationPageForBoleto(), _log.me(), "is not the confirmation page for boleto");

        _evidence.passStep(step);
        _evidence.screenshotAtEnd("confirmation_page_boleto");
    }

    @When("^I create a company with 'razao social' as \"([^\"]*)\" and 'nome fantasia' as \"([^\"]*)\" and \"([^\"]*)\"$")
    public void createPJUsingThisEmail(String company, String fantasyName, String email) throws Throwable {
        TestStepData step = new TestStepData(_scenario.getName()
                , "I create a company with 'razao social' as %COMPANY% and 'nome fantasia' as  %FANTASY% and %EMAIL%")
                .add("COMPANY", company)
                .add("FANTASY", fantasyName)
                .add("EMAIL", email);
        _evidence.startStep(_log, step);
        _pj.create(company, fantasyName, email);

        Utils.isTrue.go(menuAccount.chooseOptionLogin(), _log.me(), "could not find option 'Login' on 'account menu'");
        LoginPage loginPage = new LoginPage();
        loginPage.register(_pj.getEmail());

        RegisterPage registerPage = new RegisterPage();
        registerPage.registerPJ(_pj);
        final String dump = _pj.dumpData();

        step.result("RAZAO SOCIAL", _pj.getRazaoSocial())
        .result("NOME FANTASIA", _pj.getNomeFantasia())
        .result("CNPJ", _pj.getCNPJAsPresentation())
        .result("EMAIL", _pj.getEmail())
        .result("SENHA", _pj.getSenha())
        .result("CEP", _pj.getCEP())
        .result("TELEFONE", _pj.getTelFixoPresentation())
        .result("CELULAR", _pj.getTelCellFull());

        _log.info("created a 'pessoa juridica': " + dump);
        Utils.isTrue.go(menuAccount.isLogged(company), _log.me(), "Verify if [" + company + "] is logged");
        _evidence.passStep(step);
    }
}
