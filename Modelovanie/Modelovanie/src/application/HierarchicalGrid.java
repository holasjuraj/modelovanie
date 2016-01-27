package application;

import java.util.ArrayList;
import java.util.List;

public class HierarchicalGrid {
	private static final int MIN_RES_EXP = 2;
	private static final double resA = 0.5, resB = 1;
	private final int k;
	
	public boolean narrowPhase = false;
	
	private List<Box> boxes;
	private List<SpatialHashTable<GridCell>> grids;
	private double[] s, as, bs;
	private Matrix pairs;
	
	
	private static class GridCell{
		List<Integer> boldList = new ArrayList<Integer>();
		List<Integer> normList = new ArrayList<Integer>();
	}
	
	
	public HierarchicalGrid(List<Box> boxes, int k){
		this.boxes = boxes;
		this.k = k;
		grids = new ArrayList<SpatialHashTable<GridCell>>(k);
		s = new double[k];
		as = new double[k];
		bs = new double[k];
		for(int i = 0; i < k; i++){
			int cellsNum = 1<<(MIN_RES_EXP+i);
			grids.add(new SpatialHashTable<GridCell>(cellsNum));
			s[i] = Box.maxCoord / (double)cellsNum;
			as[i] = resA*s[i];
			bs[i] = resB*s[i];
			System.out.println("s"+i+"="+s[i]);
		}
		pairs = new Matrix(0);
	}

	public Matrix getCollisions(boolean narrowPhase){
		if(!narrowPhase){
			return pairs;
		}
		
		Matrix res = new Matrix(pairs.n);
		for (int i=0; i<boxes.size(); i++){
			Box B1 = boxes.get(i);
			for (int j=i+1; j<boxes.size(); j++){
				if(!pairs.get(i, j)){ continue; }
				Box B2 = boxes.get(j);
				if (!(B1.x>(B2.x+B2.width)) && !(B1.x+B1.width<B2.x) 
						&& !(B1.y>B2.y+B2.height) && !(B1.y+B1.height<B2.y)){
					res.set(i, j, true);
				}
			}
		}
		return res;
	}
	
	public void addBox(Box b){
		pairs.addRowCol();
		int r = resBox(b);
		for(int i = 0; i <= r; i++){
			insertToGrid(pairs.n-1, i, i==r);
		}
	}
	
	public void removeBox(int index){
		Box b = boxes.get(index);
		int r = resBox(b);
		for(int i = 0; i <= r; i++){
			removeFromGrid(b, index, i, i==r);
		}
		pairs.removeRowCol(index);
	}

	public void updateBox(int index, double[] oldPos){
		Box b = boxes.get(index);
		int r = resBox(b);
		for(int i = 0; i <= r; i++){
			int[] oldRange = cellsRange(b, oldPos[0], oldPos[1], i);
			int[] newRange = cellsRange(b, i);
			boolean changed = false;
			for(int j = 0; j < 4; j++){
				changed |= (oldRange[j]!=newRange[j]);
			}
			
			if(changed){
				removeFromGrid(oldRange, index, i, i==r);
				insertToGrid(index, i, i==r);
			}
		}
	}

	
	private void insertToGrid(int boxID, int gIdx, boolean bold){
		Box b = boxes.get(boxID);
		int[] range = cellsRange(b, gIdx);
		int startX = range[0],
			startY = range[1],
			endX = range[2],
			endY = range[3];
		
		for(int x = startX; x <= endX; x++){
			for(int y = startY; y <= endY; y++){
				GridCell gc = grids.get(gIdx).get(x, y);
				if(gc == null){
					gc = new GridCell();
					grids.get(gIdx).set(x, y, gc);
				}
				
				for(Integer otherID:gc.boldList){
					pairs.set(otherID, boxID, true);
				}
				if(bold){
					for(Integer otherID:gc.normList){
						pairs.set(otherID, boxID, true);
					}
					gc.boldList.add(boxID);
				}
				else{
					gc.normList.add(boxID);
				}
				
			}
		}
	}

	private void removeFromGrid(Box b, int bID, int gIdx, boolean bold){
		int[] range = cellsRange(b, gIdx);
		removeFromGrid(range, bID, gIdx, bold);
	}
	private void removeFromGrid(int[] range, int bID, int gIdx, boolean bold){
		int startX = range[0],
			startY = range[1],
			endX = range[2],
			endY = range[3];
		
		for(int x = startX; x <= endX; x++){
			for(int y = startY; y <= endY; y++){
				GridCell gc = grids.get(gIdx).get(x, y);
				if(gc == null){
					System.err.println("HierarchicalGrid.removeFromGrid: grid cell does not exist");
				}

				for(Integer otherID:gc.boldList){
					if(otherID == bID){ continue; }
					pairs.set(otherID, bID, false);
				}
				if(bold){
					for(Integer otherID:gc.normList){
						if(otherID == bID){ continue; }
						pairs.set(otherID, bID, false);
					}
					gc.boldList.remove(new Integer(bID));
				}
				else{
					gc.normList.remove(new Integer(bID));
				}
				
			}
		}
		
	}
	
	private int[] cellsRange(Box b, int gIdx){
		return cellsRange(b, b.x, b.y, gIdx);
	}
	private int[] cellsRange(Box b, double oldX, double oldY, int gIdx){
		return new int[]{
				(int) Math.round(Math.floor( oldX / s[gIdx] )),
				(int) Math.round(Math.floor( oldY / s[gIdx] )),
				(int) Math.round(Math.floor( (oldX+b.width-0.01) / s[gIdx] )),
				(int) Math.round(Math.floor( (oldY+b.height-0.01) / s[gIdx] ))
		};
	}

	private int resBox(Box b){
		double size = Math.max(b.height, b.width);
		if(size >= bs[0]){
			return 0;
		}
		
		for(int i = 0; i < k; i++){
			if(as[i] <= size && size < bs[i]){
				return i;
			}
		}

		return k-1;
	}
	
}