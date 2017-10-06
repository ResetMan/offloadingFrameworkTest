package basic;

import java.lang.reflect.Proxy;

/**
 * ProxyFactory 生成对象代理的工厂
 * 
 * @author csh
 *
 */
public class ProxyFactory {
	/**
	 * 
	 * @param proxied
	 *            所要代理的对象
	 * @param ID
	 *            对象ID
	 * @return
	 */
	public static Object getProxy(Object proxied, String ID) {
		return Proxy.newProxyInstance(proxied.getClass().getClassLoader(), proxied.getClass().getInterfaces(),
				new EndPoint(ID));
	}
}
