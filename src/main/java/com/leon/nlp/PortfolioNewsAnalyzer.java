package com.leon.nlp;

import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.document.TextDocument;
import de.l3s.boilerpipe.extractors.CommonExtractors;
import de.l3s.boilerpipe.sax.BoilerpipeSAXInput;
import de.l3s.boilerpipe.sax.HTMLDocument;
import de.l3s.boilerpipe.sax.HTMLFetcher;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.util.StringUtils;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * Created by ntcon on 6/10/2017.
 */
class PortfolioNewsAnalyzer {

    private static final String modelPath = "edu\\stanford\\nlp\\models\\pos-tagger\\english-left3words\\english-left3words-distsim.tagger";

    private Set<String> portfolio;

    private MaxentTagger maxentTagger;

    PortfolioNewsAnalyzer() {
        this.maxentTagger = new MaxentTagger(modelPath);
        portfolio = new HashSet<>();
    }

    //a scraper that can download and extract the body of an article
    private String extractFromURL(String url) throws IOException, SAXException, BoilerpipeProcessingException {
        HTMLDocument document = HTMLFetcher.fetch(new URL(url));
        TextDocument textDocument = new BoilerpipeSAXInput(document.toInputSource()).getTextDocument();
        return CommonExtractors.ARTICLE_EXTRACTOR.getText(textDocument);
    }

    //a tagger that can parser the article body and identify proper nouns
    private String tagPos(String input) {
        return this.maxentTagger.tagString(input);
    }

    //a processor that takes the tagged output and collects the proper nouns into a collection
    private Set<String> extractProperNouns(String taggedOutput) {
        Set<String> propNounSet = new HashSet<>();
        String[] allTokens = taggedOutput.split(" ");
        List<String> propNounList = new ArrayList<>();
        for (String token : allTokens) {
            String[] splitTokens = token.split("_");
            if ("NNP".equals(splitTokens[1])) {
                propNounList.add(splitTokens[0]);
            } else {
                if (!propNounList.isEmpty()) {
                    propNounSet.add(StringUtils.join(propNounList, " "));
                    propNounList.clear();
                }
            }
        }

        if (!propNounList.isEmpty()) {
            propNounSet.add(StringUtils.join(propNounList, " "));
            propNounList.clear();
        }

        return propNounSet;
    }

    private boolean arePortfolioCompanyMentioned(Set<String> articleProperNouns) {
        return !Collections.disjoint(articleProperNouns, portfolio);
    }

    boolean analyzeArticle(String url) throws SAXException, IOException, BoilerpipeProcessingException {
        String articleText = extractFromURL(url);
        return arePortfolioCompanyMentioned(extractProperNouns(tagPos(articleText)));
    }

    void addPortfolioCompany(String company) {
        portfolio.add(company);
    }

}
