package org.finomnis.minesweeper.game;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class DrawingWindow {
	
	private class GraphComponent extends JComponent{

		public GraphComponent(){
			this.width = 200;
			this.height = 200;
		}
		
		
		@Override
		public
		Dimension getPreferredSize(){
			return new Dimension(width,height);
		}
		
		private int width, height;
		
		private static final long serialVersionUID = 1L;
		
		private Lock l = new ReentrantLock();
		
		private BufferedImage internalRenderBuffer;
		
		@Override
		public void paintComponent(Graphics g){
			
			l.lock();
			try{
				g.drawImage(internalRenderBuffer, 0, 0, this);
			} finally {
				l.unlock();
			}
			
		}

		public boolean displayRenderBuffer(BufferedImage buffer) {
			boolean needsPack = false;
			l.lock();
			try{
				if(internalRenderBuffer == null){
					internalRenderBuffer = new BufferedImage(buffer.getWidth(), buffer.getHeight(), BufferedImage.TYPE_INT_RGB);
				} else {
					if(internalRenderBuffer.getHeight() != buffer.getHeight() || internalRenderBuffer.getWidth() != buffer.getWidth()){
						internalRenderBuffer = new BufferedImage(buffer.getWidth(), buffer.getHeight(), BufferedImage.TYPE_INT_RGB);
					}
				}
				Graphics g = internalRenderBuffer.getGraphics();
				g.drawImage(buffer, 0, 0, null);
				g.dispose();
				if(width != internalRenderBuffer.getWidth() || height != internalRenderBuffer.getHeight()){
					this.width = internalRenderBuffer.getWidth();
					this.height = internalRenderBuffer.getHeight();
					needsPack = true;
				}
			} finally {
				l.unlock();
			}
			return needsPack;
		}
		
	}
	
	private GraphComponent graphComponent;
	private JFrame window;
	
	private BufferedImage renderBuffer;
	
	public DrawingWindow(String name, int width, int height){
		
		renderBuffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		
		try {
			SwingUtilities.invokeAndWait(new Runnable(){
				public void run(){
					// Create Window
					window = new JFrame();
					window.setTitle(name);
					
					// Create Graph
					graphComponent = new GraphComponent();
					window.add(graphComponent,BorderLayout.CENTER);
					
					window.setResizable(false);
					window.pack();
					
					// Display
					window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	                window.setVisible(true);

				};
			});
		} catch (Exception e) {
			throw new RuntimeException(e);
		};
				
	}
	
	public Graphics2D createGraphics(){
		return renderBuffer.createGraphics();
	}
	
	public void clear(){
		Graphics2D g = renderBuffer.createGraphics();
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, renderBuffer.getWidth(), renderBuffer.getHeight());
		g.dispose();
	}
	
	public void resize(int width, int height){
		renderBuffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	}
	
	public int getWidth(){
	    return renderBuffer.getWidth();
	}
	
	public int getHeight(){
	    return renderBuffer.getHeight();
	}
	
	public void display(){
		if(graphComponent.displayRenderBuffer(renderBuffer)){
			window.pack();
		}		
		graphComponent.repaint();
	}
	
	public void close(){
		window.setVisible(false);
		window.dispose();
	}
	
}
