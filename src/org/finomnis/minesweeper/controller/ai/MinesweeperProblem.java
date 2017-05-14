package org.finomnis.minesweeper.controller.ai;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MinesweeperProblem {

	private Map<Integer, Coord> reverseIdMap = new HashMap<Integer, Coord>();
	private Map<Coord, Integer> idMap = new HashMap<Coord, Integer>();
	private List<Set<Integer>> values_left = null;
	private List<Integer> values_right = null;
	
	private MinesweeperProblem(){}
	
	private int addCoord(Coord coord){
		Integer existingId = idMap.get(coord);
		if(existingId != null){
			return existingId;
		}
		
		int id = idMap.size();
		reverseIdMap.put(id, coord);
		idMap.put(coord, id);
		return id;
	}
	
	private void addProblems(List<Set<Coord>> inputElems, List<Integer> inputSums){
		for(Set<Coord> coordSet : inputElems){
			for(Coord coord : coordSet){
				this.addCoord(coord);
			}
		}
		
		this.values_left = new ArrayList<Set<Integer>>();
		this.values_right = new ArrayList<Integer>();
		
        for(int i = 0; i < inputElems.size(); i++){            
            
        	Set<Integer> currentSet = new HashSet<Integer>();
            
        	for(Coord elem : inputElems.get(i)){
            	currentSet.add(idMap.get(elem));
            }
            
        	this.values_left.add(currentSet);
            this.values_right.add(inputSums.get(i));
        }		
	}
	
	public static MinesweeperProblem create(List<Coord> relevantPoints){
        
		ArrayList<Set<Coord>> inputElems = new ArrayList<Set<Coord>>();
	    ArrayList<Integer> inputSums = new ArrayList<Integer>();
		
		for(Coord importantField : relevantPoints){
            int existingFlagged = 0;
            Set<Coord> emptyNeighbors = new HashSet<Coord>();
            for(Coord neigh : importantField.getNeighbors()){
                switch(neigh.getFieldState()){
                case FLAGGED:
                    existingFlagged++;
                    break;
                case QUESTIONED:
                case UNTOUCHED:
                    emptyNeighbors.add(neigh);
                    break;
                default:
                    break;
                       
                };
            }
            
            inputElems.add(emptyNeighbors);
            inputSums.add(importantField.getFieldNumber() - existingFlagged);
        }
		
		MinesweeperProblem problem = new MinesweeperProblem();
		problem.addProblems(inputElems, inputSums);
		
		
		return problem;
	}

	public List<MinesweeperProblem> split(){
		
		if(getNumRows() < 1){
			List<MinesweeperProblem> ret = new ArrayList<MinesweeperProblem>();
			ret.add(this);
			return ret;
		}
		
		// Colorize
		Set<Integer> cols = new HashSet<Integer>();
		Set<Integer> rows = new HashSet<Integer>();
		colorize(cols, rows, 0);

		// Split
		MinesweeperProblem problem0 = new MinesweeperProblem();
		MinesweeperProblem problem1 = new MinesweeperProblem();
		for(int i = 0; i < idMap.size(); i++){
			if(cols.contains(i)){
				problem0.addCoord(reverseIdMap.get(i));
			} else {
				problem1.addCoord(reverseIdMap.get(i));
			}
		}
		
		List<Set<Coord>> val_left0 = new ArrayList<Set<Coord>>();
		List<Set<Coord>> val_left1 = new ArrayList<Set<Coord>>();
		List<Integer> val_right0 = new ArrayList<Integer>();
		List<Integer> val_right1 = new ArrayList<Integer>();
		for(int i = 0; i < values_left.size(); i++){
			Set<Coord> coordsSet = new HashSet<Coord>();
			for(int rowElems : values_left.get(i)){
				coordsSet.add(reverseIdMap.get(rowElems));
			}
			if(rows.contains(i)){
				val_left0.add(coordsSet);
				val_right0.add(values_right.get(i));
			} else {
				val_left1.add(coordsSet);
				val_right1.add(values_right.get(i));
			}
		}
		
		problem0.addProblems(val_left0, val_right0);
		
		List<MinesweeperProblem> ret = new ArrayList<MinesweeperProblem>();
		
		ret.add(problem0);

		if(!val_left1.isEmpty()){
			problem1.addProblems(val_left1, val_right1);
			ret.addAll(problem1.split());
		}
		
		return ret; 
	}
	
	private void colorize(Set<Integer> columns, Set<Integer>rows, int nextRow){
		if(!rows.add(nextRow))
			return;
		
		for(int val : values_left.get(nextRow)){
			if(columns.add(val)){
				for(int row = 0; row < values_left.size(); row++){
					if(values_left.get(row).contains(val)){
						colorize(columns,rows,row);
					}
				}
			};
		}
	}
	
	@Override
	public String toString(){
		String ret = "\nMinesweeperProblem: \n";
		for(int i = 0; i < values_left.size(); i++){
			ret += values_right.get(i) + ":";
			for(int j = 0; j < idMap.size(); j++){
				if(values_left.get(i).contains(j)){
					ret += " 1";
				} else {
					ret += " 0";
				}
			}
			ret += "\n";
		}
		return ret;
	}
	
	public int getNumCols(){
		return idMap.size();
	}
	
	public int getNumRows(){
		return values_left.size();
	}
	
	public Set<Integer> getRow(int i){
		return values_left.get(i);
	}
	
	public int getRowSum(int i){
		return values_right.get(i);
	}
	
	public Coord getCoord(int i){
		return reverseIdMap.get(i);
	}

	public Map<Coord, Boolean> mapSolutions(Map<Integer, Boolean> solutions) {
        Map<Coord, Boolean> inputSolutions = new HashMap<Coord,Boolean>();
		for(Map.Entry<Integer, Boolean> solution : solutions.entrySet()){
            inputSolutions.put(reverseIdMap.get(solution.getKey()), solution.getValue());
            System.out.println(reverseIdMap.get(solution.getKey()) + ": " + solution.getValue());
        }
		return inputSolutions;
	}

	public Set<Coord> getCoords() {
		return idMap.keySet();
	}

	public int getPosition(Coord coord) {
		return idMap.get(coord);
	}
	
}
