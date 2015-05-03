package com.tarena.elts.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.tarena.elts.entity.EntityContext;
import com.tarena.elts.entity.ExamInfo;
import com.tarena.elts.entity.Question;
import com.tarena.elts.entity.QuestionInfo;
import com.tarena.elts.entity.User;

/** ����ҵ���ܵ�ʵ���� */
public class ExamServiceImpl implements ExamService, Serializable {
	private static final long serialVersionUID = 1L;

	private EntityContext entityContext;

	public void setEntityContext(EntityContext entityContext) {
		this.entityContext = entityContext;
	}

	/** ���� */
	private List<QuestionInfo> paper = new ArrayList<QuestionInfo>();

	private User loginUser;

	private int score;

	private boolean finished = false;

	private boolean started = false;

	public User login(int id, String pwd) throws IdOrPwdException {
		User user = entityContext.findUserById(id);
		if (user == null) {
			throw new IdOrPwdException("���û�!");
		}
		if (user.getPasswd().equals(pwd)) {
			loginUser = user;
			return user;// ��¼�ɹ�
		}
		throw new IdOrPwdException("�������!");
	}

	public QuestionInfo getQuestion(int index) {
		return paper.get(index);
	}

	public ExamInfo start() {
		if (finished)
			throw new RuntimeException("�����Ѿ�������!");
		if (started)
			throw new RuntimeException("�����Ѿ���ʼ��!");
		// ��������
		createPaper();
		// ��֯������Ϣ
		ExamInfo info = new ExamInfo();
		info.setQuestionCount(paper.size());
		info.setTimeLimit(entityContext.getTimeLimit());
		info.setTitle(entityContext.getTitle());
		info.setUser(loginUser);// ��ǰϵͳ��½�û�
		started = true;
		return info;
	}

	/**
	 * �������� ����: ÿ���Ѷȼ���������
	 */
	private void createPaper() {
		Random r = new Random();
		int index = 0;
		for (int level = Question.LEVEL1; level <= Question.LEVEL10; level++) {
			List<Question> list = entityContext.findQuestions(level);
			// ��list��ȡ��(remove)һ����
			Question q1 = list.remove(r.nextInt(list.size()));
			Question q2 = list.remove(r.nextInt(list.size()));
			paper.add(new QuestionInfo(index++, q1));
			paper.add(new QuestionInfo(index++, q2));
		}
	}

	public void saveUserAnswers(int index, List<Integer> userAnswers) {
		QuestionInfo q = paper.get(index);
		q.getUserAnswers().clear();
		q.getUserAnswers().addAll(userAnswers);
	}

	public int examOver() {
		if (finished)
			throw new RuntimeException("�����Ѿ�����!");
		for (QuestionInfo q : paper) {
			Question question = q.getQuestion();
			List<Integer> userAnswers = q.getUserAnswers();
			if (userAnswers.equals(question.getAnswers())) {
				score += question.getScore();
			}
		}
		finished = true;
		return score;
	}

	public String getMsg() {
		return entityContext.getMsg();
	}

//	public String getMsg() {
//		return entityContext.getMsg();
//	}
}
