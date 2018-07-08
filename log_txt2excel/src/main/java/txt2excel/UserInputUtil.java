package txt2excel;

import java.util.Arrays;
import java.util.Scanner;

public class UserInputUtil {

	private static Scanner scanner;

	public static String input() {
		if(UserInputUtil.scanner==null)UserInputUtil.scanner = new Scanner(System.in);
		String str = UserInputUtil.scanner.nextLine();
		return str;
	}

	public static boolean validateinput(String command, String canvalues, String warnstr) {
		if(Arrays.asList(canvalues.split(",")).contains(command)){
			return true;
		}
		System.out.println(warnstr);
		return false;
	}

	public static String input(String str) {
		System.out.println(str);
		return input();
	}

}
