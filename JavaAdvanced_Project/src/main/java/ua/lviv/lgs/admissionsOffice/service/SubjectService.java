package ua.lviv.lgs.admissionsOffice.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ua.lviv.lgs.admissionsOffice.dao.SubjectRepository;
import ua.lviv.lgs.admissionsOffice.domain.Subject;

@Service
public class SubjectService {
	@Autowired
	private SubjectRepository subjectRepository;

	public List<Subject> findAll() {
		return subjectRepository.findAll();
	}

	public boolean createSubject(Subject subject) {
		Optional<Subject> subjectFromDb = subjectRepository.findByTitle(subject.getTitle());

		if (subjectFromDb.isPresent()) {
			return false;
		}

		subjectRepository.save(subject);
		return true;
	}

	public void saveSubject(Subject subject) {
		subjectRepository.save(subject);
	}

	public void deleteSubject(Subject subject) {
		subjectRepository.delete(subject);		
	}
}
