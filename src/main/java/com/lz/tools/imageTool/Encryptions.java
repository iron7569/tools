package com.lz.tools.imageTool;


import java.io.PrintStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Encryptions
{
  protected static Log log = LogFactory.getLog(Encryptions.class);
  
  public static String getMD5str(String str)
  {
    try
    {
      MessageDigest cmd = MessageDigest.getInstance("MD5");
      cmd.update(str.getBytes());
      byte[] b = cmd.digest();
      
      StringBuffer buf = new StringBuffer("");
      for (int offset = 0; offset < b.length; offset++)
      {
        int i = b[offset];
        if (i < 0) {
          i += 256;
        }
        if (i < 16) {
          buf.append("0");
        }
        buf.append(Integer.toHexString(i));
      }
      return buf.toString().toUpperCase();
    }
    catch (Exception ex)
    {
      log.info(ex.getMessage());
    }
    return "";
  }
  
  public static String comuteDigest(String name)
  {
    byte[] name_b = name.getBytes();
    String newName = "";
    try
    {
      MessageDigest messageDigest = MessageDigest.getInstance("MD5");
      messageDigest.reset();
      messageDigest.update(name_b);
      byte[] hash = messageDigest.digest();
      for (int i = 0; i < hash.length; i++)
      {
        int v = hash[i] & 0xFF;
        if (v < 16) {
          newName = newName + "0";
        }
        newName = newName + Integer.toString(v, 16).toUpperCase();
      }
    }
    catch (NoSuchAlgorithmException e)
    {
      System.out.println(e);
    }
    return newName;
  }
  
  public static void main(String[] args){
	
	  SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
      String time = format.format(new Date());
	  Long beginTime = Long.valueOf(time);
	  String path = "Z:\\image//2016/08/04/GPF-201608-00009/1/gpf-201608-00009_2.jpg";
	  String realMd5 = Encryptions.getMD5str(path + beginTime + "com.siit.image.showimage_");
	  String url = "http://imageftp.fiberhome.com:8010/HSImage/ShowImage?path="+path+"&mytype=1&oper=1&time="+time+"&test=7d1ec7471a036a5379e222b6d562f6bd&vls="
	  		+ realMd5;
	  System.out.println(path);
	  System.out.println(realMd5);
	  System.out.println(url);
	  //方式一，连续影像服务器数据库，把服务器图片放到本地（下载速度会快），然后把解析的图片的代码在本地运行起来，进行解析图片
	  //方式二：连续影像服务器数据库，根据数据中的文件路径，本地模拟下载url，循环调用url生成图片
  }
  
}
