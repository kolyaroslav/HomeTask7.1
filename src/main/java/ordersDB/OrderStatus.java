package ordersDB;

public enum OrderStatus {
    DONE("done"), ACTIVE("active"), CANCELLED("cancelled");

    private String v;

    OrderStatus(String v) {
        this.v = v;
    }

    public String getV() {
        return v;
    }

    @Override
    public String toString() {
        return v;
    }
}