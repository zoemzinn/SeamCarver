/**
 * Example program that uses SeamCarver.
 *
 * @author Chris Mayfield
 * @version 01/29/2022
 */
public class Main {

    /**
     * Read an image from a file and shrink its size.
     * 
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        // Usage
        if (args.length < 2) {
            System.out.println("Usage: <filename> <seam type> optional: <num pixels>");
            System.out.println("\t<seam type>: -h Horizontal crop");
            System.out.println("\t<seam type>: -v Vertical crop");
            return;
        }

        // Create the objects
        Picture p = new Picture(args[0]);
        SeamCarver s = new SeamCarver(p);

        // Get number of pixels to crop if specified
        int pixels = 100;
        if (args.length == 3)
        {
            pixels = Integer.parseInt(args[2]);
        }

        // Crop on horizontal seam
        if (args[1].compareTo("-h") == 0) {
            for (int i = 0; i < pixels; i++) {
                int[] seam = ProvidedCode.findHorizontalSeam(s);
                s.removeHorizontalSeam(seam);
            }

            // Save cropped image
            String suffix = args[0].substring(args[0].lastIndexOf('.'));
            String prefix = args[0].substring(0, args[0].lastIndexOf('.'));
            prefix += "_cropped";

            s.getPicture().save(prefix + suffix);
            s.getPicture().show();
        }

        // Crop on vertical seam
        if (args[1].compareTo("-v") == 0) {
            for (int i = 0; i < pixels; i++) {
                int[] seam = ProvidedCode.findVerticalSeam(s);
                s.removeVerticalSeam(seam);
            }
            
            // Save cropped image
            String suffix = args[0].substring(args[0].lastIndexOf('.'));
            String prefix = args[0].substring(0, args[0].lastIndexOf('.'));
            prefix += "_cropped";

            s.getPicture().save(prefix + suffix);
            s.getPicture().show();
        }
    }
}
