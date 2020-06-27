package com.zjn.mvcframework.annotations;

import java.lang.annotation.*;

/**
 * @author: 190coder <190coder.cn>
 * @description:
 * @create: 2020-06-07 12:07
 */
@Documented
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface MyRequestMapping {
    String value() default "";
}
