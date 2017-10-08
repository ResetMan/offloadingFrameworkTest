package basic;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import request.Request;

/**
 * ObjectFactory 负责对象的创建 迁移以及对象的管理 其中创建包括 远程创建 本地创建 对象迁移包括 迁移出去 迁移回来以及转发迁移
 * 
 * @author csh
 *
 */
public class ObjectFactory {

	public static Map<String, Object> ID_OBJ_MAP = new HashMap<String, Object>();// 对象ID-对象Copy
	public static Map<String, String> ID_LOC_MAP = new HashMap<String, String>();// 对象ID-对象Loc
	public static Map<Object, String> PROXY_ID_MAP = new HashMap<Object, String>();// 对象代理-对象ID
	public static Map<String, Object> ID_PROXY_MAP = new HashMap<String, Object>();// 对象ID-对象代理

	public static Map<String, String> SUP_ID_LOC_MAP = new HashMap<String, String>(); // 对象ID-对象SUP_LOC

	/**
	 * 
	 * @param loc
	 *            创建的位置
	 * @param clazz
	 *            创建的对象类型
	 * @param params
	 *            创建对象的构造参数
	 * @return 对象代理
	 */
	public static Object create(String loc, Class<?> clazz, Object... params) {
		Object localProxy = null;
		if (loc.equals(Utils.selfIP)) { // 如果创建位置与本地IP一致
			// 本地创建
			localProxy = localCreate(clazz, params);
		} else { // 创建位置与本地IP不一致
			// 远程创建
			localProxy = remoteCreate(loc, clazz, params);
		}

		return localProxy;
	}

