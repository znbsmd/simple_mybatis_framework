package com.zjn.mvcframework.annotations;

import java.lang.annotation.*;

/**
 * @author: 190coder <190coder.cn>
 * @description:
 * @create: 2020-06-09 21:35
 */
@Documented
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface MySecurity {

    String[] value() default {};
}
