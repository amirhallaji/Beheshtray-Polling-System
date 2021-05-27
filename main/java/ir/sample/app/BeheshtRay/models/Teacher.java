package ir.sample.app.BeheshtRay.models;

import java.util.ArrayList;

public class Teacher {
    public String teacher_name;
    public String teacher_email;
    public String teacher_academic_group;
    public String lesson_name;
	public ArrayList<Comment> teacher_comments = new ArrayList<>();

	public Teacher(String teacher_name, String lesson_name) {
		this.teacher_name = teacher_name;
		this.lesson_name = lesson_name;
	}

	public Teacher () {

	}

}
