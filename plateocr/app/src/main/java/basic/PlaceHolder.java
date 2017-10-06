package basic;

/**
 * PlaceHolder 代理对象传输的一个中间产物
 * 
 * @author csh
 *
 */
public class PlaceHolder {

	private Object copy; // 对象副本
	private String ID; // 对象ID
	private String Loc; // 对象位置LOC

	public PlaceHolder(Object proxyCopy, String proxyID, String proxyLoc) {
		// TODO Auto-generated constructor stub
		this.copy = proxyCopy;
		this.ID = proxyID;
		this.Loc = proxyLoc;
	}

	public Object getCopy() {
		return copy;
	}

	public void setCopy(Object copy) {
		this.copy = copy;
	}

	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
	}

	public String getLoc() {
		return Loc;
	}

	public void setLoc(String loc) {
		Loc = loc;
	}

}
