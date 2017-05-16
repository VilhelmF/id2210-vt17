package se.kth.app.logoot;

import se.kth.app.logoot.Operation.Operation;

import java.util.List;

public class Patch {

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
}
