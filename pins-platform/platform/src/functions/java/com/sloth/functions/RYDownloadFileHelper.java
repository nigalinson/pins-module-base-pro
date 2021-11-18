package com.sloth.functions;

import android.text.TextUtils;

import com.rongyi.common.functions.json.RYJson;
import com.rongyi.common.functions.log.LogUtils;
import com.rongyi.common.functions.storage.RYFileHelper;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Author:    ZhuWenWu
 * Version    V1.0
 * Date:      2017/8/25 下午4:23
 * Description: 文件同步下载工具类
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2017/8/25      ZhuWenWu            1.0                    1.0
 * Why & What is modified:
 */
public class RYDownloadFileHelper {
  private static final String TAG = RYDownloadFileHelper.class.getSimpleName();

  /**
   * 下载文件数据
   *
   * @param sUrl 文件下载地址
   * @return 文件下载路径
   */
  public static String downloadFile(String sUrl, String downloadDirectory) {
    if (TextUtils.isEmpty(sUrl)) {
      return null;
    }
    int nameIndex = sUrl.lastIndexOf("/");
    String fileName = sUrl.substring(nameIndex + 1);
    return RYDownloadFileHelper.downloadFile(sUrl, downloadDirectory, fileName);
  }

  /**
   * 同步下载文件数据 需要在异步线程中执行相应逻辑
   *
   * @param sUrl          文件下载地址
   * @param localFilePath 文件路径
   * @param fileName      文件名
   * @return 文件下载路径
   */
  public static String downloadFile(String sUrl, String localFilePath, String fileName) {
    try {
      LogUtils.d(TAG, "--> 准备下载文件  --> url:" + sUrl + " --> 文件路径:" + localFilePath + "文件名:" + fileName);

      File file = new File(localFilePath);
      if (!file.exists()) {
        boolean isSuccess = file.mkdirs();
        LogUtils.i(TAG, "--> 创建下载目录 :" + isSuccess);
      }
      File zipFile = new File(localFilePath + fileName);
      long downSize = 0;
      if (zipFile.exists()) {
        downSize = zipFile.length();
        LogUtils.d(TAG, "--> 文件已存在，已下载大小：" + downSize);
      }else{
        LogUtils.d(TAG, "--> 文件不存在，完全下载");
      }
      URL url = new URL(sUrl);
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setRequestProperty("Connection", "close");
      //Range 字节是从0开始，所以这里需要用文件已下载大小-1
      conn.setRequestProperty("Range", "bytes=" + downSize + "-");
      conn.connect();
      if (conn.getResponseCode() == 404) {//文件不存在
        LogUtils.w(TAG, "--> 远端文件不存在，无法下载或确认本地文件完整性，判定错误！！！  --> 文件地址:" + sUrl);
        conn.disconnect();
        return null;
      }
      if (0 == conn.getContentLength()) {
        //http协议返回空，默认已下载完成
        conn.disconnect();
        LogUtils.i(TAG, "--> HTTP返回空数据，认为已无内容需要下载，判定成功  --> 文件路径:" + zipFile.getAbsolutePath());
        //发送文件下载成功的广播
        return zipFile.getAbsolutePath();
      }
      long fileSize;
      String contentRange = conn.getHeaderField("Content-Range");
      if (TextUtils.isEmpty(contentRange)) {
        fileSize = conn.getContentLength();
        LogUtils.w(TAG, "--> 不支持断点续传，需要重新下载的文件大小：" + fileSize);
        if (downSize == fileSize) {
          conn.disconnect();
          LogUtils.i(TAG, "--> 本地已存在完整文件，直接判定成功");
          return zipFile.getAbsolutePath();
        }else{
          LogUtils.i(TAG, "--> 本地文件下载不完整，且不支持断点续传，删除历史文件，重新下载");
          downSize = 0;
          RYFileHelper.deleteFile(zipFile);
        }
      } else {
        String[] ranges = contentRange.split("/");
        LogUtils.w(TAG, "--> 断点续传参数：" + RYJson.get().toJson(ranges));
      }

      LogUtils.d(TAG, "--> 正在下载文件！！！");
      InputStream is = conn.getInputStream();
      if (!zipFile.exists()) {
        zipFile = new File(localFilePath + fileName);
      }
      RandomAccessFile fos = new RandomAccessFile(zipFile, "rwd");
      if (downSize > 0) {
        fos.seek(downSize);
      }
      byte[] buf = new byte[1024];
      int numRead;
      while ((numRead = is.read(buf)) != -1) {
        if (numRead <= 0) {
          break;
        }
        fos.write(buf, 0, numRead);
      }
      fos.close();
      is.close();
      conn.disconnect();

      return zipFile.getAbsolutePath();
    } catch (Exception e) {
      e.printStackTrace();
      LogUtils.e(TAG, "--> 文件下载出错！！！  --> e = " + e.getMessage());
      return null;
    }
  }
}
