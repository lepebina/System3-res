Feature: Search
  Background:
    Given I am at "http://www.netshoes.com.br/"

@original-pf-2
  Scenario Outline: Pagination Control
     When I make a valid "<SEARCH>"
      And check for page controls "<SHOULD_HAVE_PAGE_CONTROLS>"
     Then listing page should be correctly displayed
      And navigate forwards
      And navigate to the last page, if exist
      And navigate backwards
      And navigate to the first page, if exist
Examples:
      | SEARCH    | SHOULD_HAVE_PAGE_CONTROLS |
      | trampolim | no                        |
      #| tenis     | yes                       |

@original-pf-2
  Scenario Outline: Auto-complete
    When I type part of the "<word>" that I'm looking for
    And click on one of the link suggestions
    Then I'll be redirected successfully to the search page

    Examples:
    | word      |
    |  camis    |


@original-pf-2
  Scenario Outline: Filter with different face types
    When I make a valid "<search>"
    And select the first available filter
    Then the result is filtrated and I remove it
    And select the second available filter
    Then the result is filtrated and I remove it

    Examples:
    | search          |
    |  tenis          |

@original-pf-2
  Scenario Outline: : Back to the top
    When I make a valid "<search>" and scroll down the page
    And click on Back to Top button
    Then the page scrolls up automatically

    Examples:
    | search        |
    |   moletom     |

@original-pf-2
  Scenario Outline: Run a valid search

    When I search for a valid "<item>"
    Then a successful results will be displayed

    Examples:
      | item        |
      | camiseta     |

@original-pf-2
  Scenario Outline: Run an invalid search

    When I search for an invalid "<item>"
    Then a message is shown warning the item does not exist

    Examples:
      | item |
      | safsdfsd     |




