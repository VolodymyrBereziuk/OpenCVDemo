package volodymyr.com.opencvtest;

import android.graphics.Bitmap;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.opencv.imgproc.Imgproc.circle;
import static org.opencv.imgproc.Imgproc.line;

public class FiltersUtil{

    private static final String TAG = FiltersUtil.class.getSimpleName();

    private static Mat bitmapToMat(Bitmap bitmap) {
        Mat mat = null;
        Bitmap tempBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        mat = new Mat(tempBitmap.getHeight(), tempBitmap.getWidth(), CvType.CV_8U);
        Utils.bitmapToMat(tempBitmap, mat);
        return mat;
    }

    private static Bitmap matToBitmap(Mat mat) {
        Bitmap bitmap = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat, bitmap);
        return bitmap;
    }


    /**
     * Blur
     *
     * @param originalImage
     */
    public static Bitmap linearFilter(Bitmap originalImage) {
        Mat mat = bitmapToMat(originalImage);
        Imgproc.blur(mat, mat, new Size(3, 3));
        return matToBitmap(mat);
    }


    public static Bitmap gaussianBlur(Bitmap originalImage) {
        Mat mat = bitmapToMat(originalImage);
        Imgproc.GaussianBlur(mat, mat, new Size(3, 3), 0);
        return matToBitmap(mat);
    }

    public static Bitmap medianBlur(Bitmap originalImage) {
        Mat mat = bitmapToMat(originalImage);
        Imgproc.medianBlur(mat, mat, 3);
        return matToBitmap(mat);
    }

    public static Bitmap customKernel(Bitmap originalImage) {
        Mat mat = bitmapToMat(originalImage);
        Mat kernel = new Mat(3, 3, CvType.CV_16SC1);
        kernel.put(0, 0, 0, -1, 0, -1, 5, -1, 0, -1, 0);
        Imgproc.filter2D(mat, mat, mat.depth(), kernel);
        return matToBitmap(mat);
    }

    public static Bitmap dilation(Bitmap originalImage) {
        Mat mat = bitmapToMat(originalImage);
        Mat kernelDilate = Imgproc.getStructuringElement(
                Imgproc.MORPH_RECT, new Size(3, 3));
        Imgproc.dilate(mat, mat, kernelDilate);
        return matToBitmap(mat);
    }

    public static Bitmap erosion(Bitmap originalImage) {
        Mat mat = bitmapToMat(originalImage);
        Mat kernelErode = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(5, 5));
        Imgproc.erode(mat, mat, kernelErode);
        return matToBitmap(mat);
    }

    public static Bitmap adaptiveTresholding(Bitmap originalImage) {
        Mat mat = bitmapToMat(originalImage);
        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_BGR2GRAY);
        Imgproc.adaptiveThreshold(mat, mat, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,
                Imgproc.THRESH_BINARY, 3, 0);
        return matToBitmap(mat);
    }

    public static Bitmap gaussianDifference(Bitmap originalImage) {
        Mat originalMat = bitmapToMat(originalImage);

        Mat grayMat = new Mat();
        Mat blur1 = new Mat();
        Mat blur2 = new Mat();
        Imgproc.cvtColor(originalMat, grayMat, Imgproc.COLOR_BGR2GRAY);
        //Bluring the images using two different blurring radius
        Imgproc.GaussianBlur(grayMat, blur1, new Size(15, 15), 5);
        Imgproc.GaussianBlur(grayMat, blur2, new Size(21, 21), 5);
        //Subtracting the two blurred images
        Mat DoG = new Mat();
        Core.absdiff(blur1, blur2, DoG);
        //Inverse Binary Thresholding
        Core.multiply(DoG, new Scalar(100), DoG);
        Imgproc.threshold(DoG, DoG, 50, 255, Imgproc.THRESH_BINARY_INV);

        return matToBitmap(DoG);
    }

    public static Bitmap cannyEdge(Bitmap originalImage) {
        Mat originalMat = bitmapToMat(originalImage);

        Mat grayMat = new Mat();
        Mat cannyEdges = new Mat();
        //Converting the image to grayscale
        Imgproc.cvtColor(originalMat, grayMat, Imgproc.COLOR_BGR2GRAY);
        Imgproc.Canny(grayMat, cannyEdges, 10, 100);
        return matToBitmap(cannyEdges);
    }

    public static Bitmap sobelOperator(Bitmap originalImage) {
        Mat originalMat = bitmapToMat(originalImage);

        Mat grayMat = new Mat();
        Mat sobel = new Mat(); //Mat to store the result
        //Mat to store gradient and absolute gradient respectively
        Mat grad_x = new Mat();
        Mat abs_grad_x = new Mat();
        Mat grad_y = new Mat();
        Mat abs_grad_y = new Mat();
        //Converting the image to grayscale
        Imgproc.cvtColor(originalMat, grayMat, Imgproc.COLOR_BGR2GRAY);
        //Calculating gradient in horizontal direction
        Imgproc.Sobel(grayMat, grad_x, CvType.CV_16S, 1, 0, 3, 1, 0);
        //Calculating gradient in vertical direction
        Imgproc.Sobel(grayMat, grad_y, CvType.CV_16S, 0, 1, 3, 1, 0);
        //Calculating absolute value ofgradients in both the direction
        Core.convertScaleAbs(grad_x, abs_grad_x);
        Core.convertScaleAbs(grad_y, abs_grad_y);
        //Calculating the resultant gradient
        Core.addWeighted(abs_grad_x, 0.5, abs_grad_y, 0.5, 1, sobel);

        return matToBitmap(sobel);
    }

    public static Bitmap harrisCornerDetection(Bitmap originalImage) {
        Mat originalMat = bitmapToMat(originalImage);

        Mat grayMat = new Mat();
        Mat corners = new Mat();
        //Converting the image to grayscale
        Imgproc.cvtColor(originalMat, grayMat, Imgproc.COLOR_BGR2GRAY);
        Mat tempDst = new Mat();
        //finding corners
        Imgproc.cornerHarris(grayMat, tempDst, 2, 3, 0.04);
        //Normalizing harris corner's output
        Mat tempDstNorm = new Mat();
        Core.normalize(tempDst, tempDstNorm, 0, 255, Core.NORM_MINMAX);
        Core.convertScaleAbs(tempDstNorm, corners);
        //Drawing corners on a new image
        Random r = new Random();
        for (int i = 0; i < tempDstNorm.cols(); i++) {
            for (int j = 0; j < tempDstNorm.rows(); j++) {
                double[] value = tempDstNorm.get(j, i);
                if (value[0] > 150)
                    circle(corners, new Point(i, j), 5, new Scalar(r.nextInt(255)), 2);
            }
        }

        return matToBitmap(corners);
    }

    public static Bitmap houghLines(Bitmap originalImage) {
        Mat originalMat = bitmapToMat(originalImage);

        Mat grayMat = new Mat();
        Mat cannyEdges = new Mat();
        Mat lines = new Mat();//Converting the image to grayscale
        Imgproc.cvtColor(originalMat, grayMat, Imgproc.COLOR_BGR2GRAY);
        Imgproc.Canny(grayMat, cannyEdges, 10, 100);

        Imgproc.HoughLinesP(cannyEdges, lines, 1, Math.PI / 180, 50, 20, 20);
        Mat houghLines = new Mat();
        houghLines.create(cannyEdges.rows(), cannyEdges.cols(), CvType.CV_8UC1);
        for (int i = 0; i < lines.rows(); i++) {
            double[] points = lines.get(i, 0);
            Point pt1 = new Point(points[0], points[1]);
            Point pt2 = new Point(points[2], points[3]);
            //Drawing lines on an image
            line(houghLines, pt1, pt2, new Scalar(255, 0, 0), 2);
        }
        //Converting Mat back to Bitmap
        return matToBitmap(houghLines);
    }

    public static Bitmap houghCircles(Bitmap originalImage) {
        Mat originalMat = bitmapToMat(originalImage);

        Mat grayMat = new Mat();
        Mat cannyEdges = new Mat();
        Mat circles = new Mat();
        //Converting the image to grayscale
        Imgproc.cvtColor(originalMat,grayMat,Imgproc.COLOR_BGR2GRAY);
        Imgproc.Canny(grayMat, cannyEdges,10, 100);
        Imgproc.HoughCircles(cannyEdges, circles, Imgproc.CV_HOUGH_GRADIENT,1, grayMat.rows() / 8);
        //, grayMat.rows() / 8);
        Mat houghCircles = new Mat();
        houghCircles.create(cannyEdges.rows(),cannyEdges.cols(),CvType.CV_8UC1);
        //Drawing lines on the image
        for(int i = 0 ; i < circles.cols() ; i++)
        {
            double[] parameters = circles.get(0,i);
            double x, y;
            int r;
            x = parameters[0];
            y = parameters[1];
            r = (int)parameters[2];
            Point center = new Point(x, y);//Drawing circles on an image
            circle(houghCircles,center,r,new Scalar(255,0,0),1);
        }
        //Converting Mat back to Bitmap
        return matToBitmap(houghCircles);
    }

    public static Bitmap contours(Bitmap originalImage) {
        Mat originalMat = bitmapToMat(originalImage);

        Mat grayMat = new Mat();
        Mat cannyEdges = new Mat();
        Mat hierarchy = new Mat();
        List<MatOfPoint> contourList = new ArrayList<MatOfPoint>();
        //A list to store all the contours
        //Converting the image to grayscale
        Imgproc.cvtColor(originalMat,grayMat,Imgproc.COLOR_BGR2GRAY);
        Imgproc.Canny(grayMat, cannyEdges,10, 100);
        //finding contours
        Imgproc.findContours(cannyEdges,contourList,hierarchy,Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
        //Drawing contours on a new image
        Mat contours = new Mat();
        contours.create(cannyEdges.rows(),cannyEdges.cols(),CvType.CV_8UC3);
        Random r = new Random();
        for(int i = 0; i < contourList.size(); i++)
        {
            Imgproc.drawContours(contours,contourList,i,new Scalar(r.nextInt(255),r.nextInt(255),r.nextInt(255)), -1);
        }
        return matToBitmap(contours);
    }
}
