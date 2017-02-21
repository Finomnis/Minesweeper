package org.finomnis.minesweeper.game;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

public class Sprites {
    
    private final BufferedImage[] numbers;
    private final BufferedImage[] fields;
    
    private static Sprites singleton = new Sprites("/xpskin.bmp");
    
    public Sprites(String str) {
        BufferedImage src = null;
        try {
            InputStream res = Sprites.class.getResourceAsStream(str);
            src = ImageIO.read(res);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        
        numbers = new BufferedImage[9];
        for(int i = 0; i < numbers.length; i++){
            numbers[i] = src.getSubimage(16*i,0,16,16);
        }
        fields = new BufferedImage[8];
        for(int i = 0; i < fields.length; i++){
            fields[i] = src.getSubimage(16*i,16,16,16);
        }
    }

    public static BufferedImage getNumber(int i){
        assert(i >= 0 && i <= 8);
        return singleton.numbers[i];
    }
    
    public static BufferedImage getHiddenField(){
        return singleton.fields[0];
    }
    
    public static BufferedImage getMine(){
        return singleton.fields[2];
    }
    
    public static BufferedImage getExplodedMine(){
        return singleton.fields[5];
    }
    
    public static BufferedImage getFlag(){
        return singleton.fields[3];
    }
    
    public static BufferedImage getQuestionmark(){
        return singleton.fields[6];
    }
}
