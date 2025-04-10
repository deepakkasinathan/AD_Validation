package com.est.ad.service;

import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.est.ad.entity.AdTemplate;
import com.est.ad.entity.UserObjects;
import com.est.ad.repo.UserObjectRepo;

@Service
public class ImportService {

	@Autowired
	private UserObjectRepo userObjectRepo;
	@Autowired
	private static VelocityTemplate velocityTemplate;

	public ImportService(UserObjectRepo userObjectRepo,VelocityTemplate adScriptGenevelocityTemplaterator) {
		this.userObjectRepo = userObjectRepo;
		this.velocityTemplate=velocityTemplate;
	}

	public boolean ValidateAdMetaData(AdTemplate adTemplate) {
		System.out.println(adTemplate.getLast_modified_date());
		UserObjects userobject = userObjectRepo.findByObjectName(adTemplate.getTable_name(), "TABLE");
		System.out.println(userobject.getObject_name());
		System.out.println(userobject.getTimestamp());

		if (compareDates(adTemplate.getLast_modified_date(), userobject.getTimestamp())) {
			System.out.println("Metadata is not modified");
			return true;
		}

		else {
			System.out.println("metadata changed after the ad generation");
			return false;
		}

	}

	public String formattedTimestamp() {
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd:HH:mm:ss");

		return now.format(formatter);
	}

	public boolean compareDates(String Ad_Date, String metadata_date) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd:HH:mm:ss");

		LocalDateTime dateTime1 = LocalDateTime.parse(Ad_Date, formatter);
		LocalDateTime dateTime2 = LocalDateTime.parse(metadata_date, formatter);

		if (dateTime1.isBefore(dateTime2)) {
			return false;

		} else if (dateTime1.isAfter(dateTime2)) {
			System.out.println("ts1 is after ts2");
			return true;
		} else {
			System.out.println("Both timestamps are equal");
			return true;
		}
	}

	public static boolean validateAgainstTemplate(MultipartFile mainFile, AdTemplate adTemplate) {
		try {
			String Content =  new String(mainFile.getBytes());
			String generatedContent = velocityTemplate.renderTemplate(adTemplate).toString();
			

			if (Content.equals(generatedContent)) {
				System.out.println("Output matches template structure.");
				return true;
			} else {
				System.out.println("Output differs from expected template structure.");
				System.out.println("--- AdFile Content ---\n" + Content);
				System.out.println("--- Generated AdFile Syntax Content ---\n" + generatedContent);
				return false;
			}

		} catch (IOException e) {
			System.err.println("Error reading files: " + e.getMessage());
			return false;
		}
	}
}
