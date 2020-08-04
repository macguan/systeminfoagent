package cn.com.dwsoft.main.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.com.security.ss.exception.WEException;
import cn.com.chinet.common.dao.CommonJdbcDaoSupport;
import cn.com.chinet.common.utils.file.FileOperateUtil;
import cn.com.chinet.common.utils.file.PropertiesParser;
import cn.com.dwsoft.main.constants.CommonFuncConstants;
import cn.com.dwsoft.util.jdbc.JdbcUtil;




public class DataCommonUtil {

	public static String checkCurrentEnvirnment() {
				
				
    	StringBuilder retSb = new StringBuilder();
    	boolean foundError = false;
    	try {
			String sql = "select 'success' from dual";
			JdbcUtil.checkJdbcEnvirnment(sql);
			retSb.append("\njdbc_connection="+"connected.");	
		} catch (Exception e) {
			retSb.append("\njdbc_connection="+"failed.");	
			System.out.println("------------------current data envirnment check---------------");
    		System.out.println(retSb.toString());
    		System.out.println("------------------check end-----------------------------------");
    		System.exit(-1);
		}
    	
    	
    	
    	try {
    		String sql = "select 'success' from guan_systeminfolog";
			JdbcUtil.checkJdbcEnvirnment(sql);
			retSb.append("\nguan_systeminfolog="+"exist");
		} catch (Exception e) {
			retSb.append("\nguan_systeminfolog="+"failed,table is not exist.");	
			foundError = true;
		}
    	    	
    	
    	
    	
    	if(foundError){
    		
    		System.out.println("\n------------------current data envirnment check---------------");
    		System.out.println(retSb.toString());
    		System.out.println("\n------------------check end-----------------------------------");
    		
    		System.out.println("\nSome items are failed! The program is terminated!");
    		System.exit(-1);
    	}
    	
    	return retSb.toString();
	}
	


	

}
