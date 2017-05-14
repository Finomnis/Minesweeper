package org.finomnis.minesweeper.controller.ai;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BinaryGauss implements MinesweeperSolver {

	private int[][] matrix;
    
    private Map<Integer, Boolean> solutions = new HashMap<Integer, Boolean>();
    private Map<Coord, Boolean> inputSolutions = new HashMap<Coord, Boolean>();
    
    public boolean solve(MinesweeperProblem problem){
                
        // create matrix
        matrix = new int[problem.getNumRows()][problem.getNumCols() + 1];
        
        // fill matrix
        for(int i = 0; i < problem.getNumRows(); i++){
            Set<Integer> inputRowElems = problem.getRow(i);
            int inputRowSum = problem.getRowSum(i);
            
            for(int elem : inputRowElems){
                matrix[i][elem] = 1;
            }
            
            matrix[i][matrix[i].length-1] = inputRowSum;
        }
        
        //printMatrix("Init");
        
        // GAUSS
        gauss(0,0);
        
        //printMatrix("After Gauss");

        // Find solutions
        while(findMoreSolutions());
        
        inputSolutions = problem.mapSolutions(solutions);
        
        return !inputSolutions.isEmpty();
        
    }
    
    private boolean findMoreSolutions(){
        
        Set<Integer> currentVals = new HashSet<Integer>();
        
        boolean changed = false;
        
        for(int[] row : matrix){
            currentVals.clear();
            
            int target_sum = row[row.length - 1];
            
            int row_sum = 0;
            boolean isInvalid = false;
            for(int i = 0; i < row.length - 1; i++){
                if(row[i] == 0) continue;
                if(row[i] != 1){
                    isInvalid = true;
                    break;
                }
                if(solutions.containsKey(i)){
                    target_sum -= row[i] * (solutions.get(i)?1:0);
                } else {
                    row_sum += row[i];
                    currentVals.add(i);
                }
            }
            if(isInvalid){
                continue;
            }
            
            if(currentVals.isEmpty()){
                continue;
            }
            
            if(target_sum == 0){
                for(int i : currentVals){
                    solutions.put(i, false);
                }
                changed = true;
            } else if (target_sum == row_sum){
                for(int i : currentVals){
                    solutions.put(i, true);
                }
                changed = true;
            }
            
        }
        return changed;
    }
    
    private void gauss(int row, int col){
        
        if(row >= matrix.length) return;
        if(col >= matrix[row].length) return;
        
        // Find target row
        int targetRow = -1;
        while(targetRow == -1){
            for(int y = row; y < matrix.length; y++){
                if(matrix[y][col] != 0){
                    targetRow = y;
                    break;
                }
            }
            if(targetRow == -1){
                col++;
                if(col >= matrix[row].length) return;
            }
        }
        
        // Swap row and targetRow
        if(row != targetRow){
            int[] tmp = matrix[row];
            matrix[row] = matrix[targetRow];
            matrix[targetRow] = tmp;
        }
        
        // Divide by number
        int targetRowMult = matrix[row][col];
        for(int x = col; x < matrix[row].length; x++){
            matrix[row][x] /= targetRowMult;
        }
        
        // Subtract all others
        for(int y = row+1; y < matrix.length; y++){
            int currentRowMult = matrix[y][col];
            for(int x = col; x < matrix[row].length; x++){
                matrix[y][x] -= currentRowMult * matrix[row][x];
            }
        }
        
        //printMatrix("After gauss(" + row + ", " + col + ")");
        
        gauss(row+1,col+1);
        
        // Reverse Gauss
        for(int y = 0; y < row; y++){
            int currentRowMult = matrix[y][col];
            for(int x = col; x < matrix[row].length; x++){
                matrix[y][x] -= currentRowMult * matrix[row][x];
            }
        }
        
        //printMatrix("After reverse gauss(" + row + ", " + col + ")");
        
    }
    
    public void printMatrix(String msg){
        System.out.println(msg + ":");
        for(int[] row : matrix){
            
            for(int el : row){
                System.out.print(" " + el);
            }
            System.out.println();
        }
        System.out.println();
    }
    
    public Map<Coord, Boolean> getResults(){
        return inputSolutions;
    }
    
    public boolean hasResults(){
        return !inputSolutions.isEmpty();
    }
    
    public float getChance(Coord i){
        return 0.5f;
    }
    
}
