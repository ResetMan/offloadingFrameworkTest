package basic;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Server每个终端都是一个服务器 大概就是P2P的思想
 * 
 * @author csh
 *
 */
public class Server implements Runnable {

	private ServerSocket serverSocket;

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			serverSocket = new ServerSocket(Utils.selfPort);
			ExecutorService executorService = Executors.newCachedThreadPool();
			while (true) {
				Socket client = serverSocket.accept(); // 接收请求
				RequestProcessor requestProcessor = new RequestProcessor(client);// 开启请求处理进程
				executorService.execute(requestProcessor);// 进程调度
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

}
