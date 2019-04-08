package br.com.softbox.atest_bdd.pages;

import br.com.softbox.atest_bdd.models.FardamentoFutebol;
import br.com.softbox.atest_bdd.models.FardamentoFutebolJogador;
import br.com.softbox.core.*;
import br.com.softbox.utils.Utils;
import org.openqa.selenium.WebElement;

import java.util.List;


public class FardamentoFutebolPage {
    private AtestLog _log = new AtestLog("FardamentoFutebolPage");
    private AtestEvidence _evidence = AtestEvidence.getInstance();
    private FardamentoFutebol _fardamento;

    public FardamentoFutebolPage(FardamentoFutebol fardamento) {
        _fardamento = fardamento;
    }

    public boolean isStartingPage() {
        try {
            // check 1 - has text on the page: 'DIGITE O NOME DO SEU TIME'
            AtestWebElement.getInstance().findElement("FardamentoFutebolPage.isStartingPage.team_name_check");
            // Check 2 - input field
            AtestWebElement.getInstance().findElement("FardamentoFutebolPage.isStartingPage.team_name");
            return true;
        } catch (AtestNoSuchWebElementException | AtestWebDriverException e) {
            _log.oops(e.getMessage());
            _evidence.screenshotOnError("FardamentoFutebolPage_isStartingPage");
        }
        return false;
    }

    public boolean configTeam() {
        boolean retCode = false;
        try {
            setTeamName();
            hasTeamNameOnTopOfPage();

            // T-Shirt
            // TODO: descomentar isso
            selectFirstTShirtAvailable();
//            selectTShirtOutOfStock();               // TODO: ONLY FOR TESTS!!!!!!!!!!!!!!!!!!!!!!!!!
            Utils.wait(1000);
            removeTShirtCustomizations();
            Utils.wait(1000);
            goToNextStep();
            Utils.wait(1000);

            // Shorts
            addShorts();
            Utils.wait(1000);
            selectFirstShortsAvailable();
            Utils.wait(1000);

            // Socks
            addSocks();
            Utils.wait(1000);
            selectFirstSocksAvailable();
            Utils.wait(1000);

            // goalkeeper (goleiro)
            addGoalkeeper();
            Utils.wait(1000);
            selectFirstTShirtAvailable();
            Utils.wait(1000);
            removeTShirtCustomizations();
            Utils.wait(1000);
            goToNextStep();
            Utils.wait(1000);
            addShorts();
            Utils.wait(1000);
            selectFirstShortsAvailable();
            Utils.wait(1000);
            addSocksGoalkeeper();
            Utils.wait(1000);
            selectFirstSocksAvailable();
            setPlayers();
            goToNextStep();

            checkForOutOfStockSizes();

            checkItemsAndGo();

            retCode = true;
        } catch (AtestNoSuchWebElementException | AtestWebDriverException e) {
            _log.oops(e.getMessage());
            _evidence.screenshotOnError("FardamentoFutebolPage_configTeam");
        }
        return retCode;
    }

    private void checkForOutOfStockSizes() throws AtestNoSuchWebElementException, AtestWebDriverException {
        if (hasOutOfStockText()) {
            disableSizesOutOfStock();
        }
    }

    private void selectTShirtOutOfStock() throws AtestNoSuchWebElementException, AtestWebDriverException {
        AtestWebElement.getInstance().findElement("TESTE_FARDAMENTO_TSHIRT_OUT_OF_STOCK").click();
    }

    private void checkItemsAndGo() throws AtestNoSuchWebElementException, AtestWebDriverException {
        Utils.wait(3000);

        // TODO: in the future it might be necessary to verify all the items and its selected values... for now, this is enough.

        AtestWebElement.getInstance().findElement("FardamentoFutebolPage.checkItemsAndGo.done").click();
    }

    private void setPlayers() throws AtestNoSuchWebElementException{
        List<FardamentoFutebolJogador> players = _fardamento.getPlayers();
        if (_fardamento.getTeamType() == FardamentoFutebol.TeamType.FUTSAL && players.size() == 5) {
            int idx = 0;
            for (FardamentoFutebolJogador p : players) {
                if (p.tipo == FardamentoFutebolJogador.JogadorType.GOLEIRO) {
                    if (!setGoalkeeper(p.numero, p.nome)) {
                        throw new AtestNoSuchWebElementException("Unable to config Fardamento for goalkeeper");
                    }
                } else {
                    setPlayer(idx, p.numero, p.nome);
                    idx++;
                }
            }
        } else {
            throw new AtestNoSuchWebElementException("Wrong number of players for FUTSAL");
        }
    }

