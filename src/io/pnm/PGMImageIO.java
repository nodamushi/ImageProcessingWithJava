package io.pnm;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.*;
import java.util.Scanner;

/**
 * 
 * PGMファイルを読み込むためのクラス。
 */
public class PGMImageIO {
	/**
	 * 
	 * @param f
	 * @return TYPE_BYTE_GRYのBufferedImage
	 * @throws IOException
	 */
	public static BufferedImage readPGM(File f)throws IOException{
		try(FileInputStream in = new FileInputStream(f)){
			return readPGM(in);
		}
	}
	
	/**
	 * 
	 * @param in
	 * @return TYPE_BYTE_GRYのBufferedImage
	 * @throws IOException
	 */
	public static BufferedImage readPGM(InputStream in)throws IOException{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] imp = new byte[10000];
		int readsize = 0;
		while((readsize = in.read(imp, 0, imp.length))!=-1)out.write(imp, 0, readsize);
		byte[] data = out.toByteArray();
		out.flush();
		
		try(ByteArrayInputStream input = new ByteArrayInputStream(data);
				Scanner scan = new Scanner(input)){
			if(!scan.hasNext()){
				throw new IOException("can't read firstline!");
			}

			String type = scan.nextLine();
			if("P2".equals(type)){
				//TODO
				throw new IOException("みじっそー");
			}else if("P5".equals(type)){
				return readPGMBinary(data, scan);
			}else throw new IOException("not PGM!");
		}
	}
	
	private static BufferedImage readPGMBinary(byte[] bytedata,Scanner sc)throws IOException{
		String line =sc.nextLine();
		while(line.startsWith("#"))line=sc.nextLine();
		String[] wh = line.split(" ");
		if(wh.length < 2)throw new IOException("format error:can't get width and height!");
		String wS = wh[0];
		String hS = wh[1];
		line =sc.nextLine();
		while(line.startsWith("#"))line=sc.nextLine();
		String maxValueS = line.split(" ")[0];
		
		int width,height,maxValue;
		try{
			width = Integer.parseInt(wS);
			height = Integer.parseInt(hS);
			maxValue = Integer.parseInt(maxValueS);
		}catch(NumberFormatException e){
			throw new IOException("format error:NumberFormatException");
		}
		
		
		
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY),
				retimage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
		byte[] pixel = ((DataBufferByte)image.getRaster().getDataBuffer()).getData();
		System.arraycopy(bytedata, bytedata.length-pixel.length, pixel, 0, pixel.length);
		Graphics2D g = retimage.createGraphics();
		g.drawImage(image,0,0,null);
		g.dispose();
		image.flush();
		return retimage;
	}

}
