package br.com.softbox.atest_bdd.pages;

import java.util.List;

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import br.com.softbox.atest_bdd.models.PessoaFisica;
import br.com.softbox.atest_bdd.models.PessoaJuridica;
import br.com.softbox.core.AtestEvidence;
import br.com.softbox.core.AtestLog;
import br.com.softbox.core.AtestNoSuchWebElementException;
import br.com.softbox.core.AtestPageLoadedException;
import br.com.softbox.core.AtestWebDriverException;
import br.com.softbox.core.AtestWebElement;
import br.com.softbox.utils.Utils;

/**
 *
 */
public class RegisterPage {
    private AtestLog _log = new AtestLog("RegisterPage");
    private AtestEvidence _evidence = AtestEvidence.getInstance();
    private AtestWebElement _awe = AtestWebElement.getInstance();
    private final static String ACTIVE_CLASS = "active";

    
    public RegisterPage() {
    }

    public WebElement getCpfAlert() {
    	WebElement el;
    	 try {
    		 el = _awe.findElement("ProductPage.getExistCpfMsg.cpf");
    	 } catch (AtestNoSuchWebElementException | AtestWebDriverException e)  {
    		 el = null;
    	 }
    	 return el;
    }
    
    public WebElement getCnpjAlert() {
    	WebElement el;
    	 try {
    		 el = _awe.findElement("ProductPage.getCnpjAlert.cnpj");
    	 } catch (AtestNoSuchWebElementException | AtestWebDriverException e)  {
    		 el = null;
    	 }
    	 return el;
    }
    
    public boolean registerPF(PessoaFisica pf) {
        boolean retCode = false;
        try {
            if (selectTabPF()) {
                if (fillPF(pf)) {
                    try {
                        _evidence.screenshot("pessoa_fisica_form_filled");
                        _awe.findElement("RegisterPage.registerPF.button_continuar").click();
                        retCode = true;
                        try {
                        	WebElement el = this.getCpfAlert();
                        	if(el != null) {
	                        	String cpfString = el.getText();
	                        	if(cpfString.equals("CPF")) {
	                        		String msgErro = "CPF already registered - " + pf.getCPFasRaw();
	                        		retCode = false;
	                        		throw new AtestNoSuchWebElementException(msgErro);
	
	                        	} 
                        	} else {
                        		try {
                        			_awe.findElement("ops");
                        			_evidence.screenshot("OPS_pessoa_fisica_missing_values");
                        		} catch (AtestNoSuchWebElementException e) {
                                    _log.trace("No Ops message is present on page, it seems everything is ok");
                                }
                        	}
                            // no exception, then it is visible, in other words: the page has a filling issue.
                        } catch (AtestNoSuchWebElementException e) {
                        	 Utils.isTrue.go(false, _log.me(), e.getMessage());
                        }
                        if (retCode) {
                            try {
                                _awe.waitForPageLoaded();
                            } catch (AtestPageLoadedException e) {
                                _log.info("WARNING: page not loaded after clicking on 'register PF': " + e.getMessage());
                            }
                        }
                    } catch (AtestNoSuchWebElementException e) {
                        _evidence.screenshotOnError("RegisterPage_registerPF");
                        _log.oops(e.getMessage());
                    }
                }
            } else {
                _log.oops("unable to click on tab \"pessoa fisica\"");
            }
        } catch (AtestWebDriverException e) {
            _log.oops(e.getMessage());
        }
        return retCode;
    }

    public boolean registerPJ(PessoaJuridica pj) {
        boolean retCode = false;
        try {
            if (selectTabPJ()) {
                if (fillPJ(pj)) {
                    try {
                    	_evidence.screenshot("pessoa_juridica_form_filled");
                    	_awe.findElement("RegisterPage.registerPJ.button_continuar").click();
                    	retCode = true;
                    	WebElement el = this.getCnpjAlert();
                    	if(el != null) {
                        	String cnpjString = el.getText();
                        	if(cnpjString.equals("CNPJ")) {
                        		String msgErro = "CNPJ already registered - " + pj.getCNPJAsRaw();
                        		retCode = false;
                        		throw new AtestNoSuchWebElementException(msgErro);
                        	} 
                        	
                    	} else {
	                        try {
	                            _awe.findElement("ops");
	                            _evidence.screenshot("OPS_pessoa_juridica_missing_values");
	                            // no exception, then it is visible, in other words: the page has a filling issue.
	                        } catch (AtestNoSuchWebElementException e) {
	                            _log.trace("No Ops message is present on page, it seems everything is ok");
	                        }
                    	}
                        if (retCode) {
                            try {
                                _awe.waitForPageLoaded();
                            } catch (AtestPageLoadedException e) {
                                _log.info("WARNING: page not loaded after clicking on 'register PJ': " + e.getMessage());
                            }
                        }
                    } catch (AtestNoSuchWebElementException e) {
                    	Utils.isTrue.go(false, _log.me(), e.getMessage());
                        _evidence.screenshotOnError("RegisterPage_registerPJ");
                    }
                }
            } else {
                _log.oops("unable to click on tab \"pessoa juridica\"");
            }
        } catch (AtestWebDriverException e) {
            _log.oops(e.getMessage());
        }
        return retCode;
    }

