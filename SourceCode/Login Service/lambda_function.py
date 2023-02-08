import json
import boto3
dynamodb = boto3.resource('dynamodb')
table =dynamodb.Table('register')

def lambda_handler(event, context):
    
    id = event['userid']
    password = event['password']
    
    
    key = {
        'userid': id
    }
    resp = table.get_item(Key=key)
    pwd = resp['Item']['password']
    uid = resp['Item']['userid']
    if uid and (password==pwd):
        
        return {
            'statusCode': 200,
            'body': json.dumps('Login Success')
        }
    else:
        
        return {
            'statusCode': 200,
            'body': json.dumps('Invalid Credentials, Kindly check the userid and password')
        }