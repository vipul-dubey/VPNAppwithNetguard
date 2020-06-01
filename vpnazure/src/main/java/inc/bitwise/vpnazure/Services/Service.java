package inc.bitwise.vpnazure.Services;

import android.content.Context;
import android.content.Intent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import inc.bitwise.vpnazure.Exception.ServiceException;
import inc.bitwise.vpnazure.Utils;
import okhttp3.Cache;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import timber.log.Timber;

/**
 * Created by Vipuld on 16/09/2019.
 */

public class Service {

    private static String _url;
    private static Context appContext;
    private static boolean debuggable;
    private static Service instance = null;

    private OkHttpClient client = null;
    private Interceptor retryInterceptor;
    public static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");

    public static void initialize(Context context, String url, boolean isDebuggable)
    {
        appContext = context;
        _url = url;
        debuggable = isDebuggable;


        Service service = Service.sharedInstance();

        if(service.client == null)
        {
            service.setUpOkHttpClient(null, null);
        }
    }
  /*  public static void initialize(Context context, String url, boolean isDebuggable)
    {
        initialize(context, url,isDebuggable);
    }*/

    protected Service()
    {
    }
    public static Service sharedInstance()
    {


        if (instance == null)
        {
            instance = new Service();
        }
        return instance;
    }

    public static void setUpOkHttpClient(OkHttpClient client, Interceptor retryInterceptor)
    {
        Service service = Service.sharedInstance();

        if(client == null)
        {
            File cache = new File(service.appContext.getCacheDir(), "okhttpCache");
            int cacheSize = 10 * 1024 * 1024; // 10 MiB
            Cache clientCache = new Cache(cache, cacheSize);

            service.client = new OkHttpClient.Builder().cache(clientCache)
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .writeTimeout(20, TimeUnit.SECONDS)
                    .readTimeout(20, TimeUnit.SECONDS)
                    .build();
        }
        else
        {
            service.client = client;
        }

        service.retryInterceptor = retryInterceptor;
    }
    /**
     * Send a request to an Azure endpoint with the given method and request body.
     *
     * @param method      The request method.  Should be GET, POST, PUT, or DELETE
     * @param endpoint    The endpoint of the request.  Ex: /restaurants
     * @param body        The request body.  This should be a JSON encoded string
     * @param args        Arguments to pass to the endpoint formatter
     * @return The response from the API call.
     * @throws IOException
     */
    public JSONObject sendRequest(String method, Utils.Endpoint endpoint, String body, Object... args) throws IOException, JSONException, ServiceException
    {
        return sendRequest(client, method, endpoint, body, args);
    }

    /**
     * Send a request to an Azure endpoint with the given method and request body.
     *
     * @param method      The request method.  Should be GET, POST, PUT, or DELETE
     * @param endpoint    The endpoint of the request.  Ex: /restaurants
     * @param body        The request body.  This should be a JSON encoded string
     * @param args        Arguments to pass to the endpoint formatter
     * @return The response from the API call.
     * @throws IOException
     */
    public JSONObject sendRequestWithTimeout(int seconds, String method, Utils.Endpoint endpoint, String body, Object... args) throws IOException, JSONException,ServiceException
    {
        OkHttpClient callClient = client.newBuilder().readTimeout(seconds, TimeUnit.SECONDS).build();
        return sendRequest(callClient, method, endpoint, body, args);
    }

    /**
     * Send a request to an Azure endpoint with the given method and request body.
     *
     * @param method      The request method.  Should be GET, POST, PUT, or DELETE
     * @param endpoint    The endpoint of the request.  Ex: /restaurants
     * @param body        The request body.  This should be a JSON encoded string
     * @param args        Arguments to pass to the endpoint formatter
     * @return The response from the API call.
     * @throws IOException
     */
    public JSONObject sendRequest(OkHttpClient client, String method, Utils.Endpoint endpoint, String body, Object... args) throws IOException, JSONException, ServiceException {
        try {
            String endpointStr = String.format(endpoint.getValue(), args);

            String url = buildAzureUrl(endpointStr);
            Request.Builder builder = null;



            if (method == "GET") {
                builder = new Request.Builder().url(url);
                if(body!=null){
                    HttpUrl.Builder _builder = new HttpUrl.Builder();
                    _builder.addQueryParameter(body,body);
                }
            } else if (method == "POST") {
                if (body != null) {
                    builder = new Request.Builder().url(url).post(RequestBody.create(MEDIA_TYPE_JSON, body));
                } else {
                    builder = new Request.Builder().url(url).post(RequestBody.create(MEDIA_TYPE_JSON, ""));
                }
            } else if (method == "PUT") {
                if (body != null) {
                    builder = new Request.Builder().url(url).put(RequestBody.create(MEDIA_TYPE_JSON, body));
                } else {
                    builder = new Request.Builder().url(url).put(RequestBody.create(MEDIA_TYPE_JSON, ""));
                }
            } else if (method == "DELETE") {
                if (body != null) {
                    builder = new Request.Builder().url(url).delete(RequestBody.create(MEDIA_TYPE_JSON, body));
                } else {
                    builder = new Request.Builder().url(url).delete();
                }
            }


            OkHttpClient callClient;
            if (retryInterceptor != null && Utils.RetryEndpoints.contains(endpoint)) {
                callClient = client.newBuilder().addInterceptor(retryInterceptor).build();
            } else {
                callClient = client;
            }

            Request request = builder.build();

            Response response = callClient.newCall(request).execute();

            return handleResponse(response);
        } catch (IOException e)
        {
            throw e;
        } catch (Exception ex)
        {
            throw ex;
        }


    }

