package com.cloudslip.usermanagement.core;


import com.mongodb.client.FindIterable;
import org.bson.Document;

interface CursorPreparer {
    FindIterable<Document> prepare(FindIterable<Document> var1);
}
