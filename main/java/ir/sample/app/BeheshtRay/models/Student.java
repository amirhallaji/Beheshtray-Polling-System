package ir.sample.app.BeheshtRay.models;

public class Student {

	private String studentFirstName;
	private String studentLastName;

	private String studentFullName;

	private String studentId;
	private String studentFacultyId;
	private String userId;


	private String studentGender;
	private String studentPhotoURL;
	private String userKarma;

	public Student(String userId) {
		this.userId = userId;
		this.userKarma = "0";
	}

	public void setStudentFullName(String studentFullName) {
		this.studentFullName = studentFullName;
	}

	public String getStudentFullName() {
		return studentFirstName + " " + studentLastName;
	}




	public String getStudentFirstName() {
		return studentFirstName;
	}

	public String getStudentLastName() {
		return studentLastName;
	}


	public String getStudentId() {
		return studentId;
	}

	public String getStudentFacultyId() {
		return studentFacultyId;
	}

	public String getUserId() {
		return userId;
	}

	public String getStudentGender() {
		return studentGender;
	}

	public String getStudentPhotoURL() {
		return studentPhotoURL;
	}

	public String getUserKarma() {
		return userKarma;
	}

	public void setStudentFirstName(String studentFirstName) {
		this.studentFirstName = studentFirstName;
	}

	public void setStudentLastName(String studentLastName) {
		this.studentLastName = studentLastName;
	}



	public void setStudentId(String studentId) {
		this.studentId = studentId;
	}

	public void setStudentFacultyId(String studentFacultyId) {
		this.studentFacultyId = studentFacultyId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public void setStudentGender(String studentGender) {
		this.studentGender = studentGender;
	}

	public void setStudentPhotoURL(String gender) {
		if (gender.equals("مرد")) {
			this.studentPhotoURL = "https://s4.uupload.ir/files/cfee5087-8773-4fb3-ac5e-63372d889b1f_ks1c.png";
		} else if (gender.equals("زن")) {
			this.studentPhotoURL = "https://s4.uupload.ir/files/3b786101-e336-4e3d-96bb-a73d2227b8d2_n9a3.png";
		} else {
			this.studentPhotoURL = "https://s4.uupload.ir/files/9446101f-27b4-4f8f-9761-0397d7ea932e_mcg1.png";
		}
	}

	public void setUserKarma(String userKarma) {
		this.userKarma = userKarma;
	}
}
