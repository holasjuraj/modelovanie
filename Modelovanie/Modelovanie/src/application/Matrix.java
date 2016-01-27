package application;

import java.util.ArrayList;

public class Matrix {
	ArrayList<ArrayList<Boolean>> matica;
	int n;
	
	public Matrix(int n){
		this.n = n;
		clear();
	}
	
	public Boolean get(int r, int c){
		if (r>=0 && c>=0 && r<n && c<n) {
			return matica.get(r).get(c);
		} 
		return null;
	}
	
	public boolean set(int r, int c, boolean value){
		// Symmetric assignment: matrix[r][c] = matrix[c][r] = value
		if (r>=0 && c>=0 && r<n && c<n) {
			matica.get(r).set(c, value);
			matica.get(c).set(r, value);
		} 
		return false;
	}
	
	public boolean removeRowCol(int index){
		if (index>=0 && index<n){
			n--;
			// row
			matica.remove(index);
			// column
			for (int r=0; r<n; r++){
				matica.get(r).remove(index);
			}
			return true;
		}
		return false;
	}
	
	public void addRowCol(){
		n++;
		// row
		ArrayList<Boolean> riadok = new ArrayList<Boolean>(n);
		for(int c = 0; c < n; c++){
			riadok.add(false);
		}
		matica.add(riadok);
		// column
		for(int r = 0; r < n-1; r++){
			matica.get(r).add(false);
		}
	}
	
	public void clear(){
		matica = new ArrayList<ArrayList<Boolean>>(n);
		for(int i = 0; i < n; i++){
			ArrayList<Boolean> riadok = new ArrayList<Boolean>(n);
			for(int j = 0; j < n; j++){
				riadok.add(false);
			}
			matica.add(riadok);
		}
	}
	
}