package basic;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import request.Request;

public class EndPoint implements InvocationHandler {

	private String objectID; // 所代理的对象的ID

	public EndPoint(String objectID) {
		// TODO Auto-generated constructor stub
		this.objectID = objectID;
	}

	/**
	 *
	 * @param proxy
	 * @param method
	 * @param args
	 * @return
     * @throws Throwable
	 * 远程调用情况分为以下三种：
	 * 1.能够直接连上Loc 开始remoteInvoke {handlerType, methodName, params}
	 * 2.不能够直接连上Loc,但是有SupLoc（后备跳板） 开始remoteInvoke {handlerType, methodName, params}
	 * 3.后备跳板都没后，开始transmitInvoke{handlerType, objectID, Loc, methodName, params}
     */
	@Override
	public Object invoke(Object proxy, Method method, Object[] args){
		// TODO Auto-generated method stub
		if (method.getName().equals("hashCode")) { // 拦截hashCode方法
			return Utils.getHashCode(objectID);
		}
		Object result = null;
		String Loc = ObjectFactory.ID_LOC_MAP.get(objectID); // 获取对象所处的位置
		if (Loc.equals(Utils.selfIP)) { // 如果是所处位置是本地
			Object localObject = ObjectFactory.ID_OBJ_MAP.get(objectID); // 获取到本地的对象
			try {
				result = method.invoke(localObject, args); // 完成对象调用
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		} else {
			Socket temp = null;
			if ((temp = Utils.getSocket(Loc)).isConnected()) { // 如果可以连接到Loc
				Map remoteInvokeMap = new HashMap();
				remoteInvokeMap.put("handlerType", "RemoteInvokeHandler"); // 开启远程调用请求
				remoteInvokeMap.put("objectID", objectID);//存入objectID
				remoteInvokeMap.put("methodName", method.getName()); // 存入方法名
				if (args != null) { // 如果方法调用参数非空
					int length = args.length;
					Object[] params = new Object[length];
					for (int i = 0; i < length; i++) {
						if (Utils.isProxy(args[i])) {// 如果是Proxy对象 就转成PlaceHolder对象
							params[i] = Utils.proxy2plalecHolder(args[i]);
						} else {// 否则就不处理
							params[i] = args[i];
						}
					}
					remoteInvokeMap.put("params", params); // 存入处理好的params
				}
				Request ri = new Request(temp, remoteInvokeMap);// 远程调用请求开始运行
				Thread rit = new Thread(ri);
				rit.start();
				try {
					rit.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Map resultMap = ri.getResult();
				result = resultMap.get("result");// 获取远程调用的结果
				if (result == null) {

				} else {
					if (result.getClass() == PlaceHolder.class) {// 如果远程调用结果是PlaceHolder，则其真实对象应该是Proxy对象
						PlaceHolder placeHolder = PlaceHolder.class.cast(result);
						result = Utils.placeHolder2proxy(placeHolder);// 将PlaceHolder转成Proxy
					}
				}
			} else {
				// 如果不能连接到Loc，判断有没有后备节点进行连接
				if (ObjectFactory.SUP_ID_LOC_MAP.containsKey(objectID)) {// 如果有后备节点
					String supLoc = ObjectFactory.SUP_ID_LOC_MAP.get(objectID);// 获取后备节点的IP
					temp = Utils.getSocket(supLoc);// 连接到后备节点
					Map remoteInvokeMap = new HashMap();
					remoteInvokeMap.put("handlerType", "RemoteInvokeHandler");// 开启远程调用请求
					remoteInvokeMap.put("objectID", objectID);//存入objectID
					remoteInvokeMap.put("methodName", method.getName());// 存入方法名
					if (args != null) {
						int length = args.length;
						Object[] params = new Object[length];
						for (int i = 0; i < length; i++) {
							if (Utils.isProxy(args[i])) {
								params[i] = Utils.proxy2plalecHolder(args[i]);
							} else {
								params[i] = args[i];
							}
						}
						remoteInvokeMap.put("params", params);// 存入调用参数
					}
					Request ri = new Request(temp, remoteInvokeMap);// 远程调用请求开始
					Thread rit = new Thread(ri);
					rit.start();
					try {
						rit.join();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					Map resultMap = ri.getResult();
					result = resultMap.get("result");// 获取远程调用的结果
					// 结果的处理 同上
					if (result == null) {

					} else {
						if (result.getClass() == PlaceHolder.class) {
							PlaceHolder placeHolder = PlaceHolder.class.cast(result);
							result = Utils.placeHolder2proxy(placeHolder);
						}
					}
				} else {
					// 如果没有后备节点
					if ((temp = Utils.getSocket(Utils.cloudIP)).isConnected()) { // 首先判断Cloud能不能连接 如果能
						Object localObject = ObjectFactory.ID_OBJ_MAP.get(objectID);// 获取本地的copy对象
						ObjectFactory.transmitToRemote(Utils.cloudIP, objectID, localObject);// 发送本地的copy对象到Cloud
						Map transmitInvokeMap = new HashMap();
						transmitInvokeMap.put("handlerType", "TransmitInvokeHandler");// 开启转发调用请求
						transmitInvokeMap.put("objectID", objectID);// 存入对象ID
						transmitInvokeMap.put("Loc", Loc);// 存入Loc
						transmitInvokeMap.put("methodName", method.getName());// 存入方法名
						if (args != null) {
							int length = args.length;
							Object[] params = new Object[length];
							for (int i = 0; i < length; i++) {
								if (Utils.isProxy(args[i])) {
									params[i] = Utils.proxy2plalecHolder(args[i]);
								} else {
									params[i] = args[i];
								}
							}
							transmitInvokeMap.put("params", params);// 存入方法调用参数
						}
						Request ti = new Request(temp, transmitInvokeMap);// 转发调用请求开始
						Thread tit = new Thread(ti);
						tit.start();
						try {
							tit.join();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						Map resultMap = ti.getResult();// 获取转发调用请求
						result = resultMap.get("result");
						// 结果的处理
						if (result == null) {

						} else {
							if (result.getClass() == PlaceHolder.class) {
								PlaceHolder placeHolder = PlaceHolder.class.cast(result);
								result = Utils.placeHolder2proxy(placeHolder);
							}
						}
					} else {
						// 如果不能连接上Cloud 则通过随机选择Edge
						int nodeLength = Utils.Nodes.size();
						Random random = new Random();
						int randNode = random.nextInt(nodeLength - 1) + 1;
						int j = 0;
						String randIP = null;
						for (String ip : Utils.Nodes.keySet()) {
							if (j == randNode) {
								randIP = ip;
								break;
							} else {
								j++;
								continue;
							}
						}
						temp = Utils.getSocket(randIP);
						// 同连接Cloud动作一样
						Object localObject = ObjectFactory.ID_OBJ_MAP.get(objectID);
						ObjectFactory.transmitToRemote(randIP, objectID, localObject);
						Map transmitInvokeMap = new HashMap();
						transmitInvokeMap.put("handlerType", "TransmitInvokeHandler");
						transmitInvokeMap.put("objectID", objectID);
						transmitInvokeMap.put("Loc", Loc);
						transmitInvokeMap.put("methodName", method.getName());
						if (args != null) {
							int length = args.length;
							Object[] params = new Object[length];
							for (int i = 0; i < length; i++) {
								if (Utils.isProxy(args[i])) {
									params[i] = Utils.proxy2plalecHolder(args[i]);
								} else {
									params[i] = args[i];
								}
							}
							transmitInvokeMap.put("params", params);
						}
						Request ti = new Request(temp, transmitInvokeMap);
						Thread tit = new Thread(ti);
						tit.start();
						try {
							tit.join();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						Map resultMap = ti.getResult();
						result = resultMap.get("result");
						if (result == null) {

						} else {
							if (result.getClass() == PlaceHolder.class) {
								PlaceHolder placeHolder = PlaceHolder.class.cast(result);
								result = Utils.placeHolder2proxy(placeHolder);
							}
						}

					}
				}
			}
		}

		return result;
	}

}
