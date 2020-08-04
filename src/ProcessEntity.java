package cn.com.dwsoft.main.entity;

public class ProcessEntity{
		public String servername;
		public Integer sshport = null;
		public String username;
		public String password;
		
		public String processname;
		public String cmd;
		public String keystring;
		public String matchmode="SINGLELINE_SINGLEMATCH";
		
		 public ProcessEntity(String servername, String username,String password,String processname,String cmd,String keystring,String matchmode) {      
			   this.servername = servername;
			   this.username = username; 
			   this.password = password;
			   
			   this.processname = processname; 	
			   this.cmd = cmd;			   
			   this.keystring = keystring;
			   this.matchmode = matchmode;
			}
   
	}