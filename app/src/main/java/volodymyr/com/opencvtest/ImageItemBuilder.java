package volodymyr.com.opencvtest;

import android.graphics.Bitmap;

public class ImageItemBuilder {
    private Bitmap image;

    public ImageItemBuilder setImage(Bitmap image) {
        this.image = image;
        return this;
    }

    public ImageItem createImageItem() {
        return new ImageItem(image);
    }
}