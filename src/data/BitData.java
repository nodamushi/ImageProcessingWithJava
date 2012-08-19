package data;


/**
 * 最大で64bitの長さのBitDataを扱うクラス。
 * それ以上にするのは単純に面倒くさかったんじゃーヽ( ・∀・)ﾉ
 *
 */
public class BitData{
    
    public static final int MAX_BITLENGTH = 64;
    
    int length;
    long bits=0;
    final long BitMask;
    
    
    
    public BitData(int bitLength)throws IllegalArgumentException{
        this(bitLength,0L);
    }
    
    public BitData(int bitLength,long value)throws IllegalArgumentException{
        if(bitLength < 0 || bitLength >= MAX_BITLENGTH)
            throw new IllegalArgumentException("bit length error!:"+bitLength);
        length =bitLength;
        BitMask = (1L <<length+1)-1;
        bits = value&BitMask;
    }
    
    public int getBitLength(){return length;}
   
    private boolean isOut(int bitNumber){
        return bitNumber < 0 || bitNumber >=length;
    }
    
    
    public long getValue(){
        return bits;
    }
    
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("bit length:").append(length).append("\n").
        append("bit value:").append(bits).append("\n").
        append("bit array:\n");
        for(int i=length-1;i>=0;i--){
            sb.append(getValue(i));
        }
        
        return sb.toString();
    }
    /**
     * 
     * @param bitNumber 値をセットするbit番号 0～BitLength-1
     * @param value 0かそうでないか(=1)
     */
    public void setValue(int bitNumber,int value){
        if(isOut(bitNumber))return;
        if(value !=0)value = 1;
        long v = (long)value<<bitNumber;
        long mask = 1L << bitNumber;
        bits =bits & ~mask | v;
    }
    
    /**
     * 
     * @param bitNumber 値をセットするbit番号 0～BitLength-1
     * @param value 0かそうでないか(=1)
     */
    public void setValue(int bitNumber,long value){
        setValue(bitNumber,value!=0?1:0);
    }
    
    public void negative(int bitNumber){
        setValue(bitNumber, 1-getValue(bitNumber));
    }
    
    /**
     * 
     * @param bitNumber 値をセットするbit番号 0～BitLength-1
     * @param value true=1かfalse=0
     */
    public void setValue(int bitNumber,boolean value){
        setValue(bitNumber,value?1:0);
    }
    
    /**
     * bitNumberビットの値を返す
     * @param bitNumber
     * @return 0か1
     */
    public int getValue(int bitNumber){
        if(isOut(bitNumber))return 0;
        return (int)((bits & (1L <<bitNumber))>>>bitNumber);
    }
    
    
    /**
     * startBitNumberからendBitNumber-1までのビット列の値を返します。<br>
     * 値はstartBitNumber分すでに右にシフトされています。
     * @param startBitNumber 0～bitLength-1
     * @param endBitNumber startBitNumber～bitLength
     * @return
     */
    public long getValue(int startBitNumber,int endBitNumber){
        endBitNumber--;
        if(isOut(startBitNumber)|| isOut(endBitNumber)||startBitNumber > endBitNumber)return 0L;
        long mask1,mask2,mask;
        mask1 = (1L << startBitNumber)-1;
        mask2 = (1L << endBitNumber+1)-1;
        
        mask = mask2^mask1;
        return (bits&mask) >>>startBitNumber;
    }
    
  
    
    /**
     * valueのstartBitNumberからendBitNumber-1までを移します。
     * @param startBitNumber 0～bitLength-1
     * @param endBitNumber startBitNumber～bitLength
     * @param value
     */
    public void setValue(int startBitNumber,int endBitNumber,long value){
        endBitNumber--;
        if(isOut(startBitNumber)|| isOut(endBitNumber)||startBitNumber > endBitNumber)return;
        long mask1,mask2,mask;
        mask1 = (1L << startBitNumber)-1;
        mask2 = (1L << endBitNumber+1)-1;
        
        mask = mask2^mask1;
        long setvalue = value &mask;
        bits  = bits & ~mask | setvalue;   
    }
    
    /**
     * 最初にvalueをstartBitNumber分だけ左にシフトした後にstartBitNumberからvalueBitSize分の長さのビットを移します
     * @param startBitNumber 
     * @param valueBitNumber valueのビット長
     * @param value
     */
    public void setShiftedValue(int startBitNumber,int valueBitSize,long value){
        int endBitNumber = startBitNumber + valueBitSize-1;
        if(isOut(startBitNumber)|| isOut(endBitNumber)||startBitNumber > endBitNumber)return;
        long mask1,mask2,mask;
        mask1 = (1L << startBitNumber)-1;
        mask2 = (1L << endBitNumber+1)-1;
        
        mask = mask2^mask1;
        long setvalue = value << startBitNumber & mask;
        bits = bits & ~mask | setvalue;
    }
    
    
}
