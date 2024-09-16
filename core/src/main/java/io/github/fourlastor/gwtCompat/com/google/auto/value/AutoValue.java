package com.google.auto.value;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.CLASS)
@Target({ElementType.TYPE})
public @interface AutoValue {
    @Retention(RetentionPolicy.CLASS)
    @Target({ElementType.TYPE, ElementType.METHOD})
    public @interface CopyAnnotations {
        Class<? extends Annotation>[] exclude() default {};
    }

    @Retention(RetentionPolicy.CLASS)
    @Target({ElementType.TYPE})
    public @interface Builder {}
}
