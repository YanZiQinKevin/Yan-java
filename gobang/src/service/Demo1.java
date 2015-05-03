package service;
import  java.util.Arrays;
public class Demo1 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
   int i,j;
   
		int[][] a=new int[5][5];
		for(i=0;i<5;i++){
			for(j=0;j<5;j++){
				a[i][j]=3;
			}
		}
		for(i=0;i<5;i++){
		
				Arrays.fill(a[i], 0);
			
		}
		//Arrays.fill(a[1], 0);
		System.out.println(a[1][1]);
	}

}
