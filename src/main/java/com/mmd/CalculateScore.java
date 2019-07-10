package com.mmd;

import com.mmd.models.Photo;

import java.util.ArrayList;

public class CalculateScore {
	public static int calculateScore(ArrayList<Photo> resultList) {
		int totalInterest = 0;
		for (int i = 1; i < resultList.size(); i++) {
			Photo slide1 = resultList.get(i - 1);
			Photo slide2 = resultList.get(i);
			totalInterest += slide1.calculateInterest(slide2);
		}
		return totalInterest;
	}
}
