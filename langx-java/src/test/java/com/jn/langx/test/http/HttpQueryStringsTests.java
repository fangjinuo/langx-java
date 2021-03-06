package com.jn.langx.test.http;

import com.jn.langx.http.HttpQueryStrings;
import com.jn.langx.util.collection.StringMap;
import com.jn.langx.util.collection.multivalue.MultiValueMap;
import com.jn.langx.util.io.Charsets;
import com.jn.langx.util.net.UrlEncoder;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

public class HttpQueryStringsTests {
    @Test
    public void testHttpQueryString1() {
        String url = "localhost:8888/licenses/_search?containsData=false&productName=opslinkcenter&productVersion=1.1.0&productName=bes#tag1";
        StringMap stringMap = HttpQueryStrings.getQueryStringStringMap(url);
        System.out.println(stringMap);
    }

    @Test
    public void testHttpQueryString2() throws UnsupportedEncodingException {
        String url = "localhost:8888/licenses/_search?containsData=false&productName=opslink center&productVersion=1.1.0&productName=bes#tag1";

        MultiValueMap<String, String> multiValueMap = HttpQueryStrings.getQueryStringMultiValueMap(url);

        String s = HttpQueryStrings.toQueryString(multiValueMap);
        String encodedUrl = URLEncoder.encode(s, Charsets.UTF_8.name());
        System.out.println("JDK URLEncoder encoded:        " + encodedUrl);
        String encodedUrl2 = new UrlEncoder().encode(s, Charsets.UTF_8);
        System.out.println("langx-java URLEncoder encoded: " + encodedUrl2);

        String decodedURL = URLDecoder.decode(encodedUrl, Charsets.UTF_8.name());
        System.out.println("JDK URLDecoder decoded:        " + decodedURL);
        String decodedURL2 = URLDecoder.decode(encodedUrl2, Charsets.UTF_8.name());
        System.out.println("langx-java URLDecoder decoded: " + decodedURL2);
        System.out.println(multiValueMap);


    }


}
