package com.jn.langx.invocation.aop;

import com.jn.langx.invocation.ConstructorInvocation;

public interface ConstructorInterceptor extends InvocationInterceptor<ConstructorInvocation> {
    @Override
    Object intercept(ConstructorInvocation invocation) throws Throwable;
}
