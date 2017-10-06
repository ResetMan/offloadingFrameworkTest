package request;

import java.util.Map;

/**
 * Requestable 请求类实现了这个接口
 * 
 * @author csh
 *
 */
public interface Requestable {

	public void doRequest();

	public Map getResult();
}
