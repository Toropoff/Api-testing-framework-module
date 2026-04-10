package com.apiframework.testsupport.client;

import com.apiframework.config.EndpointDefinition;
import com.apiframework.config.HttpVerb;
import com.apiframework.http.HttpClient;
import com.apiframework.model.ApiResponse;
import org.testng.annotations.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ApiRequestBuilderTest {

    // --- stub ---

    static class StubHttpClient implements HttpClient {
        String lastPath;
        Map<String, ?> lastQuery;
        Object lastBody;
        String lastMethod;

        @Override public <T> ApiResponse<T> get(String path, Class<T> type) {
            return get(path, Map.of(), type);
        }
        @Override public <T> ApiResponse<T> get(String path, Map<String, ?> q, Class<T> type) {
            lastPath = path; lastQuery = q; lastMethod = "GET";
            return emptyResponse(type);
        }
        @Override public <T> ApiResponse<T> post(String path, Object body, Class<T> type) {
            lastPath = path; lastBody = body; lastMethod = "POST";
            return emptyResponse(type);
        }
        @Override public <T> ApiResponse<T> put(String path, Object body, Class<T> type) {
            lastPath = path; lastBody = body; lastMethod = "PUT";
            return emptyResponse(type);
        }
        @Override public <T> ApiResponse<T> patch(String path, Object body, Class<T> type) {
            lastPath = path; lastBody = body; lastMethod = "PATCH";
            return emptyResponse(type);
        }
        @Override public <T> ApiResponse<T> delete(String path, Class<T> type) {
            lastPath = path; lastMethod = "DELETE";
            return emptyResponse(type);
        }
        private <T> ApiResponse<T> emptyResponse(Class<T> type) {
            return new ApiResponse<>(200, Map.of(), null, 0, null, "");
        }
    }

    // --- helpers ---

    private <T> ApiRequestBuilder<T> builder(HttpVerb verb, String relUrl, Class<T> type) {
        StubHttpClient stub = new StubHttpClient();
        return new ApiRequestBuilder<>(stub, "", new EndpointDefinition(verb, relUrl), type);
    }

    private <T> ApiRequestBuilder<T> builderWithStub(StubHttpClient stub, HttpVerb verb, String relUrl, Class<T> type) {
        return new ApiRequestBuilder<>(stub, "", new EndpointDefinition(verb, relUrl), type);
    }

    // --- tests ---

    @Test
    public void getWithMultipleQueryParamsBuildsExpectedMap() {
        StubHttpClient stub = new StubHttpClient();
        builderWithStub(stub, HttpVerb.GET, "https://api.example.com/items", String.class)
            .query("k1", "v1")
            .query("k2", 42)
            .send();

        assertThat(stub.lastQuery).containsKey("k1").containsKey("k2");
        assertThat(stub.lastQuery.get("k1")).isEqualTo("v1");
        assertThat(stub.lastQuery.get("k2")).isEqualTo(42);
        assertThat(stub.lastMethod).isEqualTo("GET");
    }

    @Test
    public void querySkipsNullValues() {
        StubHttpClient stub = new StubHttpClient();
        builderWithStub(stub, HttpVerb.GET, "https://api.example.com/items", String.class)
            .query("key", null)
            .send();

        assertThat(stub.lastQuery).doesNotContainKey("key");
    }

    @Test
    public void bodyFieldSkipsNullValues() {
        StubHttpClient stub = new StubHttpClient();
        builderWithStub(stub, HttpVerb.POST, "https://api.example.com/items", String.class)
            .bodyField("f1", "val")
            .bodyField("f2", null)
            .send();

        @SuppressWarnings("unchecked")
        Map<String, Object> sentBody = (Map<String, Object>) stub.lastBody;
        assertThat(sentBody).containsKey("f1").doesNotContainKey("f2");
    }

    @Test
    public void pathParamIsSubstitutedInUrl() {
        StubHttpClient stub = new StubHttpClient();
        builderWithStub(stub, HttpVerb.GET, "https://api.example.com/users/{id}/orders", String.class)
            .pathParam("id", 7)
            .send();

        assertThat(stub.lastPath).isEqualTo("https://api.example.com/users/7/orders");
    }

    @Test
    public void postWithBodyDtoIgnoresBodyFields() {
        StubHttpClient stub = new StubHttpClient();
        Object dto = new Object();
        builderWithStub(stub, HttpVerb.POST, "https://api.example.com/items", String.class)
            .body(dto)
            .send();

        assertThat(stub.lastBody).isSameAs(dto);
    }

    @Test
    public void postWithBodyFieldsBuildsExpectedMap() {
        StubHttpClient stub = new StubHttpClient();
        builderWithStub(stub, HttpVerb.POST, "https://api.example.com/items", String.class)
            .bodyField("event", "created")
            .bodyField("amount", 100)
            .send();

        @SuppressWarnings("unchecked")
        Map<String, Object> sentBody = (Map<String, Object>) stub.lastBody;
        assertThat(sentBody).containsEntry("event", "created").containsEntry("amount", 100);
    }

    @Test
    public void mixingBodyAndBodyFieldThrows() {
        assertThatThrownBy(() ->
            builder(HttpVerb.POST, "https://api.example.com/items", String.class)
                .body(new Object())
                .bodyField("k", "v")
                .send()
        ).isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("not both");
    }

    @Test
    public void deleteCallsClientDeleteAndIgnoresBody() {
        StubHttpClient stub = new StubHttpClient();
        builderWithStub(stub, HttpVerb.DELETE, "https://api.example.com/items/1", String.class).send();

        assertThat(stub.lastMethod).isEqualTo("DELETE");
        assertThat(stub.lastBody).isNull();
    }

    @Test
    public void headerFollowedBySendThrowsUnsupported() {
        assertThatThrownBy(() ->
            builder(HttpVerb.GET, "https://api.example.com/items", String.class)
                .header("X-Foo", "bar")
                .send()
        ).isInstanceOf(UnsupportedOperationException.class);
    }
}
