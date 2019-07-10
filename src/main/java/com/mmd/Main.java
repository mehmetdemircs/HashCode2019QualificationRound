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
		String currentPart = cPart;
		ArrayList<Photo> photos = FileOperations.readInputs(currentPart);
		System.out.println("Total Tag Count = " + tagMap.size());

		List<Slide> horizontalSlides = SlideOperations.getHorizontalSlides(photos);
		List<Photo> verticalPhotoList = SlideOperations.getVerticalPhotoList(photos);

		ArrayList<Slide> resultList = fullGreedyParallelVertical(horizontalSlides,verticalPhotoList);

		FileOperations.writeResults(resultList, currentPart);
		System.out.println(CalculateScore.calculateScore(resultList));

		long endTime = System.currentTimeMillis();
		System.out.println("Total process took " + (endTime - startTime) / 1000.0 + " seconds.");
	}

	private static ArrayList<Slide> fullGreedyParallelVertical(List<Slide> horizontalSlides, List<Photo> verticalPhotoList) {
		System.out.println("Full greedy parallel vertical is starting!");

		int totalSize = horizontalSlides.size() + verticalPhotoList.size();
		int currentIndex = 0;
		ArrayList<Slide> resultList = new ArrayList<>();
		Slide current;
		int horizontalIndex = 0;
		int verticalIndex = 0;
		if (!horizontalSlides.isEmpty()) {
			current = horizontalSlides.get(0);
			horizontalIndex++;
		} else {
			current = new Slide(verticalPhotoList.get(0), verticalPhotoList.get(1));
			verticalIndex = 2;
		}
		resultList.add(current);
		currentIndex += verticalIndex + horizontalIndex;
		long calculationStart = System.currentTimeMillis();
		while (verticalIndex < verticalPhotoList.size() || horizontalIndex < horizontalSlides.size()) {
			currentIndex++;
			if (currentIndex % 1000 == 0) {
				System.out.println(df.format(((double) currentIndex / totalSize) * 100) + " % completed.");
				long currentTime = System.currentTimeMillis();
				double elapsedTime = (currentTime - calculationStart) / 1000.0;
				System.out.println("Current elapsed time = " + elapsedTime + " seconds.");
				System.out.println("Estimated calculation Time = " + elapsedTime * totalSize / currentIndex);
				System.out.println("Calculated Score = " + CalculateScore.calculateScore(resultList));
				System.out.println();
			}

			Slide now = current;
			if (!current.isHorizontal() && current.getPhoto2() == null) {//single photo case
				int start = verticalIndex;
				Integer maxIndex = EntryStream
						.of(verticalPhotoList)
						.skip(start)
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
				Collections.swap(verticalPhotoList, maxIndex, verticalIndex++);
			} else {//new slide case
				Map.Entry<Integer, Integer> horizontalEntry = null;
				if (horizontalIndex < horizontalSlides.size()) {
					horizontalEntry = EntryStream
							.of(horizontalSlides)
							.skip(horizontalIndex)
							.parallel()
							.mapValues(now::calculateInterest)
							.max(Comparator.comparingInt(Map.Entry::getValue))
							.get();
				}
				if (verticalIndex < verticalPhotoList.size()) {
					int start = verticalIndex;
					Map.Entry<Integer, Integer> maxVertical = EntryStream
							.of(verticalPhotoList)
							.skip(start)
							.parallel()
							.mapValues(x -> now.calculateInterest(new Slide(x)))
							.max(Comparator.comparingInt(Map.Entry::getValue))
							.get();
					Integer maxIndex = maxVertical.getKey();
					Integer value = maxVertical.getValue();
					if (horizontalEntry == null || horizontalEntry.getValue() < value) {
						current.next = new Slide(verticalPhotoList.get(maxIndex));
						current.next.previous = current;
						current = current.next;
						Collections.swap(verticalPhotoList, maxIndex, verticalIndex++);
						continue;
					}
				}

				Integer maxIndex = horizontalEntry.getKey();
				Slide nextSlide = horizontalSlides.get(maxIndex);
				nextSlide.previous = current;
				current.next = nextSlide;
				current = nextSlide;
				resultList.add(current);
				Collections.swap(horizontalSlides, maxIndex, horizontalIndex++);
			}
		}
		return resultList;
	}
}
