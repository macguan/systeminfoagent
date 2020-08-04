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

public class UserMonitor
{
	
	private static Log logger = LogFactory.getLog(UserMonitor.class);
	
	
	public static String dateToString(Date date){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");	
		return sdf.format(date);
	}
	
	public static void checkOneMachineOnGuiZhou(JSONObject jsonObject,Boolean isInsertDB) 
	{
		
		
		List<JSONObject> retList =OpensshBasicUtil.checkUserOnSsh(jsonObject);

		if(isInsertDB){
			
			String nameList = "";
			for(JSONObject obj1:retList){
				
				nameList += obj1.get("username")+",";
				//System.out.println("uesr:"+obj1.get("username"));
				//System.out.println("home:"+obj1.get("homedir"));
				//System.out.println("bash:"+obj1.get("bash"));
			}
			if(nameList.contains(",")){
				nameList = nameList.substring(0,nameList.length()-1);
				
			}
			
			String[] sqls_exception = null;
			
			String sql = "insert into guan_systemuserlog(" +
		             "serialid,"+
					 "checkdate,"+
					 "serverip,"+
					 "username"+
					 ")"+
					 " values("+
					 "'"+JdbcUtil.getUUId()+"'"+
					 ","+"'"+jsonObject.get("checkdate")+"'"+
					 ","+"'"+jsonObject.get("hostname")+"'"+
					 ","+"'"+nameList+"'"+
					 ")";
			logger.debug(sql);
			//System.out.println(sql);
			
			
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
			
			String nameList = "";
			for(JSONObject obj1:retList){
				
				nameList += obj1.get("username")+",";
				//System.out.println("uesr:"+obj1.get("username"));
				//System.out.println("home:"+obj1.get("homedir"));
				//System.out.println("bash:"+obj1.get("bash"));
			}
			if(nameList.contains(",")){
				nameList = nameList.substring(0,nameList.length()-1);
				
			}
			System.out.println(jsonObject.get("hostname")+":"+nameList);
		
		}
	}
	
	public static void checkAllMachineOnGuiZhou(Boolean isInsertDB) 
	{
		
		logger.info("\n Now check user info...");
		long startTime=System.currentTimeMillis(); 
		
		List<ProcessEntity> machineObjectList = new ArrayList<ProcessEntity>(); 
		
		ProcessEntity machine = null;
		
		
		machine = new ProcessEntity("192.168.10.188","machinemonitor","Dwsoft123"
								,"","cat /etc/passwd | grep -v nologin",""
								,"");
		machineObjectList.add(machine);
		
		machine = new ProcessEntity("192.168.10.190","machinemonitor","Dwsoft123"
								,"","cat /etc/passwd | grep -v nologin",""
								,"");
		machineObjectList.add(machine);
		
		machine = new ProcessEntity("192.168.10.191","machinemonitor","Dwsoft123"
								,"","cat /etc/passwd | grep -v nologin",""
								,"");
		machineObjectList.add(machine);
		
		machine = new ProcessEntity("192.168.10.192","machinemonitor","Dwsoft123"
								,"","cat /etc/passwd | grep -v nologin",""
								,"");
		machineObjectList.add(machine);
		
		machine = new ProcessEntity("192.168.10.193","machinemonitor","Dwsoft123"
								,"","cat /etc/passwd | grep -v nologin",""
								,"");
		machineObjectList.add(machine);
		
		machine = new ProcessEntity("192.168.10.207","machinemonitor","Dwsoft123"
								,"","cat /etc/passwd | grep -v nologin",""
								,"");
		machine.sshport=60028;
		machineObjectList.add(machine);
				
		String checkdate = dateToString(new Date());
		
		logger.info("\n checkdate:"+checkdate);
		
		for(ProcessEntity machine1:machineObjectList){
			
			JSONObject jsonObject = new JSONObject();
			
			jsonObject.put("hostname", machine1.servername);
			if(machine1.sshport != null) 
				jsonObject.put("sshport", machine1.sshport);
			jsonObject.put("username", machine1.username);
			jsonObject.put("password", machine1.password);
			
			jsonObject.put("cmd",machine1.cmd);
			jsonObject.put("expectoutput",machine1.keystring);
			jsonObject.put("matchmode",machine1.matchmode);
			
			// The following lines is for inserting into db
			jsonObject.put("checkdate", checkdate);
				
			
			checkOneMachineOnGuiZhou(jsonObject,isInsertDB);	
			
		}
		
		logger.info("\n check success!");
		long endTime=System.currentTimeMillis();
		logger.info("\n time consume:"+(endTime-startTime)+"ms");
			
					
	}
	
	
}