    private boolean setPlayer(int idx, int numero, String nome) {
        AtestWebElement awe = AtestWebElement.getInstance();
        try {
            switch (idx) {
            case 0:
                awe.findElement("FardamentoFutebolPage.setPlayer.player_name_0").sendKeys(nome);
                awe.findElement("FardamentoFutebolPage.setPlayer.player_number-0").sendKeys(String.valueOf(numero));
                awe.selectOptionByIndex("FardamentoFutebolPage.setPlayer.player_shirt_0", 1, "camisa jogador 0");
                awe.selectOptionByIndex("FardamentoFutebolPage.setPlayer.player_shorts_0", 1, "calcao jogador 0");
                awe.selectOptionByIndex("FardamentoFutebolPage.setPlayer.player_socks_0", 1, "meiao jogador 0");
                break;
            case 1:
                awe.findElement("FardamentoFutebolPage.setPlayer.player_name_1").sendKeys(nome);
                awe.findElement("FardamentoFutebolPage.setPlayer.player_number-1").sendKeys(String.valueOf(numero));
                awe.selectOptionByIndex("FardamentoFutebolPage.setPlayer.player_shirt_1", 1, "camisa jogador 1");
                awe.selectOptionByIndex("FardamentoFutebolPage.setPlayer.player_shorts_1", 1, "calcao jogador 1");
                awe.selectOptionByIndex("FardamentoFutebolPage.setPlayer.player_socks_1", 1, "meiao jogador 1");
                break;
            case 2:
                awe.findElement("FardamentoFutebolPage.setPlayer.player_name_2").sendKeys(nome);
                awe.findElement("FardamentoFutebolPage.setPlayer.player_number-2").sendKeys(String.valueOf(numero));
                awe.selectOptionByIndex("FardamentoFutebolPage.setPlayer.player_shirt_2", 1, "camisa jogador 2");
                awe.selectOptionByIndex("FardamentoFutebolPage.setPlayer.player_shorts_2", 1, "calcao jogador 2");
                awe.selectOptionByIndex("FardamentoFutebolPage.setPlayer.player_socks_2", 1, "meiao jogador 2");
                break;
            case 3:
                awe.findElement("FardamentoFutebolPage.setPlayer.player_name_3").sendKeys(nome);
                awe.findElement("FardamentoFutebolPage.setPlayer.player_number-3").sendKeys(String.valueOf(numero));
                awe.selectOptionByIndex("FardamentoFutebolPage.setPlayer.player_shirt_3", 1, "camisa jogador 3");
                awe.selectOptionByIndex("FardamentoFutebolPage.setPlayer.player_shorts_3", 1, "calcao jogador 3");
                awe.selectOptionByIndex("FardamentoFutebolPage.setPlayer.player_socks_3", 1, "meiao jogador 3");
                break;
            default:
                break;
            }
            return true;
        } catch (AtestNoSuchWebElementException | AtestWebDriverException e) {
            _log.oops(e.getMessage());
            _evidence.screenshotOnError("FardamentoFutebolPage_setPlayer");
        }
        return false;
    }

    private boolean setGoalkeeper(int numero, String nome){
        AtestWebElement awe = AtestWebElement.getInstance();
        try {
            awe.findElement("FardamentoFutebolPage.setGoalkeeper.goalkeeper_name").sendKeys(nome);
            awe.findElement("FardamentoFutebolPage.setGoalkeeper.goalkeeper_number").sendKeys(String.valueOf(numero));
            awe.selectOptionByIndex("FardamentoFutebolPage.setGoalkeeper.goalkeeper_shirt_size", 1, "camisa goleiro");
            awe.selectOptionByIndex("FardamentoFutebolPage.setGoalkeeper.goalkeeper_shorts_size", 1, "calcao goleiro");
            awe.selectOptionByIndex("FardamentoFutebolPage.setGoalkeeper.goalkeeper_socks_size", 1, "meiao goleiro");
            return true;
        } catch (AtestNoSuchWebElementException | AtestWebDriverException e) {
            _log.oops(e.getMessage());
            _evidence.screenshotOnError("FardamentoFutebolPage_setGoalkeeper");
        }
        return false;
    }

    private void addSocksGoalkeeper() throws AtestNoSuchWebElementException, AtestWebDriverException {
        AtestWebElement.getInstance().findElement("FardamentoFutebolPage.addSocksGoalkeeper.sim").click();
    }

    private void addGoalkeeper() throws AtestNoSuchWebElementException, AtestWebDriverException {
        AtestWebElement.getInstance().findElement("FardamentoFutebolPage.addGoalkeeper.sim").click();
    }

