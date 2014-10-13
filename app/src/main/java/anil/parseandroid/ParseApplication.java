package anil.parseandroid;

import android.app.Application;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.parse.SaveCallback;

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

        ParsePush.subscribeInBackground("", new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.d("com.parse.push", "successfully subscribed to the broadcast channel.");
                } else {
                    Log.e("com.parse.push", "failed to subscribe for push", e);
                }
            }
        });
    }
}
