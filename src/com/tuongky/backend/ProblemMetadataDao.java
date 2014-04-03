package com.tuongky.backend;

import java.util.List;

import com.google.common.collect.Lists;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.DAOBase;
import com.tuongky.model.datastore.ProblemMetadata;

public class ProblemMetadataDao extends DAOBase {

  static {
    ObjectifyRegister.register();
  }

  public static ProblemMetadataDao instance = new ProblemMetadataDao();

  public ProblemMetadata save(ProblemMetadata problemMetadata) {
    ObjectifyService.begin().put(problemMetadata);
    return problemMetadata;
  }

  public ProblemMetadata getById(long id) {
    return ObjectifyService.begin().find(ProblemMetadata.class, id);
  }

  public List<ProblemMetadata> getByIds(List<Long> ids) {
    List<Key<ProblemMetadata>> keys = Lists.newArrayList();
    for (Long id : ids) {
      keys.add(new Key<ProblemMetadata>(ProblemMetadata.class, id));
    }
    return Lists.newArrayList(
        ObjectifyService.begin().get(keys).values());
  }
}
