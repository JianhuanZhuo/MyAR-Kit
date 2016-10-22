package arLocationTool.hit.edu.cn;

import android.location.Location;

public class LocCalculateTool {

	public static float calculate(Location fromLoc, Location toLoc){
		//此函数返回值范围是：-180到180，以调用者为起点，参数为终点指向，有180这个值
		float bearingFT = fromLoc.bearingTo(toLoc);
//		float bearingTF = toLoc.bearingTo(fromLoc);   
		
		//转换角度的范围，变为0至360，这样便于计算与屏幕朝向的夹角
		if(bearingFT<0){
			bearingFT += 360;
		}
		return bearingFT;
	}
}
