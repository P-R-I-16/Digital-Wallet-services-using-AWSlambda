# Digital-Wallet-services-using-lambda
Register Service
File-walletapi\src\main\java\com\wallet\aws\walletapi\RegisterAPI.java
This service is used to register user with wallet.


Login Service:
File-Login Service\lambda_function.py
This service is used verify whether the user is already registered or not.
If the User is registered and provide correct credentials, then Login is Successful or else Login is Invalid.


Add Money Service:
File-Add Money Service\lambda_function.py
This service is used add money to WalletID
It will create a credit type transaction and will update the balance of wallet every time the service is called.


Link Account to Wallet Service 

File-CloudLambda\src\LinkAccounttoWalletService.java
This service is used to link bank account to wallet and generate WalletID


Send Money Service


File-CloudLambda\src\SendMoneyService.java
This service is used to transfer money from wallet


DeLink Account to Wallet Service 

File-CloudLambda\src\DeLinkAccounttoWalletService.java
This service is used to delink bank account from wallet


Transaction History Service
File-transaction\src\main\java\com\api\aws\TransactionRequestAPI.java
This service is used to get the details of a particular transaction


To test the above Services using Postman.
1.	Deploy the each service code as Lambda function in AWSLambda
2.	Create API and integrate Lambda function in the API and Deploy API 
3.	Copy the URL 
4.	Use the URL to call the Lambda function in Postman
