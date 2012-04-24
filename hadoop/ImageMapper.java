import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.io.BytesWritable;

public class ImageMapper extends MapReduceBase 
		implements Mapper<IntWritable, ImageWritable, IntWritable, BytesWritable> {

		private int[][] makePixelsArr(byte[] bytes, int height, int width) {
			int[][] pixels = new int[height][width];
			int byteNum = 0;
			for (int i = 0; i < height; i++) {
				for (int j = 0; j < width; j++) {
					pixels[i][j] = bytes[byteNum];
					if (pixels[i][j] < 0)
						pixels[i][j] += 256;
					byteNum++;
				}
			}
			
			return pixels;
		}
	
		@Override
		public void map(IntWritable key, ImageWritable value,
				OutputCollector<IntWritable, BytesWritable> collector, Reporter reporter)
				throws IOException {
			if (key.get() == 0) {
				collector.collect(key, new BytesWritable(value.getBytes()));
				return;
			}
	
			//create 2D array of pixels from 1D array of bytes
			int pixels[][] = makePixelsArr(value.getBytes(), value.getHeight(), value.getWidth());
			
			//construct a PGMImage from the 2D array of pixels
			PGMImage image = new PGMImage(255, pixels);
			try {
				//apply contrast enhacement
				PGMImage enhancedImg = PGMContrast.contrastEnhance(image);
				//get the relevant part of the image and store in newWritable
				//set the pixels array for newWritable
				int[][] newPixels = enhancedImg.getPixels();
				byte[] newBytes = new byte[value.getNumRows() * value.getWidth()];
				int byteCount = 0;
				for (int i = value.getStartRow(); i < value.getStartRow() + value.getNumRows(); i++) {
					for (int j = 0; j < value.getWidth(); j++) {
						newBytes[byteCount] = (byte) newPixels[i][j];
						byteCount++;
					}
				}

        BytesWritable output = new BytesWritable(newBytes);
				collector.collect(key, output);
				
				
				
			} catch (ImageFormatException e) {
				e.printStackTrace();
			}
		}
}