	/**
	 * 
	 * @param loc
	 *            创建位置
	 * @param clazz
	 *            创建对象类型
	 * @param params
	 *            创建对象的构造参数
	 * @return 对象代理
	 *
	 * 请求参数有handlerType, clazz, srcIP, (sendParams)
	 * 响应参数有objectID, copy
	 */
	private static Object remoteCreate(String loc, Class<?> clazz, Object[] params) {
		// TODO Auto-generated method stub
		Object localProxy = null; // 这个是创建的结果
		Socket temp = null; // 这个是网络连接
		if ((temp = Utils.getSocket(loc)).isConnected()) { // 如果可以连接上创建节点的IP
			Map remoteCreateMap = new HashMap();
			remoteCreateMap.put("handlerType", "RemoteCreateHandler");// 开启远程创建
			remoteCreateMap.put("clazz", clazz); // 存入创建的类型
			remoteCreateMap.put("srcIP", Utils.selfIP); // 存入发起者IP 即MobileIP
			// 处理创建的构造参数
			int paramLength = params.length;
			if (paramLength == 0) {

			} else {
				Object[] sendParams = new Object[paramLength];
				for (int i = 0; i < paramLength; i++) {
					if (Utils.isProxy(params[i])) {
						sendParams[i] = Utils.proxy2plalecHolder(params[i]);
					} else {
						sendParams[i] = params[i];
					}
				}
				remoteCreateMap.put("sendParams", sendParams); // 存入构造参数

			}

			Request remoteCreate = new Request(temp, remoteCreateMap);// 开启远程创建请求
			FutureTask<Map> futureTask = new FutureTask(remoteCreate);
			Thread rct = new Thread(futureTask);
			rct.start();
//			try {
//				rct.join(2000);
//			} catch (Exception e) {
//				// TODO: handle exception
//				e.printStackTrace();
//				System.out.println("远程创建超时，开始本地创建");
//				localProxy = ObjectFactory.localCreate(clazz, params);
//				return localProxy;
//			}
			Map remoteCreateResult = null;
			try {
				remoteCreateResult = futureTask.get(2, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			} catch (TimeoutException e) {
//				e.printStackTrace();
				System.out.println("远程创建超时");
				localProxy = localCreate(clazz, params);
				return localProxy;
			}
			if(remoteCreateResult != null) {
				String objectID = (String) remoteCreateResult.get("objectID");// 获取远程创建对象的ID
				Object copy = remoteCreateResult.get("copy");// 获取远程创建对象的副本
				if(copy != null) {
					System.out.println("objectID = " + objectID + " copy:" + copy.getClass().toString());
					localProxy = ProxyFactory.getProxy(copy, objectID); // 生成代理对象
					// 存入信息
					ID_OBJ_MAP.put(objectID, copy);
					ID_LOC_MAP.put(objectID, loc);
					ID_PROXY_MAP.put(objectID, localProxy);
					PROXY_ID_MAP.put(localProxy, objectID);
				} else {
					System.out.println("copy is null");
					localProxy = localCreate(clazz, params);
				}
			} else {
				System.out.println("remoteCreateResult is null");
				localProxy = localCreate(clazz, params);
			}
		} else {// 如果不能连接 则进行本地创建
			localProxy = localCreate(clazz, params);
		}
		return localProxy;
	}

	/**
	 * 
	 * @param clazz
	 *            创建对象的类型
	 * @param params
	 *            创建对象的构造参数
	 * @return
	 */
	private static Object localCreate(Class<?> clazz, Object[] params) {
		// TODO Auto-generated method stub
		Object localObject = null; // 创建对象
		Object localProxy = null; // 创建对象的代理
		Constructor<?> cons = null; // 对象的构造器
		int paramLength = params.length; // 构造参数的数量
		// 获取构造器
		if (paramLength == 0) {
			try {
				localObject = clazz.newInstance();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		} else {
			cons = Utils.getSuitableConstructor(clazz, params);
			try {
				localObject = cons.newInstance(params);
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
		}
		// 将创建的对象存入信息
		if (localObject != null) {
			String objectID = Utils.getID(Utils.selfIP, localObject); // 获取对象ID
			localProxy = ProxyFactory.getProxy(localObject, objectID); // 生成对象代理
			ID_LOC_MAP.put(objectID, Utils.selfIP);
			ID_OBJ_MAP.put(objectID, localObject);
			PROXY_ID_MAP.put(localProxy, objectID);
			ID_PROXY_MAP.put(objectID, localProxy);
		}

		return localProxy;

	}

	/**
	 * 
	 * @param dest
	 *            迁移的目标节点
	 * @param proxy
	 *            迁移对象的代理
	 * @return 返回对象代理
	 *
	 * 请求参数有handlerType, objectID, fieldVars, copy
	 */
	public static Object offloadToRemote(String dest, Object proxy) {
		// 说明对象在本地
		Socket temp = null; // 网络连接
		String objectID = PROXY_ID_MAP.get(proxy); // 获取对象的ID
		Object localObject = ID_OBJ_MAP.get(objectID); // 获取本地对象
		if ((temp = Utils.getSocket(dest)).isConnected()) { // 如果连接成功
			// 处理对象的成员属性
			Class clazz = localObject.getClass();
			Field[] fields = clazz.getDeclaredFields();
			int fieldLength = fields.length;
			Object[] fieldVars = new Object[fieldLength];
			for (int i = 0; i < fieldLength; i++) {
				fields[i].setAccessible(true);
				try {
					Object field = fields[i].get(localObject);
					if (Utils.isProxy(field)) {
						fieldVars[i] = Utils.proxy2plalecHolder(field);
					} else {
						fieldVars[i] = field;
					}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
			Map offloadToRemoteMap = new HashMap();
			offloadToRemoteMap.put("handlerType", "OffloadToRemoteHandler");
			offloadToRemoteMap.put("objectID", objectID);// 存入对象ID
			offloadToRemoteMap.put("fieldVars", fieldVars);// 存入处理之后的成员属性
			offloadToRemoteMap.put("copy", localObject);// 存入对象副本
			Request otr = new Request(temp, offloadToRemoteMap); // 开启对象迁移到远程的请求
			FutureTask<Map> futureTask = new FutureTask(otr);
			Thread otrt = new Thread(futureTask);
			otrt.start();
//			try {
//				otrt.join(2000);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				System.out.println("迁移到远程超时");
//				return proxy;
//				//e.printStackTrace();
//			}
			//Map offloadToRemoteResult = otr.getResult(); // 获取对象迁移的结果
			Map offloadToRemoteResult = null;
			try {
				offloadToRemoteResult = futureTask.get(2, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			} catch (TimeoutException e) {
				e.printStackTrace();
				System.out.println("迁移到远程超时");
			}
			if (offloadToRemoteResult.containsKey("offloadState")) {
				ObjectFactory.ID_LOC_MAP.put(objectID, dest);// 迁移成功
			} else {
				System.out.println("offloadToRemote failed");// 迁移失败
			}
		} else {
			System.out.println("can not connect to the IP : (" + dest + ")");// 网络连接不成功 则取消迁移
		}
		return proxy;
	}

	/**
	 * 
	 * @param dest
	 *            转发迁移的位置
	 * @param objectID
	 *            转发迁移的对象ID
	 * @param localObject
	 *            转发迁移的对象副本
	 *
	 * 装发迁移的概念:比如Mobile的对象A在Edge1上，这个时候Mobile与Edge1的连接出现问题，则将Mobile上的信息发
	 * 送给Cloud或者其他Edge，作为跳板将Mobile与Edge1连接起来
	 * 请求参数 handlerType, objectID, fieldVars, copy, Loc
	 */
	public static void transmitToRemote(String dest, String objectID, Object localObject) {
		// 说明对象在本地
		String Loc = ID_LOC_MAP.get(objectID);
		Socket temp = null; // 网络连接
		if ((temp = Utils.getSocket(dest)).isConnected()) {// 如果网络连接成功
			// 处理对象的成员属性
			Class clazz = localObject.getClass();
			Field[] fields = clazz.getDeclaredFields();
			int fieldLength = fields.length;
			Object[] fieldVars = new Object[fieldLength];
			for (int i = 0; i < fieldLength; i++) {
				fields[i].setAccessible(true);
				try {
					Object field = fields[i].get(localObject);
					if (Utils.isProxy(field)) {
						fieldVars[i] = Utils.proxy2plalecHolder(field);
					} else {
						fieldVars[i] = field;
					}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
			Map transmitToRemoteMap = new HashMap();
			transmitToRemoteMap.put("handlerType", "TransmitToRemoteHandler");
			transmitToRemoteMap.put("objectID", objectID);// 存入对象的ID
			transmitToRemoteMap.put("fieldVars", fieldVars);// 存入对象的成员属性
			transmitToRemoteMap.put("copy", localObject);// 存入对象的副本
			transmitToRemoteMap.put("Loc", Loc);
			Request ttr = new Request(temp, transmitToRemoteMap);// 开启转发迁移请求
			FutureTask<Map> futureTask = new FutureTask<Map>(ttr);
			Thread ttrt = new Thread(futureTask);
			ttrt.start();
//			try {
//				ttrt.join(2000);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				//e.printStackTrace();
//				System.out.println("转发迁移超时");
//
//			}
			//Map offloadToRemoteResult = ttr.getResult(); // 获取转发迁移请求的结果

			Map offloadToRemoteResult = null;
			try {
				offloadToRemoteResult = futureTask.get(2, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			} catch (TimeoutException e) {
				e.printStackTrace();
				System.out.println("转发迁移超时");
			}
			if (offloadToRemoteResult.containsKey("transmitState")) {
				ObjectFactory.SUP_ID_LOC_MAP.put(objectID, dest);// 转发迁移成功 则在本地的后备LOC更新信息
			} else {
				System.out.println("TransmitToRemote failed");
			}
		} else {
			System.out.println("can not connect to the IP : (" + dest + ")");
		}
	}

	/**
	 * 
	 * @param proxy
	 *            迁回对象的代理
	 * @return 代理对象
	 * offloadFromRemote即将对象从远程迁回本地
	 * 请求参数handlerType,objectID,srcIP
	 */
	public static Object offloadFromRemote(Object proxy) {
		String objectID = PROXY_ID_MAP.get(proxy); // 迁回对象ID
		String Loc = ID_LOC_MAP.get(objectID); // 迁回对象的位置LOC
		Socket temp = null;// 网络连接
		if ((temp = Utils.getSocket(Loc)).isConnected()) { // 网络连接成功
			Map offloadFromRemote = new HashMap();
			offloadFromRemote.put("handlerType", "OffloadFromRemoteHandler");
			offloadFromRemote.put("objectID", objectID);// 存入对象ID
			offloadFromRemote.put("srcIP", Utils.selfIP);// 存入srcIP
			Request ofr = new Request(temp, offloadFromRemote);// 开启迁回请求
			FutureTask<Map> futureTask = new FutureTask<Map>(ofr);
			Thread ofrt = new Thread(futureTask);
			ofrt.start();
//			try {
//				ofrt.join(2000);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				//e.printStackTrace();
//				System.out.println("迁回本地超时");
//				return proxy;
//			}

			//Map offloadFromRemoteResult = ofr.getResult();// 获取迁回请求的结果
			Map offloadFromRemoteResult = null;
			try {
				offloadFromRemoteResult = futureTask.get(2, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			} catch (TimeoutException e) {
				e.printStackTrace();
				System.out.println("迁回本地超时");
				return proxy;
			}
			if (offloadFromRemoteResult.containsKey("backState")) {
				System.out.println("back success");
			} else {
				System.out.println("back failed");
			}
		} else {
			System.out.println("can not connect to the IP : (" + Loc + ")");
		}
		return proxy;
	}

}
