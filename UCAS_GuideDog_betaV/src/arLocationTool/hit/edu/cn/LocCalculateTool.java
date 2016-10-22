package arLocationTool.hit.edu.cn;

import android.location.Location;

public class LocCalculateTool {

	public static float calculate(Location fromLoc, Location toLoc){
		//�˺�������ֵ��Χ�ǣ�-180��180���Ե�����Ϊ��㣬����Ϊ�յ�ָ����180���ֵ
		float bearingFT = fromLoc.bearingTo(toLoc);
//		float bearingTF = toLoc.bearingTo(fromLoc);   
		
		//ת���Ƕȵķ�Χ����Ϊ0��360���������ڼ�������Ļ����ļн�
		if(bearingFT<0){
			bearingFT += 360;
		}
		return bearingFT;
	}
}
