/*
 * UserRequest.java
 *
 * Copyright (c) 2016 Auth0 (http://auth0.com)
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

package com.auth0.management.users;


import com.auth0.Auth0;
import com.auth0.Auth0Exception;
import com.auth0.callback.BaseCallback;
import com.auth0.management.result.User;
import com.auth0.request.ParameterizableRequest;
import com.auth0.request.Request;
import com.auth0.request.internal.RequestFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.OkHttpClient;

import java.util.Arrays;
import java.util.List;


/**
 * Request to get a user. By default all user fields are included
 */
public class UserRequest implements Request<User> {

    private static final String KEY_FIELDS = "fields";
    private static final String KEY_INCLUDE_FIELDS = "include_fields";

    private final Auth0 auth0;
    private final OkHttpClient client;
    private final ObjectMapper mapper;
    private final RequestFactory factory;
    private final String token;
    private final String userId;

    private List<String> fields;
    private Boolean includeFields;

    public UserRequest(Auth0 auth0, OkHttpClient client, ObjectMapper mapper, RequestFactory factory, String token, String userId) {
        this.auth0 = auth0;
        this.client = client;
        this.mapper = mapper;
        this.factory = factory;
        this.token = token;
        this.userId = userId;
    }

    @Override
    public void start(BaseCallback<User> callback) {
        buildRequest().start(callback);
    }

    @Override
    public User execute() throws Auth0Exception {
        return buildRequest().execute();
    }

    /**
     * Excludes these fields from the result
     *
     * @param fields the list of fields to exclude from the result
     * @return itself
     */
    public UserRequest exclude(String... fields) {
        this.includeFields = false;
        this.fields = Arrays.asList(fields);
        return this;
    }

    /**
     * Only include these fields in the result
     *
     * @param fields the list of fields to include in the result
     * @return itself
     */
    public UserRequest onlyInclude(String... fields) {
        this.includeFields = true;
        this.fields = Arrays.asList(fields);
        return this;
    }

    private ParameterizableRequest<User> buildRequest() {
        HttpUrl.Builder url = HttpUrl.parse(auth0.getDomainUrl()).newBuilder()
                .addPathSegment("api")
                .addPathSegment("v2")
                .addPathSegment("users")
                .addPathSegment(userId);

        if (includeFields != null && fields != null && fields.size() > 0) {
            url.addQueryParameter(KEY_INCLUDE_FIELDS, includeFields.toString());
            StringBuilder stringBuilder = new StringBuilder();
            for (String field : fields) {
                if (stringBuilder.length() > 0)
                    stringBuilder.append(",");
                stringBuilder.append(field);
            }
            url.addQueryParameter(KEY_FIELDS, stringBuilder.toString());
        }

        return factory.GET(url.build(), client, mapper, User.class)
                .setBearer(token);
    }
}
