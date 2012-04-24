import java.io.IOException;
import java.io.InputStream;

import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.RecordReader;
import org.apache.hadoop.util.LineReader;
import org.apache.hadoop.mapred.FileInputFormat;

public class ImageRecordReader implements RecordReader<IntWritable, ImageWritable>{

	JobConf conf;
	ImageSplit split;
	FSDataInputStream inputStream;
	boolean finished = false;
	
	public ImageRecordReader(JobConf conf, ImageSplit split) {
		this.conf = conf;
		this.split = split;
		try {
			FileSystem fileSys = FileInputFormat.getInputPaths(conf)[0].getFileSystem(conf);
			inputStream = fileSys.open(FileInputFormat.getInputPaths(conf)[0]);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			// 			e.printStackTrace();
		}
	}
	
	@Override
	public void close() throws IOException {
		if (inputStream != null)
			inputStream.close();
	}

	@Override
	public IntWritable createKey() {
		return new IntWritable();
	}

	@Override
	public ImageWritable createValue() {
		return new ImageWritable();
	}

	@Override
	public long getPos() throws IOException {
		return split.getOffset();
	}

	@Override
	public float getProgress() throws IOException {
		if (finished)
			return 1;
		return 0;
	}

	@Override
	public boolean next(IntWritable key, ImageWritable value) throws IOException {
		if (finished == false) {
			key.set(split.getOffset() + split.getStartRow()*split.getWidth());
System.out.println("HEIGHTTTTTTTT: " + split.getHeight());
System.out.println("WIDTHHHHHHHHH: " + split.getWidth());
			byte[] bytes = new byte[split.getHeight() * split.getWidth()];
			try {
				inputStream.readFully(split.getOffset(), bytes);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			value.setWidth(split.getWidth());
			value.setHeight(split.getHeight());
			value.setStartRow(split.getStartRow());
			value.setNumRows(split.getNumRows());
			value.setBytes(bytes);
			
			finished = true;
			return true;
		}
		return false;
	}

}
