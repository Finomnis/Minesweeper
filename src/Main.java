

import org.finomnis.minesweeper.controller.AIController;
//import org.finomnis.minesweeper.controller.BinaryGauss;
//import org.finomnis.minesweeper.controller.ManualController;
import org.finomnis.minesweeper.game.MinesweeperGame;

public class Main {

    public static void main(String[] args) throws InterruptedException {

        MinesweeperGame game = new MinesweeperGame();
        game.setGameController(new AIController());
        game.run();

        //BinaryGauss<Integer> gauss = new BinaryGauss<Integer>();
        /*
        gauss.addDependency(1,0,3,6);
        gauss.addDependency(2, 0, 3,6,7,8,9);
        gauss.addDependency(1, 0, 7,8);
        */

        /*gauss.addDependency(2,0,0,1,3);
        gauss.addDependency(1,0,1,2);
        gauss.addDependency(1,0,2,3);
        
        gauss.solve();
        
        for(Map.Entry<Integer, Boolean> result : gauss.getResults().entrySet()){
            System.out.println(result.getKey() + " must be " + result.getValue());
        }*/
        
    }
        
}
