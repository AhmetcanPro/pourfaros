package tools;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import main.Constants;
import objects.Player;

public class Tools {
	
	public static String encode_utf8(String string) {
		return unescape(encodeURIComponent(string));
	}
	
	public static String decode_utf8(String string) {
		return decodeURIComponent(escape(string));
	}

	public static String decodeURIComponent(String string) {
		try {
			return URLDecoder.decode(string, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String unescape(String string) {
		ScriptEngineManager factory = new ScriptEngineManager();
		   ScriptEngine engine = factory.getEngineByName("JavaScript");
		   ScriptContext context = engine.getContext();
		   try {
			engine.eval("function decodeStr(encoded){"
			             + "var result = unescape(encoded);"
			             + "return result;"
			             + "};",context);
		} catch (ScriptException e1) {
			e1.printStackTrace();
		}

		    Invocable inv;   

		    inv = (Invocable) engine;
		    try {
				String res =  (String)inv.invokeFunction("decodeStr", new Object[]{string});
				return res;
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (ScriptException e) {
				e.printStackTrace();
			}
		 return null;
	}
	
	public static String escape(String string) {
		ScriptEngineManager factory = new ScriptEngineManager();
		   ScriptEngine engine = factory.getEngineByName("JavaScript");
		   ScriptContext context = engine.getContext();
		   try {
			engine.eval("function encodeStr(decoded){"
			             + "var result = escape(decoded);"
			             + "return result;"
			             + "};",context);
		} catch (ScriptException e1) {
			e1.printStackTrace();
		}

		    Invocable inv;   

		    inv = (Invocable) engine;
		    try {
				String res =  (String)inv.invokeFunction("encodeStr", new Object[]{string});
				return res;
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (ScriptException e) {
				e.printStackTrace();
			}
		 return null;
	}
	
	public static String encodeURIComponent(String string) {
		try {
			String encoded = URLEncoder.encode(string, "UTF-8")
			        .replaceAll("\\+", "%20")
			        .replaceAll("\\%21", "!")
			        .replaceAll("\\%27", "'")
			        .replaceAll("\\%28", "(")
			        .replaceAll("\\%29", ")")
			        .replaceAll("\\%7E", "~");
			return encoded;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static int getAngle(Player player, int a) {
	    int angle = (int) Math.toDegrees(Math.atan2(player.getMouseY() - player.getY(), player.getMouseX() - player.getX()));
	    angle -= a;

	    if(angle < 0){
	        angle += 360;
	    }
	    if(angle > 360) angle = 0;

	    return angle;
	}
	
	public static int getNextLogicalAnimal(Player player) {
		if(player.getScore() >= Constants.HIPPO_GROWTH) return Constants.DINO;
		if(player.getScore() >= Constants.RHINO_GROWTH) return Constants.HIPPO;
		if(player.getScore() >= Constants.CROC_GROWTH) return Constants.RHINO;
		if(player.getScore() >= Constants.BEAR_GROWTH) return Constants.CROC;
		if(player.getScore() >= Constants.CHEETAH_GROWTH) return Constants.BEAR;
		if(player.getScore() >= Constants.LION_GROWTH) return Constants.CHEETAH;
		if(player.getScore() >= Constants.ZEBRA_GROWTH) return Constants.LION;
		if(player.getScore() >= Constants.MOLE_GROWTH) return Constants.ZEBRA;
		if(player.getScore() >= Constants.DEER_GROWTH) return Constants.MOLE;
		if(player.getScore() >= Constants.FOX_GROWTH) return Constants.DEER;
		if(player.getScore() >= Constants.PIG_GROWTH) return Constants.FOX;
		if(player.getScore() >= Constants.RABBIT_GROWTH) return Constants.PIG;
		if(player.getScore() >= Constants.MOUSE_GROWTH) return Constants.RABBIT;
		return Constants.MOUSE;
	}
	
	public static int getNextAnimalGrowth(Player player) {
		switch(player.getAnimal()) {
		case Constants.MOUSE:
			return Constants.MOUSE_GROWTH;
		case Constants.RABBIT:
			return Constants.RABBIT_GROWTH;
		case Constants.PIG:
			return Constants.PIG_GROWTH;
		case Constants.FOX:
			return Constants.FOX_GROWTH;
		case Constants.DEER:
			return Constants.DEER_GROWTH;
		case Constants.MOLE:
			return Constants.MOLE_GROWTH;	
		case Constants.ZEBRA:
			return Constants.ZEBRA_GROWTH;	
		case Constants.LION:
			return Constants.LION_GROWTH;
		case Constants.CHEETAH:
			return Constants.CHEETAH_GROWTH;
		case Constants.BEAR:
			return Constants.BEAR_GROWTH;	
		case Constants.CROC:
			return Constants.CROC_GROWTH;
		case Constants.RHINO:
			return Constants.RHINO_GROWTH;
		case Constants.HIPPO:
			return Constants.HIPPO_GROWTH;	
		case Constants.DINO:
			return Constants.DINO_GROWTH;
		}
		return Constants.DINO_GROWTH;
	}
	
	public static boolean fastInMud(Player player) {
		switch(player.getAnimal()) {
		case Constants.MOUSE:
			return false;
		case Constants.RABBIT:
			return false;
		case Constants.PIG:
			return true;
		case Constants.FOX:
			return false;
		case Constants.LION:
			return false;
		case Constants.CHEETAH:
			return false;	
		case Constants.CROC:
			return true;
		case Constants.DEER:
			return false;
		case Constants.MOLE:
			return false;	
		case Constants.BEAR:
			return false;	
		case Constants.RHINO:
			return false;	
		case Constants.HIPPO:
			return true;	
		case Constants.ZEBRA:
			return false;	
		}
		return true;
	}
	
	public static int getMinSize(Player player) {
		switch(player.getAnimal()) {
		case Constants.MOUSE:
			return Constants.MOUSE_MIN;
		case Constants.RABBIT:
			return Constants.RABBIT_MIN;
		case Constants.PIG:
			return Constants.PIG_MIN;
		case Constants.FOX:
			return Constants.FOX_MIN;
		case Constants.LION:
			return Constants.LION_MIN;
		case Constants.CHEETAH:
			return Constants.CHEETAH_MIN;	
		case Constants.CROC:
			return Constants.CROC_MIN;
		case Constants.DEER:
			return Constants.DEER_MIN;
		case Constants.MOLE:
			return Constants.MOLE_MIN;	
		case Constants.BEAR:
			return Constants.BEAR_MIN;	
		case Constants.RHINO:
			return Constants.RHINO_MIN;	
		case Constants.HIPPO:
			return Constants.HIPPO_MIN;	
		case Constants.ZEBRA:
			return Constants.ZEBRA_MIN;	
		}
		return Constants.MOUSE_MIN;
	}

}
