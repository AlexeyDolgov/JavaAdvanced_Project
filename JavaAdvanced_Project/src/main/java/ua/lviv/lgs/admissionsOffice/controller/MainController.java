package ua.lviv.lgs.admissionsOffice.controller;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import ua.lviv.lgs.admissionsOffice.domain.User;
import ua.lviv.lgs.admissionsOffice.service.UserService;

@Controller
@RequestMapping("/main")
public class MainController {
	@Autowired
	private UserService userService;
		
	@GetMapping
	public String viewMainPage(HttpSession session) throws UnsupportedEncodingException {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = (User) auth.getPrincipal();
		User userFromDb = userService.findById(user.getId());
		
		session.setAttribute("user", userFromDb);
		session.setAttribute("photo", userService.parseFileData(userFromDb));
		
		return "main";
	}
}
