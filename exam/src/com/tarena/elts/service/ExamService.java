package com.tarena.elts.service;

import java.util.List;

import com.tarena.elts.entity.ExamInfo;
import com.tarena.elts.entity.QuestionInfo;
import com.tarena.elts.entity.User;

/** ����ĺ��Ĺ��ܣ���½����ʼ(����)������... */
public interface ExamService {
	// login����û�����δʣ�����Ĭ�ϵģ��ڽӿ������з������ǹ��е�
	User login(int id, String pwd) throws IdOrPwdException;

	ExamInfo start();

	QuestionInfo getQuestion(int index);

	void saveUserAnswers(int index, List<Integer> userAnswers);
	
	int examOver();

	String getMsg();

	//String getMsg();

	
}
