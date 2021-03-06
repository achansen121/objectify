package com.googlecode.objectify.impl.translate;

import com.google.appengine.api.datastore.EmbeddedEntity;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PropertyContainer;
import com.googlecode.objectify.impl.EntityMetadata;
import com.googlecode.objectify.impl.Path;
import com.googlecode.objectify.impl.Registrar;


/**
 * <p>Translator which can translate arbitrary entities based on registered kinds. This provides a layer
 * of indirection, and allows you to store heterogeneous collections.</p>
 *
 * @author Jeff Schnitzer <jeff@infohazard.org>
 */
public class EntityTranslator implements Translator<Object, PropertyContainer>
{
	private final Registrar registrar;

	/**
	 */
	public EntityTranslator(Registrar registrar) {
		this.registrar = registrar;
	}

	@Override
	public Object load(PropertyContainer container, LoadContext ctx, Path path) throws SkipException {
		Key key = getKey(container);
		EntityMetadata<?> meta = registrar.getMetadataSafe(key.getKind());

		return meta.getTranslator().load(container, ctx, path);
	}

	@Override
	public PropertyContainer save(Object pojo, boolean index, SaveContext ctx, Path path) throws SkipException {
		@SuppressWarnings("unchecked")
		EntityMetadata<Object> meta = (EntityMetadata<Object>)registrar.getMetadataSafe(pojo.getClass());

		return meta.getTranslator().save(pojo, index, ctx, path);
	}

	/**
	 */
	private Key getKey(PropertyContainer pc) {
		if (pc instanceof EmbeddedEntity)
			return ((EmbeddedEntity)pc).getKey();
		else if (pc instanceof Entity)
			return ((Entity)pc).getKey();
		else
			throw new IllegalArgumentException("Unknown new type of PropertyContainer");
	}

}
