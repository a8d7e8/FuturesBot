import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Queue;
import java.util.TimeZone;

import jna.OMSignAPI;
import messenger.CalendarSample;
import messenger.facebook;
import messenger.gtalk;
//import messenger.iStockApi;

public class NewDdeClient {
	int current = 0;
	Queue<Integer> queue = new LinkedList<Integer>();
	List<Integer> ls = new LinkedList<Integer>();
	int size = 400;
	int lsize = 25;
	int[] price = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
	int sval = 30;
	int svalA = sval;
	int wval = 30;
	int wvalA = wval;
	int win = 0;
	int lost = 0;
	int total = 0;
	int range = 16; // 變動範圍值
	// int flag = 0;
	int high = 0;
	int low = 0;
	int higho = 0;
	int lowo = 0;
	// int posarrive = 300;
	int negarrive = -50;
	// int count = 0;
	int preSettle;
	private static String Email;
	private static String fb;
	private static String botname = "XXXXXX";
	double HLpercent;
	double nowpercent;
	double percent = 0.0; // 前一天留倉評估值
	int multiple = 2;
	String version = "";
	int inputt;
	boolean runflag = true;
	LogFile txt;
	int hlflag = 0;
	int tsize = price.length;
	int abscurrent = 0;
	static String futuressignals = "小台指,MXF,";
	static final String futuressignal = "台指期,TXF,";
	int tsize1 = 2;
	int currentmulti = 1;
	String YYMMDD;
	boolean isGtalkOn = false;
	//static gtalk g = gtalk.getInstance();
	boolean isFBOn = false;
	//static facebook f = facebook.getInstance();
	boolean isCalOn = false;
	//static CalendarSample cal = CalendarSample.getInstance();
	int vol;
	int totalvol;
	// int opweek; //下緣
	// int opweek1; //上緣
	boolean close = false;
	boolean SGXclose = false;
	// int week = 0;
	boolean isOpen = false;
	boolean isLowM = true;
	double kspercent = 0;
	Futures fu;
	int Gapvolin = 15;
	int GapvoloutL = 13;
	int GapvoloutH = 18;
	int fuindex;
	double volpro;
	int lowMFlag = 0;
	SGXindex sgx;
	double SGXGap = 0.002;
	double SGXGapA = 0.0012;
	double SGXGapB = 0.0034;
	double SGXGapC = 0.0022;
	double SGXGapL = 0.0012;
	double SGXGapLin = 0.0014;
	double SGXGap34 = 0.0014;
	double SGXGap56 = 0.005;
	double SGXpercent = 0;
	double SGXindex = 0;
	static int lowmcountT = 9;
	int lowmcountB = 0;
	int lowmcountS = 0;
	int HighLowtmp = 0;
	double lowoutgap = 0.0025;
	double highoutgap = 0.0050;
	boolean intoflag = true;
	static int countSize = 100;
	int counter = 0;
	int counterPos = 0;
	int svalT = 40;
	String SGXTime;
	int lowmcount = 0;
	//iStockApi istock = iStockApi.getInstance();
	double Offset;
	double OIGap;
	boolean dangflag = false;

	public static void main(String args[]) {
		NewDdeClient client = new NewDdeClient();
		client.doit("Test");
	}

	public NewDdeClient() {
		Properties prop = new Properties();
    	try {
            //load a properties file
    		prop.load(new FileInputStream("C:\\Dropbox\\config.properties"));
            //get the property value and print it out
    		setRange(Integer.parseInt(prop.getProperty("range")));
    		setSGXGapA(Double.parseDouble(prop.getProperty("SGXGapA")));
    		setSGXGap34(Double.parseDouble(prop.getProperty("SGXGap34")));
    		setSGXGapB(Double.parseDouble(prop.getProperty("SGXGapB")));
    		setSGXGapL(Double.parseDouble(prop.getProperty("SGXGapL")));
    		setSGXGapLin(Double.parseDouble(prop.getProperty("SGXGapLin")));
    		setOffset(Double.parseDouble(prop.getProperty("Offset")));
    		setOIGap(Double.parseDouble(prop.getProperty("OIGap")));
    	} catch (IOException ex) {
    		ex.printStackTrace();
        }
    	
    	try {
            //load a properties file
    		prop.load(new FileInputStream("C:\\Profile\\config.properties"));
            //get the property value and print it out
    		if ("true".equals(prop.getProperty("USE_CAL"))) {
    			isCalOn = true;
    		}
    		if ("true".equals(prop.getProperty("USE_GTALK"))) {
    			isGtalkOn = true;
    		}
    		if ("true".equals(prop.getProperty("USE_FB"))) {
    			isFBOn = true;
    		}
    	} catch (IOException ex) {
    		ex.printStackTrace();
        }
    	
		try {
			FileInputStream fis = new FileInputStream("C:\\Dropbox\\Fu.ser");
			ObjectInputStream ois = new ObjectInputStream(fis);
			fu = (Futures) ois.readObject();
			ois.close();
			// new File("Fu.ser").delete();
			// Clean up the file
			fuindex = fu.getHistoryPro();
			volpro = fu.getVolPro();
			// System.out.println("上一交易日未平倉" + opprice + "CALL "
			// + Math.abs(current) + "口!!");

			fis = new FileInputStream("C:\\Dropbox\\SGX.ser");
			ois = new ObjectInputStream(fis);
			sgx = (SGXindex) ois.readObject();
			ois.close();
			Offset = Offset + sgx.getPreGap();

			fis = new FileInputStream("C:\\Dropbox\\Updown.ser");
			ois = new ObjectInputStream(fis);
			UpdownNew ud = (UpdownNew) ois.readObject();
			ois.close();
			percent = ud.getDirection() * 0.1; //0~1 = 0.0000 ~ 0.0010
			Offset = Offset + (percent * OIGap);
		} catch (Exception ex) {
			System.out.println(ex);
			/*if (isGtalkOn)
			g.alert(botname, Email, "Object Loading Error!! " + ex);
			if (isFBOn)
			f.alert(botname, fb, "Object Loading Error!! " + ex);*/
			System.exit(0);
		}
		boolean result = OMSignAPI.INSTANCE
				.IniDllAndPosition("MTX002", current);
		if (!result) {
			System.out.println("OMSignAPI IniDllAndPosition Error!!");
			/*if (isGtalkOn)
			g.alert(botname, Email, "OMSignAPI IniDllAndPosition Error!!");
			if (isFBOn)
			f.alert(botname, fb, "OMSignAPI IniDllAndPosition Error!!");*/
		}
		IPAddress ip = new IPAddress();
		/*if (isGtalkOn)
		g.alert(botname, Email, ip.getPPPIp() + " Start Trading System!!" + Offset);
		if (isFBOn)
		f.alert(botname, fb, ip.getPPPIp() + " Start Trading System!! " + Offset);*/

		futuressignals = futuressignals + GetWednesday.compareWed1() + ",A";
		new File("C:\\Runtime").mkdir();
		YYMMDD = GetWednesday.gettoday();
		txt = new LogFile("C:\\Runtime\\" + YYMMDD + "_tickAPI.txt");

		if (YYMMDD.equals(GetWednesday.compareWed2(YYMMDD)))
			close = true;
		if (YYMMDD.equals(GetWednesday.getSGXClose(YYMMDD)))
			SGXclose = true;
	}

	void doit(String input) {
		String[] temp = input.split(",");
		if (temp[0].equals("TX00")) {
			int a = Integer.parseInt(temp[5]);
			totalvol = Integer.parseInt(temp[7]);
			if (a != 0 && totalvol > 0) {
				preSettle = Integer.parseInt(temp[10]);
				high = Integer.parseInt(temp[8]);
				low = Integer.parseInt(temp[9]);
				vol = Integer.parseInt(temp[6]);
				detect(a);
				if (runflag) {
					add(a);
					if (isOpen && intoflag) {
						check(a);
						if (counterPos != 0)
							counter++;
					}
					checkneg(a);
					checkTotal(a);
					check_runtime();
				}
				inputt = a;
			}
			// txt.setOutput(getNowTime() + " " + a);
		} else if (temp[0].equals("KOSPI")) {
			double ksindex = Double.parseDouble(temp[1]);
			double ksPreSettle = Double.parseDouble(temp[10]);
			if (ksindex != 0)
				kspercent = (ksindex / ksPreSettle) - 1;
		} else if (temp[0].equals("TWN")) {
			SGXindex = Double.parseDouble(temp[1]);
			double SGXPreSettle = Double.parseDouble(temp[10]);
			if (SGXindex != 0)
				SGXpercent = (SGXindex / SGXPreSettle) - 1;
			else
				SGXpercent = 0;
			setSGXTime();
		}
	}

