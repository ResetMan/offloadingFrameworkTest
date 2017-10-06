package basic;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Map;

import handler.Handleable;
import handler.HandlerGenerator;

/**
 * RequestProcessor 处理请求
 * 
 * @author csh
 *
 */
public class RequestProcessor implements Runnable {

	private Socket socket;

	public RequestProcessor(Socket socket) {
		// TODO Auto-generated constructor stub
		this.socket = socket;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
			Map requestMap = (Map) objectInputStream.readObject(); // 获取请求
			String handlerType = (String) requestMap.get("handlerType"); // 判断请求类型
			Handleable handler = HandlerGenerator.getHandler(handlerType, requestMap); // 请求处理生成器生成对应的请求处理对象
			Map resultMap = handler.handle(); // 请求处理对象处理请求
			objectOutputStream.writeObject(resultMap); // 返回请求处理的结果
			objectOutputStream.flush();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

}