    /**
     * Send a GET request to the API.  Convenience method
     *
     * @param endpoint The URL of the API endpoint. Ex: /registeruser
     * @param args     Arguments to pass to the endpoint formatter
     * @return The API response
     * @throws IOException
     * @throws JSONException
     * @throws Exception
     */
    public JSONObject sendGet(Utils.Endpoint endpoint, Object... args) throws IOException, JSONException, ServiceException {

        return sendRequest("GET", endpoint, null, args);
    }

    /**
     * Send a POST request to the API.  Convenience method
     *
     * @param endpoint The URL of the API endpoint. Ex: /registeruser
     * @param body     The JSON encoded body of the request
     * @param args     Arguments to pass to the endpoint formatter
     * @return The API response
     * @throws IOException
     * @throws JSONException
     * @throws Exception
     */
    public JSONObject sendPost(Utils.Endpoint endpoint, String body, Object... args) throws IOException, JSONException, ServiceException {
        return sendRequest("POST", endpoint, body, args);
    }

    /**
     * Send a POST request to the API.  Convenience method
     *
     * @param endpoint The URL of the API endpoint. Ex: /registeruser
     * @param body     The JSON encoded body of the request
     * @param args     Arguments to pass to the endpoint formatter
     * @return The API response
     * @throws IOException
     * @throws JSONException
     * @throws Exception
     */
    public JSONObject sendPost(int timeout, Utils.Endpoint endpoint, String body, Object... args) throws IOException, JSONException, ServiceException {
        return sendRequestWithTimeout(timeout, "POST", endpoint, body, args);
    }

    /**
     * Send a PUT request to the API.  Convenience method
     *
     * @param endpoint The URL of the API endpoint. Ex: /registeruser
     * @param body     The JSON encoded body of the request
     * @param args     Arguments to pass to the endpoint formatter
     * @return The API response
     * @throws IOException
     * @throws JSONException
     * @throws Exception
     */
    public JSONObject sendPut(Utils.Endpoint endpoint, String body, Object... args) throws IOException, JSONException, ServiceException {
        return sendRequest("PUT", endpoint, body, args);
    }

    /**
     * Send a DELETE request to the API.  Convenience method
     *
     * @param endpoint The URL of the API endpoint. Ex: /registeruser
     * @param body     The JSON encoded body of the request
     * @param args     Arguments to pass to the endpoint formatter
     * @return The API response
     * @throws IOException
     * @throws JSONException
     * @throws Exception
     */
    public JSONObject sendDelete(Utils.Endpoint endpoint, String body, Object... args) throws IOException, JSONException, ServiceException {
        return sendRequest("DELETE", endpoint, body, args);
    }

    /**
     * Build an Azure URL for a specific endpoint
     *
     * @param endpoint The endpoint url.  Ex: /registeruser
     * @return A string with the full Aws URL including the API key.
     */
    private String buildAzureUrl(String endpoint) {
        return _url + endpoint;
    }

    /**
     * Handle the Azure API response.
     *
     * @param response The response to the request
     * @return The response from the API.
     * @throws IOException
     * @throws JSONException
     * @throws Exception
     */
    private JSONObject handleResponse(Response response) throws IOException, JSONException, ServiceException
    {


        int responseCode = response.code();
        String responseBody = response.body().string();
        if (debuggable) {
            Timber.tag("Response");
            Timber.i(responseBody + "");
        }
        if (responseCode != 200) {
            
             if(responseCode==500)
            {
                return new JSONObject(responseBody);
            }
            
            if (responseBody.length() > 0) {
                JSONObject jsonError = new JSONObject(responseBody);
                String message = jsonError.getString("message");
                Timber.tag("ServiceError");
                Timber.i(message);
                int awsErrorCode = jsonError.getInt("num");


                if (awsErrorCode == 100 || awsErrorCode == 101) {
                    LocalBroadcastManager.getInstance(appContext).sendBroadcast(new Intent("AuthFailure"));

                    throw new ServiceException("Invalid Auth Token", awsErrorCode);
                } else if (awsErrorCode >= 200 && awsErrorCode < 300) {
                    throw new ServiceException(message,awsErrorCode );

                } else {
                    throw new ServiceException(message, awsErrorCode);
                }
            } else {
                throw new ServiceException("Unexpected Http Error", responseCode);
            }
        }

        if (responseBody.length() <= 0) {
            return null;
        }
        return new JSONObject(responseBody);
    }
}
