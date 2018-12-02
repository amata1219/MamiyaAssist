package amata1219.mamiya.assist;

import java.lang.reflect.Field;

public class Reflection {

	public static Class<?> getReflectionClass(String s){
		Class<?> c = null;
		try{
			c = Class.forName(s);
		}catch(ClassNotFoundException e){
			e.printStackTrace();
		}
		return c;
	}

	public static Field getReflectionField(Object obj, String s){
		try{
			Field f = obj.getClass().getDeclaredField(s);
			f.setAccessible(true);
			return f;
		}catch(SecurityException e){
			e.printStackTrace();
		}catch(NoSuchFieldException e){
			e.printStackTrace();
		}catch(IllegalArgumentException e){
			e.printStackTrace();
		}
		return null;
	}

	public static Field getReflectionSuperField(Object obj, String s){
		try{
			Field f = obj.getClass().getSuperclass().getDeclaredField(s);
			f.setAccessible(true);
			return f;
		}catch(SecurityException e){
			e.printStackTrace();
		}catch(NoSuchFieldException e){
			e.printStackTrace();
		}catch(IllegalArgumentException e){
			e.printStackTrace();
		}
		return null;
	}

	public static Object getReflectionValue(Field f, Object obj){
		try {
			return f.get(obj);
		}catch(IllegalArgumentException e){
			e.printStackTrace();
		}catch(IllegalAccessException e){
			e.printStackTrace();
		}
		return null;
	}

}
