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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Operation operation = (Operation) o;

        if (type != operation.type) return false;
        if (id != null ? !id.equals(operation.id) : operation.id != null) return false;
        return content != null ? content.equals(operation.content) : operation.content == null;

    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (content != null ? content.hashCode() : 0);
        return result;
    }
}
