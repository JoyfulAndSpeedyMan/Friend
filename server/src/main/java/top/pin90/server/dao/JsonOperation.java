package top.pin90.server.dao;

import org.bson.Document;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperationContext;

import java.util.Collections;
import java.util.List;

public class JsonOperation implements AggregationOperation {

    private List<Document> documents;

    private static final String DUMMY_KEY = "dummy";

    public JsonOperation(String json) {
        documents = parseJson(json);
    }

    static final List<Document> parseJson(String json) {
        return (json.startsWith("["))
                ? Document.parse("{\"" + DUMMY_KEY + "\": " + json + "}").getList(DUMMY_KEY, Document.class)
                : Collections.singletonList(Document.parse(json));
    }

    @Override
    public Document toDocument(AggregationOperationContext context) {
        // Not necessary to return anything as we override toPipelineStages():
        return null;
    }

    @Override
    public List<Document> toPipelineStages(AggregationOperationContext context) {
        return documents;
    }
}