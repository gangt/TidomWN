package com.xiangpu.bean;
import android.os.Parcel;
import android.os.Parcelable;

import android.text.TextUtils;
import android.widget.LinearLayout;
import android.graphics.Bitmap;
import com.xiangpu.utils.*;
/**
 * 指挥中心视频信息实体类
 * @author huangda
 *
 */
public class VideoInfo implements Parcelable {
		
		public String id;
		public boolean select;
		public String name;
		public String equipmentId;
		public String position;
		public String imageUrl;
		public String domain;
		public String ip;
		public String port;

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public String getEquipmentPort() {
		return equipmentPort;
	}

	public void setEquipmentPort(String equipmentPort) {
		this.equipmentPort = equipmentPort;
	}

	public String userName;
		public String pwd;
		public String equipmentPort;

		public String getEquipmentId() {
			return equipmentId;
		}
		public void setEquipmentId(String equipmentId) {
			this.equipmentId = equipmentId;
		}
		public String getPosition() {
			return position;
		}
		public void setPosition(String position) {
			this.position = position;
		}
		public String getDomain() {
			return domain;
		}
		public void setDomain(String domain) {
			this.domain = domain;
		}
		public String getIp() {
			return ip;
		}
		public void setIp(String ip) {
			this.ip = ip;
		}
		public String startTime;
		public String endTime;
		
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		
		public boolean isSelect() {
			return select;
		}
		public void setSelect(boolean select) {
			this.select = select;
		}
		
		public String getStartTime() {
			return startTime;
		}
		public void setStartTime(String startTime) {
			this.startTime = startTime;
		}
		public String getEndTime() {
			return endTime;
		}
		public void setEndTime(String endTime) {
			this.endTime = endTime;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getImageUrl() {
			return imageUrl;
		}
		public void setImageUrl(String imageUrl) {
			this.imageUrl = imageUrl;
		}

		public LinearLayout headerView;
		public Bitmap headerBmp = null;

		public Bitmap getBitHeader(){

			if(!TextUtils.isEmpty(imageUrl)&&imageUrl.startsWith("http")){

				if(headerBmp==null){
					//if(bmp == null)
					{
						headerBmp = Utils.getUrlToImage(imageUrl);

					}
				}
			}
		return headerBmp;
	}

		 @Override
		public String toString() {
			return "VideoInfo [id=" + id + ", select=" + select + ", name=" + name + ", equipmentId=" + equipmentId
					+ ", position=" + position + ", imageUrl=" + imageUrl + ", domain=" + domain + ", ip=" + ip
					+ ", startTime=" + startTime + ", endTime=" + endTime + "]";
		}
		public static final Parcelable.Creator<VideoInfo> CREATOR = new Creator<VideoInfo>() {  
		        public VideoInfo createFromParcel(Parcel source) {  
		        	VideoInfo videoInfo = new VideoInfo();  
		        	videoInfo.id = source.readString();  
		        	videoInfo.equipmentId = source.readString();  
		        	videoInfo.name = source.readString();  
		        	videoInfo.imageUrl = source.readString();  
		        	videoInfo.position = source.readString();  
		        	videoInfo.domain = source.readString();  
		        	videoInfo.ip = source.readString();  
		            return videoInfo;
		        }  
		        public VideoInfo[] newArray(int size) {  
		            return new VideoInfo[size];  
		        }  
		    };  
		@Override
		public int describeContents() {
			return 0;
		}
		@Override
		public void writeToParcel(Parcel parcel, int flags) {
			parcel.writeString(id);
			parcel.writeString(equipmentId);
			parcel.writeString(name);
			parcel.writeString(imageUrl);
			parcel.writeString(position);
			parcel.writeString(domain);
			parcel.writeString(ip);
			
		}
		
		
		
}
