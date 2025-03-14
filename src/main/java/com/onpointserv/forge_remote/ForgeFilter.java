package com.onpointserv.forge_remote;

import java.io.IOException;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
class ForgeFilter extends OncePerRequestFilter {

	private final FitValidator validator;

	ForgeFilter(FitValidator validator) {
		this.validator = validator;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		if (request.getRequestURI().equals("/")) {
			filterChain.doFilter(request, response);
			return;
		}

		String authorization = request.getHeader("Authorization");
		if (authorization == null) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}

		try {
			ForgeContext.setUserToken(request.getHeader(ForgeHeaders.X_FORGE_OAUTH_USER));
			ForgeContext.setSystemToken(request.getHeader(ForgeHeaders.X_FORGE_OAUTH_SYSTEM));

			ForgeInvocationToken fit = validator.getFit(authorization);
			ForgeContext.setFit(fit);
			filterChain.doFilter(request, response);
		} finally {
			ForgeContext.clear();
		}
	}
}
