package br.com.softbox.atest_bdd.pages;

import br.com.softbox.core.*;
import br.com.softbox.utils.Utils;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import sun.util.resources.fi.CalendarData_fi;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;


public class PaginationPage {
    private AtestLog _log = new AtestLog("PaginationPage");
    private AtestEvidence _evidence = AtestEvidence.getInstance();
    private String _lastPageLabel;
    private String _firstPageLabel;

    private enum PaginationAction {FORWARD, BACKWARD, FIRST, LAST};
    private enum PaginationButtonType {PAGE, FORWARD, BACKWARD, UNKNOWN};
    private final String _previousButtonLabel = "anterior";
    private final String _nextButtonLabel = "proximo";
    private String _currPageLabel;

    private class PaginationButton {
        public PaginationButtonType _type;
        public String _label;
        public WebElement _we;
        public boolean _isActive;
        public PaginationButton(PaginationButtonType type, String label, WebElement we, boolean isActive) {
            _type = type;
            _label = label;
            _we = we;
            _isActive = isActive;
        }
    }
    private LinkedHashMap<String, PaginationButton> _controls = new LinkedHashMap<>();

    public boolean hasControls() {
        try {
            WebElement control = AtestWebElement.getInstance().findElement("PaginationPage.hasControls.control");
            Actions action = new Actions(_evidence.getWebDriver());
            action.moveToElement(control).build().perform();
            Utils.wait(2000);
            _evidence.screenshot("PaginationPage_controls");
            return true;
        } catch (AtestNoSuchWebElementException | AtestWebDriverException e) {
            _log.oops(e.getMessage());
            _evidence.screenshotOnError("PaginationPage_controls");
        }
        return false;
    }

    public boolean forward() {
        mapControls();
        String expectedLabel = getNextLabelAfterCurrent();
        if (doForward()) {
            if (expectedLabel.equals(_currPageLabel)) {
                hasControls();  // just to move mouse over the controls
                _evidence.screenshot("PaginationPage_forward_to_page_" + _currPageLabel);
                return true;
            } else {
                _log.oops("forwards but the current page is not the expected one ["
                        + expectedLabel + "] current is [" + _currPageLabel + "]");
                _evidence.screenshotOnError("PaginationPage_forward_inconsitency");
                return false;
            }
        }
        return true; // should not return fail if it was unabled to click on "PROXIMO"
    }

    private boolean doForward() {
        if (_controls.containsKey(_nextButtonLabel)) {
            PaginationButton pageBtn = _controls.get(_nextButtonLabel);
            pageBtn._we.click();
            Utils.wait(2000);
            mapControls();      // Used for updating _currentPageLabel
            return true;
        }
        // This is the last page, so no "PROXIMO" button exists.
        return false;
    }

    private String getNextLabelAfterCurrent() {
        String nextLabel = "";
        if (!_currPageLabel.isEmpty()) {
            boolean foundCurrent = false;
            for (String key : _controls.keySet()) {
                if (key.equals(_currPageLabel)) {
                    foundCurrent = true;
                    continue;
                }
                if (foundCurrent) {
                    nextLabel = key;
                    break;
                }
            }
        }
        return nextLabel;
    }

    private String getPreviousLabelBeforeCurrent() {
        String prevLabel = _currPageLabel;
        for (String key : _controls.keySet()) {
            if (key.equals(_currPageLabel)) {
                break;
            }
            prevLabel = key;
        }
        if (prevLabel.equals(_currPageLabel)) { // there is no previous page
            prevLabel = "";
        }
        return prevLabel;
    }

    public boolean backward() {
        mapControls();
        String expectedLabel = getPreviousLabelBeforeCurrent();
        if (doBackward()) {
            if (expectedLabel.equals(_currPageLabel)) {
                hasControls();  // just to move mouse over the controls
                _evidence.screenshot("PaginationPage_backward_to_page_" + _currPageLabel);
                return true;
            } else {
                _log.oops("backwards but the current page is not the expected one ["
                        + expectedLabel + "] current is [" + _currPageLabel + "]");
                _evidence.screenshotOnError("PaginationPage_backward_inconsitency");
                return false;
            }
        }
        return true; // should not return fail if it was unabled to click on "ANTERIOR"
    }

