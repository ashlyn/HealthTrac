package com.group7.healthtrac.services.api;

import com.squareup.otto.Bus;

/**
 * Provides a single instance of a bus for use throughout the application.
 * More than one bus cannot be instantiated at the same time in order for
 * the bus to function.
 */
public final class BusProvider {

    private static final Bus BUS = new Bus();

    public static Bus getInstance() {
        return BUS;
    }

}
