package com.jn.langx.commandline;

import java.io.InputStream;
import java.io.OutputStream;

public class ProcessAdapter implements InstructionSequence {
    private Process process;

    public ProcessAdapter(Process process) {
        this.process = process;
    }

    @Override
    public OutputStream getOutputStream() {
        return process.getOutputStream();
    }

    @Override
    public InputStream getInputStream() {
        return process.getInputStream();
    }

    @Override
    public InputStream getErrorStream() {
        return process.getErrorStream();
    }

    public void destroy() {
        process.destroy();
    }

    @Override
    public int waitFor()throws InterruptedException {
        return process.waitFor();
    }

    @Override
    public int exitValue() {
        return process.exitValue();
    }
}
