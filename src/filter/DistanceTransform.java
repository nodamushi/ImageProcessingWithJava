package filter;

import data.IntDatas;
import data.Pixels;

public class DistanceTransform{
    
    private int maxDist;
    
    public DistanceTransform(int maxDist)throws IllegalArgumentException{
        this.maxDist = maxDist;
        if(maxDist < 0)
            throw new  IllegalArgumentException("maxDist < 0");
    }
    
    /**
     * 二値画像を距離画像に変換します。
     * @param p 0か0でないかの二値画像
     * @return
     */
    public IntDatas transform(Pixels p){
        IntDatas i = new IntDatas(p.width, p.height);
        int w = p.width,h = p.height;
        for(int x = 0;x< w;x++)for(int y=0;y<h;y++){
            float value = p.getPixel(x, y);
            if(value ==0f)continue;
            
            i.setData(x, y, maxDist);
            for(int l = 1;l<maxDist;l++){
                int set = maxDist -l;
                int sx = x-l;
                int ex = x+l;
                for(int u = sx;u<ex;u++){
                    int v1 = y-l;
                    int v2 = y+l;
                    set(i,u,v1,set);
                    set(i,u,v2,set);
                }
                for(int v = y-l+1;v<y+l;v++){
                    set(i,sx,v,set);
                    set(i,ex,v,set);
                }
            }
        }
        return i;
    }
    
    private void set(IntDatas i,int x,int y,int dis){
        if(i.isOut(x, y))return;
        if(i.getData(x, y) < dis)
            i.setData(x, y, dis);
    }

}
