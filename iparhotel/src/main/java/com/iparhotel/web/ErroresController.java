package com.iparhotel.web;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.iparhotel.excepciones.ReservaException;

/** Una ReservaException fuera del formulario (habitacion inexistente u ocupada) vuelve al inicio con el aviso. */
// Solo para los controladores web: la API tiene su propio ApiExceptionHandler
@ControllerAdvice(basePackages = "com.iparhotel.web")
public class ErroresController {

	@ExceptionHandler(ReservaException.class)
	public String reservaException(ReservaException e, RedirectAttributes redirect) {
		redirect.addFlashAttribute("error", e.getMessage());
		return "redirect:/";
	}
}