	public void writetxt(int input) {
		boolean result = OMSignAPI.INSTANCE.GoOrder("MTX002", futuressignals,
				getNowTime(), current, input);
		if (!result) {
			System.out.println("OMSignAPI GoOrder Error!!");
			/*if (isGtalkOn)
			g.alert(botname, Email, "Futuresbot OMSignAPI GoOrder Error!!");
			if (isFBOn)
			f.alert(botname, fb, "Futuresbot OMSignAPI GoOrder Error!!");*/
		}
		//istock.priceGet(current, input);
		txt.setOutput(getNowTime() + " current:" + current + ", price:" + inputt);
		txt.flush();
		System.out.println("current:" + current + ", price:" + inputt);
	}

	public void check(int input) {
		double percent = Math.abs(nowpercent);
		if (0.01 > percent) {
			multiple = 2;
			// range = 11;
			// sval = 30;
		}
		if ((0.02 > percent) && (percent > 0.01)) {
			multiple = 3;
			dangflag = true;
			// range = 12;
			// sval = 25;
		}
		if ((0.03 > percent) && (percent > 0.02)) {
			multiple = 4;
			dangflag = true;
			// range = 13;
			// sval = 30;
		}
		if ((0.04 > percent) && (percent > 0.03)) {
			multiple = 5;
			dangflag = true;
			intoflag = false;
			// range = 14;
			// sval = 35;
		}
		if ((0.05 > percent) && (percent > 0.04)) {
			multiple = 6;
			dangflag = true;
			intoflag = false;
			// range = 15;
			// sval = 40;
		}
		if ((0.06 > percent) && (percent > 0.05)) {
			multiple = 7;
			dangflag = true;
			intoflag = false;
			// range = 16;
			// sval = 45;
		}
		if (percent > 0.06) {
			multiple = 8;
			dangflag = true;
			intoflag = false;
			// range = 17;
			// sval = 50;
		}

		if (current == 0) {
			if (lowMFlag == 0)
				checkin(input);
			else
				lowMcheckin(input);
		} else {
			checkout(input);
		}
	}

	public void add(int input) {
		queue.add(input);
		if (queue.size() >= size) {
			Object[] I = queue.toArray();
			Integer[] w = dwt(I);
			ls = Arrays.asList(w);
			queue.remove();
		}
	}

	private void detect(int input) {
		if (higho == 0) {
			if (isLowM) {
				higho = input + (10 * multiple);
			} else {
				higho = input + (15 * multiple);
			}
		}
		if (lowo == 0) {
			if (isLowM) {
				lowo = input - (10 * multiple);
			} else {
				lowo = input - (15 * multiple);
			}
		}
		if (input > higho) {
			higho = input;
			if ((hlflag == 0) && ((current > 0) || lowMFlag == -1)) {
				hlflag = 1;
			}
		}
		if (input < lowo) {
			lowo = input;
			if ((hlflag == 0) && ((current < 0) || lowMFlag == 1)) {
				hlflag = -1;
			}
		}
		double p1 = new Double(input).doubleValue();
		double p2 = new Double(preSettle).doubleValue();
		nowpercent = (p1 / p2) - 1;
		double p3 = new Double(high).doubleValue();
		double p4 = new Double(low).doubleValue();
		HLpercent = (p3 / p4) - 1;
		double p5 = new Double(higho).doubleValue();
		double p6 = new Double(lowo).doubleValue();
		double HLOpercent = (p5 / p6) - 1;
		if (HLOpercent > 0.009) {
			lowoutgap = 0.0025;
		} else {
			lowoutgap = 0.0050;
		}
		// percent = ProCalculator();
	}

	private int[] getQueryModel() {
		Object[] t = ls.toArray();
		Arrays.sort(t);
		List<Object> list = Arrays.asList(t);
		int min = (Integer) list.get(0);
		int max = (Integer) list.get(lsize - 1);
		int minpos = ls.lastIndexOf(min);
		int maxpos = ls.lastIndexOf(max);
		int vol = max - min;
		int[] pair = { 0, vol };
		if (vol > range) {
			if ((5 >= minpos) && (maxpos >= 20)) {
				pair[0] = 1;
			} else if ((5 >= maxpos) && (minpos >= 20)) {
				pair[0] = 2;
			}
			if ((16 <= minpos) && (minpos <= 20) && (maxpos >= 23)) {
				pair[0] = 3;
			} else if ((16 <= maxpos) && (maxpos <= 20) && (minpos >= 23)) {
				pair[0] = 4;
			}
		}
		return pair;
	}

	private void checkin(int input) {
		if (ls.size() >= lsize) {
			int[] pair = getQueryModel();
			// int vol = pair[1];
			if (pair[0] != 0) {
				if (pair[0] == 1 && !dangflag) {
					if (((SGXTWGap() + Offset) - SGXGapA) > 0 && !SGXclose) {
						boolean into = true;
						if (percent <= -1)
							into = false;
						if (into) {
							current = 1 * currentmulti;
							abscurrent = Math.abs(current);
							writetxt(0);
							for (int i = 0; i < abscurrent; i++)
								price[i] = input;
							// range += 2;
							System.out.println(getNowTime() + " max1:" + input);
							/*if (isGtalkOn)
							g.alert(botname, Email, getNowTime() + "max1 買進:"
									+ input + " " + abscurrent + "口");
							if (isFBOn)
							f.alert(botname, getNowTime() + " 買進:" + input
									+ " " + abscurrent + "口");
							if (isCalOn)
							cal.addEvent(getNowTime() + " 買進:" + input + " "
									+ abscurrent + "口");*/
							lowo = input;
							higho = 0;
							hlflag = 0;
							isLowM = false;
						}
					} else if (((SGXTWGap() + Offset) + SGXGapLin) < 0) {
						lowMFlag = -1;
						higho = input;
						lowo = 0;
						isLowM = true;
					}
				} else if (pair[0] == 2) {
					if (((SGXTWGap() + Offset) + SGXGapA) < 0 && !SGXclose) {
						boolean into = true;
						if (percent >= 1)
							into = false;
						if (into) {
							current = -1 * currentmulti;
							abscurrent = Math.abs(current);
							writetxt(0);
							for (int i = 0; i < abscurrent; i++)
								price[i] = input;
							// range += 2;
							System.out.println(getNowTime() + " min1:" + input);
							/*if (isGtalkOn)
							g.alert(botname, Email, getNowTime() + "min1 賣出:"
									+ input + " " + abscurrent + "口");
							if (isFBOn)
							f.alert(botname, getNowTime() + " 賣出:" + input
									+ " " + abscurrent + "口");
							if (isCalOn)
							cal.addEvent(getNowTime() + " 賣出:" + input + " "
									+ abscurrent + "口");*/
							higho = input;
							lowo = 0;
							hlflag = 0;
							isLowM = false;
						}
					} else if (((SGXTWGap() + Offset) - SGXGapLin) > 0) {
						lowMFlag = 1;
						lowo = input;
						higho = 0;
						isLowM = true;
					}
				} else if (pair[0] == 3 && !dangflag) {
					if (((SGXTWGap() + Offset) - SGXGap34) > 0) {
						boolean into = true;
						if (percent <= -1)
							into = false;
						if (into) {
							current = 1 * currentmulti;
							abscurrent = Math.abs(current);
							writetxt(0);
							for (int i = 0; i < abscurrent; i++)
								price[i] = input;
							// range += 2;
							System.out.println(getNowTime() + " max2:" + input);
							/*if (isGtalkOn)
							g.alert(botname, Email, getNowTime() + "max2 買進:"
									+ input + " " + abscurrent + "口");
							if (isFBOn)
							f.alert(botname, getNowTime() + " 買進:" + input
									+ " " + abscurrent + "口");
							if (isCalOn)
							cal.addEvent(getNowTime() + " 買進:" + input + " "
									+ abscurrent + "口");*/
							lowo = input;
							higho = 0;
							hlflag = 0;
							isLowM = false;
						}
					} else if (((SGXTWGap() + Offset) + SGXGapLin) < 0) {
						lowMFlag = -1;
						higho = input;
						lowo = 0;
						isLowM = true;
					}
				} else if (pair[0] == 4) {
					if (((SGXTWGap() + Offset) + SGXGap34) < 0) {
						boolean into = true;
						if (percent >= 1)
							into = false;
						if (into) {
							current = -1 * currentmulti;
							abscurrent = Math.abs(current);
							writetxt(0);
							for (int i = 0; i < abscurrent; i++)
								price[i] = input;
							// range += 2;
							System.out.println(getNowTime() + " min2:" + input);
							/*if (isGtalkOn)
							g.alert(botname, Email, getNowTime() + "min2 賣出:"
									+ input + " " + abscurrent + "口");
							if (isFBOn)
							f.alert(botname, getNowTime() + " 賣出:" + input
									+ " " + abscurrent + "口");
							if (isCalOn)
							cal.addEvent(getNowTime() + " 賣出:" + input + " "
									+ abscurrent + "口");*/
							higho = input;
							lowo = 0;
							hlflag = 0;
							isLowM = false;
						}
					} else if (((SGXTWGap() + Offset) - SGXGapLin) > 0) {
						lowMFlag = 1;
						lowo = input;
						higho = 0;
						isLowM = true;
					}
				}
			} else if (pair[0] == 0) {
				checkSGXGapB(input," sgx");
			}
		}
	}

