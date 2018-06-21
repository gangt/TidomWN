package com.suneee.mis.bean;

public class Image {
	public String path;
	public String name;
	public long time;
	public long length;

	public Image(String path, String name, long time, long length) {
		this.path = path;
		this.name = name;
		this.time = time;
		this.length = length;
	}

	@Override
	public boolean equals(Object o) {
		try {
			Image other = (Image) o;
			return this.path.equalsIgnoreCase(other.path);
		} catch (ClassCastException e) {
			e.printStackTrace();
		}
		return super.equals(o);
	}
}
