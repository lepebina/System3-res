package br.com.softbox.atest_bdd.stepdefs.stelo;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

/**
 *
 */

@RunWith(Cucumber.class)
@CucumberOptions(
        plugin = {"json:../evidence/output/report_data.json", "html:../evidence/output/"}
        , features = {"src/test/resources/feature/stelo.feature"}
        , glue = {"classpath:br/com/softbox/atest_bdd/stepdefs/stelo"}
)
public class SteloTest {
}


