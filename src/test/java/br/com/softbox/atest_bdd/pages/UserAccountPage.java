package br.com.softbox.atest_bdd.pages;

import br.com.softbox.atest_bdd.models.PessoaFisica;
import br.com.softbox.atest_bdd.models.PessoaJuridica;
import br.com.softbox.core.*;
import br.com.softbox.utils.Utils;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 *
 */
public class UserAccountPage {
    private AtestEvidence _evidence = AtestEvidence.getInstance();
    private AtestWebElement _awe = AtestWebElement.getInstance();
    private AtestLog _log = new AtestLog("UserAccountPage");

    public UserAccountPage() {
    }

    public boolean updatePFData(PessoaFisica pf) {
        boolean retCode = false;
        try {
            _awe.findElement("UserAccountPage.link_page_dados_cadastrais").click();
            WebElement name = _awe.findElement("UserAccountPage.updatePFData.name");
            name.clear();
            name.sendKeys(pf.getNome());

            if (pf.isFemale()) {
                _awe.findElement("UserAccountPage.updatePFData.female").click();
            } else {
                _awe.findElement("UserAccountPage.updatePFData.male").click();
            }

            WebElement lastName = _awe.findElement("UserAccountPage.updatePFData.lastName");
            lastName.clear();
            lastName.sendKeys(pf.getSobreNome());
            WebElement phoneDDD = _awe.findElement("UserAccountPage.updatePFData.phoneDDD");
            phoneDDD.clear();
            phoneDDD.sendKeys(pf.getTelFixoDDD());
            WebElement phone = _awe.findElement("UserAccountPage.updatePFData.phone");
            phone.clear();
            phone.sendKeys(pf.getTellFixo());
            WebElement cellDDD = _awe.findElement("UserAccountPage.updatePFData.cellDDD");
            cellDDD.clear();
            cellDDD.sendKeys(pf.getTelCellDD());
            WebElement cell = _awe.findElement("UserAccountPage.updatePFData.cell");
            cell.clear();
            cell.sendKeys(pf.getTelCel());
            _evidence.screenshot("pessoa_fisica_update_data_filled_form");
            _awe.findElement("UserAccountPage.updatePFData.btn_alterar_cadastro").click();
            retCode = hasSuccessFeedback("pessoa_fisica_update_data");
        } catch (AtestNoSuchWebElementException e) {
            _log.oops(e.getMessage());
            _evidence.screenshotOnError("UserAccountPage_updatePFData");
        } catch (AtestWebDriverException e) {
            _log.oops(e.getMessage());
        }
        return retCode;
    }

    private boolean hasSuccessFeedback(String title) throws AtestWebDriverException {
        boolean retCode = false;
        try {
            _awe.findElement("UserAccountPage.success_feedback");
            retCode = true;
            _evidence.screenshot(title + "_success");
        } catch (AtestNoSuchWebElementException e) {
            _log.oops(e.getMessage());
            _evidence.screenshotOnError("UserAccountPage_hasSuccessFeedback_" + title);
        }
        return retCode;
    }

    public boolean updatePFPassword(String oldPassword, String newPassword) {
        boolean retCode = false;
        AtestWebElement awe = AtestWebElement.getInstance();
        try {
            awe.findElement("UserAccountPage.link_alterar_senha_e_email").click();
            awe.findElement("UserAccountPage.updatePFPassword.oldPassword").sendKeys(oldPassword);
            awe.findElement("UserAccountPage.updatePFPassword.newPassword").sendKeys(newPassword);
            awe.findElement("UserAccountPage.updatePFPassword.confirmPassword").sendKeys(newPassword);
            _evidence.screenshot("pessoa_fisica_change_password_filled_form");
            awe.findElement("UserAccountPage.updatePFPassword.btn_alterar_senha").click();
            retCode = hasSuccessFeedback("pessoa_fisica_update_password");
        } catch (AtestNoSuchWebElementException e) {
            _evidence.screenshotOnError("UserAccountPage_updatePFPassword");
            _log.oops(e.getMessage());
        } catch (AtestWebDriverException e) {
            _log.oops(e.getMessage());
        }
        return retCode;
    }
    public Boolean getTooltipMsg() {
    	String  tooltipMsg = null;
    	Boolean rc = false;
    	try {
    		tooltipMsg = _awe.findElement("ops").getText();
	        if (tooltipMsg != null) {
	        	throw new Exception("Invalid CEP.");
			} 
	        
    	} catch (AtestNoSuchWebElementException e) {
    		rc = true;
	        _log.trace("No Ops message is present on page, it seems everything is ok");
	    } catch (Exception e) {
	        _log.oops(e.getMessage());
	    }
    	return rc;
    }
    
