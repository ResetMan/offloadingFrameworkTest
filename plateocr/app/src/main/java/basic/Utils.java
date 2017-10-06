package basic;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * Utils 工具包
 * 
 * @author csh
 *
 */
public class Utils {

	public static String selfIP; // 节点自身的IP

	// Java 内置的包装类数组
	public static Class[] JAVA_WRAPPER_CLASS = { Integer.class, Boolean.class, Character.class, Long.class, Float.class,
			Double.class, Byte.class, Short.class };
	// Java 内置的基本类型数组
	public static Class[] JAVA_BASE_CLASS = { int.class, boolean.class, char.class, long.class, float.class,
			double.class, byte.class, short.class };

	public final static String PROXY_SUFFIX = "$Proxy"; // 代理对象的类名后缀

	public static Map<String, Integer> Nodes = new HashMap<String, Integer>(); // 保存当前节点可以连接的计算节点的IP以及port 默认Cloud是第一个

	public static int selfPort; // 节点自身作为服务器的端口号

	public static String cloudIP; // Cloud的IP

	/**
	 * 获取节点自身IP
	 * 
	 * @return 节点IP
	 */
	public static String getSelfIP() {
		selfIP = "";
		return selfIP;
	}

	/**
	 * 
	 * @param clazz
	 *            构造器所属的类类型
	 * @param params
	 *            构造器参数
	 * @return 适当的构造器
	 */
	public static Constructor<?> getSuitableConstructor(Class<?> clazz, Object[] params) {
		Constructor<?> suitableConstructor = null;
		int paramLength = params.length;
		Constructor[] constructors = clazz.getDeclaredConstructors(); // 获取该类的所有构造器
		for (Constructor<?> candidator : constructors) {
			Class[] candidatorParamTypes = candidator.getParameterTypes(); // 获取候选构造器的参数 类型
			int candidatorParamLength = candidator.getParameterTypes().length; // 获取候选构造器的参数个数
			if (candidatorParamLength == paramLength) { // 判断参数个数是否相同
				boolean flag = true;
				for (int i = 0; i < candidatorParamLength; i++) { // 判断参数的类型是否符合
					if (!isOrSubOrIntf(candidatorParamTypes[i], params[i].getClass())) {
						flag = false;
						break;
					}
				}
				if (flag) {
					suitableConstructor = candidator;
					break;
				}
			}
		}

		return suitableConstructor;

	}

	/**
	 * 
	 * @param clazz
	 *            方法对应的类类型
	 * @param methodName
	 *            方法名
	 * @param params
	 *            方法参数类型
	 * @return 适当的方法
	 */
	public static Method getSuitableMethod(Class<?> clazz, String methodName, Class[] params) {
		Method suitableMethod = null;
		int paramLength = params.length;
		Method[] methods = clazz.getDeclaredMethods(); // 获取类所有的方法
		for (Method candidator : methods) {
			Class[] candidatorParamTypes = candidator.getParameterTypes(); // 获取候选方法的参数类型
			int candidatorParamLength = candidator.getParameterTypes().length; // 获取候选方法的参数个数
			if (candidatorParamLength == paramLength && candidator.getName().equals(methodName)) { // 如果参数个数符合并且方法名一致
				boolean flag = true;
				for (int i = 0; i < candidatorParamLength; i++) {
					if (!isOrSubOrIntf(candidatorParamTypes[i], params[i])) { // 判断方法参数类型是否符合
						flag = false;
						break;
					}
				}
				if (flag) {
					suitableMethod = candidator;
					break;
				}
			}
		}

		return suitableMethod;

	}

