package com.check.calculatecode;

import com.check.checkxml.CheckXML;
import com.check.util.IOHelper;
import com.check.util.StringUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CalculateCode {


    public static void main(String[] args) {
        CalculateCode calculateCode = new CalculateCode();
        String path = "/Users/yanglei/develop/git_ware/gonggui/decoder-ark";
        File file = new File(path);
        //针对文件进行逻辑操作
        FileReplaceDoCallBack fileReplaceDoCallBack = new FileReplaceDoCallBack("space_", "dip", ".xml");
        calculateCode.seartFile(file, fileReplaceDoCallBack);
    }

    /**
     * 提供全局替换的实现类
     */
    static class FileReplaceDoCallBack implements FileDoCallBack {

        String oldStr;//原来的字符串
        String replaceStr;//替换为新的字符串
        String suffix;//后缀名

        FileReplaceDoCallBack(String oldStr, String replaceStr, String suffix) {
            this.oldStr = oldStr;
            this.replaceStr = replaceStr;
            this.suffix = suffix;
        }

        @Override
        public void filedo(File f) {
            String name = f.getName();
            if (StringUtil.emptyOrNull(name) || !name.endsWith(".java")) {
                return;
            }
            try {
                List<String> lineList = IOHelper.readListStrByCode(IOHelper.fromFileToIputStream(f), "utf-8");
                List<String> newLineList = new ArrayList<>();
                for (String line : lineList) {
                    line = line.replaceAll(oldStr, replaceStr);
                    newLineList.add(line);
                }
                String s = IOHelper.listToStr(newLineList);
                IOHelper.writerStrByCodeToFile(f, "utf-8", false, s);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * 访问一个文件夹或者一个文件
     */
    private void seartFile(File parentFile, FileDoCallBack callBack) {
        if (parentFile.isDirectory()) {
            File[] files = parentFile.listFiles();
            for (File file : files) {
                seartFile(file, callBack);
            }
            return;
        }
        callBack.filedo(parentFile);
    }


    interface FileDoCallBack {
        void filedo(File f);
    }

}