	private void checkSGXGapB(int input, String text) {
		if (Math.abs(kspercent) > 0.01 || !close) {
			if (!SGXclose && intoflag) {
				if (((SGXTWGap() + Offset) - SGXGapB) > 0) {
					boolean into = true;
					if (percent <= -1)
						into = false;
					if (into) {
						counterPos++;
						if (counter > countSize) {
							if (counterPos > (countSize * 3 / 4)) {
								if (((SGXTWGap() + Offset) - (SGXGapB * 2)) > 0) {
									current = 2 * currentmulti;
								} else {
									current = 1 * currentmulti;
								}
								abscurrent = Math.abs(current);
								writetxt(input);
								for (int i = 0; i < abscurrent; i++)
									price[i] = input;
								System.out.println(getNowTime()
										+ " Gap: " + (SGXTWGap() * 100)
										+ text + "b:" + input);
								/*if (isGtalkOn)
								g.alert(botname, Email, getNowTime()
										+ text + "b 買進:" + input + " "
										+ abscurrent + "口" + " 次數:"
										+ counterPos + " " +SGXTWGap());
								if (isFBOn)
								f.alert(botname, getNowTime()
										+ " 買進:" + input + " "
										+ abscurrent + "口");
								if (isCalOn)
								cal.addEvent(getNowTime() + " 買進:"
										+ input + " " + abscurrent
										+ "口");*/
								lowo = input;
								higho = 0;
								hlflag = 0;
								isLowM = false;
								intoflag = false;
								counter = 0;
								counterPos = 0;
							} else {
								counter = 0;
								counterPos = 0;
							}
						}
					}
				} else if (((SGXTWGap() + Offset) + SGXGapB) < 0) {
					boolean into = true;
					if (percent >= 1)
						into = false;
					if (into) {
						counterPos--;
						if (counter > countSize) {
							if (counterPos < (countSize * -3 / 4)) {
								if (((SGXTWGap() + Offset) + (SGXGapB * 2)) < 0) {
									current = -2 * currentmulti;
								} else {
									current = -1 * currentmulti;
								}
								abscurrent = Math.abs(current);
								writetxt(input);
								for (int i = 0; i < abscurrent; i++)
									price[i] = input;
								System.out.println(getNowTime()
										+ " Gap: " + (SGXTWGap() * 100)
										+ text + "s:" + input);
								/*if (isGtalkOn)
								g.alert(botname, Email, getNowTime()
										+ text + "s 賣出:" + input + " "
										+ abscurrent + "口" + " 次數:"
										+ counterPos + " " +SGXTWGap());
								if (isFBOn)
								f.alert(botname, getNowTime()
										+ " 賣出:" + input + " "
										+ abscurrent + "口");
								if (isCalOn)
								cal.addEvent(getNowTime() + " 賣出:"
										+ input + " " + abscurrent
										+ "口");*/
								higho = input;
								lowo = 0;
								hlflag = 0;
								isLowM = false;
								intoflag = false;
								counter = 0;
								counterPos = 0;
							} else {
								counter = 0;
								counterPos = 0;
							}
						}
					}
				}
			}
		}
	}
	private void lowMcheckin(int input) {
		if ((input >= (lowo + Gapvolin)) && hlflag == -1) {
			if (((SGXTWGap() + Offset) + SGXGapC) < 0) {
				lowMFlag = 0;
				isLowM = false;
				HighLowtmp = 0;
				lowmcountB = 0;
				lowmcount = 0;
			} else if (input < (high - 20) && ((SGXTWGap() + Offset) - SGXGapL) > 0) {
				boolean into = true;
				if (percent <= -1)
					into = false;
				if (into) {
					if (lowmcountB > lowmcountT) {
						current = 1 * currentmulti;
						abscurrent = Math.abs(current);
						writetxt(input);
						for (int i = 0; i < abscurrent; i++)
							price[i] = input;
						System.out.println(getNowTime() + "LOWM BUY:" + input);
						/*if (isGtalkOn)
						g.alert(botname, Email, getNowTime() + " LOWM多單買進:"
								+ input + " " + abscurrent + "口" + " 次數:"
								+ lowmcountB);
						if (isFBOn)
						f.alert(botname, getNowTime() + " 買進:" + input
								+ " " + abscurrent + "口");
						if (isCalOn)
						cal.addEvent(getNowTime() + " 買進:" + input + " "
								+ abscurrent + "口");*/
						hlflag = 0;
						lowmcountB = 0;
						lowmcount = 0;
					} else {
						lowmcountB++;
						if (lowmcountB == 1) {
							HighLowtmp = lowo;
						} else if (lowo < HighLowtmp) {
							lowMFlag = 0;
							isLowM = true;
							HighLowtmp = 0;
							lowmcountB = 0;
							lowmcount = 0;
						}
					}
				}
			} else if (((SGXTWGap() + Offset) - 0.0004) < 0) {
				lowmcount++;
				if (lowmcount > countSize) {
					lowMFlag = 0;
					isLowM = true;
					HighLowtmp = 0;
					lowmcountB = 0;
					lowmcount = 0;
				}
			}
		} else if ((input <= (higho - Gapvolin)) && hlflag == 1) {
			if (((SGXTWGap() + Offset) - SGXGapC) > 0) {
				lowMFlag = 0;
				isLowM = false;
				HighLowtmp = 0;
				lowmcountS = 0;
				lowmcount = 0;
			} else if (input > (low + 20) && ((SGXTWGap() + Offset) + SGXGapL) < 0) {
				boolean into = true;
				if (percent >= 1)
					into = false;
				if (into) {
					if (lowmcountS > lowmcountT) {
						current = -1 * currentmulti;
						abscurrent = Math.abs(current);
						writetxt(input);
						for (int i = 0; i < abscurrent; i++)
							price[i] = input;
						System.out.println(getNowTime() + "LOWM SELL" + input);
						/*if (isGtalkOn)
						g.alert(botname, Email, getNowTime() + " LOWM空單賣出:"
								+ input + " " + abscurrent + "口" + " 次數:"
								+ lowmcountS);
						if (isFBOn)
						f.alert(botname, getNowTime() + " 賣出:" + input
								+ " " + abscurrent + "口");
						if (isCalOn)
						cal.addEvent(getNowTime() + " 賣出:" + input + " "
								+ abscurrent + "口");*/
						hlflag = 0;
						lowmcountS = 0;
						lowmcount = 0;
					} else {
						lowmcountS++;
						if (lowmcountS == 1) {
							HighLowtmp = higho;
						} else if (higho < HighLowtmp) {
							lowMFlag = 0;
							isLowM = true;
							HighLowtmp = 0;
							lowmcountS = 0;
							lowmcount = 0;
						}
					}
				}
			} else if (((SGXTWGap() + Offset) + 0.0004) > 0) {
				lowmcount++;
				if (lowmcount > countSize) {
					lowMFlag = 0;
					isLowM = true;
					HighLowtmp = 0;
					lowmcountS = 0;
					lowmcount = 0;
				}
			}
		}
		checkSGXGapB(input," sgx1");
	}

	private void checkout(int input) {
		if (isLowM) {
			LowMcheckout(input);
		} else {
			if (ls.size() >= lsize) {
				int[] pair = getQueryModel();
				if (pair[0] != 0) {
					if (pair[0] == 1) {
						checkout1(input, true);
					} else if (pair[0] == 2) {
						checkout1(input, false);
					} else if (pair[0] == 3) {
						checkout2(input, true);
					} else if (pair[0] == 4) {
						checkout2(input, false);
					}
				}
			}
			HighMcheckout(input);
		}
	}

