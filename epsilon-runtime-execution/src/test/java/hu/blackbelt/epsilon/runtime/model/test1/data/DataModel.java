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

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Model</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link hu.blackbelt.epsilon.runtime.model.test1.data.DataModel#getName <em>Name</em>}</li>
 *   <li>{@link hu.blackbelt.epsilon.runtime.model.test1.data.DataModel#getEntity <em>Entity</em>}</li>
 * </ul>
 *
 * @see hu.blackbelt.epsilon.runtime.model.test1.data.DataPackage#getDataModel()
 * @model
 * @generated
 */
public interface DataModel extends EObject {
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
     * @see hu.blackbelt.epsilon.runtime.model.test1.data.DataPackage#getDataModel_Name()
     * @model
     * @generated
     */
    String getName();

    /**
     * Sets the value of the '{@link hu.blackbelt.epsilon.runtime.model.test1.data.DataModel#getName <em>Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Name</em>' attribute.
     * @see #getName()
     * @generated
     */
    void setName(String value);

    /**
     * Returns the value of the '<em><b>Entity</b></em>' containment reference list.
     * The list contents are of type {@link hu.blackbelt.epsilon.runtime.model.test1.data.Entity}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Entity</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Entity</em>' containment reference list.
     * @see hu.blackbelt.epsilon.runtime.model.test1.data.DataPackage#getDataModel_Entity()
     * @model containment="true"
     * @generated
     */
    EList<Entity> getEntity();

} // DataModel
