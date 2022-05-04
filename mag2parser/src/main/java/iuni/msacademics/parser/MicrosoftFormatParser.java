package iuni.msacademics.parser;

import iuni.msacademics.parser.utls.Constants;

import org.slf4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

final public class MicrosoftFormatParser extends MAGFormatParser {

    public MicrosoftFormatParser(String sourceDir, String targetDir,
       String csvPrefix) {
       super(sourceDir, targetDir, csvPrefix);
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
        PrintWriter fieldOfStudyChildrenCSV = new PrintWriter(new FileWriter(targetDir + File.separator +  fieldOfStudyChildrenCSVName, true));
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
        fieldOfStudyChildrenCSV.println("field_of_study_id~child_field_of_study_id");
        fieldsOfStudyCSV.println("field_of_study_id~rank~normalized_name~display_name~main_type~level~paper_count~citation_count~created_date");
        journalsCSV.println("journal_id~rank~normalized_name~display_name~lssn~publisher~web_page~paper_count~citation_count~created_date");
        paperAbstractInvertedIndexCSV.println("paper_id~indexed_abstract");
        paperAuthorAffiliationsCSV.println("paper_id~author_id~affiliation_id~author_sequence_number~original_affiliation");
        paperCitationContextsCSV.println("paper_id~paper_reference_id~citation_context");
        paperFieldsOfStudyCSV.println("paper_id~field_of_study_id~score");
        paperLanguagesCSV.println("paper_id~language_code");
        paperRecommendationsCSV.println("paper_id~recommended_paper_id~score");
        paperReferencesCSV.println("paper_id~paper_reference_id");
        paperResourcesCSV.println("paper_id~resource_type~resource_url~source_url~relationship_type");
        paperURLsCSV.println("paper_id~source_type~source_url");
        papersCSV.println("paper_id~rank~doi~doc_type~paper_title~original_title~book_title~year~date~publisher~journal_id~conference_series_id~conference_instance_id~volume~issue~first_page~last_page~reference_count~citation_count~estimated_citation~original_venue~created_date");
        relatedFieldOfStudyCSV.println("field_of_study_id1~display_name1~type1~field_of_study_id2~display_name2~type2~rank");

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
                   parseFieldOfStudyChildrenFile(sourceFile.getAbsolutePath(), fieldOfStudyChildrenCSV);
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
                   System.out.println("The absolute path of the source file is: " + sourceFile.getAbsolutePath());
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
    private void parseAffiliationsFile(String path, PrintWriter affiliationsCSV) {
      try {
        BufferedReader br = Files.newBufferedReader(Paths.get(path), StandardCharsets.UTF_8);
        for (String line; (line = br.readLine()) != null;) {
          String[] splits = line.split("\t");
          if (splits.length != 0 && splits.length > 9) {
            String affiliationsContent = "\"" + splits[0] + "\""  + "~" +
                            		 "\"" + splits[1] + "\""  + "~" +
                            		 "\"" + removeSpecialCharacters(splits[2]) + "\"" + "~" +
                            		 "\"" + removeSpecialCharacters(splits[3]) + "\"" + "~" +
                            		 "\"" + removeSpecialCharacters(splits[4]) + "\"" + "~" +
                            		 "\"" + removeSpecialCharacters(splits[5]) + "\"" + "~" +
            				 "\"" + removeSpecialCharacters(splits[6]) + "\"" + "~" +
            				 "\"" + splits[7] + "\"" + "~" +
            				 "\"" + splits[8] + "\"" + "~" +
            				 "\"" + splits[9] + "\"";
            affiliationsCSV.println(affiliationsContent);
          }
        }
        affiliationsCSV.flush();
        br.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    
    // This is a function which parses the Authors Table in Microsoft Academic Graph Schema
    private void parseAuthorsFile(String path, PrintWriter authorsCSV) {
      try {
        BufferedReader br = Files.newBufferedReader(Paths.get(path), StandardCharsets.UTF_8);
        for (String line; (line = br.readLine()) != null;) {
          String[] splits = line.split("\t");
          if (splits.length != 0 && splits.length > 7) {
            String authorContent = "\"" + splits[0] + "\""  + "~" +
                              	   "\"" + splits[1] + "\""  + "~" +
                              	   "\"" + removeSpecialCharacters(splits[2]) + "\"" + "~" +
                              	   "\"" + removeSpecialCharacters(splits[3]) + "\"" + "~" +
                              	   "\"" + splits[4] + "\""  + "~" +
                              	   "\"" + splits[5] + "\""  + "~" +
                              	   "\"" + splits[6] + "\""  + "~" +
                              	   "\"" + splits[7] + "\"";
            authorsCSV.println(authorContent);
          }
        }
        authorsCSV.flush();
        br.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    
    // This is a function which parses the Conference Instances Table in Microsoft Academic Graph Schema
    private void parseConferenceInstancesFile(String path, PrintWriter conferenceInstancesCSV) {
      try {
        BufferedReader br = Files.newBufferedReader(Paths.get(path), StandardCharsets.UTF_8);
        for (String line; (line = br.readLine()) != null;) {
          String[] splits = line.split("\t");
          if (splits.length != 0 && splits.length > 14) {
            String conferenceInstanceContent = "\"" + splits[0] + "\"" + "~" +
            				       "\"" + removeSpecialCharacters(splits[1]) + "\"" + "~" +
            				       "\"" + removeSpecialCharacters(splits[2]) + "\"" + "~" +
                            		       "\"" + splits[3] + "\"" + "~" +
                            		       "\"" + removeSpecialCharacters(splits[4]) + "\"" + "~" +
                            		       "\"" + removeSpecialCharacters(splits[5]) + "\"" + "~" +
                            		       "\"" + splits[6] + "\"" + "~" +
                            		       "\"" + splits[7] + "\"" + "~" +
                            		       "\"" + splits[8] + "\"" + "~" +
                            		       "\"" + splits[9] + "\"" + "~" +
                            		       "\"" + splits[10] + "\"" + "~" +
                            		       "\"" + splits[11] + "\"" + "~" +
                            		       "\"" + splits[12] + "\"" + "~" +
                            		       "\"" + splits[13] + "\"" + "~" +
                            		       "\"" + splits[14] + "\"";
            String conferenceSeriesId = splits[4];
            // I am checking for the foreign key constraint here	
            if (conferenceSeriesId != null && !conferenceSeriesId.equals("")) {
              conferenceInstancesCSV.println(conferenceInstanceContent);
            }
          }
        }
        conferenceInstancesCSV.flush();
        br.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    
    // This is a function which parses the Conference Series Table in Microsoft Academic Graph Schema   
    private void parseConferenceSeriesFile(String path, PrintWriter conferenceSeriesCSV) {
      try {
        BufferedReader br = Files.newBufferedReader(Paths.get(path), StandardCharsets.UTF_8);
        for (String line; (line = br.readLine()) != null;) {
          String[] splits = line.split("\t");
          if (splits.length != 0 && splits.length > 6) {
            String conferenceSeriesContent = "\"" + splits[0] + "\"" + "~" +
                              		     "\"" + splits[1] + "\"" + "~" +
                              		     "\"" + removeSpecialCharacters(splits[2]) + "\"" + "~" +
                              		     "\"" + removeSpecialCharacters(splits[3]) + "\"" + "~" +
                              		     "\"" + splits[4] + "\"" + "~" +
                              		     "\"" + splits[5] + "\"" + "~" +
                              		     "\"" + splits[6] + "\"";
            conferenceSeriesCSV.println(conferenceSeriesContent);
          }
        }
        conferenceSeriesCSV.flush();
        br.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    
    // This is a function which parses the Field of Study Children Table in Microsoft Academic Graph Schema   
    private void parseFieldOfStudyChildrenFile(String path, PrintWriter fieldsOfStudyChildrenCSV) {
      try {
        BufferedReader br = Files.newBufferedReader(Paths.get(path), StandardCharsets.UTF_8);
        for (String line; (line = br.readLine()) != null;) {
          String[] splits = line.split("\t");
          if (splits.length != 0 && splits.length > 1) {
            String fos1 = splits[0];
            String fos2 = splits[1];
	    // I am checking for the foreign key constraint here	
            if (fos1 != null && !fos1.equals("") && fos2 != null && !fos2.equals("")) {
              String fieldOfStudyChildrenContent = "\"" + fos1 + "\"" + "~" +
                                	   	   "\"" + fos2 + "\"";
              fieldsOfStudyChildrenCSV.println(fieldOfStudyChildrenContent);
            }
          }
        }
        fieldsOfStudyChildrenCSV.flush();
        br.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    
    // This is a function which parses the Field of Study Table in Microsoft Academic Graph Schema   
    private void parseFieldsOfStudyFile(String path, PrintWriter fieldsOfStudyCSV) {
      try {
        BufferedReader br = Files.newBufferedReader(Paths.get(path), StandardCharsets.UTF_8);
        for (String line; (line = br.readLine()) != null;) {
          String[] splits = line.split("\t");
          if (splits.length != 0 && splits.length > 8) {
            String fosContent = "\"" + splits[0] + "\"" + "~" +
                              	"\"" + splits[1] + "\"" + "~" +
                              	"\"" + removeSpecialCharacters(splits[2]) + "\"" + "~" +
                                "\"" + removeSpecialCharacters(splits[3]) + "\"" + "~" +
                                "\"" + removeSpecialCharacters(splits[4]) + "\"" + "~" +
                              	"\"" + splits[5] + "\"" + "~" +
                                "\"" + splits[6] + "\"" + "~" +
                                "\"" + splits[7] + "\"" + "~" +
                                "\"" + splits[8] + "\"";
            fieldsOfStudyCSV.println(fosContent);
          }
        }
        fieldsOfStudyCSV.flush();
        br.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    
    // This is a function which parses the Journals Table in Microsoft Academic Graph Schema   
    private void parseJournalFile(String path, PrintWriter journalsCSV) {
      try {
        BufferedReader br = Files.newBufferedReader(Paths.get(path), StandardCharsets.UTF_8);
        for (String line; (line = br.readLine()) != null;) {
          String[] splits = line.split("\t");
          if (splits.length != 0 && splits.length > 9) {
            String journalContent = "\"" + splits[0] + "\"" + "~" +
                            	    "\"" + splits[1] + "\"" + "~" +
                            	    "\"" + removeSpecialCharacters(splits[2]) + "\"" + "~" +
                            	    "\"" + removeSpecialCharacters(splits[3]) + "\"" + "~" +
                            	    "\"" + removeSpecialCharacters(splits[4]) + "\"" + "~" +
                            	    "\"" + removeSpecialCharacters(splits[5]) + "\"" + "~" +
                            	    "\"" + removeSpecialCharacters(splits[6]) + "\"" + "~" +
                            	    "\"" + splits[7] + "\"" + "~" +
                            	    "\"" + splits[8] + "\"" + "~" +
                                    "\"" + splits[9] + "\"";
            journalsCSV.println(journalContent);
          }
        }
        journalsCSV.flush();
        br.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    
    // This is a function which parses the Paper Abstract Inverted Index Table in Microsoft Academic Graph Schema   
    private void parseAbstractInvertedIndexFile(String path, PrintWriter paperAbstractInvertedIndexCSV) {
      try {
        BufferedReader br = Files.newBufferedReader(Paths.get(path), StandardCharsets.UTF_8);
        for (String line; (line = br.readLine()) != null;) {
          String[] splits = line.split("\t");
          if (splits.length != 0 && splits.length > 1) {      
            String paperId = splits[0];
            String indexedAbstractString = removeSpecialCharacters(splits[1]);            

            String abstractInvertedIndexFileContent = "\"" + paperId + "\"" + "~" +
			   		      "\"" + indexedAbstractString + "\"";
	    // I am checking for the foreign key constraint here	
            if (paperId != null && !paperId.equals("")) {
              paperAbstractInvertedIndexCSV.println(abstractInvertedIndexFileContent);
            } 
          }
        }
        paperAbstractInvertedIndexCSV.flush();
        br.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    
    // This is a function which parses the Paper Author Affiliations Table in Microsoft Academic Graph Schema   
    private void parsePaperAuthorAffiliationsFile(String path, PrintWriter paperAuthorAffiliationsCSV) {
        try {
            System.out.println("The path variable is: " + path);
            BufferedReader br = Files.newBufferedReader(Paths.get(path), StandardCharsets.UTF_8);
            int lineCount = 0;
            for (String line; (line = br.readLine()) != null; ) {
                lineCount = lineCount + 1;
                String[] splits = line.split("\t");

                if (splits.length > 4) {
                    String paperAuthorAffiliationsContent = "\"" + splits[0] + "\"" + "~" +
                            "\"" + splits[1] + "\"" + "~" +
                            "\"" + splits[2] + "\"" + "~" +
                            "\"" + splits[3] + "\"" + "~" +
                            "\"" + removeSpecialCharacters(splits[4]) + "\"";
                    // String paperId = splits[0];
                    // String authorId = splits[1];
                    paperAuthorAffiliationsCSV.println(paperAuthorAffiliationsContent);
                } else if (splits.length > 3) {
                    String paperAuthorAffiliationsContent = "\"" + splits[0] + "\"" + "~" +
                            "\"" + splits[1] + "\"" + "~" +
                            "\"" + splits[2] + "\"" + "~" +
                            "\"" + splits[3] + "\"" + "~" +
                            "\"" +"\"";
                    // String paperId = splits[0];
                    // String authorId = splits[1];
                    paperAuthorAffiliationsCSV.println(paperAuthorAffiliationsContent);
                } else if (splits.length > 2) {
                    String paperAuthorAffiliationsContent = "\"" + splits[0] + "\"" + "~" +
                            "\"" + splits[1] + "\"" + "~" +
                            "\"" + splits[2] + "\"" + "~" +
                            "\"" + "\"" + "~" +
                            "\""  + "\"";
                    // String paperId = splits[0];
                    // String authorId = splits[1];
                    paperAuthorAffiliationsCSV.println(paperAuthorAffiliationsContent);
                } else if (splits.length > 1) {
                    String paperAuthorAffiliationsContent = "\"" + splits[0] + "\"" + "~" +
                            "\"" +  "\"" + "~" +
                            "\"" + "\"" + "~" +
                            "\"" +  "\"" + "~" +
                            "\"" + "\"";
                    // String paperId = splits[0]
                    // String authorId = splits[1];
                    paperAuthorAffiliationsCSV.println(paperAuthorAffiliationsContent);
                } else {
                    logger.info("Empty line..");
//                    String paperAuthorAffiliationsContent = "\"" +  "\"" + "~" +
//                            "\"" + "\"" + "~" +
//                            "\"" + "\"" + "~" +
//                            "\"" + "\"" + "~" +
//                            "\"" + "\"";
//                    // String paperId = splits[0];
//                    // String authorId = splits[1];
//                    paperAuthorAffiliationsCSV.println(paperAuthorAffiliationsContent);
                }


                // I am checking for the foreign key constraint here
              /*if (paperId != null && !paperId.equals("") && authorId != null && !authorId.equals("")) {
                paperAuthorAffiliationsCSV.println(paperAuthorAffiliationsContent);
              } 
	      */
            }
            System.out.println("Line Count of Paper Author Affiliations is: " + lineCount);
            paperAuthorAffiliationsCSV.flush();
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // This is a function which parses the Paper Citation Contexts Table in Microsoft Academic Graph Schema
    private void parsePaperCitationContextsFile(String path, PrintWriter paperCitationContextsCSV) {
      try {
        BufferedReader br = Files.newBufferedReader(Paths.get(path), StandardCharsets.UTF_8);
        for (String line; (line = br.readLine()) != null;) {
          String[] splits = line.split("\t");
          if (splits.length != 0 && splits.length > 2) {
            String paperCiteContextsContent = "\"" + splits[0] + "\"" + "~" +
                                              "\"" + splits[1] + "\"" + "~" +
                                              "\"" + removeSpecialCharacters(splits[2]) + "\"";
            String paperId = splits[0];
            String paperReferenceId = splits[1];
	    // I am checking for the foreign key constraint here	
            if (paperId != null && !paperId.equals("") && paperReferenceId != null && !paperReferenceId.equals("")) {
              paperCitationContextsCSV.println(paperCiteContextsContent);
            } 
          }
        }
        paperCitationContextsCSV.flush();
        br.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    
    // This is a function which parses the Paper Fields of Study Table in Microsoft Academic Graph Schema
    private void parsePaperFieldsOfStudyFile(String path, PrintWriter paperFieldsOfStudyCSV) {
      try {
        BufferedReader br = Files.newBufferedReader(Paths.get(path), StandardCharsets.UTF_8);
        for (String line; (line = br.readLine()) != null;) {
          String[] splits = line.split("\t");
          if (splits.length != 0 && splits.length > 2) {
            String paperFOSContent = "\"" + splits[0] + "\"" + "~" +
                                     "\"" + splits[1] + "\"" + "~" + 
                                     "\"" + splits[2] + "\"";
            String paperId = splits[0];
            String fieldsOfStudyId = splits[1];
	    // I am checking for the foreign key constraint here	
            if (paperId != null && !paperId.equals("") && fieldsOfStudyId != null && !fieldsOfStudyId.equals("")) {
              paperFieldsOfStudyCSV.println(paperFOSContent);
            } 
          }
        }
        paperFieldsOfStudyCSV.flush();
        br.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    // This is a function which parses the Paper Languages Table in Microsoft Academic Graph Schema
    private void parsePaperLanguagesFile(String path, PrintWriter paperLanguagesCSV) {
      try {
        BufferedReader br = Files.newBufferedReader(Paths.get(path), StandardCharsets.UTF_8);
        for (String line; (line = br.readLine()) != null;) {
          String[] splits = line.split("\t");
          if (splits.length != 0 && splits.length > 1) {
            String paperLanguagesContent = "\"" + splits[0] + "\"" + "~" + 
                                    	   "\"" + removeSpecialCharacters(splits[1]) + "\"";
            String paperId = splits[0];
	    // I am checking for the foreign key constraint here	
            if (paperId != null && !paperId.equals("")) {
              paperLanguagesCSV.println(paperLanguagesContent);
            } 
          }
        }
        paperLanguagesCSV.flush();
        br.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    
    // This is a function which parses the Paper Recommendations Table in Microsoft Academic Graph Schema
    private void parsePaperRecommendationsFile(String path, PrintWriter paperRecommendationsCSV) {      
      try {
        BufferedReader br = Files.newBufferedReader(Paths.get(path), StandardCharsets.UTF_8);
        for (String line; (line = br.readLine()) != null;) {
          String[] splits = line.split("\t");
          if (splits.length != 0 && splits.length > 2) {
            String paperRecommendationsContent = "\"" + splits[0] + "\"" + "~" +
                            			 "\"" + splits[1] + "\"" + "~" +
                            			 "\"" + splits[2] + "\"";
            String paperId = splits[0];
            String recommendedPaperId = splits[1];
	    // I am checking for the foreign key constraint here	
            if (paperId != null && !paperId.equals("") && recommendedPaperId != null && !recommendedPaperId.equals("")) {
              paperRecommendationsCSV.println(paperRecommendationsContent);
            }    
          }
        }
        paperRecommendationsCSV.flush();
        br.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }    
    
    // This is a function which parses the Paper References Table in Microsoft Academic Graph Schema
    private void parsePaperReferencesFile(String path, PrintWriter paperReferencesCSV) {
      try {
        BufferedReader br = Files.newBufferedReader(Paths.get(path), StandardCharsets.UTF_8);
        for (String line; (line = br.readLine()) != null;) {
          String[] splits = line.split("\t");
          if (splits.length != 0 && splits.length > 1) {
            String paperReferencesContent = "\"" + splits[0] + "\"" + "~" +
                            		    "\"" + removeSpecialCharacters(splits[1]) + "\"";
            String paperId = splits[0];
            String paperReferenceId = splits[1];
	    // I am checking for the foreign key constraint here	
            if (paperId != null && !paperId.equals("") && paperReferenceId != null && !paperReferenceId.equals("")) {
              paperReferencesCSV.println(paperReferencesContent);
            } 
          }
        }
        paperReferencesCSV.flush();
        br.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }	
    
    // This is a function which parses the Paper Resources Table in Microsoft Academic Graph Schema
    private void parsePaperResourcesFile(String path, PrintWriter paperResourcesCSV) {    
      try {
        BufferedReader br = Files.newBufferedReader(Paths.get(path), StandardCharsets.UTF_8);
        for (String line; (line = br.readLine()) != null;) {
          String[] splits = line.split("\t");
          if (splits.length != 0 && splits.length > 4) {
            String paperResourcesContent = "\"" + splits[0] + "\"" + "~" +
                            		   "\"" + splits[1] + "\"" + "~" +
                            		   "\"" + removeSpecialCharacters(splits[2]) + "\"" + "~" +
                            		   "\"" + removeSpecialCharacters(splits[3]) + "\"" + "~" +
                            	           "\"" + splits[4] + "\"";
            String paperId = splits[0];
	    // I am checking for the foreign key constraint here	
            if (paperId != null && !paperId.equals("")) {
              paperResourcesCSV.println(paperResourcesContent);
            } 
          }
        }
        paperResourcesCSV.flush();
        br.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }    
	
    // This is a function which parses the Paper URLs Table in Microsoft Academic Graph Schema
    private void parsePaperURLsFile(String path, PrintWriter paperURLsCSV) {
      try {
        BufferedReader br = Files.newBufferedReader(Paths.get(path), StandardCharsets.UTF_8);
        for (String line; (line = br.readLine()) != null;) {
          String[] splits = line.split("\t");
          if (splits.length != 0 && splits.length > 2) {
            String paperURLsContent = "\"" + splits[0] + "\"" + "~" + 
                               	      "\"" + splits[1] + "\"" + "~" + 
            		              "\"" + removeSpecialCharacters(splits[2]) + "\"";
            String paperId = splits[0];
	    // I am checking for the foreign key constraint here	
            if (paperId != null && !paperId.equals("")) {
              paperURLsCSV.println(paperURLsContent);
            } 
          }
        }
        paperURLsCSV.flush();
        br.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }	
    
    // This is a function which parses the Paper Table in Microsoft Academic Graph Schema
    private void parsePapersFile(String path, PrintWriter papersCSV) {
      try {
        BufferedReader br = Files.newBufferedReader(Paths.get(path), StandardCharsets.UTF_8);
        for (String line; (line = br.readLine()) != null;) {
          String[] splits = line.split("\t");
          if (splits.length != 0 && splits.length > 21) {
              String paperContentString = "\"" + splits[0] + "\"" + "~" +
            		  	          "\"" + splits[1] + "\"" + "~" +
                                          "\"" + removeSpecialCharacters(splits[2]) + "\"" + "~" +
                                          "\"" + removeSpecialCharacters(splits[3]) + "\"" + "~" +
                                          "\"" + removeSpecialCharacters(splits[4]) + "\"" + "~" +
                                          "\"" + removeSpecialCharacters(splits[5]) + "\"" + "~" +
                                          "\"" + removeSpecialCharacters(splits[6]) + "\"" + "~" +
                                          "\"" + splits[7] + "\"" + "~" +
                                          "\"" + splits[8] + "\"" + "~" +
                                          "\"" + removeSpecialCharacters(splits[9]) + "\"" + "~" +
                                          "\"" + splits[10] + "\"" + "~" +
                                          "\"" + splits[11] + "\"" + "~" +
                                          "\"" + splits[12] + "\"" + "~" +
                                          "\"" + removeSpecialCharacters(splits[13]) + "\"" + "~" +
                                          "\"" + removeSpecialCharacters(splits[14]) + "\"" + "~" +
                                          "\"" + removeSpecialCharacters(splits[15]) + "\"" + "~" +
                                          "\"" + removeSpecialCharacters(splits[16]) + "\"" + "~" +
              			          "\"" + splits[17] + "\"" + "~" +
              			          "\"" + splits[18] + "\"" + "~" +
              	                          "\"" + splits[19] + "\"" + "~" +
              		                  "\"" + removeSpecialCharacters(splits[20]) + "\"" + "~" +
                                          "\"" + splits[21] + "\"";
              papersCSV.println(paperContentString);
          }
        }
        papersCSV.flush();
        br.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    
    // This is a function which parses the Related Field of Study Table in Microsoft Academic Graph Schema
    private void parseRelatedFieldOfStudy(String path, PrintWriter relatedFieldOfStudyCSV) { 	  
      try {
        BufferedReader br = Files.newBufferedReader(Paths.get(path), StandardCharsets.UTF_8);
        for (String line; (line = br.readLine()) != null;) {
          String[] splits = line.split("\t");
          if (splits.length != 0 && splits.length > 4) {
            String relatedFieldOfStudyContent = "\""  + splits[0] + "\""  + "~" +
                            			"\"" + removeSpecialCharacters(splits[1]) + "\"" + "~" +
                            			"\"" + splits[2] + "\"" + "~" +
                            			"\"" + removeSpecialCharacters(splits[3]) + "\"" + "~" +
                            			"\"" + splits[4] + "\"";
            String fieldOfStudyId1 = splits[0];
            String fieldOfStudyId2 = splits[2];
	    // I am checking for the foreign key constraint here	  
            if (fieldOfStudyId1 != null && !fieldOfStudyId1.equals("") && fieldOfStudyId2 != null && !fieldOfStudyId2.equals("")) {
              relatedFieldOfStudyCSV.println(relatedFieldOfStudyContent);
            } 
          }
        }
        relatedFieldOfStudyCSV.flush();
        br.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
}
