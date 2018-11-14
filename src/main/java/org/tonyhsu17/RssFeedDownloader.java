package org.tonyhsu17;

import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.tonyhsu17.utilities.commandline.CommandLineArgs;


public class RssFeedDownloader {
    public static void main(String[] args) throws IOException, ParseException {
        CommandLine cmd = CommandLineArgs.getCommandLine(Params.getParams(), args);

        if(cmd.hasOption(Params.D.opt()) && cmd.hasOption(Params.U.opt())) {
            String url = cmd.getOptionValue(Params.U.opt());
            String dest = cmd.getOptionValue(Params.D.opt());
            if(cmd.hasOption(Params.ONCE.opt())) {
                new RunHeadlessMode(url, dest).run();
            }
            else {
                int interval;
                try {
                    interval = cmd.hasOption(Params.I.opt()) ? Integer.parseInt(cmd.getOptionValue(Params.I.opt())) : 10;
                }
                catch (NumberFormatException e) {
                    interval = 10;
                }

                RunHeadlessMode headless = new RunHeadlessMode(url, dest);
                while(true) {
                    System.out.println("Running...");
                    headless.run();
                    try {
                        Thread.sleep(interval * 1000);
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
