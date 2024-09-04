package com.rbe.adminPortal.dto;

public class QuestionObject {
    Element question;
    Element opt1;
    Element opt2;
    Element opt3;
    Element opt4;
    Element sol;
    String ans;

    public Element getQuestion() {
        return question;
    }

    public void setQuestion(Element question) {
        this.question = question;
    }

    public Element getOpt1() {
        return opt1;
    }

    public void setOpt1(Element opt1) {
        this.opt1 = opt1;
    }

    public Element getOpt2() {
        return opt2;
    }

    public void setOpt2(Element opt2) {
        this.opt2 = opt2;
    }

    public Element getOpt3() {
        return opt3;
    }

    public void setOpt3(Element opt3) {
        this.opt3 = opt3;
    }

    public Element getOpt4() {
        return opt4;
    }

    public void setOpt4(Element opt4) {
        this.opt4 = opt4;
    }

    public Element getSol() {
        return sol;
    }

    public void setSol(Element sol) {
        this.sol = sol;
    }

    public String getAns() {
        return ans;
    }

    public void setAns(String ans) {
        this.ans = ans;
    }
}

