package org.finomnis.minesweeper.game;
import java.awt.Graphics2D;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

import org.finomnis.common.graphics.DrawingWindow;

public class MinesweeperDrawer{

    private DrawingWindow window;
    
    public MinesweeperDrawer(){
        this.window = new DrawingWindow("Minesweeper", 50, 50);
    }
    
    public void draw(MinesweeperState state){
        int fieldWidth = 16*state.sizeX;
        int fieldHeight = 16*state.sizeY;
        
        
        if(window.getWidth() != fieldWidth || window.getHeight() != fieldHeight){
            window.resize(fieldWidth, fieldHeight);
        }
        Graphics2D g2d = window.createGraphics();
        
        for(int y = 0; y < state.sizeY; y++){
            for(int x = 0; x < state.sizeX; x++){
                BufferedImage fieldSprite = null;
                
                switch(state.getFieldState(x, y)){
                case FLAGGED:
                    fieldSprite = Sprites.getFlag();
                    break;
                case REVEALED:
                    if(state.isMine(x, y)){
                        fieldSprite = Sprites.getMine();
                    } else {
                        fieldSprite = Sprites.getNumber(state.getFieldNumber(x, y));
                    }
                    break;
                case UNTOUCHED:
                    fieldSprite = Sprites.getHiddenField();
                    break;
                case EXPLODED:
                    fieldSprite = Sprites.getExplodedMine();
                    break;
                case QUESTIONED:
                    fieldSprite = Sprites.getQuestionmark();
                    break;
                case FLAGGED_WRONG:
                    break;
                }
                
                
                g2d.drawImage(fieldSprite, x*16,y*16,null);
            }
        }
        
        window.display();
    }
    
    public void addMouseListener(MouseListener listener){
        window.addMouseListener(listener);
    }
    
    public void removeMouseListener(MouseListener listener){
        window.removeMouseListener(listener);
    }
    
    public void addMouseMotionListener(MouseMotionListener listener){
        window.addMouseMotionListener(listener);
    }
    
    public void removeMouseMotionListener(MouseMotionListener listener){
        window.addMouseMotionListener(listener);
    }
    
}
