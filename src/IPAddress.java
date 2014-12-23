import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class IPAddress {
 public void  getInterfaces (){
      try {
         Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();

         while(e.hasMoreElements()) {
            NetworkInterface ni = (NetworkInterface) e.nextElement();
            System.out.println("Net interface: "+ni.getName());

            Enumeration<InetAddress> e2 = ni.getInetAddresses();

            while (e2.hasMoreElements()){
               InetAddress ip = (InetAddress) e2.nextElement();
               System.out.println("IP address: "+ ip.toString());
            }
         }
      }
      catch (Exception e) {
         e.printStackTrace();
      }
   }

	public String getPPPIp() {
		String ipaddress = null;
		try {
			Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();

			while (e.hasMoreElements()) {
				NetworkInterface ni = (NetworkInterface) e.nextElement();
				if (ni.getName().equals("ppp0")) {
					//System.out.println("Net interface: " + ni.getName());
					Enumeration<InetAddress> e2 = ni.getInetAddresses();
					while (e2.hasMoreElements()) {
						InetAddress ip = (InetAddress) e2.nextElement();
						ipaddress = "IP address: " + ip.toString();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ipaddress;
	}

   public static void main(String[] args) {
    IPAddress ip = new IPAddress();
    //ip.getInterfaces();
    ip.getPPPIp();
   }
}