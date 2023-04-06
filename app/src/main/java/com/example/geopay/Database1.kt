package com.example.geopay

import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import aws.sdk.kotlin.services.dynamodb.model.PutItemRequest
import aws.sdk.kotlin.services.dynamodb.model.DynamoDbException
import aws.sdk.kotlin.services.dynamodb.model.GetItemRequest
import kotlin.system.exitProcess

class Database1 {

    suspend fun putItemInTable2(
        ddb: DynamoDbClient,
        tableNameVal: String,
        offer: String,
        offid: String,
        name: String,
        nameValue: String,
        email: String,
        emailVal: String
    ) {
        val itemValues = mutableMapOf<String, AttributeValue>()

        // Add all content to the table.
        itemValues[offer] = AttributeValue.S(offid)
        itemValues[name] = AttributeValue.S(nameValue)
        itemValues[email] = AttributeValue.S(emailVal)

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