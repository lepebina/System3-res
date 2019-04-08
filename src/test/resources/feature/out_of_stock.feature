  Feature: Product Size is Out Of Stock
@original-pf-2
  Scenario Outline: Tell me about out of stock product
    Given I am at "http://netshoes.com.br" <Out Of Stock>
    When visit a product with size out of stock at this relative path: "<PRODUCT_URL>"
    And select first size out of stock
    Then Contact me as "Alexander Miro" at "testador.site.netshoes@gmail.com" when it is available again
    Examples:
      |PRODUCT_URL|
      |produto/tenis-cocacola-primal-kick-summer-598-0122-026?|
      | produto/tenis-diadora-walker-043-9107-937?            |
      | produto/camiseta-oakley-skull-shades-D63-0284-008?    |
#      |produto/chuteira-kappa-viento-society-D24-0172-260?|
#      |produto/tenis-converse-all-star-ct-as-core-ox-047-0114-006?|
#      |produto/tenis-cocacola-primal-kick-summer-598-0122-026?|

#    Scenario Outline: Tell me about out of stock product 2
#      Given I am at "http://netshoes.com.br" <Out Of Stock>
#      When visit a product with size out of stock at this relative path: "<PRODUCT_URL>"
#      And select first size out of stock
#      Then Contact me as "Alexander Miro" at "testador.site.netshoes@gmail.com" when it is available again
#      Examples:
#        |PRODUCT_URL|
#        |produto/tenis-puma-match-vulc-025-3426-810?|
