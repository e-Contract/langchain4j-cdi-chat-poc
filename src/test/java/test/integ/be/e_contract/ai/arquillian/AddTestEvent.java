package test.integ.be.e_contract.ai.arquillian;

public class AddTestEvent {

    private final int a;

    private final int b;

    private final int result;

    public AddTestEvent(int a, int b, int result) {
        this.a = a;
        this.b = b;
        this.result = result;
    }

    public int getA() {
        return this.a;
    }

    public int getB() {
        return this.b;
    }

    public int getResult() {
        return this.result;
    }
}
