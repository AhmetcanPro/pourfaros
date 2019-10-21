package server;

import tools.Tools;

public class MsgReader {
	
	private int length;
	private int offset;
	
	private byte[] data;
	
	public MsgReader(byte[] data) {
		this.setLength(length);
		this.offset = 0;
		
		this.data = data;
	}
	
	public short readInt16() {
		short ret = (short)(((data[offset] & 0xff) << 8) + (data[offset + 1] & 0xff));
		offset+=2;
		return ret;
	}
	
	public short readUInt16() {
		if(data.length > (offset + 1)) {
		int firstByte = (0x000000FF & ((int)data[offset]));
        int secondByte = (0x000000FF & ((int)data[offset+1]));
	    offset = offset+2;
	    char anUnsignedShort  = (char) (firstByte << 8 | secondByte);
	    if((short) anUnsignedShort < 0) anUnsignedShort = 0;
	    return (short) anUnsignedShort;
		}
		return 0;
	}
	
	public int readUInt32() {
		if(data.length > (offset + 3)) {
		int firstByte = (0x000000FF & ((int)data[offset]));
        int secondByte = (0x000000FF & ((int)data[offset+1]));
        int thirdByte = (0x000000FF & ((int)data[offset+2]));
        int fourthByte = (0x000000FF & ((int)data[offset+3]));
        offset = offset+4;
	    long anUnsignedInt  = ((long) (firstByte << 24
	                | secondByte << 16
                        | thirdByte << 8
                        | fourthByte))
                       & 0xFFFFFFFFL;
	    if((int) anUnsignedInt < 0) anUnsignedInt = 0;
	    return (int) anUnsignedInt;
		}
		return 0;
	}
	
	public int readUInt8() {
		int toReturn = 0;
		if(data.length > offset) {
		toReturn = data[offset] & 0xFF;
		offset += 1;
		}
		
		if(toReturn < 0) toReturn = 0;
		return toReturn;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}
	
	public byte[] getData() {
		return data;
	}

	public String readString() {
		String c = "";
		for(int a = (int) this.readUInt16(), e, g = 0; g < a; g++) {
			e = (byte) this.readUInt8();
			c += (char) e;
		}
		return Tools.decode_utf8(c);
	}

}
