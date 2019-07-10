package com.mmd;

import com.mmd.models.Photo;
import com.mmd.models.Slide;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

public class FileOperations {
	public static void writeResults(ArrayList<Slide> resultList, String inputFileName) {
		String outputFileName = "outputs/" + inputFileName.substring(inputFileName.indexOf("/") + 1, inputFileName.indexOf(".txt"));
		outputFileName += ".out";
		System.out.println("Output File Name = " + outputFileName);
		System.out.println("Calculated Score = " + CalculateScore.calculateScore(resultList));

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File(outputFileName)))) {
			writer.write(resultList.size() + "\n");
			for (Slide slide : resultList) {
				writer.write(slide.getOutputFormat());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static ArrayList<Photo> readInputs(String fileName) {
		calculateTagMap(fileName);
		ArrayList<Photo> photoList = new ArrayList<>();
		try (Scanner scan = new Scanner(new File(fileName))) {
			int photoNumber = scan.nextInt();
			for (int i = 0; i < photoNumber; i++) {
				String type = scan.next();
				HashSet<String> tagSet = new HashSet<>();
				int tagNumber = scan.nextInt();
				for (int j = 0; j < tagNumber; j++) {
					String tag = scan.next();
					tagSet.add(tag);
				}
				Photo photo = new Photo(i, type.equals("H"), tagSet);
				photoList.add(photo);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return photoList;
	}

	public static void calculateTagMap(String fileName) {
		int tagIndex = 0;
		try (Scanner scan = new Scanner(new File(fileName))) {
			int photoNumber = scan.nextInt();
			for (int i = 0; i < photoNumber; i++) {
				String type = scan.next();
				int tagNumber = scan.nextInt();
				for (int j = 0; j < tagNumber; j++) {
					String tag = scan.next();
					if (!Main.tagMap.containsKey(tag)) {
						Main.tagMap.put(tag, tagIndex++);
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
