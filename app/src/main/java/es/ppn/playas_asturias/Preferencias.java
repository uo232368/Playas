package es.ppn.playas_asturias;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.widget.Toast;

public class Preferencias extends PreferenceActivity {

    private static final String KEY_SOLO_WIFI = "btn_solo_wifi";
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
        CheckBoxPreference soloWifi = (CheckBoxPreference) findPreference(KEY_SOLO_WIFI);

        prefs = getSharedPreferences(MainActivity.PREFERENCES, Context.MODE_PRIVATE);
        soloWifi.setChecked(prefs.getBoolean(MainActivity.PREFERENCES_WIFI_ONLY,false));


        soloWifi.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                //Toast.makeText(getBaseContext(), newValue.toString() + "", Toast.LENGTH_LONG).show();
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean(MainActivity.PREFERENCES_WIFI_ONLY,(Boolean)newValue);

                editor.commit();


                return true;
            }
        });


    }

}