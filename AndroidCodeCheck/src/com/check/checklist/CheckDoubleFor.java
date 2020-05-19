package com.check.checklist;

import com.check.checkxml.CheckXML;
import com.check.util.IOHelper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 找出所有的双重for循环以及三层for循环
 * <p>
 * 输出类名+行数
 */
public class CheckDoubleFor {

    public static void main(String[] args) {
        CheckDoubleFor checkDoubleFor = new CheckDoubleFor();

//        String path = "/Users/yanglei/develop/git_ware/gonggui/decoder-function/faultcode/src/main/java/com/crs/decoder/utils/FaultCodeUtil.java";
        String path = "/Users/yanglei/develop/git_ware/gonggui/decoder-app/";
        checkDoubleFor.searchForDirectory(new File(path));
    }


    public void searchForDirectory(File file) {
        File[] files = file.listFiles();
        for (File f : files) {
            if (f.isDirectory()) {
                searchForDirectory(f);
                continue;
            }
            searchForFile(f);
        }

    }


    public void searchForFile(File file) {
        if (!file.getName().endsWith(".java")) {
            return;
        }
        InputStream inputStream = IOHelper.fromFileToIputStream(file);
        String name = file.getName();
        List<String> list = new ArrayList<>();
        try {
            list = IOHelper.readListStrByCode(inputStream, "utf-8");
        } catch (IOException e) {
            e.printStackTrace();
        }

        int leftNum = 0;
        boolean isStart = false;

        for (int i = 0; i < list.size(); i++) {
            String s = list.get(i);
            if (s.contains("for") && !s.contains("format") && !isStart) {
                isStart = true;
                if (s.indexOf("{", s.indexOf("for")) > 0) {
                    leftNum++;
                }
                continue;
            }
            if (!isStart) {
                continue;
            }
            if (s.contains("{")) {
                leftNum++;
            }
            if (s.contains("}")) {
                leftNum--;
            }
            if (leftNum == 0) {
                isStart = false;
                continue;
            }
            if (s.contains("for") && !s.contains("format")) {
                System.out.println("fileName:" + name + ",line:" + (i + 1));
            }
        }
    }


}
