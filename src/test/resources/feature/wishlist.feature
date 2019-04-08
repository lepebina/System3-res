Feature: wishlist
  Background:
    Given I am at "http://www.netshoes.com.br" <wishlist>

@original-pf-2
  Scenario Outline: Add and remove product - Wishlist logged

    When I log in with "<useremail>" and "<userpassword>"
    And I access my empty wishlist
    And I wish a product through its "<productUrl>"
    Then my products will be displayed successfully in the wishlist
    And I remove it from wishlist
    Then my wishlist will be empty


    Examples:
      | useremail                        | userpassword | productUrl                                                                                                                         |
      |  user_automation@hotmail.com     |  123456      |  produto/tenis-everlast-atlantis-009-0135-852?lid=110:dbmrulecartridge:rc110:c02:t08:p02:d00:1&ppc=2    |

  
  @original-pf-2
  Scenario Outline: Add and remove product - Wishlist not logged

    When I add to the wishlist a product through its "<productUrl>"
    And I put my "<useremail>" and "<userpassword>" in the login page
    Then my product will be displayed successfully in the wishlist
    And I remove it from wishlist
    Then my wishlist will be empty

    Examples:
      | productUrl                                                                                               | useremail                      | userpassword |
      | produto/tenis-everlast-atlantis-009-0135-852?lid=110:dbmrulecartridge:rc110:c02:t08:p02:d00:1&ppc=2     |  user_automation@hotmail.com   |  123456      |





