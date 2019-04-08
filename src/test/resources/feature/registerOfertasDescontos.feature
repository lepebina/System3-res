Feature: Ofertas e Descontos

@original-pf-2
  Scenario Outline: Registrar ofertas e descontos

    Given I am at "http://www.netshoes.com.br" <registerOfertasDescontos>
    When I subscribe with a new random "<email>"
    Then a successful message will be displayed

    Examples:
    |email        |
    |@hotmail.com |
    |@gmail.com   |
    |@msn.com     |



