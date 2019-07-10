package com.mmd;

import com.mmd.models.Photo;
import one.util.streamex.EntryStream;

import java.text.DecimalFormat;
import java.util.*;

public class Main {
	static String aPart = "inputs/a_example.txt";
	static String bPart = "inputs/b_lovely_landscapes.txt";
	static String cPart = "inputs/c_memorable_moments.txt";
	static String dPart = "inputs/d_pet_pictures.txt";
	static String ePart = "inputs/e_shiny_selfies.txt";
	private static DecimalFormat df = new DecimalFormat("#.##");

	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		String currentPart = bPart;

		FileOperations.calculateTagSet(currentPart);

		ArrayList<Photo> photos = FileOperations.readInputs(currentPart);
		photos.sort(Comparator.comparing(x -> x.getTags().length));
		ArrayList<Photo> resultList = fullGreedyParallel(photos);

		FileOperations.writeResults(resultList, currentPart);

		long endTime = System.currentTimeMillis();
		System.out.println("Total process took " + (endTime - startTime) / 1000.0 + " seconds.");
	}

	private static ArrayList<Photo> fullGreedyParallel(List<Photo> slideList) {
		ArrayList<Photo> resultList = new ArrayList<>();
		Photo current = slideList.get(0);
		resultList.add(current);

		long calculationStartTime = System.currentTimeMillis();
		for (int i = 1; i < slideList.size(); i++) {
			if (i % 1000 == 0) {
				System.out.println(df.format(((double) i / slideList.size()) * 100) + " % completed.");
				long currentTime = System.currentTimeMillis();
				double elapsedTime = (currentTime - calculationStartTime) / 1000.0;
				System.out.println("Current elapsed time = " + elapsedTime + " seconds.");
				System.out.println("Estimated calculation Time = " + elapsedTime * slideList.size() / i);
				System.out.println("Calculated Score = " + CalculateScore.calculateScore(resultList));
				System.out.println();
			}

			Photo now = current;
			Integer maxIndex = EntryStream
					.of(slideList)
					.skip(i)
					.parallel()
					.mapValues(now::calculateInterest)
					.max(Comparator.comparingInt(Map.Entry::getValue))
					.get().getKey();
			current = slideList.get(maxIndex);
			resultList.add(current);
			Collections.swap(slideList, maxIndex, i);
		}
		return resultList;
	}
}
