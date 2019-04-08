Feature: Shopping cart
  Background:
    Given I am at "http://www.netshoes.com.br" <shopping_cart>
    And free shipping price is "R$ 99,90"

@original-pf-2
  Scenario Outline: Verify if product is added to the cart
    When I login with "<user>" and "<password>"
    And navigate to the following product "<product_url>"
    Then I add the product successfully to the shopping cart
    Examples:
    |   user                          |  password | product_url |
    |    user_automation@hotmail.com  |  123456   | produto/chuteira-nike-tiempo-rio-2-ic-futsal-004-4261-244? |
    
@original-pf-2
Scenario Outline: Frete Gratis
  When I calculate the shipping value for product "<PRODUCT_URL>" and CEP "<CEP>"
  Then verify if the shipping value is "<SHIPPING_PRICE>"
  And Delivery date is displayed: "<DELIVERY_DATE>"

  Examples:
    |PRODUCT_URL                  |CEP                  |DELIVERY_DATE      | SHIPPING_PRICE       |
   |produto/chuteira-umbro-gear-society-D21-0286-120?     | 38408216           | 7 a 9             | R$ 9,90   |
#    |produto/chuteira-umbro-gear-society-D21-0286-120?     | 38400642           | 7 a 9             | NOT FREE  |
#   |produto/chuteira-umbro-gear-society-D21-0286-120?     | 38408216           | 7 a 9             | FREE      |