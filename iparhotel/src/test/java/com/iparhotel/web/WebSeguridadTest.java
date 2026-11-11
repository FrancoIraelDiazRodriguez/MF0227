package com.iparhotel.web;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import com.iparhotel.repositorios.HabitacionRepository;

/** Arranca la aplicacion completa con H2 y los datos de ejemplo. */
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class WebSeguridadTest {

	@Autowired
	private MockMvc mvc;
	@Autowired
	private HabitacionRepository habitacionRepository;

	private Long idHabitacion(String numero) {
		return habitacionRepository.findAll().stream().filter(h -> h.getNumero().equals(numero)).findFirst()
				.orElseThrow().getId();
	}

	@Test
	void elInicioEsPublicoYSoloMuestraDisponibles() throws Exception {
		mvc.perform(get("/"))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("Suite")))
				.andExpect(content().string(containsString("Iniciar sesión")))
				.andExpect(content().string(not(containsString(">202<")))); // la 202 esta ocupada
	}

	@Test
	void loginContraLaBaseDeDatos() throws Exception {
		mvc.perform(formLogin("/login").user("email", "ana@iparhotel.com").password("ana123"))
				.andExpect(authenticated().withRoles("CLIENTE"));
		mvc.perform(formLogin("/login").user("email", "ana@iparhotel.com").password("mala"))
				.andExpect(unauthenticated())
				.andExpect(redirectedUrl("/login?error"));
	}

	@Test
	void sinSesionSeRedirigeAlLogin() throws Exception {
		mvc.perform(get("/reservas")).andExpect(redirectedUrlPattern("**/login"));
		mvc.perform(get("/habitacion/" + idHabitacion("101"))).andExpect(redirectedUrlPattern("**/login"));
		mvc.perform(get("/admin")).andExpect(redirectedUrlPattern("**/login"));
	}

	@Test
	@WithUserDetails("ana@iparhotel.com")
	void unClienteNoEntraEnAdmin() throws Exception {
		mvc.perform(get("/admin")).andExpect(status().isForbidden());
		mvc.perform(get("/")).andExpect(content().string(not(containsString("Administración</a>"))));
	}

	@Test
	@WithUserDetails("admin@iparhotel.com")
	void elAdministradorVeTodasLasReservas() throws Exception {
		mvc.perform(get("/admin"))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("ana@iparhotel.com")));
	}

	@Test
	@WithUserDetails("javier@iparhotel.com")
	void reservaCorrectaRedirigeConMensajeFlash() throws Exception {
		Long id = idHabitacion("301");
		mvc.perform(post("/habitacion/" + id).with(csrf())
						.param("fechaEntrada", LocalDate.now().plusDays(7).toString())
						.param("noches", "5"))
				.andExpect(redirectedUrl("/reservas"))
				.andExpect(flash().attribute("mensaje", containsString("720.00")));

		mvc.perform(get("/reservas")).andExpect(content().string(containsString("720,00 €")));
		// Ya no aparece como disponible ni se puede volver a reservar
		mvc.perform(get("/habitacion/" + id)).andExpect(redirectedUrl("/"))
				.andExpect(flash().attribute("error", containsString("no está disponible")));
	}

	@Test
	@WithUserDetails("javier@iparhotel.com")
	void formularioNoValidoVuelveConErrores() throws Exception {
		mvc.perform(post("/habitacion/" + idHabitacion("101")).with(csrf())
						.param("fechaEntrada", LocalDate.now().toString())
						.param("noches", "20"))
				.andExpect(status().isOk())
				.andExpect(view().name("habitacion"))
				.andExpect(model().attributeHasFieldErrors("reserva", "fechaEntrada", "noches"))
				.andExpect(content().string(containsString("Máximo 14 noches")));
	}

	@Test
	@WithUserDetails("javier@iparhotel.com")
	void sinTokenCsrfNoSePuedeReservar() throws Exception {
		mvc.perform(post("/habitacion/" + idHabitacion("101"))
						.param("fechaEntrada", LocalDate.now().plusDays(7).toString())
						.param("noches", "2"))
				.andExpect(status().isForbidden());
	}
}
