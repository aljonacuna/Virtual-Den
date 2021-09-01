package com.example.loadingscreen.win.model;

public class Students {
    String studentFullName, studentEmail, studentBirthday, studentPic,
            course, studentNumber, section, group, gender, content, isVerified, studId;

    public Students() {
    }

    public Students(String studentFullName, String studentEmail, String studentBirthday, String studentPic, String course, String studentNumber,
                    String section, String group, String gender, String content, String isVerified, String studId) {
        this.studentFullName = studentFullName;
        this.studentEmail = studentEmail;
        this.studentBirthday = studentBirthday;
        this.studentPic = studentPic;
        this.course = course;
        this.studentNumber = studentNumber;
        this.section = section;
        this.group = group;
        this.gender = gender;
        this.content = content;
        this.isVerified = isVerified;
        this.studId = studId;
    }

    public String getStudId() {
        return studId;
    }

    public void setStudId(String studId) {
        this.studId = studId;
    }

    public String getIsVerified() {
        return isVerified;
    }

    public void setIsVerified(String isVerified) {
        this.isVerified = isVerified;
    }

    public String getStudentFullName() {
        return studentFullName;
    }

    public void setStudentFullName(String studentFullName) {
        this.studentFullName = studentFullName;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    }

    public String getStudentBirthday() {
        return studentBirthday;
    }

    public void setStudentBirthday(String studentBirthday) {
        this.studentBirthday = studentBirthday;
    }

    public String getStudentPic() {
        return studentPic;
    }

    public void setStudentPic(String studentPic) {
        this.studentPic = studentPic;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}