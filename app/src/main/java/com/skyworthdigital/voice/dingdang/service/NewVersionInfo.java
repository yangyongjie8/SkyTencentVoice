package com.skyworthdigital.voice.dingdang.service;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.gson.annotations.SerializedName;

/**
 * @author hxy 自升级的版本信息
 *
 */
public class NewVersionInfo implements Parcelable {
	private static final int ENFORCE_UPGRADE = 0;

	@SerializedName("status")
	private int status;
	
	@SerializedName("version")
	private int newVersion;
	
	@SerializedName("versionName")
	private String newVersionName;
	
	@SerializedName("level")
	private int level;
	
	@SerializedName("time")
	private String time;

	@SerializedName("size")
	private long size;
	
	@SerializedName("url")
	private String url;
	
	@SerializedName("desc")
	private String desc;

	@SerializedName("md5")
	private String md5;
	
	@SerializedName("thirdPartyUpgrade")
	private int thirdPartyUpgrade;

	private int currVersion;
	private String currVersionName;
	private String cachePath;

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getCurrVersion() {
		return currVersion;
	}

	public void setCurrVersion(int currVersion) {
		this.currVersion = currVersion;
	}

	public String getCurrVersionName() {
		return currVersionName;
	}

	public void setCurrVersionName(String currVersionName) {
		this.currVersionName = currVersionName;
	}

	public int getNewVersion() {
		return newVersion;
	}

	public void setNewVersion(int newVersion) {
		this.newVersion = newVersion;
	}

	public String getNewVersionName() {
		return newVersionName;
	}

	public void setNewVersionName(String newVersionName) {
		this.newVersionName = newVersionName;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

	public String getCachePath() {
		return cachePath;
	}

	public void setCachePath(String cachePath) {
		this.cachePath = cachePath;
	}

	public boolean hasNewVersion() {
		Log.i("NewVersionInfo", "=====hasNewVersion:" + newVersion + " : " + currVersion);
		if (status == 0 && newVersion > currVersion) {
			return true;
		}
		return false;
	}

	public boolean isEnforce() {
		return level == ENFORCE_UPGRADE;
	}

	public boolean isThirdPartyUpgrade() {
		return thirdPartyUpgrade == 1;
	}

	public int getThirdPartyUpgrade() {
		return thirdPartyUpgrade;
	}

	public void setThirdPartyUpgrade(int thirdPartyUpgrade) {
		this.thirdPartyUpgrade = thirdPartyUpgrade;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(status);
		dest.writeInt(newVersion);
		dest.writeString(newVersionName);
		dest.writeInt(level);
		dest.writeString(time);
		dest.writeLong(size);
		dest.writeString(url);
		dest.writeString(desc);
		dest.writeString(md5);
		dest.writeInt(thirdPartyUpgrade);
	}

	public static final Creator<NewVersionInfo> CREATOR = new Creator<NewVersionInfo>() {
		public NewVersionInfo createFromParcel(Parcel source) {
			NewVersionInfo ret = new NewVersionInfo();
			ret.setStatus(source.readInt());
			ret.setNewVersion(source.readInt());
			ret.setNewVersionName(source.readString());
			ret.setLevel(source.readInt());
			ret.setTime(source.readString());
			ret.setSize(source.readLong());
			ret.setUrl(source.readString());
			ret.setDesc(source.readString());
			ret.setMd5(source.readString());
			ret.setThirdPartyUpgrade(source.readInt());
			return ret;
		}

		public NewVersionInfo[] newArray(int size) {
			return new NewVersionInfo[size];
		}
	};
}