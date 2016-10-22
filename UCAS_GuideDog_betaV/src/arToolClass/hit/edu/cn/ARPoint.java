package arToolClass.hit.edu.cn;

import android.location.Location;


public class ARPoint {
	
	private int type;   //类别，1=教学楼，2=生活服务
	private int id;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	private String name;   //地点名称
	private String detailInfo;
	public static final int TYPE_TECHING = 1;  //地点类别常量
	public static final int TYPE_SERVICE = 2;
	public static final int TYPE_OTHERS = 3;
	
	//地点经纬度
	public Location location;
	public float bearingMe; //与手机位置连线，与北方向所成夹角
	public float offsetCenter;   //与手机屏幕中心点
	public float distance;  //存储手机距离此地点的距离
//	private float latitude;
//	private float longitude;
//	private float altitude;
	
	public int leftMargin;   //该点相对于屏幕中心点的左偏移量
	public int topMargin;    //该点相对于屏幕中心点的上偏移量
	public boolean visible;
	
	public ARPoint() {
		super();
		this.type = 0;
		this.name = "建筑物";   //默认名字
		
		this.leftMargin = 0;
		this.topMargin = 0;
		this.location = new Location("defaultLoc");
		this.bearingMe = 0f;
		this.offsetCenter = 0f;
		this.distance = 0f;
		this.visible = false;  //默认不显示
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}

	public String getDetailInfo() {
		return detailInfo;
	}

	public void setDetailInfo(String detailInfo) {
		this.detailInfo = detailInfo;
	}
	
}
