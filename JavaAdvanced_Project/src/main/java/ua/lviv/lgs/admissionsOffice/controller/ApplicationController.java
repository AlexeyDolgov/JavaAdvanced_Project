package ua.lviv.lgs.admissionsOffice.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ua.lviv.lgs.admissionsOffice.domain.Application;
import ua.lviv.lgs.admissionsOffice.domain.User;
import ua.lviv.lgs.admissionsOffice.service.ApplicationService;
import ua.lviv.lgs.admissionsOffice.service.SpecialityService;

@Controller
@RequestMapping("/application")
@PreAuthorize("hasAuthority('USER')")
public class ApplicationController {
	@Autowired
	private ApplicationService applicationService;
	@Autowired
	private SpecialityService specialityService;
	
	@GetMapping
	public String viewApplicationList(HttpServletRequest request, Model model) {
		User user = (User) request.getSession().getAttribute("user");
		List<Application> applicationsList = applicationService.findByApplicant(user.getApplicant());
		model.addAttribute("applications", applicationsList);

		return "applicationList";
	}
	
	@GetMapping("/create")
	public String viewCreationForm(Model model) {
		model.addAttribute("specialities", specialityService.findAll());
		
		return "applicationCreator";
	}
	
	@PostMapping("/create")
	public String createApplication(@RequestParam Map<String, String> form, @Valid Application application, BindingResult bindingResult, Model model) {
		Map<String, String> znoMarksErrors = applicationService.getZnoMarksErrors(form);
		if (bindingResult.hasErrors() || form.get("speciality") == "" || !znoMarksErrors.isEmpty()) {
            Map<String, String> errors = ControllerUtils.getErrors(bindingResult);
            model.mergeAttributes(errors);
            model.addAttribute(!znoMarksErrors.isEmpty() ? "message" : "", "При заполнении баллов по ВНО были обнаружены ошибки: " +
            		znoMarksErrors.values() + ". Попробуйте заполнить форму еще раз!");
            model.addAttribute(form.get("speciality") == "" ? "specialityError" : "", "Поле Специальность не может быть пустым!");
    		model.addAttribute("specialities", specialityService.findAll());
    		
            return "applicationCreator";
        }
   		
		boolean applicationExists = !applicationService.createApplication(application, form);

		if (applicationExists) {
			model.addAttribute("message", "На выбранную специальность заявка уже существует!");
			model.addAttribute("specialities", specialityService.findAll());

			return "applicationCreator";
		}

		return "redirect:/application";
	}
	
	@GetMapping("/edit")
	public String viewEditForm(@RequestParam("id") Application application, Model model) {
		model.addAttribute("aplication", application);
		model.addAttribute("specialities", specialityService.findAll());
		
		return "applicationEditor";
	}
	
	@PostMapping("/edit")
	public String updateApplication(@RequestParam("id") Application application, @RequestParam Map<String, String> form, @Valid Application updatedApplication, BindingResult bindingResult, Model model) {
		updatedApplication.setSpeciality(application.getSpeciality());
		Map<String, String> znoMarksErrors = applicationService.getZnoMarksErrors(form);
		if (bindingResult.hasErrors() || !znoMarksErrors.isEmpty()) {
			Map<String, String> errors = ControllerUtils.getErrors(bindingResult);
			model.mergeAttributes(errors);
			model.mergeAttributes(znoMarksErrors);
			model.addAttribute("aplication", application);
			model.addAttribute("specialities", specialityService.findAll());
			
			return "applicationEditor";
		}
		
		applicationService.updateApplication(updatedApplication, form);

		return "redirect:/application";
	}
	
	@GetMapping("/delete")
	public String deleteApplication(@RequestParam("id") Application application) {
		applicationService.deleteApplication(application);

		return "redirect:/application";
	}
}