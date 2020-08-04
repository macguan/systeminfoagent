package cn.com.dwsoft.main;

import cn.com.dwsoft.util.jdbc.JdbcUtil;
import cn.com.dwsoft.util.systeminfo.SystemInfoUtil;
import cn.com.dwsoft.main.SystemMonitor;
import cn.com.dwsoft.main.ProcessMonitor;
import cn.com.dwsoft.main.TomcatMonitor;
import cn.com.dwsoft.main.UserMonitor;
import cn.com.dwsoft.main.util.ParameterCommonInstance;
import cn.com.dwsoft.main.util.DataCommonUtil;

import java.util.Date;
import net.sf.json.JSONArray;  
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

public class Bootstrap {
	
    public static void main(String[] args) throws Exception {
		
		ParameterCommonInstance paramInst = new ParameterCommonInstance();
		paramInst.getParamters(args);
		
		if(StringUtils.isNotBlank(paramInst.verify)
				&& paramInst.verify.equalsIgnoreCase("true")){
			System.out.println(DataCommonUtil.checkCurrentEnvirnment());
			System.exit(-1);
		}
		
		if(StringUtils.isNotBlank(paramInst.checkall)
				&& paramInst.checkall.equalsIgnoreCase("true")){
			SystemMonitor.dispCurrentInfo();	
		}
		
		if(StringUtils.isNotBlank(paramInst.checkonetime)
				&& paramInst.checkonetime.equalsIgnoreCase("true")){
					
			SystemMonitor.checkOneTime();
		}
		
		if(StringUtils.isNotBlank(paramInst.checkallprocessonguizhou)
				&& paramInst.checkallprocessonguizhou.equalsIgnoreCase("true")){
					
			ProcessMonitor.checkAllProcessOnGuiZhou(false);
		}
		if(StringUtils.isNotBlank(paramInst.checkonetimeprocessonguizhou)
				&& paramInst.checkonetimeprocessonguizhou.equalsIgnoreCase("true")){
					
			ProcessMonitor.checkAllProcessOnGuiZhou(true);
		}
		if(StringUtils.isNotBlank(paramInst.checkalltomcatonguizhou)
				&& paramInst.checkalltomcatonguizhou.equalsIgnoreCase("true")){
					
			TomcatMonitor.checkAllTomcatOnGuiZhou(false);
		}
		if(StringUtils.isNotBlank(paramInst.checkonetimetomcatonguizhou)
				&& paramInst.checkonetimetomcatonguizhou.equalsIgnoreCase("true")){
					
			TomcatMonitor.checkAllTomcatOnGuiZhou(true);
		}
		if(StringUtils.isNotBlank(paramInst.checkalluseronguizhou)
				&& paramInst.checkalluseronguizhou.equalsIgnoreCase("true")){
					
			UserMonitor.checkAllMachineOnGuiZhou(false);
		}
		if(StringUtils.isNotBlank(paramInst.checkonetimeuseronguizhou)
				&& paramInst.checkonetimeuseronguizhou.equalsIgnoreCase("true")){
					
			UserMonitor.checkAllMachineOnGuiZhou(true);
		}
		if(StringUtils.isNotBlank(paramInst.checktopprocess)
				&& paramInst.checktopprocess.equalsIgnoreCase("true")){
					
			ProcessMonitor.checkTopProcess();
		}
    }
}

