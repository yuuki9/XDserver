package com.egis.xdserver.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author 강민아
 * @date 2022. 6. 2.
 * 
 */
public class FormatConvert {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private static boolean _debug = false;
	private static String _header = "XDServer";
	
	public static String makeJSON(String key, String value) {
		return String.format("\"%s\":\"%s\"", key,value);
	}
	public static String makeJSON(String key, int value) {
		return String.format("\"%s\":%s", key,value);
	}
	public static String makeJSON(String key, double value) {
		return String.format("\"%s\":%s", key,value);
	}
	
	public static byte[] intToByteArray(int value) {
	    byte[] bytes = new byte[4];
	    ByteBuffer.wrap(bytes).putInt(value);
	    return bytes;
	}
	
	public static byte[] doubleToByteArray(double value) {
	    byte[] bytes = new byte[8];
	    ByteBuffer.wrap(bytes).putDouble(value);
	    return bytes;
	}

	public static double toDouble(byte[] bytes) {
	    return ByteBuffer.wrap(bytes).getDouble();
	}
	public static double toInt(byte[] bytes) {
	    return ByteBuffer.wrap(bytes).getInt();
	}
	
	public static byte[] intToByte(int integer, ByteOrder order) {
		 
		ByteBuffer buff = ByteBuffer.allocate(Integer.SIZE/8);
		buff.order(order);

		// 인수로 넘어온 integer을 putInt로설정
		buff.putInt(integer);
 
		return buff.array();
	}
	
	/**
	 * byte배열을 int형로 바꿈<br>
	 * @param bytes
	 * @param order
	 * @return
	 */
	public static int byteToInt(byte[] bytes, ByteOrder order) {
 
		ByteBuffer buff = ByteBuffer.allocate(Integer.SIZE/8);
		buff.order(order);
 
		// buff사이즈는 4인 상태임
		// bytes를 put하면 position과 limit는 같은 위치가 됨.
		buff.put(bytes);
		// flip()가 실행 되면 position은 0에 위치 하게 됨.
		buff.flip();
 
		return buff.getInt(); // position위치(0)에서 부터 4바이트를 int로 변경하여 반환
	}
	
