package org.finomnis.minesweeper.controller;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.finomnis.minesweeper.controller.ai.BinaryGauss;
import org.finomnis.minesweeper.controller.ai.BruteforceSolver;
import org.finomnis.minesweeper.controller.ai.Coord;
import org.finomnis.minesweeper.controller.ai.MinesweeperProblem;
import org.finomnis.minesweeper.controller.ai.MinesweeperSolver;
import org.finomnis.minesweeper.game.MinesweeperState.FieldState;

public class AIController extends GameController {

	private float winChance = 1.0f;
    
    private boolean isTouchable(Coord coord){
        FieldState state = coord.getFieldState();
        switch(state){
            case UNTOUCHED:
            case QUESTIONED:
                return true;
            default:
                return false;
        }
    }
    
    private boolean firstMove = true;
    
    private LinkedList<Coord> relevantFields = new LinkedList<Coord>();
    
    private void sleep(){
    	try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }
    
    private void longSleep(){
    	try {
			for(int i = 0; i < 10; i++){
				Thread.sleep(150);
				game.redraw();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }
    
    private void addToRelevantFields(Coord coord){
        while(relevantFields.remove(coord));
        if(mustHaveBomb(coord))
            return;
        if(coord.getFieldState() == FieldState.FLAGGED)
        	return;
        relevantFields.addFirst(coord);
        if(coord.getFieldState() == FieldState.QUESTIONED)
        	return;
        game.question(coord.x, coord.y);
        //sleep();
    }
    
    private Coord takeFromRelevantFields(){
        if(relevantFields.isEmpty())
            return null;
        Coord result = relevantFields.removeFirst();
        removeFromRelevantFields(result, true);
        return result;
    }
    
    private Set<Coord> unquestionLater = new HashSet<Coord>();
    
    private void removeFromRelevantFields(Coord coord, boolean perm){
        while(relevantFields.remove(coord));
        if(coord.getFieldState() == FieldState.FLAGGED)
        	return;
        if(perm){
        	unquestionLater.add(coord);
        	//game.unflag(coord.x, coord.y);
        }
        //sleep();
    }
    
    private void updateRelevantFields(Coord coord, Set<Coord> alreadyLookedAt){
        
        FieldState state = coord.getFieldState();
        
        if(state == FieldState.UNTOUCHED || state == FieldState.QUESTIONED){
            for(Coord neigh : coord.getNeighbors()){
                if(neigh.getFieldState() == FieldState.REVEALED){
                    addToRelevantFields(coord);
                    return;
                }
            }
            return;
        }
        
        if(state == FieldState.FLAGGED){
            for(Coord neigh : coord.getNeighbors()){
                if(isTouchable(neigh)){
                    updateRelevantFields(neigh);
                }
            }
            return;
        }
        
        if(state != FieldState.REVEALED){
            return;
        }
        
        if(coord.getFieldNumber() > 0){
            for(Coord neigh : coord.getNeighbors()){
                if(isTouchable(neigh)){
                    addToRelevantFields(neigh);
                }
            }
            return;
        }
        
        // we have a revealed but empty field. now do a neighbor search
        alreadyLookedAt.add(coord);
        
        for(Coord neigh : coord.getNeighbors()){
            if(alreadyLookedAt.contains(neigh)) continue;
            updateRelevantFields(neigh, alreadyLookedAt);
        }
                
    }
    
    private void updateRelevantFields(Coord coord){
        updateRelevantFields(coord, new HashSet<Coord>());
        
        // Remove all deleted questionmarks
        for(Coord relevantField : relevantFields){
        	unquestionLater.remove(relevantField);
        }
        
        for(Coord coordsToUnquestion : unquestionLater){
        	if(coordsToUnquestion.getFieldState() == FieldState.QUESTIONED)
        		game.unflag(coordsToUnquestion.x, coordsToUnquestion.y);
        }
    }

    @Override
    public boolean init() {
        // TODO Auto-generated method stub
        return false;
    }

    private void touch(Coord coord){
        //removeFromRelevantFields(coord);
    	game.mouseDown(coord.x, coord.y);
    	sleep();
    	game.touch(coord.x, coord.y);
    	game.mouseUp();
    	if(game.gameFinished()){
        	if(game.gameWon()){
        		System.out.println("Winchance was: " + (Math.round(winChance*10000))/100.0f + " %");
        	} else {
        		System.out.println("Lost after taking a total risk of " + (Math.round((1-winChance)*10000))/100.0f + " %.");
        	}
            return;
        }
        
        updateRelevantFields(coord);
    }
    
    private void flag(Coord coord){
        removeFromRelevantFields(coord, false);
        game.flag(coord.x, coord.y);
        
        updateRelevantFields(coord);
        sleep();
    }
    
    @Override
    public boolean step() {

        if(game.gameFinished())
            return false;
        
        if(firstMove){
            firstMove = false;
        	Random rnd = new Random();
        	if(rnd.nextBoolean()){
        		int x = rnd.nextBoolean()?0:game.getSizeX()-1;
        		int y = rnd.nextInt(game.getSizeY());
        		touch(new Coord(x,y,game));
        	} else {
        		int x = rnd.nextInt(game.getSizeX());
        		int y = rnd.nextBoolean()?0:game.getSizeY()-1;
        		touch(new Coord(x,y,game));
        	}
            return false;
        }
        
        // DIRECT LOGIC
        {
	        Coord coord = takeFromRelevantFields();
	        while(coord != null){
	            if(isTouchable(coord)){
	                if(!couldHaveBomb(coord)){
	                    touch(coord);
	                    return false;
	                }
	            }
	            coord = takeFromRelevantFields();
	        }
    	}
        
        System.out.println("Direct logic failed! Trying Gauss ...");

        List<Coord> touchableFields = getAllTouchableFields();
        MinesweeperProblem globalProblem = MinesweeperProblem.create(getImportantOpenFields(), touchableFields.size(), game.getNumMinesLeft());
        List<MinesweeperProblem> problems = globalProblem.split();
        
        //for(MinesweeperProblem subProblem : problems){
        //	System.out.println(subProblem);     
        //}

        // GAUSS
        for(MinesweeperProblem subProblem : problems){
            MinesweeperSolver gaussSolver = new BinaryGauss();
            if(gaussSolver.solve(subProblem)){
            	for(Map.Entry<Coord, Boolean> gaussSolution : gaussSolver.getResults().entrySet()){
            		if(gaussSolution.getValue() == true){
            			flag(gaussSolution.getKey());
            		} else {
            			touch(gaussSolution.getKey());
            		}
            	}
                return false;
            }
        }
        
        // Bruteforce
        Map<Coord, Float> probabilities = new HashMap<Coord, Float>();
        
        System.out.println("Gauss failed! Trying brute force ...");
        for(MinesweeperProblem subProblem : problems){
            MinesweeperSolver bruteforceSolver = new BruteforceSolver();
            if(bruteforceSolver.solve(subProblem)){
            	for(Map.Entry<Coord, Boolean> bruteforceSolution : bruteforceSolver.getResults().entrySet()){
            		if(bruteforceSolution.getValue() == true){
            			flag(bruteforceSolution.getKey());
            		} else {
            			touch(bruteforceSolution.getKey());
            		}
            	}
                return false;
            }
            for(Coord currentCoord : subProblem.getCoords()){
            	probabilities.put(currentCoord, bruteforceSolver.getChance(currentCoord));
            }
        }
        
        
        // Global bruteforce
        System.out.println("Bruteforce failed! Trying global bruteforce ...");
        {
    		BruteforceSolver bruteforceSolver = new BruteforceSolver();
    		if(bruteforceSolver.solve(globalProblem)){
    			
            	for(Map.Entry<Coord, Boolean> bruteforceSolution : bruteforceSolver.getResults().entrySet()){
            		if(bruteforceSolution.getValue() == true){
            			flag(bruteforceSolution.getKey());
            		} else {
            			touch(bruteforceSolution.getKey());
            		}
            	}
            	
            	return false;
                
            }
        }
        	
        // Chance
        
        // Compute global chance
        System.out.println("Global bruteforce failed! Taking lowest chance ...");
        float globalChance = globalProblem.getNumBombs()/(float)globalProblem.getNumFields();
        //System.out.println("Global chance is " + (Math.round((1-globalChance)*10000))/100.0f + " %.");
        //System.out.println(probabilities);
        float minChance = globalChance;
        Coord minCoord = null;
        for(Map.Entry<Coord, Float> prob : probabilities.entrySet()){
        	addToRelevantFields(prob.getKey());
        	//game.question(prob.getKey().x, prob.getKey().y);
        	if(prob.getValue() < minChance){
        		minChance = prob.getValue();
        		minCoord = prob.getKey();
        	}
        }
        
        if(minCoord != null){
        	winChance *= (1-minChance);
        	System.out.println("Taking a chance of " + (Math.round((1-minChance)*10000))/100.0f + " % ...");
        	longSleep();
        	touch(minCoord);
        	return false;
        }
        
        // Random
        System.out.println("Lowest chance not suitable! Taking random field ...");
        
        int bestQuality = -10000;
        Coord bestCandidate = null;
        
        for(int y = 0; y < game.getSizeY(); y++){
        	for(int x = 0; x < game.getSizeX(); x++){
        		Coord coord = new Coord(x,y,game);
        		if(!isTouchable(coord))
        			continue;
        		
        		int currentQuality = 0;
        		for(Coord neigh : coord.getNeighbors()){
        			switch(neigh.getFieldState()){
					case FLAGGED:
						currentQuality-=3;
						break;
					case QUESTIONED:
					case UNTOUCHED:
						currentQuality-=1;
						break;
					case REVEALED:
						currentQuality-=20;
						break;
					default:
						break;        				
        			}
        		}
        		if(currentQuality > bestQuality){
        			bestQuality = currentQuality;
        			bestCandidate = coord;
        		}
        	}
        }
        
        if(bestCandidate != null){
        	winChance *= (1-globalChance);
        	System.out.println("Taking a chance of " + (Math.round((1-globalChance)*10000))/100.0f + " % ...");
        	longSleep();
        	touch(bestCandidate);
        	return false;
        }
    	
        
        System.out.println("Taking random field failed. Running out of ideas.");
        
        while(true){sleep();}
    }

    private List<Coord> getImportantOpenFields() {
        List<Coord> result = new LinkedList<Coord>();
        for(int y = 0; y < game.getSizeY(); y++){
            for(int x = 0; x < game.getSizeX(); x++){
                if(game.getFieldState(x, y) != FieldState.REVEALED) continue;
                Coord currentField= new Coord(x,y,game);
                for(Coord neigh : currentField.getNeighbors()){
                    if(isTouchable(neigh)){
                        result.add(currentField);
                        break;
                    }
                }
            }
        }
        return result;
    }
    
    private List<Coord> getAllTouchableFields(){
    	List<Coord> result = new LinkedList<Coord>();
        for(int y = 0; y < game.getSizeY(); y++){
            for(int x = 0; x < game.getSizeX(); x++){
                switch(game.getFieldState(x, y)){
				case QUESTIONED:
				case UNTOUCHED:
					break;
				default:
					continue;
                }
                Coord currentField= new Coord(x,y,game);
                for(Coord neigh : currentField.getNeighbors()){
                    if(isTouchable(neigh)){
                        result.add(currentField);
                        break;
                    }
                }
            }
        }
        return result;
    }

    private void flagBombsNear(Coord coord){
        int num = coord.getFieldNumber();
        if(num <= 0) return;
        
        int numOpen = 0;
        int numFlagged = 0;
        for(Coord neigh : coord.getNeighbors()){
            switch(neigh.getFieldState()){
            case FLAGGED:
                numFlagged++;
                break;
            case QUESTIONED:
            case UNTOUCHED:
                numOpen++;
                break;
            default:
                break;
            
            }
        }
        
        int numLeft = num - numFlagged;
        if(numLeft == numOpen){
            for(Coord neigh : coord.getNeighbors()){
                switch(neigh.getFieldState()){
                case QUESTIONED:
                case UNTOUCHED:
                    flag(neigh);
                    break;
                default:
                    break;
                }
            }
        }
        
    }
    
    private boolean mustHaveBomb(Coord coord) {
        if(coord.getFieldState() == FieldState.FLAGGED){
            return true;
        }
        for(Coord neigh : coord.getNeighbors()){
            flagBombsNear(neigh);
        }
        return false;
    }

    private boolean isFilled(Coord coord){
        int num = coord.getFieldNumber();
        
        if(num < 0) return false;
        
        int numBomb = 0;
        for(Coord neigh : coord.getNeighbors()){
            if(mustHaveBomb(neigh)){
                numBomb++;
            }
        }
        
        return num==numBomb;
    }
    
    private boolean couldHaveBomb(Coord coord) {
        for(Coord neigh : coord.getNeighbors()){
            if(isFilled(neigh))
                return false;
        }
        return true;
    }

    @Override
    public boolean shutdown() {
        // TODO Auto-generated method stub
        return false;
    }

}