	private void LowMcheckout(int input) {
		if (current > 0) {
			if ((SGXTWGap() - SGXGap) <= 0) {
				if (getHighLowPro(true) > lowoutgap) {
					if ((input <= (higho - GapvoloutL)) && hlflag == 1) { // 判斷是否高點回檔
						// 多方停利
						for (int i = 0; i < abscurrent; i++) {
							if (input > price[i]) {
								win = win + (input - price[i]);
								total = total + (input - price[i]);
								txt.setOutput(getNowTime() + "BUYLOWMW1! cost:"
										+ input);
								/*if (isGtalkOn)
								g.alert(botname, Email, getNowTime()
										+ " LOWW1多單停利:" + input + " "
										+ abscurrent + "口");
								if (isFBOn)
								f.alert(botname, getNowTime() + " 多單停利:"
										+ input + " " + abscurrent + "口");
								if (isCalOn)
								cal.addEvent(getNowTime() + " 多單停利:" + input
										+ " " + abscurrent + "口");*/
							} else {
								lost = lost + (price[i] - input);
								total = total - (price[i] - input);
								txt.setOutput(getNowTime() + "BUYLOWML1! cost:"
										+ input);
								/*if (isGtalkOn)
								g.alert(botname, Email, getNowTime()
										+ " LOWL1多單停損:" + input + " "
										+ abscurrent + "口");
								if (isFBOn)
								f.alert(botname, getNowTime() + " 多單停損:"
										+ input + " " + abscurrent + "口");
								if (isCalOn)
								cal.addEvent(getNowTime() + " 多單停損:" + input
										+ " " + abscurrent + "口");*/
							}
						}
						init();
						current = 0;
						writetxt(input);
						queue.clear();
					}
				} else {
					if ((SGXTWGap() + 0.0016) < 0) {
						if (SGXTWGap() < -0.0013)
							wval = wvalA - (int) (SGXTWGap() / -0.0011) * 5;
						if (input > (price[abscurrent - 1] + wval)) {
							for (int i = 0; i < abscurrent; i++) {
								if (input > price[i]) {
									win = win + (input - price[i]);
									total = total + (input - price[i]);
									txt.setOutput(getNowTime()
											+ "BUYLOWMW2! cost:" + input);
									/*if (isGtalkOn)
									g.alert(botname, Email, getNowTime()
											+ " LOWW2多單停利:" + input + " "
											+ abscurrent + "口");
									if (isFBOn)
									f.alert(botname, getNowTime()
											+ " 多單停利:" + input + " "
											+ abscurrent + "口");
									if (isCalOn)
									cal.addEvent(getNowTime() + " 多單停利:" + input
											+ " " + abscurrent + "口");*/
								} else {
									lost = lost + (price[i] - input);
									total = total - (price[i] - input);
									txt.setOutput(getNowTime()
											+ "BUYLOWML2! cost:" + input);
									/*if (isGtalkOn)
									g.alert(botname, Email, getNowTime()
											+ " LOWL2多單停損:" + input + " "
											+ abscurrent + "口");
									if (isFBOn)
									f.alert(botname, getNowTime()
											+ " 多單停損:" + input + " "
											+ abscurrent + "口");
									if (isCalOn)
									cal.addEvent(getNowTime() + " 多單停損:" + input
											+ " " + abscurrent + "口");*/
								}
							}
							init();
							current = 0;
							writetxt(input);
							queue.clear();
						}
					}
				}
			}
		} else if (current < 0) {
			if ((SGXTWGap() + SGXGap) >= 0) {
				if (getHighLowPro(false) > lowoutgap) {
					if ((input >= (lowo + GapvoloutL)) && hlflag == -1) { // 判斷是否低點回檔
						// 空方停損
						for (int i = 0; i < abscurrent; i++) {
							if (input < price[i]) {
								win = win + (price[i] - input);
								total = total + (price[i] - input);
								txt.setOutput(getNowTime() + "SELLCW1! cost:"
										+ input);
								/*if (isGtalkOn)
								g.alert(botname, Email, getNowTime()
										+ " LOWW1空單停利:" + input + " "
										+ abscurrent + "口");
								if (isFBOn)
								f.alert(botname, getNowTime() + " 空單停利:"
										+ input + " " + abscurrent + "口");
								if (isCalOn)
								cal.addEvent(getNowTime() + " 空單停利:" + input
										+ " " + abscurrent + "口");*/
							} else {
								lost = lost + (input - price[i]);
								total = total - (input - price[i]);
								txt.setOutput(getNowTime() + "SELLCL1! cost:"
										+ input);
								/*if (isGtalkOn)
								g.alert(botname, Email, getNowTime()
										+ " LOWL1空單停損:" + input + " "
										+ abscurrent + "口");
								if (isFBOn)
								f.alert(botname, getNowTime() + " 空單停損:"
										+ input + " " + abscurrent + "口");
								if (isCalOn)
								cal.addEvent(getNowTime() + " 空單停損:" + input
										+ " " + abscurrent + "口");*/
							}
						}
						init();
						current = 0;
						writetxt(input);
						queue.clear();
					}
				} else {
					if ((SGXTWGap() - 0.0016) > 0) {
						if (SGXTWGap() > 0.0013)
							wval = wvalA - (int) (SGXTWGap() / 0.0011) * 5;
						if (input < (price[abscurrent - 1] - wval)) {
							for (int i = 0; i < abscurrent; i++) {
								if (input < price[i]) {
									win = win + (price[i] - input);
									total = total + (price[i] - input);
									txt.setOutput(getNowTime()
											+ " SELLCW2! cost:" + input);
									/*if (isGtalkOn)
									g.alert(botname, Email, getNowTime()
											+ " LOWW2空單停利:" + input + " "
											+ abscurrent + "口");
									if (isFBOn)
									f.alert(botname, getNowTime()
											+ " 空單停利:" + input + " "
											+ abscurrent + "口");
									if (isCalOn)
									cal.addEvent(getNowTime() + " 空單停利:" + input
											+ " " + abscurrent + "口");*/
								} else {
									lost = lost + (input - price[i]);
									total = total - (input - price[i]);
									txt.setOutput(getNowTime()
											+ " SELLCL2! cost:" + input);
									/*if (isGtalkOn)
									g.alert(botname, Email, getNowTime()
											+ " LOWL2空單停損:" + input + " "
											+ abscurrent + "口");
									if (isFBOn)
									f.alert(botname, getNowTime()
											+ " 空單停損:" + input + " "
											+ abscurrent + "口");
									if (isCalOn)
									cal.addEvent(getNowTime() + " 空單停損:" + input
											+ " " + abscurrent + "口");*/
								}
							}
							init();
							current = 0;
							writetxt(input);
							queue.clear();
						}
					}
				}
			}
		}
	}

