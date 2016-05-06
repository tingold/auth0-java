/*
 * UsersAPIClient.java
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
import com.auth0.management.result.User;
import com.auth0.request.ParameterizableRequest;
import com.auth0.request.Request;
import com.auth0.request.internal.RequestFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.OkHttpClient;


/**
 * API client for Auth0 Users Management API.
 *
 * @see <a href="https://auth0.com/docs/api/management/v2#!/Users/get_users">Users management API docs</a>
 */
public class UsersAPIClient {

    private final Auth0 auth0;
    private final OkHttpClient client;
    private final ObjectMapper mapper;
    private final RequestFactory factory;
    private final String token;

    public UsersAPIClient(Auth0 auth0, OkHttpClient client, ObjectMapper mapper, RequestFactory factory, String token) {
        this.auth0 = auth0;
        this.client = client;
        this.mapper = mapper;
        this.factory = factory;
        this.token = token;
    }

    /**
     * Gets a user
     *
     * @param userId the 'user_id' of the user to retrieve
     * @return an {@link UserRequest} to configure and execute
     */
    public UserRequest get(String userId) {
        return new UserRequest(auth0, client, mapper, factory, token, userId);
    }

    /**
     * Updates a user
     *
     * @param userId the 'user_id' of the user to update
     * @return an {@link UpdateUserRequest} to configure and execute
     */
    public UpdateUserRequest update(String userId) {
        HttpUrl url = getUserUrl(userId);

        ParameterizableRequest<User> request = factory.PATCH(url, client, mapper, User.class)
                .setBearer(token);

        return new UpdateUserRequest(request);
    }

    /**
     * Deletes a user
     *
     * @param userId the 'user_id' of the user to delete
     * @return a {@link Request} to execute
     */
    public Request<Void> delete(String userId) {
        HttpUrl url = getUserUrl(userId);

        return factory.DELETE(url, client, mapper)
                .setBearer(token);
    }

    private HttpUrl getUserUrl(String userId) {
        return HttpUrl.parse(auth0.getDomainUrl()).newBuilder()
                .addPathSegment("api")
                .addPathSegment("v2")
                .addPathSegment("users")
                .addPathSegment(userId)
                .build();
    }
}
