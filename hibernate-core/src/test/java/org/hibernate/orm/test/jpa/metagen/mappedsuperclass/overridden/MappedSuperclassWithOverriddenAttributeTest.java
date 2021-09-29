/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.orm.test.jpa.metagen.mappedsuperclass.overridden;

import java.util.Arrays;
import jakarta.persistence.EntityManagerFactory;

import org.hibernate.cfg.AvailableSettings;
import org.hibernate.jpa.test.TestingEntityManagerFactoryGenerator;

import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.orm.junit.BaseUnitTest;
import org.hibernate.testing.orm.junit.FailureExpected;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Oliver Breidenbach
 */
@TestForIssue(jiraKey = "HHH-11078")
@BaseUnitTest
public class MappedSuperclassWithOverriddenAttributeTest {

	@Test
	@FailureExpected(jiraKey = "HHH-11078")
	public void testStaticMetamodelOverridden() {
		EntityManagerFactory emf = TestingEntityManagerFactoryGenerator.generateEntityManagerFactory(
				AvailableSettings.LOADED_CLASSES,
				Arrays.asList( Product2.class )
		);
		try {
			assertNotNull(
					Product1_.overridenName,
					"'Product1_.overridenName' should not be null)"
			);

			assertNotNull(
					Product2_.overridenName,
					"'Product2_.overridenName' should not be null)"
			); // is null
		}
		finally {
			emf.close();
		}
	}
}