	private void HighMcheckout(int input) {
		if (current > 0) {
			if ((SGXTWGap() - SGXGap) <= 0) {
				if (getHighLowPro(true) > highoutgap) {
					if ((input <= (higho - GapvoloutH)) && hlflag == 1) { // 判斷是否高點回檔
						if (input > (price[abscurrent - 1] + sval)) {
							// 多方停利
							for (int i = 0; i < abscurrent; i++) {
								if (input > price[i]) {
									win = win + (input - price[i]);
									total = total + (input - price[i]);
									txt.setOutput(getNowTime()
											+ " BUYHIGHMW1! cost:" + input);
									/*if (isGtalkOn)
									g.alert(botname, Email, getNowTime()
											+ " HIGHM1多單停利:" + input + " "
											+ abscurrent + "口");
									if (isFBOn)
									f.alert(botname, getNowTime()
											+ " 多單停利:" + input + " "
											+ abscurrent + "口");
									if (isCalOn)
									cal.addEvent(getNowTime() + " 多單停利:" + input
											+ " " + abscurrent + "口");*/
								} else {
									lost = lost + (price[i] - input);
									total = total - (price[i] - input);
									txt.setOutput(getNowTime()
											+ " BUYHIGHML1! cost:" + input);
									/*if (isGtalkOn)
									g.alert(botname, Email, getNowTime()
											+ " HIGHL1多單停損:" + input + " "
											+ abscurrent + "口");
									if (isFBOn)
									f.alert(botname, getNowTime()
											+ " 多單停損:" + input + " "
											+ abscurrent + "口");
									if (isCalOn)
									cal.addEvent(getNowTime() + " 多單停利:" + input
											+ " " + abscurrent + "口");*/
								}
							}
							init();
							current = 0;
							writetxt(input);
							queue.clear();
						}
					}
				} else {
					if ((SGXTWGap() + 0.0012) < 0) {
						if (input > (price[abscurrent - 1] + sval)) {
							// 多方停利
							for (int i = 0; i < abscurrent; i++) {
								if (input > price[i]) {
									win = win + (input - price[i]);
									total = total + (input - price[i]);
									txt.setOutput(getNowTime()
											+ " BUYHIGHMW2! cost:" + input);
									/*if (isGtalkOn)
									g.alert(botname, Email, getNowTime()
											+ " HIGHM2多單停利:" + input + " "
											+ abscurrent + "口");
									if (isFBOn)
									f.alert(botname, getNowTime()
											+ " 多單停利:" + input + " "
											+ abscurrent + "口");
									if (isCalOn)
									cal.addEvent(getNowTime() + " 多單停利:" + input
											+ " " + abscurrent + "口");*/
								} else {
									lost = lost + (price[i] - input);
									total = total - (price[i] - input);
									txt.setOutput(getNowTime()
											+ " BUYHIGHML2! cost:" + input);
									/*if (isGtalkOn)
									g.alert(botname, Email, getNowTime()
											+ " HIGHL2多單停損:" + input + " "
											+ abscurrent + "口");
									if (isFBOn)
									f.alert(botname, getNowTime()
											+ " 多單停損:" + input + " "
											+ abscurrent + "口");
									if (isCalOn)
									cal.addEvent(getNowTime() + " 多單停利:" + input
											+ " " + abscurrent + "口");*/
								}
							}
							init();
							current = 0;
							writetxt(input);
							queue.clear();
						}
					}
				}
			}
		} else if (current < 0) {
			if ((SGXTWGap() + SGXGap) >= 0) {
				if (getHighLowPro(false) > highoutgap) {
					if ((input >= (lowo + GapvoloutH)) && hlflag == -1) { // 判斷是否低點回檔
						if (input < (price[abscurrent - 1] - sval)) {
							// 空方停損
							for (int i = 0; i < abscurrent; i++) {
								if (input < price[i]) {
									win = win + (price[i] - input);
									total = total + (price[i] - input);
									txt.setOutput(getNowTime()
											+ " SELLHIGHW1! cost:" + input);
									/*if (isGtalkOn)
									g.alert(botname, Email, getNowTime()
											+ " HIGHM1空單停利:" + input + " "
											+ abscurrent + "口");
									if (isFBOn)
									f.alert(botname, getNowTime()
											+ " 空單停利:" + input + " "
											+ abscurrent + "口");
									if (isCalOn)
									cal.addEvent(getNowTime() + " 空單停利:" + input
											+ " " + abscurrent + "口");*/
								} else {
									lost = lost + (input - price[i]);
									total = total - (input - price[i]);
									txt.setOutput(getNowTime()
											+ " SELLHIGHL1! cost:" + input);
									/*if (isGtalkOn)
									g.alert(botname, Email, getNowTime()
											+ " HIGHL1空單停損:" + input + " "
											+ abscurrent + "口");
									if (isFBOn)
									f.alert(botname, getNowTime()
											+ " 空單停損:" + input + " "
											+ abscurrent + "口");
									if (isCalOn)
									cal.addEvent(getNowTime() + " 空單停損:" + input
											+ " " + abscurrent + "口");*/
								}
							}
							init();
							current = 0;
							writetxt(input);
							queue.clear();
						}
					}
				} else {
					if ((SGXTWGap() - 0.0012) > 0) {
						if (input < (price[abscurrent - 1] - sval)) {
							// 空方停損
							for (int i = 0; i < abscurrent; i++) {
								if (input < price[i]) {
									win = win + (price[i] - input);
									total = total + (price[i] - input);
									txt.setOutput(getNowTime()
											+ " SELLHIGHW2! cost:" + input);
									/*if (isGtalkOn)
									g.alert(botname, Email, getNowTime()
											+ " HIGHM2空單停利:" + input + " "
											+ abscurrent + "口");
									if (isFBOn)
									f.alert(botname, getNowTime()
											+ " 空單停利:" + input + " "
											+ abscurrent + "口");
									if (isCalOn)
									cal.addEvent(getNowTime() + " 空單停利:" + input
											+ " " + abscurrent + "口");*/
								} else {
									lost = lost + (input - price[i]);
									total = total - (input - price[i]);
									txt.setOutput(getNowTime()
											+ " SELLHIGHL2! cost:" + input);
									/*if (isGtalkOn)
									g.alert(botname, Email, getNowTime()
											+ " HIGHL2空單停損:" + input + " "
											+ abscurrent + "口");
									if (isFBOn)
									f.alert(botname, getNowTime()
											+ " 空單停損:" + input + " "
											+ abscurrent + "口");
									if (isCalOn)
									cal.addEvent(getNowTime() + " 空單停損:" + input
											+ " " + abscurrent + "口");*/
								}
							}
							init();
							current = 0;
							writetxt(input);
							queue.clear();
						}
					}
				}
			}
		}
	}

	private void checkout1(int input, boolean up) {
		if (current > 0) {
			if ((input <= (higho - GapvoloutH)) && hlflag == 1 && (SGXTWGap() - SGXGap56) < 0) { // 判斷是否高點回檔
				if (input <= price[0]) { // 多方停損
					current = 0;
					writetxt(input);
					for (int i = 0; i < abscurrent; i++) {
						lost = lost + (price[i] - input);
						total = total - (price[i] - input);
					}
					init();
					txt.setOutput(getNowTime() + " BUY1L! cost:" + input + " "
							+ abscurrent + "口");
					/*if (isGtalkOn)
					g.alert(botname, Email, getNowTime() + " 多單停損:" + input
							+ " " + abscurrent + "口");
					if (isFBOn)
					f.alert(botname, getNowTime() + " 多單停損:" + input + " "
							+ abscurrent + "口");
					if (isCalOn)
					cal.addEvent(getNowTime() + " 多單停損:" + input + " "
							+ abscurrent + "口");*/
				} else {
					current = 0;
					writetxt(input);
					for (int i = 0; i < abscurrent; i++) {
						win = win + (input - price[i]);
						total = total + (input - price[i]);
					}
					init();
					txt.setOutput(getNowTime() + " BUY1W! cost:" + input + " "
							+ abscurrent + "口");
					/*if (isGtalkOn)
					g.alert(botname, Email, getNowTime() + " 多單停利:" + input
							+ " " + abscurrent + "口");
					if (isFBOn)
					f.alert(botname, getNowTime() + " 多單停利:" + input + " "
							+ abscurrent + "口");
					if (isCalOn)
					cal.addEvent(getNowTime() + " 多單停利:" + input + " "
							+ abscurrent + "口");*/
				}
			} else {
				if (!up) {
					if (!((SGXTWGap() - 0.0012) > 0)) {
						if (input <= price[0]) { // 多方停損
							current = 0;
							writetxt(input);
							for (int i = 0; i < abscurrent; i++) {
								lost = lost + (price[i] - input);
								total = total - (price[i] - input);
							}
							init();
							txt.setOutput(getNowTime() + " BUYL! cost:" + input
									+ " " + abscurrent + "口");
							/*if (isGtalkOn)
							g.alert(botname, Email, getNowTime() + " 多單停損:"
									+ input + " " + abscurrent + "口");
							if (isFBOn)
							f.alert(botname, getNowTime() + " 多單停損:"
									+ input + " " + abscurrent + "口");
							if (isCalOn)
							cal.addEvent(getNowTime() + " 多單停損:" + input + " "
									+ abscurrent + "口");*/
						} else {
							current = 0;
							writetxt(input);
							for (int i = 0; i < abscurrent; i++) {
								win = win + (input - price[i]);
								total = total + (input - price[i]);
							}
							init();
							txt.setOutput(getNowTime() + " BUYW! cost:" + input
									+ " " + abscurrent + "口");
							/*if (isGtalkOn)
							g.alert(botname, Email, getNowTime() + " 多單停利:"
									+ input + " " + abscurrent + "口");
							if (isFBOn)
							f.alert(botname, getNowTime() + " 多單停利:"
									+ input + " " + abscurrent + "口");
							if (isCalOn)
							cal.addEvent(getNowTime() + " 多單停利:" + input + " "
									+ abscurrent + "口");*/
						}
					}
				} else {
					if ((SGXTWGap() - 0.0026) > 0) {
						if (Math.abs(current) < tsize1) {
							int newcurrent = current + (1 * currentmulti);
							for (int i = Math.abs(current); i < Math
									.abs(newcurrent); i++)
								price[i] = input;
							current = newcurrent;
							abscurrent = Math.abs(current);
							writetxt(0);
							txt.setOutput(getNowTime() + " max3:" + input + " "
									+ abscurrent + "口");
							// range += 2;
							/*if (isGtalkOn)
							g.alert(botname, Email, getNowTime() + " 多單加碼:"
									+ input + " " + abscurrent + "口");
							if (isFBOn)
							f.alert(botname, getNowTime() + " 多單加碼:"
									+ input + " " + abscurrent + "口");
							if (isCalOn)
							cal.addEvent(getNowTime() + " 多單加碼:" + input + " "
									+ abscurrent + "口");*/
							hlflag = 0;
						}
					}
				}
			}
		} else if (current < 0) {
			if ((input >= (lowo + GapvoloutH)) && hlflag == -1 && (SGXTWGap() + SGXGap56) > 0) { // 判斷是否低點回檔
				if (input >= price[0]) { // 空方停損
					current = 0;
					writetxt(input);
					for (int i = 0; i < abscurrent; i++) {
						lost = lost + (input - price[i]);
						total = total - (input - price[i]);
					}
					init();
					txt.setOutput(getNowTime() + " SELL1L! cost:" + input + " "
							+ abscurrent + "口");
					/*if (isGtalkOn)
					g.alert(botname, Email, getNowTime() + " 空單停損:" + input
							+ " " + abscurrent + "口");
					if (isFBOn)
					f.alert(botname, getNowTime() + " 空單停損:" + input + " "
							+ abscurrent + "口");
					if (isCalOn)
					cal.addEvent(getNowTime() + " 空單停損:" + input + " "
							+ abscurrent + "口");*/
				} else {
					current = 0;
					writetxt(input);
					for (int i = 0; i < abscurrent; i++) {
						win = win + (price[i] - input);
						total = total + (price[i] - input);
					}
					init();
					txt.setOutput(getNowTime() + " SELL1! cost:" + input + " "
							+ abscurrent + "口");
					/*if (isGtalkOn)
					g.alert(botname, Email, getNowTime() + " 空單停利:" + input
							+ " " + abscurrent + "口");
					if (isFBOn)
					f.alert(botname, getNowTime() + " 空單停利:" + input + " "
							+ abscurrent + "口");
					if (isCalOn)
					cal.addEvent(getNowTime() + " 空單停利:" + input + " "
							+ abscurrent + "口");*/
				}
			} else {
				if (up) {
					if (!((SGXTWGap() + 0.0012) < 0)) {
						if (input >= price[0]) { // 空方停損
							current = 0;
							writetxt(input);
							for (int i = 0; i < abscurrent; i++) {
								lost = lost + (input - price[i]);
								total = total - (input - price[i]);
							}
							init();
							txt.setOutput(getNowTime() + " SELLL! cost:"
									+ input + " " + abscurrent + "口");
							/*if (isGtalkOn)
							g.alert(botname, Email, getNowTime() + " 空單停損:"
									+ input + " " + abscurrent + "口");
							if (isFBOn)
							f.alert(botname, getNowTime() + " 空單停損:"
									+ input + " " + abscurrent + "口");
							if (isCalOn)
							cal.addEvent(getNowTime() + " 空單停損:" + input + " "
									+ abscurrent + "口");*/
						} else {
							current = 0;
							writetxt(input);
							for (int i = 0; i < abscurrent; i++) {
								win = win + (price[i] - input);
								total = total + (price[i] - input);
							}
							init();
							txt.setOutput(getNowTime() + " SELLW! cost:"
									+ input + " " + abscurrent + "口");
							/*if (isGtalkOn)
							g.alert(botname, Email, getNowTime() + " 空單停利:"
									+ input + " " + abscurrent + "口");
							if (isFBOn)
							f.alert(botname, getNowTime() + " 空單停利:"
									+ input + " " + abscurrent + "口");
							if (isCalOn)
							cal.addEvent(getNowTime() + " 空單停利:" + input + " "
									+ abscurrent + "口");*/
						}
					}
				} else {
					if ((SGXTWGap() + 0.0026) < 0) {
						if (Math.abs(current) < tsize1) {
							int newcurrent = current + (-1 * currentmulti);
							for (int i = Math.abs(current); i < Math
									.abs(newcurrent); i++)
								price[i] = input;
							current = newcurrent;
							abscurrent = Math.abs(current);
							writetxt(0);
							txt.setOutput(getNowTime() + " min3:" + input + " "
									+ abscurrent + "口");
							// range += 2;
							/*if (isGtalkOn)
							g.alert(botname, Email, getNowTime() + " 空單加碼:"
									+ input + " " + abscurrent + "口");
							if (isFBOn)
							f.alert(botname, getNowTime() + " 空單加碼:"
									+ input + " " + abscurrent + "口");
							if (isCalOn)
							cal.addEvent(getNowTime() + " 空單加碼:" + input + " "
									+ abscurrent + "口");*/
							hlflag = 0;
						}
					}
				}
			}
		}
	}
	
