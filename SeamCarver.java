
import java.awt.Color;

/**
 * Seam carving implementation based on the algorithm discovered by Shai Avidan
 * and Ariel Shamir (SIGGRAPH 2007).
 * 
 * @author Zoe Zinn
 * @version 2/11/2022
 */
public class SeamCarver {

    private Picture picture;
    private int width;
    private int height;

    /**
     * Construct a SeamCarver object.
     * 
     * @param picture initial picture
     */
    public SeamCarver(Picture picture) {
        this.picture = picture;
        this.width = picture.width();
        this.height = picture.height();
    }

    public Picture getPicture() {
        return picture;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    /**
     * Computes the energy of a pixel.
     * 
     * @param x column index
     * @param y row index
     * @return energy value
     */
    public int energy(int x, int y) {
        // Check x and y first 
        if (x < 0 || x > width - 1) {
            throw new IndexOutOfBoundsException(String.format(
                    "x = %d, width = %d", x, width));
        } 
        if (y < 0 || y > height - 1) {
            throw new IndexOutOfBoundsException(String.format(
                    "y = %d, height = %d", y, height));
        }
        
        // Getting a row too high
        Color top;
        try {
            top = picture.get(x, y + 1);
        } catch (IllegalArgumentException e) {
            top = picture.get(x, 0);
        } 
        Color topColor = top;
        
        // Getting a row too low
        Color bottom;
        try {
            bottom = picture.get(x, y - 1);
        } catch (IllegalArgumentException e) {
            bottom = picture.get(x, height - 1);
        } 
        Color bottomColor = bottom;
        
        // Getting a col too right
        Color right;
        try {
            right = picture.get(x + 1, y);
        } catch (IllegalArgumentException e) {
            right = picture.get(0, y);
        } 
        Color rightColor = right;
        
        // Getting a col too left
        Color left;
        try {
            left = picture.get(x - 1, y);
        } catch (IllegalArgumentException e) {
            left = picture.get(width - 1, y);
        }
        Color leftColor = left;
        
        // Okay, actual math now
        // Red row version
        int ry = 0;
        if (topColor.getRed() > bottomColor.getRed()) {
            ry = topColor.getRed() - bottomColor.getRed();
        } else {
            ry = bottomColor.getRed() - topColor.getRed();
        }
        // Green row version
        int gy = 0;
        if (topColor.getGreen() > bottomColor.getGreen()) {
            gy = topColor.getGreen() - bottomColor.getGreen();
        } else {
            gy = bottomColor.getGreen() - topColor.getGreen();
        }
        // Blue row version
        int by = 0;
        if (topColor.getBlue() > bottomColor.getBlue()) {
            by = topColor.getBlue() - bottomColor.getBlue();
        } else {
            by = bottomColor.getBlue() - topColor.getBlue();
        }
        // Red col version
        int rx = 0;
        if (rightColor.getRed() > leftColor.getRed()) {
            rx = rightColor.getRed() - leftColor.getRed();
        } else {
            rx = leftColor.getRed() - rightColor.getRed();
        }
        // Green col version
        int gx = 0;
        if (rightColor.getGreen() > leftColor.getGreen()) {
            gx = rightColor.getGreen() - leftColor.getGreen();
        } else {
            gx = leftColor.getGreen() - rightColor.getGreen();
        }
        // Blue col version
        int bx = 0;
        if (rightColor.getBlue() > leftColor.getBlue()) {
            bx = rightColor.getBlue() - leftColor.getBlue();
        } else {
            bx = leftColor.getBlue() - rightColor.getBlue();
        }
        
        // Row squared 
        int ry2 = ry * ry;
        int gy2 = gy * gy;
        int by2 = by * by;
        // Col squared
        int rx2 = rx * rx;
        int gx2 = gx * gx;
        int bx2 = bx * bx;
        
        // Addition 
        int deltay2 = ry2 + gy2 + by2;
        int deltax2 = rx2 + gx2 + bx2;
        
        // Energy
        int energy = deltax2 + deltay2;
        return energy;
    }

    /**
     * Computes the energy matrix for the picture.
     * 
     * @return current energy of each pixel
     */
    public int[][] energyMatrix() {
        // Don't forget it's column major!
        int[][] matrix = new int[width][height];
        
        // For loop to get E V E R Y pixel
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                
                matrix[i][j] = energy(i, j);
            }
        }
        return matrix;
    }

    /**
     * Removes a horizontal seam from current picture.
     * 
     * @param seam sequence of row indices
     */
    public void removeHorizontalSeam(int[] seam) {
        // Let's validate the seam array
        if (seam == null) {
            throw new NullPointerException(); 
        } else if (height <= 1) {
            throw new IllegalArgumentException();
        } else if (seam.length > width || seam.length < width) {
            throw new IllegalArgumentException();
        } 
        
        // Now let's see that all the values are 1 or less apart
        for (int i = 0; i < seam.length - 1; i++) {
            int current = seam[i];
            int next = seam[i + 1];
            if (current - next > 1 || current - next < -1) {
                throw new IllegalArgumentException();
            }
        }
        
        // Now, if no exceptions were thrown, we copy the image!
        Picture carvedPic = new Picture(width, height - 1);
        for (int col = 0; col < width; col++) { 
            int setRow = 0;
            for (int row = 0; row < height; row++) {
                if (row != seam[col]) {
                    carvedPic.set(col, setRow, picture.get(col, row));
                    setRow++;
                }
            }
        }
        this.height--;
        picture = new Picture(carvedPic);
    }

    /**
     * Removes a vertical seam from the current picture.
     * 
     * @param seam sequence of column indices
     */
    public void removeVerticalSeam(int[] seam) {
        // Let's validate the seam array
        if (seam == null) {
            throw new NullPointerException(); 
        } else if (width <= 1) {
            throw new IllegalArgumentException();
        } else if (seam.length > height || seam.length < height) {
            throw new IllegalArgumentException();
        } 
        
        // Now let's see that all the values are 1 or less apart
        for (int i = 0; i < seam.length - 1; i++) {
            int current = seam[i];
            int next = seam[i + 1];
            if (current - next > 1 || current - next < -1) {
                throw new IllegalArgumentException();
            }
        }
        
        // Now, if no exceptions were thrown, we copy the image!
        Picture carvedPic = new Picture(width - 1, height);
        for (int row = 0; row < height; row++) { 
            int setCol = 0;
            for (int col = 0; col < width; col++) {
                if (col != seam[row]) {
                    carvedPic.set(setCol, row, picture.get(col, row));
                    setCol++;
                }
            }
        }
        this.width--;
        picture = new Picture(carvedPic);
    }  
}
