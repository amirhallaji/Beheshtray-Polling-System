package ir.sample.app.BeheshtRay.models;

public class Feedback {
    private int teachingId;
    private String userId;

    private double score1;
    private double score2;
    private double score3;
    private double score4;

    private String studentScore;
    private String extendedFeedback;

    private String persianDate;

    private String upVotes;
    private String downVotes;

    private int feedbackId;

    public Feedback(int teachingId, String userId, double score1, double score2, double score3, double score4, String studentScore, String extendedFeedback, String persianDate) {
        this.teachingId = teachingId;
        this.userId = userId;
        this.score1 = score1;
        this.score2 = score2;
        this.score3 = score3;
        this.score4 = score4;
        this.studentScore = studentScore;
        this.extendedFeedback = extendedFeedback;
        this.persianDate = persianDate;
    }

    public Feedback() {

    }

    public int getTeachingId() {
        return teachingId;
    }

    public String getUserId() {
        return userId;
    }

    public double getScore1() {
        return score1;
    }

    public double getScore2() {
        return score2;
    }

    public double getScore3() {
        return score3;
    }

    public double getScore4() {
        return score4;
    }

    public String getStudentScore() {
        return studentScore;
    }

    public String getExtendedFeedback() {
        return extendedFeedback;
    }

    public String getPersianDate() {
        return persianDate;
    }

    public String getUpVotes() {
        return upVotes;
    }

    public String getDownVotes() {
        return downVotes;
    }

    public int getFeedbackId() {
        return feedbackId;
    }


    public void setTeachingId(int teachingId) {
        this.teachingId = teachingId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setScore1(double score1) {
        this.score1 = score1;
    }

    public void setScore2(double score2) {
        this.score2 = score2;
    }

    public void setScore3(double score3) {
        this.score3 = score3;
    }

    public void setScore4(double score4) {
        this.score4 = score4;
    }

    public void setStudentScore(String studentScore) {
        this.studentScore = studentScore;
    }

    public void setExtendedFeedback(String extendedFeedback) {
        this.extendedFeedback = extendedFeedback;
    }

    public void setPersianDate(String persianDate) {
        this.persianDate = persianDate;
    }

    public void setUpVotes(String upVotes) {
        this.upVotes = upVotes;
    }

    public void setDownVotes(String downVotes) {
        this.downVotes = downVotes;
    }

    public void setFeedbackId(int feedbackId) {
        this.feedbackId = feedbackId;
    }
}