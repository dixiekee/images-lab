import java.io.IOException;

import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.RecordWriter;
import org.apache.hadoop.util.Progressable;


public class ImageOutputFormat extends FileOutputFormat<Object, Object>{

	@Override
	public RecordWriter getRecordWriter(FileSystem fileSys, JobConf conf,
			String name, Progressable progressable) throws IOException {
		/*Path file = FileOutputFormat.getTaskOutputPath(conf, name);
		FSDataOutputStream out = fileSys.create(file, progressable);
		return new ImageRecordWriter(out);*/
		
		 Path file = FileOutputFormat.getTaskOutputPath(conf, name);
    FileSystem fs = file.getFileSystem(conf);
    FSDataOutputStream fileOut = fs.create(file, progressable);
    return new ImageRecordWriter(fileOut);
	}

}
