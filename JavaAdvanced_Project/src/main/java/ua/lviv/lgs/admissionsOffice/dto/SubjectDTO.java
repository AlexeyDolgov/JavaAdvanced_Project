package ua.lviv.lgs.admissionsOffice.dto;

public class SubjectDTO implements Comparable<SubjectDTO>{
	private Integer id;
	private String title;

	public SubjectDTO(Integer id, String title) {
		this.id = id;
		this.title = title;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public int compareTo(SubjectDTO subjectDTO) {
		if (this.id > subjectDTO.id) {
			return 1;
		} else if (this.id < subjectDTO.id) {
			return -1;
		}
		return 0;
	}
}
