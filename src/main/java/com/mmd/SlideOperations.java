package com.mmd;

import com.mmd.models.Photo;
import com.mmd.models.Slide;
import one.util.streamex.EntryStream;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.function.Predicate.not;

public class SlideOperations {

	public static List<Slide> getHorizontalSlides(List<Photo> photoList) {
		return photoList.parallelStream()
				.filter(Photo::isHorizontal)
				.sorted(Comparator.comparing(Photo::getTotalSize, Comparator.reverseOrder()))
				.map(Slide::new)
				.collect(Collectors.toList());
	}

	public static List<Photo> getVerticalPhotoList(List<Photo> photoList) {
		return photoList
				.parallelStream().filter(not(Photo::isHorizontal))
				.sorted(Comparator.comparing(Photo::getTotalSize, Comparator.reverseOrder()))
				.collect(Collectors.toList());
	}

	public static List<Slide> getVerticalSlidesByDifferentTags(List<Photo> photoList) {
		List<Photo> verticalPhotoList = getVerticalPhotoList(photoList);
		ArrayList<Slide> verticalSlides = new ArrayList<>();

		for (int i = 0; i < verticalPhotoList.size(); i++) {
			Photo current = verticalPhotoList.get(i);
			int start = i + 1;
			Integer maxIndex = EntryStream
					.of(verticalPhotoList)
					.skip(start)
					.parallel()
					.mapValues(current::calculateInterest)
					.min(Comparator.comparingLong(Map.Entry::getValue))
					.get().getKey();
			Photo photo2 = verticalPhotoList.get(maxIndex);
			verticalSlides.add(new Slide(current, photo2));
			Collections.swap(verticalPhotoList, maxIndex, i + 1);
			i++;
		}
		return verticalSlides;
	}

}
