# SearchEngine
Search engine for documents. 

# Main features

## User can
* Create an inverted index and posting files for a collection of documents.
* Extract relevant documents from the collection regarding a specific subject with queries.
* Display the dictionary of the collection.
* Load dictionary from the disk.

# Backend

## Read package:
* ReadFile class- goes over all the given directory, sub-directories and extracts the documents content.
* Parse class- goes over all the words in a given document and characterized them by specific rules (explanations further on).
* Searcher class- searching for the relevat documents with a giving query.

## Index package
* Index class- prepares the index of the entire collection and writes to the disk.
* cityIndexer- establishing a connection to an API to import details about cities if necessary.
* Semantic- establishing a connection to an API to import semantic words for a given word.
* Ranker- ranking the relevance of each document to a giving query.

## Objects
Used to save details (expansion further on) on each element for future posting file.
* Term- represent a word. 
* Doc- represents a document
* City- represents a city.

## Term have
* Documents the word appears in.
* Number of appearances in each document.

## Doc have
* The most frequent term in the document.
* Number of unique terms.
* Document length.
* The name of the city the document is related to (optional).

## City have
* The Country it belongs to.
* Population.
* Currency

## Term Rules
The terms are characterized by the following rules:
* Single word.
* Dates.
* Numbers.
* Precentage.
* Expressions ( in the form of "word-word").
* Between Expressions ("Between ... and ... ").
* Prices.

# Code referenced from
* Stemmer class - https://tartarus.org/martin/PorterStemmer/java.txt
