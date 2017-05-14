package org.finomnis.minesweeper.game;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

import org.finomnis.common.graphics.DrawingWindow;

public class MinesweeperDrawer{

    private DrawingWindow window;
    
    public MinesweeperDrawer(){
        this.window = new DrawingWindow("Minesweeper", 51, 51);
    }
    
    public final int SIZE_BORDER_RIGHT=12;
    public final int SIZE_BORDER_LEFT=12;
    public final int SIZE_BORDER_TOP=11;
    public final int SIZE_BORDER_MID=11;
    public final int SIZE_BORDER_BOTTOM=12;
    public final int SIZE_STATUS_DISPLAY=33;
    
    public final int SIZE_STATUS_INSIDEBORDER_TOP=4;
    public final int SIZE_STATUS_INSIDEBORDER_LEFT=4;
    public final int SIZE_STATUS_INSIDEBORDER_RIGHT=6;
    
    public final int SIZE_NUMBERBOX = 41;
    public final int SIZE_FACE = 26;
    
    public final int FIELD_POS_X = SIZE_BORDER_LEFT;
    public final int FIELD_POS_Y = SIZE_STATUS_DISPLAY + SIZE_BORDER_TOP + SIZE_BORDER_MID;

    private void drawNumber(Graphics2D g2d, int num, int posX, int posY){
    	if(num > 999)
    		num=999;
    	if(num < -99)
    		num=-99;
    	if(num>=0){
    		g2d.drawImage(Sprites.getNumberBoxNumber(num/100), posX+2, posY+2, null);
    		num = num % 100;
    	} else {
    		g2d.drawImage(Sprites.getNumberBoxNumber(10), posX+2, posY+2, null);
    		num = -num;
    	}
    	
    	g2d.drawImage(Sprites.getNumberBoxNumber(num/10), posX+15, posY+2, null);
    	num = num % 10;
    	
    	g2d.drawImage(Sprites.getNumberBoxNumber(num), posX+28, posY+2, null);
    	
    	
    	
    }
    
