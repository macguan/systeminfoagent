package cn.com.dwsoft.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;  
import org.apache.commons.logging.LogFactory;

import net.sf.json.JSONArray;  
import net.sf.json.JSONObject;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

import cn.com.dwsoft.util.jdbc.JdbcUtil;
import cn.com.dwsoft.util.linux.OpensshBasicUtil;
import cn.com.dwsoft.main.entity.ProcessEntity;
import cn.com.dwsoft.main.SendMessages;

import cn.com.dwsoft.util.file.PropertiesParser;
import cn.com.security.ss.exception.WEException;


public class ProcessMonitor
{
	
	private static Log logger = LogFactory.getLog(ProcessMonitor.class);
	
	
	public static String dateToString(Date date){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");	
		return sdf.format(date);
	}
	
	public static void checkOneProcessOnGuiZhou(JSONObject jsonObject,Boolean isInsertDB) 
	{
		
		
		int retVal = OpensshBasicUtil.checkProcessOnSsh(jsonObject);

		if(isInsertDB){
			
			String[] sqls_exception = null;
			
			String sql = "insert into guan_systemProcesslog(" +
		             "serialid,"+
					 "checkdate,"+
					 "serverip,"+
					 "tomcatname,"+
					 "catalinahome,"+
					 "processnum)"+
					 " values("+
					 "'"+JdbcUtil.getUUId()+"'"+
					 ","+"'"+jsonObject.get("checkdate")+"'"+
					 ","+"'"+jsonObject.get("hostname")+"'"+
					 ","+"'"+jsonObject.get("processname")+"'"+
					 ","+"'"+jsonObject.get("keystring")+"'"+
					 ","+retVal+
					 ")";
			logger.debug(sql);
		
			String[] sqls = {sql};
			sqls_exception = sqls;
			try{
				JdbcUtil.executeSql("jdbc.properties",sqls);
			}catch (Exception e) {  
	 			logger.info(e.getMessage()); 
				if(sqls_exception.length>0){
					System.out.println(sqls_exception[0]);
				}
	 		}
		
		
			
		}else {
			System.out.println("---------------------------------------------------");
			
			System.out.println(jsonObject.get("hostname")+"|"+jsonObject.get("processname")+"|"+retVal);
		
		}
	}
	
