public class ImageHeader {
	int offset; //where the actual image starts
	int height;
	int width;
	
	public ImageHeader(int offset, int height, int width) {
		this.offset = offset;
		this.height = height;
		this.width = width;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}
	
}
