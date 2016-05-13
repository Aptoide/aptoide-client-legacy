package com.aptoide.amethyst.utils;

public class Filters {

    public enum Screen {
        notfound, small, normal, large, xlarge;

        public static Screen lookup(String screen) {
            try {
                return valueOf(screen);
            } catch (Exception e) {
                return notfound;
            }
        }
    }

    public enum Age {
        All, Mature;

        public static Age lookup(String age) {
            try {
                return valueOf(age);
            } catch (Exception e) {
                return All;
            }
        }
    }
}