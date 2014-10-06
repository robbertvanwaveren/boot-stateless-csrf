package com.jdriven.stateless.security;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = StatelessCSRF.class)
@WebAppConfiguration
@IntegrationTest("server.port:8181")
public class StatelessCSRFIntegrationTest {

	@Test
	public void test_Get_WithoutTokens() {
		ResponseEntity<String> response = http(HttpMethod.GET, "/api/test", null);
		assertEquals("GET Received", response.getBody());
	}

	@Test(expected = HttpClientErrorException.class)
	public void test_Post_WithoutTokens() {
		http(HttpMethod.POST, "/api/test", null);
		fail("should throw the exception above");
	}

	@Test
	public void test_Post_WithTokens() {
		final String clientSecret = "my_little_secret";
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.set("X-CSRF-TOKEN", clientSecret);
		httpHeaders.set("Cookie", "CSRF-TOKEN=" + clientSecret);
		ResponseEntity<String> response = http(HttpMethod.POST, "/api/test", httpHeaders);
		assertEquals("POST Received", response.getBody());
	}

	private ResponseEntity<String> http(final HttpMethod method, final String path, HttpHeaders headers) {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders httpHeaders = headers == null ? new HttpHeaders() : headers;
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<Void> testRequest = new HttpEntity<>(httpHeaders);
		return restTemplate.exchange("http://localhost:8181/" + path, method, testRequest, String.class);
	}
}
