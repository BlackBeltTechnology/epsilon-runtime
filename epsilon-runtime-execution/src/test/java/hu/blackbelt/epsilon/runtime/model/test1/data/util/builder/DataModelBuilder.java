package hu.blackbelt.epsilon.runtime.model.test1.data.util.builder;

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

/**
 * <!-- begin-user-doc --> 
 *   A builder for the model object ' <em><b>hu.blackbelt.epsilon.runtime.model.test1.data.DataModel</b></em>'.
 * <!-- end-user-doc -->
 * 
 * @generated
 */
public class DataModelBuilder implements hu.blackbelt.epsilon.runtime.model.test1.data.util.builder.IDataBuilder<hu.blackbelt.epsilon.runtime.model.test1.data.DataModel> {
  // features and builders
  private java.lang.String m_name;
  private java.util.Collection<hu.blackbelt.epsilon.runtime.model.test1.data.Entity> m_entity = new java.util.LinkedList<hu.blackbelt.epsilon.runtime.model.test1.data.Entity>();
  private java.util.Collection<hu.blackbelt.epsilon.runtime.model.test1.data.util.builder.IDataBuilder<? extends hu.blackbelt.epsilon.runtime.model.test1.data.Entity>> m_featureEntityBuilder = new java.util.LinkedList<hu.blackbelt.epsilon.runtime.model.test1.data.util.builder.IDataBuilder<? extends hu.blackbelt.epsilon.runtime.model.test1.data.Entity>>();
  // helper attributes
  private boolean m_featureEntitySet = false;
  private boolean m_featureNameSet = false;

  /**
   * Builder is not instantiated with a constructor.
   * @see #newDataModelBuilder()
   */
  private DataModelBuilder() {
  }

  /**
   * This method creates a new instance of the DataModelBuilder.
   * @return new instance of the DataModelBuilder
   */
  public static DataModelBuilder create() {
    return new DataModelBuilder();
  }

  /**
   * This method can be used to override attributes of the builder. It constructs a new builder and copies the current values to it.
   */
  public DataModelBuilder but() {
    DataModelBuilder _builder = create();
    _builder.m_featureEntitySet = m_featureEntitySet;
    _builder.m_entity = m_entity;
    _builder.m_featureEntityBuilder = m_featureEntityBuilder;
    _builder.m_featureNameSet = m_featureNameSet;
    _builder.m_name = m_name;
    return _builder;
  }

  /**
   * This method constructs the final hu.blackbelt.epsilon.runtime.model.test1.data.DataModel type.
   * @return new instance of the hu.blackbelt.epsilon.runtime.model.test1.data.DataModel type
   */
  public hu.blackbelt.epsilon.runtime.model.test1.data.DataModel build() {
    final hu.blackbelt.epsilon.runtime.model.test1.data.DataModel _newInstance = hu.blackbelt.epsilon.runtime.model.test1.data.DataFactory.eINSTANCE.createDataModel();
    if (m_featureNameSet) {
      _newInstance.setName(m_name);
    }
    if (m_featureEntitySet) {
      _newInstance.getEntity().addAll(m_entity);
    } else {
      if (!m_featureEntityBuilder.isEmpty()) {
        for (hu.blackbelt.epsilon.runtime.model.test1.data.util.builder.IDataBuilder<? extends hu.blackbelt.epsilon.runtime.model.test1.data.Entity> builder : m_featureEntityBuilder) {
          _newInstance.getEntity().add(builder.build());
        }
      }
    }
    return _newInstance;
  }

  public DataModelBuilder withName(java.lang.String p_name) {
    m_name = p_name;
    m_featureNameSet = true;
    return this;
  }

  public DataModelBuilder withEntity(hu.blackbelt.epsilon.runtime.model.test1.data.Entity p_entity) {
    m_entity.add(p_entity);
    m_featureEntitySet = true;
    return this;
  }

  public DataModelBuilder withEntity(java.util.Collection<? extends hu.blackbelt.epsilon.runtime.model.test1.data.Entity> p_entity) {
    m_entity.addAll(p_entity);
    m_featureEntitySet = true;
    return this;
  }

  public DataModelBuilder withEntity(hu.blackbelt.epsilon.runtime.model.test1.data.util.builder.IDataBuilder<? extends hu.blackbelt.epsilon.runtime.model.test1.data.Entity> p_entityBuilder) {
    m_featureEntityBuilder.add(p_entityBuilder);
    return this;
  }
}
