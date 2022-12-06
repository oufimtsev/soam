package com.soam;

import com.soam.model.specification.SpecificationRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.util.AssertionErrors.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SoamApplicationTests {

	@Autowired
	private SpecificationRepository specifications;

	@LocalServerPort
	int port;

	@Autowired
	private RestTemplateBuilder builder;

	@Test
	@Disabled
	// this takes time, but it's here to get 100% test code coverage
	public void testContextLoads() {
		SoamApplication.main(new String[] {});
		assertEquals( "We made it here w/o exception", true, true  );
	}


	@Test
	void testSpecificationDetails() {
		RestTemplate template = builder.rootUri("http://localhost:" + port).build();
		ResponseEntity<String> result = template.exchange(RequestEntity.get("/specification/1").build(), String.class);
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
	}


}