Feature: Magento Feature

  Scenario: Test
    Given I open "magento" page
    And I maximize window
    And I login with user melike
    And I select Woman element
    And I select Tops element
    And I click Jacket element
    And I select "price" in Sorter
    And I select first product element
    And I click first product compare element
    #And I see compare element count "x"
    And I select medium product element
    And I click medium product compare element
    And I click sorter direction element
    And I select first product element
    And I click first product compare element
    And I click Compare element
    And I select cheapest of product 1, product 2, product 3
    And I click add wish list element
    And I select wish product element
    And I select Gear element
    And I click Fitness Equipment element
    And I click "Sprite Yoga Companion Kit"
    And I click Customize and Add to Cart element
    And I click Sprite Stasis Ball 75 cm element
    And I fill:
      | brick count | 5 |
    And I click Sprite Yoga Strap 8 foot element
    And I fill:
      | sprite count | 2 |
      | roller count | 1 |
    And I click add to cart element

