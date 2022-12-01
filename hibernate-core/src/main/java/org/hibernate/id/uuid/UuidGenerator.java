/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.id.uuid;

import java.lang.reflect.Member;
import java.util.UUID;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.factory.spi.CustomIdGeneratorCreationContext;
import org.hibernate.tuple.GenerationTiming;
import org.hibernate.tuple.InMemoryGenerator;
import org.hibernate.type.descriptor.java.UUIDJavaType;
import org.hibernate.type.descriptor.java.UUIDJavaType.ValueTransformer;

import static org.hibernate.annotations.UuidGenerator.Style.TIME;
import static org.hibernate.internal.util.ReflectHelper.getPropertyType;

/**
 * UUID-based IdentifierGenerator
 *
 * @see org.hibernate.annotations.UuidGenerator
 */
public class UuidGenerator implements InMemoryGenerator {
	interface ValueGenerator {
		UUID generateUuid(SharedSessionContractImplementor session);
	}

	private final ValueGenerator generator;
	private final ValueTransformer valueTransformer;

	public UuidGenerator(
			org.hibernate.annotations.UuidGenerator config,
			Member idMember,
			CustomIdGeneratorCreationContext creationContext) {
		if ( config.style() == TIME ) {
			generator = new CustomVersionOneStrategy();
		}
		else {
			generator = StandardRandomStrategy.INSTANCE;
		}

		final Class<?> propertyType = getPropertyType( idMember );

		if ( UUID.class.isAssignableFrom( propertyType ) ) {
			valueTransformer = UUIDJavaType.PassThroughTransformer.INSTANCE;
		}
		else if ( String.class.isAssignableFrom( propertyType ) ) {
			valueTransformer = UUIDJavaType.ToStringTransformer.INSTANCE;
		}
		else if ( byte[].class.isAssignableFrom( propertyType ) ) {
			valueTransformer = UUIDJavaType.ToBytesTransformer.INSTANCE;
		}
		else {
			throw new HibernateException( "Unanticipated return type [" + propertyType.getName() + "] for UUID conversion" );
		}
	}

	@Override
	public GenerationTiming getGenerationTiming() {
		return GenerationTiming.INSERT;
	}

	@Override
	public Object generate(SharedSessionContractImplementor session, Object owner, Object currentValue) {
		return valueTransformer.transform( generator.generateUuid( session ) );
	}
}
