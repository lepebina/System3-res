package br.com.softbox.atest_bdd.pages;

import br.com.softbox.core.*;


public class SteloPage {
    private AtestEvidence _evidence = AtestEvidence.getInstance();
    private AtestLog _log = new AtestLog("SteloPage");

    public SteloPage() {
    }

    public boolean isHomePage() {
        try {
            AtestWebElement.getInstance().findElement("SteloPage.isHomePage.username");
            _evidence.screenshot("SteloPage_isHomePage");
            return true;
        } catch (AtestNoSuchWebElementException | AtestWebDriverException e) {
            _log.oops(e.getMessage());
            _evidence.screenshotOnError("SteloPage_no_home_page");
        }
        return false;
    }
}
