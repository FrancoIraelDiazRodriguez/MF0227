package com.iparhotel.web;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.iparhotel.dtos.ReservaDto;
import com.iparhotel.dtos.ReservaFormDto;
import com.iparhotel.excepciones.ReservaException;
import com.iparhotel.servicios.ReservaService;

import jakarta.validation.Valid;

@Controller
public class HotelController {

	private final ReservaService reservaService;

	public HotelController(ReservaService reservaService) {
		this.reservaService = reservaService;
	}

	@GetMapping("/")
	public String inicio(Model model) {
		model.addAttribute("habitaciones", reservaService.listarDisponibles());
		return "index";
	}

	@GetMapping("/habitacion/{id}")
	public String formularioReserva(@PathVariable Long id, Model model) {
		model.addAttribute("habitacion", reservaService.buscarDisponible(id));
		model.addAttribute("reserva", new ReservaFormDto());
		return "habitacion";
	}

	@PostMapping("/habitacion/{id}")
	public String reservar(@PathVariable Long id, @Valid @ModelAttribute("reserva") ReservaFormDto form,
			BindingResult resultado, Principal principal, Model model, RedirectAttributes redirect) {
		if (!resultado.hasErrors()) {
			try {
				// El email sale de la sesion, nunca del formulario
				ReservaDto reserva = reservaService.reservar(id, form.getFechaEntrada(), form.getNoches(),
						principal.getName());
				redirect.addFlashAttribute("mensaje", "Reserva confirmada: habitación " + reserva.getNumeroHabitacion()
						+ ", " + reserva.getNoches() + " noche(s), importe " + reserva.getImporte() + " €");
				return "redirect:/reservas";
			} catch (ReservaException e) {
				resultado.reject("reserva", e.getMessage());
			}
		}
		// Hay errores: se vuelve a mostrar el formulario con lo que escribio el usuario
		model.addAttribute("habitacion", reservaService.buscarDisponible(id));
		return "habitacion";
	}

	@GetMapping("/reservas")
	public String misReservas(Principal principal, Model model) {
		model.addAttribute("reservas", reservaService.misReservas(principal.getName()));
		return "reservas";
	}

	@GetMapping("/admin")
	public String admin(Model model) {
		model.addAttribute("reservas", reservaService.todasLasReservas());
		return "admin";
	}

	@GetMapping("/login")
	public String login() {
		return "login";
	}
}
