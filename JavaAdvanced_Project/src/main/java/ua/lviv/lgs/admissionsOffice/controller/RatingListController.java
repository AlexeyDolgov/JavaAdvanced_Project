package ua.lviv.lgs.admissionsOffice.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ua.lviv.lgs.admissionsOffice.domain.Applicant;
import ua.lviv.lgs.admissionsOffice.domain.Speciality;
import ua.lviv.lgs.admissionsOffice.service.RatingListService;

@Controller
@RequestMapping("/ratingList")
public class RatingListController {
	@Autowired
	private RatingListService ratingListService;
	
	@GetMapping("/speciality")
	public String viewApplicantsRankBySpeciality(@RequestParam("id") Speciality speciality, HttpServletRequest request, Model model) throws URISyntaxException {
		Map<Double, Applicant> applicantsRank = ratingListService.parseApplicantsRankBySpeciality(speciality.getId());
		model.addAttribute("speciality", speciality);
		model.addAttribute("applicantsRank", applicantsRank);
		model.addAttribute("refererURI", new URI(request.getHeader("referer")).getPath());

		return "ratingList";
	}
}
