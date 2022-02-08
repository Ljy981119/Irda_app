package com.example.myapplication.utils;

import java.io.IOException;
import java.io.InputStream;

import tw.com.prolific.driver.pl2303.PL2303Driver;

/**
 * Created by wly on 2018/4/17.
 */
public class HexConvert {
	public PL2303Driver mSerial;
	public static String convertStringToHex(String str) {

		char[] chars = str.toCharArray();

		StringBuffer hex = new StringBuffer();
		for (int i = 0; i < chars.length; i++) {
			hex.append(Integer.toHexString((int) chars[i]));
		}

		return hex.toString();
	}

	public static String convertHexToString(String hex) {

		StringBuilder sb = new StringBuilder();
		StringBuilder sb2 = new StringBuilder();

		for (int i = 0; i < hex.length() - 1; i += 2) {

			String s = hex.substring(i, (i + 2));
			int decimal = Integer.parseInt(s, 16);
			sb.append((char) decimal);
			sb2.append(decimal);
		}

		return sb.toString();
	}

	public static byte[] hexStringToBytes(String hexString) {
		if (hexString == null || hexString.equals("")) {
			return null;
		}
		// toUpperCase将字符串中的所有字符转换为大写
		hexString = hexString.toUpperCase();
		int length = hexString.length() / 2;
		// toCharArray将此字符串转换为一个新的字符数组。
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
		}
		return d;
	}

	//返回匹配字符
	private static byte charToByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}

	//将字节数组转换为short类型，即统计字符串长度
	public static short bytes2Short2(byte[] b) {
		short i = (short) (((b[1] & 0xff) << 8) | b[0] & 0xff);
		return i;
	}

	//将字节数组转换为16进制字符串
	public static String BinaryToHexString(byte[] bytes) {
		String hexStr = "0123456789ABCDEF";
		String result = "";
		String hex = "";
		for (byte b : bytes) {
			hex = String.valueOf(hexStr.charAt((b & 0xF0) >> 4));
			hex += String.valueOf(hexStr.charAt(b & 0x0F));
			result += hex + " ";
		}
		return result;
	}

	//String转10进制ASCII
	public static String str2Dec(String str) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			// 第二个参数10表示10进制
			sb.append(Integer.toString(c, 10));
			// 或者省略第二个参数，默认为10进制
			// sb.append(Integer.toString(c));
		}
		return sb.toString();
	}

	//10进制ASCII转String
	public static String dec2Str(String ascii) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < ascii.length() - 1; i += 2) {
			String h = ascii.substring(i, (i + 2));
			// 这里第二个参数传10表10进制
			int decimal = Integer.parseInt(h, 10);
			sb.append((char) decimal);
		}
		return sb.toString();
	}

	//  就是一个明文字符生成两个字符表示的16进制ASCII码
	public static String str2Hex(String str) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			// 这里的第二个参数16表示十六进制
			sb.append(Integer.toString(c, 16));
			// 或用toHexString方法直接转成16进制
			// sb.append(Integer.toHexString(c));
		}
		return sb.toString();
	}


	//16进制ASCII码解析成一个明文字符
	public static String hex2Str(String hex) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < hex.length() - 1; i += 2) {
			String h = hex.substring(i, (i + 2));
			int decimal = Integer.parseInt(h, 16);
			sb.append((char) decimal);
		}
		return sb.toString();
	}

	public static int SumStrAscii(String str){
		byte[] bytestr = str.getBytes();
		int sum = 0;
		for(int i=0;i<bytestr.length;i++){
			sum += bytestr[i];
		}
		return sum;
	}

	public static String spliceStrAscii(String str) {
		byte[] bytestr = str.getBytes();
		String result = "";
		for (int i = 0; i < bytestr.length; i++) {
			result += bytestr[i];
		}
		return result;
	}


	/**

	 * 16进制直接转换成为字符串

	 * @explain

	 * @param

	 * @return String (字符集：UTF-8)

	 */

	public static String fromHexString(String hexString) throws Exception {
// 用于接收转换结果

		String result = "";

// 转大写

		hexString = hexString.toUpperCase();

// 16进制字符

		String hexDigital = "0123456789ABCDEF";

// 将16进制字符串转换成char数组

		char[] hexs = hexString.toCharArray();

// 能被16整除，肯定可以被2整除

		byte[] bytes = new byte[hexString.length() / 2];

		int n;

		for (int i = 0; i < bytes.length; i++) {
			n = hexDigital.indexOf(hexs[2 * i]) * 16 + hexDigital.indexOf(hexs[2 * i + 1]);

			bytes[i] = (byte) (n & 0xff);

		}

// byte[]-->String

		result = new String(bytes, "UTF-8");

		return result;

	}



	/**

	 * 字符串转换成为16进制字符串(大写)

	 * @explain 因为java转义字符串在java中有着特殊的意义，

	 * 所以当字符串中包含转义字符串，并将其转换成16进制后，16进制再转成String时，会出问题：

	 * java会将其当做转义字符串所代表的含义解析出来

	 * @param str 字符串(去除java转义字符)

	 * @return 16进制字符串

	 * @throws Exception

	 */

	public static String toHexString(String str) throws Exception {
// 用于接收转换结果

		String hexString = "";

// 1.校验是否包含特殊字符内容

// java特殊转义符

// String[] escapeArray = {"\b","\t","\n","\f","\r","\'","\"","\\"};

		String[] escapeArray = {"\b","\t","\n","\f","\r"};

// 用于校验参数是否包含特殊转义符

		boolean flag = false;

// 迭代

		for (String esacapeStr : escapeArray) {
// 一真则真

			if (str.contains(esacapeStr)) {
				flag = true;

				break;// 终止循环

			}

		}

// 包含特殊字符

		if (flag) throw new Exception("参数字符串不能包含转义字符！");

// 16进制字符

		char[] hexArray = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};

		StringBuilder sb = new StringBuilder();

