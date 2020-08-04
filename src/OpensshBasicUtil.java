package cn.com.dwsoft.util.linux;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;  
import org.apache.commons.logging.LogFactory;

import net.sf.json.JSONArray;  
import net.sf.json.JSONObject;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

public class OpensshBasicUtil
{
	private static Log logger = LogFactory.getLog(OpensshBasicUtil.class);
	
	public static List<String> goConnectCmd(JSONObject jsonObject) throws Exception
	{
		
		String hostname = (String)jsonObject.get("hostname");
		Integer sshport = (Integer)jsonObject.get("sshport");
		String username = (String)jsonObject.get("username");
		String password = (String)jsonObject.get("password");
		String cmd = (String)jsonObject.get("cmd");
		
		Connection conn = null;
		Session sess = null;
		List<String> cmdOutputStrList = new ArrayList<String>();
		try
		{
			/* Create a connection instance */
			if(sshport==null){
				//System.out.println("sshport is null");
				conn = new Connection(hostname);
			}else{ 
				//System.out.println("sshport is not null:"+sshport);
				conn = new Connection(hostname,sshport);
			}
			
			
			/* Now connect */

			conn.connect();

			/* Authenticate.
			 * If you get an IOException saying something like
			 * "Authentication method password not supported by the server at this stage."
			 * then please check the FAQ.
			 */

			boolean isAuthenticated = conn.authenticateWithPassword(username, password);

			if (isAuthenticated == false)
				throw new IOException("Authentication failed.");

			/* Create a session */

			sess = conn.openSession();

			//sess.execCommand("uname -a && date && uptime && who");
			sess.execCommand(cmd);
			
			
			/* 
			 * This basic example does not handle stderr, which is sometimes dangerous
			 * (please read the FAQ).
			 */

			InputStream stdout = new StreamGobbler(sess.getStdout());

			BufferedReader br = new BufferedReader(new InputStreamReader(stdout));

			while (true)
			{
				String line = br.readLine();
				if (line == null)
					break;
				//System.out.println(line);
				cmdOutputStrList.add(line);
			}
			
			
			if(cmdOutputStrList.size()==0){
				logger.debug("命令输出为空[There is nothing in process output].");
				//cmdOutputStrList.add("cmd process failed! exitValue = "+sess.getExitStatus());
			}

			/* Show exit status, if available (otherwise "null") */
			logger.debug("ExitCode: " + sess.getExitStatus());

			return cmdOutputStrList;

		}
		catch (Exception e)
		{
			e.printStackTrace(System.err);
			throw e;
		}finally{
		
			
			if(sess !=null){
				/* Close this session */
				sess.close();

				/* Close the connection */

				conn.close();
			}else {
				if(conn !=null){
					/* Close the connection */

					conn.close();
				}
			}
			
		}
	}
	
	
	public static int checkProcessOnSsh(JSONObject jsonObject) 
	{
		
		
		int foundNum = 0;
		Boolean foundKeys = false;
		
		try
		{
			List<String> cmdOutputStrList = goConnectCmd(jsonObject);
				
			if(cmdOutputStrList.size()!=0){
				
				String expectoutput = (String)jsonObject.get("expectoutput");
				String matchmode = (String)jsonObject.get("matchmode");
				
				if(matchmode.equals("SINGLELINE_SINGLEMATCH")){
					foundKeys = false;
					for(String line:cmdOutputStrList){
						
						if(line.contains(expectoutput)){
								foundKeys = true;
								foundNum++;
						}
						
						
						//logger.info(line);
					}
				}else if(matchmode.equals("SINGLELINE_MULTIMATCH")){
				
					String[] expectoutput_as = expectoutput.split("\\|");
					
					for(String line:cmdOutputStrList){
						
						foundKeys = true;
						for(String matchkey:expectoutput_as){
							//System.out.println("!!!!!!!!!!!!!!to find :"+matchkey);
							if(line.contains(matchkey)){
								
							}else{
								foundKeys = false;
							}
						}
						if(foundKeys){
							foundNum++;
						}
						
						//logger.info(line);
					}
				
				}else if(matchmode.equals("MULTILINE_MULTIMATCH")){

					String[] expectoutput_as = expectoutput.split("\\|\\|");
					
					List<String> keyList = new ArrayList<String>();
					List<String> keyList_bak = new ArrayList<String>();
					for(String key1:expectoutput_as){
						keyList.add(key1);
					}
					keyList_bak.addAll(keyList);
					//System.out.println(keyList.size());
					//System.out.println(keyList);
					
					foundKeys = true;
					for(String line:cmdOutputStrList){
						
						
						for(String matchkey:keyList){
							//System.out.println("!!!!!!!!!!!!!!to find :"+matchkey);
							if(line.contains(matchkey)){
								if(keyList_bak.contains(matchkey))
									keyList_bak.remove(matchkey);
							}
						}

					}
					
					if(keyList_bak.size()>0){
						foundNum=0;
					}else {
						foundNum=1;
					}
				
				}
				
				
			}
		}catch (Exception e)
		{
			e.printStackTrace(System.err);
			
		}	
		
		
		return foundNum;
			
					
	}
	
	
	public static List<JSONObject> checkUserOnSsh(JSONObject param1) 
	{
		List<JSONObject> userList = new ArrayList<JSONObject>();

		try
		{
			param1.put("cmd","cat /etc/passwd | grep -v nologin | grep -v root| grep -v sync| grep -v shutdown| grep -v halt");
			List<String> cmdOutputStrList = goConnectCmd(param1);
				
			if(cmdOutputStrList.size()!=0){
				
				for(String line:cmdOutputStrList){
					JSONObject jsonObject = getUserFromLine(line);
		
					if(jsonObject!=null){
						userList.add(jsonObject);
					}
					
				} // end of for
				
				
			} // end of if
		}catch (Exception e)
		{
			e.printStackTrace(System.err);
			
		}	
		
		
		return userList;
			
					
	}
	
	
	public static JSONObject getUserFromLine(String line) 
	{
		JSONObject jsonObject = null;
		//String line = "maolijun:x:1037:1037::/home/maolijun:/bin/bash";
		
		if(line!=null){
			if(line.contains(":")){
				String[] splitTeam = line.split(":");
				
				if(splitTeam.length==7){
					
					jsonObject = new JSONObject();
					
					jsonObject.put("username",splitTeam[0]);
					jsonObject.put("homedir",splitTeam[5]);
					jsonObject.put("bash",splitTeam[6]);
					
				}
				
			}
		}
		
		return jsonObject;
		
		
	}
	public static void main(String[] args) throws Exception {
		
		/*
		String as = "maolijun:x:1037:1037::/home/maolijun:/bin/bash";
		
		JSONObject jsonObject = getUserFromLine(as);
		
		if(jsonObject!=null){
			System.out.println(jsonObject.get("username"));
			System.out.println(jsonObject.get("homedir"));
			System.out.println(jsonObject.get("bash"));
		}
		*/
		
		JSONObject jsonObject = new JSONObject();
			
		jsonObject.put("hostname", "192.168.10.188");
		jsonObject.put("username", "machinemonitor");
		jsonObject.put("password", "Dwsoft123");
			
		List<JSONObject> retList = checkUserOnSsh(jsonObject);
		for(JSONObject obj1:retList){
			System.out.println("uesr:"+obj1.get("username"));
			System.out.println("home:"+obj1.get("homedir"));
			System.out.println("bash:"+obj1.get("bash"));
		}
		
	}
	
}
