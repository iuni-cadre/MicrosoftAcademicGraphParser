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
    private String inputFormat = null;
    private String sourceDir = null;
    private String targetDir = null;
    private String csvPrefix = null;

    public static void main(String[] args) {
      try {
        MAGFormatParser formatParser = null;
        MSAcademicsParser parser = new MSAcademicsParser();
        parser.parseArguments(args);

        if (parser.getInputFormat().compareTo("MicrosoftFormat") == 0) {
          formatParser = new MicrosoftFormatParser(parser.sourceDir,
             parser.targetDir, parser.csvPrefix);
        } else if (parser.getInputFormat().compareTo("OpenAlexFormat") == 0) {
          System.out.println("inputFormat: " + parser.inputFormat);
          System.out.println("sourceDir: " + parser.sourceDir);
          System.out.println("targetDir: " + parser.targetDir);
          System.out.println("csvPrefix: " + parser.csvPrefix);
          formatParser = new OpenAlexFormatParser(parser.sourceDir,
             parser.targetDir, parser.csvPrefix);
        } 
 
        formatParser.readFile();
      } catch (Exception e) {
        logger.error("Error occurred !!!" , e);
        e.printStackTrace();
      }
    }

    public String getInputFormat() {
       String inputFormatCopy = new String(inputFormat);
       return inputFormatCopy;
    }

    public void parseArguments(String[] args) throws Exception {
      try {
        Options options = new Options();

        options.addOption("inputFormat", true, "Micrsoft Academic Graph format (MicrosoftFormat) or backwards compatible OpenAlex MAG format (OpenAlexFormat)");
        options.addOption("sourceDir", true, "Home directory of JSON files");
        options.addOption("targetDir", true, "Directory to save csv files");
        options.addOption("csvPrefix", true, "Prefix for publish and soure csv files");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);
        Option[] cmdOptions = cmd.getOptions();
        if (cmdOptions == null || cmdOptions.length == 0) {
          System.out.println("You have not provided needed info, sourceDir, targetDir and csvPrefix");
          throw new Exception("You have not provided needed info, sourceDir, targetDir and csvPrefix");
        }
        for (Option option : cmdOptions) {

          if (option.getOpt().equals("inputFormat")) {
            inputFormat = option.getValue();
            if (inputFormat == null) {
              System.out.println("Please provide the inputFormat option");
              System.exit(0);
            }
          } else if (option.getOpt().equals("sourceDir")) {
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
            csvPrefix = option.getValue();
            if (csvPrefix == null) {
              System.out.println("Please provide csvPrefix option");
              System.exit(0);
            }
          }
        }

      } catch (ParseException e) {
        logger.error("Error while reading command line parameters" , e);
        throw new Exception("Error while reading command line parameters" , e);
      }
    }
}

