/**
 */
package hu.blackbelt.epsilon.runtime.model.test1.data.impl;

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

import hu.blackbelt.epsilon.runtime.model.test1.data.*;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class DataFactoryImpl extends EFactoryImpl implements DataFactory {
    /**
     * Creates the default factory implementation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static DataFactory init() {
        try {
            DataFactory theDataFactory = (DataFactory)EPackage.Registry.INSTANCE.getEFactory(DataPackage.eNS_URI);
            if (theDataFactory != null) {
                return theDataFactory;
            }
        }
        catch (Exception exception) {
            EcorePlugin.INSTANCE.log(exception);
        }
        return new DataFactoryImpl();
    }

    /**
     * Creates an instance of the factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public DataFactoryImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EObject create(EClass eClass) {
        switch (eClass.getClassifierID()) {
            case DataPackage.DATA_MODEL: return createDataModel();
            case DataPackage.ENTITY: return createEntity();
            case DataPackage.ATTRIBUTE: return createAttribute();
            case DataPackage.ENTITY_REFERENCE: return createEntityReference();
            default:
                throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public DataModel createDataModel() {
        DataModelImpl dataModel = new DataModelImpl();
        return dataModel;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Entity createEntity() {
        EntityImpl entity = new EntityImpl();
        return entity;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Attribute createAttribute() {
        AttributeImpl attribute = new AttributeImpl();
        return attribute;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EntityReference createEntityReference() {
        EntityReferenceImpl entityReference = new EntityReferenceImpl();
        return entityReference;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public DataPackage getDataPackage() {
        return (DataPackage)getEPackage();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @deprecated
     * @generated
     */
    @Deprecated
    public static DataPackage getPackage() {
        return DataPackage.eINSTANCE;
    }

} //DataFactoryImpl
