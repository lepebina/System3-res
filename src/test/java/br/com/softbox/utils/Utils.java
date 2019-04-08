package br.com.softbox.utils;

import br.com.softbox.core.AtestEvidence;
import br.com.softbox.core.AtestLogEvent;
import br.com.softbox.core.TestStepData;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 *
 */
public class Utils {
    /**
     * Take a screenshot
     * @param testName
     * @param driver
     */
    public static void takeSS(String testName, String dirName, WebDriver driver) {
        File dir = new File(dirName);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        try {
            String picName = testName + "_" + System.currentTimeMillis();
            FileUtils.copyFile(scrFile, new File(dirName + File.separator + picName + ".png"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the current date and time in a format for naming a file.
     * @return the current d
     */
    public static String getCurDateTimeForFileNames() {
        Date curDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        return sdf.format(curDate);
    }

    public static String getTimeStamp() {
        Date curDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        return sdf.format(curDate);
    }

    public static String getTime() {
        Date curDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(curDate);
    }

    public static String replacePunctuation(String value, String newValue) {
        return value.replaceAll("[^a-zA-Z]", newValue);
    }

    public static String getURL(String base, String relative) {
        String sUrl = "";
        try {
            String page = base + "/" + relative;
            URL url = new URL(page);
            sUrl = url.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return sUrl;
    }

    static public UtilsExpectTrue isTrue = (condition, publisher, msgOnError) -> {
        if (!condition) {
            AtestLogEvent event = new AtestLogEvent(AtestLogEvent.LogType.OOPS, publisher, msgOnError);
            AtestEvidence evidence = AtestEvidence.getInstance();
            evidence.log(event);
            TestStepData step = evidence.getCurrentScenarioStep();
            step.status = TestStepData.TestStepStatus.TEST_STEP_FAILED;
            step.finishTime = System.currentTimeMillis();
            step.error = msgOnError;
            evidence.failStep(step);
            evidence.finishScenario(step.scenarioName, "failed");
        }
        assertTrue(condition);
    };

    static public UtilsExpectTrue isFalse = (condition, publisher, msgOnError) -> {
        if (condition) {
            AtestLogEvent event = new AtestLogEvent(AtestLogEvent.LogType.OOPS, publisher, msgOnError);
            AtestEvidence evidence = AtestEvidence.getInstance();
            evidence.log(event);
            TestStepData step = evidence.getCurrentScenarioStep();
            step.status = TestStepData.TestStepStatus.TEST_STEP_FAILED;
            step.error = msgOnError;
            step.finishTime = System.currentTimeMillis();
            evidence.failStep(step);
            evidence.finishScenario(step.scenarioName, "failed");
        }
        assertFalse(condition);
    };

    static public String formatLogEvent(AtestLogEvent event) {
        String tag = "?";
        if (event.getLogType() == AtestLogEvent.LogType.INFO) {
            tag = "INFO";
        } else if (event.getLogType() == AtestLogEvent.LogType.OOPS) {
            tag = "OOPS";
        } else if (event.getLogType() == AtestLogEvent.LogType.TRACE) {
            tag = "TRACE";
        }
        return String.format("[%s] %s -%s- %s\n"
                , event.getTimestamp()
                , tag
                , event.getPublisher()
                , event.getMsg());
    }

    static public void wait(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    static public void deleteFolder(String folderPath) {
        File folder = new File(folderPath);
        if (!folder.exists()) return;
        if (!folder.isDirectory()) return;
        File[] files = folder.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    deleteFolder(f.getPath());
                } else {
                    f.delete();
                }
            }
        }
        folder.delete();
    }

    public static String getElapsedTimeFormatted(long startTime, long finishTime) {
        long elapsed = finishTime - startTime;
        SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
        return sdf.format(elapsed);
    }
}
