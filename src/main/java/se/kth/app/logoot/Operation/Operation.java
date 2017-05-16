package se.kth.app.logoot.Operation;

import se.kth.app.logoot.LineIdentifier;

public class Operation {

    private OperationType type;
    private LineIdentifier id;
    private String content;

    public Operation(OperationType type, LineIdentifier id, String content) {
        this.type = type;
        this.id = id;
        this.content = content;
    }

    public OperationType getType() {
        return type;
    }

    public void setType(OperationType type) {
        this.type = type;
    }

    public LineIdentifier getId() {
        return id;
    }

    public void setId(LineIdentifier id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