// String-->byte[]

		byte[] bs = str.getBytes();

		int bit;

		for (int i = 0; i < bs.length; i++) {
			bit = (bs[i] & 0x0f0) >> 4;

			sb.append(hexArray[bit]);

			bit = bs[i] & 0x0f;

			sb.append(hexArray[bit]);

		}

		hexString = sb.toString();

		return hexString;

	}

	/**
	 * 从串口读取数据
	 *
	 * @param mSerial
	 *            当前已建立连接的SerialPort对象
	 * @return 读取到的数据
	 */
	public static String readFromWay(PL2303Driver mSerial) {

		byte[] bytes = {};
		String receiveData ="";
		try {

			// 缓冲区大小为一个字节
			byte[] readBuffer = new byte[1];
			int bytesNum = mSerial.read(readBuffer);
			while (bytesNum > 0) {
				bytes = ArrayUtils.concat(bytes, readBuffer);
				bytesNum = mSerial.read(readBuffer);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			receiveData = ByteUtils.byteArrayToHexString(bytes);
		}
		return receiveData;
	}

	/***

	 * @explain

	 *@paramargs

	 *@throwsException*/

	public static void main(String[] args) throws Exception{

		String str = "[01]";

		String hexStr=HexConvert.toHexString(str);

		System.out.println(hexStr);

		System.out.println(HexConvert.fromHexString(hexStr));

	}


//
////// 结果为656667686970
////System.out.println(str2Dec("ABCDEF"));
//
//    public static void main(String[] args) {
//
////        String str=HexConvert.str2Dec("[01]");
////        System.out.println("======十进制码转换为ASCII进制======");
////
////        System.out.println("字符串: " + str);
////
////        String str1=HexConvert.dec2Str(str);
////
////        System.out.println("======ASCII码转换为十进制======");
////
////        System.out.println("字符串: " + str1);
//
//
//
//
//
//        System.out.println("======ASCII码转换为16进制======");
//       String str = "POS88884";
//        System.out.println("字符串: " + str);
//          String str1=HexConvert.spliceStrAscii(str);
//       System.out.println("======ASCII码转换为十进制======"+str1);
//
//
//        String hex = HexConvert.convertStringToHex(str);
//        System.out.println("====转换为16进制=====" + hex);
//
//        System.out.println("======16进制转换为ASCII======");
//        System.out.println("Hex : " + hex);
//        System.out.println("ASCII : " + HexConvert.convertHexToString(hex));
//
//        byte[] bytes = HexConvert.hexStringToBytes(hex);
//
//        System.out.println(HexConvert.BinaryToHexString( bytes ));
//    }
}