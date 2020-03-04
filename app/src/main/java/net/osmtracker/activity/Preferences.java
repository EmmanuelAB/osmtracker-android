package net.osmtracker.activity;

import java.io.File;
import java.io.FilenameFilter;

import net.osmtracker.OSMTracker;
import net.osmtracker.R;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;

import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import lib.folderpicker.FolderPicker;


/**
 * Manages preferences screen.
 * 
 * @author Nicolas Guillaumin
 *
 */
public class Preferences extends PreferenceActivity {

	@SuppressWarnings("unused")
	private static final String TAG = Preferences.class.getSimpleName();
	
	/**
	 * Directory containing user layouts, relative to storage dir.
	 */
	public static final String LAYOUTS_SUBDIR = "layouts";
	
	/**
	 * File extension for layout files
	 */

	public static final String LAYOUT_FILE_EXTENSION = ".xml";

	/**
	 * The suffix that must be added to the layout's name for getting its icons directory
	 * Example: water_supply       <- layout name
	 *          water_supply_icons <- icon directory
	 */

	public static final String ICONS_DIR_SUFFIX = "_icons";

	private static final int STORAGE_PERMISSION_REQUEST_CODE = 1;
	private static final int  PICK_FOLDER_REQUEST_CODE = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);

		// Set summary of some preferences to their actual values
		// and register a change listener to set again the summary in case of change
		final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

		// External storage directory
		Preference storageDirPref = (Preference) findPreference(OSMTracker.Preferences.KEY_STORAGE_DIR);
		storageDirPref.setSummary(prefs.getString(OSMTracker.Preferences.KEY_STORAGE_DIR, OSMTracker.Preferences.VAL_STORAGE_DIR));
		storageDirPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				launchFolderPicker();
				return true;
			}
		});
		//		storageDirPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
