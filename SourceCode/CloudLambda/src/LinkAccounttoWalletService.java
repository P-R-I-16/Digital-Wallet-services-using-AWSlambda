
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;

public class LinkAccounttoWalletService implements RequestStreamHandler {

	private AmazonDynamoDB dynamoDBInstance;

	private String DYNAMODB_TABLE_NAME = "wallet";
	private String DYNAMODB_TABLE_NAME_1 = "walletAccount";
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
			BankAcct acctDtls = new BankAcct();
			if (reqObject.get("accountNo") != null) {
				acctDtls.setAccountNo(String.valueOf(reqObject.get("accountNo")));
			}
			if (reqObject.get("bankName") != null) {
				acctDtls.setBankName(String.valueOf(reqObject.get("bankName")));
			}
			if (reqObject.get("ifsccode") != null) {
				acctDtls.setIfsccode(String.valueOf(reqObject.get("ifsccode")));
			}
			if (reqObject.get("accountType") != null) {
				acctDtls.setAccountType(String.valueOf(reqObject.get("accountType")));
			}
			if (reqObject.get("mobileNo") != null) {
				acctDtls.setMobileNo(String.valueOf(reqObject.get("mobileNo")));
			}

			// call a bank acct verification API
			if (FakeBankAPICall.verifyBankAcct(acctDtls)) {
				// link acct to wallet
				String walletID = generateWalletID(String.valueOf(reqObject.get("accountNo")));

				// store data in wallet table
				Map<String, AttributeValue> walletMap = new HashMap<>();
				walletMap.put("walletId", new AttributeValue(walletID));
				walletMap.put("account_no", new AttributeValue(String.valueOf(reqObject.get("accountNo"))));
				walletMap.put("bank_name", new AttributeValue(String.valueOf(reqObject.get("bankName"))));
				walletMap.put("ifsc_code", new AttributeValue(String.valueOf(reqObject.get("ifsccode"))));
				walletMap.put("acct_type", new AttributeValue(String.valueOf(reqObject.get("accountType"))));
				walletMap.put("mobileno", new AttributeValue(String.valueOf(reqObject.get("mobileNo"))));
				walletMap.put("WalletBalance", new AttributeValue("0"));
				walletMap.put("isActive", new AttributeValue("Y"));
				dynamoDBInstance.putItem(DYNAMODB_TABLE_NAME, walletMap);

				Map<String, AttributeValue> walletAcctMap = new HashMap<>();
				walletAcctMap.put("walletId", new AttributeValue(walletID));
				walletAcctMap.put("mobileNo", new AttributeValue(String.valueOf(reqObject.get("mobileNo"))));
				walletAcctMap.put("account_no", new AttributeValue(String.valueOf(reqObject.get("accountNo"))));
				dynamoDBInstance.putItem(DYNAMODB_TABLE_NAME_1, walletAcctMap);

				// return successful response
				resBody.put("message", reqObject.get("accountNo") + "account linked successfully");
				resBody.put("walletId", walletID);
				resObj.put("statusCode", 200);
				resObj.put("body", resBody.toString());
			} else {
				// raise error
				resBody.put("message", "invalid account details");

				resObj.put("statusCode", 404);

				resObj.put("body", resBody.toString());
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resBody.put("message", e.getLocalizedMessage());
			resObj.put("statusCode", 404);
			resObj.put("body", resBody.toString());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resBody.put("message", e.getLocalizedMessage());
			resObj.put("statusCode", 404);
			resObj.put("body", resBody.toString());
		}
		outputWriter.write(resObj.toString());

		inputReader.close();

		outputWriter.close();

	}

	private String generateWalletID(String acctNo) {
		return acctNo + "XX" + System.nanoTime();
	}

}
