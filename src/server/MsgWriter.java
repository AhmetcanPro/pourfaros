package server;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import tools.Tools;

public class MsgWriter {
	
	private int offset;
	private List<Byte> data = new ArrayList<Byte>();
	
	public MsgWriter() {
		this.setOffset(0);
	}
	
	public void writeUInt8(int num) {
		data.add((byte) num);
		setOffset(getOffset() + 1);
	}
	
	public void writeUInt16(short num) {
		byte[] bytes = ByteBuffer.allocate(2).putShort(num).array();
		
		data.add(bytes[0]);
		setOffset(getOffset() + 1);
		data.add(bytes[1]);
		setOffset(getOffset() + 1);
	}

	public void writeUInt32(int num) {
		byte[] bytes = ByteBuffer.allocate(4).putInt(num).array();
		
		data.add(bytes[0]);
		setOffset(getOffset() + 1);
		data.add(bytes[1]);
		setOffset(getOffset() + 1);
		data.add(bytes[2]);
		setOffset(getOffset() + 1);
		data.add(bytes[3]);
		setOffset(getOffset() + 1);
	}
	
	private void writeByte(byte byteData) {
		data.add(byteData);
		setOffset(getOffset() + 1);
	}
	
	public void writeString(String string) {
		string = Tools.encode_utf8(string);
		
		this.writeUInt16((short) (string.length() + 1));
		for(int i = 0; i < string.length();  i++) {
			this.writeByte((byte) string.charAt(i));
		}
		this.writeByte((byte) 'm');
	}
	
	public byte[] getData() {
	    byte ret[] = new byte[data.size()];
	    for (int i = 0; i < data.size(); i++) {
	        ret[i] = data.get(i);
	    }
	    return ret;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

}
