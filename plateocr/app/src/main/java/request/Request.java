package request;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * Request 请求类
 * 
 * @author csh
 *
 */
public class Request implements Runnable, Requestable {

	protected Socket socket;
	protected Map requestMap;
	protected Map resultMap;

	public Request(Socket socket, Map requestMap) {
		// TODO Auto-generated constructor stub
		this.socket = socket;
		this.requestMap = requestMap;
		this.resultMap = new HashMap();
	}

	@Override
	public void doRequest() {
		// TODO Auto-generated method stub
		try {
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
			objectOutputStream.writeObject(requestMap);
			objectOutputStream.flush(); // 发送请求

			ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
			resultMap = (Map) objectInputStream.readObject();// 得到请求的结果
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public Map getResult() {
		// TODO Auto-generated method stub
		return resultMap;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		doRequest();
	}

}
