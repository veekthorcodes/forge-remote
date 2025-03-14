package com.onpointserv.forge_remote;

import org.jose4j.jwk.HttpsJwks;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.keys.resolvers.HttpsJwksVerificationKeyResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;

@Component
class FitValidator {

	private static final Logger log = LoggerFactory.getLogger(FitValidator.class);
	static final String BEARER_SCHEME = "bearer ";
	static final String APP_ID_PREFIX = "ari:cloud:ecosystem::app/";

	@Value("${jwks.endpoint:https://forge.cdn.prod.atlassian-dev.net/.well-known/jwks.json}")
	private String jwksUrl;

	@Value("${app.id}")
	private String id;

	private String appId;

	@PostConstruct
	void init() {
		if (id.isBlank()) {
			throw new IllegalArgumentException(
					"app.id is not provided. Please provide app.id in application.properties");
		}
		this.appId = APP_ID_PREFIX + id;
	}

	private static String getToken(String authHeader) {
		if (authHeader == null || !authHeader.toLowerCase().startsWith(BEARER_SCHEME)) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid authorization header");
		}
		return authHeader.substring(BEARER_SCHEME.length());
	}

	JwtClaims validate(String token) {
		var invocationToken = getToken(token);
		var resolver = new HttpsJwksVerificationKeyResolver(new HttpsJwks(jwksUrl));

		try {
			var jwtConsumer = new JwtConsumerBuilder()
					.setVerificationKeyResolver(resolver)
					.setExpectedAudience(appId)
					.setExpectedIssuer("forge/invocation-token")
					.build();
			var jwtClaims = jwtConsumer.process(invocationToken).getJwtClaims();
			log.info("JWK validated");
			return jwtClaims;
		} catch (InvalidJwtException e) {
			log.error("JWK is not valid {}", e.getMessage());
			throw new ResponseStatusException(
					HttpStatus.UNAUTHORIZED,
					"Invalid JWK");
		}
	}

	ForgeInvocationToken getFit(String token) {
		var jwtClaims = validate(token);
		final ObjectMapper objectMapper = new ObjectMapper();

		try {
			final String jsonClaims = jwtClaims.toJson();
			objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			return objectMapper.readValue(jsonClaims, ForgeInvocationToken.class);
		} catch (JsonProcessingException e) {
			log.error("Error decoding JWT claims {}", e.getMessage());
			throw new ResponseStatusException(
					HttpStatus.UNAUTHORIZED,
					"Invalid Authorization header");
		}
	}

}
