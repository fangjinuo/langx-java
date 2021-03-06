package com.jn.langx.test.text.i18n;

import com.jn.langx.text.i18n.JdkResourceBundleI18nMessageStorage;
import com.jn.langx.text.i18n.LocaleCode;
import org.junit.Test;

public class JdkI18nTests {

    @Test
    public void test() {
        JdkResourceBundleI18nMessageStorage messageStorage = new JdkResourceBundleI18nMessageStorage();
        String message = messageStorage.getMessage("jdk_bundle_test", LocaleCode.zh.toLocale(), "every_second");
        System.out.println(message);

        message = messageStorage.getMessage("jdk_bundle_test", LocaleCode.zh.toLocale(), "between_weekday_description_format","x","y");
        System.out.println(message);

        message = messageStorage.getMessage("jdk_bundle_test", LocaleCode.zh.toLocale(), "test_dolar_variable", "01","2020");
        System.out.println(message);
    }

}
