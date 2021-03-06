/*
 * UserIdentity.java
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

package com.auth0.authentication.result;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * Class that holds the information from a Identity Provider like Facebook or Twitter.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserIdentity {

    private static final String USER_ID_KEY = "user_id";
    private static final String CONNECTION_KEY = "connection";
    private static final String PROVIDER_KEY = "provider";
    private static final String IS_SOCIAL_KEY = "isSocial";
    private static final String ACCESS_TOKEN_KEY = "access_token";
    private static final String ACCESS_TOKEN_SECRET_KEY = "access_token_secret";
    private static final String PROFILE_DATA_KEY = "profileData";
    protected String id;
    protected String connection;
    protected String provider;
    protected boolean social;
    protected String accessToken;
    protected String accessTokenSecret;
    protected Map<String, Object> profileInfo;

    protected UserIdentity() {

    }

    @SuppressWarnings("unchecked")
    public UserIdentity(Map<String, Object> values) {
        final Object idValue = values.get(USER_ID_KEY);
        if (idValue != null) {
            this.id = idValue.toString();
        }
        this.connection = (String) values.get(CONNECTION_KEY);
        this.provider = (String) values.get(PROVIDER_KEY);
        this.social = (boolean) values.get(IS_SOCIAL_KEY);
        this.accessToken = (String) values.get(ACCESS_TOKEN_KEY);
        this.accessTokenSecret = (String) values.get(ACCESS_TOKEN_SECRET_KEY);
        this.profileInfo = (Map<String, Object>) values.get(PROFILE_DATA_KEY);
    }

    @JsonCreator
    public UserIdentity(@JsonProperty(value = USER_ID_KEY) String id,
                        @JsonProperty(value = CONNECTION_KEY) String connection,
                        @JsonProperty(value = PROVIDER_KEY) String provider,
                        @JsonProperty(value = IS_SOCIAL_KEY) boolean social,
                        @JsonProperty(value = ACCESS_TOKEN_KEY) String accessToken,
                        @JsonProperty(value = ACCESS_TOKEN_SECRET_KEY) String accessTokenSecret,
                        @JsonProperty(value = PROFILE_DATA_KEY) Map<String, Object> profileInfo) {
        this.id = id;
        this.connection = connection;
        this.provider = provider;
        this.social = social;
        this.accessToken = accessToken;
        this.accessTokenSecret = accessTokenSecret;
        this.profileInfo = profileInfo;
    }

    public String getId() {
        return id;
    }

    public String getConnection() {
        return connection;
    }

    public String getProvider() {
        return provider;
    }

    public boolean isSocial() {
        return social;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getAccessTokenSecret() {
        return accessTokenSecret;
    }

    public Map<String, Object> getProfileInfo() {
        return profileInfo;
    }

    public String getUserIdentityId() {
        return String.format("%s|%s", this.provider, this.id);
    }
}