	/**
	 * 
	 * @param class1
	 *            类型1
	 * @param class2
	 *            类型2
	 * @return 类型是否匹配
	 */
	private static boolean isOrSubOrIntf(Class class1, Class<? extends Object> class2) {
		// TODO Auto-generated method stub
		Class[] interfaces = class2.getInterfaces(); // 获取类型2的所继承的接口
		if (class1 == Object.class) { // 如果类型1是Object 则 不管类型2是什么都符合
			return true;
		}
		if (class1 == class2) { // 如果类型1 == 类型2 则 符合
			return true;
		}
		if (class1 == class2.getSuperclass()) { // 如果类型1是类型2 的父类 则符合
			return true;
		}

		for (Class intf : interfaces) { // 如果类型1 是类型2实现的一个接口 则符合
			if (intf == class1) {
				return true;
			}
		}

		for (int i = 0; i < JAVA_WRAPPER_CLASS.length; i++) { // 判断是不是包装器和基本类型的关系
			if (class1 == JAVA_BASE_CLASS[i] && class2 == JAVA_WRAPPER_CLASS[i]) {
				return true;
			}
		}

		return false;
	}

	/**
	 * 
	 * @param IP
	 *            IP
	 * @param object
	 *            生成的对象
	 * @return 对象ID
	 */
	public static String getID(String IP, Object object) {
		String objectID = IP + "$" + object.hashCode();
		return objectID;
	}

	/**
	 * 
	 * @param objectID
	 *            对象ID
	 * @return 对象的HashCode
	 */
	public static int getHashCode(String objectID) {
		String hashCode = objectID.substring(objectID.lastIndexOf("$") + 1);
		return Integer.parseInt(hashCode);
	}

	/**
	 * 
	 * @param object
	 *            一个对象
	 * @return 判断该对象是否是代理对象还是不是
	 */
	public static boolean isProxy(Object object) {
		// TODO Auto-generated method stub
		String clazzName = object.getClass().getName();
		return clazzName.contains(PROXY_SUFFIX);
	}

	/**
	 * 
	 * @param proxy
	 *            代理对象
	 * @return 发送时候， Proxy转换成PlaceHolder对象
	 */
	public static Object proxy2plalecHolder(Object proxy) {
		// TODO Auto-generated method stub
		String proxyID = ObjectFactory.PROXY_ID_MAP.get(proxy);
		String proxyLoc = ObjectFactory.ID_LOC_MAP.get(proxyID);
		Object proxyCopy = ObjectFactory.ID_OBJ_MAP.get(proxyID);
		PlaceHolder ph = new PlaceHolder(proxyCopy, proxyID, proxyLoc);
		return null;
	}

	/**
	 * 
	 * @param placeHolder
	 *            PlaceHolder
	 * @return 接收时候， 将PlaceHolder转换成Proxy对象
	 */
	public static Object placeHolder2proxy(PlaceHolder placeHolder) {
		String ID = placeHolder.getID();
		String Loc = placeHolder.getLoc();
		Object copy = placeHolder.getCopy();
		Object proxy = null;
		if (ObjectFactory.ID_LOC_MAP.containsKey(ID)) {
			ObjectFactory.ID_LOC_MAP.put(ID, Loc);
			proxy = ObjectFactory.ID_PROXY_MAP.get(ID);
		} else {
			proxy = ProxyFactory.getProxy(copy, ID);
			ObjectFactory.ID_LOC_MAP.put(ID, Loc);
			ObjectFactory.ID_OBJ_MAP.put(ID, copy);
			ObjectFactory.ID_PROXY_MAP.put(ID, proxy);
			ObjectFactory.PROXY_ID_MAP.put(proxy, ID);
		}
		return proxy;
	}

	/**
	 * 
	 * @param IP
	 *            Edge或Cloud的IP
	 * @return 网络连接
	 */
	public static Socket getSocket(String IP) {
		Socket temp = new Socket();
		if (Nodes.containsKey(IP)) {
			int port = Nodes.get(IP);
			GetSocket gs = new GetSocket(IP, port);
			Thread gst = new Thread(gs);
			gst.start();
			try {
				gst.join();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			temp = gs.getSocket();
		} else {

		}
		return temp;
	}

}
