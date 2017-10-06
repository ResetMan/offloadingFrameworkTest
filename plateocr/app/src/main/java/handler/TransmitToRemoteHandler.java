package handler;

import java.lang.reflect.Field;
import java.util.Map;

import basic.ObjectFactory;
import basic.PlaceHolder;
import basic.ProxyFactory;
import basic.Utils;

/**
 * TransmitToRemoteHandler 转发迁移处理器
 * 
 * @author csh
 *
 */
public class TransmitToRemoteHandler extends Handler implements Handleable {

	public TransmitToRemoteHandler(Map requestMap) {
		super(requestMap);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Map handle() {
		// TODO Auto-generated method stub
		String objectID = (String) requestMap.get("objectID"); // 获取转发迁移的对象ID
		Object[] fieldVars = (Object[]) requestMap.get("fieldVars"); // 获取转发迁移的对象的成员属性
		Object copy = requestMap.get("copy"); // 获取转发迁移的对象副本
		String Loc = (String) requestMap.get("Loc"); // 获取转发迁移的对象实际位置
		// 处理成员属性
		Class clazz = copy.getClass();
		Field[] fields = clazz.getDeclaredFields();
		int fieldLength = fields.length;
		for (int i = 0; i < fieldLength; i++) {
			fields[i].setAccessible(true);
			Object field = fieldVars[i];
			if (field.getClass() == PlaceHolder.class) {
				PlaceHolder placeHolder = PlaceHolder.class.cast(field);
				fieldVars[i] = Utils.placeHolder2proxy(placeHolder);
			} else {

			}
			try {
				fields[i].set(copy, fieldVars[i]);
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// 存入信息
		if (!ObjectFactory.ID_LOC_MAP.containsKey(objectID)) {
			Object localProxy = ProxyFactory.getProxy(copy, objectID);
			ObjectFactory.PROXY_ID_MAP.put(localProxy, objectID);
			ObjectFactory.ID_PROXY_MAP.put(objectID, localProxy);
		}
		ObjectFactory.ID_LOC_MAP.put(objectID, Loc);
		ObjectFactory.ID_OBJ_MAP.put(objectID, copy);

		resultMap.put("transmitState", "success");
		return resultMap;
	}

}