    public boolean addPFAddress(PessoaFisica pf, String initialSite) {
        boolean retCode = false;
        boolean returnCep = false;
        try {
            AtestWebElement awe = AtestWebElement.getInstance();
            awe.findElement("UserAccountPage.link_page_enderecos").click();
            awe.findElement("UserAccountPage.addPFAddress.btn_adicionar_endereco").click();
            final String newAddressName = "Morada randomica #" + String.valueOf(System.currentTimeMillis());
            awe.findElement("UserAccountPage.addPFAddress.address_name").sendKeys(newAddressName);
            awe.findElement("UserAccountPage.addPFAddress.name").sendKeys("Fulaninho");
            awe.findElement("UserAccountPage.addPFAddress.surname").sendKeys("Sem Sobrenome");
            awe.findElement("UserAccountPage.addPFAddress.cep1").sendKeys(pf.getCEPCampo1());
            WebElement cep2 = awe.findElement("UserAccountPage.addPFAddress.cep2");
            cep2.sendKeys(pf.getCEPCampo2());
            cep2.sendKeys(Keys.TAB);
            Utils.wait(2000);
            awe.findElement("UserAccountPage.addPFAddress.number").sendKeys(pf.getCEPCampo2());
            returnCep = this.getTooltipMsg();
            _evidence.screenshot("pessoa_fisica_new_address");
            awe.findElement("UserAccountPage.addPFAddress.btn_salvar_endereco").click();
            try {
                awe.waitForPageLoaded();
            } catch (AtestPageLoadedException e) {
                _log.info("WARNING: page not loaded after clicking on 'save address': " + e.getMessage());
                _evidence.screenshotOnError("UserAccountPage_page_not_loaded_addPFAddress");
            }
            retCode = awe.hasText(newAddressName) && returnCep;
        } catch (AtestNoSuchWebElementException e) {
            _evidence.screenshotOnError("UserAccountPage_addPFAddress");
            _log.oops(e.getMessage());
        } catch (AtestWebDriverException e) {
            _log.oops(e.getMessage());
        }
        return retCode;
    }

    public boolean removeJustOnePFAddress() {
        AtestWebElement awe = AtestWebElement.getInstance();
        try {
            awe.findElement("UserAccountPage.link_page_enderecos").click();
            awe.findElement("UserAccountPage.remove_address").click();
            try {
                awe.waitForPageLoaded();
                _evidence.screenshot("deleting_address");
            } catch (AtestPageLoadedException e) {
                _log.info("WARNING: page not loaded after clicking on 'remove address': " + e.getMessage());
                _evidence.screenshotOnError("UserAccountPage_page_not_loaded_removeJustOnePFAddress");
            }
            return true;
        } catch (AtestNoSuchWebElementException e) {
            _log.oops(e.getMessage());
            _evidence.screenshotOnError("UserAccountPage_removeJustOnePFAddress");
        } catch (AtestWebDriverException e) {
            _log.oops(e.getMessage());
        }
        return false;
    }

