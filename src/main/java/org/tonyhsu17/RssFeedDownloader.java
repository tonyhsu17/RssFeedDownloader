package org.tonyhsu17;

import java.io.IOException;
import java.util.Optional;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.tonyhsu17.utilities.commandline.CommandLineArgs;


public class RssFeedDownloader {
    public static void main(String[] args) throws IOException, ParseException {
        CommandLine cmd = CommandLineArgs.getCommandLine(Params.getParams(), args);
        String url = cmd.getOptionValue(Params.U.opt(), Optional.ofNullable(System.getenv("RSS_URL")).orElse(""));
        String dest = cmd.getOptionValue(Params.D.opt(), Optional.ofNullable(System.getenv("RSS_DES")).orElse(""));
        boolean hasCron = !cmd.hasOption(Params.ONCE.opt()) || Boolean.parseBoolean(Optional.ofNullable(System.getenv("RSS_USE_CRON")).orElse("false"));
        int cronInterval = Integer.parseInt(cmd.getOptionValue(Params.I.opt(), Optional.ofNullable(System.getenv("RSS_CRON_INTERVAL")).orElse("10")));
        if(!url.isEmpty() && !dest.isEmpty()) {
            if(!hasCron) {
                new RunHeadlessMode(url, dest).run();
            }
            else {
                RunHeadlessMode headless = new RunHeadlessMode(url, dest);
                while(true) {
                    System.out.println("Running...");
                    headless.run();
                    try {
                        Thread.sleep(cronInterval * 1000);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        else {
            System.out.println("Failed to run");
        }
    }
}
