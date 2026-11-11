package com.iparhotel.api;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iparhotel.repositorios.HabitacionRepository;

/** Pruebas de la API REST con JWT, con la aplicacion completa sobre H2 y los datos de ejemplo. */
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ApiTest {

	@Autowired
	private MockMvc mvc;
	@Autowired
	private ObjectMapper json;
	@Autowired
	private JwtDecoder jwtDecoder;
	@Autowired
	private HabitacionRepository habitacionRepository;

	private final String manana = LocalDate.now().plusDays(1).toString();

	private Long idHabitacion(String numero) {
		return habitacionRepository.findAll().stream().filter(h -> h.getNumero().equals(numero)).findFirst()
				.orElseThrow().getId();
	}

	private String login(String email, String password) throws Exception {
		String respuesta = mvc.perform(post("/api/v2/usuarios/login").contentType(MediaType.APPLICATION_JSON)
						.content("{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}"))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
		JsonNode nodo = json.readTree(respuesta);
		assertEquals("Bearer", nodo.get("tokenType").asText());
		return nodo.get("accessToken").asText();
	}

	private String cuerpo(Object idHabitacion, String fecha, Object noches) {
		return "{\"idHabitacion\":" + idHabitacion + ",\"fechaEntrada\":\"" + fecha + "\",\"noches\":" + noches + "}";
	}

	@Test
	void listadoPublicoDeDisponibles() throws Exception {
		mvc.perform(get("/api/v2/habitaciones"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(5))
				.andExpect(jsonPath("$[0].tipo").value("Individual"))
				.andExpect(jsonPath("$[*].numero", not(hasItem("202")))); // ocupada en los datos de ejemplo
	}

	@Test
	void loginDevuelveUnJwtConEmailNombreYRol() throws Exception {
		String token = login("ana@iparhotel.com", "ana123");

		Jwt jwt = jwtDecoder.decode(token); // comprueba firma HS256, caducidad y emisor
		assertEquals("ana@iparhotel.com", jwt.getSubject());
		assertEquals("Ana López", jwt.getClaimAsString("nombre"));
		assertEquals("CLIENTE", jwt.getClaimAsString("rol"));
		assertEquals("HS256", jwt.getHeaders().get("alg").toString());
	}

	@Test
	void loginIncorrectoDevuelve401() throws Exception {
		mvc.perform(post("/api/v2/usuarios/login").contentType(MediaType.APPLICATION_JSON)
						.content("{\"email\":\"ana@iparhotel.com\",\"password\":\"mala\"}"))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.detail").value("Email o contraseña incorrectos"));
	}

	@Test
	void reservarSinTokenDevuelve401() throws Exception {
		mvc.perform(post("/api/v2/reservas").contentType(MediaType.APPLICATION_JSON)
						.content(cuerpo(idHabitacion("101"), manana, 2)))
				.andExpect(status().isUnauthorized());
		mvc.perform(post("/api/v2/reservas").header("Authorization", "Bearer token.falso.x")
						.contentType(MediaType.APPLICATION_JSON).content(cuerpo(idHabitacion("101"), manana, 2)))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void reservaCorrecta201YSegundaVez409() throws Exception {
		String token = login("javier@iparhotel.com", "javier123");
		Long suite = idHabitacion("301");

		mvc.perform(post("/api/v2/reservas").header("Authorization", "Bearer " + token)
						.contentType(MediaType.APPLICATION_JSON).content(cuerpo(suite, manana, 5)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.importe").value(720.00))
				.andExpect(jsonPath("$.emailUsuario").value("javier@iparhotel.com")) // sacado del token
				.andExpect(jsonPath("$.tipoHabitacion").value("Suite"));

		mvc.perform(post("/api/v2/reservas").header("Authorization", "Bearer " + token)
						.contentType(MediaType.APPLICATION_JSON).content(cuerpo(suite, manana, 2)))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.motivo").value("HABITACION_NO_DISPONIBLE"));
	}

	@Test
	void datosNoValidos400YHabitacionInexistente404() throws Exception {
		String token = login("javier@iparhotel.com", "javier123");

		mvc.perform(post("/api/v2/reservas").header("Authorization", "Bearer " + token)
						.contentType(MediaType.APPLICATION_JSON)
						.content(cuerpo(idHabitacion("101"), LocalDate.now().toString(), 15)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errores.noches").value("Máximo 14 noches"))
				.andExpect(jsonPath("$.errores.fechaEntrada").exists());

		mvc.perform(post("/api/v2/reservas").header("Authorization", "Bearer " + token)
						.contentType(MediaType.APPLICATION_JSON).content(cuerpo(idHabitacion("101"), "no-es-fecha", 2)))
				.andExpect(status().isBadRequest());

		mvc.perform(post("/api/v2/reservas").header("Authorization", "Bearer " + token)
						.contentType(MediaType.APPLICATION_JSON).content(cuerpo(9999, manana, 2)))
				.andExpect(status().isNotFound());
	}

	@Test
	void corsPermiteAngularEnLocalhost4200() throws Exception {
		mvc.perform(options("/api/v2/reservas")
						.header("Origin", "http://localhost:4200")
						.header("Access-Control-Request-Method", "POST")
						.header("Access-Control-Request-Headers", "authorization,content-type"))
				.andExpect(status().isOk())
				.andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:4200"));

		mvc.perform(options("/api/v2/reservas")
						.header("Origin", "http://otra-web.com")
						.header("Access-Control-Request-Method", "POST"))
				.andExpect(status().isForbidden());
	}
}
