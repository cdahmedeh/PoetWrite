package net.cdahmedeh.poetwrite.annotation;

/**
 * For code that is still an incredible mess and that I haven't tested enough on
 * enough to determine how and where to optimize it.
 */
public @interface Draft {
    public String value() default "";
}
