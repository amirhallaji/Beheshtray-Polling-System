package ir.sample.app.BeheshtRay.models;

import java.util.ArrayList;

public class Comment {
    public String commentId;
	public Teacher commentTeacher;
	public Student commentStudent;
	public String commentText;
	public String commentScore;
	public String commentUpvote;
	public String commentDownvote;

	public Comment(String commentId) {
		this.commentId = commentId;
		//todo assign comment id
	}

	public String getCommentId() {
		return commentId;
	}

	public void setCommentId(String commentId) {
		this.commentId = commentId;
	}

	public Teacher getCommentTeacher() {
		return commentTeacher;
	}

	public void setCommentTeacher(Teacher commentTeacher) {
		this.commentTeacher = commentTeacher;
	}

	public Student getCommentStudent() {
		return commentStudent;
	}

	public void setCommentStudent(Student commentStudent) {
		this.commentStudent = commentStudent;
	}

	public String getCommentText() {
		return commentText;
	}

	public void setCommentText(String commentText) {
		this.commentText = commentText;
	}

	public String getCommentScore() {
		return commentScore;
	}

	public void setCommentScore(String commentScore) {
		this.commentScore = commentScore;
	}

	public String getCommentUpvote() {
		return commentUpvote;
	}

	public void setCommentUpvote(String commentUpvote) {
		this.commentUpvote = commentUpvote;
	}

	public String getCommentDownvote() {
		return commentDownvote;
	}

	public void setCommentDownvote(String commentDownvote) {
		this.commentDownvote = commentDownvote;
	}
}
