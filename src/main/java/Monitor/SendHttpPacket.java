package Monitor;

import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * Sending Http requests
 *
 * @source <a href="https://mkyong.com/java/how-to-send-http-request-getpost-in-java">...</a>     1. Apache HttpClient
 *
 * */
public class SendHttpPacket {
    private static final CloseableHttpClient httpClient = HttpClients.createDefault();

    /**
     * Sends HTTP request to provided URL and returns response.
     *
     * @param url address where the http request is sent
     * @return http response
     */
    public static String get(String url) {
        try {
            HttpGet request = new HttpGet(url);

            CloseableHttpResponse response = httpClient.execute(request);

            HttpEntity entity = response.getEntity();

            String result = EntityUtils.toString(entity);
            if (result.equals("{\"errors\":{\"detail\":\"Not Found\"}}")) return "Not Found";
            return result;
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sends http request to
     *
     * @param url address where http request is sent
     * @return http response
     */
    public static String getVendor(String url) {
        try {

            // get api key from https://macvendors.com/app/subscription
            String api_key = System.getenv("VendorApiKey");

            HttpGet request = new HttpGet(url);
            request.addHeader("Authorization", "Bearer " + api_key);

            CloseableHttpResponse response = httpClient.execute(request);

            HttpEntity entity = response.getEntity();

            String result = EntityUtils.toString(entity);
            if (result.equals("{\"errors\":{\"detail\":\"Not Found\"}}")) return "Not Found";
            return result;
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
    }


}