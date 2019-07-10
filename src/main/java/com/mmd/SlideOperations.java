package com.mmd;

import com.mmd.models.Photo;
import com.mmd.models.Slide;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.function.Predicate.not;

public class SlideOperations {

	public static List<Slide> getHorizontalSlides(List<Photo> photoList) {
		return photoList.parallelStream()
				.filter(Photo::isHorizontal)
				.sorted(Comparator.comparing(Photo::getTotalSize))
				.map(Slide::new)
				.collect(Collectors.toList());
	}

	public static List<Photo> getVerticalPhotoList(List<Photo> photoList) {
		return photoList
				.parallelStream().filter(not(Photo::isHorizontal))
				.sorted(Comparator.comparing(Photo::getTotalSize))
				.collect(Collectors.toList());
	}

}
