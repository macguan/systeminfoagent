package cn.com.dwsoft.main;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import cn.com.dwsoft.main.HttpClientUtil;

import cn.com.security.ss.exception.WEException;
import cn.com.chinet.common.utils.file.FileOperateUtil;
import cn.com.chinet.common.utils.date.BatchDateTimeUtil;

import cn.com.dwsoft.util.file.PropertiesParser;

public class GetReportService{
	
	private static Log logger = LogFactory.getLog(GetReportService.class);
	
	//获取的报表,先存放在exportTmpPath下,再move到destPath目录下
	public static void getReport(String templateCode,String reportParam,String exportTmpPath,String destPath) throws Exception  {
			
			//1.define export dir
			//File directory = new File("export"); 
			//String exportTmpPath = directory.getAbsolutePath();
			
			FileOperateUtil.delDir(exportTmpPath);
			
			//2.httpclient access report website
			String exportTmpPathUrl=exportTmpPath.replaceAll("\\/","%2F");
			exportTmpPathUrl=exportTmpPathUrl.replaceAll("\\\\","%2F");
			exportTmpPathUrl=exportTmpPathUrl.replaceAll("\\:","%3A");
	
			//String templateCode = "DW_OA_hostsmonitor";
			//String reportParam = "&startdate=00000000&enddate=00000000&&unitId=1111&&random=128";
			
			//String hostportUrl = "http://localhost:8190/dw_oaV/";
			//String uriWithoutParam = "fsrcustom/FSRshowTemplateSumReportExcel.do?";
			String hostportUrl = null;
			String uriWithoutParam = null;
			try {
				PropertiesParser prop = new PropertiesParser("report.properties");
				String receiverStr = null;
				if(prop!=null){
					hostportUrl = prop.getInfoFromConfiguration("hostportUrl");
					uriWithoutParam = prop.getInfoFromConfiguration("uriWithoutParam");
				}else {
					throw new WEException(" The report.properties is null.  ");
					
				}
		;
			}catch(Exception e){
				logger.info(e.getMessage()); 
			}
			
			String param = "templateCode=" + templateCode
						   + reportParam
						   +"&export=1&downloadtype=excel_copy"
						   +"&exportdirpath="+exportTmpPathUrl;
						   
	        URL url = new URL(hostportUrl+uriWithoutParam+param);
	        //URL url = new URL("http://localhost:81912/dw_oaV/");
	   
			String resp = HttpClientUtil.doGet(url);
	       			
			//3. wait for received the export file and deal it (send mail)
			List<String> fileNameList= new ArrayList<String>();
			int i=0;
			while(fileNameList.size()==0&&i<60){
				fileNameList = FileOperateUtil.listFile(exportTmpPath);
				Thread.sleep(1000);
				i++;
			}
			if(i>=60){
				throw new WEException("Receiving export file failed.");
			}else {
				
				
				for(String fileName1:fileNameList){
					String exportFileName = fileName1;
					String exportFileNamefullPath =FileOperateUtil.addPathSuffix(exportTmpPath)+exportFileName;
					logger.info("received file: "+exportFileNamefullPath);
					FileOperateUtil.copyFile2Dir(destPath,exportFileName,new File(exportFileNamefullPath));
					FileOperateUtil.delDir(exportTmpPath);
					logger.info("file is moved to : "+destPath);
				}
				
				
			}
	                      
			
	    }

	public static void getReportAndSendMail() throws Exception {
		
		String checkdate = BatchDateTimeUtil.getBatch14();
		String alias_tmppath = "export"+"/"+"tmp";
		FileOperateUtil.createDir(alias_tmppath);
		File directory = new File(alias_tmppath);
		String exportTmpPath = directory.getAbsolutePath();
				
		
		String alias_exportpath = "export"+"/"+checkdate;
		FileOperateUtil.createDir(alias_exportpath);
		directory = new File(alias_exportpath);
		String exportDirPath = directory.getAbsolutePath();
		
		try{
			String templateCode = null;
			String reportParam = null;
			
			templateCode = "DW_OA_hostsmonitor";
			reportParam = "&startdate=00000000&enddate=00000000&&unitId=1111&&random=128";
			GetReportService.getReport(templateCode,reportParam,exportTmpPath,exportDirPath);
			
			templateCode = "DW_OA_processdownday";
			reportParam = "&startdate=00000000&enddate=00000000&&unitId=1111&&random=128";
			GetReportService.getReport(templateCode,reportParam,exportTmpPath,exportDirPath);
			
			
			String prjName = "贵州联通防骚扰";
			String mailType = "每日运营报表";
			//String reportName = "主机资源报表";
			
			String content = "见附件";
			
			String title = prjName+"-"+mailType+"[自动邮件]"+"["+checkdate+"]";
			
			
			SendMessages.SendWithAttachDir("macguan@163.com,guanxiaoyu@dwsoft.com.cn"
										,title
										,content
										,exportDirPath);
	    
		
		}catch (Exception e) {  
	 		logger.info(e.getMessage());  
	 	}finally{
			logger.info("delete directory "+exportDirPath); 
			FileOperateUtil.delDir(exportDirPath);
			FileOperateUtil.delDir(exportDirPath);
		}
		
		
		
				
		
	}
	
	
	public static void main(String[] args) throws Exception {
		
		GetReportService.getReportAndSendMail();
	}

}