Feature: PDP
  Background:
    Given I am at "http://www.netshoes.com.br" <pdp>

@original-pf-2
  Scenario Outline: Verify if a product's Video view option works correctly
    When I access the product that contains video at this relative path: "<PRODUCT_URL>"
    And click in the video icon
    Then video frame will be displayed

    Examples:
      | PRODUCT_URL             |
      | produto/jogo-copa-do-mundo-da-fifa-brasil-2014-ps3-624-0016-092?lid=17:dbmrulecartridge:rc17:c04:t08:p10:l01:t01:2&ppc=4 |
      | produto/624-0018-114-01 |



@original-pf-2
  Scenario Outline: Table size
    When I access a shoes through its url "<URL>"
    And navigate through the size table options
    Then all selected brands should be displayed correctly

    Examples:
    | URL                                                                                               |
    | produto/bota-gonew-maximum-C62-0548-006?lid=110:dbmrulecartridge:rc110:c02:t08:p02:d00:1&ppc=1    |



@original-pf-2
  Scenario Outline: Power Reviews
    When I access a product with power reviews comments through its url "<URL>"
    And go to the power reviews section
    Then power reviews information should be displayed correctly

    Examples:
    | URL                                                                                                       |
    | produto/tenis-nike-capri-3-low-lthr-004-7608-240?lid=110:dbmrulecartridge:rc110:c02:t08:p02:d00:1&ppc=2   |




