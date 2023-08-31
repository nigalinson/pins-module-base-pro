package com.sloth.pontus;

import com.sloth.platform.ResourceManagerComponent;
import com.sloth.platform.constants.Constants;
import com.sloth.utils.ConvertUtils;
import com.sloth.utils.FileUtils;
import com.sloth.utils.StringUtils;
import java.security.MessageDigest;
import java.util.Random;

class PathTool {

    static String willPutinPath(ResourceManagerComponent.ResourceManagerConfig resourceManagerConfig, String url) {
        String fileName = null;
        if(resourceManagerConfig.hashFileName()){
            fileName = getMD5(url);
            if(fileName == null){
                fileName = "default";
            }
            if(resourceManagerConfig.withSuffix()){
                int sufIndex = url.lastIndexOf(".");
                if(sufIndex != -1){
                    String suffix = url.substring(sufIndex);
                    fileName = fileName + suffix;
                }else{
                    fileName = fileName + ".unknown";
                }
            }
        }else{
            fileName = FileUtils.getFileName(url);
        }

        Constants.FileType fileType = FileUtils.predictFileType(url);

        return resourceManagerConfig.baseFolder() + fileType.name + "/" + fileName;
    }

    private static String getMD5(String var0) {
        Object var1 = null;
        if (var0 == null) {
            return null;
        } else {
            try {
                byte[] var2 = var0.getBytes();
                MessageDigest var3 = MessageDigest.getInstance("MD5");
                var3.update(var2);
                return ConvertUtils.bytes2HexString(var3.digest());
            } catch (Exception var4) {
                return (String)var1;
            }
        }
    }

}
