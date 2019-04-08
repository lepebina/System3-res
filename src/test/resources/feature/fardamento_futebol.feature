Feature: Fardamento Futebol

  Background: background
    Given I am at "http://netshoes.com.br"
    When I am logged as "alexander" with "softbox.miro@gmail.com" and "abC 123#"
@original-pf-2
Scenario Outline: Buy 'Fardamento Futebol Salao' with credit card (Simplified)
    And set Futsal team name as "SB Netshoes"
    And pay with card: "<CARD_HOLDER>", "<CARD_USER_NAME>", "<CARD_NUMBER>", "<CARD_VALIDITY>" and "<CARD_CVV>"
   Then my order will be finish with a successful message
Examples:
      | CARD_HOLDER | CARD_NUMBER      | CARD_USER_NAME    | CARD_VALIDITY | CARD_CVV |
      | Visa        | 4440000000000000 | Testador Netshoes | 05/2018       | 888      |

