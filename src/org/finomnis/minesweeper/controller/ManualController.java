package org.finomnis.minesweeper.controller;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class ManualController extends GameController implements MouseListener, MouseMotionListener {

    @Override
    public boolean step() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean init() {
        System.out.println("Init");
        game.addMouseListener(this);
        game.addMouseMotionListener(this);
        return false;
    }

    @Override
    public boolean shutdown() {
        game.removeMouseMotionListener(this);
        game.removeMouseListener(this);
        return false;
    }

    @Override
    public void mouseDragged(MouseEvent arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void mouseMoved(MouseEvent arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void mouseClicked(MouseEvent arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void mouseEntered(MouseEvent arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void mouseExited(MouseEvent arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void mousePressed(MouseEvent arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void mouseReleased(MouseEvent arg0) {
        
        int tileX = (arg0.getX() - game.getFieldOriginX())/game.getTileSizeX();
        int tileY = (arg0.getY() - game.getFieldOriginY())/game.getTileSizeY();
        
        if(tileX >= 0 && tileX < game.getSizeX() && tileY >= 0 && tileY < game.getSizeY()){
            if(arg0.getButton() == MouseEvent.BUTTON1){
                game.touch(tileX, tileY);
            }
            else if(arg0.getButton() == MouseEvent.BUTTON3){
                switch(game.getFieldState(tileX, tileY)){
                    case FLAGGED:
                        game.question(tileX, tileY);
                        break;
                    case QUESTIONED:
                        game.unflag(tileX, tileY);
                        break;
                    case UNTOUCHED:
                        game.flag(tileX, tileY);
                        break;
                    default:
                        break;
                }
            }
        }
    
    }

}
