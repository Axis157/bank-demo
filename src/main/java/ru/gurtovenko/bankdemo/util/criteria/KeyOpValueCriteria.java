package ru.gurtovenko.bankdemo.util.criteria;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KeyOpValueCriteria {

    private String key;

    private String operation;

    private Object value;

    private static final Pattern MATCHER = Pattern.compile(
            "^" +
                "(.+?);" +
                "(" + String.join("|", KeyOpValueSpecification.SUPPORTED_OPERATORS) + ")" +
                "(;?)" +
                "(.*?)" +
            "$"
    );

    public KeyOpValueCriteria() {
    }

    public KeyOpValueCriteria(String expr) {
        Matcher matcher = MATCHER.matcher(expr);
        if (matcher.find() && matcher.group(1) != null && matcher.group(2) != null) {
            this.key = matcher.group(1);
            this.operation = matcher.group(2);

            if (matcher.group(4) != null) {
                this.value = matcher.group(4);
            }
        } else {
            throw new IllegalArgumentException("Unknown search criteria expr: " + expr);
        }

    }

    public KeyOpValueCriteria(String key, String operation, Object value) {
        this.key = key;
        this.operation = operation;
        this.value = value;
    }


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        KeyOpValueCriteria that = (KeyOpValueCriteria) o;

        if (key != null ? !key.equals(that.key) : that.key != null) return false;
        if (operation != null ? !operation.equals(that.operation) : that.operation != null) return false;
        return value != null ? value.equals(that.value) : that.value == null;
    }

    @Override
    public int hashCode() {
        int result = key != null ? key.hashCode() : 0;
        result = 31 * result + (operation != null ? operation.hashCode() : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "KeyOpValueCriteria{" +
                "key='" + key + '\'' +
                ", operation='" + operation + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
