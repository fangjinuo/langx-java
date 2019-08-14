package com.jn.langx.util.collect;

import com.jn.langx.util.Emptys;

/**
 * Array tools
 */
public class Arrs {
    /**
     * judge whether an object is an Array
     */
    public static boolean isArray(Object o){
        return Emptys.isNull(o) ? false : o.getClass().isArray();
    }

    /**
     * wrap a string using new String[]{string}
     */
    public static String[] wrapAsArray(String string){
        if(Emptys.isNull(string)){
            return new String[0];
        }
        return new String[]{string};
    }

    /**
     * wrap a number using new String[]{number}
     */
    public static Number[] wrapAsArray(Number number){
        if(Emptys.isNull(number)){
            return new Number[0];
        }
        return new Number[]{number};
    }

    /**
     * wrap a boolean using new String[]{bool}
     */
    public static Boolean[] wrapAsArray(Boolean bool){
        if(Emptys.isNull(bool)){
            return new Boolean[0];
        }
        return new Boolean[]{bool};
    }

    /**
     * Wrap any object using new Object[]{object};
     */
    public static Object[] wrapAsArray(Object o){
        if(Emptys.isNull(o)){
            return new Object[0];
        }
        return new Object[]{o};
    }


}
