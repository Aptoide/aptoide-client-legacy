package com.aptoide.amethyst.analytics;

/**
 * Created by marcelobenites on 6/9/16.
 */
public interface ABTest<T> {
    String getName();

    void participate();

    void convert();

    T alternative();

    void prefetch();
}
