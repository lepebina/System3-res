Feature: Stelo

@original-pf-2
  Scenario Outline: Buy with Stelo
    Given I am at "http://netshoes.com.br"
      And I am logged as "Joao" with "qanetshoes.sb@gmail.com" and "abC 123$"
     When I buy the "<PRODUCT>"
     Then I will be redirected to the Stelo login page

    Examples:
    | PRODUCT |
    | produto/trampolim-gonew--C62-0644-006?lid=4:dbmrulecartridge:rc04:c00:t08:p01:d00:9&ppc=3 |
