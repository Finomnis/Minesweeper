package org.finomnis.minesweeper.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.finomnis.minesweeper.game.MinesweeperState.FieldState;

public class AIController extends GameController {

    private static class Coord {

        public Coord(int x, int y){
            this.x = x;
            this.y = y;
        }
        public final int x;
        public final int y;
        
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + x;
            result = prime * result + y;
            return result;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Coord other = (Coord) obj;
            if (x != other.x)
                return false;
            if (y != other.y)
                return false;
            return true;
        }
        
        @Override
        public String toString(){
            return "Field(" + x + "," + y + ")";
        }
    }
    
    private List<Coord> getNeighbors(Coord coord){
        List<Coord> res = new ArrayList<Coord>();
        
        for(int y = coord.y - 1; y <= coord.y + 1; y++){
            if(y < 0 || y >= game.getSizeY()) continue;
            for(int x = coord.x - 1; x <= coord.x+1; x++){
                if(x < 0 || x >= game.getSizeX()) continue;
                if(x == coord.x && y == coord.y){
                    continue;
                }
                res.add(new Coord(x,y));
            }
        }
        
        return res;
    }
    
    private boolean isTouchable(Coord coord){
        FieldState state = game.getFieldState(coord.x,coord.y);
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
    
    private void addToRelevantFields(Coord coord){
        while(relevantFields.remove(coord));
        if(mustHaveBomb(coord))
            return;
        relevantFields.addFirst(coord);
        game.question(coord.x, coord.y);
        
    }
    
    private Coord takeFromRelevantFields(){
        if(relevantFields.isEmpty())
            return null;
        Coord result = relevantFields.removeFirst();
        removeFromRelevantFields(result);
        return result;
    }
    
    private void removeFromRelevantFields(Coord coord){
        while(relevantFields.remove(coord));
        game.unflag(coord.x, coord.y);
    }
    
    private void updateRelevantFields(Coord coord, Set<Coord> alreadyLookedAt){
        
        FieldState state = game.getFieldState(coord.x,coord.y);
        
        if(state == FieldState.UNTOUCHED || state == FieldState.QUESTIONED){
            for(Coord neigh : getNeighbors(coord)){
                if(game.getFieldState(neigh.x, neigh.y) == FieldState.REVEALED){
                    addToRelevantFields(coord);
                    return;
                }
            }
            return;
        }
        
        if(state == FieldState.FLAGGED){
            for(Coord neigh : getNeighbors(coord)){
                if(isTouchable(neigh)){
                    updateRelevantFields(neigh);
                }
            }
            return;
        }
        
        if(state != FieldState.REVEALED){
            return;
        }
        
        if(game.getFieldNumber(coord.x,coord.y) > 0){
            for(Coord neigh : getNeighbors(coord)){
                if(isTouchable(neigh)){
                    addToRelevantFields(neigh);
                }
            }
            return;
        }
        
        // we have a revealed but empty field. now do a neighbor search
        alreadyLookedAt.add(coord);
        
        for(Coord neigh : getNeighbors(coord)){
            if(alreadyLookedAt.contains(neigh)) continue;
            updateRelevantFields(neigh, alreadyLookedAt);
        }
        
    }
    
    private void updateRelevantFields(Coord coord){
        updateRelevantFields(coord, new HashSet<Coord>());
    }

    @Override
    public boolean init() {
        // TODO Auto-generated method stub
        return false;
    }

    private void touch(Coord coord){
        //removeFromRelevantFields(coord);
        game.touch(coord.x, coord.y);
        if(game.gameFinished()){
            return;
        }
        
        updateRelevantFields(coord);
    }
    
    private void flag(Coord coord){
        removeFromRelevantFields(coord);
        game.flag(coord.x, coord.y);
        
        updateRelevantFields(coord);
    }
    
    @Override
    public boolean step() {

        if(game.gameFinished())
            return false;
        
        if(firstMove){
            firstMove = false;
            touch(new Coord(0,0));
            return false;
        }
        
        Coord coord = takeFromRelevantFields();
        while(coord != null){
            if(isTouchable(coord)){
                if(!couldHaveBomb(coord)){
                    touch(coord);
                    System.out.println(coord);
                    return false;
                }
            }
            coord = takeFromRelevantFields();
        }
        
        
        // GAUSS
        System.out.println("Direct logic failed! Trying Gauss ...");
        
        // get important fields for gauss
        List<Coord> importantFields = getImportantOpenFields();
        
        // initialize gauss
        BinaryGauss<Coord> gaussSolver = new BinaryGauss<Coord>();
        for(Coord importantField : importantFields){
            int existingFlagged = 0;
            Set<Coord> emptyNeighbors = new HashSet<Coord>();
            for(Coord neigh : getNeighbors(importantField)){
                switch(game.getFieldState(neigh.x, neigh.y)){
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
            gaussSolver.addDependency(game.getFieldNumber(importantField.x, importantField.y), existingFlagged, emptyNeighbors);
        }
        
        // run gauss
        gaussSolver.solve();
        
        if(gaussSolver.hasResults()){
            for(Map.Entry<Coord, Boolean> gaussSolution : gaussSolver.getResults().entrySet()){
                if(gaussSolution.getValue() == true){
                    game.flag(gaussSolution.getKey().x, gaussSolution.getKey().y);
                } else {
                    touch(gaussSolution.getKey());
                }
            }
            return false;
        }
        
        // Random
        System.out.println("Gauss failed! Trying something else ...");
        
        while(true);
        //return false;
    }

    private List<Coord> getImportantOpenFields() {
        List<Coord> result = new LinkedList<Coord>();
        for(int y = 0; y < game.getSizeY(); y++){
            for(int x = 0; x < game.getSizeX(); x++){
                if(game.getFieldState(x, y) != FieldState.REVEALED) continue;
                Coord currentField= new Coord(x,y);
                for(Coord neigh : getNeighbors(currentField)){
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
        int num = game.getFieldNumber(coord.x, coord.y);
        if(num <= 0) return;
        
        int numOpen = 0;
        int numFlagged = 0;
        for(Coord neigh : getNeighbors(coord)){
            switch(game.getFieldState(neigh.x, neigh.y)){
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
            for(Coord neigh : getNeighbors(coord)){
                switch(game.getFieldState(neigh.x, neigh.y)){
                case QUESTIONED:
                case UNTOUCHED:
                    game.flag(neigh.x, neigh.y);
                    break;
                default:
                    break;
                }
            }
        }
        
    }
    
    private boolean mustHaveBomb(Coord coord) {
        if(game.getFieldState(coord.x, coord.y) == FieldState.FLAGGED){
            return true;
        }
        for(Coord neigh : getNeighbors(coord)){
            flagBombsNear(neigh);
        }
        return false;
    }

    private boolean isFilled(Coord coord){
        int num = game.getFieldNumber(coord.x,coord.y);
        
        if(num < 0) return false;
        
        int numBomb = 0;
        for(Coord neigh : getNeighbors(coord)){
            if(mustHaveBomb(neigh)){
                numBomb++;
            }
        }
        
        return num==numBomb;
    }
    
    private boolean couldHaveBomb(Coord coord) {
        for(Coord neigh : getNeighbors(coord)){
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
