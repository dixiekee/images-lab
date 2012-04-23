import java.io.File;
import java.io.IOException;

/**
 * Uses the PGMTransform class to apply contrast enhancement to a PGMImage.
 */
public class PGMContrast
{
    /**
     * Applies contrast enhancement to a PGMImage, returning a new image. Does not modify the
     * original image.
     *
     * @param image the image to apply contrast enhancement to.
     */
    public static PGMImage contrastEnhance(PGMImage image)
        throws IOException, ImageFormatException
    {
        int n = 6; // each pyramid has 6 levels

        PGMImage g[] = new PGMImage[n];
        PGMImage l[] = new PGMImage[n];

        // construct the gaussian pyramid
        g[0] = image;
        for(int i = 1; i < n; i++)
            g[i] = PGMTransform.applyReduce(g[i-1]);

        // apply the difference between each level of the pyramid, obtaining the laplacian pyramid.
        l[n-1] = g[n-1];
        for(int i = 0; i < n-1; i++)
            l[i] = PGMTransform.applySubtract( g[i], PGMTransform.applyExpand(g[i+1]) );

        // apply the q(x) function to each level of the laplacian pyramid to obtain the modified
        // laplacian pyramid.
        // note that q(x) isn't applied to the last level of the pyramid.
        for(int i = 0; i < n-1; i++)
        {
            // these parameters were chosen by trial and error.
            if(i == 0) g[i] = PGMTransform.applyQuantize(l[i], 3.0, 1.5);
            else if(i == 1) g[i] = PGMTransform.applyQuantize(l[i], 3.0, 1.5);
            else if(i == 2) g[i] = PGMTransform.applyQuantize(l[i], 2.0, 1.2);
            else if(i == 3) g[i] = PGMTransform.applyQuantize(l[i], 1.8, 0.8);
            else if(i == 4) g[i] = PGMTransform.applyQuantize(l[i], 1.6, 0.5);
        }

        // reconstruct the gaussian pyramid from the modified laplacian pyramid.
        // note that if q(x) = x, then this should reconstruct the original image.
        l[n-1] = g[n-1];
        for(int i = n-2; i >= 0; i--)
            l[i] = PGMTransform.applyAdd( g[i], PGMTransform.applyExpand(l[i+1]) );

        return l[0];
    }

    /**
     * Convenience function for applying the contrast enhancement to an image. Does not modify
     * its argument. Returns a new image with the contrast enhancement applied.
     */
    public static PGMImage contrastEnhance(File imageFile)
        throws IOException, ImageFormatException
    {
        PGMImage image = new PGMImage(imageFile);
        return contrastEnhance(image);
    }
}

