package handler;

import java.util.HashMap;
import java.util.Map;

/**
 * Handler 每个请求处理类都是继承这个类
 * 
 * @author csh
 *
 */
public class Handler {

	protected Map requestMap; // 请求信息
	protected Map resultMap; // 响应信息

	public Handler(Map requestMap) {
		// TODO Auto-generated constructor stub
		this.requestMap = requestMap;
		this.resultMap = new HashMap();
	}

}
