package com.wallet.aws.walletapi;

import java.util.HashMap;

import java.util.Map;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;


import model.RegisterRequest;
import model.RegisterResponce;


public class RegisterAPI implements RequestHandler<RegisterRequest, RegisterResponce> {

    private AmazonDynamoDB amazonDynamoDB;

    private String DYNAMODB_TABLE_NAME = "register";
    private Regions REGION = Regions.US_EAST_1;

   
    

	@Override
	public RegisterResponce handleRequest(RegisterRequest input, Context context) {
		// TODO Auto-generated method stub
        this.initDynamoDbClient();
        
        if(this.equals(input)) {
        	 RegisterResponce requestResponse = new RegisterResponce();
             requestResponse.setMessage("data more");
             return requestResponse;
        }
        
        else {
        	persistData(input);

            RegisterResponce requestResponse = new RegisterResponce();
            requestResponse.setMessage("Saved Successfully registered!!!");
            return requestResponse;
        }
        
	}
	
	private void persistData(RegisterRequest request) throws ConditionalCheckFailedException {

        Map<String, AttributeValue> attributesMap = new HashMap<>();

        attributesMap.put("userid", new AttributeValue(request.getUserid()));
        attributesMap.put("email", new AttributeValue(request.getEmail()));
        attributesMap.put("firstname", new AttributeValue(request.getFirstname()));
        attributesMap.put("lastname", new AttributeValue(request.getLastname()));
        attributesMap.put("password", new AttributeValue((request.getPassword())));
        attributesMap.put("phonenumber", new AttributeValue(request.getPhonenumber()));

        amazonDynamoDB.putItem(DYNAMODB_TABLE_NAME, attributesMap);
    }

    private void initDynamoDbClient() {
        this.amazonDynamoDB = AmazonDynamoDBClientBuilder.standard()
            .withRegion(REGION)
            .build();
    }

	
}




























/*
 * public class RegisterAPI implements RequestHandler {
 * 
 * 
 * private AmazonDynamoDB amazonDynamoDB;
 * 
 * private String DYNAMODB_TABLE_NAME = "register"; private Regions REGION =
 * Regions.US_EAST_1;
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * @Override public Object handleRequest(Object input, Context context) { //
 * TODO Auto-generated method stub this.initDynamoDbClient();
 * 
 * persistData(input);
 * 
 * RegisterResponce personResponse = new RegisterResponce();
 * personResponse.setMessage("Saved Successfully!!!"); return personResponse; }
 * 
 * 
 * 
 * private void persistData(PersonRequest personRequest) throws
 * ConditionalCheckFailedException {
 * 
 * Map<String, AttributeValue> attributesMap = new HashMap<>();
 * 
 * attributesMap.put("id", new
 * AttributeValue(String.valueOf(personRequest.getId())));
 * attributesMap.put("firstName", new
 * AttributeValue(personRequest.getFirstName())); attributesMap.put("lastName",
 * new AttributeValue(personRequest.getLastName())); attributesMap.put("age",
 * new AttributeValue(String.valueOf(personRequest.getAge())));
 * attributesMap.put("address", new AttributeValue(personRequest.getAddress()));
 * 
 * amazonDynamoDB.putItem(DYNAMODB_TABLE_NAME, attributesMap); }
 * 
 * private void initDynamoDbClient() { this.amazonDynamoDB =
 * AmazonDynamoDBClientBuilder.standard() .withRegion(REGION) .build(); } }
 */