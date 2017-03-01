/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.customer_portal.tag.cloud.sample;

import com.google.gson.*;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import sun.misc.BASE64Encoder;

import java.io.*;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CustomerService {
    private static final Logger logger = Logger.getLogger(CustomerService.class.getName());

    public List<Customer> getCustomerList() throws Exception {
        String customerDataServiceEndpoint = System.getenv("CUSTOMER_DATA_SERVICE_ENDPOINT");

        try {
            HttpClient client = getHttpClient();
            HttpGet apiMethod = new HttpGet(customerDataServiceEndpoint);
            apiMethod.addHeader("Accept", "application/json");

            HttpResponse response = client.execute(apiMethod);
            if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {

                logger.info("Get customer" + response.getStatusLine().getStatusCode());
                return formatDataToCustomers(getStringFromInputStream(response.getEntity().getContent()));
            } else {
                logger.log(Level.SEVERE, "Error occurred invoking the api endpoint. Http Status : " + response.getStatusLine().getStatusCode());
                throw new Exception("Failed to get Buzzwords from backend API:" + customerDataServiceEndpoint);
            }

        } catch (HttpException e) {
            logger.log(Level.SEVERE, "Error occurred while invoking API endpoint.", e);
            throw new Exception("Failed to get Buzzwords from backend API:" + customerDataServiceEndpoint);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error occurred while invoking API endpoint.", e);
            throw new Exception("Failed to get Buzzwords from backend API:" + customerDataServiceEndpoint);
        }

    }

    private String getAccessToken() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException,
                                                   UnsupportedEncodingException {
        String apiGatewayTokenEndpoint = System.getenv("API_GATEWAY_TOKEN_ENDPOINT");
        String apiConsumerKey = System.getenv("API_CONSUMER_KEY");
        String apiConsumerSecret = System.getenv("API_CONSUMER_SECRET");

        BASE64Encoder base64Encoder = new BASE64Encoder();
        String applicationToken = apiConsumerKey + ":" + apiConsumerSecret;
        applicationToken = "Bearer " + base64Encoder.encode(applicationToken.getBytes()).trim();

        HttpClient client = getHttpClient();
        HttpPost postMethod = new HttpPost(apiGatewayTokenEndpoint);
        postMethod.addHeader("Authorization", applicationToken);
        postMethod.addHeader("Content-Type", "application/x-www-form-urlencoded");

        List<NameValuePair> params = new ArrayList<NameValuePair>(3);
        params.add(new BasicNameValuePair("grant_type", "client_credentials"));
        postMethod.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

        String accessToken = null;
        try {
            HttpResponse response = client.execute(postMethod);

            if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
                String accessTokenJson = getStringFromInputStream(response.getEntity().getContent());
                JSONParser parser = new JSONParser();
                Object obj = parser.parse(accessTokenJson);
                JSONObject jsonObject = (JSONObject) obj;
                accessToken = (String) jsonObject.get("access_token");
            } else {
                logger.log(Level.SEVERE,
                        "Error occurred invoking the token endpoint \n Http status : " +
                                response.getStatusLine().getStatusCode() + " response: "
                                + getStringFromInputStream(response.getEntity().getContent()));
            }
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Error occurred while invoking token endpoint.", ex);
        }
        return accessToken;
    }

    private List<Customer> formatDataToCustomers(String customers) throws ParseException {
        List<Customer> customerList = new ArrayList<Customer>();
        if (customers != null) {
            JsonElement jelement = new JsonParser().parse(customers);
            JsonObject jobject = jelement.getAsJsonObject();
            JsonArray jarray = jobject.get("CustomersCollection").getAsJsonObject().get("Customers").getAsJsonArray();
            for (JsonElement jo : jarray) {
                JsonObject j = (JsonObject) jo;
                Customer customer = new Customer(j.get("id").getAsInt(), j.get("company_name").getAsString(),
                                                        j.get("email").getAsString(),
                                                        j.get("project_description").getAsString(),
                                                        j.get("used_products").getAsString());

                customerList.add(customer);
            }
        }
        return customerList;
    }

    private HttpClient getHttpClient() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        SSLContextBuilder builder = new SSLContextBuilder();
        builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(builder.build());

        // create a post request to addAPI.
        return HttpClients.custom().setSSLSocketFactory(sslsf).build();
    }

    private String getStringFromInputStream(InputStream is) {
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }

    public void registerCustomer(String comName, String email, String desc_product, String in_products) throws Exception {
        String customersAPIEndpoint = System.getenv("CUSTOMERS_API_ENDPOINT");
        String jsonPayload = "{\"payloadEmail\":\"" + email + "\","
                                     + "\"payloadCompanyName\":\"" + comName + "\","
                                     + "\"payloadProjectDesc\":\"" + desc_product + "\","
                                     + "\"payloadUsedProducts\":\"" + in_products + "\"}";


        logger.info(jsonPayload);
       
        try {
            HttpClient client = getHttpClient();
            StringEntity requestEntity = new StringEntity(jsonPayload, ContentType.APPLICATION_JSON);

            HttpPost request = new HttpPost(customersAPIEndpoint);
            request.addHeader("Accept", "application/json");
            request.addHeader("Content-Type", "application/json");
            request.addHeader("Authorization", "Bearer " + getAccessToken());
            request.setEntity(requestEntity);

            HttpResponse response = client.execute(request);
            if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
                logger.info(response.getEntity().getContent().toString());
            } else {
                logger.log(Level.SEVERE, "Error occurred invoking the api endpoint. Http Response : " + response.getEntity().getContent().toString());
                throw new Exception("Failed to register customer api call for: " + customersAPIEndpoint);
            }

        } catch (HttpException e) {
            logger.log(Level.SEVERE, "Error occurred while register customer api call.", e);
            throw new Exception("Failed to register customer api call for: " + customersAPIEndpoint);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error occurred while register customer api call.", e);
            throw new Exception("Failed to register customer api call for: " + customersAPIEndpoint);
        }
    }
}
