package com.mobiquityinc.packer.domain;

import java.util.List;

/**
 * Represents built package of things. It's used as a result of packaging algorithm.
 */
public class Package {

    private final List<Thing> things;

    public Package(List<Thing> things) {
        this.things = things;
    }

    public List<Thing> getThings() {
        return things;
    }
}
