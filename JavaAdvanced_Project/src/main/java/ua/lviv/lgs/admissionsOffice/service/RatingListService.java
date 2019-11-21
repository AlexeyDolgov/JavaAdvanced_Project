package ua.lviv.lgs.admissionsOffice.service;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ua.lviv.lgs.admissionsOffice.dao.RatingListRepository;
import ua.lviv.lgs.admissionsOffice.domain.Application;
import ua.lviv.lgs.admissionsOffice.domain.RatingList;
import ua.lviv.lgs.admissionsOffice.domain.Subject;

@Service
public class RatingListService {
	@Autowired
	private RatingListRepository ratingListRepository;
	
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
}
