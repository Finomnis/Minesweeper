package org.finomnis.minesweeper.game;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

public class Sprites {
    
    private final BufferedImage[] numbers;
    private final BufferedImage[] fields;
    private final BufferedImage[] borders;
    private final BufferedImage[] tileableBorders;
    private final BufferedImage backgroundColor;
    private final BufferedImage numberBox;
    private final BufferedImage[] numberBoxNumbers;
    private final BufferedImage[] faces;
    
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
        
        borders = new BufferedImage[6];
        borders[0] = src.getSubimage( 0,  82, 12, 11);
        borders[1] = src.getSubimage(15,  82, 12, 11);
        borders[2] = src.getSubimage( 0,  96, 12, 11);
        borders[3] = src.getSubimage(15,  96, 12, 11);
        borders[4] = src.getSubimage( 0, 110, 12, 12);
        borders[5] = src.getSubimage(15, 110, 12, 12);
        
        tileableBorders = new BufferedImage[7];
        tileableBorders[0] = src.getSubimage(13,  82,  1, 11);
        tileableBorders[1] = src.getSubimage( 0,  94, 12,  1);
        tileableBorders[2] = src.getSubimage(15,  94, 12,  1);
        tileableBorders[3] = src.getSubimage(13,  96,  1, 11);
        tileableBorders[4] = src.getSubimage( 0, 108, 12,  1);
        tileableBorders[5] = src.getSubimage(15, 108, 12,  1);
        tileableBorders[6] = src.getSubimage(13, 110,  1, 12);
     
        backgroundColor = src.getSubimage(70, 82, 1, 1);
        
        numberBox = src.getSubimage(28, 82, 41, 25);
        
        numberBoxNumbers = new BufferedImage[11];
        for(int i = 0; i < numberBoxNumbers.length; i++){
        	numberBoxNumbers[i] = src.getSubimage(12*i,33,11,21);
        }
        
        faces = new BufferedImage[5];
        for(int i = 0; i < faces.length; i++){
        	faces[i] = src.getSubimage(27*i,55,26,26);
        }
        
    }

    public static BufferedImage getNumber(int i){
        assert(i >= 0 && i <= 8);
        return singleton.numbers[i];
    }
    
    public static BufferedImage getHiddenField(){
        return singleton.fields[0];
    }
    
    public static BufferedImage getHiddenFieldPressed(){
        return singleton.fields[1];
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
    
    public static BufferedImage getQuestionmarkPressed(){
        return singleton.fields[7];
    }

	public static BufferedImage getBorder(int i) {
		assert(i >= 0 && i <= 5);
		return singleton.borders[i];
	}
	
	public static BufferedImage getTileableBorder(int i) {
		assert(i >= 0 && i <= 6);
		return singleton.tileableBorders[i];
	}
	
	public static BufferedImage getBackgroundColor(){
		return singleton.backgroundColor;
	}
	
	public static BufferedImage getNumberBox(){
		return singleton.numberBox;
	}
	
	public static BufferedImage getNumberBoxNumber(int i) {
		assert(i >= 0 && i <= 10);
		return singleton.numberBoxNumbers[i];
	}
	
	public static BufferedImage getFace(int i) {
		assert(i >= 0 && i <= 4);
		return singleton.faces[i];
	}
	
}
