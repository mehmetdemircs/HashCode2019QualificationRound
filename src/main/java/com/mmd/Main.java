package com.mmd;

import com.mmd.models.Photo;
import com.mmd.models.Slide;
import one.util.streamex.EntryStream;

import java.text.DecimalFormat;
import java.util.*;

public class Main {
	static String aPart = "inputs/a_example.txt";
	static String bPart = "inputs/b_lovely_landscapes.txt";
	static String cPart = "inputs/c_memorable_moments.txt";
	static String dPart = "inputs/d_pet_pictures.txt";
	static String ePart = "inputs/e_shiny_selfies.txt";
	static DecimalFormat df = new DecimalFormat("#.##");
	public static HashMap<String, Integer> tagMap = new HashMap<>();

	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		String currentPart = ePart;
		ArrayList<Photo> photos = FileOperations.readInputs(currentPart);
		System.out.println("Total Tag Count = " + tagMap.size());

		photos.sort(Comparator.comparing(Photo::getTotalSize));
		ArrayList<Slide> resultList = fullGreedyParallelVertical(photos);


		FileOperations.writeResults(resultList, currentPart);

		long endTime = System.currentTimeMillis();
		System.out.println("Total process took " + (endTime - startTime) / 1000.0 + " seconds.");
	}

	private static ArrayList<Slide> fullGreedyParallelVertical(List<Photo> verticalPhotoList) {
		System.out.println("Full greedy parallel vertical is starting!");
		ArrayList<Slide> resultList = new ArrayList<>();
		Slide current = new Slide(verticalPhotoList.get(0), verticalPhotoList.get(1));
		resultList.add(current);
		long calculationStart = System.currentTimeMillis();
		for (int i = 2; i < verticalPhotoList.size(); i++) {
			if (i % 1000 == 0) {
				System.out.println(df.format(((double) i / verticalPhotoList.size()) * 100) + " % completed.");
				long currentTime = System.currentTimeMillis();
				double elapsedTime = (currentTime - calculationStart) / 1000.0;
				System.out.println("Current elapsed time = " + elapsedTime + " seconds.");
				System.out.println("Estimated calculation Time = " + elapsedTime * verticalPhotoList.size() / i);
				System.out.println("Calculated Score = " + CalculateScore.calculateScore(resultList));
				System.out.println();
			}
			Slide now = current;
			if (current.getPhoto2() == null) {//single photo case
				Integer maxIndex = EntryStream
						.of(verticalPhotoList)
						.skip(i)
						.parallel()
						.mapValues(x -> new Slide(now.getPhoto1(), x).calculateInterest(now.previous))
						.max(Comparator.comparingInt(Map.Entry::getValue))
						.get().getKey();
				Photo nextPhoto = verticalPhotoList.get(maxIndex);
				Slide nextSlide = new Slide(current.getPhoto1(), nextPhoto);
				nextSlide.previous = current.previous;
				nextSlide.previous.next = nextSlide;
				current = nextSlide;
				resultList.add(current);
				Collections.swap(verticalPhotoList, maxIndex, i);
			} else {//new slide case
				Integer maxIndex = EntryStream
						.of(verticalPhotoList)
						.skip(i)
						.parallel()
						.mapValues(x -> now.calculateInterest(new Slide(x)))
						.max(Comparator.comparingInt(Map.Entry::getValue))
						.get().getKey();
				current.next = new Slide(verticalPhotoList.get(maxIndex));
				current.next.previous = current;
				current = current.next;
				Collections.swap(verticalPhotoList, maxIndex, i);
			}
		}
		return resultList;
	}
}
