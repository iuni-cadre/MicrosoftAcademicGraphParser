package iuni.msacademics.parser;

import iuni.msacademics.parser.utls.Constants;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicLong;
import java.nio.file.Files;
import java.io.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;


/*
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
*/

abstract public class MAGFormatParser {
    protected static final Logger logger = LoggerFactory.getLogger(MAGFormatParser.class);
    protected String sourceDir = null;
    protected String targetDir = null;
    protected String csvPrefix = null;
    protected String affiliationsCSVName = null;
    protected String authorsCSVName = null;
    protected String conferenceInstancesCSVName = null;
    protected String conferenceSeriesCSVName = null;
    protected String fieldOfStudyChildrenCSVName = null;
    protected String fieldsOfStudyCSVName = null;
    protected String journalsCSVName = null;
    protected String paperAbstractInvertedIndexCSVName = null;
    protected String paperAuthorAffiliationsCSVName = null;
    protected String paperCitationContextsCSVName = null;
    protected String paperFieldsOfStudyCSVName = null;
    protected String paperLanguagesCSVName = null;
    protected String paperRecommendationsCSVName = null;
    protected String paperReferencesCSVName = null;
    protected String paperResourcesCSVName = null;
    protected String paperURLsCSVName = null;
    protected String papersCSVName = null;
    protected String relatedFieldOfStudyCSVName = null;
    protected static final AtomicLong counter = new AtomicLong(0);
    protected static final AtomicLong LAST_TIME_MS = new AtomicLong();

    public MAGFormatParser(String sourceDir, String targetDir,
       String csvPrefix)
    {
       this.sourceDir = new String(sourceDir);
       this.targetDir = new String(targetDir);
       this.csvPrefix = new String(csvPrefix);

       affiliationsCSVName = csvPrefix + "_affiliations.csv";
       authorsCSVName = csvPrefix + "_authors.csv";
       conferenceInstancesCSVName = csvPrefix + "_conferenceInstances.csv";
       conferenceSeriesCSVName = csvPrefix + "_conferenceSeries.csv";
       fieldOfStudyChildrenCSVName = csvPrefix + "_fieldOfStudyChildren.csv";
       fieldsOfStudyCSVName = csvPrefix + "_fieldsOfStudy.csv";
       journalsCSVName = csvPrefix + "_journals.csv";
       paperAbstractInvertedIndexCSVName = csvPrefix + "_paperAbstractInvertedIndex.csv";
       paperAuthorAffiliationsCSVName = csvPrefix + "_paperAuthorAffiliations.csv";
       paperCitationContextsCSVName = csvPrefix + "_paperCitationContexts.csv";
       paperFieldsOfStudyCSVName = csvPrefix + "_paperFieldsOfStudy.csv";
       paperLanguagesCSVName = csvPrefix + "_paperLanguages.csv";
       paperRecommendationsCSVName = csvPrefix + "_paperRecommendations.csv";
       paperReferencesCSVName = csvPrefix + "_paperReferences.csv";
       paperResourcesCSVName = csvPrefix + "_paperResources.csv";
       paperURLsCSVName = csvPrefix + "_paperURLs.csv";
       papersCSVName = csvPrefix + "_papers.csv";
       relatedFieldOfStudyCSVName = csvPrefix + "_relatedFieldOfStudy.csv";
    }

    final public long uniqueCurrentTimeMS() {
      long now = System.currentTimeMillis() * 1000;
      while(true) {
        long lastTime = LAST_TIME_MS.get();
        if (lastTime >= now) {
          now = lastTime + 1;
        }
        if (LAST_TIME_MS.compareAndSet(lastTime, now)) {
          return now;
        }
      }
    }

    abstract public void readFile();

    // This is a function which splits the big file into multiple smaller chunks
    final private static List<String> splitFile(File file) throws IOException {
      List<String> fileList = new ArrayList<>();
      int partCounter = 1; // I like to name parts from 001, 002, 003, ...
      // you can change it to 0 if you want 000, 001, ...

      int sizeOfFiles = 1024 * 1024* 1000;// 1GB
      byte[] buffer = new byte[sizeOfFiles];

      String fileName = file.getName();

      //try-with-resources to ensure closing stream
      try (FileInputStream fis = new FileInputStream(file);
        BufferedInputStream bis = new BufferedInputStream(fis)) {

        int bytesAmount = 0;
        while ((bytesAmount = bis.read(buffer)) > 0) {
          //write each chunk of data into separate file with different number in name
          String filePartName = String.format("%s.%03d", fileName, partCounter++);
          File newFile = new File(file.getParent(), filePartName);
          try (FileOutputStream out = new FileOutputStream(newFile)) {
            out.write(buffer, 0, bytesAmount);
          }
          fileList.add(newFile.getAbsolutePath());
        }
      }
      return fileList;
    }

    final protected Long getId(Map<String, Long> idMap, String key) {
      if (idMap.containsKey(key)) {
        return idMap.get(key);
      } else {
        long gId = uniqueCurrentTimeMS();
        idMap.put(key, gId);
        return gId;
      }
    }

    final protected String removeSpecialCharacters (String inputString) {
      if (inputString.contains("\"")) {
        inputString = inputString.replace("\"", "'");
      }
      if (inputString.contains("~")) {
        inputString = inputString.replace("~", " ");
      }
      if (inputString.contains("\n")) {
        inputString = inputString.replace("\n", " ");
      }
      return inputString;
    }

    // This is a function to check for unicode strings
    final protected boolean checkForUnicode (String inputString) {
      Pattern p = Pattern.compile("\\\\u(\\p{XDigit}{4})");
      Matcher m = p.matcher(inputString);
      if (m.find()) {
        return true;
      } else {
        return false;
      }
    }
}


