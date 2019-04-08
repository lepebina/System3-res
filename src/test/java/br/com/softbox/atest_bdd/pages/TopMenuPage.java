package br.com.softbox.atest_bdd.pages;

import br.com.softbox.core.*;
import br.com.softbox.utils.Utils;


public class TopMenuPage {
    private AtestLog _log = new AtestLog("TopMenuPage");
    private AtestEvidence _evidence = AtestEvidence.getInstance();

//    public boolean isPage(TopMenuOptions esportesFutebolFardmento) {
//        return false;
//    }

    private boolean chooseEsporte() {
        try {
            AtestWebElement.getInstance().findElement("TopMenuPage.chooseEsporte").click();
            return true;
        } catch (AtestNoSuchWebElementException | AtestWebDriverException e) {
            _log.oops(e.getMessage());
            _evidence.screenshotOnError("TopMenuPage_chooseEsporte");
        }
        return false;
    }

    public boolean chooseEsporteFutebolFardamento() {
        try {
            if (chooseEsporte()) {
                AtestWebElement.getInstance().findElement("TopMenuPage.chooseEsporteFutebolFardamento").click();
                return true;
            }
        } catch (AtestNoSuchWebElementException | AtestWebDriverException e) {
            _log.oops(e.getMessage());
            _evidence.screenshotOnError("TopMenuPage_chooseEsporteFutebolFardamento");
        }
        return false;
    }

}
