package org.tonyhsu17;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.tonyhsu17.utilities.HistoryLog;
import org.tonyhsu17.utilities.Logger;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;



public class RunHeadlessMode implements Logger {
    private String destination;
    private String url;
    private HistoryLog history;

    public RunHeadlessMode(String url, String destination) throws IOException {
        this.destination = destination;
        this.url = url;
        history = new HistoryLog(destination);
        System.setProperty("http.agent", "Chrome");
    }

    public void run() {
        try {
            parseFeed();
        }
        catch (IOException e) {
        }
    }

    private void parseFeed() throws IOException {
        try {
            SyndFeed feed = new SyndFeedInput().build(new XmlReader(new URL(url)));
            List<SyndEntry> entries = feed.getEntries();
            for(SyndEntry entry : entries) {
                String downloadLink = entry.getLink();
                if(!history.isInHistory(downloadLink)) {
                    for(int i = 0; i < 12; i++) {
                        URL url = handleRedirects(downloadLink);
                        // download file
                        try {
                            File f = new File(destination + File.separator + System.nanoTime() + ".torrent");
                            FileUtils.copyURLToFile(url, f, 10000, 20000);
                            info("Saving: " + downloadLink + " to " + f.getName());
                            history.add(downloadLink);
                            break;
                        }
                        catch (SocketTimeoutException e) {
                            // retry again after 5 min
                            Thread.sleep(1000 * 60 * 5); // 5 minutes
                        }
                        catch (IOException e) {
                            error(e);
                        }
                    }
                }
            }
            history.save();
        }
        catch (IllegalArgumentException | FeedException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    // https://www.mkyong.com/java/java-httpurlconnection-follow-redirect-example/
    private URL handleRedirects(String link) throws IOException {
        URL url = new URL(link);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setReadTimeout(300000);
        conn.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
        conn.addRequestProperty("User-Agent", "Mozilla");
        conn.addRequestProperty("Referer", "google.com");
        //        info("Request URL ... " + url);
        boolean redirect = false;
        // normally, 3xx is redirect
        int status = conn.getResponseCode();
        if(status != HttpURLConnection.HTTP_OK) {
            if(status == HttpURLConnection.HTTP_MOVED_TEMP ||
               status == HttpURLConnection.HTTP_MOVED_PERM ||
               status == HttpURLConnection.HTTP_SEE_OTHER) {
                redirect = true;
            }
        }

        //        info("Response Code ... " + status);
        if(redirect) {
            // get redirect url from "location" header field
            String newUrl = conn.getHeaderField("Location");
            // get the cookie if need, for login
            String cookies = conn.getHeaderField("Set-Cookie");
            // open the new connnection again
            conn = (HttpURLConnection)new URL(newUrl).openConnection();
            conn.setRequestProperty("Cookie", cookies);
            conn.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
            conn.addRequestProperty("User-Agent", "Mozilla");
            conn.addRequestProperty("Referer", "google.com");
            //            info("Redirect to URL : " + newUrl);
            url = new URL(newUrl);
        }
        return url;
    }
}
