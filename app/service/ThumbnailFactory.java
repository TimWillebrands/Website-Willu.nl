package service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import javax.imageio.ImageIO;

import net.coobird.thumbnailator.Thumbnails;
import play.Logger;

public class ThumbnailFactory {
	
	private static HashMap<String,String> waitingQueue = new HashMap<>();
	private static boolean isRunning = false;

	public static void createThumbnail(final String name, final String url){
		waitingQueue.put(name, url);
		generateNextThumbnail();
	}

	private static void generateNextThumbnail() {
		if(!waitingQueue.isEmpty() && !isRunning){
			isRunning = true;
			final String name = waitingQueue.keySet().iterator().next();
			final String url = waitingQueue.get(name);
					
			new Thread(new Runnable() {
	            public void run(){
	            	BufferedImage image =null;
	                try{
	                   image = ImageIO.read(new URL(url));
		               File fold=new File("public/images/thumbnails/thumbnail_"+name+".png");
		               if(fold.exists())
		            	   fold.delete();
	                   Thumbnails.of(image)
	        		       .size(60, 60)
	        		       .toFile("public/images/thumbnails/thumbnail_"+name+".png");
	                }catch(java.io.FileNotFoundException e){
	                    Logger.info("Thumbnail FileNotFoundException");
	                }catch(IOException e){
	                    Logger.info("Thumbnail IOException");
	                }

	    			isRunning = false;
	    			generateNextThumbnail();
	            }
	        }).start();
		}
	}
	
}
