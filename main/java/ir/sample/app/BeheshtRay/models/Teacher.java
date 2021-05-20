package ir.sample.app.BeheshtRay.models;

import ir.sample.app.BeheshtRay.models.Comment;

import java.util.ArrayList;

public class Teacher {
    public String teacherName;
    public String teacherEmail;
    public String teacherAcademicGroup;
    public String teacherLessons;
	public ArrayList<Comment> teacherComments = new ArrayList<>();

	public Teacher() {
	}

	public String getTeacherName() {
		return teacherName;
	}

	public void setTeacherName(String teacherName) {
		this.teacherName = teacherName;
	}

	public String getTeacherEmail() {
		return teacherEmail;
	}

	public void setTeacherEmail(String teacherEmail) {
		this.teacherEmail = teacherEmail;
	}

	public String getTeacherAcademicGroup() {
		return teacherAcademicGroup;
	}

	public void setTeacherAcademicGroup(String teacherAcademicGroup) {
		this.teacherAcademicGroup = teacherAcademicGroup;
	}

	public String getTeacherLessons() {
		return teacherLessons;
	}

	public void setTeacherLessons(String teacherLessons) {
		this.teacherLessons = teacherLessons;
	}

	public ArrayList<Comment> getTeacherComments() {
		return teacherComments;
	}

	public void setTeacherComments(ArrayList<Comment> teacherComments) {
		this.teacherComments = teacherComments;
	}
}
