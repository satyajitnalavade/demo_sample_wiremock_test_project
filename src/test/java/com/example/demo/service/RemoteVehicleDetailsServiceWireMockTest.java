package com.example.demo.service;



import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.client.HttpServerErrorException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit.WireMockRule;


/**
 * Tests for {@link RemoteVehicleDetailsService}.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RemoteVehicleDetailsServiceWireMockTest {
	
	private static final String VIN = "01234567890123456";

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Rule
	public WireMockRule wireMock = new WireMockRule(
			WireMockConfiguration.options().dynamicPort());

	@Autowired
	private RemoteVehicleDetailsServiceProperties properties;
	
	@Autowired
	private RemoteVehicleDetailsService service;
	
	@Autowired
	private ObjectMapper objectMapper;

	
	@Before
	public void setup() {
		this.properties.setRootUrl("http://localhost:" + this.wireMock.port() + '/');
	}
	
	@Test
	public void getVehicleDetailsWhenVinIsNullShouldThrowException() throws Exception {
		this.thrown.expect(IllegalArgumentException.class);
		this.thrown.expectMessage("vin should not be null");
		this.service.getVehicleDetails(null);
	}
	
	@Test
	public void getVehicleDetailsWhenResultIsSuccessShouldReturnDetails()
			throws Exception {
		String vehicleDetailsString = objectMapper.writeValueAsString(new VehicleDetails("Honda", "Civic"));
		this.wireMock.stubFor(get(urlEqualTo("/vehicle/" + VIN + "/details"))
				.willReturn(aResponse().withStatus(200)
						.withHeader(HttpHeaders.CONTENT_TYPE,
								MediaType.APPLICATION_JSON_VALUE)
						.withBody(vehicleDetailsString)));
		VehicleDetails details = this.service
				.getVehicleDetails(new VehicleIdentificationNumber(VIN));
		assertThat(details.getMake()).isEqualTo("Honda");
		assertThat(details.getModel()).isEqualTo("Civic");
	}
	
	@Test
	public void getVehicleDetailsWhenResultIsNotFoundShouldThrowException()
			throws Exception {
		this.wireMock.stubFor(get(urlEqualTo("/vehicle/" + VIN + "/details"))
				.willReturn(aResponse().withStatus(404)));
		this.thrown.expect(VehicleIdentificationNumberNotFoundException.class);
		this.service.getVehicleDetails(new VehicleIdentificationNumber(VIN));
	}

	@Test
	public void getVehicleDetailsWhenResultIServerErrorShouldThrowException()
			throws Exception {
		this.wireMock.stubFor(get(urlEqualTo("/vehicle/" + VIN + "/details"))
				.willReturn(aResponse().withStatus(500)));
		this.thrown.expect(HttpServerErrorException.class);
		this.service.getVehicleDetails(new VehicleIdentificationNumber(VIN));
	}

	
	
	

	
}
