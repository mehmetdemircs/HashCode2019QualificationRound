package com.mmd.models;

import com.mmd.Main;

import java.util.BitSet;
import java.util.HashSet;
import java.util.Objects;

public class Photo {
	private boolean isHorizontal;
	private int id;
	private long[] longArray;
	private int totalSize;

	public Photo(int id, boolean isHorizontal, HashSet<String> tags) {
		this.id = id;
		this.isHorizontal = isHorizontal;
		this.totalSize = tags.size();
		BitSet bitSet = new BitSet();
		bitSet.set(Main.tagMap.size());
		tags.forEach(x -> bitSet.set(Main.tagMap.get(x)));
		this.longArray = bitSet.toLongArray();
	}

	public boolean isHorizontal() {
		return isHorizontal;
	}

	public void setHorizontal(boolean horizontal) {
		isHorizontal = horizontal;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "Photo{" +
				"isHorizontal=" + isHorizontal +
				", id=" + id +
				'}';
	}

	public int getTotalSize() {
		return totalSize;
	}

	public void setTotalSize(int totalSize) {
		this.totalSize = totalSize;
	}

	public long[] getLongArray() {
		return longArray;
	}

	public void setLongArray(long[] longArray) {
		this.longArray = longArray;
	}

	public int calculateInterest(Photo other) {
		if (other == null)
			return 0;
		//calculate common and differences
		int commonNumber = -1;
		for (int i = 0; i < this.longArray.length; i++) {
			commonNumber += Long.bitCount(this.longArray[i] & other.longArray[i]);
		}
		int dif1 = this.totalSize - commonNumber;
		int dif2 = other.totalSize - commonNumber;
		return Math.min(commonNumber, Math.min(dif1, dif2));
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Photo)) return false;
		Photo photo = (Photo) o;
		return id == photo.id;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
