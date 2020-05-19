package com.check.checkxml;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.check.util.IOHelper;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * 检查可复用的xml文件
 * @author xiangleiliu
 *
 */
public class CheckXML {

	static String path="D:\\develop_workspace\\git_warehouse\\android_2\\CTHotel\\CTHotelMain\\res\\layout\\";
	SAXReader reader = new SAXReader();
	static Map<String,List<String>> map=new HashMap<>();
	
	public static void main(String[] args) {

		CheckXML checkXML = new CheckXML();
		try {
			checkXML.readFiles(path);

			// 遍历输出
			for (String key : map.keySet()) {
				List<String> list = map.get(key);
				for (String fileName : list) {
					System.out.println( key + ";" + fileName);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void readFiles(String path) throws DocumentException {
		File file=new File(path);
		File[] listFiles = file.listFiles();
		for (int i = 0; i < listFiles.length; i++) {
			File f = listFiles[i];
			String xmlKey = readXML(f);
			String fileName = f.getName();
			List<String> list = map.get(xmlKey);
			if(list==null){
				list=new ArrayList<>();
				map.put(xmlKey, list);
			}
			list.add(fileName);
		}
	}

	private String readXML(File f) throws DocumentException {
		InputStream is = IOHelper.fromFileToIputStream(f);
		Document read = reader.read(is);
		Element rootNode = read.getRootElement();
		String readElement = readElement(rootNode);
		return readElement;
	}

	private String readElement(Element element) {
		String name = element.getName();
		String str = "<" + name + ">";
		List<Element> elements = element.elements();
		for (Element ele : elements) {
			str += readElement(ele);
		}
		str += "</" + name + ">";
//		System.out.println(str);
		return str;
	}

}
