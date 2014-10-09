package anil.parseandroid;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseUser;

/**
 * Created by Anil on 10/1/2014.
 */
public class ParseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // initialization code
        Parse.initialize(this, "w2akEFYvsfFnLYro0PVaH2phaoK50n97pNuvFV4T",
                "CPHtfLdfnNTaFQ98OZgZqeHlDRehgrLEgjbYa9cb");

        ParseUser.enableAutomaticUser();
        ParseACL defaultACL = new ParseACL();

        defaultACL.setPublicReadAccess(true);

        ParseACL.setDefaultACL(defaultACL, true);
    }
}
