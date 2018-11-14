package org.tonyhsu17;

import org.tonyhsu17.utilities.commandline.Parameter;



/**
 * Command line options available for use.
 * 
 * @author Tony Hsu
 *
 */
public class Params {
    public static final Parameter D = new Parameter("d", true, "destination path");
    public static final Parameter U = new Parameter("u", true, "rss url");
    public static final Parameter I = new Parameter("i", true, "cron time");
    public static final Parameter ONCE = new Parameter("once", false, "run once, overrides cron time");

    public static Parameter[] getParams() {
        return new Parameter[] {D, U, I, ONCE};
    }
}