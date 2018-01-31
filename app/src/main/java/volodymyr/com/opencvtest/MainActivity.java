package volodymyr.com.opencvtest;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import org.opencv.android.OpenCVLoader;

import java.util.ArrayList;
import java.util.List;

import volodymyr.com.FeatureDetectionUtil;

public class MainActivity extends AppCompatActivity {
    private final int SELECT_PHOTO = 1;

    static {
        if (!OpenCVLoader.initDebug()) {
            // Handle initialization error
        }
    }

    private RecyclerView recyclerView;
    private ImagesAdapter mAdapter;

    private Bitmap originalBitmap;
    private Bitmap saltPaperBitmap;
    private Bitmap logoBitmap;
    private Bitmap sudokuBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initImages();
        initView();
    }

    private void initImages() {
        originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.lena);
        saltPaperBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.lena_salt_paper);
        logoBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.open_cv);
        sudokuBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sudoku);
    }

    private void initView() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mAdapter = new ImagesAdapter(prepareRecognitionData());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), 1);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
    }

    private List<ImageItem> prepareFilteredData() {
        List<ImageItem> images = new ArrayList<>();

        images.add(new ImageItemBuilder().setImage(FiltersUtil.houghLines(sudokuBitmap)).createImageItem());
        images.add(new ImageItemBuilder().setImage(sudokuBitmap).createImageItem());

        images.add(new ImageItemBuilder().setImage(FiltersUtil.contours(logoBitmap)).createImageItem());
        images.add(new ImageItemBuilder().setImage(logoBitmap).createImageItem());

        images.add(new ImageItemBuilder().setImage(FiltersUtil.houghCircles(originalBitmap)).createImageItem());
        images.add(new ImageItemBuilder().setImage(originalBitmap).createImageItem());

        images.add(new ImageItemBuilder().setImage(FiltersUtil.houghLines(logoBitmap)).createImageItem());
        images.add(new ImageItemBuilder().setImage(logoBitmap).createImageItem());

        images.add(new ImageItemBuilder().setImage(FiltersUtil.harrisCornerDetection(originalBitmap)).createImageItem());
        images.add(new ImageItemBuilder().setImage(originalBitmap).createImageItem());

        images.add(new ImageItemBuilder().setImage(FiltersUtil.sobelOperator(originalBitmap)).createImageItem());
        images.add(new ImageItemBuilder().setImage(originalBitmap).createImageItem());

        images.add(new ImageItemBuilder().setImage(FiltersUtil.cannyEdge(logoBitmap)).createImageItem());
        images.add(new ImageItemBuilder().setImage(logoBitmap).createImageItem());

        images.add(new ImageItemBuilder().setImage(FiltersUtil.gaussianDifference(originalBitmap)).createImageItem());
        images.add(new ImageItemBuilder().setImage(originalBitmap).createImageItem());

        images.add(new ImageItemBuilder().setImage(FiltersUtil.adaptiveTresholding(originalBitmap)).createImageItem());
        images.add(new ImageItemBuilder().setImage(originalBitmap).createImageItem());

        images.add(new ImageItemBuilder().setImage(FiltersUtil.erosion(logoBitmap)).createImageItem());
        images.add(new ImageItemBuilder().setImage(logoBitmap).createImageItem());

        images.add(new ImageItemBuilder().setImage(FiltersUtil.dilation(logoBitmap)).createImageItem());
        images.add(new ImageItemBuilder().setImage(logoBitmap).createImageItem());

        images.add(new ImageItemBuilder().setImage(FiltersUtil.customKernel(originalBitmap)).createImageItem());
        images.add(new ImageItemBuilder().setImage(originalBitmap).createImageItem());

        images.add(new ImageItemBuilder().setImage(FiltersUtil.medianBlur(saltPaperBitmap)).createImageItem());
        images.add(new ImageItemBuilder().setImage(saltPaperBitmap).createImageItem());

        images.add(new ImageItemBuilder().setImage(FiltersUtil.gaussianBlur(originalBitmap)).createImageItem());
        images.add(new ImageItemBuilder().setImage(FiltersUtil.linearFilter(originalBitmap)).createImageItem());
        images.add(new ImageItemBuilder().setImage(originalBitmap).createImageItem());

        return images;
    }

    private List<ImageItem> prepareRecognitionData() {
        List<ImageItem> images = new ArrayList<>();

        images.add(new ImageItemBuilder().setImage(FeatureDetectionUtil.sift(sudokuBitmap,sudokuBitmap)).createImageItem());

        return images;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_load_image) {
            Intent intent = new Intent(Intent.ACTION_PICK,
                    Uri.parse("content://media/internal/images/media"));
            startActivityForResult(intent,
                    SELECT_PHOTO);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn =
                    {MediaStore.Images.Media.DATA};
            Cursor cursor =
                    getContentResolver().query(selectedImage,
                            filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;
//            bitmap = BitmapFactory.decodeFile(picturePath, options);
//
//            Bitmap tempBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
//            mat = new Mat(tempBitmap.getHeight(), tempBitmap.getWidth(), CvType.CV_8U);
//            Utils.bitmapToMat(tempBitmap, mat);
//            bitmap = tempBitmap.copy(Bitmap.Config.ARGB_8888, false);


//            differenceOfGaussian();
        }
    }


//    public void differenceOfGaussian() {
//        Mat grayMat = new Mat();
//        Mat blur1 = new Mat();
//        Mat blur2 = new Mat();
//        //Converting the image to grayscale
//        Imgproc.cvtColor(mat, grayMat, Imgproc.COLOR_BGR2GRAY);
//        //Bluring the images using two different blurring radius
//        Imgproc.GaussianBlur(grayMat, blur1, new Size(15, 15), 5);
//        Imgproc.GaussianBlur(grayMat, blur2, new Size(21, 21), 5);
//        //Subtracting the two blurred images
//        Mat DoG = new Mat();
//        Core.absdiff(blur1, blur2, DoG);
//        //Inverse Binary Thresholding
//        Core.multiply(DoG, new Scalar(100), DoG);
//        Imgproc.threshold(DoG, DoG, 50, 255, Imgproc.THRESH_BINARY_INV);
//        //Converting Mat back to Bitmap
//        Utils.matToBitmap(DoG, bitmap);
//
//    }
}
