package com.aptoide.amethyst.utils;

import android.support.annotation.NonNull;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

public class CacheKeyFactory {

    @NonNull
    public String create(String key) {
        try {
            return AptoideUtils.Algorithms.computeSHA1sum(key);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }
}