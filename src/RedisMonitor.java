package cn.com.dwsoft.main;

import redis.clients.jedis.Jedis;
//import redis.clients.jedis.Connection;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray; 
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

import org.apache.commons.logging.Log;  
import org.apache.commons.logging.LogFactory;

import java.util.Date;
import java.text.SimpleDateFormat;

import cn.com.dwsoft.util.jdbc.JdbcUtil;


public class RedisMonitor {
 
	private static Log logger = LogFactory.getLog(RedisMonitor.class);
 
 	public static String dateToString(Date date){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");	
		return sdf.format(date);
	}
	
	public static String dropStringTail(String s){
		if(StringUtils.isNotBlank(s)){
			return s.substring(0,s.length()-1);	
		}else return ""; 
	}
	
	public static String getRedisInfo(String serverip,int port) throws Exception{
		
        Jedis jedis = null;	
		String infoStrs = null;
		
		try{
			//	jedis = new Jedis("192.168.10.133", 6379);
			//	jedis = new Jedis("192.168.10.190", 6379);
			
			 //jedis.set("hello","javaRedis");
			 //String hello = jedis.get("hello");
			 //System.out.println(hello);
			
			jedis = new Jedis(serverip, port);
			
			jedis.auth("admin123");
			//System.out.println(jedis.ping());
			
			infoStrs = jedis.info();
			
			
			
		 
		}catch (Exception e) {  
			logger.info(e.getMessage());  
		}finally{
			if(jedis != null){
				jedis.close();
			}
		}
		 
        return infoStrs; 
	
		 
     }
	
	public static String getRedisInfoVal(String infoStr,String keyStr) throws Exception{
		
		if(infoStr == null) return "";
		
		String[] infoList1 = infoStr.split("\\\n");
		for(String line:infoList1){
			if(line.contains(keyStr)){
				String[] pairs = line.split(":");
				if(pairs.length==2){
					return pairs[1].trim();
				}else {
					return "";
				}
				
			}
		}
		
		return "";
	}
    
	public static void checkRedisOnGuiZhou(String redisname,String serverip,int port,String checkdate,Boolean isInsertDB) 
	{
		logger.info("\n Now check redis info...");
		long startTime=System.currentTimeMillis(); 
		
		//String checkdate = dateToString(new Date());
		
		JSONObject jsonObject = null;
		try{
			
			String infoStrs = getRedisInfo(serverip,port);
		
			jsonObject = new JSONObject();
					
			jsonObject.put("tomcatname", redisname);
			jsonObject.put("ipaddress", serverip);
			jsonObject.put("listen_port", port+"");
		
			// The following lines is for inserting into db
			jsonObject.put("checkdate", checkdate);
			
			jsonObject.put("timespan", getRedisInfoVal(infoStrs,"uptime_in_days"));
			
			jsonObject.put("maxMemory", getRedisInfoVal(infoStrs,"maxmemory_human"));
			jsonObject.put("usedMemory", getRedisInfoVal(infoStrs,"used_memory_rss_human"));
			jsonObject.put("memUsage_basedoncommit", getRedisInfoVal(infoStrs,"instantaneous_ops_per_sec"));
			
			jsonObject.put("currentThreadCount", getRedisInfoVal(infoStrs,"keyspace_hits"));
			jsonObject.put("currentThreadsBusy", getRedisInfoVal(infoStrs,"keyspace_misses"));
								 
		
			checkOneRedisOnGuiZhou(jsonObject,isInsertDB);
		
		}catch (Exception e){
			e.printStackTrace(System.err);
			jsonObject = null;
			
		}		
		
		logger.info("\n check success!");
		long endTime=System.currentTimeMillis();
		logger.info("\n time consume:"+(endTime-startTime)+"ms");	
	}

	public static void checkOneRedisOnGuiZhou(JSONObject jsonObject,Boolean isInsertDB) 
	{
		

		if(isInsertDB){
			
			String sql = "insert into guan_tomcatProcesslog(" +
		             "serialid,"+
					 "checkdate,"+
					 
					 "serverip,"+
					 "tomcatname,"+
					 "listen_port,"+
					 
					 "timespan,"+
					 
					 "maxMemory,"+
					 "usedMemory,"+
					 "memUsage_basedoncommit,"+
					 
					 "currentThreadCount,"+
					 "currentThreadsBusy)"+
					 
					 " values("+
					 
					 "'"+JdbcUtil.getUUId()+"'"+
					 ","+"'"+jsonObject.get("checkdate")+"'"+
					 
					 ","+"'"+jsonObject.get("ipaddress")+"'"+
					 ","+"'"+jsonObject.get("tomcatname")+"'"+
					 ","+"'"+jsonObject.get("listen_port")+"'"+

					 ","+"'"+jsonObject.get("timespan")+"'"+
					 
					 ","+dropStringTail(jsonObject.get("maxMemory")+"")+
					 ","+dropStringTail(jsonObject.get("usedMemory")+"")+
					 ","+jsonObject.get("memUsage_basedoncommit")+
					 
					 ","+jsonObject.get("currentThreadCount")+
					 ","+jsonObject.get("currentThreadsBusy")+

					 ")";
			logger.debug(sql);
			//System.out.println(sql);
		
			String[] sqls = {sql};
			try{
				JdbcUtil.executeSql("jdbc.properties",sqls);
			}catch (Exception e) {  
	 			logger.info(e.getMessage());  
	 		}
			
			
		
		
			
		}else {
		System.out.println("---------------------------------------------------");
		System.out.println("采集时间:" + jsonObject.get("checkdate"));  
		System.out.println("ipaddress:" + jsonObject.get("ipaddress"));  
		System.out.println("redisname:" + jsonObject.get("tomcatname"));    
		System.out.println("redis连续工作时间:" + jsonObject.get("timespan"));
		
		System.out.println("maxmemory_human:"+dropStringTail(jsonObject.get("maxMemory")+""));
		System.out.println("used_memory_rss_human:"+dropStringTail(jsonObject.get("usedMemory")+""));
		System.out.println("instantaneous_ops_per_sec:"+jsonObject.get("memUsage_basedoncommit"));
		
  
		System.out.println("keyspace_hits:" + jsonObject.get("currentThreadCount"));  
		System.out.println("keyspace_misses:" + jsonObject.get("currentThreadsBusy"));
		
		}
	}
	
	public static void main(String[] args) throws Exception{
		
        String checkdate = dateToString(new Date());
		
		//checkRedisOnGuiZhou("redis","192.168.10.190", 6379,checkdate,false);
		checkRedisOnGuiZhou("redis_133","192.168.10.133", 6379,checkdate,true);
		
		
		
     }
 
 }