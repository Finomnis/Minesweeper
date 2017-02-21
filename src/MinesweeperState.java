import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

public class MinesweeperState {

    public static enum FieldState { UNTOUCHED, FLAGGED, EXPLODED, QUESTIONED, REVEALED, FLAGGED_WRONG };
    
    private class Field{
        
        public final boolean isMine;
        private FieldState fieldState = FieldState.UNTOUCHED;
        
        public FieldState getState(){
            return fieldState;
        }
        public void setState(FieldState state){
            
            switch(this.fieldState){
            case FLAGGED:
            case FLAGGED_WRONG:
                numFlagged--;
                break;
            case REVEALED:
                numTouched--;
                break;
            default:
                break;
            }
            
            switch(state){
            case FLAGGED:
            case FLAGGED_WRONG:
                numFlagged++;
                break;
            case REVEALED:
                numTouched++;
                break;
            default:
                break;
            }

            this.fieldState = state;
            
        }
        
        public Field(boolean mine){
            this.isMine = mine;
            this.setState(FieldState.UNTOUCHED);
        }
        public boolean isTouchable(){
            switch(this.fieldState){
                case UNTOUCHED:
                case QUESTIONED:
                    return true;
                default:
                    return false;
            }
        }
        public void touch() {
            if(isTouchable()){
                if(isMine) this.setState(FieldState.EXPLODED);
                else this.setState(FieldState.REVEALED);
            }
        }
    }
    
    private boolean untouched = true;
    private boolean finished = false;
    private boolean won = false;
    private final Field[] state;
    private final int[] numbers;
    public final int sizeX;
    public final int sizeY;
    
    private int numMines;
    private int numTouched = 0;
    private int numFlagged = 0;
    
    public boolean gameFinished(){
        return finished;
    }
    
    public boolean gameWon(){
        return won;
    }
    
    public int getNumMinesLeft(){
        return numMines - numFlagged;
    }
    
    public int getFreeSpacesLeft(){
        return sizeX*sizeY - numMines - numTouched;
    }
    
    private Field[] generateMines(int size, int numMines){
        Field[] ret = new Field[size];
        ArrayList<Integer> nums = new ArrayList<Integer>();
        for(int i = 1; i<size; i++){
            nums.add(i);
        }
        Collections.shuffle(nums);
        for(int i = 0; i < numMines; i++){
            int pos = nums.get(i);
            ret[pos] = new Field(true);
        }
        for(int i = 0; i < size; i++){
            if(ret[i] == null){
                ret[i] = new Field(false);
            }
        }
        return ret;
    }
    
    public static MinesweeperState createExpert(){
        return new MinesweeperState(30,16,99);
    }
    
    public static MinesweeperState createIntermediate(){
        return new MinesweeperState(16,16,40);
    }
    
    public static MinesweeperState createBeginner(){
        return new MinesweeperState(8,8,10);
    }
    
    public MinesweeperState(int sizeX, int sizeY, int numMines){
        this.state = generateMines(sizeX * sizeY,numMines);
        this.numbers = new int[sizeX * sizeY];
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.numMines = numMines;
        this.computeNumbers();
    }
    
    private int getPos(int x, int y){
        return y*sizeX+x;
    }
    
    private int getX(int pos){
        return pos%sizeX;
    }
    
    private int getY(int pos){
        return pos/sizeX;
    }
    
    private Field getField(int x, int y){
        return state[getPos(x,y)];
    }
    
    private void firstTouch(int x, int y){
        int pos = getPos(x,y);
        Field tmp = state[pos];
        state[pos] = state[0];
        state[0] = tmp;
        computeNumbers();
    }
    
    private void computeNumbers(){
        for(int y = 0; y < sizeY; y++){
            for(int x = 0; x < sizeX; x++){
                int num = 0;
                for(int dy = -1; dy <= 1; dy++){
                    for(int dx = -1; dx <= 1; dx++){
                        if(dx == 0 && dy == 0) continue;
                        int px = x+dx;
                        int py = y+dy;
                        if(px < 0 || px >= sizeX || py < 0 || py >= sizeY) continue;
                        if(getField(px, py).isMine)
                            num++;
                    }
                }
                numbers[getPos(x,y)] = num;
            }
        }
    }
    
    public int getFieldNumber(int x, int y){
        return numbers[getPos(x,y)];
    }
    
    public FieldState getFieldState(int x, int y){
        return getField(x,y).getState();
    }
    
    public boolean isMine(int x, int y){
        return getField(x,y).isMine;
    }
    
    private void reveal(boolean exploded){
        for(Field field : state){
            switch(field.getState()){
            case QUESTIONED:
            case UNTOUCHED:
                if(field.isMine){
                    if(exploded){
                        field.setState(FieldState.REVEALED);
                    } else {
                        field.setState(FieldState.FLAGGED);
                    }
                }
                break;
            case FLAGGED:
                if(!field.isMine){
                    field.setState(FieldState.FLAGGED_WRONG);
                }
                break;
            case EXPLODED:
            case REVEALED:
            case FLAGGED_WRONG:
                break;
            }
        }
    }
    
    public boolean touch(int x, int y){

        assert(x >= 0 && x < sizeX && y >= 0 && y < sizeY);

        if(untouched){
            untouched = false;
            firstTouch(x,y);
        }

        Field field = getField(x,y);
        
        if(!field.isTouchable()){
            return false;
        }
        
        field.touch();
        
        if(field.getState() == FieldState.EXPLODED){
            reveal(true);
            finished = true;
            return true;
        }
        
        if(getFieldNumber(x,y) == 0){
            touchEmptySpace(x,y);   
        }
        
        if(this.getFreeSpacesLeft() == 0){
            reveal(false);
            this.finished = true;
            this.won = true;
            return true;
        }
        
        return false;
    }

    private void touchEmptySpace(int x, int y) {
        Set<Integer> toDo = new TreeSet<Integer>();
        toDo.add(getPos(x,y));
        
        while(!toDo.isEmpty()){
            int nextPos = toDo.iterator().next();
            toDo.remove(nextPos);
            
            int nextX = getX(nextPos);
            int nextY = getY(nextPos);
            
            for(int dy = -1; dy <= 1; dy++){
                for(int dx = -1; dx <= 1; dx++){
                    if(dx == 0 && dy == 0) continue;
                    int px = nextX+dx;
                    int py = nextY+dy;
                    if(px < 0 || px >= sizeX || py < 0 || py >= sizeY) continue;
                    
                    int fieldNumber = getFieldNumber(px,py);
                    Field field = getField(px,py);
                    
                    if(!field.isTouchable()) continue;
                    
                    field.touch();
                    
                    if(fieldNumber == 0){
                        toDo.add(getPos(px,py));
                    }
                }
            }
        }
        
    }
    
}
