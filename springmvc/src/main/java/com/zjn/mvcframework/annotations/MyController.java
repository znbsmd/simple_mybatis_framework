package com.zjn.mvcframework.annotations;

import java.lang.annotation.*;

/**
 * @author: 190coder <190coder.cn>
 * @description:
 * @create: 2020-06-07 12:07
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MyController {
    String value() default "";
}
