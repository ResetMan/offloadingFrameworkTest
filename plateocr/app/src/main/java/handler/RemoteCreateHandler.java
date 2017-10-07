package handler;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import basic.ObjectFactory;
import basic.PlaceHolder;
import basic.ProxyFactory;
import basic.Utils;

/**
 * RemoteCreateHandler 远程创建请求处理器
 * 
 * @author csh
 *
 */
public class RemoteCreateHandler extends Handler implements Handleable {

	public RemoteCreateHandler(Map requestMap) {
		super(requestMap);
		// TODO Auto-generated constructor stub
	}

	/**
	 * remoteCreate创建的结果是copy和objectID
	 * @return
     */
	@Override
	public Map handle() {
		// TODO Auto-generated method stub
		Class clazz = (Class) requestMap.get("clazz"); // 需要创建的对象类型
		Constructor<?> constructor = null;
		Object remoteObject = null;
		Object remoteProxy = null;
		String srcIP = (String) requestMap.get("srcIP"); // 发起创建对象的IP 用来作为后面对象ID生成的一个变量 这边可以看成是Mobile
		if (requestMap.containsKey("sendParams")) { // 如果有参创建
			Object[] params = (Object[]) requestMap.get("sendParams"); // 处理参数 也是PlaceHolder->Proxy
			int paramLength = params.length;
			for (int i = 0; i < paramLength; i++) {
				if (params[i].getClass() == PlaceHolder.class) {
					PlaceHolder ph = PlaceHolder.class.cast(params[i]);
					params[i] = Utils.placeHolder2proxy(ph);
				}
			}
			constructor = Utils.getSuitableConstructor(clazz, params); // 获取合适的构造器
			try {
				remoteObject = constructor.newInstance(params); // 创建对象
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
		} else {
			try {
				remoteObject = clazz.newInstance();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		String objectID = Utils.getID(srcIP, remoteObject); // 生成对象ID
		remoteProxy = ProxyFactory.getProxy(remoteObject, objectID); // 生成对象代理
		// 存入信息
		ObjectFactory.ID_OBJ_MAP.put(objectID, remoteObject);
		ObjectFactory.ID_LOC_MAP.put(objectID, Utils.selfIP);
		ObjectFactory.PROXY_ID_MAP.put(remoteProxy, objectID);
		ObjectFactory.ID_PROXY_MAP.put(objectID, remoteProxy);
		// 返回结果
		resultMap.put("copy", remoteObject);
		resultMap.put("objectID", objectID);
		return resultMap;
	}

}
