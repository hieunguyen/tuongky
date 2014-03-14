package com.tuongky.service;

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.Field;
import com.google.appengine.api.search.Index;
import com.google.appengine.api.search.IndexSpec;
import com.google.appengine.api.search.Query;
import com.google.appengine.api.search.QueryOptions;
import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;
import com.google.appengine.api.search.SearchServiceFactory;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.tuongky.Settings;
import com.tuongky.model.GameCategory;
import com.tuongky.model.GameQuery;
import com.tuongky.model.GameSearchResult;
import com.tuongky.model.datastore.Game;

public final class SearchService {

  private static final String GAME_INDEX_NAME = Settings.DEV ? "GameIndex" : "GameIndex_1";
  private static int LIMIT = 10;

  private static final ImmutableMap<GameCategory, String> CATEGORY_TO_STRING_MAP =
      ImmutableMap.<GameCategory, String>builder()
          .put(GameCategory.UNKNOWN, "_unknown_")
          .put(GameCategory.MATCH, "vandau")
          .put(GameCategory.OPENING, "khaicuoc")
          .put(GameCategory.MIDDLE_GAME, "trungcuoc")
          .put(GameCategory.END_GAME, "tancuoc")
          .put(GameCategory.POSTURE, "cothe")
          .build();

  private SearchService() {}

  private static Document toDocument(Game game) {
    Preconditions.checkNotNull(game.getId(), "Game Id must not be null.");
    return Document.newBuilder()
        .setId(game.getId())
        .addField(
            Field.newBuilder()
                .setName("username")
                .setAtom(game.getUsername()))
        .addField(
            Field.newBuilder()
                .setName("fb_id")
                .setAtom(game.getFbId()))
        .addField(
            Field.newBuilder()
                .setName("fb_name")
                .setAtom(game.getFbName()))
        .addField(
            Field.newBuilder()
                .setName("category")
                .setAtom(CATEGORY_TO_STRING_MAP.get(game.getCategory())))
        .addField(
            Field.newBuilder()
                .setName("title")
                .setText(game.getTitle()))
        .addField(
            Field.newBuilder()
                .setName("n_title")
                .setText(game.getNTitle()))
        .addField(
            Field.newBuilder()
                .setName("book")
                .setText(game.getBook()))
        .addField(
            Field.newBuilder()
                .setName("n_book")
                .setText(game.getNBook()))
        .build();
  }

  private static List<Document> toDocuments(List<Game> games) {
    List<Document> documents = Lists.newArrayList();
    for (Game game : games) {
      documents.add(toDocument(game));
    }
    return documents;
  }

  private static Index getIndex() {
    IndexSpec indexSpec = IndexSpec.newBuilder()
        .setName(GAME_INDEX_NAME)
        .build();
    return SearchServiceFactory.getSearchService().getIndex(indexSpec);
  }

  public static void indexGame(Game game) {
    indexGames(Lists.newArrayList(game));
  }

  public static void indexGames(List<Game> games) {
    getIndex().put(toDocuments(games));
  }

  private static String buildQueryString(GameQuery gameQuery) {
    if (gameQuery.hasQueryString()) {
      return gameQuery.getQueryString();
    }
    StringBuilder builder = new StringBuilder();
    if (gameQuery.hasCategory()) {
      builder.append("category:" + gameQuery.getCategory().toLowerCase());
    }
    if (gameQuery.hasTitle()) {
      if (builder.length() > 0) {
        builder.append(" AND ");
      }
      builder.append("n_title:" + gameQuery.getTitle().toLowerCase());
    }
    if (gameQuery.hasBook()) {
      if (builder.length() > 0) {
        builder.append(" AND ");
      }
      builder.append("n_book:" + gameQuery.getBook().toLowerCase());
    }
    return builder.toString();
  }

  private static GameCategory getGameCategoryFromString(String category) {
    for (Entry<GameCategory, String> entry : CATEGORY_TO_STRING_MAP.entrySet()) {
      if (entry.getValue().equalsIgnoreCase(category)) {
        return entry.getKey();
      }
    }
    return GameCategory.UNKNOWN;
  }

  private static String getAtom(ScoredDocument scoredDocument, String fieldName) {
    if (scoredDocument.getFieldCount(fieldName) == 0) {
      return null;
    }
    Iterator<Field> it = scoredDocument.getFields(fieldName).iterator();
    return it.hasNext() ? it.next().getAtom() : null;
  }

  private static String getText(ScoredDocument scoredDocument, String fieldName) {
    if (scoredDocument.getFieldCount(fieldName) == 0) {
      return null;
    }
    Iterator<Field> it = scoredDocument.getFields(fieldName).iterator();
    return it.hasNext() ? it.next().getText() : null;
  }

  private static Game toGame(ScoredDocument scoredDocument) {
    String id = scoredDocument.getId();
    String username = getAtom(scoredDocument, "username");
    String fbId = getAtom(scoredDocument, "fb_id");
    String fbName = getAtom(scoredDocument, "fb_name");
    String category = getAtom(scoredDocument, "category");
    String title = getText(scoredDocument, "title");
    String nTitle = getText(scoredDocument, "n_title");
    String book = getText(scoredDocument, "book");
    String nBook = getText(scoredDocument, "n_book");
    return new Game(id, username, fbId, fbName, getGameCategoryFromString(category),
        title, nTitle, book, nBook, null);
  }

  public static GameSearchResult search(GameQuery gameQuery) {
    String queryString = buildQueryString(gameQuery);
    QueryOptions options = QueryOptions.newBuilder()
        .setLimit(LIMIT)
        .setOffset(gameQuery.getOffset())
        .build();
    Query query = Query.newBuilder()
        .setOptions(options)
        .build(queryString);
    Results<ScoredDocument> result =  getIndex().search(query);
    List<Game> games = Lists.newArrayList();
    for (ScoredDocument scoredDoc : result.getResults()) {
      games.add(toGame(scoredDoc));
    }
    return new GameSearchResult(games, result.getNumberFound());
  }

  public static void deleteGame(String gameId) {
    getIndex().delete(gameId);
  }
}
