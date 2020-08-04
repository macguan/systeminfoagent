package cn.com.dwsoft.main;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;

import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeBodyPart;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.DataHandler;

import java.io.File;
import java.io.IOException;

import cn.com.dwsoft.util.file.PropertiesParser;
import cn.com.security.ss.exception.WEException;
import cn.com.chinet.common.utils.file.FileOperateUtil;

public class SendMessages {
	
	private static Log logger = LogFactory.getLog(SendMessages.class);

	
    public static void sendOneMailWithoutAttach(String receiverStr,String title,String value) throws Exception{
		logger.info("send mail without attach:");
		logger.info("receiver:"+receiverStr);
		logger.info("title:"+title);
		logger.info("send time:"+new Date());
		
		
        //Properties properties = new Properties();
        
		
		//String smtpPro = "smtp";
		//String smtphost = "smtp.mxhichina.com";
		//String smtpPort = "25";
		//String smtpAuth = "true";
		//String smtpSslEnable = "false";
		//String debugMode = "false";
		//properties.put("mail.transport.protocol",smtpPro );// 连接协议
        //properties.put("mail.smtp.host",smtphost );
		//properties.put("mail.smtp.port",smtpPort );// 端口号
        //properties.put("mail.smtp.auth",smtpAuth );
		//properties.put("mail.smtp.ssl.enable",smtpSslEnable );//设置是否使用ssl安全连接 ---一般都使用
        //properties.put("mail.debug",debugMode );//设置是否显示debug信息 true 会在控制台显示相关信息
		
		//String senderAddr = "guanxiaoyu@dwsoft.com.cn";
		//String senderPasswd = "Macguan008421";
		//String receiverStr = "macguan@163.com,guanxiaoyu@dwsoft.com.cn";
		
		String senderAddr = null;
		String senderPasswd = null;
		//String receiverStr = null;
		
		PropertiesParser prop = new PropertiesParser("mail.properties");
		if(prop!=null){
			senderAddr = prop.getInfoFromConfiguration("senderAddr");
			senderPasswd = prop.getInfoFromConfiguration("senderPasswd");
			//receiverStr = prop.getInfoFromConfiguration("receiverStr");
		}else {
			logger.info(" The mail.properties is null.  ");
			return ;
		}
		
		        
		//得到回话对象
        //Session session = Session.getInstance(properties);
		Session session = Session.getInstance(prop.getProperties());
		
		// 获取邮件对象
        Message message = new MimeMessage(session);
        
		//设置发件人邮箱地址
        message.setFrom(new InternetAddress(senderAddr));
        
		//设置收件人地址(可以群发)
		String[] receiverStrAs = null;
		if(StringUtils.isNotBlank(receiverStr)){
			receiverStrAs = receiverStr.split(",");
			InternetAddress[] receiverAs = new InternetAddress[receiverStrAs.length];
			for(int i=0;i<receiverStrAs.length;i++){
				receiverAs[i]=new InternetAddress(receiverStrAs[i]);	
			}
			 message.setRecipients(MimeMessage.RecipientType.TO, receiverAs);
		}else {
			throw new WEException("receiverStr is null.");
		}			
		
		
       
        
		//设置邮件标题
        message.setSubject(title);
        //设置邮件内容
        message.setText(value);
        
		//得到邮差对象
        Transport transport = session.getTransport();
        
		//连接自己的邮箱账户
        transport.connect(senderAddr,senderPasswd );      
		
		//发送
        transport.sendMessage(message, message.getAllRecipients());
		
		//结束
		for(int i=0;i<receiverStrAs.length;i++){	
			logger.info("send messaage to "+receiverStrAs[i]);			
		}
		
		
    }
	