    public boolean updatePJPassword(String oldPassword, String newPassword) {
        boolean retCode = false;
        AtestWebElement awe = AtestWebElement.getInstance();
        try {
            awe.findElement("UserAccountPage.link_alterar_senha_e_email").click();
            awe.findElement("UserAccountPage.updatePFPassword.oldPassword").sendKeys(oldPassword);
            awe.findElement("UserAccountPage.updatePFPassword.newPassword").sendKeys(newPassword);
            awe.findElement("UserAccountPage.updatePFPassword.confirmPassword").sendKeys(newPassword);
            _evidence.screenshot("pessoa_fisica_change_password_filled_form");
            awe.findElement("UserAccountPage.updatePFPassword.btn_alterar_senha").click();
            retCode = hasSuccessFeedback("pessoa_juridica_update_password");
        } catch (AtestNoSuchWebElementException e) {
            _log.oops(e.getMessage());
            _evidence.screenshotOnError("UserAccountPage_updatePJPassword");
        } catch (AtestWebDriverException e) {
            _log.oops(e.getMessage());
        }
        return retCode;
    }

    public boolean updatePJData(PessoaJuridica pj) {
        boolean retCode = false;
        try {
            _awe.findElement("UserAccountPage.link_page_dados_cadastrais").click();
            WebElement name = _awe.findElement("UserAccountPage.updatePJData.company_name");
            name.clear();
            name.sendKeys(pj.getRazaoSocial());
            WebElement lastName = _awe.findElement("UserAccountPage.updatePJData.fantasy_name");
            lastName.clear();
            lastName.sendKeys(pj.getNomeFantasia());
            WebElement phoneDDD = _awe.findElement("UserAccountPage.updatePJData.phoneDDD");
            phoneDDD.clear();
            phoneDDD.sendKeys(pj.getTelFixoDDD());
            WebElement phone = _awe.findElement("UserAccountPage.updatePJData.phone");
            phone.clear();
            phone.sendKeys(pj.getTellFixo());
            WebElement cellDDD = _awe.findElement("UserAccountPage.updatePJData.cellDDD");
            cellDDD.clear();
            cellDDD.sendKeys(pj.getTelCellDD());
            WebElement cell = _awe.findElement("UserAccountPage.updatePJData.cell");
            cell.clear();
            cell.sendKeys(pj.getTelCel());
            _evidence.screenshot("pessoa_juridica_update_data_filled_form");
            _awe.findElement("UserAccountPage.updatePJData.btn_alterar_cadastro").click();
            retCode = hasSuccessFeedback("pessoa_juridica_update_data");
        } catch (AtestNoSuchWebElementException e) {
            _evidence.screenshotOnError("UserAccountPage_updatePJData");
            _log.oops(e.getMessage());
        } catch (AtestWebDriverException e) {
            _log.oops(e.getMessage());
        }
        return retCode;
    }

    public boolean addPJAddress(PessoaJuridica pj, String initialSite) {
        boolean retCode = false;
        boolean returnCep = false;
        AtestWebElement awe = AtestWebElement.getInstance();
        try {
            awe.findElement("UserAccountPage.link_page_enderecos").click();
            awe.findElement("UserAccountPage.addPJAddress.btn_adicionar_endereco").click();
            final String newAddressName = "Filial randomica #" + String.valueOf(System.currentTimeMillis());
            awe.findElement("UserAccountPage.addPJAddress.address_name").sendKeys(newAddressName);
            awe.findElement("UserAccountPage.addPJAddress.company_name").sendKeys("Seu Creyson Silva");
            awe.findElement("UserAccountPage.addPJAddress.fantasy_name").sendKeys("Fadiga Vidgal");
            awe.findElement("UserAccountPage.addPJAddress.cep1").sendKeys(pj.getCEPCampo1());
            WebElement cep2 = awe.findElement("UserAccountPage.addPJAddress.cep2");
            cep2.sendKeys(pj.getCEPCampo2());
            cep2.sendKeys(Keys.TAB);
            Utils.wait(2000);
            awe.findElement("UserAccountPage.addPJAddress.number").sendKeys(pj.getCEPCampo2());
            returnCep = this.getTooltipMsg();
            _evidence.screenshot("pessoa_juridica_new_address");
            awe.findElement("UserAccountPage.addPJAddress.btn_salvar_endereco").click();
            try {
                awe.waitForPageLoaded();
            } catch (AtestPageLoadedException e) {
                _log.info("WARNING: page not loaded after clicking on 'save address': " + e.getMessage());
                _evidence.screenshotOnError("UserAccountPage_page_not_loaded_addPJAddress");
            }
            retCode = awe.hasText(newAddressName) && returnCep;
        } catch (AtestNoSuchWebElementException e) {
            _log.oops(e.getMessage());
            _evidence.screenshotOnError("UserAccountPage_addPJAddress");
        } catch (AtestWebDriverException e) {
            _log.oops(e.getMessage());
        }
        return retCode;
    }

