package basic;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * GetSocket 获取网络连接
 * 
 * @author csh
 *
 */
public class GetSocket implements Runnable {

	private String IP;
	private int port;
	private Socket socket;

	/**
	 * 
	 * @param IP
	 *            连接的IP
	 * @param port
	 *            连接的port
	 */
	public GetSocket(String IP, int port) {
		// TODO Auto-generated constructor stub
		this.IP = IP;
		this.port = port;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		socket = new Socket();
		SocketAddress socketAddress = new InetSocketAddress(IP, port);
		try {
			socket.connect(socketAddress, 2000);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public Socket getSocket() {
		return socket;
	}

}
