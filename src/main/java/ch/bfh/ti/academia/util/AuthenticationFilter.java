/*
 * Academia (c) 2021, Bern University of Applied Sciences, Switzerland
 */

package ch.bfh.ti.academia.util;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.logging.Logger;

import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

/**
 * The class AuthenticationFilter is used to authenticate HTTP requests.
 */
@WebFilter(urlPatterns = "/api/*")
public class AuthenticationFilter extends HttpFilter {

	private static final Logger logger = Logger.getLogger(AuthenticationFilter.class.getName());

	private static final String AUTH_HEADER = "Authorization";
	private static final String AUTH_SCHEME = "Basic";
	private static final String USERNAME = "admin";
	private static final String PASSWORD = "admin";

	@Override
	public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		if (!request.getMethod().equals("GET")) {
			try {
				String[] credentials = getCredentials(request);
				validateCredentials(credentials);
				logger.info("User " + credentials[0] + " authenticated");
			} catch (Exception ex) {
				logger.info("Authentication failed");
				response.setStatus(SC_UNAUTHORIZED);
				return;
			}
		}
		chain.doFilter(request, response);
	}

	private String[] getCredentials(HttpServletRequest request) throws Exception {
		String authHeader = request.getHeader(AUTH_HEADER);
		String[] headerTokens = authHeader.split(" ");
		if (!headerTokens[0].equals(AUTH_SCHEME)) throw new Exception();
		byte[] decoded = Base64.getDecoder().decode(headerTokens[1]);
		return new String(decoded, StandardCharsets.UTF_8).split(":");
	}

	private void validateCredentials(String[] credentials) throws Exception {
		if (!credentials[0].equals(USERNAME) || !credentials[1].equals(PASSWORD)) {
			throw new Exception();
		}
	}
}
