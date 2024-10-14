package common;

public class Error {

    private int lineNumber;
    ErrorType type;

    public Error(int lineNumber, ErrorType type) {
        this.lineNumber = lineNumber;
        this.type = type;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public String toString() {
        return lineNumber + " " + type.toString() + "\n";
    }
}
