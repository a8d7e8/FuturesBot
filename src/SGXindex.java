import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;


public class SGXindex implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	private List<Double> SGXpercent = new ArrayList<Double>();
	private List<Double> SGXHistory = new ArrayList<Double>();
	private List<Double> SGXGap = new ArrayList<Double>();
	private double openindex;
	
	public static void main(String args[]) {
		try {
		FileInputStream fis = new FileInputStream("C:\\Dropbox\\SGX.ser");
		ObjectInputStream ois = new ObjectInputStream(fis);
		SGXindex sgx = (SGXindex) ois.readObject();
		ois.close();
		
		sgx.setOpenindex(277.7);
		double d1 = (277.7/273.8 - 1);
		double d2 = (7735.0/7640.0 - 1);
		sgx.addGap(d1 - d2);
		
		FileOutputStream fos = new FileOutputStream("C:\\Dropbox\\SGX.ser");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(sgx);
			oos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public SGXindex (){
	}
	
	private void addPercent(double input){
		if (SGXpercent.size() >= 10) {
			SGXpercent.remove(0);
		}
		SGXpercent.add(input);
	}
	
	private void addHistory(double input){
		if (SGXHistory.size() >= 10) {
			SGXHistory.remove(0);
		}
		SGXHistory.add(input);
	}
	
	public List<Double> getAllPercent(){
		return SGXpercent;
	}
	
	public Double getLastPercent() {
		if (SGXpercent.size() > 0)
			return SGXpercent.get(SGXpercent.size() - 1);
		else
			return 0.0d;
	}

	public double getOpenindex() {
		return openindex;
	}

	public void setOpenindex(double input) {
		addHistory(input);
		addPercent((input / this.openindex) - 1);
		this.openindex = input;
	}
	
	public void setPreSettle(double input) {
		this.openindex = input;
	}
	
	public void addGap(double input){
		if (SGXGap.size() >= 10) {
			SGXGap.remove(0);
		}
		SGXGap.add(input);
	}
	
	public double getlastSGX() {
		if (SGXGap.size() > 0)
			return SGXGap.get(SGXGap.size() - 1);
		else
			return 0;
	}
	
	public double getPreGap() {
		if (getlastSGX() > 0.002) {
			return 0.001;
		} else if (getlastSGX() < -0.002) { 
			return -0.001;
		} else 
			return 0;
	}
}
