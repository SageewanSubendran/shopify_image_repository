package shopify_image_repository;
import javax.swing.*;  
import java.net.*; 
import java.awt.image.*;
import javax.imageio.*;
import java.io.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class seller {

	public static void main(String[] args) throws Exception {
	
		Socket soc;
	    BufferedImage img = null;
	    soc=new Socket("localhost",8080);
	    System.out.println("Client is running. ");
	
	    try {	
	         System.out.println("Reading image from disk. ");
	         img = ImageIO.read(new File("AppLogo.png"));
	         ByteArrayOutputStream baos = new ByteArrayOutputStream();
	         
	         ImageIO.write(img, "png", baos);
	         baos.flush();
	         
	         byte[] bytes = baos.toByteArray();
	         baos.close();
	         
	         System.out.println("Sending image to server. ");
	         
	         OutputStream out = soc.getOutputStream(); 
	         DataOutputStream dos = new DataOutputStream(out);
	         
	         dos.writeInt(bytes.length);
	         dos.write(bytes, 0, bytes.length);
	         
	         System.out.println("Image sent to server. ");

	         dos.close();
	         out.close();
	         
	      } catch (Exception e) {
	         System.out.println("Exception: " + e.getMessage());
	         soc.close();
	      }
	      soc.close();
	}

}
