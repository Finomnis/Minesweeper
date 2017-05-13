package org.finomnis.minesweeper.controller;

import org.finomnis.minesweeper.game.MinesweeperGame;

public abstract class GameController {
    protected MinesweeperGame game;
    
    public void start(MinesweeperGame game){
        this.game = game;
        init();
    }
    
    public void stop(){
        shutdown();
        this.game = null;
    }

    public abstract boolean init();
    public abstract boolean step();
    public abstract boolean shutdown();
    
}
