# Digital-Wallet-services-using-lambda
##Introduction
Digital wallet will help users to add money to the wallet to use it anywhere and
anytime and keep track of the transaction records. We will be using AWS Lambda, which will
make this application serverless. This serverless feature will provide availability and scalability
to the project. The project's basic workflow is as follows: a user submits a request to the
AmazonAPI Gateway restful service, which transfers the user's information to a specific AWS
Lambda function based on the user's input to retrieve the data from the Amazon Dynamo Database. Our
project aims to build a digital wallet to help users perform operations like storing money in the
wallet, conducting transactions, and fetching recorded transactions.

##The feature offered by wallet applications are as follows: 

• Creating a user's account on the wallet application: - there is a login and register api for user to
create their account. To create the account, the user should fill up the registration request. The
field includes phone no/email address, user first name, user last name, and password.
• Adding or linking a bank account to the application: -This api will help to add and link your
bank account to the wallet. For linking or adding the account, the user should enter the following
details: Account no, Bank Name, IFSC code, and Account type.
• Sending money to the recipient: This api should let the user send money to the recipient
irrespective of whether the recipient is using wallet services. If the recipient is a user, provide a
find option to search for the recipient. If the recipient is not using the wallet, let the user send
money by using the account details of the recipient
• Adding money to the user's wallet: Adding the money from the bank to the wallet
• Recording transactions of the user in the wallet: Store all the transactions made by the wallet
• Displaying the transaction details of the user: Display monthly transaction history
• Delinking the bank account from the wallet: Removing the Bank Account from the wallet.

##App Architecture

![image](https://user-images.githubusercontent.com/100308683/217440373-cfad9e56-0f51-4cb1-bfeb-6f9ceee01175.png)



##Services Created
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
