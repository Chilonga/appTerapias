package aplication.android.wimervm.appterapias;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class AppTerapias extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

}
