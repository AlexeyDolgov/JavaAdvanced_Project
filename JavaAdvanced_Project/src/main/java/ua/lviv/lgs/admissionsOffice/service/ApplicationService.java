package ua.lviv.lgs.admissionsOffice.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ua.lviv.lgs.admissionsOffice.dao.ApplicationRepository;
import ua.lviv.lgs.admissionsOffice.dao.SubjectRepository;
import ua.lviv.lgs.admissionsOffice.domain.Applicant;
import ua.lviv.lgs.admissionsOffice.domain.Application;
import ua.lviv.lgs.admissionsOffice.domain.Subject;

@Service
public class ApplicationService {
	@Autowired
	private ApplicationRepository applicationRepository;
	@Autowired
	private SubjectRepository subjectRepository;

	public List<Application> findByApplicant(Applicant applicant) {
		return applicationRepository.findByApplicant(applicant);
	}
	
	public boolean createApplication(Application application, Map<String, String> form) {
		Optional<Application> applicationFromDb = applicationRepository
				.findByApplicantAndSpeciality(application.getApplicant(), application.getSpeciality());
		
		if (applicationFromDb.isPresent()) {
			return false;
		}

		Map<Subject, Integer> znoMarks = parseZnoMarks(form);
		application.setZnoMarks(znoMarks);

		applicationRepository.save(application);
		return true;
	}
	
	public void updateApplication(Application application, Map<String, String> form) {
		Map<Subject, Integer> znoMarks = parseZnoMarks(form);
		application.setZnoMarks(znoMarks);
		
		applicationRepository.save(application);
	}
	
	public Map<String, String> getZnoMarksErrors(Map<String, String> form) {
		Set<Integer> subjectIds = subjectRepository.findAll().stream().map(Subject::getId).collect(Collectors.toSet());
		Map<String, String> znoMarksErrors = new HashMap<>();

		for (String key : form.keySet()) {
			if (key.startsWith("subject")) {
				Integer keyId = Integer.valueOf(key.replace("subject", ""));
				if (subjectIds.contains(keyId)) {
					Subject subject = subjectRepository.findById(keyId).get();
					if (form.get(key).isEmpty()) {
						znoMarksErrors.put(key + "Error", "Поле баллы по предмету " + subject.getTitle() + " не может быть пустым!");
					}
					if (!form.get(key).isEmpty() && !form.get(key).matches("\\d+")) {
						znoMarksErrors.put(key + "Error", "Баллы по предмету " + subject.getTitle()	+ " должны быть числом!");
					}
					if (!form.get(key).isEmpty() && form.get(key).matches("\\d+")) {
						if (Integer.valueOf(form.get(key)) <= 0) {
							znoMarksErrors.put(key + "Error", "Баллы по предмету " + subject.getTitle()	+ " не могут быть равны нулю!");
						}
						if (Integer.valueOf(form.get(key)) > 200) {
							znoMarksErrors.put(key + "Error", "Баллы по предмету " + subject.getTitle()	+ " не могут быть больше 200!");
						}
					}
				}
			}
		}
		return znoMarksErrors;
	}
	
	public Map<Subject, Integer> parseZnoMarks(Map<String, String> form) {
		Set<Integer> subjectIds = subjectRepository.findAll().stream().map(Subject::getId).collect(Collectors.toSet());
		Map<Subject, Integer> znoMarks = new HashMap<>();

		for (String key : form.keySet()) {
			if (key.startsWith("subject")) {
				Integer keyId = Integer.valueOf(key.replace("subject", ""));
				if (subjectIds.contains(keyId)) {
					Subject subject = subjectRepository.findById(keyId).get();
					znoMarks.put(subject, Integer.valueOf(form.get(key)));
				}
			}
		}
		return znoMarks;
	}

	public void deleteApplication(Application application) {
		applicationRepository.delete(application);		
	}
}