	// 16진수 문자열 (Hex String)을 byte 배열로 변환
	public static byte[] hexToBytes(String hex) {
		byte[] result = null;
		if (hex != null) {
			result = new byte[hex.length() / 2];
			for (int i = 0; i < result.length; i++) {
				result[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
			}
		}

		return result;
	}

	// byte 배열을 16진수 문자열로 변환
	public static String asHex(byte[] bytes) {
		StringBuffer sb = new StringBuffer(bytes.length * 2);
		for (int i = 0; i < bytes.length; i++) {
			if (((int) bytes[i] & 0xff) < 0x10) {
				sb.append('0');
			}

			sb.append(Integer.toString(bytes[i] & 0xff, 16)+" "); // 문자 자르기 위함 공백 추가
		}

		return sb.toString();
	}
	
	/**
	 * 바이너리 -> base64 변환
	 */
	public String getBinarytoBase64(String buf) throws IOException  {
		
		byte[] encoded = Base64.getEncoder().encode(new String(buf).getBytes());
		logger.info("Converted Base64 from String: "+new String(encoded)); 
		
		return new String(encoded);
	}
	
	/**
	 * FileUtil getFileBytes
	 **/
	public byte[] getFileBytes(File file, int buffSize) throws IOException  {
	    ByteArrayOutputStream ous = null;
	    InputStream ios = null;
	    byte[] buffer = new byte[buffSize];
	    try {
	        ous = new ByteArrayOutputStream();
	        try {
	        ios = new FileInputStream(file);
	        }catch(IOException e) {
	        	e.printStackTrace();
	        	logger.info("0237 : getFileBytes : IOException : FileInputStream : new");
	        }
	        int read = 0;
	        try {
	        while ((read = ios.read(buffer)) != -1)
	            ous.write(buffer, 0, read);
	        }catch(IOException e) {
	        	logger.info("0243 : getFileBytes : IOException : ByteArrayOutputStream : write()");
	        }
	    } finally {
	        try { if (ous != null) ous.close();} catch (IOException e) {//e.printStackTrace();
	        	logger.info("0248 : getFileBytes : IOException : ByteArrayOutputStream : close()");
	        }
	        try { if (ios != null) ios.close();} catch (IOException e) {//e.printStackTrace();
	        	logger.info("0252 : getFileBytes : IOException : FileInputStream : close()");
	        }
	    }
	    ios.close();
	    ous.close();
	    
	    byte[] res =  ous.toByteArray();
	    buffer = null; ios = null; ous=null;
	    return res;
	}
	
	public int getU32(byte[]bytes, int sp,ByteOrder endian) {
		byte [] dest = getByteArray(bytes,sp,4);
		
		int res = byteArrayToInt(dest,endian);
		dest = null;
		return res;
	}
	
	public byte[] getByteArray(byte[] org, int pos, int len) {
		byte[] dest = new byte[len];
		if(org.length<len+pos) {
			logger.info(String.format("NG : getByteArray : length : %d : %d : %d",org.length,pos,len));
			return null;
		}
		System.arraycopy(org, pos, dest, 0, len);
		return dest;
	}

	//=========================================================
	
	/**
     * 바이너리 바이트 배열을 스트링으로 변환
     * 
     * @param b
     * @return
     */
    public String byteArrayToBinaryString(byte[] b) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < b.length; ++i) {
            sb.append(byteToBinaryString(b[i]));
        }
        return sb.toString();
    }
 
    /**
     * 바이너리 바이트를 스트링으로 변환
     * 
     * @param n
     * @return
     */
    public String byteToBinaryString(byte n) {
        StringBuilder sb = new StringBuilder("00000000");
        for (int bit = 0; bit < 8; bit++) {
            if (((n >> bit) & 1) > 0) {
                sb.setCharAt(7 - bit, '1');
            }
        }
        return sb.toString();
    }
 
    /**
     * 바이너리 스트링을 바이트배열로 변환
     * 
     * @param s
     * @return
     */
    public byte[] binaryStringToByteArray(String s) {
        int count = s.length() / 8;
        byte[] b = new byte[count];
        for (int i = 1; i < count; ++i) {
            String t = s.substring((i - 1) * 8, i * 8);
            b[i - 1] = binaryStringToByte(t);
        }
        return b;
    }
 
    /**
     * 바이너리 스트링을 바이트로 변환
     * 
     * @param s
     * @return
     */
    public byte binaryStringToByte(String s) {
        byte ret = 0, total = 0;
        for (int i = 0; i < 8; ++i) {
            ret = (s.charAt(7 - i) == '1') ? (byte) (1 << i) : 0;
            total = (byte) (ret | total);
        }
        return total;
    }
	
	//=========================================================
	public static final short byteArrayToShort(byte[] bytes, ByteOrder endian) {
	    return ByteBuffer.wrap(bytes).order(endian).getShort();
	}

	public static final int byteArrayToInt(byte[] bytes, ByteOrder endian) {
	    return ByteBuffer.wrap(bytes).order(endian).getInt();
	}

	public static final float byteArrayToFloat(byte[] bytes, ByteOrder endian) {
	    return ByteBuffer.wrap(bytes).order(endian).getFloat();
	}
	
	public static final long byteArrayToLong(byte[] bytes, ByteOrder endian) {
	    return ByteBuffer.wrap(bytes).order(endian).getLong();
	}

	public static double byteArrayToDouble(byte[] bytes, ByteOrder endian) {
	    return ByteBuffer.wrap(bytes).order(endian).getDouble();
	}
	
	public static byte[] intToByteArray(int value, ByteOrder endian) {
		return ByteBuffer.allocate(4).order(endian).putInt(value).array();
	}
	
	public static byte[] doubleToByteArray(double value, ByteOrder endian) {
		return ByteBuffer.allocate(8).order(endian).putDouble(value).array();
	}
	//=========================================================
	
	public  void debugFormat(String format, Object ... args){
    	if(_debug)outFormat(format,args);
    }
	
	public  void outFormat(String format, Object ... args) {
		out(String.format(format,args));
	}
	
	public  void out(String msg){
		Date dt = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss"); 
		logger.info(String.format("[%s %s] %s",_header,sdf.format(dt).toString(),msg));
    }
	
	public  void infoFormat(String format, Object ... args){
    	outFormat("[INFO] "+format,args);
    }
	
	public  void errorFormat(String format, Object ... args){
    	outFormat("[ERROR] "+format,args);
    }
}
