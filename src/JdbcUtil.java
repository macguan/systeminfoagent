package cn.com.dwsoft.util.jdbc;

import cn.com.dwsoft.util.file.PropertiesParser;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.logging.Log;  
import org.apache.commons.logging.LogFactory;

import cn.com.chinet.common.utils.db.DbUtil;  // this file is in chinet_common_1.5.4.161013.jar


public class JdbcUtil {
	
	private static Log logger = LogFactory.getLog(JdbcUtil.class); 
	

	/**
	 * 鑾峰彇杩炴帴,瀹氫箟鍦╦dbc.properties
	 */
	@SuppressWarnings("unused")
	
	/**
	 * 
	 * @param jdbcpropertiesFilePath
	 * 渚嬪:
	 * getConn("jdbc.properties");
	 * 灏辨槸jdbc.properties鏂囦欢鏀惧湪src鏍圭洰褰曚笅(鎴朩EB-INF/classes涓�)
	 * 
	 * getConn("/res/properties/jdbc.properties");
	 * 灏辨槸jdbc.properties鏂囦欢鏀惧湪src涓嬬殑res.properties鍖呬笅,(鎴朩EB-INF/classes/res/properties/涓�)
	 */
	public static Connection getConn(String jdbcpropertiesFilePath) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		
		PropertiesParser paser = new PropertiesParser(jdbcpropertiesFilePath);
		if(paser!=null){
			Class.forName(paser.getInfoFromConfiguration("jdbc.driverClassName")).newInstance(); 
			String   url= paser.getInfoFromConfiguration("jdbc.url"); 
			String   user= paser.getInfoFromConfiguration("jdbc.username"); 
			String   password= paser.getInfoFromConfiguration("jdbc.password"); 
			
			logger.debug("Now.get Connect from DB.");
			logger.debug("jdbc.driverClassName:"+paser.getInfoFromConfiguration("jdbc.driverClassName"));
			logger.debug("jdbc.url:"+url);
			logger.debug("jdbc.username:"+user);
			logger.debug("jdbc.password:"+password);
			Connection   conn=   DriverManager.getConnection(url,user,password); 
			
			paser.closeConfiguration();
			
			return conn;
		}else
			return null;
				
	}

	/**
	 * 鑾峰彇鑷畾涔夎繛鎺�
	 */
	public static Connection getConn(String driverClassName
									,String dbUrl
									,String dbUserName
									,String dbPasswd
									) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		
		Class.forName(driverClassName).newInstance(); 
		Connection   conn=   DriverManager.getConnection(dbUrl,dbUserName,dbPasswd); 
		
		return conn;
	}

	/**
	 * 鎵归噺鎵цSQL璇彞
	 * 閲囩敤缂虹渷JDBC鍙傛暟(jdbc.properties鏂囦欢涓畾涔夌殑)
	 */
	public static void executeSql(String jdbcpropertiesFilePath,String[] sqls) throws SQLException,
			InstantiationException, IllegalAccessException,
			ClassNotFoundException, IOException {
				Connection conn = getConn(jdbcpropertiesFilePath);
				Statement stmt=conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE); 
				for(String temp:sqls)
					stmt.executeUpdate(temp);
				stmt.close();
				conn.close();
			}



	/**
	 * 鑾峰彇SQL鐨勬暟瀛楃粨鏋�(绗竴鏉¤褰�,绗竴涓瓧娈�),瑕佹眰鏌ヨ鐨勬槸鏁板瓧瀛楁鎴朿ount涓�绫�
	 * 浼犲叆JDBC鍙傛暟
	 */
	public static Integer getSqlResult(
										String driverClassName
										,String dbUrl
										,String dbUserName
										,String dbPasswd
										,String sql
									) throws SQLException,
										InstantiationException, IllegalAccessException,
										ClassNotFoundException, IOException {
		
		Connection conn = getConn(driverClassName,dbUrl,dbUserName,dbPasswd);
		
		Statement   stmt=conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
												ResultSet.CONCUR_UPDATABLE); 
		ResultSet rs=stmt.executeQuery(sql);  
		
		Integer returnValue = 0;
		if(rs.next())
			returnValue = rs.getInt(1);
		
		rs.close();
		stmt.close();
		
		conn.close();
		
		return returnValue;
	}
	
	/**
	 * 鑾峰彇SQL鐨勫瓧绗︿覆缁撴灉(绗竴鏉¤褰�,绗竴涓瓧娈�),瑕佹眰鏌ヨ鐨勬槸瀛楃涓�
	 * 浼犲叆JDBC鍙傛暟
	 */
	public static String getSqlStringResult(
											String driverClassName
											,String dbUrl
											,String dbUserName
											,String dbPasswd
											,String sql
											) throws SQLException,
											InstantiationException, IllegalAccessException,
											ClassNotFoundException, IOException {

		Connection conn = getConn(driverClassName,dbUrl,dbUserName,dbPasswd);

		Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
											ResultSet.CONCUR_UPDATABLE);
		ResultSet rs = stmt.executeQuery(sql);

		String returnValue = "";
		if (rs.next())
			returnValue = rs.getString(1);

		rs.close();
		stmt.close();

		conn.close();

		return returnValue;
	}
	
	/**
	 * 鑾峰彇SQL鐨勬暟瀛楃粨鏋�(绗竴鏉¤褰�,绗竴涓瓧娈�),瑕佹眰鏌ヨ鐨勬槸鏁板瓧瀛楁鎴朿ount涓�绫�
	 * 閲囩敤缂虹渷JDBC鍙傛暟(jdbc.properties鏂囦欢涓畾涔夌殑)
	 */
	public static Integer getSqlResult(String jdbcpropertiesFilePath,String sql) throws SQLException,
			InstantiationException, IllegalAccessException,
			ClassNotFoundException, IOException {
				Connection conn = getConn(jdbcpropertiesFilePath);
				
				Statement   stmt=conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE); 
				ResultSet rs=stmt.executeQuery(sql);  
				
				Integer returnValue = 0;
				if(rs.next())
					returnValue = rs.getInt(1);
				
				rs.close();
				stmt.close();
				
				conn.close();
				
				return returnValue;
			}
	
	/**
	 * 鑾峰彇SQL鐨勫瓧绗︿覆缁撴灉(绗竴鏉¤褰�,绗竴涓瓧娈�),瑕佹眰鏌ヨ鐨勯瀛楁鏄瓧绗︿覆
	 * 閲囩敤缂虹渷JDBC鍙傛暟(jdbc.properties鏂囦欢涓畾涔夌殑)
	 */
	public static String getSqlStringResult(String jdbcpropertiesFilePath,String sql) throws SQLException,
																InstantiationException, IllegalAccessException,
																ClassNotFoundException, IOException {
		Connection conn = getConn(jdbcpropertiesFilePath);
		
		Statement   stmt=conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE); 
		ResultSet rs=stmt.executeQuery(sql);  
		
		String returnValue = "";
		if(rs.next())
			returnValue = rs.getString(1);
		
		rs.close();
		stmt.close();
		
		conn.close();
		
		return returnValue;
	}
	
	
	public static  String getUUId(){
		String id = DbUtil.getUUID32Str();
		return id;
	}

	public static void checkJdbcEnvirnment(String sql) throws Exception {
		//String sql = "select 'success' from dual";
		String retValue = getSqlStringResult("jdbc.properties",sql);
		System.out.println(retValue);

	}
	

}
