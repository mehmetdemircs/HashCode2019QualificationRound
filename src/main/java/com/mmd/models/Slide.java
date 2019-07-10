package com.mmd.models;

import java.util.Objects;

public class Slide {
	private Photo photo1, photo2;
	private boolean isHorizontal;
	private int totalSize;
	private long[] longArray;
	public Slide previous, next;

	public Slide(Photo photo1) {
		this.photo1 = photo1;
		this.photo2 = null;
		this.isHorizontal = photo1.isHorizontal();
		this.totalSize = photo1.getTotalSize();
		this.longArray = photo1.getLongArray().clone();
	}

	public Slide(Photo photo1, Photo photo2) {
		this.photo1 = photo1;
		this.photo2 = photo2;
		this.isHorizontal = false;

		this.longArray = photo1.getLongArray().clone();
		this.totalSize = -1;
		for (int i = 0; i < this.longArray.length; i++) {
			this.longArray[i] |= photo2.getLongArray()[i];
			this.totalSize += Long.bitCount(this.longArray[i]);
		}
	}

	public int calculateInterest(Slide other) {
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

	public Photo getPhoto1() {
		return photo1;
	}

	public void setPhoto1(Photo photo1) {
		this.photo1 = photo1;
	}

	public Photo getPhoto2() {
		return photo2;
	}

	public void setPhoto2(Photo photo2) {
		this.photo2 = photo2;
	}

	public boolean isHorizontal() {
		return isHorizontal;
	}

	public void setHorizontal(boolean horizontal) {
		isHorizontal = horizontal;
	}


	public String getOutputFormat() {
		if (this.isHorizontal) {
			return this.photo1.getId() + "\n";
		}
		return this.photo1.getId() + " " + this.photo2.getId() + "\n";
	}

	@Override
	public String toString() {
		return "Slide{" +
				"photo1=" + photo1 +
				", photo2=" + photo2 +
				", isHorizontal=" + isHorizontal +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Slide)) return false;
		Slide slide = (Slide) o;
		return Objects.equals(photo1, slide.photo1) &&
				Objects.equals(photo2, slide.photo2);
	}

	@Override
	public int hashCode() {
		return Objects.hash(photo1, photo2);
	}

	public int getTotalSize() {
		return totalSize;
	}

	public void setTotalSize(int totalSize) {
		this.totalSize = totalSize;
	}
}
