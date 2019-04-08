package br.com.softbox.atest_bdd.stepdefs.pdp;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

/**
 *
 */


@RunWith(Cucumber.class)
@CucumberOptions(
        plugin = {"json:../evidence/output/report_data.json", "html:../evidence/output/"}
        , features = {"src/test/resources/feature/pdp.feature"}
        , glue = {"classpath:br/com/softbox/atest_bdd/stepdefs/pdp"}
)
public class PdpTest {
}
