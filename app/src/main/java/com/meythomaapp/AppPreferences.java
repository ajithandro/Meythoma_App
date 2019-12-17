package com.meythomaapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

/**
 * 
 * @author i-velozity
 * * created By:Surendra
 * 
 * This class will store some values in android shared prefences,
 * these values will be used for authentication and to check whether the app is installed or not and to set access code dynamically from webpage.
 *
 */
public class AppPreferences {
	Context context;
	public AppPreferences(Context c) {
		// TODO Auto-generated constructor stub
		context=c;
	}

	public void SavePreferences(String key, String value, String type){
		if(Constants.DEBUGGING) System.out.println("   key saved");
		SharedPreferences sharedPreferences=null;
		if(type.equalsIgnoreCase(Constants.PUBLIC_PREFERENCE)){
			sharedPreferences = context.getSharedPreferences(Constants.PUBLIC_PREFERENCE_NAME, Context.MODE_WORLD_READABLE);
		}else if(type.equalsIgnoreCase(Constants.PRIVATE_PREFERENCE)){
			sharedPreferences = context.getSharedPreferences(Constants.PRIVATE_PREFERENCE_NAME, Context.MODE_PRIVATE);
		}


		SharedPreferences.Editor editor = sharedPreferences.edit();
		if ( value==null || value.equalsIgnoreCase(" "))
			value = null;
	    else
	    	value = Base64.encodeToString( value.getBytes(), Base64.DEFAULT );
		editor.putString(key, value);
		editor.commit();

	}

	void SavePreferences(String key, boolean value, String type){

		if(Constants.DEBUGGING) System.out.println("  GOT IPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPP "+key);

		SharedPreferences sharedPreferences=null;
		if(type.equalsIgnoreCase(Constants.PUBLIC_PREFERENCE)){
			sharedPreferences = context.getSharedPreferences(Constants.PUBLIC_PREFERENCE_NAME, Context.MODE_WORLD_READABLE);
		}else if(type.equalsIgnoreCase(Constants.PRIVATE_PREFERENCE)){
			sharedPreferences = context.getSharedPreferences(Constants.PRIVATE_PREFERENCE_NAME, Context.MODE_PRIVATE);
		}

		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}



	public String getStringPreference(Context context, String keyString){

		SharedPreferences sharedPrefs = context.getSharedPreferences(Constants.PRIVATE_PREFERENCE_NAME, Context.MODE_PRIVATE);
		String homeurl=new String( Base64.decode( sharedPrefs.getString(keyString, ""), Base64.DEFAULT ) );//sharedPrefs.getString(Constants.PREFERENCES_ACCESS_CODE, "0");
		System.out.println("#^&*()!@#$%^&*((()~~!@#$%^&*( "+homeurl);
		return homeurl;
	}
	public boolean getBooleanPreference(Context context, String keyString){

		SharedPreferences sharedPrefs = context.getSharedPreferences(Constants.PRIVATE_PREFERENCE_NAME, Context.MODE_PRIVATE);
		boolean boolval= sharedPrefs.getBoolean(keyString, false);//sharedPrefs.getString(Constants.PREFERENCES_ACCESS_CODE, "0");
		System.out.println("#^&*()!@#$%^&*((()~~!@#$%^&*( "+boolval);
		return boolval;
	}

}