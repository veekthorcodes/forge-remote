package com.onpointserv.forge_remote;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class ForgeStorage implements ForgeListCrudRepository {
	private static final Logger log = LoggerFactory.getLogger(ForgeStorage.class);
	private final HttpGraphQlClient client = HttpGraphQlClient.builder(
			WebClient.builder()
					.baseUrl("https://api.atlassian.com/graphql")
					.build())
			.build();

	@Override
	public Collection<ForgeStorageEntity.Node> findAll() {
		final String query = """
				query forge_app_getApplicationStorageEntities {
					appStoredEntities {
						edges {
							node {
								key
								value
							}
						}
					}
				}
				""";
		return client.mutate()
				.header("Authorization", "Bearer " + ForgeContext.getSystemToken())
				.build()
				.document(query)
				.execute()
				.map(res -> res.field("appStoredEntities.edges")
						.toEntityList(ForgeStorageEntity.Edge.class)
						.stream().map(ForgeStorageEntity.Edge::node).toList())
				.block();
	}

	@Override
	public Optional<Object> findByKey(String key, boolean encrypted) {
		final String query = """
					query forge_app_getApplicationStorageEntity($key: ID!, $encrypted: Boolean!) {
						appStoredEntity(key: $key, encrypted: $encrypted) {
							value
						}
					}
				""";
		return Optional.ofNullable(client.mutate()
				.header("Authorization", "Bearer " + ForgeContext.getSystemToken())
				.build()
				.document(query)
				.variables(Map.of("key", key, "encrypted", encrypted))
				.execute()
				.mapNotNull(res -> res.field("appStoredEntity.value").toEntity(Object.class))
				.block());
	}

	@Override
	public boolean set(String key, Object value, boolean encrypted) {
		final String query = """
					mutation forge_app_setApplicationStorageEntity($input: SetAppStoredEntityMutationInput!) {
						appStorage {
							setAppStoredEntity(input: $input){
								success
							}
						}
					}
				""";

		var res = client.mutate()
				.header("Authorization", "Bearer " + ForgeContext.getSystemToken())
				.build()
				.document(query)
				.variable("input", new ForgeStorageEntity.SetInput(key, value, encrypted))
				.retrieve("appStorage.setAppStoredEntity.success")
				.toEntity(Boolean.class)
				.block();
		log.info("{}", res);
		return Boolean.TRUE.equals(true);
	}

	@Override
	public boolean deleteByKey(String key) {
		final String query = """
				mutation forge_app_deleteApplicationStorageEntity($input: DeleteAppStoredEntityMutationInput!) {
					appStorage {
						deleteAppStoredEntity(input: $input) {
							success
						}
					}
				}
				""";
		return Boolean.TRUE.equals(
				client.mutate()
						.header("Authorization", "Bearer " + ForgeContext.getSystemToken())
						.build()
						.document(query)
						.variable("input", new ForgeStorageEntity.DeleteInput(key))
						.retrieve("appStorage.deleteAppStoredEntity.succes")
						.toEntity(Boolean.class)
						.block());
	}
}
