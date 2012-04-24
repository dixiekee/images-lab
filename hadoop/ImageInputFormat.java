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
		ArrayList<ImageSplit> splits = new ArrayList<ImageSplit>();
		Path p = ImageInputFormat.getInputPaths(conf)[0];
		//convert Path to a PGMImage
		FileSystem fileSys;

		//read the header to figure out how many ImageSplits to make
		try {
			fileSys = p.getFileSystem(conf);
			FSDataInputStream inputStream = fileSys.open(p);
			ImageHeader header = PGMImage.getHeader(inputStream);
			int height = header.getHeight(); //height of whole image (wihtout header)
			int width = header.getWidth(); //width of whole image (without header)
			int offset = header.getOffset();
			splits.add(new ImageSplit(0, 1, offset, 0, 0));
			int linesPerSplit;
			int padding = 64;
			int linesLeft = height;
			int position = 0;
			while (linesLeft > 0) {
				linesPerSplit = Math.min(linesLeft, 67617/width);
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


                                int splitHeight = linesPerSplit + frontPadding + backPadding;
                                while (splitHeight % 32 != 1) {
                                        if (frontPadding > 0) {
                                                frontPadding++;
                                                splitHeight++;
                                                if (splitHeight % 32 == 1) break;
                                        }
                                        if (position + linesPerSplit + backPadding + 1 < height) {
                                                backPadding++;
                                                splitHeight++;
                                        }
                                }


				ImageSplit split = new ImageSplit(offset + (position - frontPadding)*width, 
						linesPerSplit + frontPadding + backPadding, width, frontPadding, linesPerSplit);


/*System.out.println("-------linesPerSplit: " + linesPerSplit + " frontPadding: " + frontPadding + " backPadding: " + backPadding);
System.out.print("offset: " + (offset + (position - frontPadding)*width));
System.out.print(" height: " + (linesPerSplit + frontPadding + backPadding));
System.out.print(" width: " + width);
System.out.print(" startRow: " + frontPadding);
System.out.println(" numRows: " + linesPerSplit);*/

				splits.add(split);
				position+=linesPerSplit;
			}
		} catch (Exception e) {
			
		}

		return splits.toArray(new ImageSplit[splits.size()]);

	}

	@Override
	public RecordReader<IntWritable, ImageWritable> getRecordReader(InputSplit split,
			JobConf jobConf, Reporter reporter) throws IOException {
		return new ImageRecordReader(jobConf, (ImageSplit)split);
	}

}
