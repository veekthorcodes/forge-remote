package com.onpointserv.forge_remote;

import com.fasterxml.jackson.annotation.JsonProperty;

record ForgeInvocationToken(@JsonProperty("app") App app, @JsonProperty("context") Context context,
        @JsonProperty("principal") String principal, @JsonProperty("aud") String aud, @JsonProperty("iss") String iss,
        @JsonProperty("iat") long iat, @JsonProperty("nbf") long nbf, @JsonProperty("exp") long exp,
        @JsonProperty("jti") String jti) {

    record App(@JsonProperty("id") String id, @JsonProperty("version") String version,
            @JsonProperty("installationId") String installationId, @JsonProperty("apiBaseUrl") String apiBaseUrl,
            @JsonProperty("environment") Environment environment, @JsonProperty("module") Module module) {
    }

    record Environment(@JsonProperty("type") String type, @JsonProperty("id") String id) {
    }

    record Module(@JsonProperty("type") String type, @JsonProperty("key") String key) {
    }

    record Context(@JsonProperty("localId") String localId, @JsonProperty("cloudId") String cloudId,
            @JsonProperty("environmentId") String environmentId,
            @JsonProperty("environmentType") String environmentType, @JsonProperty("moduleKey") String moduleKey,
            @JsonProperty("siteUrl") String siteUrl, @JsonProperty("extension") Extension extension) {
    }

    record Extension(@JsonProperty("type") String type) {
    }
}
