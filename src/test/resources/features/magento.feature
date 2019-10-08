Feature: Magento Feature

  Scenario: Test
    Given I open "magento" page
    And I maximize window
    And I create a user
    And I select Woman element
    And I select Tops element
    And I click Jacket element
    And I select "price" in Sorter
    And I select first product element
    And I click first product compare element
    And I select medium product element
    And I click medium product compare element
    And I click sorter direction element
    And I select first product element
    And I click first product compare element
    And I click Compare element
    And I select cheapest of product 1, product 2, product 3
    And I click wish product element
    And I click size element
    And I click color element
    And I click product add to cart element

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
    And I click basket element
    And I check final amount is lower than "150"
    And I click proceed to checkout element
    And I select "TR" in Country
    And I fill:
      | Company | Snapbytes - B.K.M.                 |
      | Street  | Akmerkez B Kule Kat 4 Agile 5 Room |
      | City    | Istanbul                           |
      | State   | Besiktas                           |
      | Zip     | 34100                              |
      | Phone   | 02122121212                        |
    And I click Next element
    And I click biling check element
    And I select "TR" in Biling Country
    And I fill:
      | Biling Company | Home              |
      | Biling Street  | Maltepe - Cevizli |
      | Biling City    | Istanbul          |
      | Biling State   | Maltepe           |
      | Biling Zip     | 34150             |
      | Biling Phone   | 02162161616       |
    And I click Update element
    And  I click Place Order element

  Scenario: To Be Failed
    Given I open "magento" page
    And I login with user melike
    And I click Update element


