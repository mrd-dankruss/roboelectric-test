package fi.gfarr.mrd.helper;

/**
 * @author Kevin
 */

public class FontHelper
{
	public static String FONT_ROBOTO = "fonts/roboto/Roboto-";

	public static String FONT_TYPE_TTF = "ttf";

	public static int STYLE_REGULAR = 0x0;
	public static int STYLE_THIN = 0x1;
	public static int STYLE_LIGHT = 0x2;
	public static int STYLE_MEDIUM = 0x4;
	public static int STYLE_ITALIC = 0x8;
	public static int STYLE_BOLD = 0x10;
	public static int STYLE_BLACK = 0x32;

	public static String getFontString(String font, String type, int style_byte)
	{
		boolean a = (style_byte & STYLE_THIN) != 0;
		boolean b = (style_byte & STYLE_LIGHT) != 0;
		boolean c = (style_byte & STYLE_MEDIUM) != 0;
		boolean d = (style_byte & STYLE_ITALIC) != 0;
		boolean e = (style_byte & STYLE_BOLD) != 0;
		boolean f = (style_byte & STYLE_BLACK) != 0;

		if (a && d)
		{
			font += "ThinItalic";
		}
		else if (b && d)
		{
			font += "LightItalic";
		}
		else if (c && d)
		{
			font += "MediumItalic";
		}
		else if (e && d)
		{
			font += "BoldItalic";
		}
		else if (f && d)
		{
			font += "BlackItalic";
		}
		else if (a)
		{
			font += "Thin";
		}
		else if (b)
		{
			font += "Light";
		}
		else if (c)
		{
			font += "Medium";
		}
		else if (d)
		{
			font += "Italic";
		}
		else if (e)
		{
			font += "Bold";
		}
		else if (f)
		{
			font += "Black";
		}
		else
		{
			font += "Regular";
		}
		return font + "." + type;
	}
}