    private boolean selectTabPJ() throws AtestWebDriverException {
        boolean retCode = false;
        try {
        	Utils.wait(1000);
            List<WebElement> tabs = _awe.findElements("RegisterPage.selectTabPJ.pj");
            for (WebElement tab : tabs) {
                String att = tab.getAttribute("class").trim();
                String text = tab.getText().trim().toLowerCase();
                if (text.matches("pessoa jur.+dica")){
                    if(!att.equalsIgnoreCase(ACTIVE_CLASS)){
                        tab.click();
                    }
                    retCode = true;
                    break;
                }
            }
        } catch (AtestNoSuchWebElementException e) {
            _log.oops(e.getMessage());
        }
        return retCode;
    }

    private boolean fillPJ(PessoaJuridica pj) throws AtestWebDriverException {
        boolean retCode = false;
        Utils.wait(1000);
        try {
            _awe.findElement("RegisterPage.fillPJ.company_name").sendKeys(pj.getRazaoSocial());
            _awe.findElement("RegisterPage.fillPJ.fantasy_name").sendKeys(pj.getNomeFantasia());
            _awe.findElement("RegisterPage.fillPJ.cnpj").sendKeys(pj.getCNPJAsRaw());
            _awe.findElement("RegisterPage.fillPJ.state_registration").sendKeys(pj.getInscricaoEstatual());
            _awe.findElement("RegisterPage.fillPJ.cep1").sendKeys(pj.getCEPCampo1());
            WebElement cep2 = _awe.findElement("RegisterPage.fillPJ.cep2");
            cep2.sendKeys(pj.getCEPCampo2());
            cep2.sendKeys(Keys.TAB);
            _awe.findElement("RegisterPage.fillPJ.addressNum").sendKeys(pj.getNumero());
            _awe.findElement("RegisterPage.fillPJ.phoneDDD").sendKeys(pj.getTelFixoDDD());
            _awe.findElement("RegisterPage.fillPJ.phone").sendKeys(pj.getTellFixo());
            _awe.findElement("RegisterPage.fillPJ.cellDDD").sendKeys(pj.getTelCellDD());
            _awe.findElement("RegisterPage.fillPJ.cell").sendKeys(pj.getTelCel());
            _awe.findElement("RegisterPage.fillPJ.password").sendKeys(pj.getSenha());
            _awe.findElement("RegisterPage.fillPJ.accept_contract_terms").click();
            retCode = true;
        } catch (AtestNoSuchWebElementException e) {
            _log.oops(e.getMessage());
        }
        return retCode;
    }
    
    public Boolean getTooltipMsg() {
    	Boolean rc = false;
    	String  tooltipMsg = null;
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

    private boolean fillPF(PessoaFisica pf) throws AtestWebDriverException {
        boolean retCode = false;

        try {
            _awe.findElement("RegisterPage.fillPF.name").sendKeys(pf.getNome());
            if (pf.isFemale()) {
                _awe.findElement("RegisterPage.fillPF.female").click();
            } else {
                _awe.findElement("RegisterPage.fillPF.male").click();
            }
            _awe.findElement("RegisterPage.fillPF.lastName").sendKeys(pf.getSobreNome());
            _awe.selectOption("RegisterPage.fillPF.dayBrithday", pf.getNascDia(), "Dia nascimento");
            _awe.selectOption("RegisterPage.fillPF.monthBrithday", pf.getNascMes(), "Mes nascimento");
            _awe.selectOption("RegisterPage.fillPF.yearBrithday", pf.getNascAno(), "Ano nascimento");
            _awe.findElement("RegisterPage.fillPF.cpf").sendKeys(pf.getCPFasRaw());
            _awe.findElement("RegisterPage.fillPF.cep1").sendKeys(pf.getCEPCampo1());
            WebElement cep2 = _awe.findElement("RegisterPage.fillPF.cep2");
            cep2.sendKeys(pf.getCEPCampo2());
            cep2.sendKeys(Keys.TAB);
            Utils.wait(2000);
            _awe.findElement("RegisterPage.fillPF.addressNum").sendKeys(pf.getNumero());
            retCode = this.getTooltipMsg();
            _awe.findElement("RegisterPage.fillPF.phoneDDD").sendKeys(pf.getTelFixoDDD());
            
            _awe.findElement("RegisterPage.fillPF.phone").sendKeys(pf.getTellFixo());
            _awe.findElement("RegisterPage.fillPF.cellDDD").sendKeys(pf.getTelCellDD());
            _awe.findElement("RegisterPage.fillPF.cell").sendKeys(pf.getTelCel());
            _awe.findElement("RegisterPage.fillPF.password").sendKeys(pf.getSenha());
            _awe.findElement("RegisterPage.fillPF.accept_contract_terms").click();
        } catch (AtestNoSuchWebElementException e) {
            _log.oops(e.getMessage());
        }

        return retCode;
    }

    private boolean selectTabPF() throws AtestWebDriverException {
        boolean retCode = false;
        try {
            List<WebElement> tabs = _awe.findElements("RegisterPage.selectTabPF.pf");
            for (WebElement tab : tabs) {
                String att = tab.getAttribute("class").trim();
                String text = tab.getText().trim().toLowerCase();
                if (text.matches("pessoa f.sica")){
                    if(!att.equalsIgnoreCase(ACTIVE_CLASS)){
                        tab.click();
                    }
                    retCode = true;
                    break;
                }
            }
        } catch (AtestNoSuchWebElementException e) {
            _log.oops(e.getMessage());
        }
        return retCode;
    }
}
