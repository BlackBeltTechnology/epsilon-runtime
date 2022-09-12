/**
 */
package hu.blackbelt.epsilon.runtime.model.test1.data;

/*-
 * #%L
 * epsilon-runtime-execution
 * %%
 * Copyright (C) 2018 - 2022 BlackBelt Technology
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Entity Reference</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link hu.blackbelt.epsilon.runtime.model.test1.data.EntityReference#getName <em>Name</em>}</li>
 *   <li>{@link hu.blackbelt.epsilon.runtime.model.test1.data.EntityReference#isToMany <em>To Many</em>}</li>
 *   <li>{@link hu.blackbelt.epsilon.runtime.model.test1.data.EntityReference#getTarget <em>Target</em>}</li>
 * </ul>
 *
 * @see hu.blackbelt.epsilon.runtime.model.test1.data.DataPackage#getEntityReference()
 * @model
 * @generated
 */
public interface EntityReference extends EObject {
	/**
	 * Returns the value of the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Name</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Name</em>' attribute.
	 * @see #setName(String)
	 * @see hu.blackbelt.epsilon.runtime.model.test1.data.DataPackage#getEntityReference_Name()
	 * @model
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link hu.blackbelt.epsilon.runtime.model.test1.data.EntityReference#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

	/**
	 * Returns the value of the '<em><b>To Many</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>To Many</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>To Many</em>' attribute.
	 * @see #setToMany(boolean)
	 * @see hu.blackbelt.epsilon.runtime.model.test1.data.DataPackage#getEntityReference_ToMany()
	 * @model
	 * @generated
	 */
	boolean isToMany();

	/**
	 * Sets the value of the '{@link hu.blackbelt.epsilon.runtime.model.test1.data.EntityReference#isToMany <em>To Many</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>To Many</em>' attribute.
	 * @see #isToMany()
	 * @generated
	 */
	void setToMany(boolean value);

	/**
	 * Returns the value of the '<em><b>Target</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Target</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Target</em>' reference.
	 * @see #setTarget(Entity)
	 * @see hu.blackbelt.epsilon.runtime.model.test1.data.DataPackage#getEntityReference_Target()
	 * @model required="true"
	 * @generated
	 */
	Entity getTarget();

	/**
	 * Sets the value of the '{@link hu.blackbelt.epsilon.runtime.model.test1.data.EntityReference#getTarget <em>Target</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Target</em>' reference.
	 * @see #getTarget()
	 * @generated
	 */
	void setTarget(Entity value);

} // EntityReference
