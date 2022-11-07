package com.lz.tools.imageTool;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ImageDownloadTest {


	static String basePath  = "d:\\imageTemp";


	static String excelPath = "d:\\imageTemp\\影像下载.xlsx";

	//static String excelPath = basePath + "\\"+ "2.xlsx";
	public static void main(String[] args) {
		Connection connection = null;
		try {
			  connection =getJDBCConnect();
			  Statement statement = createStatement(connection);
			  Set<String> businessnoList = getBusinessList(excelPath);
			  for (String businessno : businessnoList) {
				  String sql = "select path ,test from  T_IMAGE where barcode = '" + businessno + "'" ;
				  ResultSet result = statement.executeQuery(sql);
				  while(result.next()){
					  
					  //  /2019/05/14/T-2019-00002274/1/T-2019-00002274_1.jpg
					  String path = result.getString("path");
					  String[] split = path.split("/");
					  String path1 = "/"+split[4]+"/"+split[6];
					  String test = result.getString("test");
					  downloadPicture(getImageUrl(path,test),path1);
				  }
			  }
			  System.out.println("下载结束");
		} catch(Exception e){
			e.printStackTrace();
		}finally {
			if(connection != null){
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		 
	}
	
	
	public void downloadImage(List<String> urlList){
		for (String urlStr : urlList) {
			//downloadPicture(urlStr,path);
		}
	}
	
	
	private static void downloadPicture(String urlList,String path) {
        URL url = null;
        try {
        	  File file = new File(basePath+path);
              File fileParent = file.getParentFile();  
              if(!fileParent.exists()){  
                  fileParent.mkdirs();  
              }
              file.createNewFile();  
              if(!file.exists()){
              	file.mkdirs();
              }
            System.out.println("新生成：" + urlList);
            url = new URL(urlList);
            DataInputStream dataInputStream = new DataInputStream(url.openStream());
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            ByteArrayOutputStream output = new ByteArrayOutputStream();
 
            byte[] buffer = new byte[1024];
            int length;
 
            while ((length = dataInputStream.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
            fileOutputStream.write(output.toByteArray());
            dataInputStream.close();
            fileOutputStream.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	  //方式一，连续影像服务器数据库，把服务器已加密图片（不能直接）放到本地（下载速度会快），然后把解析的图片的代码在本地运行起来，进行解析图片
	  //方式二：连续影像服务器数据库，根据数据中的文件路径，本地模拟下载url，循环调用url生成图片
	public  static String getImageUrl(String path,String test){
		  SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
	      String time = format.format(new Date());
		  Long beginTime = Long.valueOf(time);
		  String picturePath = "Z:\\image"+path;
		  String realMd5 = Encryptions.getMD5str(picturePath + beginTime + "com.siit.image.showimage_");
		  String url = "http://image.fiberhome.com:8020/HSImage/ShowImage?path="+picturePath+"&mytype=1&oper=1&time="+time+"&test="+test+"&vls="
		  		+ realMd5;
		  //System.out.println("picturePath:"+picturePath);
		 // System.out.println(realMd5);
		 // System.out.println(url);
		  return url;
	}
	//方式一，连续影像服务器数据库，把服务器图片放到本地（下载速度会快），然后把解析的图片的代码在本地运行起来，进行解析图片
	//方式二：连续影像服务器数据库，根据数据中的文件路径，本地模拟下载url，循环调用url生成图片
	public  static Set<String> getBusinessList(String excelPath) throws Exception{
		Set<String> businessList = new HashSet<String>();
		/*ExcelImporter excelImporter = new ExcelImporter(excelPath);
		CellSet sheet = excelImporter.getCellSet(0);
		int count = sheet.getRow();*/
		//678
		ReadExcelUtils excelReader = new ReadExcelUtils(excelPath);
		// 对读取Excel表格标题测试
//		String[] title = excelReader.readExcelTitle();
//		System.out.println("获得Excel表格的标题:");
//		for (String s : title) {
//			System.out.print(s + " ");
//		}
		
		// 对读取Excel表格内容测试
		Map<Integer, Map<Integer,Object>> map = excelReader.readExcelContent();
		System.out.println("获得Excel表格的内容:");
		for (int i = 1; i <= map.size(); i++) {
			Object object = map.get(i).get(0);
			if(object != null){
				businessList.add(object.toString());
			}
		}
		return businessList;
	}
	
	public static Connection getJDBCConnect(){
		Connection conn=null;
		try {

			String url="";

			String password="";

			String user="";

			//首先建立驱动
			Class.forName("oracle.jdbc.driver.OracleDriver");

			//驱动成功后进行连接
			conn=DriverManager.getConnection(url, user, password);

			System.out.println("连接成功");
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return conn; //返回一个连接

	} 
	
	public static Statement createStatement(Connection connection) throws Exception{
		if(connection == null){
			connection = getJDBCConnect();
		}
		return connection.createStatement();
	}

}
