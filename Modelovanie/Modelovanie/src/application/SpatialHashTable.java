package application;

import java.util.HashMap;

public class SpatialHashTable<T> {
	
	private HashMap<Integer, T> table = new HashMap<Integer, T>();
	private static final int PRIME = 131071;
	final int res;
	
	public SpatialHashTable(int max) {
		this.res = max;
	}
	public SpatialHashTable() {
		this(Integer.MAX_VALUE);
	}
	
	public void set(int x, int y, T value){
		if(x>=0 && y>=0 && x<res && y<res){
			table.put(computeHash(x, y), value);
		}
		else{
			System.err.println("SpatialHashTable.set: index ["+x+","+y+"] out of bounds");
		}
	}
	
	public T get(int x, int y){
		if(x>=0 && y>=0 && x<res && y<res){
			return table.get(computeHash(x, y));
		}
		System.err.println("SpatialHashTable.get: index ["+x+","+y+"] out of bounds");
		return null;
	}
	
	private static int computeHash(Integer x, Integer y){
		return x.hashCode() + PRIME*y.hashCode();
	}

}