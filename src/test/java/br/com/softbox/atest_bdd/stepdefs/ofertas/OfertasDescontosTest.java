package br.com.softbox.atest_bdd.stepdefs.ofertas;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

/**
 *
 */


@RunWith(Cucumber.class)
@CucumberOptions(
        plugin = {"json:../evidence/output/report_data.json", "html:../evidence/output/"}
        , features = {"src/test/resources/feature/registerOfertasDescontos.feature"}
        , glue = {"classpath:br/com/softbox/atest_bdd/stepdefs/ofertas"}
)
public class OfertasDescontosTest {
}
