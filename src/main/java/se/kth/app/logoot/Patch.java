package se.kth.app.logoot;

import se.kth.app.logoot.Operation.Operation;
import se.sics.kompics.KompicsEvent;

import java.io.Serializable;
import java.util.List;

public class Patch implements KompicsEvent, Serializable {

    private static final long serialVersionUID = -5669431159047202367L;

    private int id;
    private List<Operation> operations;
    private int degree;

    public Patch(int id, List<Operation> operations, int degree) {
        this.id = id;
        this.operations = operations;
        this.degree = degree;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Operation> getOperations() {
        return operations;
    }

    public void setOperations(List<Operation> operations) {
        this.operations = operations;
    }

    public int getDegree() {
        return degree;
    }

    public void setDegree(int degree) {
        this.degree = degree;
    }

    public void incrementDegree() {
        this.degree++;
    }

    public void decrementDegree() {
        this.degree--;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Patch patch = (Patch) o;

        if (id != patch.id) return false;
        if (degree != patch.degree) return false;
        return operations != null ? operations.equals(patch.operations) : patch.operations == null;

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (operations != null ? operations.hashCode() : 0);
        result = 31 * result + degree;
        return result;
    }
}
