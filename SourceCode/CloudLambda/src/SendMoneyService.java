
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.AttributeValueUpdate;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.GetItemResult;
import com.amazonaws.services.dynamodbv2.model.ReturnConsumedCapacity;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;
import com.amazonaws.services.dynamodbv2.model.UpdateItemRequest;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;

public class SendMoneyService implements RequestStreamHandler {

	private AmazonDynamoDB dynamoDBInstance;
	private String DYNAMODB_TABLE_NAME_2 = "Transaction";
	private Regions REGION = Regions.US_EAST_1;

	private void initDynamoDbClient() {
		this.dynamoDBInstance = AmazonDynamoDBClientBuilder.standard().withRegion(REGION).build();
	}

	public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
		BufferedReader inputReader = new BufferedReader(new InputStreamReader(input));
		OutputStreamWriter outputWriter = new OutputStreamWriter(output);
		JSONParser inputParser = new JSONParser();
		JSONObject resObj = new JSONObject();

		JSONObject resBody = new JSONObject();
		this.initDynamoDbClient();

		try {
			JSONObject reqObject = (JSONObject) inputParser.parse(inputReader);
			String from_acct = String.valueOf(reqObject.get("walletid"));
			String to_account = String.valueOf(reqObject.get("accountNo"));
			String txnId = String.valueOf(System.nanoTime());
			double currentBalance = 0.0;
			GetItemRequest request = new GetItemRequest();
			request.setTableName("wallet");
			request.setReturnConsumedCapacity(ReturnConsumedCapacity.TOTAL);
			request.setProjectionExpression("account_no,WalletBalance");
			request.setConsistentRead(true);
			Map<String, AttributeValue> keysMap = new HashMap<>();
			keysMap.put("walletId", new AttributeValue(String.valueOf(reqObject.get("walletid"))));
			request.setKey(keysMap);

			GetItemResult result = dynamoDBInstance.getItem(request);

			if (result.getItem() != null) {

				for (Entry<String, AttributeValue> entry : result.getItem().entrySet()) {
					if (entry.getKey().equals("account_no"))
						from_acct = entry.getValue().getS();
					else if (entry.getKey().equals("WalletBalance")) {
						currentBalance = Double.valueOf(entry.getValue().getS());
					}
				}
			}

			if (currentBalance - Double.valueOf(String.valueOf(reqObject.get("amount"))) >= 0) {
				if (reqObject.get("mobileNo") != null && !(reqObject.get("mobileNo").toString().isEmpty())) {

					// check if mobile no present in wallet table

					GetItemRequest request2 = new GetItemRequest();
					request2.setTableName("walletAccount");
					request2.setReturnConsumedCapacity(ReturnConsumedCapacity.TOTAL);
					request2.setProjectionExpression("account_no");
					request2.setConsistentRead(true);

					Map<String, AttributeValue> keysMap1 = new HashMap<>();
					keysMap1.put("mobileNo", new AttributeValue(String.valueOf(reqObject.get("mobileNo"))));
					request2.setKey(keysMap1);

					GetItemResult result1 = dynamoDBInstance.getItem(request2);

					if (result.getItem() != null) {
						to_account = String.valueOf(result1.getItem().get("account_no"));
						if (result.getItem() != null) {
							for (Entry<String, AttributeValue> entry : result.getItem().entrySet()) {
								to_account = entry.getValue().getS();
							}
						}
						AmazonSQS sqsClient = AmazonSQSClientBuilder.standard().withRegion(Regions.US_EAST_1)
								.withCredentials(new DefaultAWSCredentialsProviderChain()).build();
						resBody.put("message", reqObject.toJSONString());
						resBody.put("from_account", from_acct);
						resBody.put("to_account", to_account);
						resBody.put("transactionid", txnId);
						resObj.put("body", resBody.toString());

						sqsClient.sendMessage("Payment", resObj.toJSONString());
						// send msg to q
					}

					// fetch account and prepare payment msg and send it to SQS

				} else {
					AmazonSQS sqsClient = AmazonSQSClientBuilder.standard().withRegion(Regions.US_EAST_1)
							.withCredentials(new DefaultAWSCredentialsProviderChain()).build();
					resBody.put("message", reqObject.toJSONString());
					resBody.put("from_account", from_acct);
					resBody.put("to_account", to_account);
					resBody.put("transactionid", txnId);
					resObj.put("body", resBody.toString());
					sqsClient.sendMessage("Payment", resObj.toJSONString());
					// take acct details and prepare payment msg
				}
//insert into transaction table and update balance

				Map<String, AttributeValue> walletMap = new HashMap<>();
				walletMap.put("transactionid", new AttributeValue(txnId));
				walletMap.put("amount", new AttributeValue(String.valueOf(reqObject.get("amount"))));
				walletMap.put("amount_currency", new AttributeValue(String.valueOf(reqObject.get("amountCurrency"))));
				long millis = System.currentTimeMillis();
				java.sql.Date date = new java.sql.Date(millis);
				walletMap.put("date", new AttributeValue(String.valueOf(date)));
				walletMap.put("from_account", new AttributeValue(from_acct));
				walletMap.put("to_account", new AttributeValue(to_account));
				walletMap.put("type", new AttributeValue("debit"));
				walletMap.put("remarks", new AttributeValue(String.valueOf(reqObject.get("remarks"))));
				dynamoDBInstance.putItem(DYNAMODB_TABLE_NAME_2, walletMap);

				// update balance

				UpdateItemRequest request1 = new UpdateItemRequest();

				request1.setTableName("wallet");

				request1.setReturnConsumedCapacity(ReturnConsumedCapacity.TOTAL);

				request1.setReturnValues(ReturnValue.UPDATED_OLD);

				Map<String, AttributeValue> keysMap1 = new HashMap<>();
				keysMap1.put("walletId", new AttributeValue(String.valueOf(reqObject.get("walletid"))));
				request1.setKey(keysMap1);

				Map<String, AttributeValueUpdate> map1 = new HashMap<>();
				currentBalance = currentBalance - Double.valueOf(String.valueOf(reqObject.get("amount")));
				map1.put("WalletBalance",
						new AttributeValueUpdate(new AttributeValue(String.valueOf(currentBalance)), "PUT"));
				request1.setAttributeUpdates(map1);

				dynamoDBInstance.updateItem(request1);

				// return successful response
				resBody.put("message", "payment initated linked successfully");
				resBody.put("transactionid", txnId);
				resObj.put("statusCode", 200);
				resObj.put("body", resBody.toString());

			} else {
				resBody.put("message", "insufficient funds in the wallet");
				resObj.put("statusCode", 404);
				resObj.put("body", resBody.toString());
			}
		} catch (IOException e) {
			e.printStackTrace();
			resBody.put("message", e.getLocalizedMessage());
			resObj.put("statusCode", 404);
			resObj.put("body", resBody.toString());
		} catch (ParseException e) {

			e.printStackTrace();
			resBody.put("message", e.getLocalizedMessage());
			resObj.put("statusCode", 404);
			resObj.put("body", resBody.toString());
		}
		outputWriter.write(resObj.toString());

		inputReader.close();

		outputWriter.close();

	}

}
