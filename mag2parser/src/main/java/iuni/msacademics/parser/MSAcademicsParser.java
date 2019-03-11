package iuni.msacademics.parser;

import iuni.msacademics.parser.utls.Constants;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MSAcademicsParser {
    private static final Logger logger = LoggerFactory.getLogger(MSAcademicsParser.class);
    private String sourceDir = null;
    private String targetDir = null;
    private String institutionCSVName = null;
    private String confInstanceCSVName = null;
    private String authorsCSVName = null;
    private String abstractIndexCSVName = null;
    private String languageCSVName = null;
    private String urlCSVName = null;
    private String confSeriesCSVName = null;
    private String fieldOfStudyCSVName = null;
    private String journalCSVName = null;
    private String authorAffCSVName = null;
    private String affiliatePaperCSVName = null;
    private String paperJournalCSVName = null;
    private String authorPaperCSVName = null;
    private String paperPaperCitationContextsCSVName = null;
    private String paperConfIdCSVName = null;
    private String confIdConfSeriesIdCSVName = null;
    private String paperIdFOSCSVName = null;
    private String paperPaperAbstractCSVName = null;
    private String paperPaperLanguagesCSVName = null;
    private String paperPaperURLsForCSVName = null;
    private String fosFosCSVName = null;
    private String papersCSVName = null;
    private String paperRecommendationsCSVName = null;
    private static final AtomicLong counter = new AtomicLong(0);
    private Map<String, Long> languageIdMap = new HashMap<>();
    private static final AtomicLong LAST_TIME_MS = new AtomicLong();


    public static void main(String[] args) {
      try {
        MSAcademicsParser parser = new MSAcademicsParser();
        parser.parseArguments(args);
        parser.readFile();
      } catch (Exception e) {
        logger.error("Error occurred !!!" , e);
        e.printStackTrace();
      }
    }

    public long uniqueCurrentTimeMS() {
      long now = System.currentTimeMillis() * 1000;
      while(true) {
        long lastTime = LAST_TIME_MS.get();
        if (lastTime >= now) {
          now = lastTime+1;
        }
        if (LAST_TIME_MS.compareAndSet(lastTime, now)) {
          return now;
        }
      }
    }

    public void parseArguments(String[] args) throws Exception {
      try {
        Options options = new Options();

        options.addOption("sourceDir", true , "Home directory of JSON files");
        options.addOption("targetDir", true, "Directory to save csv files");
        options.addOption("csvPrefix", true, "Prefix for publish and soure csv files");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse( options, args);
        Option[] cmdOptions = cmd.getOptions();
        if (cmdOptions == null || cmdOptions.length == 0) {
          System.out.println("You have not provided needed info, sourceDir, targetDir and csvPrefix");
          throw new Exception("You have not provided needed info, sourceDir, targetDir and csvPrefix");
        }
        for (Option option : cmdOptions) {

          if (option.getOpt().equals("sourceDir")) {
            sourceDir = option.getValue();
            if (sourceDir == null) {
              System.out.println("Please provide the path for sourceDir option");
              System.exit(0);
            }
          } else if (option.getOpt().equals("targetDir")) {
            targetDir = option.getValue();
            if (targetDir == null) {
              System.out.println("Please provide the path for targetDir option");
              System.exit(0);
            }
          } else if (option.getOpt().equals("csvPrefix")) {
            String csvPrefix = option.getValue();
            if (csvPrefix == null) {
              System.out.println("Please provide csvPrefix option");
              System.exit(0);
            } else {
              institutionCSVName = csvPrefix + "_institution.csv";
              confInstanceCSVName = csvPrefix + "_confInstance.csv";
              authorsCSVName = csvPrefix + "_author.csv";
              abstractIndexCSVName = csvPrefix + "_abstractIndex.csv";
              languageCSVName = csvPrefix + "_language.csv";
              urlCSVName = csvPrefix + "_url.csv";
              papersCSVName = csvPrefix + "_paper.csv";
              confSeriesCSVName = csvPrefix + "_confSeries.csv";
              journalCSVName = csvPrefix + "_journal.csv";
              fieldOfStudyCSVName = csvPrefix + "_fos.csv";
              authorAffCSVName = csvPrefix + "_autAff.csv";
              affiliatePaperCSVName = csvPrefix + "_affPaper.csv";
              paperJournalCSVName = csvPrefix + "_paperJournal.csv";
              authorPaperCSVName = csvPrefix + "_authorPaper.csv";
              paperPaperCitationContextsCSVName = csvPrefix + "_paperPaperCitationContexts.csv";
              paperConfIdCSVName = csvPrefix + "_paperConfId.csv";
              confIdConfSeriesIdCSVName = csvPrefix + "_confIdConfSeriesId.csv";
              paperIdFOSCSVName = csvPrefix + "_paperFOS.csv";
              paperPaperAbstractCSVName = csvPrefix + "_paperPaperAbstract.csv";
              paperPaperLanguagesCSVName = csvPrefix + "_paperPaperLang.csv";
              paperPaperURLsForCSVName = csvPrefix + "_paperPaperURL.csv";
              fosFosCSVName = csvPrefix + "_fosFos.csv";
            }
          }
        }

      } catch (ParseException e) {
        logger.error("Error while reading command line parameters" , e);
        throw new Exception("Error while reading command line parameters" , e);
      }
    }


    public void readFile() {

      File[] sourceFiles = null;
      try  {
        File fileSource = new File(sourceDir);
        if (fileSource.exists() && fileSource.isDirectory()) {
          sourceFiles = fileSource.listFiles();
        }

        PrintWriter institutionCSV = new PrintWriter(new FileWriter(targetDir + File.separator + institutionCSVName, true));
        PrintWriter authorsCSV = new PrintWriter(new FileWriter(targetDir + File.separator + authorsCSVName, true));
        PrintWriter confInstanceCSV = new PrintWriter(new FileWriter(targetDir + File.separator + confInstanceCSVName, true));
        PrintWriter confIdConfSeriesIdCSV = new PrintWriter(new FileWriter(targetDir + File.separator + confIdConfSeriesIdCSVName, true));
        PrintWriter confSeriesCSV = new PrintWriter(new FileWriter(targetDir + File.separator + confSeriesCSVName, true));
        PrintWriter fosFOSCSV = new PrintWriter(new FileWriter(targetDir + File.separator + fosFosCSVName, true));
        PrintWriter fosCSV = new PrintWriter(new FileWriter(targetDir + File.separator + fieldOfStudyCSVName, true));
        PrintWriter journalCSV = new PrintWriter(new FileWriter(targetDir + File.separator + journalCSVName, true));
        PrintWriter abstractIndexCSV = new PrintWriter(new FileWriter(targetDir + File.separator + abstractIndexCSVName, true));
        PrintWriter authorAffCSV = new PrintWriter(new FileWriter(targetDir + File.separator + authorAffCSVName, true));
        PrintWriter paperPaperAbstractCSV = new PrintWriter(new FileWriter(targetDir + File.separator + paperPaperAbstractCSVName, true));
        PrintWriter paperPaperCitationContextsCSV = new PrintWriter(new FileWriter(targetDir + File.separator + paperPaperCitationContextsCSVName, true));
        PrintWriter paperFOSCSV = new PrintWriter(new FileWriter(targetDir + File.separator + paperIdFOSCSVName, true));
        PrintWriter paperLanguagesCSV = new PrintWriter(new FileWriter(targetDir + File.separator + paperPaperLanguagesCSVName, true));
		PrintWriter paperRecommendationsCSV = new PrintWriter(new FileWriter(targetDir + File.separator + paperRecommendationsCSVName, true));
        PrintWriter languageCSV = new PrintWriter(new FileWriter(targetDir + File.separator + languageCSVName, true));
        PrintWriter urlCSV = new PrintWriter(new FileWriter(targetDir + File.separator + urlCSVName, true));
        PrintWriter affiliatePaperCSV = new PrintWriter(new FileWriter(targetDir + File.separator + affiliatePaperCSVName, true));
        PrintWriter paperJournalCSV = new PrintWriter(new FileWriter(targetDir + File.separator + paperJournalCSVName, true));
        PrintWriter authorPaperCSV = new PrintWriter(new FileWriter(targetDir + File.separator + authorPaperCSVName, true));   
        PrintWriter paperConfIdCSV = new PrintWriter(new FileWriter(targetDir + File.separator + paperConfIdCSVName, true));
        PrintWriter paperPaperURLsCSV = new PrintWriter(new FileWriter(targetDir + File.separator + paperPaperURLsForCSVName, true));
        PrintWriter papersCSV = new PrintWriter(new FileWriter(targetDir + File.separator + papersCSVName, true));
        
        institutionCSV.println(":ID~rank~normalized_name~display_name~paper_count~citation_count~:LABEL");
        confInstanceCSV.println(":ID~rank~normalized_name~display_name~location~official_uRL~start_date~end_date~abstract_registration_date~submission_deadline_date~notification_due_date~final_version_dues_date~paper_count~citation_count~:LABEL");
        authorsCSV.println(":ID~rank~normalized_name~display_name~last_known_affiliation_ID~paper_count~citation_count~:LABEL");
        abstractIndexCSV.println(":ID~indexed_abstract~:LABEL");
        languageCSV.println(":ID~paper_language_code~:LABEL");
        urlCSV.println(":ID~source_type~source_url~:LABEL");
        journalCSV.println(":ID~rank~normalized_name~display_name~paper_count~citation_count~:LABEL");
        papersCSV.println(":ID~rank~doi~doc_type~paper_title~original_title~book_title~paper_year~paper_date~publisher~volume~issue~first_page~last_page~reference_count~citation_count~estimated_citation_count~:LABEL");
        confSeriesCSV.println(":ID~rank~normalized_name~display_name~paper_count~citation_count~:LABEL");
        fosCSV.println(":ID~rank~normalized_name~display_name~level~paper_count~citation_count~:LABEL");
        authorAffCSV.println(":START_ID~:END_ID~:TYPE");
        affiliatePaperCSV.println(":START_ID~:END_ID~:TYPE");
        paperJournalCSV.println(":START_ID~:END_ID~:TYPE");
        authorPaperCSV.println(":START_ID~:END_ID~:TYPE");
        paperPaperCitationContextsCSV.println(":START_ID~context~:END_ID~:TYPE");
        paperConfIdCSV.println(":START_ID~:END_ID~:TYPE");
        confIdConfSeriesIdCSV.println(":START_ID~:END_ID~:TYPE");
        paperFOSCSV.println(":START_ID~:END_ID~:TYPE");
        paperPaperAbstractCSV.println(":START_ID~:END_ID~:TYPE");
        paperLanguagesCSV.println(":START_ID~:END_ID~:TYPE");
        paperPaperURLsCSV.println(":START_ID~:END_ID~:TYPE");
        fosFOSCSV.println(":START_ID~:END_ID~:TYPE");

        if (sourceFiles != null && sourceFiles.length != 0) {
          for (File sourceFile : sourceFiles) {
            Constants.sourceFileNames sourceFileName = Constants.sourceFileNamesMap.get(sourceFile.getName());
            switch (sourceFileName) {
              case AFFILIATIONS:
                   parseAffliliationsFile(sourceFile.getAbsolutePath(), institutionCSV);
                   break;
              case AUTHORS:
                   parseAuthorsFile(sourceFile.getAbsolutePath(), authorsCSV);
                   break;
              case CONFERENCE_INSTANCES:
                   parseConfInstances(sourceFile.getAbsolutePath(), confInstanceCSV);
                   break;
              case CONFERENCE_SERIES:
                   parseConfSeriesFile(sourceFile.getAbsolutePath(), confSeriesCSV);
                   break;     
              case FIELD_OF_STUDY_CHILDREN:
                   parseFOSChildrenFile(sourceFile.getAbsolutePath(), fosFOSCSV);
                   break;
              case FIELD_OF_STUDY:
                   parseFOSFile(sourceFile.getAbsolutePath(), fosCSV);
                   break;     
              case JOURNALS:
                   parseJournalFile(sourceFile.getAbsolutePath(), journalCSV);
                   break;        
              case PAPER_ABSTRACT_INVERTED_INDEX:
                   parseAbstractInvertedIndexFile(sourceFile.getAbsolutePath(), abstractIndexCSV, paperPaperAbstractCSV);
                   break;
              case PAPER_AUTHOR_AFFILIATIONS:
                   parsePaperAuthorAffiliationFile(sourceFile.getAbsolutePath(),authorAffCSV, affiliatePaperCSV, authorPaperCSV);
                   break;
              case PAPER_CITATION_CONTEXTS:
                   parsePaperCitationContextFile(sourceFile.getAbsolutePath(), paperPaperCitationContextsCSV);
                   break;
              case PAPER_FIELD_OF_STUDY:
                   parsePaperFOSFile(sourceFile.getAbsolutePath(), paperFOSCSV);
                   break;                  
              case PAPER_LANGUAGES:
                   parsePaperLanguagesFile(sourceFile.getAbsolutePath(), languageCSV, paperLanguagesCSV);
                   break;
              case PAPER_RECOMMENDATIONS:
                   parsePaperRecommendationsFile(sourceFile.getAbsolutePath(), languageCSV, paperRecommendationsCSV);
                   break;
              case PAPER_REFERENCES:
                   parsePaperReferencesFile(sourceFile.getAbsolutePath(), papersCSV, paperJournalCSV, paperConfIdCSV, confIdConfSeriesIdCSV);
                   break;   
              case PAPER_RESOURCES:
                   parsePaperResourcesFile(sourceFile.getAbsolutePath(), papersCSV, paperJournalCSV, paperConfIdCSV, confIdConfSeriesIdCSV);
                   break;
              case PAPER_URLS:
                   parsePaperURLsFile(sourceFile.getAbsolutePath(), urlCSV, paperPaperURLsCSV);
                   break;
              case PAPERS:
                   parsePaperFile(sourceFile.getAbsolutePath(), papersCSV, paperJournalCSV, paperConfIdCSV, confIdConfSeriesIdCSV);
                   break;            
              case RELATED_FIELD_OF_STUDY:
                   parseRelatedFieldOfStudy(sourceFile.getAbsolutePath(), papersCSV, paperJournalCSV, paperConfIdCSV, confIdConfSeriesIdCSV);
                   break;            
              default:
                   logger.error("Could not find the file type specified !!!");
                   break;
            }
          }
        }

      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    
	// This is a function which parses the Affiliations Table in Microsoft Academic Graph Schema
    private void parseAffliliationsFile(String path, PrintWriter outputFile) {
      try {
        BufferedReader br = Files.newBufferedReader(Paths.get(path), StandardCharsets.UTF_8);
        for (String line; (line = br.readLine()) != null;) {
          String[] splits = line.split("\t");
          if (splits.length != 0 && splits.length > 5) {
            String intituteContent = "\""  + splits[0] + "\""  + "~" +
                            		 "\""  + splits[1] + "\""  + "~" +
                            		 "\"" + removeSpecialCharacters(splits[2]) + "\"" + "~" +
                            		 "\"" + removeSpecialCharacters(splits[3]) + "\"" + "~" +
                            		 "\""  + splits[4] + "\""  + "~" +
                            		 "\""  + splits[5] + "\""  + "~institution";
            outputFile.println(intituteContent);
          }
        }
        outputFile.flush();
        br.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    
    // This is a function which parses the Authors Table in Microsoft Academic Graph Schema
    private void parseAuthorsFile(String path, PrintWriter outputFile) {
      try {
        BufferedReader br = Files.newBufferedReader(Paths.get(path), StandardCharsets.UTF_8);
        for (String line; (line = br.readLine()) != null;) {
          String[] splits = line.split("\t");
          if (splits.length != 0 && splits.length > 6) {
            String authorContent = "\""  + splits[0] + "\""  + "~" +
                              	   "\""  + splits[1] + "\""  + "~" +
                              	   "\"" + removeSpecialCharacters(splits[2]) + "\"" + "~" +
                              	   "\"" + removeSpecialCharacters(splits[3]) + "\"" + "~" +
                              	   "\""  + splits[4] + "\""  + "~" +
                              	   "\""  + splits[5] + "\""  + "~" +
                              	   "\""  + splits[6] + "\""  +
                              	   "~author";
            outputFile.println(authorContent);
          }
        }
        outputFile.flush();
        br.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    
    // This is a function which parses the Conference Instances Table in Microsoft Academic Graph Schema
    private void parseConfInstances(String path, PrintWriter outputFile) {
      try {
        BufferedReader br = Files.newBufferedReader(Paths.get(path), StandardCharsets.UTF_8);
        for (String line; (line = br.readLine()) != null;) {
          String[] splits = line.split("\t");
          if (splits.length != 0 && splits.length > 13) {
            String confInstanceContent = "\""  + splits[0] + "\""  + "~" +
                            			 "\""  + splits[1] + "\""  + "~" +
                            			 "\"" + removeSpecialCharacters(splits[2]) + "\"" + "~" +
                            			 "\"" + removeSpecialCharacters(splits[3]) + "\"" + "~" +
                            			 "\""  + removeSpecialCharacters(splits[4]) + "\""  + "~" +
                            			 "\""  + removeSpecialCharacters(splits[5]) + "\""  + "~" +
                            			 "\"" + splits[6] + "\"" + "~" +
                            			 "\"" + splits[7] + "\"" + "~"+
                            			 "\"" + splits[8] + "\"" + "~" +
                            			 "\"" + splits[9] + "\"" + "~" +
                            			 "\"" + splits[10] + "\"" + "~" +
                            			 "\"" + splits[11] + "\"" + "~" +
                            			 "\"" + splits[12] + "\"" + "~" +
                            			 "\"" + splits[13] + "\"" + "~conference_instance" ;
            outputFile.println(confInstanceContent);
          }
        }
        outputFile.flush();
        br.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    
    // This is a function which parses the Conference Series Table in Microsoft Academic Graph Schema   
    private void parseConfSeriesFile(String path, PrintWriter outputFile) {
      try {
        BufferedReader br = Files.newBufferedReader(Paths.get(path), StandardCharsets.UTF_8);
        for (String line; (line = br.readLine()) != null;) {
          String[] splits = line.split("\t");
          if (splits.length != 0 && splits.length > 5) {
            String confSeriesContent = "\"" + splits[0] + "\"" +  "~" +
                              		   "\"" + removeSpecialCharacters(splits[1]) + "\"" + "~" +
                              		   "\"" + removeSpecialCharacters(splits[2]) + "\"" + "~" +
                              		   "\"" + removeSpecialCharacters(splits[3]) + "\"" + "~" +
                              		   "\"" + removeSpecialCharacters(splits[4]) + "\"" + "~" +
                              		   "\"" + removeSpecialCharacters(splits[5]) + "\"" + "~conference_series";
            outputFile.println(confSeriesContent);
          }
        }
        outputFile.flush();
        br.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    
    // This is a function which parses the Field of Study Children Table in Microsoft Academic Graph Schema   
    private void parseFOSChildrenFile(String path, PrintWriter outputFile) {
      try {
        BufferedReader br = Files.newBufferedReader(Paths.get(path), StandardCharsets.UTF_8);
        for (String line; (line = br.readLine()) != null;) {
          String[] splits = line.split("\t");
          if (splits.length != 0 && splits.length > 1) {
            String fos1 = splits[0];
            String fos2 = splits[1];
            if (fos1 != null && !fos1.equals("") && fos2 != null && !fos2.equals("")) {
              String paperFOSContent = "\"" + fos1 + "\"" + "~" +
                                	   "\"" + fos2 + "\"" + "~SUB_FIELD_OF";
              outputFile.println(paperFOSContent);
            }
          }
        }
        outputFile.flush();
        br.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    
    // This is a function which parses the Field of Study Table in Microsoft Academic Graph Schema   
    private void parseFOSFile(String path, PrintWriter outputFile) {
      try {
        BufferedReader br = Files.newBufferedReader(Paths.get(path), StandardCharsets.UTF_8);
        for (String line; (line = br.readLine()) != null;) {
          String[] splits = line.split("\t");
          if (splits.length != 0 && splits.length > 6) {
            String fosContent = "\"" + splits[0] + "\"" + "~" +
                              	"\"" + removeSpecialCharacters(splits[1]) + "\"" + "~" +
                              	"\"" + removeSpecialCharacters(splits[2]) + "\"" + "~" +
                                "\"" + removeSpecialCharacters(splits[3]) + "\"" + "~" +
                                "\"" + removeSpecialCharacters(splits[4]) + "\"" + "~" +
                              	"\"" + removeSpecialCharacters(splits[5]) + "\"" + "~" +
                                "\"" + removeSpecialCharacters(splits[6]) + "\"" +  "~field_of_study";
            outputFile.println(fosContent);
          }
        }
        outputFile.flush();
        br.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    
    // This is a function which parses the Journals Table in Microsoft Academic Graph Schema   
    private void parseJournalFile(String path, PrintWriter outputFile) {
      try {
        BufferedReader br = Files.newBufferedReader(Paths.get(path), StandardCharsets.UTF_8);
        for (String line; (line = br.readLine()) != null;) {
          String[] splits = line.split("\t");
          if (splits.length != 0 && splits.length > 5) {
            String abstractIndexContent = "\""  + splits[0] + "\""  + "~" +
                            			  "\"" + removeSpecialCharacters(splits[1]) + "\"" + "~" +
                            			  "\"" + removeSpecialCharacters(splits[2]) + "\"" + "~" +
                            			  "\"" + removeSpecialCharacters(splits[3]) + "\"" + "~" +
                            			  "\"" + removeSpecialCharacters(splits[4]) + "\"" + "~" +
                            			  "\"" + removeSpecialCharacters(splits[5]) + "\"" + "~journal";
            outputFile.println(abstractIndexContent);
          }
        }
        outputFile.flush();
        br.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    
    // This is a function which parses the Paper Abstract Inverted Index Table in Microsoft Academic Graph Schema   
    private void parseAbstractInvertedIndexFile(String path, PrintWriter abstractIndexFile, PrintWriter paperAbstractIndexFile) {
      try {
        BufferedReader br = Files.newBufferedReader(Paths.get(path), StandardCharsets.UTF_8);
        for (String line; (line = br.readLine()) != null;) {
          String[] splits = line.split("\t");
          if (splits.length != 0 && splits.length > 1) {
            String abstractString = splits[1];
            if (checkForUnicode(abstractString)) {
              abstractString = "unicode characters detected";
            }
            long abstractId = uniqueCurrentTimeMS();
            String abstractIndexContent = "\""  + abstractId + "\""  + "~" +
                              			  "\"" + removeSpecialCharacters(abstractString) + "\"" + "~abstract_index";
            abstractIndexFile.println(abstractIndexContent);
            String paperId = splits[0];
            if (paperId != null && !paperId.equals("")) {
              String paperAbsIndexContent = "\""  + abstractId + "\""  + "~" + "\""  + paperId + "\""  + "~ABSTRACT_OF";
              paperAbstractIndexFile.println(paperAbsIndexContent);
            }
          }
        }
        abstractIndexFile.flush();
        paperAbstractIndexFile.flush();
        br.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    
    // This is a function which parses the Paper Author Affiliation Table in Microsoft Academic Graph Schema   
    private void parsePaperAuthorAffiliationFile(String path, PrintWriter authorAffFile, PrintWriter affPaperFile, PrintWriter authorPaperFile) {
      try {
        List<String> fileSplits = splitFile(new File(path));
        if (fileSplits != null && !fileSplits.isEmpty()) {
          for (String splitPath : fileSplits) {
            BufferedReader br = Files.newBufferedReader(Paths.get(splitPath), StandardCharsets.UTF_8);
            Set<String> affPaperLines = new HashSet<String>();
            for (String line; (line = br.readLine()) != null;) {
              String[] splits = line.split("\t");
              if (splits.length != 0 && splits.length > 2) {
                String paperId = splits[0];
                String authorId = splits[1];
                String affliationId = splits[2];
                if (affliationId != null && !affliationId.equals("") && authorId != null && !authorId.equals("")) {
                  String authorAffString = "\"" + authorId + "\"" + "~" + "\"" + affliationId + "\"" + "~AFFILIATED_WITH";
                  authorAffFile.println(authorAffString);
                }
                if (affliationId != null && !affliationId.equals("") && paperId != null && !paperId.equals("")) {
                  String affPaperContent = "\"" + affliationId + "\"" + "~" + "\"" + paperId + "\"" + "~AUTHOR_AFFILIATION";
                  String combinedId = affliationId + ":" + paperId;
                  if (!affPaperLines.contains(combinedId)) {
                    affPaperLines.add(combinedId);
                    affPaperFile.println(affPaperContent);
                  }
                }
                if (authorId != null && !authorId.equals("") && paperId != null && !paperId.equals("")) {
                  String authorPaperContent = "\"" + authorId + "\"" + "~" + "\"" + paperId + "\"" + "~AUTHOR_OF";
                  authorPaperFile.println(authorPaperContent);
                }
              }
            }
            br.close();
          }
        }

         authorAffFile.flush();
         affPaperFile.flush();
         authorPaperFile.flush();

      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    // This is a function which parses the Paper Citation Contexts Table in Microsoft Academic Graph Schema
    private void parsePaperCitationContextFile(String path, PrintWriter outputFile) {
      try {
        BufferedReader br = Files.newBufferedReader(Paths.get(path), StandardCharsets.UTF_8);
        for (String line; (line = br.readLine()) != null;) {
          String[] splits = line.split("\t");
          if (splits.length != 0 && splits.length > 2) {
            String paperCiteContextContent = "\"" + splits[0] + "\"" + "~" +
                                             "\"" + removeSpecialCharacters(splits[2]) + "\"" + "~" +
                                             "\"" + splits[1] + "\"" + "~CITING";
            outputFile.println(paperCiteContextContent);
          }
        }
        outputFile.flush();
        br.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    
    // This is a function which parses the Paper Fields of Study Table in Microsoft Academic Graph Schema
    private void parsePaperFOSFile(String path, PrintWriter outputFile) {
      try {
        BufferedReader br = Files.newBufferedReader(Paths.get(path), StandardCharsets.UTF_8);
        for (String line; (line = br.readLine()) != null;) {
          String[] splits = line.split("\t");
          if (splits.length != 0 && splits.length > 1) {
            String paperFOSContent = "\"" + splits[0] + "\"" + "~" +
                                     "\"" +  splits[1] + "\"" + "~BELONGS_TO";
            outputFile.println(paperFOSContent);
          }
        }
        outputFile.flush();
        br.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    // This is a function which parses the Paper Languages Table in Microsoft Academic Graph Schema
    private void parsePaperLanguagesFile(String path, PrintWriter languageFile, PrintWriter languagePaperFile) {
      try {
        BufferedReader br = Files.newBufferedReader(Paths.get(path), StandardCharsets.UTF_8);
        Map<Long, String> languageContentMap = new HashMap<>();
        for (String line; (line = br.readLine()) != null;) {
          String[] splits = line.split("\t");
          if (splits.length != 0 && splits.length > 1) {
            Long langId = getId(languageIdMap, splits[1]);
            String languageString = "\"" + langId + "\"" + "~" + "\"" + removeSpecialCharacters(splits[1]) + "\"" + "~language";
            languageContentMap.put(langId, languageString);
            String paperId = splits[0];
            if (paperId != null && !paperId.equals("")) {
              String languagePaperContent = "\"" + langId + "\"" + "~" + "\"" + paperId + "\"" + "~LANGUAGE_OF";
              languagePaperFile.println(languagePaperContent);
            }
          }
        }
        for (Long langId : languageContentMap.keySet()) {
          languageFile.println(languageContentMap.get(langId));
        }
        languageFile.flush();
        languagePaperFile.flush();
        br.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    
    // This is a function which parses the Paper Recommendations Table in Microsoft Academic Graph Schema
    private void parsePaperRecommendationsFile(String absolutePath, PrintWriter languageCSV, PrintWriter paperLanguagesCSV) {
	
		
	}    
    
    // This is a function which parses the Paper References Table in Microsoft Academic Graph Schema
    private void parsePaperReferencesFile(String absolutePath, PrintWriter paperCSV, PrintWriter paperJournalCSV, PrintWriter paperConfIdCSV, PrintWriter confIdConfSeriesIdCSV) {
		
		
	}	
    
    // This is a function which parses the Paper Resources Table in Microsoft Academic Graph Schema
	private void parsePaperResourcesFile(String absolutePath, PrintWriter paperCSV, PrintWriter paperJournalCSV, PrintWriter paperConfIdCSV, PrintWriter confIdConfSeriesIdCSV) {
	
		
	}    
	
	// This is a function which parses the Paper URLs Table in Microsoft Academic Graph Schema
    private void parsePaperURLsFile(String path, PrintWriter urlFile, PrintWriter urlPaperFile) {
      try {
        BufferedReader br = Files.newBufferedReader(Paths.get(path), StandardCharsets.UTF_8);
        for (String line; (line = br.readLine()) != null;) {
          String[] splits = line.split("\t");
          if (splits.length != 0 && splits.length > 2) {
            Long urlId = uniqueCurrentTimeMS();
            String urlString = "\"" + urlId + "\"" + "~" + "\"" + removeSpecialCharacters(splits[1]) + "\"" + "~" + "\"" + removeSpecialCharacters(splits[2]) + "\"" + "~url";
            urlFile.println(urlString);
            String paperId = splits[0];
            if (paperId != null && !paperId.equals("")) {
              String urlPaperContent = "\"" + urlId + "\"" + "~" + "\"" + paperId + "\""  + "~URL_OF";
              urlPaperFile.println(urlPaperContent);
            }
          }
        }
        urlFile.flush();
        urlPaperFile.flush();
        br.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }	
    
    // This is a function which parses the Paper Table in Microsoft Academic Graph Schema
    private void parsePaperFile(String path, PrintWriter paperFile, PrintWriter paperJournalFile, PrintWriter paperConfInstanceFile, PrintWriter paperConfSeriesFile) {
      try {
        BufferedReader br = Files.newBufferedReader(Paths.get(path), StandardCharsets.UTF_8);
        for (String line; (line = br.readLine()) != null;) {
          String[] splits = line.split("\t");
          if (splits.length != 0 && splits.length > 19) {
            String paperId = splits[0];
            if (paperId != null && !paperId.equals("")) {
              String paperString = "\"" + paperId + "\"" + "~" +
                                   "\"" + splits[1] + "\"" + "~" +
                                   "\"" + removeSpecialCharacters(splits[2]) + "\"" + "~" +
                                   "\"" + removeSpecialCharacters(splits[3]) + "\"" + "~" +
                                   "\"" + removeSpecialCharacters(splits[4]) + "\"" + "~" +
                                   "\"" + removeSpecialCharacters(splits[5]) + "\"" + "~" +
                                   "\"" + removeSpecialCharacters(splits[6]) + "\"" + "~" +
                                   "\"" + removeSpecialCharacters(splits[7]) + "\"" + "~" +
                                   "\"" + removeSpecialCharacters(splits[8]) + "\"" + "~" +
                                   "\"" + removeSpecialCharacters(splits[9]) + "\"" + "~" +
                                   "\"" + removeSpecialCharacters(splits[13]) + "\"" + "~" +
                                   "\"" + removeSpecialCharacters(splits[14]) + "\"" + "~" +
                                   "\"" + removeSpecialCharacters(splits[15]) + "\"" + "~" +
                                   "\"" + removeSpecialCharacters(splits[16]) + "\"" + "~" +
                                   "\"" + removeSpecialCharacters(splits[17]) + "\"" + "~" +
                                   "\"" + removeSpecialCharacters(splits[18]) + "\"" + "~" +
                                   "\"" + removeSpecialCharacters(splits[19]) + "\"" + "~paper";
              paperFile.println(paperString);
            }

            String journalId = splits[10];
            if (journalId != null && !journalId.equals("") && paperId != null && !paperId.equals("")) {
              String paperJournalContent = "\"" + paperId + "\"" + "~" + "\"" + journalId + "\"" + "~PUBLISHED_IN";
              paperJournalFile.println(paperJournalContent);
            }
            String confInstanceId = splits[12];
            if (confInstanceId != null && !confInstanceId.equals("") && paperId != null && !paperId.equals("")) {
              String paperConfInstanceContent = "\"" + paperId + "\"" + "~" + "\"" + confInstanceId + "\"" + "~PRESENTED_AT";
              paperConfInstanceFile.println(paperConfInstanceContent);
            }
            String confSeriesId = splits[11];
            if (confInstanceId != null && !confInstanceId.equals("") && confSeriesId != null && !confSeriesId.equals("")) {
              String paperConfSeriesContent = "\"" + confInstanceId + "\"" + "~" + "\"" + confSeriesId + "\"" + "~INSTANCE_OF";
              paperConfSeriesFile.println(paperConfSeriesContent);
            }
          }
        }
        paperFile.flush();
        paperJournalFile.flush();        
        paperConfInstanceFile.flush();
        paperConfSeriesFile.flush();
        br.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    
    // This is a function which parses the Related Field of Study Table in Microsoft Academic Graph Schema
	private void parseRelatedFieldOfStudy(String absolutePath, PrintWriter paperCSV, PrintWriter paperJournalCSV, PrintWriter paperConfIdCSV, PrintWriter confIdConfSeriesIdCSV) {
		
		
	}
	
	// This is a function which splits the big file into multiple smaller chunks 
    private static List<String> splitFile(File f) throws IOException {
      List<String> fileList = new ArrayList<>();
      int partCounter = 1;//I like to name parts from 001, 002, 003, ...
      //you can change it to 0 if you want 000, 001, ...

      int sizeOfFiles = 1024 * 1024* 1000;// 1GB
      byte[] buffer = new byte[sizeOfFiles];

      String fileName = f.getName();

      //try-with-resources to ensure closing stream
      try (FileInputStream fis = new FileInputStream(f);
        BufferedInputStream bis = new BufferedInputStream(fis)) {

        int bytesAmount = 0;
        while ((bytesAmount = bis.read(buffer)) > 0) {
          //write each chunk of data into separate file with different number in name
          String filePartName = String.format("%s.%03d", fileName, partCounter++);
          File newFile = new File(f.getParent(), filePartName);
          try (FileOutputStream out = new FileOutputStream(newFile)) {
            out.write(buffer, 0, bytesAmount);
          }
          fileList.add(newFile.getAbsolutePath());
        }
      }
      return fileList;
    }

    private Long getId(Map<String, Long> idMap, String key) {
      if (idMap.containsKey(key)) {
        return idMap.get(key);
      } else {
        long gId = uniqueCurrentTimeMS();
        idMap.put(key, gId);
        return gId;
      }
    }

    private long getUniqueId() {
      return counter.incrementAndGet();
    }

    private String removeSpecialCharacters (String inputString) {
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


    private boolean checkForUnicode (String inputString){
      Pattern p = Pattern.compile("\\\\u(\\p{XDigit}{4})");
      Matcher m = p.matcher(inputString);
      if (m.find()) {
        return true;
      } else {
        return false;
      }
    }
  }
