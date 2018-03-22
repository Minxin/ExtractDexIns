package com.wjdiankong.parsedex;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

import com.wjdiankong.parsedex.struct.CodeItem;

public class ParseDexMain {
	
	public static void main(String[] args){
		
		String className="Lcom/fancy/loaddextest/MainActivity;";
		String methodName="calsum(III)I";
		
		HashMap<String,CodeItem> codeItemMap=new HashMap<String,CodeItem>();
		
		
		byte[] srcByte = null;
		FileInputStream fis = null;
		ByteArrayOutputStream bos = null;
		try{
			fis = new FileInputStream("dex/testclasses.dex");
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
		
		System.out.println("ParseHeader:");
		ParseDexUtils.praseDexHeader(srcByte);
		System.out.println("++++++++++++++++++++++++++++++++++++++++");
		
		System.out.println("Parse StringIds:");
		ParseDexUtils.parseStringIds(srcByte);
		System.out.println("++++++++++++++++++++++++++++++++++++++++");
		
		System.out.println("Parse StringList:");
		ParseDexUtils.parseStringList(srcByte);
		System.out.println("++++++++++++++++++++++++++++++++++++++++");
		
		System.out.println("Parse TypeIds:");
		ParseDexUtils.parseTypeIds(srcByte);
		System.out.println("++++++++++++++++++++++++++++++++++++++++");
		
		System.out.println("Parse ProtoIds:");
		ParseDexUtils.parseProtoIds(srcByte);
		System.out.println("++++++++++++++++++++++++++++++++++++++++");
		
		System.out.println("Parse FieldIds:");
		ParseDexUtils.parseFieldIds(srcByte);
		System.out.println("++++++++++++++++++++++++++++++++++++++++");
		
		System.out.println("Parse MethodIds:");
		ParseDexUtils.parseMethodIds(srcByte);
		System.out.println("++++++++++++++++++++++++++++++++++++++++");
		
		System.out.println("Parse ClassIds:");
		ParseDexUtils.parseClassIds(srcByte);
		System.out.println("++++++++++++++++++++++++++++++++++++++++");
		
		System.out.println("Parse MapList:");
		ParseDexUtils.parseMapItemList(srcByte);
		System.out.println("++++++++++++++++++++++++++++++++++++++++");
		
		System.out.println("Parse Class Data:");
		ParseDexUtils.parseClassData(srcByte);
		System.out.println("++++++++++++++++++++++++++++++++++++++++");
		
		System.out.println("Parse Code Content:");
		ParseDexUtils.parseCode(srcByte);
		System.out.println("++++++++++++++++++++++++++++++++++++++++");
		
		
		
		codeItemMap.putAll(ParseDexUtils.directMethodCodeItemMap);
		codeItemMap.putAll(ParseDexUtils.virtualMethodCodeItemMap);
		
		for (String key : codeItemMap.keySet()) {
			System.out.println("key:"+key);
			if(key.equals(className+methodName)){
				CodeItem codeitem=codeItemMap.get(key);
				
				int insns_size=codeitem.insns_size;
				int insnsoffset = codeitem.insnsoffset;
				
				System.out.println("ins_size:"+insns_size+"ins_offset:"+insnsoffset);
				
				byte[] nopBytes = new byte[insns_size*2];
				for(int i =0;i<nopBytes.length;i++) {
					nopBytes[i]=0;
				}
				
				srcByte=Utils.replaceBytes(srcByte, nopBytes, insnsoffset);
				byte[] signvalue=Utils.signature(srcByte,32);
				srcByte=Utils.replaceBytes(srcByte, signvalue, 12);
				
				byte[] checksum=Utils.checksum_bin(srcByte, 12);
				srcByte=Utils.replaceBytes(srcByte, checksum, 8);
			
				Utils.saveFile("dex/classes_tmp.dex", srcByte);
				break;
			}
		}
		
		
	
	}

}
