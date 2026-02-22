package com.minerasoftware.graphql.core.graphqljava;

import graphql.language.AstComparator;
import graphql.language.AstPrinter;
import graphql.language.AstSorter;
import graphql.language.Document;
import graphql.parser.Parser;
import com.minerasoftware.graphql.core.GraphqlMatcher;

/**
 * graphql-java based semantic matcher.
 */
public final class GraphqlJavaMatcher implements GraphqlMatcher {

    private final Parser parser;
    private final AstSorter sorter;

    public GraphqlJavaMatcher() {
        this(new Parser(), new AstSorter());
    }

    GraphqlJavaMatcher(Parser parser, AstSorter sorter) {
        this.parser = parser;
        this.sorter = sorter;
    }

    @Override
    public boolean semanticallyEqual(String expectedOperation, String actualOperation) {
        Document expected = parseAndNormalize(expectedOperation);
        Document actual = parseAndNormalize(actualOperation);
        return AstComparator.isEqual(expected, actual);
    }

    @Override
    public String canonicalForm(String operation) {
        return AstPrinter.printAst(parseAndNormalize(operation));
    }

    @Override
    public String canonicalCompactForm(String operation) {
        return AstPrinter.printAstCompact(parseAndNormalize(operation));
    }

    private Document parseAndNormalize(String operation) {
        return sorter.sort(parser.parseDocument(operation));
    }
}