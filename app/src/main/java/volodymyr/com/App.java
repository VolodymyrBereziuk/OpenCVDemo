package volodymyr.com;

import android.app.Application;
import android.content.Context;

import com.instabug.library.Instabug;
import com.instabug.library.invocation.InstabugInvocationEvent;

/**
 * Created by User on 1/31/2018.
 */

public class App extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        new Instabug.Builder(this, "b4193eefa1fb81951bc197e718777ccc")
                .setInvocationEvent(InstabugInvocationEvent.SHAKE)
                .build();

        App.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return App.context;
    }
}