    private void selectFirstSocksAvailable() throws AtestNoSuchWebElementException, AtestWebDriverException {
        AtestWebElement.getInstance().findElement("FardamentoFutebolPage.selectFirstSocksAvailable.socks").click();
    }

    private void addSocks() throws AtestNoSuchWebElementException, AtestWebDriverException {
        AtestWebElement.getInstance().findElement("FardamentoFutebolPage.addSocks.sim").click();
    }

    private void addShorts() throws AtestNoSuchWebElementException, AtestWebDriverException {
        AtestWebElement.getInstance().findElement("FardamentoFutebolPage.addShorts.sim").click();
    }

    private void selectFirstShortsAvailable() throws AtestNoSuchWebElementException, AtestWebDriverException {
        AtestWebElement.getInstance().findElement("FardamentoFutebolPage.selectFirstShortsAvailable.shorts").click();
    }

    private void goToNextStep() throws AtestNoSuchWebElementException, AtestWebDriverException {
        AtestWebElement awe = AtestWebElement.getInstance();
        awe.findElement("FardamentoFutebolPage.goToNextStep.continuar").click();

    }

    private void disableSizesOutOfStock() throws AtestNoSuchWebElementException, AtestWebDriverException {
        List<WebElement> tshirts = null;
        List<WebElement> shorts = null;
        List<WebElement> socks = null;
        try {
            tshirts = AtestWebElement.getInstance()
                    .findElements("FardamentoFutebolPage.tryAnotherSizesForItensOutOfStock.tshirts");
        } catch (AtestNoSuchWebElementException | AtestWebDriverException e) {
            ; // The error is not here.
        }
        try {
            shorts = AtestWebElement.getInstance()
                    .findElements("FardamentoFutebolPage.tryAnotherSizesForItensOutOfStock.shorts");
        } catch (AtestNoSuchWebElementException | AtestWebDriverException e) {
            ; // The error is not here.
        }
        try {
            socks = AtestWebElement.getInstance()
                    .findElements("FardamentoFutebolPage.tryAnotherSizesForItensOutOfStock.socks");
        } catch (AtestNoSuchWebElementException | AtestWebDriverException e) {
            ; // The error is not here.
        }
        if (tshirts == null && shorts == null && socks == null) {
            throw new AtestNoSuchWebElementException("unable to find the sizes out of stock");
        }
        AtestWebElement awe = AtestWebElement.getInstance();
        if (tshirts != null) {
            for (WebElement item : tshirts) {
                awe.selectLastOption(item, "size for t-shirt");
            }
        }
        if (shorts != null) {
            for (WebElement item : shorts) {
                awe.selectLastOption(item, "size for shorts");
            }
        }
        if (socks != null) {
            for (WebElement item : socks) {
                awe.selectLastOption(item, "size for socks");
            }
        }
    }

    private boolean hasOutOfStockText() {
        try {
            AtestWebElement.getInstance().findElement("FardamentoFutebolPage.hasOutOfStockText.out_of_stock_text");
            return true;
        } catch (AtestNoSuchWebElementException | AtestWebDriverException e) {
            ;
        }
        return false;
    }

    private void removeTShirtCustomizations() throws AtestNoSuchWebElementException, AtestWebDriverException {
        AtestWebElement awe = AtestWebElement.getInstance();
        awe.findElement("FardamentoFutebolPage.removeTShirtCustomizations.remover_estilo").click();
        Utils.wait(2000);
        awe.findElement("FardamentoFutebolPage.removeTShirtCustomizations.remover_escudo").click();
        Utils.wait(2000);
        awe.findElement("FardamentoFutebolPage.removeTShirtCustomizations.remover_patrocinio").click();
    }

    private void selectFirstTShirtAvailable() throws AtestNoSuchWebElementException, AtestWebDriverException {
        AtestWebElement.getInstance().findElement("FardamentoFutebolPage.selectFirstTShirtAvailable.tshirt").click();
    }

    private void setTeamName() throws AtestNoSuchWebElementException, AtestWebDriverException {
        AtestWebElement awe = AtestWebElement.getInstance();
        awe.findElement("FardamentoFutebolPage.isStartingPage.team_name").sendKeys(_fardamento.getTeamName());
        awe.findElement("FardamentoFutebolPage.setTeamName.start").click();
    }

    private boolean hasTeamNameOnTopOfPage() throws AtestNoSuchWebElementException, AtestWebDriverException {
        WebElement teamName = AtestWebElement.getInstance()
                .findElement("FardamentoFutebolPage.hasTeamNameOnTopOfPage.team_name_on_top_of_page");
        return teamName.getText().trim().equals(_fardamento.getTeamName());
    }
}