	public static void SendWithAttach(String receiverStr,String title,String value,List<String> filePahtList) throws Exception{
		
		logger.info("send mail with attach:");
		logger.info("receiver:"+receiverStr);
		logger.info("title:"+title);
		logger.info("attach num:"+filePahtList.size());
		
		if(filePahtList!=null&& filePahtList.size()>0){
		   
		    for(String attachPath:filePahtList){
				
				logger.info("attach file:"+attachPath);
			}
			
		  }else {
			  throw new WEException("The attach is null!");
		  }
		  
		logger.info("send time:"+new Date());
		
		String senderAddr = null;
		String senderPasswd = null;
		//String receiverStr = null;
		
		PropertiesParser prop = new PropertiesParser("mail.properties");
		if(prop!=null){
			senderAddr = prop.getInfoFromConfiguration("senderAddr");
			senderPasswd = prop.getInfoFromConfiguration("senderPasswd");
			
			//receiverStr = prop.getInfoFromConfiguration("receiverStr");
		
		}else {
			logger.info(" The mail.properties is null.  ");
			return ;
		}
		
		        
		Session session = Session.getInstance(prop.getProperties());
		
        Message message = new MimeMessage(session);
        
		//设置发件人邮箱地址
        message.setFrom(new InternetAddress(senderAddr));
        
		//设置邮件回复人
        // msg.setReplyTo(new Address[]{new InternetAddress("123456@163.com")});
 
		//设置收件人地址(可以群发)
		String[] receiverStrAs = receiverStr.split(",");
		InternetAddress[] receiverAs = new InternetAddress[receiverStrAs.length];
		for(int i=0;i<receiverStrAs.length;i++){
			receiverAs[i]=new InternetAddress(receiverStrAs[i]);	
		}
		message.setRecipients(MimeMessage.RecipientType.TO, receiverAs);
        
		
		//整封邮件的MINE消息体
         MimeMultipart msgMultipart = new MimeMultipart("mixed");//混合的组合关系
         
		 //MINE消息体加入邮件
         message.setContent(msgMultipart);
		
		
		
          //正文内容
          MimeBodyPart content = new MimeBodyPart();	
		  
		  //把正文加入到 MINE消息体中
		  msgMultipart.addBodyPart(content);
		  
		  //正文（图片和文字部分）
         MimeMultipart bodyMultipart  = new MimeMultipart("related");
         //设置内容为正文
         content.setContent(bodyMultipart);		  
          //html代码部分
          MimeBodyPart htmlPart = new MimeBodyPart();
          //html中嵌套的图片部分
          //MimeBodyPart imgPart = new MimeBodyPart();
          
          //正文添加图片和html代码
          bodyMultipart.addBodyPart(htmlPart);
          //bodyMultipart.addBodyPart(imgPart);
          
         //把文件，添加到图片中
         //DataSource imgds = new FileDataSource(new File("C:/Users/H__D/Desktop/logo.png"));
         //DataHandler imgdh = new DataHandler(imgds );
         //imgPart.setDataHandler(imgdh);
         //说明html中的img标签的src，引用的是此图片
        //imgPart.setHeader("Content-Location", "http://sunteam.cc/logo.jsg");
         
         //html代码
         htmlPart.setContent("<span style='color:red'>"+value+"</span>","text/html;charset=utf-8");	
		 
		   
		for(String attachPath:filePahtList){
			//附件1
			MimeBodyPart attch1 = new MimeBodyPart();
			
			//把附件加入到 MINE消息体中
			msgMultipart.addBodyPart(attch1);
			
			//数据源,指向文件路径
			DataSource ds1 = new FileDataSource(new File(attachPath));
			//数据处理器
			DataHandler dh1 = new DataHandler(ds1);
			
			//将文件处理器设入附件中
			attch1.setDataHandler(dh1);
			//将文件名设入附件中
			attch1.setFileName(FileOperateUtil.getFileName(attachPath));
	  
		}
          
          //附件2
          //MimeBodyPart attch2 = new MimeBodyPart();
          	  
          //msgMultipart.addBodyPart(attch2);
        
		  
		   //把文件，添加到附件1中
		   
				  
   	    //生成文件邮件
         message.saveChanges();
		  
		  
 
		//设置邮件标题
        message.setSubject(title);
        
		//设置邮件内容
        //message.setText(value);
        
		
		
		
		
		//得到邮差对象
        Transport transport = session.getTransport();
        
		//连接自己的邮箱账户
        transport.connect(senderAddr,senderPasswd );      
		
		//发送
        transport.sendMessage(message, message.getAllRecipients());
		
		//结束
		for(int i=0;i<receiverStrAs.length;i++){	
			logger.info("send messaage to "+receiverStrAs[i]);			
		}
		
		
    }
	
	
	public static void SendWithAttachDir(String receiverStr,String title,String content,String dirPath) throws Exception{
        
		
		List<String> filePathList = new ArrayList<String>();
		
		//File directory = new File("export"+"/"+"20190818"); 
		//File directory = new File("export"); 
		File directory = new File(dirPath); 
		if(FileOperateUtil.checkPath(directory)!=2){
			throw new WEException(dirPath+" is not a directory");
		}
		
		String exportDirPath = directory.getAbsolutePath();
		
		List<String> fileNameList= FileOperateUtil.listFile(exportDirPath);
		for(String fileName1:fileNameList){
			String exportFileName = fileName1;
			String exportFileNamefullPath =FileOperateUtil.addPathSuffix(exportDirPath)+exportFileName;
			filePathList.add(exportFileNamefullPath);
		}
		
		
        SendMessages.SendWithAttach("macguan@163.com,guanxiaoyu@dwsoft.com.cn"
									,title
									,content
									,filePathList);
		
	}
	
	public static void main(String[] args) throws Exception {
        
		  //test1
          //SendMessages.sendOneMailWithoutAttach("test关晓雨","haha","macguan@163.com,guanxiaoyu@dwsoft.com.cn");
		  
		  //test2
		  String prjName = "贵州联通防骚扰";
		String mailType = "每日巡检";
		String reportName = "主机资源报表";
		
		String content = "见附件";
		
		String title = prjName+"-"+mailType+"-"+reportName;
		
		
        SendMessages.SendWithAttachDir("macguan@163.com,guanxiaoyu@dwsoft.com.cn"
									,title
									,content
									,"export");
		  
	}
}

