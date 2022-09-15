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
 *   A builder for the model object ' <em><b>hu.blackbelt.epsilon.runtime.model.test1.data.EntityReference</b></em>'.
 * <!-- end-user-doc -->
 * 
 * @generated
 */
public class EntityReferenceBuilder implements hu.blackbelt.epsilon.runtime.model.test1.data.util.builder.IDataBuilder<hu.blackbelt.epsilon.runtime.model.test1.data.EntityReference> {
  // features and builders
  private java.lang.String m_name;
  private hu.blackbelt.epsilon.runtime.model.test1.data.Entity m_target;
  private hu.blackbelt.epsilon.runtime.model.test1.data.util.builder.IDataBuilder<? extends hu.blackbelt.epsilon.runtime.model.test1.data.Entity> m_featureTargetBuilder;
  private Boolean m_toMany;
  // helper attributes
  private boolean m_featureNameSet = false;
  private boolean m_featureTargetSet = false;
  private boolean m_featureToManySet = false;

  /**
   * Builder is not instantiated with a constructor.
   * @see #newEntityReferenceBuilder()
   */
  private EntityReferenceBuilder() {
  }

  /**
   * This method creates a new instance of the EntityReferenceBuilder.
   * @return new instance of the EntityReferenceBuilder
   */
  public static EntityReferenceBuilder create() {
    return new EntityReferenceBuilder();
  }

  /**
   * This method can be used to override attributes of the builder. It constructs a new builder and copies the current values to it.
   */
  public EntityReferenceBuilder but() {
    EntityReferenceBuilder _builder = create();
    _builder.m_featureNameSet = m_featureNameSet;
    _builder.m_name = m_name;
    _builder.m_featureTargetSet = m_featureTargetSet;
    _builder.m_target = m_target;
    _builder.m_featureTargetBuilder = m_featureTargetBuilder;
    _builder.m_featureToManySet = m_featureToManySet;
    _builder.m_toMany = m_toMany;
    return _builder;
  }

  /**
   * This method constructs the final hu.blackbelt.epsilon.runtime.model.test1.data.EntityReference type.
   * @return new instance of the hu.blackbelt.epsilon.runtime.model.test1.data.EntityReference type
   */
  public hu.blackbelt.epsilon.runtime.model.test1.data.EntityReference build() {
    final hu.blackbelt.epsilon.runtime.model.test1.data.EntityReference _newInstance = hu.blackbelt.epsilon.runtime.model.test1.data.DataFactory.eINSTANCE.createEntityReference();
    if (m_featureNameSet) {
      _newInstance.setName(m_name);
    }
    if (m_featureTargetSet) {
      _newInstance.setTarget(m_target);
    } else {
      if (m_featureTargetBuilder != null) {
        _newInstance.setTarget(m_featureTargetBuilder.build());
      }
    }
    if (m_featureToManySet) {
      _newInstance.setToMany(m_toMany);
    }
    return _newInstance;
  }

  public EntityReferenceBuilder withName(java.lang.String p_name) {
    m_name = p_name;
    m_featureNameSet = true;
    return this;
  }

  public EntityReferenceBuilder withTarget(hu.blackbelt.epsilon.runtime.model.test1.data.Entity p_target) {
    m_target = p_target;
    m_featureTargetSet = true;
    return this;
  }

  public EntityReferenceBuilder withTarget(hu.blackbelt.epsilon.runtime.model.test1.data.util.builder.IDataBuilder<? extends hu.blackbelt.epsilon.runtime.model.test1.data.Entity> p_entityBuilder) {
    m_featureTargetBuilder = p_entityBuilder;
    return this;
  }

  public EntityReferenceBuilder withToMany(Boolean p_toMany) {
    m_toMany = p_toMany;
    m_featureToManySet = true;
    return this;
  }
}
