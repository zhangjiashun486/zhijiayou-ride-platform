package com.zhijiaoyou.commons.initial;

import com.zhijiaoyou.commons.initial.annotation.InitialResolver;

public interface InitialParser {

    boolean isMatch(Class clazz);


    Object getDefaultValue(Class clazz, InitialResolver initialResolver);
}