	private void checkout2(int input, boolean up) {
		if (current > 0) {
			if ((input <= (higho - GapvoloutH)) && hlflag == 1 && (SGXTWGap() - SGXGap56) < 0) { // 判斷是否高點回檔
				if (input <= price[0]) { // 多方停損
					current = 0;
					writetxt(input);
					for (int i = 0; i < abscurrent; i++) {
						lost = lost + (price[i] - input);
						total = total - (price[i] - input);
					}
					init();
					txt.setOutput(getNowTime() + " BUY1L! cost:" + input + " "
							+ abscurrent + "口");
					/*if (isGtalkOn)
					g.alert(botname, Email, getNowTime() + " 多單停損:" + input
							+ " " + abscurrent + "口");
					if (isFBOn)
					f.alert(botname, getNowTime() + " 多單停損:" + input + " "
							+ abscurrent + "口");
					if (isCalOn)
					cal.addEvent(getNowTime() + " 多單停損:" + input + " "
							+ abscurrent + "口");*/
				} else {
					current = 0;
					writetxt(input);
					for (int i = 0; i < abscurrent; i++) {
						win = win + (input - price[i]);
						total = total + (input - price[i]);
					}
					init();
					txt.setOutput(getNowTime() + " BUY1W! cost:" + input + " "
							+ abscurrent + "口");
					/*if (isGtalkOn)
					g.alert(botname, Email, getNowTime() + " 多單停利:" + input
							+ " " + abscurrent + "口");
					if (isFBOn)
					f.alert(botname, getNowTime() + " 多單停利:" + input + " "
							+ abscurrent + "口");
					if (isCalOn)
					cal.addEvent(getNowTime() + " 多單停利:" + input + " "
							+ abscurrent + "口");*/
				}
			} else {
				if (!up) {
					if (!((SGXTWGap() - 0.0012) > 0)) {
						if (input <= price[0]) { // 多方停損
							current = 0;
							writetxt(input);
							for (int i = 0; i < abscurrent; i++) {
								lost = lost + (price[i] - input);
								total = total - (price[i] - input);
							}
							init();
							txt.setOutput(getNowTime() + " BUYL! cost:" + input
									+ " " + abscurrent + "口");
							/*if (isGtalkOn)
							g.alert(botname, Email, getNowTime() + " 多單停損:"
									+ input + " " + abscurrent + "口");
							if (isFBOn)
							f.alert(botname, getNowTime() + " 多單停損:"
									+ input + " " + abscurrent + "口");
							if (isCalOn)
							cal.addEvent(getNowTime() + " 多單停損:" + input + " "
									+ abscurrent + "口");*/
						} else {
							current = 0;
							writetxt(input);
							for (int i = 0; i < abscurrent; i++) {
								win = win + (input - price[i]);
								total = total + (input - price[i]);
							}
							init();
							txt.setOutput(getNowTime() + " BUYW! cost:" + input
									+ " " + abscurrent + "口");
							/*if (isGtalkOn)
							g.alert(botname, Email, getNowTime() + " 多單停利:"
									+ input + " " + abscurrent + "口");
							if (isFBOn)
							f.alert(botname, getNowTime() + " 多單停利:"
									+ input + " " + abscurrent + "口");
							if (isCalOn)
							cal.addEvent(getNowTime() + " 多單停利:" + input + " "
									+ abscurrent + "口");*/
						}
					}
				} else {
					if ((SGXTWGap() - 0.0014) > 0) {
						if (Math.abs(current) < tsize1) {
							int newcurrent = current + (1 * currentmulti);
							for (int i = Math.abs(current); i < Math
									.abs(newcurrent); i++)
								price[i] = input;
							current = newcurrent;
							abscurrent = Math.abs(current);
							writetxt(0);
							txt.setOutput(getNowTime() + " max4:" + input + " "
									+ abscurrent + "口");
							// range += 2;
							/*if (isGtalkOn)
							g.alert(botname, Email, getNowTime() + " 多單加碼:"
									+ input + " " + abscurrent + "口");
							if (isFBOn)
							f.alert(botname, getNowTime() + " 多單加碼:"
									+ input + " " + abscurrent + "口");
							if (isCalOn)
							cal.addEvent(getNowTime() + " 多單加碼:" + input + " "
									+ abscurrent + "口");*/
							hlflag = 0;
						}
					}
				}
			}
		} else if (current < 0) {
			if ((input >= (lowo + GapvoloutH)) && hlflag == -1 && (SGXTWGap() + SGXGap56) > 0) { // 判斷是否低點回檔
				if (input >= price[0]) { // 空方停損
					current = 0;
					writetxt(input);
					for (int i = 0; i < abscurrent; i++) {
						lost = lost + (input - price[i]);
						total = total - (input - price[i]);
					}
					init();
					txt.setOutput(getNowTime() + " SELL1L! cost:" + input + " "
							+ abscurrent + "口");
					/*if (isGtalkOn)
					g.alert(botname, Email, getNowTime() + " 空單停損:" + input
							+ " " + abscurrent + "口");
					if (isFBOn)
					f.alert(botname, getNowTime() + " 空單停損:" + input + " "
							+ abscurrent + "口");
					if (isCalOn)
					cal.addEvent(getNowTime() + " 空單停損:" + input + " "
							+ abscurrent + "口");*/
				} else {
					current = 0;
					writetxt(input);
					for (int i = 0; i < abscurrent; i++) {
						win = win + (price[i] - input);
						total = total + (price[i] - input);
					}
					init();
					txt.setOutput(getNowTime() + " SELL1! cost:" + input + " "
							+ abscurrent + "口");
					/*if (isGtalkOn)
					g.alert(botname, Email, getNowTime() + " 空單停利:" + input
							+ " " + abscurrent + "口");
					if (isFBOn)
					f.alert(botname, getNowTime() + " 空單停利:" + input + " "
							+ abscurrent + "口");
					if (isCalOn)
					cal.addEvent(getNowTime() + " 空單停利:" + input + " "
							+ abscurrent + "口");*/
				}
			} else {
				if (up) {
					if (!((SGXTWGap() + 0.0012) < 0)) {
						if (input >= price[0]) { // 空方停損
							current = 0;
							writetxt(input);
							for (int i = 0; i < abscurrent; i++) {
								lost = lost + (input - price[i]);
								total = total - (input - price[i]);
							}
							init();
							txt.setOutput(getNowTime() + " SELLL! cost:"
									+ input + " " + abscurrent + "口");
							/*if (isGtalkOn)
							g.alert(botname, Email, getNowTime() + " 空單停損:"
									+ input + " " + abscurrent + "口");
							if (isFBOn)
							f.alert(botname, getNowTime() + " 空單停損:"
									+ input + " " + abscurrent + "口");
							if (isCalOn)
							cal.addEvent(getNowTime() + " 空單停損:" + input + " "
									+ abscurrent + "口");*/
						} else {
							current = 0;
							writetxt(input);
							for (int i = 0; i < abscurrent; i++) {
								win = win + (price[i] - input);
								total = total + (price[i] - input);
							}
							init();
							txt.setOutput(getNowTime() + " SELLW! cost:"
									+ input + " " + abscurrent + "口");
							/*if (isGtalkOn)
							g.alert(botname, Email, getNowTime() + " 空單停利:"
									+ input + " " + abscurrent + "口");
							if (isFBOn)
							f.alert(botname, getNowTime() + " 空單停利:"
									+ input + " " + abscurrent + "口");
							if (isCalOn)
							cal.addEvent(getNowTime() + " 空單停利:" + input + " "
									+ abscurrent + "口");*/
						}
					}
				} else {
					if ((SGXTWGap() + 0.0014) < 0) {
						if (Math.abs(current) < tsize1) {
							int newcurrent = current + (-1 * currentmulti);
							for (int i = Math.abs(current); i < Math
									.abs(newcurrent); i++)
								price[i] = input;
							current = newcurrent;
							abscurrent = Math.abs(current);
							writetxt(0);
							txt.setOutput(getNowTime() + " min4:" + input + " "
									+ abscurrent + "口");
							// range += 2;
							/*if (isGtalkOn)
							g.alert(botname, Email, getNowTime() + " 空單加碼:"
									+ input + " " + abscurrent + "口");
							if (isFBOn)
							f.alert(botname, getNowTime() + " 空單加碼:"
									+ input + " " + abscurrent + "口");
							if (isCalOn)
							cal.addEvent(getNowTime() + " 空單加碼:" + input + " "
									+ abscurrent + "口");*/
							hlflag = 0;
						}
					}
				}
			}
		}
	}

