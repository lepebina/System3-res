package br.com.softbox.atest_bdd.stepdefs.fardamento_futebol;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;



@RunWith(Cucumber.class)
@CucumberOptions(
        plugin = {"json:../evidence/output/report_data.json", "html:../evidence/output/"}
        , features = {"src/test/resources/feature/fardamento_futebol.feature"}
        , glue = {"classpath:br/com/softbox/atest_bdd/stepdefs/fardamento_futebol"}
)
public class FardamentoFutebolTest {
}


