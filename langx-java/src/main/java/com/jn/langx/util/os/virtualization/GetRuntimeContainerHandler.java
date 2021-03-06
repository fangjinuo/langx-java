package com.jn.langx.util.os.virtualization;

import com.jn.langx.commandline.streamhandler.OutputExtractExecuteStreamHandler;

abstract class GetRuntimeContainerHandler extends OutputExtractExecuteStreamHandler {
    /**
     * 获取当前运行在的容器，不限定docker容器
     *
     * @return
     */
    public abstract RuntimeContainer getContainer();

}
