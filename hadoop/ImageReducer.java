import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;

public class ImageReducer extends MapReduceBase
			implements Reducer<IntWritable, ImageWritable, IntWritable, ImageWritable> {

	@Override
	public void reduce(IntWritable key, Iterator<ImageWritable> itr,
			OutputCollector<IntWritable, ImageWritable> collector, Reporter reporter)
			throws IOException {
		//collector.collect(key, null);
		//collector.collect(key, itr.next());
		while (itr.hasNext()) {
			ImageWritable writable = itr.next();
			
			collector.collect(key, writable);
		}
	}
}
