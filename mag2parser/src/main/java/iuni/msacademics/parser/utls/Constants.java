package iuni.msacademics.parser.utls;

import java.util.HashMap;
import java.util.Map;

public class Constants {
    public enum sourceFileNames {
        AFFILIATIONS,
        AUTHORS,
        CONFERENCE_INSTANCES,
        CONFERENCE_SERIES,
        FIELD_OF_STUDY_CHILDREN,
        FIELD_OF_STUDY,
        JOURNALS,
        PAPER_ABSTRACT_INVERTED_INDEX,
        PAPER_AUTHOR_AFFILIATIONS,
        PAPER_CITATION_CONTEXTS,
        PAPER_FIELD_OF_STUDY,
        PAPER_LANGUAGES,
        PAPER_RECOMMENDATIONS,
        PAPER_REFERENCES,
        PAPER_RESOURCES,
        PAPER_URLS,
        PAPERS,
        RELATED_FIELD_OF_STUDY
    }

    public static final Map<String, sourceFileNames> sourceFileNamesMap;
    static {
        sourceFileNamesMap = new HashMap<>();
        sourceFileNamesMap.put("Affiliations.txt",sourceFileNames.AFFILIATIONS);
        sourceFileNamesMap.put("Authors.txt",sourceFileNames.AUTHORS);
        sourceFileNamesMap.put("ConferenceInstances.txt",sourceFileNames.CONFERENCE_INSTANCES);
        sourceFileNamesMap.put("ConferenceSeries.txt",sourceFileNames.CONFERENCE_SERIES);
        sourceFileNamesMap.put("FieldsOfStudy.txt",sourceFileNames.FIELD_OF_STUDY);
        sourceFileNamesMap.put("FieldOfStudyChildren.txt",sourceFileNames.FIELD_OF_STUDY_CHILDREN);
        sourceFileNamesMap.put("Journals.txt",sourceFileNames.JOURNALS);
        sourceFileNamesMap.put("PaperAbstractsInvertedIndex.txt",sourceFileNames.PAPER_ABSTRACT_INVERTED_INDEX);
        sourceFileNamesMap.put("PaperAuthorAffiliations.txt",sourceFileNames.PAPER_AUTHOR_AFFILIATIONS);
        sourceFileNamesMap.put("PaperCitationContexts.txt",sourceFileNames.PAPER_CITATION_CONTEXTS);
        sourceFileNamesMap.put("PaperFieldsOfStudy.txt",sourceFileNames.PAPER_FIELD_OF_STUDY);
        sourceFileNamesMap.put("PaperLanguages.txt",sourceFileNames.PAPER_LANGUAGES);
        sourceFileNamesMap.put("PaperRecommendations.txt",sourceFileNames.PAPER_RECOMMENDATIONS);
        sourceFileNamesMap.put("PaperReferences.txt",sourceFileNames.PAPER_REFERENCES);
        sourceFileNamesMap.put("PaperResources.txt",sourceFileNames.PAPER_RESOURCES);
        sourceFileNamesMap.put("PaperUrls.txt",sourceFileNames.PAPER_URLS);
        sourceFileNamesMap.put("Papers.txt",sourceFileNames.PAPERS);
        sourceFileNamesMap.put("RelatedFieldOfStudy.txt",sourceFileNames.RELATED_FIELD_OF_STUDY);
    }
}
