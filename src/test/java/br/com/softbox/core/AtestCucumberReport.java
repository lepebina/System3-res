package br.com.softbox.core;

import br.com.softbox.utils.Utils;
import com.github.mkolisnyk.cucumber.reporting.CucumberResultsOverview;
import org.apache.commons.io.Charsets;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class AtestCucumberReport {
    private AtestEvidence _evidence = AtestEvidence.getInstance();
    private String _sourceDir = "";
    private String _jquery = "";
    private String _formatter = "";
    private String _report = "";
    private String _css = "";
    private final String _patJQueryJs = "<script src=\"jquery-1.8.2.min.js\"></script>";
    private final String _patFormatterJs = "<script src=\"formatter.js\"></script>";
    private final String _patReportJs = "<script src=\"report.js\"></script>";
    private final String _patCss = "<link href=\"style.css\" rel=\"stylesheet\">";
    private String _reportJson = "";
    private final String _reporterBeautifierFilename = "beautifier";

    public AtestCucumberReport(String testTitle, String evidenceDir) {
        _sourceDir = evidenceDir + File.separator + "output";
        _jquery = _sourceDir + File.separator + "jquery-1.8.2.min.js";
        _formatter = _sourceDir + File.separator + "formatter.js";
        _report = _sourceDir + File.separator + "report.js";
        _css = _sourceDir + File.separator + "style.css";
        _reportJson = _sourceDir + File.separator + "report_data.json";
    }

    public String getReport() {
        try {
            byte[] encodedJQuery = new byte[0];
            encodedJQuery = Files.readAllBytes(Paths.get(_jquery));
            String contentJQuery = new String(encodedJQuery, Charsets.UTF_8);

            byte[] encodedFormatter = new byte[0];
            encodedFormatter = Files.readAllBytes(Paths.get(_formatter));
            String contentFormatter = new String(encodedFormatter, Charsets.UTF_8);

            byte[] encodedReport = new byte[0];
            encodedReport = Files.readAllBytes(Paths.get(_report));
            String contentReport = new String(encodedReport, Charsets.UTF_8);

            byte[] encodedCss = new byte[0];
            encodedCss = Files.readAllBytes(Paths.get(_css));
            String contentCss = new String(encodedCss, Charsets.UTF_8);

            String index = "<!DOCTYPE html><html><head><meta charset=\"utf-8\"><title>Cucumber Features</title>"
                         + "<style type=\"text/css\">" + getCss(contentCss) + "</style>"
                         + "<script type=\"text/javascript\">" + contentJQuery + "</script>"
                         + "<script type=\"text/javascript\">" + contentFormatter + "</script>"
                         + "<script type=\"text/javascript\">" + contentReport + "</script>"
                         + getJavascriptForBeautifier()
                         + "</head><body>" + getOverview() + "<div class=\"cucumber-report\"></div></body></html>";
            //Utils.deleteFolder(_sourceDir);
            return index;
        } catch (IOException e) {
            AtestLog log = new AtestLog("AtestCucumberReport");
            log.info("Unable to generate report. File IO exception: " + e.getMessage());
        }
        return "";
    }

    private String getJavascriptForBeautifier() {
        return "    <script type=\"text/javascript\" src=\"https://www.google.com/jsapi\">\n" +
                "    </script>\n" +
                "    <script type=\"text/javascript\">\n" +
                "//<![CDATA[\n" +
                "\n" +
                "      google.load(\"visualization\", \"1.1\", {packages:[\"corechart\",\"bar\"]});\n" +
                "\t  google.setOnLoadCallback(drawChart);\n" +
                "      function drawChart() {\n" +
                "        var sc_data = google.visualization.arrayToDataTable([\n" +
                "          ['Status', 'Number'],\n" +
                "['Passed', 1], ['Failed', 0], ['Undefined', 0]\n" +
                "        ]);\n" +
                "\n" +
                "        var sc_options = {\n" +
                "          title: 'Scenario Status',\n" +
                "          is3D: true,\n" +
                "\t\t  slices: {\n" +
                "            0: { color: 'green' },\n" +
                "            1: { color: 'red' , offset: 0.1 },\n" +
                "            2: { color: 'silver' , offset: 0.1 }\n" +
                "          },\n" +
                "\t\t  tooltip: {\n" +
                "\t\t  showColorCode: true\n" +
                "\t\t  }\n" +
                "        };\n" +
                "\n" +
                "        var sc_chart = new google.visualization.PieChart(document.getElementById('scenariochart_3d'));\n" +
                "        sc_chart.draw(sc_data, sc_options);\n" +
                "        var feat_data = google.visualization.arrayToDataTable([\n" +
                "          ['Status', 'Number'],\n" +
                "['Passed', 1], ['Failed', 0], ['Undefined', 0]\n" +
                "        ]);\n" +
                "\n" +
                "        var feat_options = {\n" +
                "          title: 'Feature Status',\n" +
                "          is3D: true,\n" +
                "\t\t  slices: {\n" +
                "            0: { color: 'green' },\n" +
                "            1: { color: 'red', offset: 0.1 },\n" +
                "            2: { color: 'silver', offset: 0.1 }\n" +
                "          },\n" +
                "\t\t  tooltip: {\n" +
                "\t\t  showColorCode: true\n" +
                "\t\t  }\n" +
                "        };\n" +
                "\n" +
                "        var feat_chart = new google.visualization.PieChart(document.getElementById('featurechart_3d'));\n" +
                "        feat_chart.draw(feat_data, feat_options);\n" +
                "      }\n" +
                "    \n" +
                "//]]>\n" +
                "    </script>\n" +
                "    <script src=\"https://www.google.com/uds/?file=visualization&amp;v=1.1&amp;packages=bar%2Ccorechart\" type=\"text/javascript\">\n" +
                "    </script>\n" +
                "    <link href=\"https://www.google.com/uds/api/visualization/1.1/44bff6e97aae1761b69c336923d965d7/ui+en.css\" type=\"text/css\" rel=\"stylesheet\"/>\n" +
                "    <script src=\"https://www.google.com/uds/api/visualization/1.1/44bff6e97aae1761b69c336923d965d7/dygraph,webfontloader,format+en,default+en,ui+en,bar+en,corechart+en.I.js\" type=\"text/javascript\">\n" +
                "    </script>\n" +
                "    <link href=\"https://ajax.googleapis.com/ajax/static/modules/gviz/1.1/core/tooltip.css\" rel=\"stylesheet\" type=\"text/css\"/>";
    }

    private String getCss(String moreCss) {
        return  "      h1 {background-color:#9999CC}\n" +
                "h2 {background-color:#BBBBCC}\n" +
                "h3 {background-color:#DDDDFF}\n" +
                "th {border:1px solid black;background-color:#CCCCDD;}\n" +
                "td{border:1px solid black;}\n" +
                "table {border:1px solid black;border-collapse: collapse;}\n" +
                ".chart {border:0px none black;border-collapse: collapse;background-color: none;}\n" +
                ".passed {background-color:lightgreen;font-weight:bold;color:darkgreen}\n" +
                ".failed {background-color:tomato;font-weight:bold;color:darkred}\n" +
                ".undefined {background-color:gold;font-weight:bold;color:goldenrod}\n " + moreCss;
    }
    private String getOverview() {
        CucumberResultsOverview results = new CucumberResultsOverview();
        results.setOutputDirectory(_sourceDir);
        results.setOutputName(_reporterBeautifierFilename);
        results.setSourceFile(_reportJson);
        try {
            results.executeFeaturesOverviewReport();

            final String overviewFilename = _sourceDir + File.separator + _reporterBeautifierFilename + "-feature-overview.html";
            byte[] encoded = Files.readAllBytes(Paths.get(overviewFilename));
            final String overviewContents = new String(encoded, Charsets.UTF_8);

            int idxFeaturesStatus = overviewContents.indexOf("Features Status");
            int idxCloseBody = overviewContents.indexOf("</body>", idxFeaturesStatus);
            return "<h1>" + overviewContents.substring(idxFeaturesStatus, idxCloseBody);
        } catch (Exception e) {
            String x;
            x = e.getMessage();
        }
        return "";
    }
}
