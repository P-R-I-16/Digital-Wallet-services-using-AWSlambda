package com.api.aws;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.api.model.TransactionResponse;


public class TransactionRequestAPI implements RequestStreamHandler{
	private String DYNAMO_TABLE = "Transaction";
	@SuppressWarnings("unchecked")
	@Override
	public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
		
	OutputStreamWriter writer = new OutputStreamWriter(output);
	BufferedReader reader = new BufferedReader(new InputStreamReader(input));
	JSONParser parser = new JSONParser(); // this will help us parse the request object
	JSONObject responseObject = new JSONObject(); // we will add to this object for our api response
	JSONObject responseBody = new JSONObject();// we will add the item to this object
	
	AmazonDynamoDB client = AmazonDynamoDBClientBuilder.defaultClient();
	DynamoDB dynamoDB = new DynamoDB(client);
	
	String transactid; 
	Item resItem = null;
	
	try {
		JSONObject reqObject = (JSONObject) parser.parse(reader);
		//pathParameters
		if (reqObject.get("transactionid")!=null) {
			//JSONObject pps = (JSONObject)reqObject.get("transactionid");
			//if (pps.get("id")!=null) {
			transactid = (String)reqObject.get("transactionid");
				resItem = dynamoDB.getTable(DYNAMO_TABLE).getItem("transactionid",transactid);
				
			}
		
		//queryStringParameters
	/*	else if (reqObject.get("queryStringParameters")!=null) {
			JSONObject qps =(JSONObject) reqObject.get("queryStringParameters");
			if (qps.get("id")!=null) {
				id= Integer.parseInt((String)qps.get("id"));
				resItem = dynamoDB.getTable(DYNAMO_TABLE).getItem("id",id);
				
			}
		}*/
		
		if (resItem!=null) {
			TransactionResponse t1 = new TransactionResponse(resItem.toJSON());
			responseBody.put("Transaction", t1);
			responseObject.put("statusCode", 200);
		}else {
			responseBody.put("message", "No Items Found");
			responseObject.put("statusCode", 404);
		}
		
		responseObject.put("body", responseBody.toString());
		
	} catch (Exception e) {
		context.getLogger().log("ERROR : "+e.getMessage());
	}
	writer.write(responseObject.toString());
	reader.close();
	writer.close();
	
}
}