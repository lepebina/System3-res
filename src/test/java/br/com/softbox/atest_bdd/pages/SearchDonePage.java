package br.com.softbox.atest_bdd.pages;

import br.com.softbox.core.*;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

import java.util.List;


public class SearchDonePage {
    private AtestEvidence _evidence = AtestEvidence.getInstance();
    private AtestLog _log = new AtestLog("SearchDonePage");

    public boolean isSearchOk(){
        try {
            AtestWebElement.getInstance().findElement("Search.resultOK");
            _evidence.screenshot("Valid_search_done");
            return true;
        }   catch (AtestNoSuchWebElementException e) {
            _log.oops("Unable to find any product result. It means the search wasn't valid " + e.getMessage());
            _evidence.screenshotOnError("Valid_search_done");
        } catch (AtestWebDriverException e) {
            _log.oops(e.getMessage());
        }
        return false;
    }


    public boolean isSearchNotOk(){
        try {
            AtestWebElement.getInstance().findElement("Search.resultNOK");
            _evidence.screenshot("Invalid_Search_done_Ops");
            return true;
        }   catch (AtestNoSuchWebElementException e) {
            _log.oops("Unable to find the OPS message. It means the search wasn't invalid " + e.getMessage());
            _evidence.screenshotOnError("Invalid_Search_done_Ops");
        } catch (AtestWebDriverException e) {
            _log.oops(e.getMessage());
        }
        return false;
    }

    public boolean BacktoTop() {
        try {
            ((JavascriptExecutor) _evidence.getWebDriver()).executeScript("scroll(0,1000)");
            _evidence.screenshot("Backing_to_the_top");
            Thread.sleep(2);
            AtestWebElement.getInstance().findElement("Search.backToTopButton").click();

            return true;
        } catch (AtestNoSuchWebElementException e) {
            _log.oops("Button 'back to top' not found " + e.getMessage());
            _evidence.screenshotOnError("Backing_to_the_top");
        } catch (AtestWebDriverException e) {
            _log.oops(e.getMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isBackToTopOk() {
        try {
            AtestWebElement.getInstance().findElement("Search.backToTopButtonNotVisible");
            _evidence.screenshot("Back_to_the_top_ok");
            return true;
        } catch (AtestNoSuchWebElementException e) {
            _log.oops("Button 'back to top' was found. It means the scroll up didn't work" + e.getMessage());
            _evidence.screenshotOnError("Back_to_the_top_ok");
        } catch (AtestWebDriverException e) {
            _log.oops(e.getMessage());
        }
        return false;
    }

    public boolean SearchFilter(int filterPosition){
        try {
            AtestWebElement awe = AtestWebElement.getInstance();
            List<WebElement> allFilters = awe.findElements("Search.findFilter");
            final String filterName = allFilters.get(filterPosition).getText();
            allFilters.get(filterPosition).click();

            for (WebElement elem : awe.findElements("Search.isFilterSelected")) {
                if (elem.getText().trim().equals(filterName)) {
                    return true;
                }
            }
        } catch (AtestNoSuchWebElementException e) {
            _log.oops("No filter element found" + e.getMessage());

        } catch (AtestWebDriverException e) {
            _log.oops(e.getMessage());
        }
        return false;
    }

    public boolean IsFilterSelected(){
        try {
            AtestWebElement.getInstance().findElement("Search.isFilterSelected");
            _evidence.screenshot("Filter_selected");
            return true;
        } catch (AtestNoSuchWebElementException e) {
            _log.oops("No filter was selected" + e.getMessage());
            _evidence.screenshotOnError("Filter_selected");
        } catch (AtestWebDriverException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean RemoveFilter(){
        try {
            List<WebElement> filterRemoveButton = AtestWebElement.getInstance().findElements("Search.deleteFilterSelected");
            for (WebElement removeButton : filterRemoveButton) {
                if (removeButton.getText().contains("Excluir")) {
                    removeButton.click();
                    return true;
                }
            }
        } catch (AtestNoSuchWebElementException e) {
            _log.oops("Delete filter button not found" + e.getMessage());
        } catch (AtestWebDriverException e) {
            _log.oops(e.getMessage());
        }
        return false;
    }

    public boolean searchDoneAutoComplete() {
        boolean retCode = false;
        AtestWebElement awe = AtestWebElement.getInstance();
       /* JavascriptExecutor jse = null;*/

        try {
           /* jse = (JavascriptExecutor) _evidence.getWebDriver();
            jse.executeScript("arguments[0].scrollIntoView(true);", isSearchOK);*/

            awe.findElement("Search.resultOK").isDisplayed();
            _evidence.screenshot("Valid_search_done");
            List<WebElement> paginationNum = awe.findElements("Search.SelectPage");

            paginationNum.get(2).click();

            awe.findElement("Search.resultOK").isDisplayed();
            _evidence.screenshot("Valid_search_done");
            List<WebElement> paginationNum2 = awe.findElements("Search.SelectPage");
            paginationNum2.get(3).click();

            awe.findElement("Search.resultOK").isDisplayed();

            retCode = true;
        } catch (AtestNoSuchWebElementException e) {
            _log.oops("Search valid result not found" + e.getMessage());
            _evidence.screenshotOnError("Valid_search_done");
        } catch (AtestWebDriverException e) {
            _log.oops(e.getMessage());
        }
        return retCode;
    }

}
