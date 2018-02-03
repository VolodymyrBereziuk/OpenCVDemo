package volodymyr.com.opencvtest;

import android.content.Context;
import android.graphics.Bitmap;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.DMatch;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import volodymyr.com.App;

import static org.opencv.imgproc.Imgproc.rectangle;


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

    public static Bitmap orb(Bitmap originalBitmap1, Bitmap originalBitmap2) {
        Mat mat1 = bitmapToMat(originalBitmap1);
        Mat mat2 = bitmapToMat(originalBitmap2);

        Scalar RED = new Scalar(255, 0, 0);
        Scalar GREEN = new Scalar(0, 255, 0);

        FeatureDetector detector = FeatureDetector.create(FeatureDetector.ORB);
        DescriptorExtractor descriptor = DescriptorExtractor.create(DescriptorExtractor.ORB);
        DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);

        Imgproc.cvtColor(mat1, mat1, Imgproc.COLOR_RGB2GRAY);
        Mat descriptors1 = new Mat();
        MatOfKeyPoint keypoints1 = new MatOfKeyPoint();
        detector.detect(mat1, keypoints1);
        descriptor.compute(mat1, keypoints1, descriptors1);

        Imgproc.cvtColor(mat2, mat2, Imgproc.COLOR_RGB2GRAY);
        Mat descriptors2 = new Mat();
        MatOfKeyPoint keypoints2 = new MatOfKeyPoint();
        detector.detect(mat2, keypoints2);
        descriptor.compute(mat2, keypoints2, descriptors2);

        MatOfDMatch matches = new MatOfDMatch();
        if (mat1.type() == mat2.type()) {
            matcher.match(descriptors1, descriptors2, matches);
        }
        List<DMatch> matchesList = matches.toList();

        MatOfDMatch goodMatches = new MatOfDMatch();
        goodMatches.fromList(matchesList);
        Mat outputImg = new Mat();
        MatOfByte drawnMatches = new MatOfByte();
        Features2d.drawMatches(mat1, keypoints1, mat2, keypoints2, goodMatches, outputImg, GREEN, RED, drawnMatches, Features2d.NOT_DRAW_SINGLE_POINTS);
        return matToBitmap(outputImg);
    }

    public static Bitmap haar(Bitmap originalBitmap) {
        Mat mat = bitmapToMat(originalBitmap);
        InputStream is = App.getAppContext().getResources().openRawResource(R.raw.haarcascade_frontalface_default);
        File cascadeDir = App.getAppContext().getDir("cascade", Context.MODE_PRIVATE);
        File mCascadeFile = new File(cascadeDir, "cascade.xml");
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(mCascadeFile);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            is.close();
            os.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        CascadeClassifier haarCascade = new CascadeClassifier(mCascadeFile.getAbsolutePath());
        haarCascade.load(mCascadeFile.getAbsolutePath() );

        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_BGR2GRAY);

        MatOfRect faces = new MatOfRect();
        if(haarCascade != null) {
            haarCascade.detectMultiScale(mat, faces, 1.1, 2, 2, new Size(200,200), new Size());
        }
        Rect[] facesArray = faces.toArray();
        for (int i = 0; i < facesArray.length; i++)
            rectangle(mat, facesArray[i].tl(),facesArray[i].br(), new Scalar(100), 3);
        return matToBitmap(mat);
    }


}
