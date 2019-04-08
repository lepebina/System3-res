package br.com.softbox.atest_bdd.pages;

import br.com.softbox.core.*;
import br.com.softbox.utils.Utils;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;


/**
 * Manages the user menu
 */
public class MenuUserAccountPage {
    private AtestEvidence _evidence = AtestEvidence.getInstance();
    private final String VISITANTE = "Visitante";
    private final Integer TRIES = 5;
    private AtestLog _log = new AtestLog("MenuUserAccountPage");

    public MenuUserAccountPage() {
    }

    private boolean chooseOption(String selectorOptionId, String msgTrace, Integer tries) {
        boolean retCode = false;
        AtestWebElement awe = AtestWebElement.getInstance();
        try {
            Integer count = 0;
            while (count < tries) {
                try {
                    if (TestController.WebBrowser.IE != _evidence.getBrowserInUse()) {
                        awe.waitForPageLoaded();
                    }
                    WebElement menu = awe.findElement("MenuUserAccountPage.menu");
                    Actions action = new Actions(_evidence.getWebDriver());
                    action.moveToElement(menu).build().perform();
                    WebElement clickOption = awe.findElement(selectorOptionId);
                    if (!clickOption.isDisplayed()) {
                        action.moveToElement(menu).build().perform();
                    }
                    if(TestController.WebBrowser.IE == _evidence.getBrowserInUse()){
                        action.click(clickOption).build().perform();
                    } else {
                        clickOption.click();
                    }
                    Utils.wait(500);
                    retCode = true;
                    if (TestController.WebBrowser.IE != _evidence.getBrowserInUse()) {
                        awe.waitForPageLoaded();
                    }
                    break;
                } catch (AtestNoSuchWebElementException e) {
                    _log.info(msgTrace + " - Try: " + ++count + " - " + e.getMessage());
                }
            }
        } catch (AtestPageLoadedException e) {
            _log.info(msgTrace + " - " + "WARNING: page not loaded after clicking on Menu option: " + e.getMessage());
        } catch (AtestWebDriverException e) {
            _log.oops(msgTrace + " - " + e.getMessage());
        }
        return retCode;
    }

    /**
     * Returns whether the user is logged
     * @return true the user is logged, otherwise false.
     */
    public boolean isLogged(String expectedUserName) {
        boolean retCode = false;
        try {
            String nameOnMenu = "";
            boolean keepTryingWhenUserEmpty = false;
            do {
                WebElement userNameElement = AtestWebElement.getInstance().findElement("MenuUserAccountPage.greetings_name");
                nameOnMenu = userNameElement.getText().trim();
                if (nameOnMenu.isEmpty()) {
                    Utils.wait(1000);
                    keepTryingWhenUserEmpty = true;
                    continue;
                }
                return nameOnMenu.equals(expectedUserName);
            } while (keepTryingWhenUserEmpty);

        } catch (AtestNoSuchWebElementException | AtestWebDriverException e) {
            _log.oops(e.getMessage());
        }
        return retCode;
    }

    private boolean doLogout() {
        if (isLogged(VISITANTE)) {
            return true;
        }
        int count = 0;
        while (!isLogged(VISITANTE) && count++ < TRIES) {
            if (chooseOptionSair()) {
                _log.trace("Attempt #" + count + " to logout...");
                return true;
            }
        }
        return false;
    }

    public boolean chooseOptionLogin() {
        if (!doLogout()) {
            _log.oops("Failed to logout");
            return false;
        }
        boolean retCode = chooseOption("MenuUserAccountPage.menu_option_login"
                , "menu hover on login"
                , TRIES);
        if (retCode && !_evidence.getCurrentPage(TestController.MainMenuOptionCode.LOGIN)) {
            goToPage(_evidence.getMainMenuPageURL(TestController.MainMenuOptionCode.LOGIN));
        }
        return retCode;
    }

    public boolean chooseOptionMinhaConta() {
        boolean retCode =  chooseOption("MenuUserAccountPage.menu_option_minha_conta"
                , "menu hover on 'minha conta'"
                , TRIES);
        if (retCode && !_evidence.getCurrentPage(TestController.MainMenuOptionCode.ACCOUNT)) {
            goToPage(_evidence.getMainMenuPageURL(TestController.MainMenuOptionCode.ACCOUNT));
        }
        return retCode;
    }

    public boolean chooseOptionListadeDesejos() {
        boolean retCode =  chooseOption("MenuUserAccountPage.menu_option_wishlist"
                , "menu hover on 'lista de desejos'"
                , TRIES);
        if (retCode && !_evidence.getCurrentPage(TestController.MainMenuOptionCode.WISH_LIST)) {
            goToPage(_evidence.getMainMenuPageURL(TestController.MainMenuOptionCode.WISH_LIST));
        }
        return retCode;
    }

    public boolean chooseOptionSair() {
        if (chooseOption("MenuUserAccountPage.menu_option_sair"
                    , "menu hover on 'sair'"
                    , TRIES)) {
            Utils.wait(3000);
            if (!isLogged(VISITANTE)) {
                goToPage(_evidence.getMainMenuPageURL(TestController.MainMenuOptionCode.LOGOUT));
            }
            return true;
        }
        return false;
    }

    private void goToPage(String url) {
        try {
            String navigateTo = _evidence.getInitialSiteURL() + "/" + url;
            _evidence.getWebDriver().navigate().to(navigateTo);
        } catch (AtestWebDriverException e) {
            e.printStackTrace();
        }
    }
}

