package org.finomnis.minesweeper.game;

public class MinesweeperGame {

    private final MinesweeperDrawer drawer;
    private final MinesweeperState currentGame;
    
    public MinesweeperGame(){
        this.drawer = new MinesweeperDrawer();
        this.currentGame = MinesweeperState.createExpert();
        redraw();
    }
    
    private void redraw(){
        this.drawer.draw(this.currentGame);
    }
    
    public void run(){
        while(true){
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
}
