Feature: Finish Orders

  #NOTE 1) Scenario: "Deliveries with installments".
  #Some of the methods (Agendada, Super Expressa, Super Esportiva)
  #are only shown for specific CEP's, therefore it it important that the logged user has an address that fulfills the
  #requirements for these types of product delivery. In other words, this scenario will not add a new address to assure
  #that the product will have all the desired delivery options.

  #NOTE 2) Scenario : "Buy with two cards and installments"
  #Choose a product that does not have additional options to buy (ie. sun glasses, fitness apparell)

  Background: background
    Given I am at "http://netshoes.com.br" <Finish Orders>
    When I am logged as "teste" with "softboxpf@qa.com.br" and "qa123456"

#Scenario: Buy with 'boleto'
#And buy the "produto/aparelho-abdominal-gonew-super-star-C62-0647-006?"
#And pay with 'boleto'
#Then my order will be finish with a successful message

#----------------------------------------------------------------------------------------------------------------------
#  Scenario Outline: Buy with 'Bundle'
#    And buy the bundle for the product: "<PRODUCT_URL>"
#    And pay with card: "<CARD_HOLDER>", "<CARD_USER_NAME>", "<CARD_NUMBER>", "<CARD_VALIDITY>" and "<CARD_CVV>"
#    Then my order will be finish with a successful message
#    Examples:
#      | CARD_HOLDER | CARD_NUMBER      | CARD_USER_NAME    | CARD_VALIDITY | CARD_CVV | PRODUCT_URL                                    |
#      | Visa        | 4440000000000000 | Testador Netshoes | 05/2018        | 888      | produto/chuteira-adidas-ace-15-4-tf-society-D13-1878-274?                                                                                  |
#      | Visa        | 4440000000000000 | Testador Netshoes | 052018        | 888      | produto/calcao-adidas-squadra-13-132-6126-058? |
#      | Visa        | 4440000000000000 | Testador Netshoes | 052018        | 888      | produto/apoio-p-flexao-de-braco-acte-sports--D56-0056-010? |
#      | Visa        | 4440000000000000 | Testador Netshoes | 052018        | 888      | produto/100-whey-gold-standard-5-lbs--optimum-nutrition-165-7008-433?lid=108:dbmrulecartridge:rc108:c04:t08:p10:l02:t01:3&ppc=5 |
#      | Visa        | 4440000000000000 | Testador Netshoes | 052018        | 888      | produto/tenis-pretorian-knockout-D36-0244-068? |


#----------------------------------------------------------------------------------------------------------------------
#--------- Certifique-se de estar com endereco principal que esteja disponiveis os tipos de entrega: Normal, expressa, agendada, super expressa
#Scenario Outline: Deliveries with installments
#  And buy the "<PRODUCT_URL>"
#  And on payment page select "<DELIVERY_OPTION>"
#  And pay in "<NUM_INSTALLMENTS>" with card: "<CARD_HOLDER>", "<CARD_USER_NAME>", "<CARD_NUMBER>", "<CARD_VALIDITY>" and "<CARD_CVV>"
#  Then my order will be finish with a successful message
#  Examples:
#    | NUM_INSTALLMENTS | CARD_HOLDER | DELIVERY_OPTION | CARD_NUMBER      | CARD_USER_NAME    | CARD_VALIDITY | CARD_CVV | PRODUCT_URL                                              |
#    | 2                | Amex        | Normal          | 349000000000000  | Testador Netshoes | 05/2018       | 8885     | produto/roda-de-exercicios-abdominais-ziva-A32-0265-010? |
#    | 3                | Visa        | Normal        | 4440000000000000 | Testador Netshoes | 05/2018       | 888      | produto/tenis-asics-gel-nimbus-16-020-0728-387 |
#    | 3                | Visa        | Normal        | 4440000000000000 | Testador Netshoes | 05/2018       | 888      | produto/bone-new-era-950-red-bull-blackout-F83-0792-006? |
#    | 3                | Visa        | Agendada        | 4440000000000000 | Testador Netshoes | 05/2018       | 888      | produto/roda-de-exercicios-abdominais-ziva-A32-0265-010? |
#    | 2                | Amex        | Normal          | 349000000000000  | Testador Netshoes | 05/2018       | 8885     | produto/roda-de-exercicios-abdominais-ziva-A32-0265-010? |
#    | 3                | Visa        | Expressa        | 4440000000000000 | Testador Netshoes | 05/2018       | 888      | produto/roda-de-exercicios-abdominais-ziva-A32-0265-010? |
#    | 3                | Visa        | Super Expressa  | 4440000000000000 | Testador Netshoes | 05/2018       | 888      | produto/roda-de-exercicios-abdominais-ziva-A32-0265-010? |
#	|2                | Visa        | Super Esportiva  |4440000000000000 | Testador Netshoes  | 05/2028        | 888      | produto/roda-de-exercicios-abdominais-ziva-A32-0265-010? |



