package top.pin90.server.dao;
import org.bson.Document;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperationContext;

public class CustomAggregationOperation implements AggregationOperation {
    private String jsonOperation;

    public CustomAggregationOperation(String jsonOperation) {
        this.jsonOperation = jsonOperation;
    }
    @Override
    public Document toDocument(AggregationOperationContext context) {
        return Document.parse(jsonOperation);
//        return context.getMappedObject(Document.parse(jsonOperation));
    }
}