	private void checkneg(int input) {
		if (current != 0) {
			if (current > 0) {
				if (SGXTWGap() > 0)
					sval = svalA + (int) (SGXTWGap() / 0.0012) * 5;
				if (!(SGXTWGap() > 0.00248)) {
					if (input <= (price[abscurrent - 1] - sval)) { // 多方停損
						current = 0;
						writetxt(input);
						for (int i = 0; i < abscurrent; i++) {
							if (input > price[i]) {
								win = win + (input - price[i]);
								total = total + (input - price[i]);
							} else {
								lost = lost + (price[i] - input);
								total = total - (price[i] - input);
							}
						}
						init();
						txt.setOutput(getNowTime() + " BUYLN! cost:" + input
								+ " " + abscurrent + "口");
						/*if (isGtalkOn)
						g.alert(botname, Email, getNowTime() + " 多單停損:" + input
								+ " " + abscurrent + "口");
						if (isFBOn)
						f.alert(botname, getNowTime() + " 多單停損:" + input
								+ " " + abscurrent + "口");
						if (isCalOn)
						cal.addEvent(getNowTime() + " 多單停損:" + input + " "
								+ abscurrent + "口");*/
						sval = 30;
					}
				} else if (input <= (price[abscurrent - 1] - svalT)) { // 多方停損
					int wint = 0;
					int lostt = 0;
					int totalt = total;
					for (int i = 0; i < abscurrent; i++) {
						if (input > price[i]) {
							wint = wint + (input - price[i]);
							totalt = totalt + (input - price[i]);
						} else {
							lostt = lostt + (price[i] - input);
							totalt = totalt - (price[i] - input);
						}
					}
					if (totalt <= negarrive) {
						current = 0;
						writetxt(input);
						for (int i = 0; i < abscurrent; i++) {
							if (input > price[i]) {
								win = win + (input - price[i]);
								total = total + (input - price[i]);
							} else {
								lost = lost + (price[i] - input);
								total = total - (price[i] - input);
							}
						}
						init();
						txt.setOutput(getNowTime() + " BUYL80! cost:" + input
								+ " " + abscurrent + "口");
						/*if (isGtalkOn)
						g.alert(botname, Email, getNowTime() + " 多單停損:" + input
								+ " " + abscurrent + "口");
						if (isFBOn)
						f.alert(botname, getNowTime() + " 多單停損:" + input
								+ " " + abscurrent + "口");
						if (isCalOn)
						cal.addEvent(getNowTime() + " 多單停損:" + input + " "
								+ abscurrent + "口");*/
						sval = 30;
						runflag = false;
					}
				}
			} else {
				if (SGXTWGap() < 0)
					sval = svalA + (int) (SGXTWGap() / -0.0012) * 5;
				if (!(SGXTWGap() < -0.00248)) {
					if (input >= (price[abscurrent - 1] + sval)) { // 空方停損
						current = 0;
						writetxt(input);
						for (int i = 0; i < abscurrent; i++) {
							if (input < price[i]) {
								win = win + (price[i] - input);
								total = total + (price[i] - input);
							} else {
								lost = lost + (input - price[i]);
								total = total - (input - price[i]);
							}
						}
						init();
						txt.setOutput(getNowTime() + " SELLLN! cost:" + input
								+ " " + abscurrent + "口");
						/*if (isGtalkOn)
						g.alert(botname, Email, getNowTime() + " 空單停損:" + input
								+ " " + abscurrent + "口");
						if (isFBOn)
						f.alert(botname, getNowTime() + " 空單停損:" + input
								+ " " + abscurrent + "口");
						if (isCalOn)
						cal.addEvent(getNowTime() + " 空單停損:" + input + " "
								+ abscurrent + "口");*/
						sval = 30;
					}
				} else if (input >= (price[abscurrent - 1] + svalT)) { // 空方停損
					int wint = 0;
					int lostt = 0;
					int totalt = total;
					for (int i = 0; i < abscurrent; i++) {
						if (input < price[i]) {
							wint = wint + (price[i] - input);
							totalt = totalt + (price[i] - input);
						} else {
							lostt = lostt + (input - price[i]);
							totalt = totalt - (input - price[i]);
						}
					}
					if (totalt <= negarrive) {
						current = 0;
						writetxt(input);
						for (int i = 0; i < abscurrent; i++) {
							if (input < price[i]) {
								win = win + (price[i] - input);
								total = total + (price[i] - input);
							} else {
								lost = lost + (input - price[i]);
								total = total - (input - price[i]);
							}
						}
						init();
						txt.setOutput(getNowTime() + " SELLL80! cost:" + input
								+ " " + abscurrent + "口");
						/*if (isGtalkOn)
						g.alert(botname, Email, getNowTime() + " 空單停損:" + input
								+ " " + abscurrent + "口");
						if (isFBOn)
						f.alert(botname, getNowTime() + " 空單停損:" + input
								+ " " + abscurrent + "口");
						if (isCalOn)
						cal.addEvent(getNowTime() + " 空單停損:" + input + " "
								+ abscurrent + "口");*/
						sval = 30;
						runflag = false;
					}
				}
			}
		}
	}

