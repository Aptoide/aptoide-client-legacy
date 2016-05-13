package com.aptoide.amethyst.database.schema.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created with IntelliJ IDEA.
 * User: brutus
 * Date: 09-10-2013
 * Time: 15:50
 * To change this template use File | Settings | File Templates.
 */

@Retention(RetentionPolicy.RUNTIME)

public @interface TableDefinition {

    String[] primaryKey() default {};

    Composite_Unique[] uniques() default {};

    Index[] indexes() default {};


    @Retention(RetentionPolicy.RUNTIME)
    public @interface Index {
        String index_name();
        boolean unique() default false;
        Key[] keys();
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface Key {
        String field();
        boolean descending() default false;
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface Composite_Unique {
        String[] fields();
    }

}
