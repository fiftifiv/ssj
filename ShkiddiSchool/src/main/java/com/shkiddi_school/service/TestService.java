package com.shkiddi_school.service;

import com.shkiddi_school.domain.Answer;
import com.shkiddi_school.domain.Question;
import com.shkiddi_school.domain.Test;
import com.shkiddi_school.repos.AnswerRepo;
import com.shkiddi_school.repos.QuestionRepo;
import com.shkiddi_school.repos.TestRepo;
import org.aspectj.weaver.patterns.TypePatternQuestions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TestService {

    @Autowired
    TestRepo testRepo;

    @Autowired
    QuestionRepo questionRepo;

    @Autowired
    AnswerRepo answerRepo;

    public void addTestToDB(Test test) {
        testRepo.save(test);
    }

    public Question addNewQuestionToTest(Test test, String question) {
        Question question1 = new Question();
        question1.setQuestion(question);


        test.add(question1);
        questionRepo.save(question1);

        return question1;
    }

    public void deleteQuestion(Question question) {
        questionRepo.delete(question);
    }

    public Test save(Test test) {
        testRepo.save(test);
        return test;
    }

    public Answer addNewAnswerToQuestion(String textAnswer, Question question) {

        Answer answer = new Answer();
        answer.setText(textAnswer);

        question.getFalseAnswers().add(answer);
        answerRepo.save(answer);
        return answer;
    }

    public Question updateTrueAnswer(String trueAnswer, Question question) {

        question.setTrueAnswer(trueAnswer);
        questionRepo.save(question);
        return question;
    }

    public Question updateQuestion(String textQuestion, Question question) {
        question.setQuestion(textQuestion);
        questionRepo.save(question);
        return question;
    }

    public void deleteAnswer(Answer answer) {
        answerRepo.delete(answer);
    }

    public void updateAnswer(Answer answer, String textAnswer) {
        answer.setText(textAnswer);
        answerRepo.save(answer);
    }

    public void updateAnswer(int id, String textAnswer) {
        answerRepo.findById(id)
                .ifPresent(answer -> {
                    answer.setText(textAnswer);
                    answerRepo.save(answer);
                });
    }

    public int  getTestCompletionRate(int countTrueAnswer , Test test ){
        return (countTrueAnswer * 100) / test.getQuestions().size();
    }

    public List<Test> findAll(){
        List<Test> tests = new ArrayList<>();
        testRepo.findAll().forEach(test -> tests.add(test));
        return tests;
    }
}
