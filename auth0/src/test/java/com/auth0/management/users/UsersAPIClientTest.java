package com.auth0.management.users;

import com.auth0.Auth0;
import com.auth0.management.result.User;
import com.auth0.request.internal.RequestFactory;
import com.auth0.util.MockBaseCallback;
import com.auth0.util.UsersManagementAPI;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.mockwebserver.RecordedRequest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static com.auth0.util.CallbackMatcher.hasNoError;
import static com.auth0.util.CallbackMatcher.hasPayloadOfType;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class UsersAPIClientTest {

    private static final String CLIENT_ID = "CLIENT_ID";
    private static final String TOKEN = "TOKEN";
    private static final String USER_ID = "USER_ID";

    private UsersAPIClient users;

    private UsersManagementAPI mockAPI;

    @Before
    public void setUp() throws Exception {
        mockAPI = new UsersManagementAPI();
        final String domain = mockAPI.getDomain();
        Auth0 auth0 = new Auth0(CLIENT_ID, domain, domain);
        users = new UsersAPIClient(auth0, new OkHttpClient(), new ObjectMapper(), new RequestFactory(), TOKEN);
    }

    @After
    public void tearDown() throws Exception {
        mockAPI.shutdown();
    }

    @Test
    public void shouldGetFullUser() throws Exception {
        mockAPI.willReturnUser();

        final MockBaseCallback<User> callback = new MockBaseCallback<>();

        users.get(USER_ID)
                .start(callback);

        assertThat(callback, hasPayloadOfType(User.class));

        final RecordedRequest request = mockAPI.takeRequest();
        assertThat(request.getHeader("Authorization"), equalTo("Bearer TOKEN"));
        assertThat(request.getMethod(), equalTo("GET"));
        assertThat(request.getPath(), equalTo("/api/v2/users/USER_ID"));
    }

    @Test
    public void shouldGetUserIncludingFields() throws Exception {
        mockAPI.willReturnUser();

        final MockBaseCallback<User> callback = new MockBaseCallback<>();

        users.get(USER_ID)
                .onlyInclude("field1", "field2")
                .start(callback);

        assertThat(callback, hasPayloadOfType(User.class));

        final RecordedRequest request = mockAPI.takeRequest();
        assertThat(request.getHeader("Authorization"), equalTo("Bearer TOKEN"));
        assertThat(request.getMethod(), equalTo("GET"));
        assertThat(request.getPath(), equalTo("/api/v2/users/USER_ID?include_fields=true&fields=field1,field2"));
    }

    @Test
    public void shouldGetUserExcludingFields() throws Exception {
        mockAPI.willReturnUser();

        final MockBaseCallback<User> callback = new MockBaseCallback<>();

        users.get(USER_ID)
                .exclude("field3", "field4")
                .start(callback);

        assertThat(callback, hasPayloadOfType(User.class));

        final RecordedRequest request = mockAPI.takeRequest();
        assertThat(request.getHeader("Authorization"), equalTo("Bearer TOKEN"));
        assertThat(request.getMethod(), equalTo("GET"));
        assertThat(request.getPath(), equalTo("/api/v2/users/USER_ID?include_fields=false&fields=field3,field4"));
    }

    @Test
    public void shouldUpdateBlocked() throws Exception {
        mockAPI.willReturnUser();

        final MockBaseCallback<User> callback = new MockBaseCallback<>();

        users.update(USER_ID)
                .setBlocked(false)
                .start(callback);

        assertThat(callback, hasPayloadOfType(User.class));

        final RecordedRequest request = mockAPI.takeRequest();
        assertThat(request.getHeader("Authorization"), equalTo("Bearer TOKEN"));
        assertThat(request.getMethod(), equalTo("PATCH"));
        assertThat(request.getPath(), equalTo("/api/v2/users/USER_ID"));

        Map<String, Object> body = bodyFromRequest(request);
        assertThat(body, hasEntry("blocked", (Object) false));
    }

    @Test
    public void shouldUpdateAppMetadata() throws Exception {
        mockAPI.willReturnUser();

        final MockBaseCallback<User> callback = new MockBaseCallback<>();

        HashMap<String, Object> appMetadata = new HashMap<>();
        appMetadata.put("app_field1_key", "app_field1_value");

        users.update(USER_ID)
                .setAppMetadata(appMetadata)
                .start(callback);

        assertThat(callback, hasPayloadOfType(User.class));

        final RecordedRequest request = mockAPI.takeRequest();
        assertThat(request.getHeader("Authorization"), equalTo("Bearer TOKEN"));
        assertThat(request.getMethod(), equalTo("PATCH"));
        assertThat(request.getPath(), equalTo("/api/v2/users/USER_ID"));

        Map<String, Object> body = bodyFromRequest(request);
        assertThat(body, hasEntry("app_metadata", (Object) appMetadata));

        Map<String, Object> appMetadata2 = (Map<String, Object>) body.get("app_metadata");
        assertThat(appMetadata2, hasEntry("app_field1_key", (Object) "app_field1_value"));
    }

    @Test
    public void shouldUpdateEmailAlreadyVerified() throws Exception {
        mockAPI.willReturnUser();

        final MockBaseCallback<User> callback = new MockBaseCallback<>();

        users.update(USER_ID)
                .setPhoneNumber("+1 555 666 777")
                .setPhoneNumberVerified(true)
                .forConnection("some_connection_name")
                .withClientId("some_client_id")
                .start(callback);

        assertThat(callback, hasPayloadOfType(User.class));

        final RecordedRequest request = mockAPI.takeRequest();
        assertThat(request.getHeader("Authorization"), equalTo("Bearer TOKEN"));
        assertThat(request.getMethod(), equalTo("PATCH"));
        assertThat(request.getPath(), equalTo("/api/v2/users/USER_ID"));

        Map<String, Object> body = bodyFromRequest(request);
        assertThat(body, hasEntry("phone_number", (Object) "+1 555 666 777"));
        assertThat(body, hasEntry("connection", (Object) "some_connection_name"));
        assertThat(body, hasEntry("client_id", (Object) "some_client_id"));
        assertThat(body, hasEntry("phone_verified", (Object) true));
    }

    @Test
    public void shouldUpdateEmailAndRequireVerification() throws Exception {
        mockAPI.willReturnUser();

        final MockBaseCallback<User> callback = new MockBaseCallback<>();

        users.update(USER_ID)
                .setEmail("email@example.com")
                .requiresEmailVerification(true)
                .forConnection("some_connection_name")
                .withClientId("some_client_id")
                .start(callback);

        assertThat(callback, hasPayloadOfType(User.class));

        final RecordedRequest request = mockAPI.takeRequest();
        assertThat(request.getHeader("Authorization"), equalTo("Bearer TOKEN"));
        assertThat(request.getMethod(), equalTo("PATCH"));
        assertThat(request.getPath(), equalTo("/api/v2/users/USER_ID"));

        Map<String, Object> body = bodyFromRequest(request);
        assertThat(body, hasEntry("email", (Object) "email@example.com"));
        assertThat(body, hasEntry("connection", (Object) "some_connection_name"));
        assertThat(body, hasEntry("client_id", (Object) "some_client_id"));
        assertThat(body, hasEntry("verify_email", (Object) true));
    }

    @Test
    public void shouldUpdatePhoneNumberAlreadyVerified() throws Exception {
        mockAPI.willReturnUser();

        final MockBaseCallback<User> callback = new MockBaseCallback<>();

        users.update(USER_ID)
                .setEmail("email@example.com")
                .setEmailVerified(true)
                .forConnection("some_connection_name")
                .withClientId("some_client_id")
                .start(callback);

        assertThat(callback, hasPayloadOfType(User.class));

        final RecordedRequest request = mockAPI.takeRequest();
        assertThat(request.getHeader("Authorization"), equalTo("Bearer TOKEN"));
        assertThat(request.getMethod(), equalTo("PATCH"));
        assertThat(request.getPath(), equalTo("/api/v2/users/USER_ID"));

        Map<String, Object> body = bodyFromRequest(request);
        assertThat(body, hasEntry("email", (Object) "email@example.com"));
        assertThat(body, hasEntry("connection", (Object) "some_connection_name"));
        assertThat(body, hasEntry("client_id", (Object) "some_client_id"));
        assertThat(body, hasEntry("email_verified", (Object) true));
    }

    @Test
    public void shouldUpdatePhoneNumberAndRequireVerification() throws Exception {
        mockAPI.willReturnUser();

        final MockBaseCallback<User> callback = new MockBaseCallback<>();

        users.update(USER_ID)
                .setPhoneNumber("+1 555 666 777")
                .requiresPhoneNumberVerification(true)
                .forConnection("some_connection_name")
                .withClientId("some_client_id")
                .start(callback);

        assertThat(callback, hasPayloadOfType(User.class));

        final RecordedRequest request = mockAPI.takeRequest();
        assertThat(request.getHeader("Authorization"), equalTo("Bearer TOKEN"));
        assertThat(request.getMethod(), equalTo("PATCH"));
        assertThat(request.getPath(), equalTo("/api/v2/users/USER_ID"));

        Map<String, Object> body = bodyFromRequest(request);
        assertThat(body, hasEntry("phone_number", (Object) "+1 555 666 777"));
        assertThat(body, hasEntry("connection", (Object) "some_connection_name"));
        assertThat(body, hasEntry("client_id", (Object) "some_client_id"));
        assertThat(body, hasEntry("verify_phone_number", (Object) true));
    }

    @Test
    public void shouldUpdatePasswordAndRequireVerification() throws Exception {
        mockAPI.willReturnUser();

        final MockBaseCallback<User> callback = new MockBaseCallback<>();

        users.update(USER_ID)
                .setPassword("some secret password")
                .verifyPasswordByEmail(true)
                .forConnection("some_connection_name")
                .withClientId("some_client_id")
                .start(callback);

        assertThat(callback, hasPayloadOfType(User.class));

        final RecordedRequest request = mockAPI.takeRequest();
        assertThat(request.getHeader("Authorization"), equalTo("Bearer TOKEN"));
        assertThat(request.getMethod(), equalTo("PATCH"));
        assertThat(request.getPath(), equalTo("/api/v2/users/USER_ID"));

        Map<String, Object> body = bodyFromRequest(request);
        assertThat(body, hasEntry("password", (Object) "some secret password"));
        assertThat(body, hasEntry("connection", (Object) "some_connection_name"));
        assertThat(body, hasEntry("client_id", (Object) "some_client_id"));
        assertThat(body, hasEntry("verify_password", (Object) true));
    }

    @Test
    public void shouldUpdateUsernameAndIncludeConnection() throws Exception {
        mockAPI.willReturnUser();

        final MockBaseCallback<User> callback = new MockBaseCallback<>();

        users.update(USER_ID)
                .setUsername("my_username")
                .forConnection("connection_name")
                .start(callback);

        assertThat(callback, hasPayloadOfType(User.class));

        final RecordedRequest request = mockAPI.takeRequest();
        assertThat(request.getHeader("Authorization"), equalTo("Bearer TOKEN"));
        assertThat(request.getMethod(), equalTo("PATCH"));
        assertThat(request.getPath(), equalTo("/api/v2/users/USER_ID"));

        Map<String, Object> body = bodyFromRequest(request);
        assertThat(body, hasEntry("username", (Object) "my_username"));
        assertThat(body, hasEntry("connection", (Object) "connection_name"));
    }

    @Test
    public void shouldUpdateUserMetadata() throws Exception {
        mockAPI.willReturnUser();

        final MockBaseCallback<User> callback = new MockBaseCallback<>();

        HashMap<String, Object> userMetadata = new HashMap<>();
        userMetadata.put("user_field1_key", "user_field1_value");

        users.update(USER_ID)
                .setUserMetadata(userMetadata)
                .start(callback);

        assertThat(callback, hasPayloadOfType(User.class));

        final RecordedRequest request = mockAPI.takeRequest();
        assertThat(request.getHeader("Authorization"), equalTo("Bearer TOKEN"));
        assertThat(request.getMethod(), equalTo("PATCH"));
        assertThat(request.getPath(), equalTo("/api/v2/users/USER_ID"));

        Map<String, Object> body = bodyFromRequest(request);
        assertThat(body, hasEntry("user_metadata", (Object) userMetadata));

        Map<String, Object> userMetadata2 = (Map<String, Object>) body.get("user_metadata");
        assertThat(userMetadata2, hasEntry("user_field1_key", (Object) "user_field1_value"));
    }

    @Test
    public void testDeleteUser() throws Exception {
        mockAPI.willReturnSuccess(204);

        final MockBaseCallback<Void> callback = new MockBaseCallback<>();

        users.delete(USER_ID)
                .start(callback);

        final RecordedRequest request = mockAPI.takeRequest();
        assertThat(request.getHeader("Authorization"), equalTo("Bearer TOKEN"));
        assertThat(request.getMethod(), equalTo("DELETE"));
        assertThat(request.getPath(), equalTo("/api/v2/users/USER_ID"));

        assertThat(callback, hasNoError());
    }

    private Map<String, Object> bodyFromRequest(RecordedRequest request) throws java.io.IOException {
        return new ObjectMapper().readValue(request.getBody().inputStream(), new TypeReference<Map<String, Object>>() {
        });
    }
}