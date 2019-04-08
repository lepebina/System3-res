package br.com.softbox.atest_bdd.stepdefs.multiplus;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

/**
 *
 */


@RunWith(Cucumber.class)
@CucumberOptions(
        plugin = {"json:../evidence/output/report_data.json", "html:../evidence/output/"}
        , features = {"src/test/resources/feature/multiplus.feature"}
        , glue = {"classpath:br/com/softbox/atest_bdd/stepdefs/multiplus"}
)
public class MultiplusTest {
}
