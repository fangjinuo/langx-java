package com.jn.langx.security.prevention.injection;

import com.jn.langx.util.collection.Collects;

import java.util.List;

public class CommandLineInjectionPreventionHandler extends InjectionPreventionHandler{
    private final List<String> DEFAULT_REMOVED_SYMBOLS = Collects.asList(
            "&", "|","||", ";", "$", "%", "-", "(", ")", "`"
    );

    @Override
    public List<String> getBlacklist() {
        List<String> blacklist = super.getBlacklist();
        return blacklist == null ? DEFAULT_REMOVED_SYMBOLS : blacklist;
    }
}
