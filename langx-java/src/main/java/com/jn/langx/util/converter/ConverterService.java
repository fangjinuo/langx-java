package com.jn.langx.util.converter;


import com.jn.langx.Converter;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.exception.ValueConvertException;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.Maps;
import com.jn.langx.util.function.Function;
import com.jn.langx.util.function.Predicate2;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConverterService {
    public static final ConverterService DEFAULT = new ConverterService();
    /**
     * key: target class
     * value:
     */
    private static final Map<Class, Converter> BUILTIN = new HashMap<Class, Converter>();

    static {
        BUILTIN.put(Byte.class, ByteConverter.INSTANCE);
        BUILTIN.put(byte.class, ByteConverter.INSTANCE);
        BUILTIN.put(Short.class, ShortConverter.INSTANCE);
        BUILTIN.put(short.class, ShortConverter.INSTANCE);
        BUILTIN.put(Integer.class, IntegerConverter.INSTANCE);
        BUILTIN.put(int.class, IntegerConverter.INSTANCE);
        BUILTIN.put(Long.class, LongConverter.INSTANCE);
        BUILTIN.put(long.class, LongConverter.INSTANCE);
        BUILTIN.put(Float.class, FloatConverter.INSTANCE);
        BUILTIN.put(float.class, FloatConverter.INSTANCE);
        BUILTIN.put(Double.class, DoubleConverter.INSTANCE);
        BUILTIN.put(double.class, DoubleConverter.INSTANCE);
        BUILTIN.put(boolean.class, BooleanConverter.INSTANCE);
        BUILTIN.put(Boolean.class, BooleanConverter.INSTANCE);
    }

    /**
     * 没有 source 类型，只有 target 类型
     */
    private final Map<Class, Converter> target_registry = new ConcurrentHashMap<Class, Converter>(BUILTIN);
    /**
     * 有 source 类型，也有 target 类型
     * key： target class
     * sub key: source class
     */
    private ConcurrentHashMap<Class, ConcurrentHashMap<Class, Converter>> target_source_registry = new ConcurrentHashMap<Class, ConcurrentHashMap<Class, Converter>>();

    public void register(@NonNull Class targetClass, @Nullable Class sourceClass, @NonNull Converter converter) {
        if (sourceClass == null) {
            register(targetClass, converter);
            return;
        }
        Preconditions.checkNotNull(targetClass);
        Preconditions.checkNotNull(converter);
        ConcurrentHashMap<Class, Converter> map = Maps.putIfAbsent(target_source_registry, targetClass, new Function<Class, ConcurrentHashMap<Class, Converter>>() {
            @Override
            public ConcurrentHashMap<Class, Converter> apply(Class input) {
                return new ConcurrentHashMap<Class, Converter>();
            }
        });
        map.put(sourceClass, converter);
    }

    public void register(@NonNull Class targetClass, @NonNull Converter converter) {
        Preconditions.checkNotNull(targetClass);
        Preconditions.checkNotNull(converter);
        target_registry.put(targetClass, converter);
    }

    public <T> T convert(@Nullable Object obj, @NonNull Class<T> targetClass) {
        Converter converter = findConverter(obj, targetClass);

        if (converter == null) {
            if (obj == null) {
                return null;
            }
            throw new ValueConvertException(StringTemplates.formatWithPlaceholder("Can't cast {} to {}", obj, targetClass));
        }
        return (T) converter.apply(obj);
    }

    /**
     * 根据 source 是否 为 null 自行决定从哪个registry
     *
     * @param source
     * @param targetClass
     * @param <S>
     * @param <T>
     * @return
     */
    public <S, T> Converter<S, T> findConverter(@Nullable S source, @NonNull Class<T> targetClass) {
        Preconditions.checkNotNull(targetClass);
        if (source == null) {
            return target_registry.get(targetClass);
        }

        Converter converter = findConverter(source.getClass(), targetClass);
        if (converter == null) {
            converter = findConverter(null, targetClass);
        }
        if (converter == null && source != null) {
            if (source.getClass() == targetClass) {
                return NoopConverter.INSTANCE;
            }
        }
        return converter;
    }

    /**
     * 从 target-source-registry 中找
     *
     * @param sourceClass
     * @param targetClass
     * @param <S>
     * @param <T>
     * @return
     */
    public <S, T> Converter<S, T> findConverter(@NonNull Class<S> sourceClass, @NonNull final Class<T> targetClass) {
        Preconditions.checkNotNull(sourceClass);
        Preconditions.checkNotNull(targetClass);

        ConcurrentHashMap<Class, Converter> mapping = target_source_registry.get(targetClass);
        if (mapping != null) {
            Converter converter = mapping.get(sourceClass);
            if (converter != null) {
                return converter;
            }
            Map.Entry<? extends Class, ? extends Converter> entry = Collects.findFirst(mapping, new Predicate2<Class, Converter>() {
                @Override
                public boolean test(Class sourceClass, Converter converter) {
                    return converter.isConvertible(sourceClass, targetClass);
                }
            });
            if (entry != null) {
                return entry.getValue();
            }
        }
        return null;
    }

}
