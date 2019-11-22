package ua.lviv.lgs.admissionsOffice.service;

import java.math.BigInteger;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ua.lviv.lgs.admissionsOffice.dao.ApplicantRepository;
import ua.lviv.lgs.admissionsOffice.dao.RatingListRepository;
import ua.lviv.lgs.admissionsOffice.dao.SpecialityRepository;
import ua.lviv.lgs.admissionsOffice.domain.Applicant;
import ua.lviv.lgs.admissionsOffice.domain.Application;
import ua.lviv.lgs.admissionsOffice.domain.RatingList;
import ua.lviv.lgs.admissionsOffice.domain.Speciality;
import ua.lviv.lgs.admissionsOffice.domain.Subject;
import ua.lviv.lgs.admissionsOffice.dto.SpecialityDTO;

@Service
public class RatingListService {
	@Autowired
	private RatingListRepository ratingListRepository;
	@Autowired
	private SpecialityRepository specialityRepository;
	@Autowired
	private ApplicantRepository applicantRepository;
	
	public Optional<RatingList> findById(Integer id) {
		return ratingListRepository.findById(id);
	}

	public RatingList initializeRatingList(Application application) {
		Optional<RatingList> ratingListFromDb = findById(application.getId());
		RatingList ratingList = ratingListFromDb.orElse(new RatingList());
		
		ratingList.setId(application.getId());
		
		Double totalMark = calculateTotalMark(application.getZnoMarks(), application.getAttMark());
		ratingList.setTotalMark(totalMark);
		ratingList.setApplication(application);
		
		return ratingList;
	}

	public Double calculateTotalMark(Map<Subject, Integer> znoMarks, Integer attMark) {
		Integer i = 1;
		Double totalMark = Double.valueOf(attMark);
		
		for (Integer znoMark : znoMarks.values()) {
			i += 1;
			totalMark += znoMark;
		}
		
		totalMark = totalMark/i;
		
		return totalMark;
	}

	public Map<Speciality, Integer> parseApplicationsBySpeciality() {
		List<Object[]> submittedAppsFromDb = ratingListRepository.countApplicationsBySpeciality();
		List<Speciality> specialitiesList = specialityRepository.findAll();
		Map<Speciality, Integer> submittedApps = new HashMap<>();
		
		for (Speciality speciality : specialitiesList) {
			for (Object[] object : submittedAppsFromDb) {
				
				if (((Integer) object[0]).equals(speciality.getId())) {
					submittedApps.put(speciality, ((BigInteger) object[1]).intValue());
					break;
				} else {
					submittedApps.put(speciality, 0);
				}
			}
		}
		return submittedApps;
	}
	
	public Map<Double, Applicant> parseApplicantsRankBySpeciality(Integer specialityId) {
		List<Object[]> applicantsRankFromDb = ratingListRepository.getApplicantsRankBySpeciality(specialityId);
		List<Applicant> applicantsList = applicantRepository.findAll();
		Map<Double, Applicant> applicantsRank = new TreeMap<>(Collections.reverseOrder());
		
		for (Applicant applicant : applicantsList) {
			for (Object[] object : applicantsRankFromDb) {
				if (((Integer) object[1]).equals(applicant.getId())) {
					applicantsRank.put((Double) object[0], applicant);
				}
			}
		}
		return applicantsRank;
	}
	
	public List<Speciality> findSpecialitiesByApplicant(Integer applicantId) {
		List<Integer> specialitiesByApplicantFromDb = ratingListRepository.findSpecialitiesByApplicant(applicantId);
		List<Speciality> specialitiesList = specialityRepository.findAll();
		
		return specialitiesList.stream()
				.filter(speciality -> specialitiesByApplicantFromDb.stream()
						.anyMatch(specialityId -> specialityId.equals(speciality.getId())))
				.collect(Collectors.toList());
	}
	
	public Set<SpecialityDTO> parseSpecialitiesByApplicant(Integer applicantId) {
		List<Speciality> specialities = findSpecialitiesByApplicant(applicantId);

		return specialities.stream().map(speciality -> new SpecialityDTO(speciality.getId(), speciality.getTitle()))
				.collect(Collectors.toCollection(TreeSet::new));
	}
}
