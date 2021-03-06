package com.jn.langx.commandline.launcher;

import com.jn.langx.commandline.CommandLine;
import com.jn.langx.commandline.InstructionSequence;
import com.jn.langx.commandline.ProcessAdapter;
import com.jn.langx.commandline.environment.EnvironmentUtils;

import java.io.File;
import java.io.IOException;
import java.util.Map;


/**
 * A command launcher for a particular JVM/OS platform. This class is a general
 * purpose command launcher which can only launch commands in the current
 * working directory.
 */
public abstract class LocalCommandLauncher implements CommandLauncher<ProcessAdapter> {

    public ProcessAdapter exec(final CommandLine cmd, final Map<String, String> env)
            throws IOException {
        final String[] envVar = EnvironmentUtils.toStrings(env);
        return new ProcessAdapter(Runtime.getRuntime().exec(cmd.toStrings(), envVar));
    }

    public abstract ProcessAdapter exec(final CommandLine cmd, final Map<String, String> env, final File workingDir) throws IOException;

    /**
     * @see com.jn.langx.commandline.launcher.CommandLauncher#isFailure(int)
     */
    public boolean isFailure(final int exitValue) {
        // non zero exit value signals failure
        return exitValue != 0;
    }

}
