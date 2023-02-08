package com.api.model;

import com.google.gson.Gson;


public class TransactionResponse {
	
private String date;
private String from_account;
private String to_account;
private String amount;
private String amount_currency;
private String remarks;
private String type;




public TransactionResponse(String date, String from_account, String to_account, String amount, String amount_currency,
		String remarks, String type) {
	super();
	this.date = date;
	this.from_account = from_account;
	this.to_account = to_account;
	this.amount = amount;
	this.amount_currency = amount_currency;
	this.remarks = remarks;
	this.type = type;
}
public TransactionResponse(String json) {
	Gson gson = new Gson();
	TransactionResponse tempProduct = gson.fromJson(json, TransactionResponse.class);
	this.date = tempProduct.date;
	this.from_account = tempProduct.from_account;
	this.to_account = tempProduct.to_account;
	
	this.amount = tempProduct.amount;
	this.amount_currency = tempProduct.amount_currency;
	this.remarks = tempProduct.remarks;
	this.type = tempProduct.type;
	
}


public String toString() {
	return new Gson().toJson(this);
}





public String getDate() {
	return date;
}
public void setDate(String date) {
	this.date = date;
}
public String getFrom_account() {
	return from_account;
}
public void setFrom_account(String from_account) {
	this.from_account = from_account;
}
public String getTo_account() {
	return to_account;
}
public void setTo_account(String to_account) {
	this.to_account = to_account;
}
public String getAmount() {
	return amount;
}
public void setAmount(String amount) {
	this.amount = amount;
}
public String getAmount_currency() {
	return amount_currency;
}
public void setAmount_currency(String amount_currency) {
	this.amount_currency = amount_currency;
}
public String getRemarks() {
	return remarks;
}
public void setRemarks(String remarks) {
	this.remarks = remarks;
}
public String getType() {
	return type;
}
public void setType(String type) {
	this.type = type;
}





}
