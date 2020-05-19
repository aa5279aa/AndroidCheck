package com.check.util;

import java.io.*;

public class RunTimeUtil {
	

//	cmd /c D:\develop_workspace\git_warehouse\protobuf\tools\execute\/protoc.exe -I=D:\develop_workspace\git_warehouse\protobuf\Contract --descriptor_set_out=D:\develop_workspace\git_warehouse\protobufout\proto\17301001\17301001.desc D:\develop_workspace\git_warehouse\protobuf\Contract/17301001.proto
//	public static boolean execCMD(String cmd, File path) {
//		Process proc;
//		try {
//			StringBuilder builder = new StringBuilder();
//			builder.append("cmd /k ");
//			builder.append(path);
//			builder.append(File.separator);
//			builder.append("/"+cmd);
//			Runtime rt = Runtime.getRuntime();
//			proc = rt.exec(builder.toString());
//			InputStream stderr = proc.getErrorStream();
//			InputStreamReader isr = new InputStreamReader(stderr);
//			BufferedReader br = new BufferedReader(isr);
//			String line = null;
//			while ((line = br.readLine()) != null) {
//				System.out.println(line);
//			}
//			int exitVal = proc.waitFor();
//			return exitVal == 0;
//		} catch (Exception e) {
//			e.printStackTrace();
//			return false;
//		}
//	}

	
	static ExecResultEnity lastSuccessExecEntity = new ExecResultEnity();
	static ExecResultEnity lastExecEntity = new ExecResultEnity();
//	public static boolean execCMD(String cmd, File execFile) {
//		cmd  = "git log --author=\"xiangleiliu\" --no-merges --since=2017-03-10 --until=2017-04-12 --stat > d://xiangleiliu.txt";
//        Runtime run = Runtime.getRuntime();// 杩斿洖涓庡綋鍓� Java 搴旂敤绋嬪簭鐩稿叧鐨勮繍琛屾椂瀵硅薄
//        try {
//            Process p = run.exec(cmd, null, execFile);
//            // 妫�鏌ュ懡浠ゆ槸鍚︽墽琛屽け璐ャ��
//            if (p.waitFor() != 0) {
//                if (p.exitValue() == 1)// p.exitValue()==0琛ㄧず姝ｅ父缁撴潫锛�1锛氶潪姝ｅ父缁撴潫
//                    System.err.println("鍛戒护鎵ц澶辫触!");
//            }
//            paserProcessResult(p, lastExecEntity);
//            System.out.println("success,and result:" + lastExecEntity.result);
//            p.destroy();
//            if (lastExecEntity.isSuccess && !lastExecEntity.result.contains("error")) {
//                lastSuccessExecEntity = lastExecEntity;
//                return true;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return false;
//    }
	
	public static String exeCmd(String commandStr, File file) {
        return exeCmd(commandStr, file, false);
    }

    public static String exeCmd(String commandStr, File file, boolean emptyIsSucess) {
        BufferedReader br = null;
        try {
            Process p = Runtime.getRuntime().exec(commandStr, null, file);
            String result = IOHelper.fromIputStreamToString(p.getInputStream());
            String error = IOHelper.fromIputStreamToString(p.getErrorStream());
            if (!StringUtil.emptyOrNull(error)) {
                return "fail:" + error;
            }
            if (StringUtil.emptyOrNull(result) && emptyIsSucess) {
                return "success";
            } else if (!StringUtil.emptyOrNull(result)) {
                return "success:" + result;
            }
            return "fail:";
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return "fail:";
    }
	
	
	private static void paserProcessResult(Process p, ExecResultEnity enity) {
        StringBuilder builder = new StringBuilder();
        InputStream errorStream = p.getErrorStream();
        BufferedInputStream error = new BufferedInputStream(errorStream);
        BufferedReader errorBr = new BufferedReader(
                new InputStreamReader(error));
        String lineStr = "";
        try {
            while ((lineStr = errorBr.readLine()) != null) {
                System.out.println(lineStr);// 鎵撳嵃杈撳嚭淇℃伅
                builder.append(lineStr + "\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (lineStr != null && lineStr.length() > 0) {
            enity.isSuccess = false;
            enity.result = builder.toString();
            return;
        }

        BufferedInputStream in = new BufferedInputStream(p.getInputStream());
        BufferedReader inBr = new BufferedReader(new InputStreamReader(in));

        try {
            while ((lineStr = inBr.readLine()) != null) {
                // 鑾峰緱鍛戒护鎵ц鍚庡湪鎺у埗鍙扮殑杈撳嚭淇℃伅
                System.out.println(lineStr);// 鎵撳嵃杈撳嚭淇℃伅
                builder.append(lineStr + "\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                inBr.close();
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        enity.isSuccess = true;
        enity.result = builder.toString();
    }
}
