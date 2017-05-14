package org.finomnis.minesweeper.controller.ai;

import java.util.ArrayList;
import java.util.List;

import org.finomnis.minesweeper.game.MinesweeperGame;
import org.finomnis.minesweeper.game.MinesweeperState.FieldState;

public class Coord {
	public Coord(int x, int y, MinesweeperGame game){
        this.x = x;
        this.y = y;
        this.game = game;
    }
    public final int x;
    public final int y;
    private final MinesweeperGame game;
    
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
        if (game != other.game)
        	return false;
        if (x != other.x)
            return false;
        if (y != other.y)
            return false;
        return true;
    }
    
    public List<Coord> getNeighbors(){
        List<Coord> res = new ArrayList<Coord>();
        
        for(int y = this.y - 1; y <= this.y + 1; y++){
            if(y < 0 || y >= game.getSizeY()) continue;
            for(int x = this.x - 1; x <= this.x+1; x++){
                if(x < 0 || x >= game.getSizeX()) continue;
                if(x == this.x && y == this.y){
                    continue;
                }
                res.add(new Coord(x,y,game));
            }
        }
        
        return res;
    }
    
    public FieldState getFieldState(){
    	return game.getFieldState(x, y);
    }
    
    public int getFieldNumber(){
    	return game.getFieldNumber(x, y);
    }
    
    @Override
    public String toString(){
        return "Field(" + x + "," + y + ")";
    }
}
