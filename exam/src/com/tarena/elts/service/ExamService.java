package com.tarena.elts.service;

import java.util.List;

import com.tarena.elts.entity.ExamInfo;
import com.tarena.elts.entity.QuestionInfo;
import com.tarena.elts.entity.User;

/** 软件的核心功能：登陆，开始(发卷)，交卷... */
public interface ExamService {
	// login方法没有修饰词，不是默认的，在接口里所有方法都是公有的
	User login(int id, String pwd) throws IdOrPwdException;

	ExamInfo start();

	QuestionInfo getQuestion(int index);

	void saveUserAnswers(int index, List<Integer> userAnswers);
	
	int examOver();

	String getMsg();

	//String getMsg();

	
}
