package handler;

import java.util.Map;

/**
 * Handleable接口 所有请求处理类都实现了这个接口
 * 
 * @author csh
 *
 */
public interface Handleable {

	public Map handle();
}
