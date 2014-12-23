package jna;

import com.sun.jna.Native;
import com.sun.jna.win32.StdCallLibrary;

public interface OMSignAPI extends StdCallLibrary {
	OMSignAPI INSTANCE = (OMSignAPI)
    Native.loadLibrary("C:\\Program Files\\OrderMaster\\OMSignAPI",
    		OMSignAPI.class);
	public boolean IniDllAndPosition(String signID, int iniPosition);
	public boolean GoOrder(String signID, String bySignStr,
			String dateTimeStr, int nowPosition, double nowPrice);
}
