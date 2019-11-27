package ua.lviv.lgs.admissionsOffice.controller;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import ua.lviv.lgs.admissionsOffice.domain.AccessLevel;
import ua.lviv.lgs.admissionsOffice.domain.User;
import ua.lviv.lgs.admissionsOffice.service.ApplicationService;
import ua.lviv.lgs.admissionsOffice.service.RatingListService;
import ua.lviv.lgs.admissionsOffice.service.UserService;

@Controller
@RequestMapping("/main")
public class MainController {
	@Autowired
	private UserService userService;
	@Autowired
	private ApplicationService applicationService;
	@Autowired
	private RatingListService ratingListService;
		
	@GetMapping
	public String viewMainPage(HttpSession session, Model model) throws UnsupportedEncodingException {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = (User) auth.getPrincipal();
		User userFromDb = userService.findById(user.getId());
		
		session.setAttribute("user", userFromDb);
		
		if (userFromDb.getAccessLevels().contains(AccessLevel.valueOf("USER"))) {
			session.setAttribute("photo", userService.parseFileData(userFromDb));
			session.setAttribute("specialities", ratingListService.findSpecialitiesByApplicant(userFromDb.getId()));
			model.addAttribute("submittedApps", ratingListService.parseApplicationsBySpeciality());
			model.addAttribute("isRejectedAppsPresent", applicationService.checkForRejectedApplications(applicationService.findByApplicant(userFromDb.getApplicant())));
		}
		
		if (userFromDb.getAccessLevels().contains(AccessLevel.valueOf("ADMIN"))) {
			session.setAttribute("notAcceptedApps", ratingListService.findNotAcceptedApps());
		}
		
		return "main";
	}
}
