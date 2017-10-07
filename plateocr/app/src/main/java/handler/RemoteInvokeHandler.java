package handler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.Map;

import basic.ObjectFactory;
import basic.PlaceHolder;
import basic.Utils;
import request.Request;

/**
 * RemoteInvokeHandler 远程调用处理器
 * 
 * @author csh
 *
 */
public class RemoteInvokeHandler extends Handler implements Handleable {

	public RemoteInvokeHandler(Map requestMap) {
		super(requestMap);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Map handle() {
		// TODO Auto-generated method stub
		String objectID = (String) requestMap.get("objectID"); // 远程调用的对象ID
		String Loc = ObjectFactory.ID_LOC_MAP.get(objectID);
		String methodName = (String) requestMap.get("methodName"); // 调用的方法名
		Object result = null; // 调用的结果
		if (Loc.equals(Utils.selfIP)) { // 如果对象就在当前位置
			Object localObject = ObjectFactory.ID_OBJ_MAP.get(objectID); // 获取对象
			// 获取对应的方法
			Class clazz = localObject.getClass();
			if (requestMap.containsKey("params")) {
				Object[] params = (Object[]) requestMap.get("params");
				int length = params.length;
				Class[] paramTypes = new Class[length];
				Object[] paramVars = new Object[length];
				for (int i = 0; i < length; i++) {
					if (params[i].getClass() == PlaceHolder.class) {
						PlaceHolder placeHolder = PlaceHolder.class.cast(params[i]);
						paramTypes[i] = placeHolder.getCopy().getClass();
						paramVars[i] = Utils.placeHolder2proxy(placeHolder);
					} else {
						paramTypes[i] = params[i].getClass();
						paramVars[i] = params[i];
					}
				}
				Method method = Utils.getSuitableMethod(clazz, methodName, paramTypes);
				try {
					result = method.invoke(localObject, paramVars);// 方法调用
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
					Method method = clazz.getDeclaredMethod(methodName); // 获取无参方法
					result = method.invoke(localObject);// 方法调用
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SecurityException e) {
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
			if (ObjectFactory.PROXY_ID_MAP.containsKey(result)) { // 判断返回结果是否是一个Proxy对象
				String resultID = ObjectFactory.PROXY_ID_MAP.get(result);
				String resultLoc = ObjectFactory.ID_LOC_MAP.get(objectID);
				Object resultCopy = ObjectFactory.ID_OBJ_MAP.get(resultID);
				PlaceHolder placeHolder = new PlaceHolder(resultCopy, resultID, resultLoc);
				result = placeHolder; // 将Proxy对象包装成PlaceHolder
			}
			resultMap.put("result", result);
		} else { // 如果当前节点是个中间转发的
			Socket temp = null;
			if ((temp = Utils.getSocket(Loc)).isConnected()) { // 获取自身节点存储的对象位置Loc
				Request ri = new Request(temp, requestMap); // 开启远程调用
				Thread rit = new Thread(ri);
				rit.start();
				try {
					rit.join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				resultMap = ri.getResult();
				result = resultMap.get("result");
				if (result == null) {

				} else {
					if (result.getClass() == PlaceHolder.class) {
						PlaceHolder placeHolder = PlaceHolder.class.cast(result);
						Utils.placeHolder2proxy(placeHolder);
					}
				}
			}
		}
		return resultMap;
	}

}