//			@Override
//			public boolean onPreferenceChange(Preference preference, Object newValue) {
//				// Ensure there is always a leading slash
//				if (! ((String) newValue).startsWith(File.separator)) {
//					newValue = File.separator + (String) newValue;
//				}
//
//				// Set summary with the directory value
//				preference.setSummary((String) newValue);
//
//				return true;
//			}
//		});

		// Voice record duration
		Preference pref = findPreference(OSMTracker.Preferences.KEY_VOICEREC_DURATION);
		pref.setSummary(prefs.getString(OSMTracker.Preferences.KEY_VOICEREC_DURATION, OSMTracker.Preferences.VAL_VOICEREC_DURATION) + " " + getResources().getString(R.string.prefs_voicerec_duration_seconds));
		pref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				// Set summary with the number of seconds, following by "seconds"
				preference.setSummary(newValue+ " " + getResources().getString(R.string.prefs_voicerec_duration_seconds));
				return true;
			}
		});

		// Update GPS logging interval summary to the current value
		pref = findPreference(OSMTracker.Preferences.KEY_GPS_LOGGING_INTERVAL);
		pref.setSummary(
				prefs.getString(OSMTracker.Preferences.KEY_GPS_LOGGING_INTERVAL, OSMTracker.Preferences.VAL_GPS_LOGGING_INTERVAL)
						+ " " + getResources().getString(R.string.prefs_gps_logging_interval_seconds)
						+ ". " + getResources().getString(R.string.prefs_gps_logging_interval_summary));
		pref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				// Set summary with the interval and "seconds"
				preference.setSummary(newValue
						+ " " + getResources().getString(R.string.prefs_gps_logging_interval_seconds)
						+ ". " + getResources().getString(R.string.prefs_gps_logging_interval_summary));
				return true;
			}
		});

		// Update GPS min. distance summary to the current value
		pref = findPreference(OSMTracker.Preferences.KEY_GPS_LOGGING_MIN_DISTANCE);
		pref.setSummary(
				prefs.getString(OSMTracker.Preferences.KEY_GPS_LOGGING_MIN_DISTANCE, OSMTracker.Preferences.VAL_GPS_LOGGING_MIN_DISTANCE)
						+ " " + getResources().getString(R.string.prefs_gps_logging_min_distance_meters)
						+ ". " + getResources().getString(R.string.prefs_gps_logging_min_distance_summary));
		pref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				// Set summary with the interval and "seconds"
				preference.setSummary(newValue
						+ " " + getResources().getString(R.string.prefs_gps_logging_min_distance_meters)
						+ ". " + getResources().getString(R.string.prefs_gps_logging_min_distance_summary));
				return true;
			}
		});

		// don't allow the logging_min_distance to be empty
		final EditText et = ((EditTextPreference)pref).getEditText();
		final EditTextPreference etp = (EditTextPreference)pref;
		et.addTextChangedListener(
				new TextWatcher() {
					@Override
					public void beforeTextChanged(CharSequence s, int start, int count, int after) {
					}

					@Override
					public void onTextChanged(CharSequence s, int start, int before, int count) {
						if (s.length() >= 0) {
							try {
								Button bt_ok = ((AlertDialog) etp.getDialog()).getButton(AlertDialog.BUTTON_POSITIVE);
								if (s.length() == 0) {
									bt_ok.setEnabled(false);
								} else {
									((AlertDialog) etp.getDialog()).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
								}
							} catch (Exception ex) {
							}
						}
					}

					@Override
					public void afterTextChanged(Editable s) {
					}
				}
		);

		pref = findPreference(OSMTracker.Preferences.KEY_GPS_OSSETTINGS);
		pref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
				return true;
			}
		});

		// Button screen orientation option
		pref = findPreference(OSMTracker.Preferences.KEY_UI_ORIENTATION);
		ListPreference orientationListPreference = (ListPreference) pref;
		String displayValueKey = prefs.getString(OSMTracker.Preferences.KEY_UI_ORIENTATION, OSMTracker.Preferences.VAL_UI_ORIENTATION);
		int displayValueIndex = orientationListPreference.findIndexOfValue(displayValueKey);
		String displayValue = orientationListPreference.getEntries()[displayValueIndex].toString();
		orientationListPreference.setSummary(displayValue + ".\n" 
				+ getResources().getString(R.string.prefs_ui_orientation_summary));

		// Set a listener to update the preference display after a change is made
		pref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				// Set summary with the display text of the item and a description of the preference
				ListPreference orientationListPreference = (ListPreference)preference;
				// Pull the display string from the list preference rather than simply using the key value
				int newValueIndex = orientationListPreference.findIndexOfValue((String)newValue);
				String newPreferenceDisplayValue = orientationListPreference.getEntries()[newValueIndex].toString();

				preference.setSummary(newPreferenceDisplayValue
						+ ".\n" + getResources().getString(R.string.prefs_ui_orientation_summary));
				return true;
			}
		});

		// Clear OSM data: Disable if there's no OSM data stored
		pref = findPreference(OSMTracker.Preferences.KEY_OSM_OAUTH_CLEAR_DATA);
		if (prefs.contains(OSMTracker.Preferences.KEY_OSM_OAUTH_TOKEN)
				&& prefs.contains(OSMTracker.Preferences.KEY_OSM_OAUTH_SECRET)) {
			pref.setEnabled(true);
		} else {
			pref.setEnabled(false);
		}
		pref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				// Clear data
				Editor editor = prefs.edit();
				editor.remove(OSMTracker.Preferences.KEY_OSM_OAUTH_TOKEN);
				editor.remove(OSMTracker.Preferences.KEY_OSM_OAUTH_SECRET);
				editor.commit();

				preference.setEnabled(false);
				return false;
			}
		});

	}

	private void launchFolderPicker(){
		int writePermission = ContextCompat.checkSelfPermission(Preferences.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

		// Check for storage permissions before launching the activity
		// since it's required to list directories and pick one
		if ( writePermission != PackageManager.PERMISSION_GRANTED) {
			// request the permission
			ActivityCompat.requestPermissions(Preferences.this,
					new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
					STORAGE_PERMISSION_REQUEST_CODE);

		} else {
			// Permission granted
			Intent intent = new Intent(Preferences.this, FolderPicker.class);
			startActivityForResult(intent, PICK_FOLDER_REQUEST_CODE);
		}


	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if( resultCode == RESULT_OK && requestCode == PICK_FOLDER_REQUEST_CODE){
			String folderLocation = data.getExtras().getString("data");
			Log.e( "#", "folder:"+folderLocation );

			// take only the last folder name
			int i = folderLocation.lastIndexOf("0");
			folderLocation = folderLocation.substring(i+1, folderLocation.length());


			// save the directory
			PreferenceManager.getDefaultSharedPreferences(this).edit().putString(
					OSMTracker.Preferences.KEY_STORAGE_DIR,
					folderLocation).commit();

			// update the summary in UI
			Preference storageDirPref = (Preference) findPreference(OSMTracker.Preferences.KEY_STORAGE_DIR);
			storageDirPref.setSummary(folderLocation);

		}

	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		switch (requestCode) {
			case STORAGE_PERMISSION_REQUEST_CODE: {
				// If GRANTED
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
					launchFolderPicker();
				else // DENIED
					Toast.makeText(Preferences.this, "Storage permission is required for this action", Toast.LENGTH_LONG).show();
				return;
			}
		}
	}
}
