package txt2excel;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;

public class DbUtil {

	public static void saveLogData(ArrayList<String> logrows) {
		Connection conn = getConn();
		PreparedStatement pStat = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		ArrayList<Object[]> strarrList = new ArrayList<Object[]>();
		ArrayList<String> allowdays = new ArrayList<String>();
		for (String str : logrows) {
			if(!str.trim().isEmpty()) {
				String[] strs0 = str.split("\t");
				Object[] strs = new Object[strs0.length];
				System.arraycopy(strs0, 0, strs, 0, strs0.length);
				if(strs.length > 10) {
					System.out.println("最大只能到十列,请调整后再导入.问题行:"+str);
					return;
				}
				if(strs.length == 1) {
					System.out.println("每行最少要有2列,请调整后再导入.问题行:"+str);
					return;
				}
				String daystr = strs[0].toString();
				if(!DateUtil.judgeDate(daystr)){
					System.out.println("第一列只能是日期,请调整后再导入.问题行:"+str);
					return;
				} else {
					Date d = DateUtil.parseDate(daystr);
					strs[0] = d;
					if (!allowdays.contains(daystr)){
						try {
							pst = conn.prepareStatement("select count(*) from MYLOGS where day = ?");
							pst.setObject(1, d);
							rs = pst.executeQuery();
							int rowCount = 0; 
							if(rs.next()) { 
							  rowCount=rs.getInt(1); 
							}
							if (rowCount!=0) {
								String yn = UserInputUtil.input("数据库中已经有"+daystr+"这一天的数据了,是否仍然要添加(yes/no)");
								while(!UserInputUtil.validateinput(yn, "yes,no", "输入值不正确")){
									yn = UserInputUtil.input("是否仍然要添加(yes/no)");
								}
								if (yn.equals("yes")) {
									allowdays.add(daystr);
								}else if(yn.equals("no")) {
									return;
								}
							}else{
								allowdays.add(daystr);
							}
						} catch (SQLException e) {
							e.printStackTrace();
						}finally{
							closeConn(conn);
							closeState(pst);
							closeResult(rs);
						}
					}
				}
				strarrList.add(strs);
			}
		}
		
		conn = getConn();
		try {

			String insertsql = "insert into MYLOGS(day,col2,col3,col4,col5,col6,col7,col8,col9,col10) values(?,?,?,?,?,?,?,?,?,?)";
			pStat = conn.prepareStatement(insertsql);
			for (Object[] strings : strarrList) {
				Object[] args = new Object[10];
				if(strings.length < 10) {
					System.arraycopy(strings, 0, args, 0, strings.length);
				}else{
					args = strings;
				}
				for (int i = 1; i <= args.length; i++) {
					pStat.setObject(i, args[i-1]);
				}
				pStat.addBatch();
			}
			pStat.executeBatch();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			closeConn(conn);
			closeState(pStat);
		}
	}

	private static void closeResult(ResultSet rs) {
		try {
			if(rs!=null)rs.close();
		} catch (SQLException e1) {
			rs=null;
		}
	}

	private static void closeState(Statement pst) {
		try {
			if(pst!=null)pst.close();
		} catch (SQLException e1) {
			pst=null;
		}
	}
	
	private static void closeConn(Connection conn) {
		try {
			if(conn!=null)conn.close();
		} catch (SQLException e1) {
			conn=null;
		}
	}

	private static Connection getConn() {
		Connection conn = null;
		Connection conn2 = null;
	    try {
	      Class.forName("org.sqlite.JDBC");
	      conn = DriverManager.getConnection("jdbc:sqlite:mylogs.db");
	      conn2 = DriverManager.getConnection("jdbc:sqlite:mylogs.db");
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
		ResultSet tab = null;
		Statement stat = null;
		try {
			tab = conn2.getMetaData().getTables("TEST", null, "MYLOGS", new String[]{"TABLE"});
			if(!tab.next()){
				//第一次就新建表
				stat = conn2.createStatement();
				String createtabsql = "create table MYLOGS ("
								+"		day date,        "
								+"		col2 varchar,     "
								+"		col3 varchar,     "
								+"		col4 varchar,     "
								+"		col5 varchar,     "
								+"		col6 varchar,     "
								+"		col7 varchar,     "
								+"		col8 varchar,     "
								+"		col9 varchar,     "
								+"		col10 varchar     "
								+"		)";
				stat.execute(createtabsql);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			closeConn(conn2);
			closeResult(tab);
			closeState(stat);
		}
	    
		return conn;
	}

	public static void deldata() {
		Connection conn = getConn();
		PreparedStatement stat = null;
		try {
			String sql = "delete from MYLOGS where day between ? and ?";
			stat = conn.prepareStatement(sql);
			String days = UserInputUtil.input("请输入删除的开始和结束日期,中间用逗号隔开格式:yyyy-MM-dd");
			String[] dayarr = days.split(",");
			if(dayarr.length<2 || !DateUtil.judgeDate(dayarr[0]) || !DateUtil.judgeDate(dayarr[1])) {
				days = UserInputUtil.input("输入错误,请重新输入删除的开始和结束日期,中间用逗号隔开格式:yyyy-MM-dd");
				dayarr = days.split(",");
			}
			String yn = UserInputUtil.input("是否真要删除"+days+"期间的数据(yes/no)");
			while(!UserInputUtil.validateinput(yn , "yes,no", "输入值不正确")){
				yn = UserInputUtil.input("是否真要删除(yes/no)");
			}
			if(yn.equals("no")) {
				return;
			}
			stat.setString(1, dayarr[0]);
			stat.setString(2, dayarr[1]);
			stat.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			closeConn(conn);
			closeState(stat);
		}
	}

	public static ArrayList<ArrayList<String>> queryLogData(String[] dayarr, String[] orderindexarr) {
		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
		Connection conn = getConn();
		PreparedStatement stat = null;
		ResultSet rs = null;
		try {
			String sql = "select * from MYLOGS where day between ? and ? order by ";
			StringBuffer sb = new StringBuffer("");
			for (String idx : orderindexarr) {
				if(sb.length()>1)sb.append(",");
				sb.append("col"+idx);
			}
			stat = conn.prepareStatement(sql+sb.toString());
			stat.setObject(1, DateUtil.parseDate(dayarr[0]));
			stat.setObject(2, DateUtil.parseDate(dayarr[1]));
			rs = stat.executeQuery();
			while(rs.next()) {
				ArrayList<String> row = new ArrayList<String>();
				for (int j = 1; j <= 10; j++) {
					String cellstr = rs.getString(j);
					if (cellstr!=null) {
						row.add(cellstr);
					}
				}
				result.add(row);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			closeConn(conn);
			closeState(stat);
		}
		return result ;
	}

}
