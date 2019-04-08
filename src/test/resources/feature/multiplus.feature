Feature: Multiplus

@original-pf-2
  Scenario Outline: Multiplus
    Given I am at "http://www.netshoes.com.br" <multiplus>
    And I log in with "<username>" and "<password>" to finish my payment with a "<multiplus product>"
    When I click to receive multiplus
    Then a successful message is display
    And when I click on multiplus policies
    Then a new page is shown with multiplus policies

    Examples:
     |username                   | password | multiplus product                                                                                                                      |
     |user_automation@hotmail.com| 123456   | produto/tenis-mizuno-wave-impetus-2-149-0444-074?lid=108:dbmrulecartridge:rc108:c01:t08:p02:d00:1&ppc=1     |