    private boolean doBackward() {
        if (_controls.containsKey(_previousButtonLabel)) {
            PaginationButton pageBtn = _controls.get(_previousButtonLabel);
            pageBtn._we.click();
            Utils.wait(2000);
            mapControls();      // Used for updating _currentPageLabel
            return true;
        }
        // This is the last page, so no "ANTERIOR" button exists.
        return false;
    }

    public boolean first() {
        mapControls();
        String expectedLabel = _firstPageLabel;
        if (doFirst()) {
            if (expectedLabel.equals(_firstPageLabel)) {
                _evidence.screenshot("PaginationPage_first");
                return true;
            } else {
                _log.oops("first page navigation inconsistency. Expected ["
                        + expectedLabel + "] current is [" + _firstPageLabel + "]");
                _evidence.screenshotOnError("PaginationPage_first_page_inconsitency");
                return false;
            }
        }
        return true;
    }

    private boolean doFirst() {
        if (_controls.containsKey(_firstPageLabel)) {
            PaginationButton pageBtn = _controls.get(_firstPageLabel);
            pageBtn._we.click();
            Utils.wait(2000);
            mapControls();      // Used for updating _currentPageLabel
            return true;
        }
        return false;
    }

    public boolean last() {
        mapControls();
        String expectedLabel = _lastPageLabel;
        if (doLast()) {
            if (expectedLabel.equals(_lastPageLabel)) {
                _evidence.screenshot("PaginationPage_last");
                return true;
            } else {
                _log.oops("last page navigation inconsistency. Expected ["
                        + expectedLabel + "] current is [" + _lastPageLabel + "]");
                _evidence.screenshotOnError("PaginationPage_last_page_inconsitency");
                return false;
            }
        }
        return true; // should not return fail if it was unabled to click on "PROXIMO"
    }

    private boolean doLast() {
        if (_controls.containsKey(_lastPageLabel)) {
            PaginationButton pageBtn = _controls.get(_lastPageLabel);
            pageBtn._we.click();
            Utils.wait(2000);
            mapControls();      // Used for updating _currentPageLabel
            return true;
        }
        return false;
    }

    private void mapControls() {
        _controls.clear();
        try {
            List<WebElement> pages = AtestWebElement.getInstance().findElements("PaginationPage.getControls.control_set");
            Actions action = new Actions(_evidence.getWebDriver());
            action.moveToElement(pages.get(0)).build().perform();
            _evidence.screenshot("PaginationPage_current_controls");
            _lastPageLabel = "";
            _currPageLabel = "";
            _firstPageLabel = "";

            for (WebElement e : pages) {
                String attClass = e.getAttribute("class");
                String key = "unknown";
                PaginationButtonType buttonType = PaginationButtonType.UNKNOWN;

                boolean isActual = attClass.contains("is-actual");
                if (attClass.contains("is-prev")) {
                    key = _previousButtonLabel;
                    buttonType = PaginationButtonType.BACKWARD;
                } else if (attClass.contains("is-next")) {
                    key = _nextButtonLabel;
                    buttonType = PaginationButtonType.FORWARD;
                } else if (attClass.contains("is-numeric")) {
                    key = e.getText().trim();
                    buttonType = PaginationButtonType.PAGE;
                    if (_firstPageLabel.isEmpty()) {
                        _firstPageLabel = key;
                    }
                    _lastPageLabel = key;
                }
                if (!attClass.contains("is-sequel")) {
                    _controls.put(key, new PaginationButton(buttonType, e.getText().trim(), e, isActual));
                    if (isActual) {
                        _currPageLabel = key;
                    }
                }
            }
        } catch (AtestNoSuchWebElementException | AtestWebDriverException e) {
            _log.oops(e.getMessage());
            _evidence.screenshotOnError("PaginationPage_getControls");
        }
    }
}
