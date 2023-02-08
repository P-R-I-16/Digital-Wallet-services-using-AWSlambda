
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

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.AttributeValueUpdate;
import com.amazonaws.services.dynamodbv2.model.ReturnConsumedCapacity;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;
import com.amazonaws.services.dynamodbv2.model.UpdateItemRequest;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;

public class DeLinkAccounttoWalletService implements RequestStreamHandler {

	private AmazonDynamoDB dynamoDBInstance;
	private Regions REGION = Regions.US_EAST_1;

	private void initDynamoDbClient() {
		this.dynamoDBInstance = AmazonDynamoDBClientBuilder.standard().withRegion(REGION).build();
	}
	/* code to delink account from wallet */
	public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
		BufferedReader inputReader = new BufferedReader(new InputStreamReader(input));
		OutputStreamWriter outputWriter = new OutputStreamWriter(output);
		JSONParser inputParser = new JSONParser();
		JSONObject resObj = new JSONObject();

		JSONObject resBody = new JSONObject();
		this.initDynamoDbClient();

		try {
			JSONObject reqObject = (JSONObject) inputParser.parse(inputReader);

			if ("true".equals(reqObject.get("deLink"))) {
				UpdateItemRequest request = new UpdateItemRequest();
				request.setTableName("wallet");
				request.setReturnConsumedCapacity(ReturnConsumedCapacity.TOTAL);
				request.setReturnValues(ReturnValue.UPDATED_OLD);
				/* primary key map */
				Map<String, AttributeValue> pkMap = new HashMap<>();
				pkMap.put("walletId", new AttributeValue(String.valueOf(reqObject.get("walletid"))));
				request.setKey(pkMap);

				/* values tobe updated */
				Map<String, AttributeValueUpdate> valueMap = new HashMap<>();
				valueMap.put("isActive", new AttributeValueUpdate(new AttributeValue("N"), "PUT"));
				request.setAttributeUpdates(valueMap);
				dynamoDBInstance.updateItem(request);

				resBody.put("message", "account delinked successfully");
				resObj.put("statusCode", 200);
				resObj.put("body", resBody.toString());

			} else {
				resBody.put("message", "account delink is false..no status change");
				resObj.put("statusCode", 404);
				resObj.put("body", resBody.toString());
			}

		} catch (Exception e) {
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
