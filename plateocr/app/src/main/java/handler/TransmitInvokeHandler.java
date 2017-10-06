package handler;

import java.net.Socket;
import java.util.Map;


import basic.ObjectFactory;
import basic.Utils;
import request.Request;

/**
 * TransmitInvokeHandler 转发调用处理器
 * 
 * @author csh
 *
 */
public class TransmitInvokeHandler extends Handler implements Handleable {

	public TransmitInvokeHandler(Map requestMap) {
		super(requestMap);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Map handle() {
		// TODO Auto-generated method stub
		// 转发调用是建立在转发迁移之上 所以当转发调用到这个节点，说明节点已经被转发迁移过了
		String objectID = (String) requestMap.get("objectID"); // 获取要调用的对象ID
		String Loc = (String) requestMap.get("Loc"); // 获取对象应该在的Loc
		Socket temp = null;
		if ((temp = Utils.getSocket(Loc)).isConnected()) { // 如果当前节点能够连接上Loc 如Cloud
			requestMap.put("handlerType", "RemoteInvokeHandler"); // 开始远程调用请求
			Request remoteInvoke = new Request(temp, requestMap);
			Thread rit = new Thread(remoteInvoke);
			rit.start();
			try {
				rit.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			resultMap = remoteInvoke.getResult();
		} else { // Edge
			if (ObjectFactory.SUP_ID_LOC_MAP.containsKey(objectID)) {
				String SUPLoc = ObjectFactory.SUP_ID_LOC_MAP.get(objectID);
				temp = Utils.getSocket(SUPLoc);
				requestMap.put("handlerType", "RemoteInvokeHandler"); // 开始远程调用请求
				Request remoteInvoke = new Request(temp, requestMap);
				Thread rit = new Thread(remoteInvoke);
				rit.start();
				try {
					rit.join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				resultMap = remoteInvoke.getResult();
			} else {
				temp = Utils.getSocket(Utils.cloudIP);
				Object localObject = ObjectFactory.ID_OBJ_MAP.get(objectID);
				ObjectFactory.transmitToRemote(Utils.cloudIP, objectID, localObject);// 转发迁移到Cloud
				Request transmitInvoke = new Request(temp, requestMap); // 开始转发调用
				Thread tit = new Thread(transmitInvoke);
				tit.start();
				try {
					tit.join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				resultMap = transmitInvoke.getResult();
			}
		}

		return resultMap;
	}

}
