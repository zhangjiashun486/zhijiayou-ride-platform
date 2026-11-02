package com.zhijiaoyou.commons.initial.annotation;

import com.zhijiaoyou.commons.enums.InitialResolverType;
import com.zhijiaoyou.commons.groups.Group;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
@Documented
public @interface InitialResolver {
    InitialResolverType resolver();

    Class<?>[] groups() default Group.All.class;

    //默认值
    String def() default "";
}
