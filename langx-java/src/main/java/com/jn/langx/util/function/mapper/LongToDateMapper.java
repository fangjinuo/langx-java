package com.jn.langx.util.function.mapper;

import com.jn.langx.util.converter.ConverterService;
import com.jn.langx.util.function.Mapper;

import java.util.Date;

public class LongToDateMapper implements Mapper<Long, Date> {
    @Override
    public Date apply(Long value) {
        return ConverterService.DEFAULT.convert(value, Date.class);
    }
}
