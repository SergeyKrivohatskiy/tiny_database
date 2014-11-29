package ru.spbau.tinydb.expressions.bool;

import org.jetbrains.annotations.NotNull;
import ru.spbau.tinydb.expressions.Expression;

/**
 * @author adkozlov
 */
public abstract class AbstractBooleanExpression<V extends Expression<Boolean>> implements Expression<Boolean> {

    @NotNull
    private final V first;

    public AbstractBooleanExpression(@NotNull V first) {
        this.first = first;
    }

    @NotNull
    public V getFirst() {
        return first;
    }

    @NotNull
    @Override
    public String toString() {
        return getFirst().toString();
    }
}
