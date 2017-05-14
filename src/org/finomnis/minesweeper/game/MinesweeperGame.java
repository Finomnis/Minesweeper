package org.finomnis.minesweeper.game;

import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import org.finomnis.minesweeper.controller.GameController;
import org.finomnis.minesweeper.game.MinesweeperState.FieldState;

public class MinesweeperGame {

    private final MinesweeperDrawer drawer;
    private final MinesweeperState currentGame;
    
    private GameController gameController = null;
    
    public MinesweeperGame(){
        this.drawer = new MinesweeperDrawer();
        //this.currentGame = new MinesweeperState(110, 55, 999);
        this.currentGame = MinesweeperState.createExpert();
        redraw();
    }
    
    public void redraw(){
        this.drawer.draw(this.currentGame);
    }
    
    public void run(){
        while(true){
            try {
                if(gameController != null){
                    while(gameController.step()){};
                }
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    public FieldState getFieldState(int x, int y){
        return currentGame.getFieldState(x, y);
    }
    
    public int getSizeX(){
        return currentGame.sizeX;
    }
    
    public int getSizeY(){
        return currentGame.sizeY;
    }
    
    public void touch(int x, int y){
        if(gameFinished()) return;
        currentGame.touch(x, y);
        redraw();
        if(gameFinished()){
            if(gameWon()){
                System.out.println("Game won! (" + currentGame.getTime()/1000.0f + " seconds)");
            }else{
                System.out.println("Game lost!");
            }
        }
    }
    
    public void flag(int x, int y){
        if(gameFinished()) return;
        currentGame.flag(x, y);
        redraw();
    }
    
    public void question(int x, int y){
        if(gameFinished()) return;
        currentGame.question(x, y);
        redraw();
    }
    
    public void unflag(int x, int y){
        if(gameFinished()) return;
        currentGame.unflag(x, y);
        redraw();
    }
    
    public boolean gameFinished(){
        return currentGame.gameFinished();
    }
    
    public boolean gameWon(){
        return currentGame.gameWon();
    }
    
    public void addMouseListener(MouseListener listener){
        drawer.addMouseListener(listener);
    }
    
    public void removeMouseListener(MouseListener listener){
        drawer.removeMouseListener(listener);
    }
    
    public void addMouseMotionListener(MouseMotionListener listener){
        drawer.addMouseMotionListener(listener);
    }
    
    public void removeMouseMotionListener(MouseMotionListener listener){
        drawer.addMouseMotionListener(listener);
    }

    public void setGameController(GameController controller) {
        if(this.gameController != null){
            this.gameController.stop();
        }
        
        this.gameController = controller;
        
        if(this.gameController != null){
            this.gameController.start(this);
        }        
    }

    public int getFieldOriginX(){
        return 0;
    }
    
    public int getFieldOriginY(){
        return 0;
    }
    
    public int getTileSizeX(){
        return 16;
    }
    
    public int getTileSizeY(){
        return 16;
    }

    public void mouseDown(int x, int y){
    	currentGame.mouseDown(x, y);
    	redraw();
    }
    
    public void mouseUp(){
    	currentGame.mouseUp();
    	redraw();
    }
    
    public int getFieldNumber(int x, int y) {
        if(currentGame.getFieldState(x, y) == FieldState.REVEALED)
            return currentGame.getFieldNumber(x, y);
        else
            return -1;
    }

	public int getNumMinesLeft() {
		return currentGame.getNumMinesLeft();
	}
    
}
