import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Writable;


public class ImageWritable implements Writable{

	int width;
	int height;
	byte[] bytes;
	int startRow;
	int numRows;
	
	public ImageWritable() {

	}
	
	@Override
	public void readFields(DataInput in) throws IOException {
		width = in.readInt();
		height = in.readInt();
		byte[] b = new byte[height * width];
		in.readFully(b);
		startRow = in.readInt();
		numRows = in.readInt();
	}

	@Override
	public void write(DataOutput out) throws IOException {
		out.write(width);
		out.write(height);
		out.write(bytes);
		out.write(startRow);
		out.write(numRows);
	}
	
	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public byte[] getBytes() {
		return bytes;
	}

	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}

	public int getStartRow() {
		return startRow;
	}
	
	public void setStartRow(int startRow) {
		this.startRow = startRow;
	}
	
	public int getNumRows() {
		return numRows;
	}

	public void setNumRows(int numRows) {
		this.numRows = numRows;
	}
}
