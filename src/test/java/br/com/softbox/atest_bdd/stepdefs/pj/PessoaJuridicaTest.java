package br.com.softbox.atest_bdd.stepdefs.pj;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

/**
 *
 */

@RunWith(Cucumber.class)
@CucumberOptions(
        plugin = {"json:../evidence/output/report_data.json", "html:../evidence/output/"}
        , features = {"src/test/resources/feature/pessoa_juridica.feature"}
        , glue = {"classpath:br/com/softbox/atest_bdd/stepdefs/pj"}
)
public class PessoaJuridicaTest {
}
