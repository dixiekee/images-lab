import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.InputSplit;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.RecordReader;
import org.apache.hadoop.mapred.Reporter;


public class ImageInputFormat extends FileInputFormat<IntWritable, ImageWritable>{

	public ImageSplit[] getSplits(JobConf conf, int numsplits)
	{
		try{
		ArrayList<ImageSplit> splits = new ArrayList<ImageSplit>();
		
		//gets an array of Paths to process
		Path[] paths = ImageInputFormat.getInputPaths(conf);
		for (Path p : paths) {
			//convert Path to a PGMImage
			FileSystem fileSys = p.getFileSystem(conf);
			FSDataInputStream inputStream = fileSys.open(p);
			//read the header to figure out how many ImageSplits to make
			try {
				ImageHeader header = PGMImage.getHeader(inputStream);
				int height = header.getHeight();
				int width = header.getWidth();
				int offset = header.getOffset();
				splits.add(new ImageSplit(0, 1, offset, 0, 0));
				int linesPerSplit;
				int padding = 64;
				int linesLeft = height;
				int position = 0;
				while (linesLeft > 0) {
					linesPerSplit = Math.min(linesLeft, 33);
					int frontPadding; //how many rows in front of the stuff you care about
					int backPadding; //how many rows behind the stuff you care about
					if (position - padding < 0)
						frontPadding = position;
					else
						frontPadding = padding;
					if (position + linesPerSplit + padding > height)
						backPadding = height - (position + linesPerSplit);
					else
						backPadding = padding;
					linesLeft -= linesPerSplit;
					ImageSplit split = new ImageSplit(offset + (position - frontPadding)*width, 
							height, width, frontPadding, linesPerSplit);
					splits.add(split);
					position+=linesPerSplit;
				}
			} catch (ImageFormatException e) {
				
			}

		}

		return splits.toArray(new ImageSplit[splits.size()]);
		}catch(Exception e)
		{
			throw new NullPointerException("asdkfjsdkjf");
		}
	}

	@Override
	public RecordReader<IntWritable, ImageWritable> getRecordReader(InputSplit split,
			JobConf jobConf, Reporter reporter) throws IOException {
		return new ImageRecordReader(jobConf, (ImageSplit)split);
	}

}
