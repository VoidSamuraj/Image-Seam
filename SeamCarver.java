import edu.princeton.cs.algs4.Picture;
import java.awt.Color;

/**
 * @author Karol Robak
 */
public class SeamCarver {
    
    private Picture picture;
    
    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture){
        if(picture==null)
            throw new IllegalArgumentException("null argument");
        this.picture=picture;
    }
    
    // current picture
    public Picture picture(){
        return this.picture;
    }
    
    // width of current picture
    public int width(){
        return this.picture.width();
    }
    
    // height of current picture
    public int height(){
        return this.picture.height();
    }
    
    // energy of pixel at column x and row y
    public double energy(int x, int y){
        if(x<0 || y<0 || x>width()-1 || y>height()-1)
            throw new IllegalArgumentException("Coordinates outside image");
        if(x==0 || y==0 || x==width()-1 || y==height()-1)
            return 1000;
        
        Color x0=picture.get(x-1, y);
        Color x1=picture.get(x+1, y);
        
        Color y0=picture.get(x, y-1);
        Color y1=picture.get(x, y+1);
        
        double xg=Math.pow(x0.getRed()-x1.getRed(),2) + Math.pow(x0.getGreen()- x1.getGreen(),2) + Math.pow(x0.getBlue()-x1.getBlue(),2);
        double yg=Math.pow(y0.getRed()-y1.getRed(),2) + Math.pow(y0.getGreen()- y1.getGreen(),2) + Math.pow(y0.getBlue()-y1.getBlue(),2);
        
        return Math.sqrt(xg+yg);
    }
    
    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam(){
        
        double[][] cumulativeEnergy = new double[width()][height()];
        // connections to previous pixels
        int[][] edgeTo = new int[width()][height()];
        
        // calculate first column energy
        for (int y = 0; y < height(); y++) {
            cumulativeEnergy[0][y] = energy(0, y);
        }
        
        // calculate other collumns
        for (int x = 1; x < width(); x++) {
            for (int y = 0; y < height(); y++) {
                double minEnergy = cumulativeEnergy[x-1][y];
                int minIndex = y;
                
                // select best connection from neighbour pixels from previous column
                for (int dy = -1; dy <= 1; dy++) {
                    int ny = y + dy;
                    if (ny >= 0 && ny < height()) {
                        double currentEnergy = cumulativeEnergy[x-1][ny];
                        if (currentEnergy < minEnergy) {
                            minEnergy = currentEnergy;
                            minIndex = ny;
                        }
                    }
                }
                
                // save sum of energy needed to get to this point
                cumulativeEnergy[x][y] = minEnergy + energy(x, y);
                edgeTo[x][y] = minIndex;
            }
        }
        
        // find lowest effort pixel in last column
        int minIndex = 0;
        for (int y = 1; y < height(); y++) {
            if (cumulativeEnergy[width()-1][y] < cumulativeEnergy[width()-1][minIndex]) {
                minIndex = y;
            }
        }
        
        // create seam based on min index
        int[] seam = new int[width()];
        for (int x = width() - 1; x >= 0; x--) {
            seam[x] = minIndex;
            minIndex = edgeTo[x][minIndex];
        }
        
        return seam;
    }
    
    // sequence of indices for vertical seam
    public int[] findVerticalSeam(){
        
        double[][] cumulativeEnergy = new double[width()][height()];
        
        // connections to previous pixels
        int[][] edgeTo = new int[width()][height()];
        
        // calculate first row energy
        for (int x = 0; x < width(); x++) {
            cumulativeEnergy[x][0] = energy(x, 0);
        }
        
        //  calculate other rows
        for (int y = 1; y < height(); y++) {
            for (int x = 0; x < width(); x++) {
                double minEnergy = cumulativeEnergy[x][y - 1];
                int minIndex = x;
                
                // select best connection from neighbour pixels from previous rows
                for (int dx = -1; dx <= 1; dx++) {
                    int nx = x + dx;
                    if (nx >= 0 && nx < width()) {
                        double currentEnergy = cumulativeEnergy[nx][y - 1];
                        if (currentEnergy < minEnergy) {
                            minEnergy = currentEnergy;
                            minIndex = nx;
                        }
                    }
                }
                
                // save sum of energy needed to get to this point
                cumulativeEnergy[x][y] = minEnergy + energy(x, y);
                edgeTo[x][y] = minIndex;
            }
        }
        
        // find lowest effort pixel in last row
        int minIndex = 0;
        for (int x = 1; x < width(); x++) {
            if (cumulativeEnergy[x][height() - 1] < cumulativeEnergy[minIndex][height() - 1]) {
                minIndex = x;
            }
        }
        
        //create seam based on min index
        int[] seam = new int[height()];
        for (int y = height() - 1; y >= 0; y--) {
            seam[y] = minIndex;
            minIndex = edgeTo[minIndex][y];
        }
        
        return seam;
    }
    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam){
        if(seam==null || seam.length!=width() || height()<=1)
            throw new IllegalArgumentException("null argument");
        
        Picture newPicture = new Picture(width(), height() - 1);
        for (int x = 0; x < width(); x++) {
            if(x<width()-1 && Math.abs(seam[x]-seam[x+1])>1)
                throw new IllegalArgumentException("null argument");
            
            for (int y = 0; y < seam[x]; y++) {
                newPicture.setRGB(x, y, picture.getRGB(x, y));
            }
            for (int y = seam[x] + 1; y < height(); y++) {
                newPicture.setRGB(x, y - 1, picture.getRGB(x, y));
            }
        }
        // update picture
        picture = newPicture; 
    }
    
    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam){
        if(seam==null || seam.length!=height() || width()<=1)
            throw new IllegalArgumentException("null argument");
        
        Picture newPicture = new Picture(width() - 1, height());
        for (int y = 0; y < height(); y++) {
            if(y<height()-1 && Math.abs(seam[y]-seam[y+1])>1)
                throw new IllegalArgumentException("null argument");
            
            for (int x = 0; x < seam[y]; x++) {
                newPicture.setRGB(x, y, picture.getRGB(x, y));
            }
            for (int x = seam[y] + 1; x < width(); x++) {
                newPicture.setRGB(x - 1, y, picture.getRGB(x, y));
            }
        }
        // update picture
        picture = newPicture;
        
    }

    public static void main(String[] args){
        // read picture
        Picture p = new Picture("PATH_TO_FILE");
        SeamCarver sc=new SeamCarver(p);
        
        //remove 50 pixels in both axis
        for(int j=0;j<50;j++){
            sc.removeVerticalSeam(sc.findVerticalSeam());
            sc.removeHorizontalSeam(sc.findHorizontalSeam());
        }
        sc.picture().show();
    }
    
}
