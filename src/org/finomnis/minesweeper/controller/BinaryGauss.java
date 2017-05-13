package org.finomnis.minesweeper.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BinaryGauss<T> {

    private ArrayList<Set<T>> inputElems = new ArrayList<Set<T>>();
    private ArrayList<Integer> inputSums = new ArrayList<Integer>();
    
    public void addDependency(Integer sum, Integer constant, @SuppressWarnings("unchecked") T... elements){
        Set<T> elemsSet = new HashSet<T>();
        for(T elem : elements){
            elemsSet.add(elem);
        }
        addDependency(sum,constant,elemsSet);
    }
    
    public void addDependency(Integer sum, Integer constant, Set<T> elements){
        System.out.println("Adding " + sum + ", " + constant + ", " +elements.size());
        Set<T> elemsCpy = new HashSet<T>(elements);
        inputElems.add(elemsCpy);
        inputSums.add(sum-constant);
    }
    
    private int[][] matrix;
    
    private Map<T, Integer> elementIds = new HashMap<T, Integer>();
    private Map<Integer, T> reverseElementIds = new HashMap<Integer, T>();
    
    private Map<Integer, Boolean> solutions = new HashMap<Integer, Boolean>();
    private Map<T, Boolean> inputSolutions = new HashMap<T, Boolean>();
    
    public void solve(){
                
        // create input elements mapping
        for(Set<T> inputRowElems : inputElems){
            for(T elem : inputRowElems){
                if(elementIds.containsKey(elem))
                    continue;
                reverseElementIds.put(elementIds.size(), elem);
                elementIds.put(elem, elementIds.size());
            }
        }
        
        // create matrix
        matrix = new int[inputElems.size()][elementIds.size() + 1];
        
        // fill matrix
        for(int i = 0; i < inputElems.size(); i++){
            Set<T> inputRowElems = inputElems.get(i);
            int inputRowSum = inputSums.get(i);
            
            for(T elem : inputRowElems){
                int pos = elementIds.get(elem);
                matrix[i][pos] = 1;
            }
            
            matrix[i][matrix[i].length-1] = inputRowSum;
        }
        
        printMatrix("Init");
        
        // GAUSS
        gauss(0,0);
        
        printMatrix("After Gauss");

        // Find solutions
        while(findMoreSolutions());
        
        for(Map.Entry<Integer, Boolean> solution : solutions.entrySet()){
            inputSolutions.put(reverseElementIds.get(solution.getKey()), solution.getValue());
            System.out.println(solution.getKey() + ": " + solution.getValue());
        }
        
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
        
        printMatrix("After gauss(" + row + ", " + col + ")");
        
        gauss(row+1,col+1);
        
        // Reverse Gauss
        for(int y = 0; y < row; y++){
            int currentRowMult = matrix[y][col];
            for(int x = col; x < matrix[row].length; x++){
                matrix[y][x] -= currentRowMult * matrix[row][x];
            }
        }
        
        printMatrix("After reverse gauss(" + row + ", " + col + ")");
        
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
    
    public boolean mustBeTrue(Integer i){
        Boolean res = solutions.get(i);
        if(res == null)
            return false;
        return res;
    }
    
    public boolean mustBeFalse(Integer i){
        Boolean res = solutions.get(i);
        if(res == null)
            return false;
        return !((boolean)res);
    }
    
    public Map<T, Boolean> getResults(){
        return inputSolutions;
    }
    
    public boolean hasResults(){
        return !inputSolutions.isEmpty();
    }
    
    public float chance(Integer i){
        return 0;
    }
    
}
