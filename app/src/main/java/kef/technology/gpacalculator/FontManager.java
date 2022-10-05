package kef.technology.gpacalculator;

import android.graphics.Typeface;
import android.content.Context;

public class FontManager
{
	public static final String ROOT = "fonts/";
	public static final String FONTAWESOME = ROOT + "fontawesome.ttf";
	
	public static Typeface getTypeface(Context context, String font){
		return Typeface.createFromAsset(context.getAssets(), font);
	}
}
