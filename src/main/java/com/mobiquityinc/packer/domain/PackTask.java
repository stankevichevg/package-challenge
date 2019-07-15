package com.mobiquityinc.packer.domain;

import java.util.List;

/**
 * Holds given parameters of the packaging task to solve. It's used as an input of packaging algorithm.
 */
public class PackTask {

    private final Double maxWeight;
    private final List<Thing> things;

    public PackTask(Double maxWeight, List<Thing> things) {
        this.maxWeight = maxWeight;
        this.things = things;
    }

    public Double getMaxWeight() {
        return maxWeight;
    }

    public List<Thing> getThings() {
        return things;
    }

}
