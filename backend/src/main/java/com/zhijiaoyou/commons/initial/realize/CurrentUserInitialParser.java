package com.zhijiaoyou.commons.initial.realize;


import com.zhijiaoyou.commons.constant.HtichConstants;
import com.zhijiaoyou.commons.initial.InitialParser;
import com.zhijiaoyou.commons.initial.annotation.InitialResolver;
import com.zhijiaoyou.commons.utils.RequestUtils;

public class CurrentUserInitialParser implements InitialParser {
    @Override
    public boolean isMatch(Class clazz) {
        return clazz.isAssignableFrom(String.class);
    }

    @Override
    public Object getDefaultValue(Class clazz, InitialResolver initialResolver) {
        return RequestUtils.getCurrentUserId();
    }
}
