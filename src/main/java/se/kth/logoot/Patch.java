package se.kth.logoot;

import se.kth.logoot.Operation.Operation;

import java.util.List;

public class Patch {

    private List<Operation> operations;
    private int degree;

    public Patch(List<Operation> operations, int degree) {
        this.operations = operations;
        this.degree = degree;
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
