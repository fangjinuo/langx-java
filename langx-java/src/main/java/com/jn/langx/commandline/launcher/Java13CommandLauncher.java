package com.jn.langx.commandline.launcher;

import com.jn.langx.commandline.CommandLine;
import com.jn.langx.commandline.ProcessAdapter;
import com.jn.langx.commandline.environment.EnvironmentUtils;

import java.io.File;
import java.io.IOException;
import java.util.Map;


/**
 * A command launcher for JDK/JRE 1.3 (and higher). Uses the built-in
 * Runtime.exec() command
 */
public class Java13CommandLauncher extends LocalCommandLauncher {

    /**
     * Constructor
     */
    public Java13CommandLauncher() {
    }

    /**
     * Launches the given command in a new process, in the given working
     * directory
     *
     * @param cmd        the command line to execute as an array of strings
     * @param env        the environment to set as an array of strings
     * @param workingDir the working directory where the command should run
     * @throws IOException probably forwarded from Runtime#exec
     */
    @Override
    public ProcessAdapter exec(final CommandLine cmd, final Map<String, String> env,
                               final File workingDir) throws IOException {

        final String[] envVars = EnvironmentUtils.toStrings(env);

        return new ProcessAdapter(Runtime.getRuntime().exec(cmd.toStrings(),
                envVars, workingDir));
    }
}
