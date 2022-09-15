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
 *   A builder for the model object ' <em><b>hu.blackbelt.epsilon.runtime.model.test1.data.Attribute</b></em>'.
 * <!-- end-user-doc -->
 * 
 * @generated
 */
public class AttributeBuilder implements hu.blackbelt.epsilon.runtime.model.test1.data.util.builder.IDataBuilder<hu.blackbelt.epsilon.runtime.model.test1.data.Attribute> {
  // features and builders
  private java.lang.String m_name;
  private java.lang.String m_type;
  // helper attributes
  private boolean m_featureNameSet = false;
  private boolean m_featureTypeSet = false;

  /**
   * Builder is not instantiated with a constructor.
   * @see #newAttributeBuilder()
   */
  private AttributeBuilder() {
  }

  /**
   * This method creates a new instance of the AttributeBuilder.
   * @return new instance of the AttributeBuilder
   */
  public static AttributeBuilder create() {
    return new AttributeBuilder();
  }

  /**
   * This method can be used to override attributes of the builder. It constructs a new builder and copies the current values to it.
   */
  public AttributeBuilder but() {
    AttributeBuilder _builder = create();
    _builder.m_featureNameSet = m_featureNameSet;
    _builder.m_name = m_name;
    _builder.m_featureTypeSet = m_featureTypeSet;
    _builder.m_type = m_type;
    return _builder;
  }

  /**
   * This method constructs the final hu.blackbelt.epsilon.runtime.model.test1.data.Attribute type.
   * @return new instance of the hu.blackbelt.epsilon.runtime.model.test1.data.Attribute type
   */
  public hu.blackbelt.epsilon.runtime.model.test1.data.Attribute build() {
    final hu.blackbelt.epsilon.runtime.model.test1.data.Attribute _newInstance = hu.blackbelt.epsilon.runtime.model.test1.data.DataFactory.eINSTANCE.createAttribute();
    if (m_featureNameSet) {
      _newInstance.setName(m_name);
    }
    if (m_featureTypeSet) {
      _newInstance.setType(m_type);
    }
    return _newInstance;
  }

  public AttributeBuilder withName(java.lang.String p_name) {
    m_name = p_name;
    m_featureNameSet = true;
    return this;
  }

  public AttributeBuilder withType(java.lang.String p_type) {
    m_type = p_type;
    m_featureTypeSet = true;
    return this;
  }
}
