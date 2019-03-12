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
    private String affiliationsCSVName = null;
    private String authorsCSVName = null;
    private String conferenceInstancesCSVName = null;
    private String conferenceSeriesCSVName = null;
    private String fieldsOfStudyChildrenCSVName = null;
    private String fieldsOfStudyCSVName = null;
    private String journalsCSVName = null;
    private String paperAbstractInvertedIndexCSVName = null;
    private String paperAuthorAffiliationsCSVName = null;
    private String paperCitationContextsCSVName = null;
    private String paperFieldsOfStudyCSVName = null;
    private String paperLanguagesCSVName = null;
    private String paperRecommendationsCSVName = null;
    private String paperReferencesCSVName = null;
    private String paperResourcesCSVName = null;
    private String paperURLsCSVName = null;
    private String papersCSVName = null;
    private String relatedFieldOfStudyCSVName = null;
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
              affiliationsCSVName = csvPrefix + "_affiliations.csv";
              authorsCSVName = csvPrefix + "_authors.csv";
              conferenceInstancesCSVName = csvPrefix + "_conferenceInstances.csv";
              conferenceSeriesCSVName = csvPrefix + "_conferenceSeries.csv";
              fieldsOfStudyChildrenCSVName = csvPrefix + "_fieldsOfStudyChildren.csv";
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
       
        PrintWriter affiliationsCSV = new PrintWriter(new FileWriter(targetDir + File.separator + affiliationsCSVName, true));
        PrintWriter authorsCSV = new PrintWriter(new FileWriter(targetDir + File.separator + authorsCSVName, true));
        PrintWriter conferenceInstancesCSV = new PrintWriter(new FileWriter(targetDir + File.separator + conferenceInstancesCSVName, true));
        PrintWriter conferenceSeriesCSV = new PrintWriter(new FileWriter(targetDir + File.separator + conferenceSeriesCSVName, true));
        PrintWriter fieldsOfStudyChildrenCSV = new PrintWriter(new FileWriter(targetDir + File.separator +  fieldsOfStudyChildrenCSVName, true));
        PrintWriter fieldsOfStudyCSV = new PrintWriter(new FileWriter(targetDir + File.separator + fieldsOfStudyCSVName, true));
        PrintWriter journalsCSV = new PrintWriter(new FileWriter(targetDir + File.separator + journalsCSVName, true));
        PrintWriter paperAuthorAffiliationsCSV = new PrintWriter(new FileWriter(targetDir + File.separator + paperAuthorAffiliationsCSVName, true));
        PrintWriter paperAbstractInvertedIndexCSV = new PrintWriter(new FileWriter(targetDir + File.separator + paperAbstractInvertedIndexCSVName, true));
        PrintWriter paperCitationContextsCSV = new PrintWriter(new FileWriter(targetDir + File.separator + paperCitationContextsCSVName, true));
        PrintWriter paperFieldsOfStudyCSV = new PrintWriter(new FileWriter(targetDir + File.separator + paperFieldsOfStudyCSVName, true));    
        PrintWriter paperLanguagesCSV = new PrintWriter(new FileWriter(targetDir + File.separator + paperLanguagesCSVName, true));	
        PrintWriter paperRecommendationsCSV = new PrintWriter(new FileWriter(targetDir + File.separator + paperRecommendationsCSVName, true));
        PrintWriter paperReferencesCSV = new PrintWriter(new FileWriter(targetDir + File.separator + paperReferencesCSVName, true));
        PrintWriter paperResourcesCSV = new PrintWriter(new FileWriter(targetDir + File.separator + paperResourcesCSVName, true));
        PrintWriter paperURLsCSV = new PrintWriter(new FileWriter(targetDir + File.separator + paperURLsCSVName, true));
        PrintWriter papersCSV = new PrintWriter(new FileWriter(targetDir + File.separator + papersCSVName, true));
        PrintWriter relatedFieldOfStudyCSV = new PrintWriter(new FileWriter(targetDir + File.separator + relatedFieldOfStudyCSVName, true));
        
        
        affiliationsCSV.println("affiliation_id~rank~normalized_name~display_name~grid_id~official_page~wiki_page~paper_count~citation_count~created_date");
        authorsCSV.println("author_id~rank~normalized_name~display_name~last_known_affiliation_id~paper_count~citation_count~created_date");
        conferenceInstancesCSV.println("conference_instance_id~normalized_name~display_name~conference_series_id~location~official_url~start_date~end_date~abstract_registration_date~submission_deadline_date~notification_due_date~final_version_due_date~paper_count~citation_count~created_date");
        conferenceSeriesCSV.println("conference_series_id~rank~normalized_name~display_name~paper_count~citation_count~created_date");
        fieldsOfStudyChildrenCSV.println("field_of_study_id~child_field_of_study_id");
        fieldsOfStudyCSV.println("field_of_study_id~rank~normalized_name~display_name~main_type~level~paper_count~citation_count~created_date");
        journalsCSV.println("journal_id~rank~normalized_name~display_name~lssn~publisher~web_page~paper_count~citation_count~created_date");
        paperAbstractInvertedIndexCSV.println("paper_id~indexed_abstract");
        paperAuthorAffiliationsCSV.println("paper_id~author_id~affiliation_id~author_sequence_number~original_affiliation");
        paperCitationContextsCSV.println("paper_id~paper_reference_id~citation_context");
        paperFieldsOfStudyCSV.println("paper_id~field_of_study_id~score");
        paperLanguagesCSV.println("paper_id~language_code");
        paperRecommendationsCSV.println("paper_id~recommended_paper_id~score");
        paperReferencesCSV.println("paper_id~paper_reference_id");
        paperResourcesCSV.println(":START_ID~:END_ID~:TYPE");
        paperURLsCSV.println(":START_ID~:END_ID~:TYPE");
        papersCSV.println("paper_id~rank~doi~doc_type~paper_title~original_title~book_title~year~date~publisher~journal_id~conference_series_id~conference_instance_id~volume~issue~first_page~last_page~reference_count~citation_count~estimated_citation_count~original_venue~created_date");
        relatedFieldOfStudyCSV.println(":START_ID~:END_ID~:TYPE");

        if (sourceFiles != null && sourceFiles.length != 0) {
          for (File sourceFile : sourceFiles) {
            Constants.sourceFileNames sourceFileName = Constants.sourceFileNamesMap.get(sourceFile.getName());
            switch (sourceFileName) {
              case AFFILIATIONS:
                   parseAffiliationsFile(sourceFile.getAbsolutePath(), affiliationsCSV);
                   break;
              case AUTHORS:
                   parseAuthorsFile(sourceFile.getAbsolutePath(), authorsCSV);
                   break;
              case CONFERENCE_INSTANCES:
                   parseConferenceInstancesFile(sourceFile.getAbsolutePath(), conferenceInstancesCSV);
                   break;
              case CONFERENCE_SERIES:
                   parseConferenceSeriesFile(sourceFile.getAbsolutePath(), conferenceSeriesCSV);
                   break;     
              case FIELD_OF_STUDY_CHILDREN:
                   parseFieldsOfStudyChildrenFile(sourceFile.getAbsolutePath(), fieldsOfStudyChildrenCSV);
                   break;
              case FIELD_OF_STUDY:
                   parseFieldsOfStudyFile(sourceFile.getAbsolutePath(), fieldsOfStudyCSV);
                   break;     
              case JOURNALS:
                   parseJournalFile(sourceFile.getAbsolutePath(), journalsCSV);
                   break;        
              case PAPER_ABSTRACT_INVERTED_INDEX:
                   parseAbstractInvertedIndexFile(sourceFile.getAbsolutePath(), paperAbstractInvertedIndexCSV);
                   break;
              case PAPER_AUTHOR_AFFILIATIONS:
                   parsePaperAuthorAffiliationsFile(sourceFile.getAbsolutePath(), paperAuthorAffiliationsCSV);
                   break;
              case PAPER_CITATION_CONTEXTS:
                   parsePaperCitationContextsFile(sourceFile.getAbsolutePath(), paperCitationContextsCSV);
                   break;
              case PAPER_FIELD_OF_STUDY:
                   parsePaperFieldsOfStudyFile(sourceFile.getAbsolutePath(), paperFieldsOfStudyCSV);
                   break;                  
              case PAPER_LANGUAGES:
                   parsePaperLanguagesFile(sourceFile.getAbsolutePath(), paperLanguagesCSV);
                   break;
              case PAPER_RECOMMENDATIONS:
                   parsePaperRecommendationsFile(sourceFile.getAbsolutePath(), paperRecommendationsCSV);
                   break;
              case PAPER_REFERENCES:
                   parsePaperReferencesFile(sourceFile.getAbsolutePath(), paperReferencesCSV);
                   break;   
              case PAPER_RESOURCES:
                   parsePaperResourcesFile(sourceFile.getAbsolutePath(), paperResourcesCSV);
                   break;
              case PAPER_URLS:
                   parsePaperURLsFile(sourceFile.getAbsolutePath(), paperURLsCSV);
                   break;
              case PAPERS:
                   parsePapersFile(sourceFile.getAbsolutePath(), papersCSV);
                   break;            
              case RELATED_FIELD_OF_STUDY:
                   parseRelatedFieldOfStudy(sourceFile.getAbsolutePath(), relatedFieldOfStudyCSV);
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
    private void parseAffiliationsFile(String path, PrintWriter outputFile) {
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
    private void parseConferenceInstancesFile(String path, PrintWriter outputFile) {
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
    private void parseConferenceSeriesFile(String path, PrintWriter outputFile) {
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
    private void parseFieldsOfStudyChildrenFile(String path, PrintWriter outputFile) {
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
    private void parseFieldsOfStudyFile(String path, PrintWriter outputFile) {
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
    private void parseAbstractInvertedIndexFile(String path, PrintWriter paperAbstractIndexFile) {
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
         //   abstractIndexFile.println(abstractIndexContent);
            String paperId = splits[0];
            if (paperId != null && !paperId.equals("")) {
              String paperAbsIndexContent = "\""  + abstractId + "\""  + "~" + "\""  + paperId + "\""  + "~ABSTRACT_OF";
              paperAbstractIndexFile.println(paperAbsIndexContent);
            }
          }
        }
     //   abstractIndexFile.flush();
        paperAbstractIndexFile.flush();
        br.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    
    // This is a function which parses the Paper Author Affiliations Table in Microsoft Academic Graph Schema   
    private void parsePaperAuthorAffiliationsFile(String path, PrintWriter authorAffiliationFile) {
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
                  authorAffiliationFile.println(authorAffString);
                }
                if (affliationId != null && !affliationId.equals("") && paperId != null && !paperId.equals("")) {
                  String affPaperContent = "\"" + affliationId + "\"" + "~" + "\"" + paperId + "\"" + "~AUTHOR_AFFILIATION";
                  String combinedId = affliationId + ":" + paperId;
                  if (!affPaperLines.contains(combinedId)) {
                    affPaperLines.add(combinedId);
                    authorAffiliationFile.println(affPaperContent);
                  }
                }
                if (authorId != null && !authorId.equals("") && paperId != null && !paperId.equals("")) {
                  String authorPaperContent = "\"" + authorId + "\"" + "~" + "\"" + paperId + "\"" + "~AUTHOR_OF";
          //        authorPaperFile.println(authorPaperContent);
                }
              }
            }
            br.close();
          }
        }

        authorAffiliationFile.flush();

      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    // This is a function which parses the Paper Citation Contexts Table in Microsoft Academic Graph Schema
    private void parsePaperCitationContextsFile(String path, PrintWriter outputFile) {
      try {
        BufferedReader br = Files.newBufferedReader(Paths.get(path), StandardCharsets.UTF_8);
        for (String line; (line = br.readLine()) != null;) {
          String[] splits = line.split("\t");
          if (splits.length != 0 && splits.length > 2) {
            String paperCiteContextsContent = "\"" + splits[0] + "\"" + "~" +
                                             "\"" + removeSpecialCharacters(splits[2]) + "\"" + "~" +
                                             "\"" + splits[1] + "\"" + "~CITING";
            outputFile.println(paperCiteContextsContent);
          }
        }
        outputFile.flush();
        br.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    
    // This is a function which parses the Paper Fields of Study Table in Microsoft Academic Graph Schema
    private void parsePaperFieldsOfStudyFile(String path, PrintWriter outputFile) {
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
    private void parsePaperLanguagesFile(String path, PrintWriter paperLanguagesCSV) {
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
           //   languagePaperFile.println(languagePaperContent);
            }
          }
        }
        for (Long langId : languageContentMap.keySet()) {
        	paperLanguagesCSV.println(languageContentMap.get(langId));
        }
        paperLanguagesCSV.flush();
        br.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    
    // This is a function which parses the Paper Recommendations Table in Microsoft Academic Graph Schema
    private void parsePaperRecommendationsFile(String absolutePath, PrintWriter paperRecommendationsCSV) {
	
		
	}    
    
    // This is a function which parses the Paper References Table in Microsoft Academic Graph Schema
    private void parsePaperReferencesFile(String absolutePath, PrintWriter paperReferencesCSV) {
		
		
	}	
    
    // This is a function which parses the Paper Resources Table in Microsoft Academic Graph Schema
	private void parsePaperResourcesFile(String absolutePath, PrintWriter paperResourcesCSV) {
	
		
	}    
	
	// This is a function which parses the Paper URLs Table in Microsoft Academic Graph Schema
    private void parsePaperURLsFile(String path, PrintWriter PaperURLsCSV) {
      try {
        BufferedReader br = Files.newBufferedReader(Paths.get(path), StandardCharsets.UTF_8);
        for (String line; (line = br.readLine()) != null;) {
          String[] splits = line.split("\t");
          if (splits.length != 0 && splits.length > 2) {
            Long urlId = uniqueCurrentTimeMS();
            String urlString = "\"" + urlId + "\"" + "~" + "\"" + removeSpecialCharacters(splits[1]) + "\"" + "~" + "\"" + removeSpecialCharacters(splits[2]) + "\"" + "~url";
       //     urlFile.println(urlString);
            String paperId = splits[0];
            if (paperId != null && !paperId.equals("")) {
              String urlPaperContent = "\"" + urlId + "\"" + "~" + "\"" + paperId + "\""  + "~URL_OF";
          //    urlPaperFile.println(urlPaperContent);
            }
          }
        }
        PaperURLsCSV.flush();
        br.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }	
    
    // This is a function which parses the Paper Table in Microsoft Academic Graph Schema
    private void parsePapersFile(String path, PrintWriter paperFile) {
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
          //    paperJournalFile.println(paperJournalContent);
            }
            String confInstanceId = splits[12];
            if (confInstanceId != null && !confInstanceId.equals("") && paperId != null && !paperId.equals("")) {
              String paperConfInstanceContent = "\"" + paperId + "\"" + "~" + "\"" + confInstanceId + "\"" + "~PRESENTED_AT";
        //      paperConfInstanceFile.println(paperConfInstanceContent);
            }
            String confSeriesId = splits[11];
            if (confInstanceId != null && !confInstanceId.equals("") && confSeriesId != null && !confSeriesId.equals("")) {
              String paperConfSeriesContent = "\"" + confInstanceId + "\"" + "~" + "\"" + confSeriesId + "\"" + "~INSTANCE_OF";
        //      paperConfSeriesFile.println(paperConfSeriesContent);
            }
          }
        }
        paperFile.flush();
        br.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    
    // This is a function which parses the Related Field of Study Table in Microsoft Academic Graph Schema
	private void parseRelatedFieldOfStudy(String absolutePath, PrintWriter paperCSV) {
		
		
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

    // This is a function to check for unicode strings
    private boolean checkForUnicode (String inputString) {
      Pattern p = Pattern.compile("\\\\u(\\p{XDigit}{4})");
      Matcher m = p.matcher(inputString);
      if (m.find()) {
        return true;
      } else {
        return false;
      }
    }
  }
