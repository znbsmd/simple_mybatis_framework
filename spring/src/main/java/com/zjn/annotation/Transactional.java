package com.zjn.annotation;

import java.lang.annotation.*;

/**
 * @author zjn
 * @create 2020-06-02 09:49
 * @description
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Transactional {

    String value() default "";


    String transactionManager() default "";


    String[] label() default {};


    String timeoutString() default "";


    boolean readOnly() default false;


    Class<? extends Throwable>[] rollbackFor() default {};


    String[] rollbackForClassName() default {};


    Class<? extends Throwable>[] noRollbackFor() default {};


    String[] noRollbackForClassName() default {};

}

