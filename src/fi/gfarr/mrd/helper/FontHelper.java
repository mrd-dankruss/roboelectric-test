package fi.gfarr.mrd.helper;

public class FontHelper
{
	public static String FONT_ROBOTO = "fonts/Roboto";
	
	public static String FONT_TYPE_TTF = "ttf";
	//public static String FONT_TYPE_OTF = "otf";
	public static String FONT_TYPE_OTF = "ttf";
	
	public static int STYLE_NONE = 0x0;
	public static int STYLE_REGULAR = 0x1;
	public static int STYLE_ITALIC = 0x2;
	public static int STYLE_BOLD = 0x4;
	public static int STYLE_COND = 0x8;
	public static int STYLE_SEMI = 0x10;
	
	public static String getFontString(String font, String type, int style_byte)
	{
		String font_append = "";
		
		boolean a = (style_byte & STYLE_REGULAR) != 0;
		boolean b = (style_byte & STYLE_ITALIC) != 0;
		boolean c = (style_byte & STYLE_BOLD) != 0;
		boolean d = (style_byte & STYLE_COND) != 0;
		boolean e = (style_byte & STYLE_SEMI) != 0;
		//boolean f = (style_byte & 0x20) != 0;
		//boolean g = (style_byte & 0x40) != 0;
		//boolean h = (style_byte & 0x80) != 0;
		
		if (a == true)
		{
			font_append += "Regular";
		}
		if (c == true)
		{
			font_append += "Bold";
		}
		if (d == true)
		{
			font_append += "Cond";
		}
		if (e == true)
		{
			font_append += "Semibold";
		}
		if (b == true)
		{
			font_append += "It";
		}
		
		if (font_append.compareTo("") == 0)
		{
			return font+"."+type;
		}
		else
		{
			//Log.i("[FontHelper]", "Font generated is |"+font+"-"+font_append+"."+type+"|");
			return font+"-"+font_append+"."+type;
		}
	}
}
