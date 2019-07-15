package com.mobiquityinc.packer.domain;

import java.util.Objects;

/**
 * Represents one thing which is a candidate to be packed to the result {@link Package}.
 */
public class Thing {

    private final int index;
    private final Double weight;
    private final Double cost;

    public Thing(int index, Double weight, Double cost) {
        this.index = index;
        this.weight = weight;
        this.cost = cost;
    }

    public int getIndex() {
        return index;
    }

    public Double getWeight() {
        return weight;
    }

    public Double getCost() {
        return cost;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Thing thing = (Thing) o;
        return index == thing.index &&
            Objects.equals(weight, thing.weight) &&
            Objects.equals(cost, thing.cost);
    }

    @Override
    public int hashCode() {
        return Objects.hash(index, weight, cost);
    }
}