#	Scenario Outline: Buy with two cards and installments.
#	  And buy the "<PRODUCT_URL>"
#	  And on payment page choose pay with two cards
#	  And pay R$ "50.25" in "<NUM_INSTALLMENTS_1>" with card ONE: "<CARD_HOLDER_1>", "<CARD_USER_NAME_1>", "<CARD_NUMBER_1>", "<CARD_VALIDITY_1>" and "<CARD_CVV_1>"
#	  And pay in "<NUM_INSTALLMENTS_2>" with card TWO: "<CARD_HOLDER_2>", "<CARD_USER_NAME_2>", "<CARD_NUMBER_2>", "<CARD_VALIDITY_2>" and "<CARD_CVV_2>"
#	  Then my order will be finish with a successful message
#	  Examples:
#    | NUM_INSTALLMENTS_1 | CARD_HOLDER_1 | CARD_NUMBER_1    | CARD_USER_NAME_1    | CARD_VALIDITY_1 | CARD_CVV_1 | NUM_INSTALLMENTS_2 | CARD_HOLDER_2 | CARD_NUMBER_2    | CARD_USER_NAME_2 | CARD_VALIDITY_2 | CARD_CVV_2 | PRODUCT_URL                                           |
#    | 2                  | Visa          | 4440000000000000 | Robert Bruce Banner | 05/2018         | 888        | 4                  | Master        | 5199000000000000 | Tony Stark       | 10/2020         | 888        | produto/oculos-shimano-s20r--c-2-lentes-827-8963-006? |

#Scenario Outline: Customized products with all cardholders
#  And I customized a "<PRODUCT_URL>" of type "<PRODUCT_TYPE>"
#  And an additional value related to its personalization is shown
#  And pay with card: "<CARD_HOLDER>", "<CARD_USER_NAME>", "<CARD_NUMBER>", "<CARD_VALIDITY>" and "<CARD_CVV>"
#  Then my order will be finish with a successful message
#  Examples:
#    | CARD_HOLDER | CARD_NUMBER      | CARD_USER_NAME      | CARD_VALIDITY | CARD_CVV | PRODUCT_TYPE     | PRODUCT_URL                                                                                    |
#    | Visa        | 4440000000000000 | Testador Netshoes   | 05/2018       | 888      | Ball             | produto/bola-penalty-matis-5-campo-D23-0126-114?lid=74:rulebanner:rc74:c01:t06:p02:d00:8&ppc=1 |
#    | Amex        | 349000000000000  | Robert Bruce Banner | 01/2017       | 5555     | camiseta de time | produto/camisa-nike-selecao-brasil-i-1415-sn--jogador-004-2684-046?                            |
#    | Master      | 5199000000000000 | Testador Netshoes   | 10/2020       | 666      | Cap              | produto/bone-nike-daybreak-004-3240-006                                                        |
#    | Diners      | 3640000000000000 | Mini Me             | 11/2017       | 333      | jacket           | produto/jaqueta-adidas-sst-tt-D13-2401-012?                                                    |
# produto sem estoque produto/camisa-nike-corinthians-i-2015-sn--jogador-D12-1054-028?
#Scenario Outline: Buy with two cards and installments.
#  And buy the "<PRODUCT_URL>"
#  And on payment page choose pay with two cards
#  And pay R$ "50.25" in "<NUM_INSTALLMENTS_1>" with card ONE: "<CARD_HOLDER_1>", "<CARD_USER_NAME_1>", "<CARD_NUMBER_1>", "<CARD_VALIDITY_1>" and "<CARD_CVV_1>"
#  And pay in "<NUM_INSTALLMENTS_2>" with card TWO: "<CARD_HOLDER_2>", "<CARD_USER_NAME_2>", "<CARD_NUMBER_2>", "<CARD_VALIDITY_2>" and "<CARD_CVV_2>"
#  Then my order will be finish with a successful message
#  Examples:
#    | NUM_INSTALLMENTS_1 | CARD_HOLDER_1 | CARD_NUMBER_1    | CARD_USER_NAME_1    | CARD_VALIDITY_1 | CARD_CVV_1 | NUM_INSTALLMENTS_2 | CARD_HOLDER_2 | CARD_NUMBER_2    | CARD_USER_NAME_2 | CARD_VALIDITY_2 | CARD_CVV_2 | PRODUCT_URL                                           |
#    | 2                  | Visa          | 4440000000000000 | Robert Bruce Banner | 05/2018         | 888        | 4                  | Master        | 5199000000000000 | Tony Stark       | 10/2020         | 888        | produto/oculos-shimano-s20r--c-2-lentes-827-8963-006? |

#    Scenario Outline: Rateio
#        When I put in the cart the product with rateio promotion "<PRODUCT_URL>"
#        Then the promotion message is displayed in the header
#        And when I change up to 3 units
#        Then the promotion images are applied and displayed successfully
    
#    Examples:
#      |   PRODUCT_URL                                               |
#      |   produto/camisa-polo-flamengo-shape-basica-D40-0010-016?   |
 
    @original-pf-2
    Scenario Outline: Discount with balance      
        And buy the "<PRODUCT_URL>"
		And I choose my discount method "<DISCOUNT>", "<DISCOUNT_CODE>"
		And pay with card: "<CARD_HOLDER>", "<CARD_USER_NAME>", "<CARD_NUMBER>", "<CARD_VALIDITY>" and "<CARD_CVV>"
		Then my order will be finished with a successful message
		
		Examples:
		| PRODUCT_URL                                                   | DISCOUNT      | DISCOUNT_CODE   | CARD_HOLDER  | CARD_NUMBER        | CARD_USER_NAME       | CARD_VALIDITY | CARD_CVV |
		| produto/camisa-polo-flamengo-shape-basica-D40-0010-016?       | Cupom         | 1@$@$C          |   Visa       |  4440000000000000  | Robert Bruce Banner  | 10/2020       |   888    |      
      