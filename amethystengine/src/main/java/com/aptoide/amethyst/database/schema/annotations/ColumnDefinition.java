package com.aptoide.amethyst.database.schema.annotations;



import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created with IntelliJ IDEA.
 * User: brutus
 * Date: 04-10-2013
 * Time: 14:51
 * To change this template use File | Settings | File Templates.
 */
@Retention(RetentionPolicy.RUNTIME)

public @interface ColumnDefinition {

    SQLType type();
    String defaultValue() default "";

    // Constraints
    boolean primaryKey() default false;
    boolean notNull() default false;
    boolean unique() default false;
    boolean autoIncrement() default false;

    // OnConflict -- Only works for primary keys, unique and Not nulls fields
    OnConflict onConflict() default OnConflict.NONE;

}
