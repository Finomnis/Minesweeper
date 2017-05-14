package org.finomnis.minesweeper.controller.ai;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class BruteforceSolver implements MinesweeperSolver{

	private List<List<Boolean>> solutions = new ArrayList<List<Boolean>>();

	private LinkedList<Boolean> currentSolution = new LinkedList<Boolean>();
	
	private int[] intermediateValuesTrue;
	private int[] intermediateValuesFalse;
		
	private MinesweeperProblem problem;
	
	private Map<Integer, Boolean> solutionsMap = new HashMap<Integer, Boolean>();
    private Map<Coord, Boolean> inputSolutionsMap = new HashMap<Coord, Boolean>();
    
	private void recurse(){
		
		int position = currentSolution.size();
		
		if(position == problem.getNumCols()){
			
			solutions.add(new ArrayList<Boolean>(currentSolution));
			return;
		}		
		
		for(int i = 0; i < 2; i++){
			boolean currentValue = i==0;
			currentSolution.addLast(currentValue);
		
			// Add to intermediateValues
			for(int j = 0; j < problem.getNumRows(); j++){
				if(problem.getRow(j).contains(position)){
					if(currentValue == true){
						intermediateValuesTrue[j]++;
					} else {
						intermediateValuesFalse[j]++;
					}
				}
			}
			
			// Check for problems
			boolean problems = false;
			
			for(int j = 0; j < problem.getNumRows(); j++){
				if(intermediateValuesTrue[j] > problem.getRowSum(j)){
					problems = true;
					break;
				}
				if(intermediateValuesFalse[j] + problem.getRowSum(j) > problem.getRow(j).size()){
					problems = true;
					break;
				}
			}
			
			int numBombsInCurrentSolution = 0;
			for(boolean isBomb : currentSolution){
				if(isBomb){
					numBombsInCurrentSolution++;
				}
			}
			if(numBombsInCurrentSolution > problem.getNumBombs()){
				problems = true;
			}
			else if(currentSolution.size() == problem.getNumCols()
					&& numBombsInCurrentSolution + problem.getNumFields() < problem.getNumBombs()){
				problems = true;
			}
			
			// recurse
			if(!problems)
				recurse();
			
			// Remove from intermediateValues
			for(int j = 0; j < problem.getNumRows(); j++){
				if(problem.getRow(j).contains(position)){
					if(currentValue == true){
						intermediateValuesTrue[j]--;
					} else {
						intermediateValuesFalse[j]--;
					}
				}
			}
					
			currentSolution.removeLast();
		}
		
	}
	
	@Override
	public boolean solve(MinesweeperProblem problem){
		
		this.problem = problem;
		
		intermediateValuesTrue = new int[problem.getNumRows()];
		intermediateValuesFalse = new int[problem.getNumRows()];
		
		recurse();
		
		//for(List<Boolean> solution : solutions){
		//	System.out.println(solution);
		//}
		
		// extract results
		if(solutions.isEmpty())
			return false;
		
		for(int i = 0; i < solutions.size(); i++){
			List<Boolean> solution = solutions.get(i);
			if(i == 0){
				for(int j = 0; j < solution.size(); j++){
					solutionsMap.put(j, solution.get(j));
				}
			} else {
				List<Integer> keys = new ArrayList<Integer>(solutionsMap.keySet());
				for(int j : keys){
					if(solutionsMap.get(j) != solution.get(j)){
						solutionsMap.remove(j);
					}
				}
			}
		}
		
		inputSolutionsMap = problem.mapSolutions(solutionsMap);
		
		return !inputSolutionsMap.isEmpty();
	}

	@Override
	public Map<Coord, Boolean> getResults() {
		return inputSolutionsMap;
	}

	@Override
	public float getChance(Coord coord) {
		if(solutions.isEmpty())
			return 0.5f;
		
		int pos = problem.getPosition(coord);
		int yes = 0;
		for(List<Boolean> solution : solutions){
			if(solution.get(pos) == true) yes++;
		}
		return yes/(float)solutions.size();
	}


}
