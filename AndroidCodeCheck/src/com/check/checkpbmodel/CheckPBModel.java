package com.check.checkpbmodel;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.check.util.IOHelper;

/**
 * 检查PB单重可复用的Model
 * @author xiangleiliu
 */
public class CheckPBModel {

	// static Map<String, List<String>> map = new HashMap<>();
	static List<PBClassModel> pbList = new ArrayList<>();
	static List<PBGroupModel> groupList = new ArrayList<>();

	// static Map<String,>

	// 报文比对工具
	public static void main(String[] args) {
		File file = new File(
				"D:\\develop_workspace\\git_warehouse\\android_2\\CTHotel\\CTHotelMain\\src\\ctrip\\android\\hotel\\model");
		try {
			CheckPBModel test6 = new CheckPBModel();
			test6.traverseFile(file);

			for (int i = 0; i < pbList.size(); i++) {
				PBClassModel pbClassModel = pbList.get(i);
				test6.actionPbModel(pbClassModel);
			}

			for (PBGroupModel groupModel : groupList) {
				for (PBClassModel pBClassModel : groupModel.list) {
					System.out.println(groupModel.groupId + ","
							+ pBClassModel.className);
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private void actionPbModel(PBClassModel inputPbClassModel) {

		for (PBGroupModel groupModel : groupList) {
			PBClassModel maxPBClassModel = groupModel.maxPBClassModel;
			int result = comparePBClassModel(maxPBClassModel, inputPbClassModel);
			if (result == -1) {
				continue;
			} else if (result == 1) {
				groupModel.list.add(inputPbClassModel);
				return;
			} else if (result == 2) {
				groupModel.maxPBClassModel = inputPbClassModel;
				groupModel.list.add(0,inputPbClassModel);
				return;
			} else {
				System.out.println("xx");
			}

		}

		PBGroupModel groupModel = new PBGroupModel();
		groupModel.groupId = groupList.size();
		groupModel.maxPBClassModel = inputPbClassModel;
		groupModel.list.add(0,inputPbClassModel);
		groupList.add(groupModel);
	}

	// -1=不匹配 1=model1包含model2 2=model2包含model1
	private int comparePBClassModel(PBClassModel model1, PBClassModel model2) {

		Map<String, String> value1 = model1.valueMap;
		Map<String, String> value2 = model2.valueMap;

		boolean value1More = value1.size() > value2.size();
		Map<String, String> valueBig;
		Map<String, String> valueSmall;
		if (value1More) {
			valueBig = value1;
			valueSmall = value2;
		} else {
			valueBig = value2;
			valueSmall = value1;
		}
		for (String key : valueBig.keySet()) {
			String string1 = valueBig.get(key);
			String string2 = valueSmall.get(key);
			if (string2 == null) {
				continue;
			}
//			System.out.println("key:"+key);
			if (string1 == null) {
				return -1;
			}
			if (!string1.equals(string2)) {
				return -1;
			}
		}
		return value1More ? 1 : 2;
	}

	private void traverseFile(File file) throws IOException {
		if (file.isDirectory()) {
			File[] listFiles = file.listFiles();
			for (File f : listFiles) {
				traverseFile(f);
			}
			return;
		}
		if (!file.getName().endsWith(".java")) {
			return;
		}
		InputStream is = IOHelper.fromFileToIputStream(file.getAbsolutePath());
		List<String> list = IOHelper.readListStrByCode(is, "utf-8");
		readModel(list);
	}

	public void readModel(List<String> list) {
		boolean isMatch = false;
		PBClassModel pbModel = new PBClassModel();
		for (int i = 0; i < list.size(); i++) {
			String line = list.get(i);
			if (line.contains("public class")) {
				isMatch = true;
				// 匹配获取类名
				pbModel.className = matchClassName(line);
				if (pbModel.className.length() == 0) {
					System.out.println("错误放置类:" + line);
					return;
				}
			}
			if (line.contains("public enum")) {
				System.out.println("枚举类:" + line);
				return;
			}

			if (!isMatch) {
				continue;
			}

			if (line.contains("@ProtoBufferField")) {
				String tagIndex = matchTagIndex(line);
				line = list.get(++i);
				if (line.contains("@SerializeField")) {
					line = list.get(++i);
				}
				String tagType = matchTagType(line);
				if (tagIndex.length() == 0) {
					System.out.println("pb格式不准确:" + pbModel.className);
				}
				pbModel.valueMap.put(tagIndex, tagType);
			}
		}
		if(pbModel.valueMap.size()==0){
			System.out.println("非PB类:" + pbModel.className);
			return;
		}
		pbList.add(pbModel);
	}

	public String matchClassName(String line) {
		Pattern compile = Pattern.compile("public class (.*?) extends");
		Matcher matcher = compile.matcher(line);
		while (matcher.find()) {
			String group = matcher.group(1);
			return group;
		}
		return "";
	}

	public String matchTagIndex(String line) {
		line = line.replaceAll(" ", "");
		Pattern compile = Pattern.compile(".*?tag=(\\d+),.*?");
		Matcher matcher = compile.matcher(line);
		while (matcher.find()) {
			String group = matcher.group(1);
			return group;
		}
		return "";
	}

	public String matchTagType(String line) {
		Pattern compile = Pattern.compile("public (.*?) ");
		Matcher matcher = compile.matcher(line);
		while (matcher.find()) {
			String group = matcher.group(1);
			return group;
		}
		return "";
	}

	class PBClassModel {
		String className;
		Map<String, String> valueMap = new HashMap<>();
	}

	class PBGroupModel {
		int groupId;
		PBClassModel maxPBClassModel;
		List<PBClassModel> list = new ArrayList<>();
	}

}
