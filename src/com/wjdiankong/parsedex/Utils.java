package com.wjdiankong.parsedex;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Adler32;

public class Utils {
	
	public static int byte2int(byte[] res) { 
		int targets = (res[0] & 0xff) | ((res[1] << 8) & 0xff00)
				| ((res[2] << 24) >>> 8) | (res[3] << 24); 
		return targets; 
	}
	
	public static byte[] int2Byte(final int integer) {
		int byteNum = (40 -Integer.numberOfLeadingZeros (integer < 0 ? ~integer : integer))/ 8;
		byte[] byteArray = new byte[4];

		for (int n = 0; n < byteNum; n++)
			byteArray[3 - n] = (byte) (integer>>> (n * 8));

		return (byteArray);
	}
	
    public static byte[] short2Byte(short number) { 
        int temp = number; 
        byte[] b = new byte[2]; 
        for (int i = 0; i < b.length; i++) { 
            b[i] = new Integer(temp & 0xff).byteValue();//将最低位保存在最低位 
            temp = temp >> 8; // 向右移8位 
        } 
        return b; 
    } 
	
    public static short byte2Short(byte[] b) { 
        short s = 0; 
        short s0 = (short) (b[0] & 0xff);
        short s1 = (short) (b[1] & 0xff); 
        s1 <<= 8; 
        s = (short) (s0 | s1); 
        return s; 
    }
	
	public static String bytesToHexString(byte[] src){  
		//byte[] src = reverseBytes(src1);
		StringBuilder stringBuilder = new StringBuilder("");  
		if (src == null || src.length <= 0) {  
			return null;  
		}  
		for (int i = 0; i < src.length; i++) {  
			int v = src[i] & 0xFF;  
			String hv = Integer.toHexString(v);  
			if (hv.length() < 2) {  
				stringBuilder.append(0);  
			}  
			stringBuilder.append(hv+" ");  
		}  
		return stringBuilder.toString();  
	}  
	
	public static char[] getChars(byte[] bytes) {
		Charset cs = Charset.forName ("UTF-8");
		ByteBuffer bb = ByteBuffer.allocate (bytes.length);
		bb.put (bytes);
		bb.flip ();
		CharBuffer cb = cs.decode (bb);
		return cb.array();
	}
	
	public static byte[] copyByte(byte[] src, int start, int len){
		if(src == null){
			return null;
		}
		if(start > src.length){
			return null;
		}
		if((start+len) > src.length){
			return null;
		}
		if(start<0){
			return null;
		}
		if(len<=0){
			return null;
		}
		byte[] resultByte = new byte[len];
		for(int i=0;i<len;i++){
			resultByte[i] = src[i+start];
		}
		return resultByte;
	}
	
	public static byte[] reverseBytes(byte[] bytess){
		byte[] bytes = new byte[bytess.length];
		for(int i=0;i<bytess.length;i++){
			bytes[i] = bytess[i];
		}
    	if(bytes == null || (bytes.length % 2) != 0){
    		return bytes;
    	}
    	int i = 0, len = bytes.length;
    	while(i < (len/2)){
    		byte tmp = bytes[i];
    		bytes[i] = bytes[len-i-1];
    		bytes[len-i-1] = tmp;
    		i++;
    	}
    	return bytes;
    }
	
	public static String filterStringNull(String str){
		if(str == null || str.length() == 0){
			return str;
		}
		byte[] strByte = str.getBytes();
		ArrayList<Byte> newByte = new ArrayList<Byte>();
		for(int i=0;i<strByte.length;i++){
			if(strByte[i] != 0){
				newByte.add(strByte[i]);
			}
		}
		byte[] newByteAry = new byte[newByte.size()];
		for(int i=0;i<newByteAry.length;i++){
			newByteAry[i] = newByte.get(i);
		}
		return new String(newByteAry);
	}
	
	public static String getStringFromByteAry(byte[] srcByte, int start){
		if(srcByte == null){
			return "";
		}
		if(start < 0){
			return "";
		}
		if(start >= srcByte.length){
			return "";
		}
		byte val = srcByte[start];
		int i = 1;
		ArrayList<Byte> byteList = new ArrayList<Byte>();
		while(val != 0){
			byteList.add(srcByte[start+i]);
			val = srcByte[start+i];
			i++;
		}
		byte[] valAry = new byte[byteList.size()];
		for(int j=0;j<byteList.size();j++){
			valAry[j] = byteList.get(j); 
		}
		try{
			return new String(valAry, "UTF-8");
		}catch(Exception e){
			System.out.println("encode error:"+e.toString());
			return "";
		}
	}
	
