package com.example.geopay

import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import aws.sdk.kotlin.services.dynamodb.model.PutItemRequest
import aws.sdk.kotlin.services.dynamodb.model.DynamoDbException
import aws.sdk.kotlin.services.dynamodb.model.GetItemRequest
import kotlin.system.exitProcess

class Database {

    suspend fun putItemInTable2(
        ddb: DynamoDbClient,
        tableNameVal: String,
        name: String,
        nameValue: String,
        email: String,
        emailVal: String,
        mobile: String,
        number: String,
        password: String,
        pass: String
    ) {
        val itemValues = mutableMapOf<String, AttributeValue>()

        // Add all content to the table.
        itemValues[name] = AttributeValue.S(nameValue)
        itemValues[email] = AttributeValue.S(emailVal)
        itemValues[mobile]= AttributeValue.S(number)
        itemValues[password]= AttributeValue.S(pass)

        val request = PutItemRequest {
            tableName=tableNameVal
            item = itemValues
        }


        try {
            ddb.putItem(request)
            println(" A new item was placed into $tableNameVal.")

        } catch (ex: DynamoDbException) {
            println(ex.message)
            ddb.close()
            exitProcess(0)
        }
    }
}