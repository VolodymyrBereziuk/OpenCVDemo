package volodymyr.com;

import android.graphics.Bitmap;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.imgproc.Imgproc;

import static org.opencv.imgproc.Imgproc.putText;

public class FeatureDetectionUtil {

    private static final String TAG = FeatureDetectionUtil.class.getSimpleName();

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

    public static Bitmap sift(Bitmap originalBitmap1, Bitmap originalBitmap2) {
        Mat mat1 = bitmapToMat(originalBitmap1);
        Mat mat2 = bitmapToMat(originalBitmap2);

        FeatureDetector detector;

        MatOfKeyPoint keypoints1 = null;
        MatOfKeyPoint keypoints2 = null;

        DescriptorExtractor descriptorExtractor;

        Mat descriptors1 = null;
        Mat descriptors2 = null;

        detector = FeatureDetector.create(FeatureDetector.SIFT);
        descriptorExtractor = DescriptorExtractor.create(DescriptorExtractor.SIFT);
        //Add SIFT specific code
        detector.detect(mat2, keypoints2);
        detector.detect(mat1, keypoints1);
        int keypointsObject1 = keypoints1.toArray().length; //Thesehave been added to display the number of keypoints later.
        int keypointsObject2 = keypoints2.toArray().length;
        descriptorExtractor.compute(mat1, keypoints1, descriptors1);
        descriptorExtractor.compute(mat2, keypoints2, descriptors2);

        DescriptorMatcher descriptorMatcher;
        MatOfDMatch matches = new MatOfDMatch();
        if (true) {
            //Brute-force matcher
            descriptorMatcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_SL2);
        } else {
            //FLANN based matcher
            descriptorMatcher = DescriptorMatcher.create(DescriptorMatcher.FLANNBASED);
        }
        descriptorMatcher.match(descriptors1, descriptors2, matches);
        Mat result = drawMatches(mat1, keypoints1, mat2, keypoints2, matches, true);
        return matToBitmap(result);
    }

    private static Mat drawMatches(Mat img1, MatOfKeyPoint key1, Mat img2, MatOfKeyPoint key2, MatOfDMatch matches, boolean imageOnly) {
        Mat out = new Mat();
        Mat im1 = new Mat();
        Mat im2 = new Mat();
        Imgproc.cvtColor(img1, im1, Imgproc.COLOR_BGR2RGB);
        Imgproc.cvtColor(img2, im2, Imgproc.COLOR_BGR2RGB);
        if (imageOnly) {
            MatOfDMatch emptyMatch = new MatOfDMatch();
            MatOfKeyPoint emptyKey1 = new MatOfKeyPoint();
            MatOfKeyPoint emptyKey2 = new MatOfKeyPoint();
            Features2d.drawMatches(im1, emptyKey1, im2, emptyKey2, emptyMatch, out);
        } else {
            Features2d.drawMatches(im1, key1, im2, key2, matches, out);
        }
        Bitmap bmp = Bitmap.createBitmap(out.cols(), out.rows(), Bitmap.Config.ARGB_8888);
        Imgproc.cvtColor(out, out, Imgproc.COLOR_BGR2RGB);
        putText(out, "FRAME", new Point(img1.width() / 2, 30), Core.FONT_HERSHEY_PLAIN, 2, new Scalar(0, 255, 255), 3);
        putText(out, "MATCHED", new Point(img1.width() + img2.width() / 2, 30), Core.FONT_HERSHEY_PLAIN, 2, new Scalar(255, 0, 0), 3);
        return out;
    }


}
