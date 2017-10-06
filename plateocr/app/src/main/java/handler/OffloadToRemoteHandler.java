package handler;

import java.lang.reflect.Field;
import java.util.Map;

import basic.ObjectFactory;
import basic.PlaceHolder;
import basic.ProxyFactory;
import basic.Utils;

/**
 * OffloadToRemoteHandler 迁移请求处理器
 * 
 * @author csh
 *
 */
public class OffloadToRemoteHandler extends Handler implements Handleable {

	public OffloadToRemoteHandler(Map requestMap) {
		super(requestMap);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Map handle() {
		// TODO Auto-generated method stub
		String objectID = (String) requestMap.get("objectID"); // 获取迁移过来的对象ID
		Object[] fieldVars = (Object[]) requestMap.get("fieldVars"); // 获取迁移过来的对象成员属性
		Object copy = requestMap.get("copy"); // 获取迁移过来的对象副本
		Class clazz = copy.getClass(); // 获取迁移过来的对象类类型
		Field[] fields = clazz.getDeclaredFields(); // 获取声明的成员
		int fieldLength = fields.length;
		for (int i = 0; i < fieldLength; i++) {
			fields[i].setAccessible(true); // 使得成员可以被访问
			Object field = fieldVars[i];
			if (field.getClass() == PlaceHolder.class) { // 如果成员属性是PlaceHolder 则转换成Proxy
				PlaceHolder placeHolder = PlaceHolder.class.cast(field);
				fieldVars[i] = Utils.placeHolder2proxy(placeHolder);
			} else {

			}
			try {
				fields[i].set(copy, fieldVars[i]); // 对副本进行重设置成员属性
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (!ObjectFactory.ID_LOC_MAP.containsKey(objectID)) { // 如果对象是第一次迁移过来的 要设置Proxy_ID 否则就不需要
			Object localProxy = ProxyFactory.getProxy(copy, objectID);
			ObjectFactory.PROXY_ID_MAP.put(localProxy, objectID);
			ObjectFactory.ID_PROXY_MAP.put(objectID, localProxy);
		}
		// 存入对象信息
		ObjectFactory.ID_LOC_MAP.put(objectID, Utils.selfIP);
		ObjectFactory.ID_OBJ_MAP.put(objectID, copy);

		resultMap.put("offloadState", "success");
		return resultMap;
	}

}
