package utils;

import java.util.Vector;
/**
 *
 * @author Cristian
 */
public class Main {


	public static void main(String[] args) {
		int[][] M = {{0,1},{10,11},{20,21}};
		boolean test=true;
		for(int i=0;i<M.length;i++){
			for(int j=0;j<M[i].length;j++){
				System.out.print(M[i][j]);
				System.out.print(" ");
				if(M[i][j]!=10*i+j){
					test=false;
				}
			}
			System.out.println();
		}
		System.out.println(test);
	}
}