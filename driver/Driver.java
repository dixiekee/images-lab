package driver;

import java.io.*;

import contrast.*;
import hadoop.ImageMapper;
import hadoop.ImageReducer;
import image.*;
import transform.*;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.Job;

/**
 * The main class for the contrast enhancer. 
 *
 * The arguments should be an input directory full of .pgm images (one image per file), and the
 * output directory should be a directory that does not currently exist which will contain the
 * enhanced images upon completion.
 *
 * Usage: java driver.Driver <input directory> <output directory>
 */
public class Driver
{
    /**
     * The main driver for the contrast enhancer.
     *
     * Verifies that the input directory exists, and that the output directory doesn't exist. Then
     * tries to apply the contrast enhancement to each of the files inside the input directory.
     *
     * @param args should be two arguments, the first being the input directory and the second being
     * the output directory. the second directory should not exist.
     */
    public static void main(String[] args) throws IOException, ImageFormatException
    {
        if(args.length != 2)
        {
            System.out.println("Usage: java driver.Driver <input directory>" +
                    " <output directory>");
            System.out.println("Jarfile Usage: java -jar $jarfile <input directory>" +
                    " <output directory>");
            System.exit(0);
        }

        /* verify that input directory exists and the output directory doesn't. */
        File imageDirectory = new File(args[0]);
        File outDirectory = new File(args[1]);

        if(!imageDirectory.exists())
            throw new IllegalArgumentException("The input directory <" + imageDirectory
                    + "> doesn't exist.");

        if(!imageDirectory.isDirectory())
            throw new IllegalArgumentException(imageDirectory + " is not a directory");

        if(outDirectory.exists())
            throw new IllegalArgumentException("The output directory <" + outDirectory 
                    + "> already exists.");
        
        // Writing random Hadoop stuff here. 
        
        Configuration conf = new Configuration();
        Job job = new Job(conf, "image dups remover");
        job.setJarByClass(Driver.class);
        job.setInputFormatClass(SequenceFileInputFormat.class);
        job.setOutputKeyClass(ImageMapper.class);
        job.setOutputValueClass(ImageReducer.class);
        
        // Ends here. Bleh.

        File[] images = imageDirectory.listFiles();

        for(File image: images)
        {
            try
            { 
                System.out.println(image.getName());
                PGMImage test = new PGMImage(image);

                if(!outDirectory.exists())
                    outDirectory.mkdir();

                PGMImage enhancedImage = PGMContrast.contrastEnhance(image); 
                enhancedImage.write(new File(outDirectory, image.getName()));
            }
            catch(FileNotFoundException e)
            { 
                System.out.println("Error processing " + image + ". Continuing.");
                e.printStackTrace();
            }
            catch(IOException e)
            {
                System.out.println("Error processing " + image + ". Continuing.");
                e.printStackTrace();
            }
            catch(ImageFormatException e)
            {
                System.out.println("Error processing " + image + " (probably not a .pgm image)."
                        + " Continuing.");
                e.printStackTrace();
            }
        }
    }
}
