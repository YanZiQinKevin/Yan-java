package gobang;

import java.io.IOException;

public class Demo1 {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		try {
			System.out.println(1);
			gcTest();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("has exited gcTest!");
		System.in.read();
		System.in.read();
		System.out.println("out begin gc!");
		for (int i = 0; i < 10; i++) {
			System.gc();
			System.in.read();
			System.in.read();
		}
	}

	private static void gcTest() throws IOException {
		// TODO Auto-generated method stub
        System.in.read();
        System.out.println(3);
        System.in.read();
        System.out.println(4);
        Person p1=new Person();
       
          System.in.read();
          System.in.read();
          Person p2=new Person();
            p1.setMate(p2);
            p2.setMate(p1);
            System.out.println("before exit gctext!");
            System.in.read();
            System.in.read();
            System.gc();
            System.out.println("exit gctest!");
	}
   private static class Person{
	   byte[] data=new byte[20000000];
	   Person mate=null;
	   
	   public void setMate(Person other){
		   mate=other;
		   System.out.println(2);
	   }
   }
}
