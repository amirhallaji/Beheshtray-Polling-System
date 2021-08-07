package ir.sample.app.BeheshtRay.models;

import java.util.ArrayList;

public class Teacher {
	private String teacherName;
	private String lessonName;
	private String email;
	private String academicGroup;
	private String photoURL;
	private int facultyId;
	private int teachingId;

	public Teacher(String teacherName, String lessonName, String email, String academicGroup, String photoURL, int facultyId) {
		this.teacherName = teacherName;
		this.lessonName = lessonName;
		this.email = email;
		this.academicGroup = academicGroup;
		this.photoURL = photoURL;
		this.facultyId = facultyId;
	}

	public Teacher() {
	}

	public String getTeacherName() {
		return teacherName;
	}

	public String getLessonName() {
		return lessonName;
	}

	public String getEmail() {
		return email;
	}

	public String getAcademicGroup() {
		return academicGroup;
	}

	public String getPhotoURL() {
		return photoURL;
	}

	public int getFacultyId() {
		return facultyId;
	}

	public int getTeachingId() {
		return teachingId;
	}

	public void setTeacherName(String teacherName) {
		this.teacherName = teacherName;
	}

	public void setLessonName(String lessonName) {
		this.lessonName = lessonName;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setAcademicGroup(String academicGroup) {
		this.academicGroup = academicGroup;
	}

	public void setPhotoURL(String photoURL) {
		this.photoURL = photoURL;
	}

	public void setFacultyId(int facultyId) {
		this.facultyId = facultyId;
	}

	public void setTeachingId(int teachingId) {
		this.teachingId = teachingId;
	}

	private String averageScore;
	private String numberTitle;

	public String getAverageScore() {
		return averageScore;
	}

	public String getNumberTitle() {
		return numberTitle;
	}

	public void setAverageScore(String averageScore) {
		this.averageScore = averageScore;
	}

	public void setNumberTitle(String numberTitle) {
		this.numberTitle = numberTitle;
	}
}
