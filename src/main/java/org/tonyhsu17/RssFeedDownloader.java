package org.tonyhsu17;

import java.io.IOException;
import java.util.Optional;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.tonyhsu17.utilities.Logger;
import org.tonyhsu17.utilities.commandline.CommandLineArgs;
import org.tonyhsu17.utilities.commandline.Parameter;



public class RssFeedDownloader implements Logger {
    private RunHeadlessMode headless;
    private int cronInterval;
    private boolean hasCron;

    public RssFeedDownloader(String[] args) {
        try {
            CommandLine cmd = CommandLineArgs.getCommandLine(Params.getParams(), args);
            String url = getOptionValue(cmd, Params.U, "RSS_URL", "");
            String dest = getOptionValue(cmd, Params.D, "RSS_DES", "");
            hasCron = !cmd.hasOption(Params.ONCE.opt()) || Boolean.parseBoolean(
                Optional.ofNullable(System.getenv("RSS_USE_CRON")).orElse("false"));
            cronInterval = Integer.parseInt(getOptionValue(cmd, Params.I, "RSS_CRON_INTERVAL", "10"));
            if(!url.isEmpty() && !dest.isEmpty()) {
                headless = new RunHeadlessMode(url, dest);
            }
            else {
                CommandLineArgs.printHelp("rss-feed-downloader.jar", Params.params);
                System.exit(0);
            }
        }
        catch (ParseException | NumberFormatException | IOException e) {
            error(e);
            System.exit(1);
        }
    }

    public String getOptionValue(CommandLine cmd, Parameter param, String sysEnvKey, String defaultValue) {
        String val = null;
        if(cmd.hasOption(param.opt())) {
            info("Using arg[" + param.opt() + "]");
            val = cmd.getOptionValue(param.opt());
        }
        else if(cmd.hasOption(param.longOpt())) {
            info("Using arg[" + param.longOpt() + "]");
            val = cmd.getOptionValue(param.longOpt());
        }
        else if(System.getenv(sysEnvKey) != null) {
            info("Using env[" + sysEnvKey + "]");
            val = System.getenv(sysEnvKey);
        }
        else if(defaultValue != null) {
            info("Using default value for arg[" + param.opt() + "]");
            val = defaultValue;
        }
        else {
            info("Nothing found for arg[" + param.opt() + "]");
        }
        return val;
    }

    public void run() throws IOException {
        if(!hasCron) {
            headless.run();
        } else {
            while(true) {
                headless.run();
                try {
                    Thread.sleep(cronInterval);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
