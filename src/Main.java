
public class Main {

    public static void main(String[] args) throws InterruptedException {
        // TODO Auto-generated method stub
        MinesweeperDrawer drawer = new MinesweeperDrawer();
        
        MinesweeperState state = new MinesweeperState(50,50,10);
        //state = MinesweeperState.createBeginner();
        Thread.sleep(1000);
        
        drawer.draw(state);
        Thread.sleep(200);
        state.touch(2, 2);
        //state.touch(3, 2);
        drawer.draw(state);
        
        System.out.println(state.getFreeSpacesLeft());
        
        while(true){
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

}