    public boolean removeJustOnePJAddress() {
        try {
            AtestWebElement awe = AtestWebElement.getInstance();
            awe.findElement("UserAccountPage.link_page_enderecos").click();
            awe.findElement("UserAccountPage.remove_address").click();
            try {
                awe.waitForPageLoaded();
            } catch (AtestPageLoadedException e) {
                _log.info("WARNING: page not loaded after clicking on 'remove PJ address': " + e.getMessage());
            }
            return true;
        } catch (AtestNoSuchWebElementException e) {
            _log.oops(e.getMessage());
        } catch (AtestWebDriverException e) {
            _log.oops(e.getMessage());
        }
        return false;
    }

    public boolean socialNetworkValidation(String _socialNetwork) {
        boolean retCode = false;

        try {
            WebElement socialNetwork = AtestWebElement.getInstance().findElement("SocialNetwork.assertAccess");
            socialNetwork.getText().contains(_socialNetwork);
            _evidence.screenshot("Logged_with_SocialNetwork");

           retCode = true;
        } catch (AtestNoSuchWebElementException e) {
            _log.oops("User not logged with social network" + e.getMessage());
            _evidence.screenshotOnError("Logged_with_SocialNetwork");
        } catch (AtestWebDriverException e) {
            _log.oops(e.getMessage());
        }
        return retCode;

    }



    public boolean socialNetworkDisconnect() {

        boolean retCode = false;

        AtestWebElement awe = AtestWebElement.getInstance();


        try {
            List<WebElement> currentSocialNetwork = awe.findElements("SocialNetwork.removeAccess");
            int num = currentSocialNetwork.size();


            for (WebElement check : currentSocialNetwork) {


                if (num > 0) {

                    check.click();

                } else {

                    break;
                }
            }

            _evidence.screenshot("Disconnected_from_SocialNetwork");
            retCode = true;

        } catch (AtestNoSuchWebElementException e) {
            _log.info("Unable to disconnect from social networks, maybe the user wasn't associated to none of them." + e.getMessage());
            _evidence.screenshotOnError("Disconnected_from_SocialNetwork");
        } catch (AtestWebDriverException e) {
            _log.oops(e.getMessage());
        }

        return retCode;
    }

    public boolean removeSocialAccounts() {
        try {
            List<WebElement> accounts = AtestWebElement.getInstance().findElements("UserAccountPage.removeSocialAccounts.accounts");
            int numAccounts = accounts.size();
            while (numAccounts > 0) {
                accounts.get(0).click();
                Utils.wait(2000);
                accounts = AtestWebElement.getInstance().findElements("UserAccountPage.removeSocialAccounts.accounts");
                numAccounts = accounts.size();
            }
            _evidence.screenshot("UserAccountPage_removeSocialAccounts");
            return true;
        } catch (AtestNoSuchWebElementException e) {
            _evidence.screenshotOnError("UserAccountPage_removeSocialAccounts_no_account");
            return true;  // maybe there is no social account
        } catch (AtestWebDriverException e) {
            ;
        }
        return false;
    }


    public boolean checkSocialExists(String socialType) {
        try {
            List<WebElement> accounts = AtestWebElement.getInstance().findElements("UserAccountPage.checkSocialExists.names");
            for (WebElement account : accounts) {
                if (account.getText().trim().toLowerCase().contains(socialType)) {
                    _evidence.screenshot("UserAccountPage_checkSocialExists_" + socialType);
                    return true;
                }
            }
        } catch (AtestNoSuchWebElementException | AtestWebDriverException e) {
            ;
        }
        _evidence.screenshotOnError("UserAccountPage_checkSocialExists_" + socialType);
        return false;
    }
}
