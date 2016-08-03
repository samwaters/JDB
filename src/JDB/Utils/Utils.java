package JDB.Utils;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class Utils
{
	public static void logMessage(String message)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
		String timeStamp = sdf.format(new Date());
		System.out.println("[" + timeStamp + "] " + message);
	}
}
