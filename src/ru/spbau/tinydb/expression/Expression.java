package ru.spbau.tinydb.expression;

public interface Expression {

    public boolean check(Object[] row);
}
