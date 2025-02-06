package org.tonyhsu17;

import org.tonyhsu17.utilities.commandline.Parameter;



/**
 * Command line options available for use.
 * 
 * @author Tony Hsu
 *
 */
public class Params {
    public static final Parameter D = new Parameter("d", "des", true, "destination path");
    public static final Parameter U = new Parameter("u", "url", true, "rss url");
    public static final Parameter I = new Parameter("i", "interval", true, "cron time");
    public static final Parameter ONCE = new Parameter("once", "once", false, "run once, overrides cron time");

    public static Parameter[] params = {D, U, I, ONCE};
}
