package ua.lviv.lgs.admissionsOffice.controller;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import ua.lviv.lgs.admissionsOffice.dao.UserRepository;
import ua.lviv.lgs.admissionsOffice.domain.AccessLevel;
import ua.lviv.lgs.admissionsOffice.domain.User;

@Controller
@RequestMapping("/registration")
public class RegistrationController {
	@Autowired
	private UserRepository userRepository;

	@GetMapping
	public String viewRegistrationForm() {
		return "registration";
	}

	@PostMapping
	public String addUser(User user, Model model) {
		User userFromDb = userRepository.findByEmail(user.getEmail());

		if (userFromDb != null) {
			model.addAttribute("message", "Такой пользователь уже существует!");
			return "registration";
		}

		user.setActive(true);
		user.setAccessLevels(Collections.singleton(AccessLevel.USER));
		userRepository.save(user);

		return "redirect:/login";
	}
}