package cn.com.dwsoft.main;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;


public class HttpClientUtil{
	
	private static Log logger = LogFactory.getLog(HttpClientUtil.class);
	
	public static String doGet(URL url) {
    
		
		long beginTime = System.currentTimeMillis();
        String respStr = StringUtils.EMPTY;
               
        
        
        HttpConnectionManager httpConnectionManager = new MultiThreadedHttpConnectionManager();
        
        HttpConnectionManagerParams params = httpConnectionManager.getParams();
        params.setConnectionTimeout(5000);
        params.setSoTimeout(20000);
        params.setDefaultMaxConnectionsPerHost(1000);
        params.setMaxTotalConnections(1000);
        
        HttpClient client = new HttpClient(httpConnectionManager);
        
        client.getParams().setContentCharset("UTF-8");
        client.getParams().setHttpElementCharset("UTF-8");

        
        try {
            logger.info("开始get通信，目标host:" + url);
            HttpMethod method = new GetMethod(url.toString());
            // 中文转码
            method.getParams().setContentCharset("UTF-8");
            try {
                client.executeMethod(method);
            } catch (HttpException e) {

                logger.error(new StringBuffer("发送HTTP GET给\r\n").append(url).append("\r\nHTTP异常\r\n"), e);
            } catch (IOException e) {

                logger.error(new StringBuffer("发送HTTP GET给\r\n").append(url).append("\r\nIO异常\r\n"), e);
            }
            if (method.getStatusCode() == 200) {
                respStr = method.getResponseBodyAsString();
            }
            // 释放连接
            method.releaseConnection();

            logger.info( "通讯完成，返回码：" + method.getStatusCode());
            logger.info( "返回内容：" + method.getResponseBodyAsString());
            logger.info( "结束..返回结果:" + respStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        long endTime = System.currentTimeMillis();
        logger.info( "共计耗时:" + (endTime - beginTime) + "ms");

        return respStr;
    }
	
	 public static void main(String[] args) throws Exception {
		 
			File directory = new File("export"); 
			String exportDirPath = directory.getAbsolutePath();
			
			String exportDirPathUrl=exportDirPath.replaceAll("\\/","%2F");
			exportDirPathUrl=exportDirPathUrl.replaceAll("\\\\","%2F");
			exportDirPathUrl=exportDirPathUrl.replaceAll("\\:","%3A");

			String hostportUrl = "http://localhost:8190/dw_oaV/";
			//String uriWithoutParam = "ycfastemplatemgr/showTemplateSumReportExcel.do?";
			String uriWithoutParam = "fsrcustom/FSRshowTemplateSumReportExcel.do?";
			String param = "templateCode=DW_OA_hostsmonitor"
			               +"&export=1&downloadtype=excel_copy"
						   +"&startdate=00000000&enddate=00000000&&unitId=1111&&random=128"
						   +"&exportdirpath="+exportDirPathUrl;
						   
	        URL url = new URL(hostportUrl+uriWithoutParam+param);
	        //URL url = new URL("http://localhost:81912/dw_oaV/");
	   
			String resp = doGet(url);
	        System.out.println(resp);
			
	                      
			
	    }



}