    public void draw(MinesweeperState state){
        int fieldWidthRaw = 16*state.sizeX;
        int fieldHeightRaw = 16*state.sizeY;
    	
    	int fieldWidth = fieldWidthRaw + SIZE_BORDER_RIGHT + SIZE_BORDER_LEFT;
        int fieldHeight = fieldHeightRaw + SIZE_STATUS_DISPLAY + SIZE_BORDER_TOP + SIZE_BORDER_MID + SIZE_BORDER_BOTTOM;
        
        
        if(window.getWidth() != fieldWidth || window.getHeight() != fieldHeight){
            window.resize(fieldWidth, fieldHeight);
            Graphics2D g2d = window.createGraphics();
            g2d.setColor(new Color(255,0,255));
            g2d.fillRect(0, 0, window.getWidth(), window.getHeight());
            g2d.dispose();
        }
        Graphics2D g2d = window.createGraphics();
        
        int borderPosRight = fieldWidth-SIZE_BORDER_RIGHT;
        int borderPosMid = SIZE_BORDER_TOP + SIZE_STATUS_DISPLAY;
        int borderPosBottom = fieldHeight-SIZE_BORDER_BOTTOM;
        
        // Border corners
        g2d.drawImage(Sprites.getBorder(0),              0,               0, null);
        g2d.drawImage(Sprites.getBorder(1), borderPosRight,               0, null);
        g2d.drawImage(Sprites.getBorder(2),              0,    borderPosMid, null);
        g2d.drawImage(Sprites.getBorder(3), borderPosRight,    borderPosMid, null);
        g2d.drawImage(Sprites.getBorder(4),              0, borderPosBottom, null);
        g2d.drawImage(Sprites.getBorder(5), borderPosRight, borderPosBottom, null);
        
        // Horizontal borders
       	g2d.drawImage(Sprites.getTileableBorder(0),
       			SIZE_BORDER_LEFT,0,
       			fieldWidthRaw,SIZE_BORDER_TOP,null);
       	g2d.drawImage(Sprites.getTileableBorder(3),
       			SIZE_BORDER_LEFT,borderPosMid,
       			fieldWidthRaw,SIZE_BORDER_MID,null);
       	g2d.drawImage(Sprites.getTileableBorder(6),
       			SIZE_BORDER_LEFT,borderPosBottom,
       			fieldWidthRaw,SIZE_BORDER_BOTTOM,null);
       	
       	// Vertical borders left
       	g2d.drawImage(Sprites.getTileableBorder(1),
       			0, SIZE_BORDER_TOP,
       			SIZE_BORDER_LEFT, SIZE_STATUS_DISPLAY,null);
       	g2d.drawImage(Sprites.getTileableBorder(4),
       			0, FIELD_POS_Y,
       			SIZE_BORDER_LEFT, fieldHeightRaw,null);
       	
       	// Vertical borders right
       	g2d.drawImage(Sprites.getTileableBorder(2),
       			borderPosRight, SIZE_BORDER_TOP,
       			SIZE_BORDER_LEFT, SIZE_STATUS_DISPLAY,null);
       	g2d.drawImage(Sprites.getTileableBorder(5),
       			borderPosRight, FIELD_POS_Y,
       			SIZE_BORDER_LEFT, fieldHeightRaw,null);
       	
        // Status field background
        g2d.drawImage(Sprites.getBackgroundColor(), SIZE_BORDER_LEFT, SIZE_BORDER_TOP, fieldWidthRaw, SIZE_STATUS_DISPLAY, null);
       	
        // Numberbox
        g2d.drawImage(Sprites.getNumberBox(),
        		SIZE_BORDER_LEFT + SIZE_STATUS_INSIDEBORDER_LEFT,
        		SIZE_BORDER_TOP  + SIZE_STATUS_INSIDEBORDER_TOP, null);
        g2d.drawImage(Sprites.getNumberBox(),
        		borderPosRight - (SIZE_STATUS_INSIDEBORDER_RIGHT + SIZE_NUMBERBOX),
        		SIZE_BORDER_TOP  + SIZE_STATUS_INSIDEBORDER_TOP, null);
        
        drawNumber(g2d, state.getNumMinesLeft(),
        		SIZE_BORDER_LEFT + SIZE_STATUS_INSIDEBORDER_LEFT,
        		SIZE_BORDER_TOP  + SIZE_STATUS_INSIDEBORDER_TOP);
        
        drawNumber(g2d, (int)((state.getTime()+999)/1000),
        		borderPosRight - (SIZE_STATUS_INSIDEBORDER_RIGHT + SIZE_NUMBERBOX),
        		SIZE_BORDER_TOP  + SIZE_STATUS_INSIDEBORDER_TOP);
        
        // Face
        BufferedImage faceImg = Sprites.getFace(0);
        if(state.gameFinished()){
        	if(state.gameWon()){
        		faceImg = Sprites.getFace(3);
        	} else {
        		faceImg = Sprites.getFace(2);
        	}
        } else if(state.isFacePressed()) {
        	faceImg = Sprites.getFace(4);
        } else if(state.isMouseDown()){
        	faceImg = Sprites.getFace(1);
        }
        g2d.drawImage(faceImg,
        		(fieldWidth-SIZE_FACE)/2,
        		SIZE_BORDER_TOP  + SIZE_STATUS_INSIDEBORDER_TOP, null);
        
        
       	// Field
        for(int y = 0; y < state.sizeY; y++){
            for(int x = 0; x < state.sizeX; x++){
                BufferedImage fieldSprite = null;
                
                boolean mouseDown = false;
                if(state.isMouseDown() && x == state.getMouseDownX() && y == state.getMouseDownY()){
                	mouseDown = true;
                }
                
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
                	if(mouseDown){
                		fieldSprite = Sprites.getHiddenFieldPressed();
                	} else {
                		fieldSprite = Sprites.getHiddenField();
                	}
                    break;
                case EXPLODED:
                    fieldSprite = Sprites.getExplodedMine();
                    break;
                case QUESTIONED:
                	if(mouseDown){
                		fieldSprite = Sprites.getQuestionmarkPressed();
                	} else {
                		fieldSprite = Sprites.getQuestionmark();
                	}
                    break;
                case FLAGGED_WRONG:
                    break;
                }
                
                
                g2d.drawImage(fieldSprite, FIELD_POS_X + x*16, FIELD_POS_Y + y*16,null);
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
