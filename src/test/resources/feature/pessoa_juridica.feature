
Feature: Manages Pessoa Juridica

#----------------------------------------------------------------------------------------------------------------------
 @original-pf-1
  Scenario: Pessoa Juridica (PJ) - For checking confirmation e-mail (not a random email)
    Given I am at "http://netshoes.com.br" <PJ>
     When I create a company with 'razao social' as "Qualquercoisa" and 'nome fantasia' as "Fadiga Sports" and "softbox.ns.pj01@gmail.com"
      And change 'razao social' to "OutraCoisa", 'nome fantasia' as "Trincado Fitness", phone to "(34) 3232-3232" and cellphone to "(34)99300 - 1010"
      And change the company password to "netshoeS 123#" and do a logout and login again
      And add this CEP "06460-908" for shipping
      And put into the cart the product "produto/rolo-p-exercicios-acte-sports-509-0087-150?" choosing another shipping address for PJ
      And buy with boleto PJ
      And delete one of the shipping addresses
     Then all of the previous steps were done successfully


#----------------------------------------------------------------------------------------------------------------------
#@chosen
#@target
 @original-pf-1
  Scenario: Pessoa Juridica (PJ)
    Given I am at "http://netshoes.com.br" <PJ>
     When I create a company with 'razao social' as "Qualquercoisa" and 'nome fantasia' as "Fadiga Sports"
      And change 'razao social' to "OutraCoisa", 'nome fantasia' as "Trincado Fitness", phone to "(34) 3232-3232" and cellphone to "(34)99300 - 1010"
      And change the company password to "netshoeS 123#" and do a logout and login again
      And add this CEP "06460-908" for shipping
      And put into the cart the product "produto/rolo-p-exercicios-acte-sports-509-0087-150?" choosing another shipping address for PJ
      And buy with boleto PJ
      And delete one of the shipping addresses
     Then all of the previous steps were done successfully
     
     #@injected
     @original-pf-1
     Scenario: Pessoa Juridica (PJ)-29
     Given I have access to internet
     And opened "http://netshoes.com.br" <PJ>
     When i create a company with the details as 'razao social' as "Qualquercoisa" and 'nome fantasia' as "Fadiga Sports"
     And add this CEP "06460-908" for shipping 
     And i update'razao social' to "OutraCoisa" and 'nome fantasia' as "Trincado Fitness", phone to "(34) 3232-3232" and cellphone to "(34)99300 - 1010"
     And put into the cart the product "produto/rolo-p-exercicios-acte-sports-509-0087-150?" choosing another shipping address for PJ
     And buy with boleto PJ
     Then all of the previous steps were done successfully
     
     #@injected
     @original-pf-1
     Scenario: Pessoa Juridica (PJ)-27
    Given I access web address "http://netshoes.com.br" <PJ>
    When I register a companies with 'razao social' as "Qualquercoisa" and 'nome fantasia' as "Fadiga Sports"
    And change 'razao social' to "Maria" and 'nome fantasia'  to "Trincado Fitness", phone to phone to "(34) 3232-3232" and cellphone to "(34)99300 - 1010"
    And reset change the company password to "netshoeS 123#"
    Then I logout and login again
    And add missing shipping address as   CEP "06460-908"
    Then add to the cart a product with SKU "produto/rolo-p-exercicios-acte-sports-509-0087-150?" and chose another address to ship for PJ
    And purchase with boleto PJ
    And delete one of my shipping addresses
    Then all previous actions are done successfully
     
     #@injected
     @original-pf-1
     Scenario: Pessoa Juridica (PJ)-24
     Given I am navigating "http://netshoes.com.br" site <PJ>
     When I register companies "razao social" and "nome fantasia" as "Qualquercoisa" "Fadiga Sports" respectively
     And modify details for "razao social" to "OutraCoisa", "nome fantasia" modified to "Trincado Fitness", new contact phone as "(34)3232-3232", and "(34)99300-1010" as a mobile number
     And have the company passcode changed to "netshoeS 123#" and sign out and re-sign in.
     And provide shipping details as "06460-908"
     And add item "produto/rolo-p-exercicios-acte-sports-509-0087-150?" into the cart to be shipped for PJ to a different address.
     And use boleto PJ to make a purchase
     And have one of the shipping addresses removed
     Then the previous actions were all successfully finalized    