	public static void checkAllProcessOnGuiZhou(Boolean isInsertDB) 
	{
		
		logger.info("\n Now check process info...");
		long startTime=System.currentTimeMillis(); 
		
		List<ProcessEntity> processObjectList = new ArrayList<ProcessEntity>(); 
		
		ProcessEntity process = null;
		
		
		process = new ProcessEntity("192.168.10.190","hadoop","Haaddcc123"
								,"NameNode,JournalNode,DFSZKFailoverController,ResourceManager,JobHistoryServer,HRegionServer,QuorumPeerMain","jps","NameNode||JournalNode||DFSZKFailoverController||ResourceManager||JobHistoryServer||HRegionServer||QuorumPeerMain"
								,"MULTILINE_MULTIMATCH");
		processObjectList.add(process);
		
		process = new ProcessEntity("192.168.10.190","machinemonitor","Dwsoft123"
								,"HiveMetaStore","ps -ef | grep hive","org.apache.hadoop.hive.metastore.HiveMetaStore"
								,"SINGLELINE_SINGLEMATCH");
		processObjectList.add(process);
		
		process = new ProcessEntity("192.168.10.190","machinemonitor","Dwsoft123"
								,"HiveServer2","ps -ef | grep hive","org.apache.hive.service.server.HiveServer2"
								,"SINGLELINE_SINGLEMATCH");
		processObjectList.add(process);	
		
		process = new ProcessEntity("192.168.10.190","machinemonitor","Dwsoft123"
								,"hbase-daemon","ps -ef | grep hbase","hbase-daemon"
								,"SINGLELINE_SINGLEMATCH");
		processObjectList.add(process);
		
		process = new ProcessEntity("192.168.10.190","machinemonitor","Dwsoft123"
								,"redis","ps -ef | grep redis","/usr/local/bin/redis-server *:6379"
								,"SINGLELINE_SINGLEMATCH");
		processObjectList.add(process);
		
		
		
		process = new ProcessEntity("192.168.10.191","hadoop","Haaddcc123"
								,"DataNode,JournalNode,DFSZKFailoverController,NodeManager,HMaster,QuorumPeerMain","jps","DataNode||JournalNode||DFSZKFailoverController||NodeManager||HMaster||QuorumPeerMain"
								,"MULTILINE_MULTIMATCH");
		processObjectList.add(process);
		
		process = new ProcessEntity("192.168.10.191","machinemonitor","Dwsoft123"
								,"HiveServer2","ps -ef | grep hive","org.apache.hive.service.server.HiveServer2"
								,"SINGLELINE_SINGLEMATCH");
		processObjectList.add(process);	
		
		process = new ProcessEntity("192.168.10.191","machinemonitor","Dwsoft123"
								,"hbase-daemon","ps -ef | grep hbase","hbase-daemon"
								,"SINGLELINE_SINGLEMATCH");
		processObjectList.add(process);
		
		process = new ProcessEntity("192.168.10.191","machinemonitor","Dwsoft123"
								,"tomcat8_guan","ps -ef | grep tomcat8","Dcatalina.home=/home/gxfas/localdata/software/server/tomcat8_guan"
								,"SINGLELINE_SINGLEMATCH");
		processObjectList.add(process);
		
		process = new ProcessEntity("192.168.10.192","hadoop","Haaddcc123"
								,"DataNode,JournalNode,NodeManager,HRegionServer,QuorumPeerMain","jps","DataNode||JournalNode||NodeManager||HRegionServer||QuorumPeerMain"
								,"MULTILINE_MULTIMATCH");
		processObjectList.add(process);
		
		process = new ProcessEntity("192.168.10.192","machinemonitor","Dwsoft123"
								,"hbase-daemon","ps -ef | grep hbase","hbase-daemon"
								,"SINGLELINE_SINGLEMATCH");
		processObjectList.add(process);
		
		process = new ProcessEntity("192.168.10.193","hadoop","Haaddcc123"
								,"DataNode,NodeManager,HRegionServer","jps","DataNode||NodeManager||HRegionServer"
								,"MULTILINE_MULTIMATCH");
		processObjectList.add(process);
		
		process = new ProcessEntity("192.168.10.193","machinemonitor","Dwsoft123"
								,"hbase-daemon","ps -ef | grep hbase","hbase-daemon"
								,"SINGLELINE_SINGLEMATCH");
		processObjectList.add(process);
		
		
		process = new ProcessEntity("192.168.10.207","machinemonitor","Dwsoft123"
								,"vpn server","ps -ef | grep l2tpd","/usr/sbin/xl2tpd"
								,"SINGLELINE_SINGLEMATCH");
		process.sshport=60028;		 				
		processObjectList.add(process);
		
		process = new ProcessEntity("192.168.10.207","machinemonitor","Dwsoft123"
								,"nginx_master","ps -ef | grep nginx","nginx: master process"
								,"SINGLELINE_SINGLEMATCH");
		process.sshport=60028;
		processObjectList.add(process);
		
		process = new ProcessEntity("192.168.10.207","machinemonitor","Dwsoft123"
								,"nginx_worker","ps -ef | grep nginx","nginx: worker process"
								,"SINGLELINE_SINGLEMATCH");
		process.sshport=60028;
		processObjectList.add(process);
		
		process = new ProcessEntity("192.168.10.207","machinemonitor","Dwsoft123"
								,"nginx_php_module","ps -ef | grep nginx","php-fpm: pool www"
								,"SINGLELINE_SINGLEMATCH");
		process.sshport=60028;
		processObjectList.add(process);
		
		process = new ProcessEntity("192.168.10.207","machinemonitor","Dwsoft123"
								,"mysql","ps -ef | grep mysqld","/usr/sbin/mysqld --basedir=/usr"
								,"SINGLELINE_SINGLEMATCH");
		process.sshport=60028;
		processObjectList.add(process);
		
		process = new ProcessEntity("192.168.10.188","machinemonitor","Dwsoft123"
								,"tomcat","ps -ef | grep tomcat","org.apache.catalina.startup.Bootstrap"
								,"SINGLELINE_SINGLEMATCH");
		processObjectList.add(process);
		
		process = new ProcessEntity("192.168.10.188","machinemonitor","Dwsoft123"
								,"nginx_master","ps -ef | grep nginx","nginx: master process"
								,"SINGLELINE_SINGLEMATCH");
		processObjectList.add(process);
		
		process = new ProcessEntity("192.168.10.188","machinemonitor","Dwsoft123"
								,"nginx_worker","ps -ef | grep nginx","nginx: worker process"
								,"SINGLELINE_SINGLEMATCH");
		processObjectList.add(process);
		
		
		process = new ProcessEntity("192.168.10.188","machinemonitor","Dwsoft123"
								,"oracle","ps -ef | grep oracle","oracleorcl"
								,"SINGLELINE_SINGLEMATCH");
		processObjectList.add(process);
		
		process = new ProcessEntity("192.168.10.188","machinemonitor","Dwsoft123"
								,"mysql","ps -ef | grep mysqld","/usr/libexec/mysqld --basedir=/usr"
								,"SINGLELINE_SINGLEMATCH");
		processObjectList.add(process);
		
		process = new ProcessEntity("192.168.10.188","machinemonitor","Dwsoft123"
								,"sftp root@123.56.230.134","ps -ef | grep sftp","sftp root@123.56.230.134"
								,"SINGLELINE_SINGLEMATCH");
		processObjectList.add(process);
		
		process = new ProcessEntity("192.168.10.188","machinemonitor","Dwsoft123"
								,"apache httpd","ps -ef | grep httpd","/usr/sbin/httpd"
								,"SINGLELINE_SINGLEMATCH");
		processObjectList.add(process);
		
		process = new ProcessEntity("192.168.10.188","machinemonitor","Dwsoft123"
								,"pure-ftpd","ps -ef | grep ftpd","pure-ftpd (SERVER)"
								,"SINGLELINE_SINGLEMATCH");
		processObjectList.add(process);
		
		process = new ProcessEntity("192.168.10.188","machinemonitor","Dwsoft123"
								,"vsftpd","ps -ef | grep vsftpd","/usr/sbin/vsftpd"
								,"SINGLELINE_SINGLEMATCH");
		processObjectList.add(process);
		
		process = new ProcessEntity("192.168.10.188","machinemonitor","Dwsoft123"
								,"redis_6381","ps -ef | grep redis","redis-server 192.168.10.188:6381"
								,"SINGLELINE_SINGLEMATCH");
		processObjectList.add(process);
		
		process = new ProcessEntity("192.168.10.188","machinemonitor","Dwsoft123"
								,"redis_6380","ps -ef | grep redis","redis-server 127.0.0.1:6380"
								,"SINGLELINE_SINGLEMATCH");
		processObjectList.add(process);
		
		process = new ProcessEntity("192.168.10.188","machinemonitor","Dwsoft123"
								,"redis_6379","ps -ef | grep redis","redis-server 127.0.0.1:6379"
								,"SINGLELINE_SINGLEMATCH");
		processObjectList.add(process);
		
		process = new ProcessEntity("192.168.10.188","machinemonitor","Dwsoft123"
								,"weblogic_etl","ps -ef | grep weblogic","-Dweblogic.home=/home/etl/weblogic_web/wlserver/server"
								,"SINGLELINE_SINGLEMATCH");
		processObjectList.add(process);
		
		process = new ProcessEntity("192.168.10.188","machinemonitor","Dwsoft123"
								,"weblogic_xiweisong","ps -ef | grep weblogic","Dweblogic.home=/home/xiweisong/oracle/Middleware/wlserver/server"
								,"SINGLELINE_SINGLEMATCH");
		processObjectList.add(process);
		
		process = new ProcessEntity("192.168.10.188","machinemonitor","Dwsoft123"
								,"weblogic12c_server","ps -ef | grep weblogic","-Dweblogic.home=/home/testenv/weblogic12c/oracle/Middleware/wlserver/server"
								,"SINGLELINE_SINGLEMATCH");
		processObjectList.add(process);
		
		process = new ProcessEntity("192.168.10.188","machinemonitor","Dwsoft123"
								,"weblogic12c_derby","ps -ef | grep weblogic","org.apache.derby.drda.NetworkServerControl"
								,"SINGLELINE_SINGLEMATCH");
		processObjectList.add(process);
		
		process = new ProcessEntity("192.168.10.188","machinemonitor","Dwsoft123"
								,"jsvc","ps -ef | grep jsvc","jsvc.exec"
								,"SINGLELINE_SINGLEMATCH");
		processObjectList.add(process);
		
		

		//The last propertie named keystring can be split by "|". 
		//For excample 
		//1521|LISTEN
		//Which means: to find 1521 and LISTEN in one line.
		/*
		process = new ProcessEntity("192.168.10.132","dwsoft","Dwsoft123"
								,"oracle_132","netstat -an | grep LISTEN | grep -v LISTENING | grep 1521","1521|LISTEN");
		processObjectList.add(process);
		*/
		
				
		String checkdate = dateToString(new Date());
		
		logger.info("\n checkdate:"+checkdate);
		
		for(ProcessEntity to1:processObjectList){
			
			JSONObject jsonObject = new JSONObject();
			
			jsonObject.put("hostname", to1.servername);
			if(to1.sshport != null) 
				jsonObject.put("sshport", to1.sshport);
			jsonObject.put("username", to1.username);
			jsonObject.put("password", to1.password);
			
			jsonObject.put("cmd",to1.cmd);
			jsonObject.put("expectoutput",to1.keystring);
			jsonObject.put("matchmode",to1.matchmode);
			
			// The following lines is for inserting into db
			jsonObject.put("checkdate", checkdate);
			jsonObject.put("processname", to1.processname);
			jsonObject.put("keystring", to1.keystring);
			
			
			checkOneProcessOnGuiZhou(jsonObject,isInsertDB);	
			
		}
		
		logger.info("\n check success!");
		long endTime=System.currentTimeMillis();
		logger.info("\n time consume:"+(endTime-startTime)+"ms");
			
					
	}
	
	
	public static void checkTopProcess() 
	{
		
		logger.info("\n Now check top process info...");
		long startTime=System.currentTimeMillis(); 
		
		List<ProcessEntity> processObjectList = new ArrayList<ProcessEntity>(); 
		
		ProcessEntity process = null;
		
		
		process = new ProcessEntity("192.168.10.190","hadoop","Haaddcc123"
								,"NameNode,JournalNode,DFSZKFailoverController,ResourceManager,JobHistoryServer,HRegionServer,QuorumPeerMain","jps","NameNode||JournalNode||DFSZKFailoverController||ResourceManager||JobHistoryServer||HRegionServer||QuorumPeerMain"
								,"MULTILINE_MULTIMATCH");
		processObjectList.add(process);
						
		
		process = new ProcessEntity("192.168.10.191","hadoop","Haaddcc123"
								,"DataNode,JournalNode,DFSZKFailoverController,NodeManager,HMaster,QuorumPeerMain","jps","DataNode||JournalNode||DFSZKFailoverController||NodeManager||HMaster||QuorumPeerMain"
								,"MULTILINE_MULTIMATCH");
		processObjectList.add(process);
		
		
		process = new ProcessEntity("192.168.10.192","hadoop","Haaddcc123"
								,"DataNode,JournalNode,NodeManager,HRegionServer,QuorumPeerMain","jps","DataNode||JournalNode||NodeManager||HRegionServer||QuorumPeerMain"
								,"MULTILINE_MULTIMATCH");
		processObjectList.add(process);
		
		
		process = new ProcessEntity("192.168.10.193","hadoop","Haaddcc123"
								,"DataNode,NodeManager,HRegionServer","jps","DataNode||NodeManager||HRegionServer"
								,"MULTILINE_MULTIMATCH");
		processObjectList.add(process);
		
				
		process = new ProcessEntity("192.168.10.207","machinemonitor","Dwsoft123"
								,"vpn server","ps -ef | grep l2tpd","/usr/sbin/xl2tpd"
								,"SINGLELINE_SINGLEMATCH");
		process.sshport=60028;		 				
		processObjectList.add(process);
		
		
		process = new ProcessEntity("192.168.10.188","machinemonitor","Dwsoft123"
								,"oracle","ps -ef | grep oracle","oracleorcl"
								,"SINGLELINE_SINGLEMATCH");
		processObjectList.add(process);
		
				
		String checkdate = dateToString(new Date());
		
		logger.info("\n checkdate:"+checkdate);
		List<String> foundErrorLines = new ArrayList<String>();
		
		for(ProcessEntity to1:processObjectList){
			
			JSONObject jsonObject = new JSONObject();
			
			jsonObject.put("hostname", to1.servername);
			if(to1.sshport != null) 
				jsonObject.put("sshport", to1.sshport);
			jsonObject.put("username", to1.username);
			jsonObject.put("password", to1.password);
			
			jsonObject.put("cmd",to1.cmd);
			jsonObject.put("expectoutput",to1.keystring);
			jsonObject.put("matchmode",to1.matchmode);
			
			// The following lines is for inserting into db
			jsonObject.put("checkdate", checkdate);
			jsonObject.put("processname", to1.processname);
			jsonObject.put("keystring", to1.keystring);
			
			
			//checkOneProcessOnGuiZhou(jsonObject,isInsertDB);
			int retVal = OpensshBasicUtil.checkProcessOnSsh(jsonObject);
			if(retVal == 0){
				logger.info("Found error"+"|"+jsonObject.get("hostname")+"|"+jsonObject.get("processname")+"|"+retVal);
				
				foundErrorLines.add("\n"+"ip:"+jsonObject.get("hostname")+"|"+"process:"+jsonObject.get("processname")+"|"+"process_num:"+retVal);
			}
			
			
		}
		
		if(foundErrorLines.size()>0){
			String title = "prj:道隆公司内网"+"|"+"checkdate:"+checkdate+"|"+"Found "+foundErrorLines.size()+" errors!";
			
			String content = "";
			for(String line:foundErrorLines){
				content += line;
			}
			
			try {
				PropertiesParser prop = new PropertiesParser("mail.properties");
				String receiverStr = null;
				if(prop!=null){
					receiverStr = prop.getInfoFromConfiguration("maintain_receiverStr");
				}else {
					throw new WEException(" The mail.properties is null.  ");
					
				}
		
				SendMessages.sendOneMailWithoutAttach(receiverStr,title,content);
			}catch(Exception e){
				logger.info(e.getMessage()); 
			}
			
		}
		
				
		logger.info("\n check success!");
		long endTime=System.currentTimeMillis();
		logger.info("\n time consume:"+(endTime-startTime)+"ms");
			
					
	}
	
}
