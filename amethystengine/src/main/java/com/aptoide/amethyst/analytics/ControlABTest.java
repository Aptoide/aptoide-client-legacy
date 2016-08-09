package com.aptoide.amethyst.analytics;

import com.aptoide.amethyst.utils.Logger;

public class ControlABTest<T> implements ABTest<T> {

    private String name;
    private T control;

    public ControlABTest(String name, T control) {
        this.name = name;
        this.control = control;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void participate() {
        Logger.d("ControlABTest", "AB test manager not initialized. Participate called in control AB Test: " + name);
    }

    @Override
    public void convert() {
        Logger.d("ControlABTest", "AB test manager not initialized. Convert called in control AB Test: " + name);
    }

    @Override
    public T alternative() {
        return control;
    }

    @Override
    public void prefetch() {
        Logger.d("ControlABTest", "AB test manager not initialized. Prefetch called in control AB Test: " + name);
    }
}
