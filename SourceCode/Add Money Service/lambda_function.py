import json
import boto3
dynamodb = boto3.resource('dynamodb')
table =dynamodb.Table('Transaction')
tablew =dynamodb.Table('wallet')
def lambda_handler(event, context):
    # TODO implement
    table.put_item(Item=event)
    
    acct = event['to_account']
    amount = event['amount']
    
    
    key = {
        'walletId': acct
    }
    
    resp = tablew.get_item(Key=key)
    currentbalance=resp['Item']['WalletBalance']
    c=int(currentbalance)
    
    
    update_bal = c + amount
    
    
    # update
    resp['Item']['WalletBalance'] = str(update_bal)
    
    # put (idempotent)
    tablew.put_item(Item=resp['Item'])
    print(resp['Item'])
    return {
        'statusCode': 200,
        'body': json.dumps('Money is Added Successful')
    } 
    
    
   