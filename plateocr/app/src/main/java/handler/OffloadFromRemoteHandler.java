package handler;

import java.util.Map;

import basic.ObjectFactory;
import basic.Utils;

/**
 * OffloadFromRemoteHandler 迁回请求处理器
 * 
 * @author csh
 *
 */
public class OffloadFromRemoteHandler extends Handler implements Handleable {

	public OffloadFromRemoteHandler(Map requestMap) {
		super(requestMap);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Map handle() {
		// TODO Auto-generated method stub
		String objectID = (String) requestMap.get("objectID"); // 迁回对象的ID
		String srcIP = (String) requestMap.get("srcIP"); // 迁回对象的发起者
		Object localProxy = ObjectFactory.ID_PROXY_MAP.get(objectID); // 获取迁回对象的代理
		String Loc = ObjectFactory.ID_LOC_MAP.get(objectID); // 获取迁回对象在当前节点所存的位置Loc
		if (Loc.equals(Utils.selfIP)) {// 如果Loc与当前节点IP一致
			ObjectFactory.offloadToRemote(srcIP, localProxy);// 将当前节点的对象迁移到srcIP上
		} else {
			ObjectFactory.offloadFromRemote(localProxy);// 如果节点IP与Loc不一致 继续发出迁回请求
			ObjectFactory.offloadToRemote(srcIP, localProxy); // 然后将迁回的对象 迁移到srcIP上
		}
		resultMap.put("backState", "success");
		return resultMap;
	}

}
