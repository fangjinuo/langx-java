package com.jn.langx.text.stringtemplate;

import com.jn.langx.Formatter;

public interface StringTemplateFormatter extends Formatter<String, String> {
    @Override
    String format(String input, Object... args);
}
