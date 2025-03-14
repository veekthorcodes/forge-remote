package com.onpointserv.forge_remote;

import org.springframework.stereotype.Component;

@Component
class ForgeContext {

	private static final ThreadLocal<ForgeInvocationToken> fit = new ThreadLocal<>();
	private static final ThreadLocal<String> systemToken = new ThreadLocal<>();
	private static final ThreadLocal<String> userToken = new ThreadLocal<>();

	/**
	 * Sets the current Token
	 * 
	 * @param token {@link ForgeInvocationToken}
	 */
	static void setFit(ForgeInvocationToken token) {
		fit.set(token);
	}

	/**
	 * Gets {@link ForgeInvocationToken}
	 * 
	 * @return The Token or null if it is not set
	 */
	static ForgeInvocationToken getFit() {
		return fit.get();
	}

	/**
	 * Sets the system token
	 * 
	 * @param systemToken {@link String}
	 */
	static void setSystemToken(String token) {
		systemToken.set(token);
	}

	/**
	 * Gets the system token
	 * 
	 * @return {@link String}
	 */
	static String getSystemToken() {
		return systemToken.get();
	}

	/**
	 * Sets the user token
	 * 
	 * @param userToken {@link String}
	 */
	static void setUserToken(String token) {
		userToken.set(token);
	}

	/**
	 * Gets the user token
	 * 
	 * @return {@link String}
	 */
	static String getUserToken() {
		return userToken.get();
	}

	/**
	 * Clears all the Tokens from the thread
	 * Should be called and the end of request processing to prevent memory leaks
	 */
	static void clear() {
		fit.remove();
		systemToken.remove();
		userToken.remove();
	}
}
