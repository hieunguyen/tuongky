package com.tuongky.backend;

import java.util.List;

import com.google.common.collect.Lists;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.DAOBase;
import com.tuongky.model.datastore.GameMetadata;

public class GameMetadataDao extends DAOBase {

  static {
    ObjectifyService.register(GameMetadata.class);
  }

  public GameMetadata save(GameMetadata gameMetadata) {
    ObjectifyService.begin().put(gameMetadata);
    return gameMetadata;
  }

  public GameMetadata getById(String id) {
    return ObjectifyService.begin().find(GameMetadata.class, id);
  }

  public List<GameMetadata> getByIds(List<String> ids) {
    List<Key<GameMetadata>> keys = Lists.newArrayList();
    for (String id : ids) {
      keys.add(new Key<GameMetadata>(GameMetadata.class, id));
    }
    return Lists.newArrayList(
        ObjectifyService.begin().get(keys).values());
  }
}
