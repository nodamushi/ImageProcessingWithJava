package data;

import static java.awt.image.BufferedImage.*;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

/**
 * 
 *突貫工事で作ったからそのうちきれいにしたい
 */
public class IntDatas{
    private int[] pixels;
    public final int width,height;
    
    public IntDatas(int width,int height){
        pixels = new int[width*height];
        this.width = width;
        this.height = height;
    }
    
    public boolean isOut(int x,int y){
        return x<0 || x>=width || y < 0 || y>=height;
    }
    
    public int getData(int x,int y){
        if(isOut(x, y))throw new ArrayIndexOutOfBoundsException(x+":"+y);
        return pixels[x+y*width];
    }
    
    public void setData(int x,int y,int data){
        if(isOut(x, y))return;
        pixels[x+y*width]=data;
    }
    
    public void inc(int x,int y){
        if(isOut(x, y))return;
        pixels[x+y*width]++;
    }
    
    public void dec(int x,int y){
        if(isOut(x, y))return;
        pixels[x+y*width]--;
    }
    
    
    public BufferedImage toImage(){
        BufferedImage img = new BufferedImage(width, height, TYPE_BYTE_GRAY);
        byte[] b =((DataBufferByte)img.getRaster().getDataBuffer()).getData();
        for(int i=0;i<pixels.length;i++){
            b[i] = (byte)(pixels[i]*25);
        }
        return img;
    }
}
