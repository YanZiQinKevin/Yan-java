package com.tarena.elts.ui;

import javax.swing.JOptionPane;

import com.tarena.elts.entity.EntityContext;
import com.tarena.elts.entity.User;
import com.tarena.elts.service.ExamService;
import com.tarena.elts.service.ExamServiceImpl;
import com.tarena.elts.service.IdOrPwdException;
import com.tarena.elts.util.Config;

public class Main {
	public static void main(String[] args) {
		try{
			//�������ж���ע�룬���������ϵ
			LoginFrame loginFrame = new LoginFrame();
			MenuFrame menuFrame = new MenuFrame();
			ExamFrame examFrame = new ExamFrame();
			MsgFrame msgFrame = new MsgFrame();
			ClientContext clientContext = new ClientContext();
			WelcomeWindow welcomeWindow = new WelcomeWindow();
//			ExamService examService = new ExamService(){
//				public User login(int id, String pwd) throws IdOrPwdException{
//					if(id==1 && pwd.equals("1234")){
//						return new User("jintao", 1, "1234");
//					}
//					throw new IdOrPwdException("��½����");
//				}
//			};
			ExamServiceImpl examService = new ExamServiceImpl();
			Config config = new Config("src/com/tarena/elts/util/client.properties");
			EntityContext entityContext = new EntityContext(config);
			examService.setEntityContext(entityContext);
			clientContext.setExamService(examService);
			clientContext.setLoginFrame(loginFrame);
			clientContext.setMenuFrame(menuFrame);
			clientContext.setMsgFrame(msgFrame);
			clientContext.setExamFrame(examFrame);
			clientContext.setWelcomeWindow(welcomeWindow);
			loginFrame.setClientContext(clientContext);
			menuFrame.setClientContext(clientContext);
			examFrame.setClientContext(clientContext);
			msgFrame.setClientContext(clientContext);
			//ʹ��show()��ʾ��½����
			clientContext.show();
		}catch (Exception e){
			JOptionPane.showMessageDialog(null,e.getMessage());
		}
	}
}
