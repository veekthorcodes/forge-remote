package com.onpointserv.forge_remote;

import java.util.Collection;
import java.util.Optional;

public interface ForgeListCrudRepository {
	Collection<ForgeStorageEntity.Node> findAll();

	Optional<Object> findByKey(String key, boolean encrypted);

	boolean set(String key, Object value, boolean encrypted);

	boolean deleteByKey(String key);
}
