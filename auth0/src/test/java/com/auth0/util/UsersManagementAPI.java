/*
 * AuthenticationAPI.java
 *
 * Copyright (c) 2015 Auth0 (http://auth0.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.auth0.util;

import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

import java.io.IOException;

public class UsersManagementAPI {

    private MockWebServer server;

    public UsersManagementAPI() throws IOException {
        this.server = new MockWebServer();
        this.server.start();
    }

    public String getDomain() {
        return server.url("/").toString();
    }

    public void shutdown() throws IOException {
        this.server.shutdown();
    }

    public RecordedRequest takeRequest() throws InterruptedException {
        return server.takeRequest();
    }

    public UsersManagementAPI willReturnSuccess() {
        return willReturnSuccess(200);
    }

    public UsersManagementAPI willReturnSuccess(int code) {
        server.enqueue(responseWithJSON("", code));
        return this;
    }

    public UsersManagementAPI willReturnUser() {
        String json = "{\n" +
                "  \"email\": \"john.doe@gmail.com\",\n" +
                "  \"email_verified\": false,\n" +
                "  \"username\": \"johndoe\",\n" +
                "  \"phone_number\": \"+199999999999999\",\n" +
                "  \"phone_verified\": false,\n" +
                "  \"user_id\": \"usr_5457edea1b8f33391a000004\",\n" +
                "  \"created_at\": \"2016-05-17T11:22:33.444Z\",\n" +
                "  \"updated_at\": \"2016-05-17T11:22:33.444Z\",\n" +
                "  \"identities\": [\n" +
                "    {\n" +
                "      \"connection\": \"Initial-Connection\",\n" +
                "      \"user_id\": \"5457edea1b8f22891a000004\",\n" +
                "      \"provider\": \"auth0\",\n" +
                "      \"isSocial\": false\n" +
                "    }\n" +
                "  ],\n" +
                "  \"app_metadata\": {},\n" +
                "  \"user_metadata\": {},\n" +
                "  \"picture\": \"http://example.com/userpicture.png\",\n" +
                "  \"name\": \"John\",\n" +
                "  \"nickname\": \"johncito\",\n" +
                "  \"multifactor\": [\n" +
                "    \"guardian\"\n" +
                "  ],\n" +
                "  \"last_ip\": \"222.111.222.111\",\n" +
                "  \"last_login\": \"2016-05-17T11:22:33.444Z\",\n" +
                "  \"logins_count\": 1,\n" +
                "  \"blocked\": false\n" +
                "}";
        server.enqueue(responseWithJSON(json, 200));
        return this;
    }

    private MockResponse responseWithJSON(String json, int statusCode) {
        return new MockResponse()
                .setResponseCode(statusCode)
                .addHeader("Content-Type", "application/json")
                .setBody(json);
    }

}
