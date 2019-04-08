Feature: Subpage
  Background:
    Given I am at "http://www.netshoes.com.br" <sub_page>

@original-pf-2
  Scenario Outline: Verify if sub page loads correctly
    When I access "<SUB_URL>"
    Then the page is loaded correctly

    Examples:
    |   SUB_URL     |
    |sub/futebol    |
    #|sub/nike       |
    #|sub/chuteira   |
    #|sub/corinthians|

@original-pf-2
  Scenario Outline: Verify if 'ops' message is displayed
    When I access "<Invalid_URL>"
    Then the page displays an 'ops' message
    Examples:
    | Invalid_URL |
    | /sdafsdfsd   |