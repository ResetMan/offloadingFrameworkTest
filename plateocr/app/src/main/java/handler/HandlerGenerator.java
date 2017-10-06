package handler;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * HandlerGenerator 请求处理类的生成器
 * 
 * @author csh
 *
 */
public class HandlerGenerator {

	/**
	 * 
	 * @param type
	 *            请求处理类的type
	 * @param requestMap
	 *            请求信息
	 * @return 请求处理类对象
	 */
	public static Handleable getHandler(String type, Map requestMap) {
		Handleable handler = null;
		try {
			Class clazz = Class.forName("Handler." + type); // 获取对应请求处理类的类类型
			Constructor<?> constructor = clazz.getDeclaredConstructor(Map.class); // 得到请求处理类的构造器
			handler = (Handleable) constructor.newInstance(requestMap); // 生成对应的请求处理类
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return handler;
	}
}