	private void checkTotal(int input) {
		if (total <= -30)
			tsize1 = 1;
		if (tsize1 > tsize)
			tsize1 = tsize;
		if (total <= negarrive) {
			txt.setOutput("Win:" + win);
			txt.setOutput("Lost:" + lost);
			txt.setOutput("Total:" + total);
			txt.setOutput("Negarrive have arrived " + negarrive + "!");
			runflag = false;
		}
	}

	void check_runtime() {
		java.util.Date now = new java.util.Date(); // 取得現在時間
		SimpleDateFormat sf = new SimpleDateFormat("HH:mm:ss E",
				java.util.Locale.TAIWAN);
		String sGMT = sf.format(now);
		int hour = Integer.valueOf(sGMT.substring(0, 2)).intValue();
		int min = Integer.valueOf(sGMT.substring(3, 5)).intValue();
		int sec = Integer.valueOf(sGMT.substring(6, 8)).intValue();
		CheckSGXTime(min);
		if (current != 0) {
			if (hour > 12 && min > 13) {
				if (close) {
					clear();
					runflag = false;
				}
				intoflag = false;
			}
			if (hour > 12 && min > 28) {
				clear();
				runflag = false;
			}
		} else {
			if (hour > 12 && min > 0 && close) {
				runflag = false;
				intoflag = false;
			}
			if (hour > 12 && min > 15) {
				runflag = false;
				intoflag = false;
			}
		}
		if (hour > 7 && min > 44 && sec > 30 && !isOpen) {
			isOpen = true;
			if (SGXindex != 0)
				runflag = true;
			else
				runflag = false;
		}
	}

	private String getNowTime() {
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy/MM/dd HH:mm:ss");
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8"));
		java.util.Date date = new java.util.Date();
		String datetime = dateFormat.format(date);
		return datetime;
	}

	private void clear() {
		if (current != 0) {
			if (current >= 1) {
				for (int i = 0; i < abscurrent; i++) {
					if (inputt > price[i]) {
						win = win + (inputt - price[i]);
						total = total + (inputt - price[i]);
						txt.setOutput(getNowTime() + " BUYCW! cost:" + inputt);
						/*if (isGtalkOn)
						g.alert(botname, Email, getNowTime() + " 多單停利:"
								+ inputt + " " + abscurrent + "口");
						if (isFBOn)
						f.alert(botname, getNowTime() + " 多單停利:" + inputt
								+ " " + abscurrent + "口");
						if (isCalOn)
						cal.addEvent(getNowTime() + " 多單停利:" + inputt + " "
								+ abscurrent + "口");*/
					} else {
						lost = lost + (price[i] - inputt);
						total = total - (price[i] - inputt);
						txt.setOutput(getNowTime() + " BUYCL! cost:" + inputt);
						/*if (isGtalkOn)
						g.alert(botname, Email, getNowTime() + " 多單停損:"
								+ inputt + " " + abscurrent + "口");
						if (isFBOn)
						f.alert(botname, getNowTime() + " 多單停損:" + inputt
								+ " " + abscurrent + "口");
						if (isCalOn)
						cal.addEvent(getNowTime() + " 多單停損:" + inputt + " "
								+ abscurrent + "口");*/
					}
				}
				current = 0;
				writetxt(inputt);
				queue.clear();
				abscurrent = Math.abs(current);
			} else {
				for (int i = 0; i < abscurrent; i++) {
					if (inputt < price[i]) {
						win = win + (price[i] - inputt);
						total = total + (price[i] - inputt);
						txt.setOutput(getNowTime() + " SELLCW! cost:" + inputt);
						/*if (isGtalkOn)
						g.alert(botname, Email, getNowTime() + " 空單停利:"
								+ inputt + " " + abscurrent + "口");
						if (isFBOn)
						f.alert(botname, getNowTime() + " 空單停利:" + inputt
								+ " " + abscurrent + "口");
						if (isCalOn)
						cal.addEvent(getNowTime() + " 空單停利:" + inputt + " "
								+ abscurrent + "口");*/
					} else {
						lost = lost + (inputt - price[i]);
						total = total - (inputt - price[i]);
						txt.setOutput(getNowTime() + " SELLCL! cost:" + inputt);
						/*if (isGtalkOn)
						g.alert(botname, Email, getNowTime() + " 空單停損:"
								+ inputt + " " + abscurrent + "口");
						if (isFBOn)
						f.alert(botname, getNowTime() + " 空單停損:" + inputt
								+ " " + abscurrent + "口");
						if (isCalOn)
						cal.addEvent(getNowTime() + " 空單停損:" + inputt + " "
								+ abscurrent + "口");*/
					}
				}
				current = 0;
				writetxt(inputt);
				queue.clear();
				abscurrent = Math.abs(current);
			}
		}
		runflag = false;
	}

	public void close() {
		fu.setPreSettle(inputt);
		sgx.setOpenindex(SGXindex);
		sgx.addGap(SGXTWGap());
		try {
			FileOutputStream fos = new FileOutputStream("C:\\Dropbox\\Fu.ser");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(fu);
			oos.close();

			fos = new FileOutputStream("C:\\Dropbox\\SGX.ser");
			oos = new ObjectOutputStream(fos);
			oos.writeObject(sgx);
			oos.close();
		} catch (Exception ex) {
			System.out.println("Exception thrown during writing Options: "
					+ ex.toString());
		}
		/*if (isGtalkOn)
		g.alert(botname, Email, getNowTime() + " Trading System Stop!!");
		if (isFBOn)
		f.alert(botname, fb, getNowTime() + " Trading System Stop!!");*/
		txt.setOutput("Win:" + win);
		txt.setOutput("Lost:" + lost);
		txt.setOutput("Total:" + total);
		txt.close();
	}

	private Integer[] fhw(Object in[]) {
		int size = in.length;
		Integer[] tmp = new Integer[size / 2];

		for (int i = 0; i < size; i += 2) {
			tmp[i / 2] = ((Integer) in[i] + (Integer) in[i + 1]) / 2;
		}
		return tmp;
	}

	private Integer[] dwt(Object in[]) {
		Integer[] temp = null;
		switch (size) {
		case 1600:
			temp = fhw(fhw(fhw(fhw(fhw(fhw(in))))));
			break;
		case 800:
			temp = fhw(fhw(fhw(fhw(fhw(in)))));
			break;
		case 400:
			temp = fhw(fhw(fhw(fhw(in))));
			break;
		case 200:
			temp = fhw(fhw(fhw(in)));
			break;
		case 100:
			temp = fhw(fhw(in));
			break;
		}
		return temp;
	}

	private void init() {
		abscurrent = 0;
		higho = 0;
		lowo = 0;
		hlflag = 0;
		lowMFlag = 0;
		sval = 30;
		wval = 30;
	}

	private double SGXTWGap(){
		return SGXpercent - nowpercent;
	}

	private double getHighLowPro(boolean hlflag) {
		double p1;
		double p2 = new Double(price[abscurrent - 1]).doubleValue();
		if (hlflag) {
			p1 = new Double(higho).doubleValue();
			p1 = p1 - p2;
		} else {
			p1 = new Double(lowo).doubleValue();
			p1 = p2 - p1;
		}
		return (p1 / p2);
	}
	
	private void CheckSGXTime(int mm){
		if (SGXTime != null) {
			int min = Integer.valueOf(SGXTime.substring(3, 5)).intValue();
			if (mm > (min + 1) && runflag) {
				/*if (isGtalkOn)
				g.alert(botname, Email, getNowTime() + " futuresbot SGXIndex Stop Error!!");
				if (isFBOn)
				f.alert(botname, fb, getNowTime() + " futuresbot SGXIndex Stop Error!!");*/
				System.out.println("futuresbot SGXIndex Stop Error!!");
				runflag = false;
			}
		}
	}
	
	private void setSGXTime(){
		java.util.Date now = new java.util.Date(); // 取得現在時間
		SimpleDateFormat sf = new SimpleDateFormat("HH:mm:ss E",
				java.util.Locale.TAIWAN);
		SGXTime = sf.format(now);
	}
	
	private void setRange(int range) {
		this.range = range;
	}

	private void setSGXGapA(double sGXGapA) {
		SGXGapA = sGXGapA;
	}
	
	private void setSGXGap34(double sGXGap34) {
		SGXGap34 = sGXGap34;
	}

	private void setSGXGapB(double sGXGapB) {
		SGXGapB = sGXGapB;
	}

	private void setSGXGapL(double sGXGapL) {
		SGXGapL = sGXGapL;
	}

	private void setSGXGapLin(double sGXGapLin) {
		SGXGapLin = sGXGapLin;
	}
	
	private void setOffset(double sOffset) {
		Offset = sOffset;
	}
	
	private void setOIGap(double sOIGap) {
		OIGap = sOIGap;
	}
}