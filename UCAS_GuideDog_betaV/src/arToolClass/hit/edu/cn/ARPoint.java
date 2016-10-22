package arToolClass.hit.edu.cn;

import android.location.Location;


public class ARPoint {
	
	private int type;   //���1=��ѧ¥��2=�������
	private int id;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	private String name;   //�ص�����
	private String detailInfo;
	public static final int TYPE_TECHING = 1;  //�ص������
	public static final int TYPE_SERVICE = 2;
	public static final int TYPE_OTHERS = 3;
	
	//�ص㾭γ��
	public Location location;
	public float bearingMe; //���ֻ�λ�����ߣ��뱱�������ɼн�
	public float offsetCenter;   //���ֻ���Ļ���ĵ�
	public float distance;  //�洢�ֻ�����˵ص�ľ���
//	private float latitude;
//	private float longitude;
//	private float altitude;
	
	public int leftMargin;   //�õ��������Ļ���ĵ����ƫ����
	public int topMargin;    //�õ��������Ļ���ĵ����ƫ����
	public boolean visible;
	
	public ARPoint() {
		super();
		this.type = 0;
		this.name = "������";   //Ĭ������
		
		this.leftMargin = 0;
		this.topMargin = 0;
		this.location = new Location("defaultLoc");
		this.bearingMe = 0f;
		this.offsetCenter = 0f;
		this.distance = 0f;
		this.visible = false;  //Ĭ�ϲ���ʾ
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
