package com.zjn.mvcframework.annotations;

import java.lang.annotation.*;

/**
 * @author: 190coder <190coder.cn>
 * @description:
 * @create: 2020-06-07 12:06
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MyAutoWired {

    String value() default "";
}