	/**
	 * 读取C语言中的uleb类型
	 * 目的是解决整型数值浪费问题
	 * 长度不固定，在1~5个字节中浮动
	 * @param srcByte
	 * @param offset
	 * @return
	 */
	public static byte[] readUnsignedLeb128(byte[] srcByte, int offset){
		List<Byte> byteAryList = new ArrayList<Byte>();
		byte bytes = Utils.copyByte(srcByte, offset, 1)[0];
		byte highBit = (byte)(bytes & 0x80);
		byteAryList.add(bytes);
		offset ++;
		while(highBit != 0){
			bytes = Utils.copyByte(srcByte, offset, 1)[0];
			highBit = (byte)(bytes & 0x80);
			offset ++;
			byteAryList.add(bytes);
		}
		byte[] byteAry = new byte[byteAryList.size()];
		for(int j=0;j<byteAryList.size();j++){
			byteAry[j] = byteAryList.get(j);
		}
		return byteAry;
	}
	
	/**
	 * 解码leb128数据
	 * 每个字节去除最高位，然后进行拼接，重新构造一个int类型数值，从低位开始
	 * @param byteAry
	 * @return
	 */
	public static int decodeUleb128(byte[] byteAry) {
//		int index = 0, cur;
//	    int result = byteAry[index];
//	    index++;
//	    
//	    if(byteAry.length == 1){
//	    	return result;
//	    }
//	    
//	    if(byteAry.length == 2){
//	    	cur = byteAry[index];
//	        index++;
//	        result = (result & 0x7f) | ((cur & 0x7f) << 7);
//	        return result;
//	    }
//	    
//	    if(byteAry.length == 3){
//	    	cur = byteAry[index];
//	        index++;
//	        result |= (cur & 0x7f) << 14;
//	        return result;
//	    }
//	    
//	    if(byteAry.length == 4){
//	    	cur = byteAry[index];
//	        index++;
//	        result |= (cur & 0x7f) << 21;
//	        return result;
//	    }
//        
//        if(byteAry.length == 5){
//        	cur = byteAry[index];
//            index++;
//            result |= cur << 28;
//            return result;
//        }
		int result;
	    byte cur;

	    result = byteAry[0];
	    if(result <0 || result>0x7f) {
	        cur = byteAry[1];
	        result = (result & 0x7f) | (int)((cur & 0x7f) << 7);
	        if(cur <0 || cur >0x7f) {
	            cur = byteAry[2];
	            result |= (int)(cur & 0x7f) << 14;
	            if(cur <0 || cur>0x7f) {
	                cur = byteAry[3];
	                result |= (int)(cur & 0x7f) << 21;
	                if(cur < 0  || cur >0x7f) {
	                    cur = byteAry[4];
	                    result |= (int)cur << 28;
	                }
	            }
	        }
	    }
        
        return result;
        
	}
	
	public static byte[] replaceBytes(byte[] srcByte,byte[] nopByte,int insoffset) {
		for(int i=insoffset;i<insoffset+nopByte.length;i++){
    			srcByte[i] = nopByte[i-insoffset];
    		}
		return srcByte;
		
	}
	
	public static boolean saveFile(String fileName, byte[] arys){
		File file = new File(fileName);
		FileOutputStream fos = null;
		try{
			fos = new FileOutputStream(file);
			fos.write(arys);
			fos.flush();
			return true;
		}catch(Exception e){
			System.out.println("save file error:"+e.toString());
		}finally{
			if(fos != null){
				try{
					fos.close();
				}catch(Exception e){
					System.out.println("close file error:"+e.toString());
				}
			}
		}
		return false;
	}
	
	public static int checksum_Lit(byte[] data, int off) {
	    byte[] bin = checksum_bin(data, off);
	    int value = 0;
	    for (int i = 0; i < 4; i++) {
	        int seg = bin[i];
	        if (seg < 0) {
	            seg = 256 + seg;
	        }
	        value += seg << (8 * i);
	    }
	    return value;
	}
	//计算checksum
	public static byte[] checksum_bin(byte[] data, int off) {
	    int len = data.length - off;
	    Adler32 adler32 = new Adler32();
	    adler32.reset();
	    adler32.update(data, off, len);
	    long checksum = adler32.getValue();
	    byte[] checksumbs = new byte[]{
	            (byte) checksum,
	            (byte) (checksum >> 8),
	            (byte) (checksum >> 16),
	            (byte) (checksum >> 24)};
	    return checksumbs;
	}
	
	//计算signature
    public static byte[] signature(byte[] data, int off) {
        int len = data.length - off;
        byte[] signature = SHA1(data, off, len);
        return signature;
    }

    //sha1算法
    private static byte[] SHA1(byte[] decript, int off, int len) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.update(decript, off, len);
            byte messageDigest[] = digest.digest();
            return messageDigest;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}
