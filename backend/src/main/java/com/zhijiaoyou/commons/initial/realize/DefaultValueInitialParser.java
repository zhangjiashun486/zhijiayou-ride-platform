package com.zhijiaoyou.commons.initial.realize;


import com.zhijiaoyou.commons.initial.InitialParser;
import com.zhijiaoyou.commons.initial.annotation.InitialResolver;
import com.zhijiaoyou.commons.utils.reflect.ReflectUtils;

public class DefaultValueInitialParser implements InitialParser {
    @Override
    public boolean isMatch(Class clazz) {
        return ReflectUtils.isBasicTypes(clazz);
    }

    @Override
    public Object getDefaultValue(Class clazz, InitialResolver initialResolver) {
        Object defValue = null;
        try {
            defValue = ReflectUtils.getDefValue(clazz, initialResolver.def());
        } catch (Exception e) {

        }
        return defValue;
    }
}
