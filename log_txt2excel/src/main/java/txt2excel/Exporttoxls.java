package txt2excel;

import java.io.IOException;
import java.util.ArrayList;

public class Exporttoxls {

	public static void exportxls() throws IOException {
		String days = UserInputUtil.input("请输入导出的开始和结束日期,中间用逗号隔开格式:yyyy-MM-dd");
		String[] dayarr = days.split(",");
		while(dayarr.length<2 || !DateUtil.judgeDate(dayarr[0]) || !DateUtil.judgeDate(dayarr[1])) {
			days = UserInputUtil.input("输入错误,请重新输入导出的开始和结束日期,中间用逗号隔开格式:yyyy-MM-dd");
			dayarr = days.split(",");
		}
		String orderindexs = UserInputUtil.input("请输入按第几列进行排序,用数字表示(可写从2到10),中间用逗号隔开");
		String[] orderindexarr = orderindexs.split(",");
		while(orderindexarr.length<1 || !isIntegers(orderindexarr)) {
			orderindexs = UserInputUtil.input("输入错误,请重新输入按第几列进行排序,用数字表示(可写从2到10),中间用逗号隔开");
			orderindexarr = days.split(",");
		}
		ArrayList<ArrayList<String>> datas = DbUtil.queryLogData(dayarr,orderindexarr);
		ExcelUtil.exportdata(datas);
	}

	private static boolean isIntegers(String[] orderindexarr) {
		for (String str : orderindexarr) {
			try {
				Integer.parseInt(str);
			} catch (NumberFormatException e) {
				return false;
			}
		}
		return true;
	}

}
