package com.mmd.models;

import java.util.Arrays;
import java.util.HashSet;

public class Photo {
	private boolean isHorizontal;
	private String tags[];
	private int id;

	public Photo(int id, boolean isHorizontal, HashSet<String> tags) {
		this.id = id;
		this.isHorizontal = isHorizontal;
		this.tags = tags.toArray(new String[0]);
		Arrays.sort(this.tags);
	}

	public int calculateInterest(Photo other) {
		if (other == null)
			return 0;
		String[] otherTags = other.getTags();
		//calculate common and differences
		int commonNumber = 0;
		int leftIndex = 0, rightIndex = 0;
		while(leftIndex < this.tags.length && rightIndex < otherTags.length){
			String left = this.tags[leftIndex];
			String right = otherTags[rightIndex];
			int result = left.compareTo(right);
			if(result == 0){
				commonNumber++;
				leftIndex++;
				rightIndex++;
			}else if(result > 0){
				rightIndex++;
			}else{
				leftIndex++;
			}

		}
		int dif1 = this.tags.length - commonNumber;
		int dif2 = otherTags.length - commonNumber;
		return Math.min(commonNumber, Math.min(dif1, dif2));
	}


	public boolean isHorizontal() {
		return isHorizontal;
	}

	public void setHorizontal(boolean horizontal) {
		isHorizontal = horizontal;
	}

	public String[] getTags() {
		return tags;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}


	public String getOutputFormat() {
		return this.getId() + "\n";
	}

	@Override
	public String toString() {
		return "Photo{" +
				"isHorizontal=" + isHorizontal +
				", id=" + id +
				'}';
	}
}
