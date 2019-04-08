Feature: Manages Pessoa Fisica

#----------------------------------------------------------------------------------------------------------------------
 #@target-for-H1 
 @original-pf-1
  Scenario: Pessoa Fisica (PF) - For checking confirmation e-mail (not a random email)
    Given I am at "http://netshoes.com.br" <PF>
     When I create a user with name "Softbox" and surname "Back November" and "softbox.ns.blackfriday2015@gmail.com"
      And change my name to "Netshoes", surname to "Back Friday", gender to "F", phone to "(34) 3232-3232" and cellphone to "(34)9191-9191"
      And change my password to "netshoeS 123#" and do a logout and login again
      And add shipping address to CEP "06460-908"
      And put into the cart the product "produto/bone-nike-sb-icon-004-6866-244?" choosing another shipping address for PF
      And buy with boleto PF
      And delete one of my shipping addresses
     Then all of my previous steps were done successfully

#----------------------------------------------------------------------------------------------------------------------

 @original-pf-1
   Scenario: Pessoa Fisica (account registration)-29
   Given i access "http://netshoes.com.br"
   When i register new account with name as "Softbox", surname as "Back November", and email as "softbox.ns.blackfriday2015@gmail.com"
   And update my account with name as "Netshoes", surname as "BackFriday"
   And add other information like gender as female, phone as "(34) 3232-3232", cellphone as "(34) 9191-9191", and shipping address as CEP "06460-908"
   And add account security by putting password as "netshoeS 123"
   And logout and login again.
   And choose another shipping address for PF
   And with boleto PF, buy the product "produto/bone-nike-sb-icon-004-6866-244?" 
   And delete one of my shipping addresses
   Then all of my previous steps were done successfully
 
 #@chosen
#@target
 @original-pf-1
  Scenario: Pessoa Fisica (PF)
   Given I am at "http://netshoes.com.br" <PF>
   When I create a user with name "Joao" and surname "Pitbull da Silva"
   And change my name to "Maria", surname to "Florentina", gender to "F", phone to "(34) 3232-3232" and cellphone to "(34)9191-9191"
   And change my password to "netshoeS 123#" and do a logout and login again
   And add shipping address to CEP "06460-908"
   And put into the cart the product "produto/bone-nike-sb-icon-004-6866-244?" choosing another shipping address for PF
   And buy with boleto PF
   And delete one of my shipping addresses
   Then all of my previous steps were done successfully
   
  # @injected
   @original-pf-1
   Scenario: Pessoa Fisica (PF)-27
   Given I access web address "http://netshoes.com.br" <PF>
   When I register with a first name "Joao" and last name "Pitbull da Silva"
   And change my first name to "Maria" and last name to "Florentina", gender to "F", phone to phone to "(34) 3232-3232" and cellphone to "(34)9191-9191"
   And reset my passwort to "netshoeS 123#" 
   Then I logout and login again
    And add missing shipping address as  CEP "06460-908"
   Then add to the cart a product with SKU "produto/bone-nike-sb-icon-004-6866-244?" and chose another address to ship for PF
   And purchase with boleto PF
   And delete one of my shipping addresses
   Then all previous actions are done successfully
    
    #@injected
     @original-pf-1
    Scenario: Pessoa Fisica (PF)-24
    Given I am browsing "http://netshoes.com.br" site <PF>
    When I register an account with "Joao" as the name and "Pitbull da Silva" as the surname
    And I update my particulars with new details to "Maria" as the name, "Florentina" as surname, "Female" as gender, "(34)3232-3232" as Telephone and "(34)9191-9191" as mobile contact
    And have my login password modified to "netshoeS 123#", sign out from the account and then signing in
    And register CEP "06460-908" as a shipping address
    And purchase item "produto/bone-nike-sb-icon-004-6866-244?" to be shipped to another address for PF
    And use boleto PF to make a purchase
    And have one of my shipping addresses removed
    Then my previous operations were all completed with success
