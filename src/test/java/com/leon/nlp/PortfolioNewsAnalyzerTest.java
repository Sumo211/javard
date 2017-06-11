package com.leon.nlp;

import de.l3s.boilerpipe.BoilerpipeProcessingException;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import java.io.IOException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by ntcon on 6/11/2017.
 */
public class PortfolioNewsAnalyzerTest {

    private static final String articleURL = "http://www.reuters.com/article/us-essilor-m-a-luxottica-group-idUSKBN14Z110";

    private PortfolioNewsAnalyzer portfolioNewsAnalyzer;

    @Before
    public void prepareData() {
        portfolioNewsAnalyzer = new PortfolioNewsAnalyzer();
    }

    @Test
    public void verifyPortfolioIsMentionedInArticle() throws SAXException, IOException, BoilerpipeProcessingException {
        portfolioNewsAnalyzer.addPortfolioCompany("Luxottica");
        assertTrue(portfolioNewsAnalyzer.analyzeArticle(articleURL));
    }

    @Test
    public void verifyPortfolioIsNotMentionedInArticle() throws SAXException, IOException, BoilerpipeProcessingException {
        portfolioNewsAnalyzer.addPortfolioCompany("Luxotticas");
        assertFalse(portfolioNewsAnalyzer.analyzeArticle(articleURL));
    }

}
