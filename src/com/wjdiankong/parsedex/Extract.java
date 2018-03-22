package com.wjdiankong.parsedex;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;

public class Extract {

	public static void main(String args[]) {
		byte[] srcByte = null;
		FileInputStream fis = null;
		ByteArrayOutputStream bos = null;
		try{
			fis = new FileInputStream("dex/classes.dex");
			bos = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int len = 0;
			while((len=fis.read(buffer)) != -1){
				bos.write(buffer, 0, len);
			}
			srcByte = bos.toByteArray();
		}catch(Exception e){
			System.out.println("read res file error:"+e.toString());
		}finally{
			try{
				fis.close();
				bos.close();
			}catch(Exception e){
				System.out.println("close file error:"+e.toString());
			}
		}
		
		if(srcByte == null){
			System.out.println("get src error...");
			return;
		}
		
		System.out.println("++++++++++++++++++++++++++++++++++++++++");
		System.out.println("Parse Code Content:");
		ParseDexUtils.parseCode(srcByte);
		System.out.println("++++++++++++++++++++++++++++++++++++++++");
		
		
		
		
		System.out.println("++++++++++++++++++++++++++++++++++++++++");
		System.out.println("Parse Code Content:");
		ParseDexUtils.parseCode(srcByte);
		System.out.println("++++++++++++++++++++++++++++++++++++++++");
	}
}
