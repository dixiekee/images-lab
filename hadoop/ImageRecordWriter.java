import java.io.DataOutputStream;
import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.RecordWriter;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.TaskAttemptContext;
import org.apache.hadoop.io.BytesWritable;
public class ImageRecordWriter implements RecordWriter<IntWritable, BytesWritable> {

	DataOutputStream out;
	
	public ImageRecordWriter(DataOutputStream out) {
		this.out = out;
	}

	@Override
	public void close(Reporter reporter) throws IOException {
		out.close();		
	}

	@Override
	public void write(IntWritable key, BytesWritable value) throws IOException {
		//out.write((key.get()+"\n").getBytes());
		out.write(value.getBytes(), 0, value.getLength());
	}

}
