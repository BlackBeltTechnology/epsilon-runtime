package hu.blackbelt.epsilon.runtime.model.test1.data.util.builder;

/**
 * <!-- begin-user-doc --> 
 *   A builder for the model object ' <em><b>hu.blackbelt.epsilon.runtime.model.test1.data.Entity</b></em>'.
 * <!-- end-user-doc -->
 * 
 * @generated
 */
public class EntityBuilder implements hu.blackbelt.epsilon.runtime.model.test1.data.util.builder.IDataBuilder<hu.blackbelt.epsilon.runtime.model.test1.data.Entity> {
  // features and builders
  private java.lang.String m_name;
  private java.util.Collection<hu.blackbelt.epsilon.runtime.model.test1.data.Attribute> m_attribute = new java.util.LinkedList<hu.blackbelt.epsilon.runtime.model.test1.data.Attribute>();
  private java.util.Collection<hu.blackbelt.epsilon.runtime.model.test1.data.util.builder.IDataBuilder<? extends hu.blackbelt.epsilon.runtime.model.test1.data.Attribute>> m_featureAttributeBuilder = new java.util.LinkedList<hu.blackbelt.epsilon.runtime.model.test1.data.util.builder.IDataBuilder<? extends hu.blackbelt.epsilon.runtime.model.test1.data.Attribute>>();
  private java.util.Collection<hu.blackbelt.epsilon.runtime.model.test1.data.EntityReference> m_reference = new java.util.LinkedList<hu.blackbelt.epsilon.runtime.model.test1.data.EntityReference>();
  private java.util.Collection<hu.blackbelt.epsilon.runtime.model.test1.data.util.builder.IDataBuilder<? extends hu.blackbelt.epsilon.runtime.model.test1.data.EntityReference>> m_featureReferenceBuilder = new java.util.LinkedList<hu.blackbelt.epsilon.runtime.model.test1.data.util.builder.IDataBuilder<? extends hu.blackbelt.epsilon.runtime.model.test1.data.EntityReference>>();
  // helper attributes
  private boolean m_featureAttributeSet = false;
  private boolean m_featureNameSet = false;
  private boolean m_featureReferenceSet = false;

  /**
   * Builder is not instantiated with a constructor.
   * @see #newEntityBuilder()
   */
  private EntityBuilder() {
  }

  /**
   * This method creates a new instance of the EntityBuilder.
   * @return new instance of the EntityBuilder
   */
  public static EntityBuilder create() {
    return new EntityBuilder();
  }

  /**
   * This method can be used to override attributes of the builder. It constructs a new builder and copies the current values to it.
   */
  public EntityBuilder but() {
    EntityBuilder _builder = create();
    _builder.m_featureAttributeSet = m_featureAttributeSet;
    _builder.m_attribute = m_attribute;
    _builder.m_featureAttributeBuilder = m_featureAttributeBuilder;
    _builder.m_featureNameSet = m_featureNameSet;
    _builder.m_name = m_name;
    _builder.m_featureReferenceSet = m_featureReferenceSet;
    _builder.m_reference = m_reference;
    _builder.m_featureReferenceBuilder = m_featureReferenceBuilder;
    return _builder;
  }

  /**
   * This method constructs the final hu.blackbelt.epsilon.runtime.model.test1.data.Entity type.
   * @return new instance of the hu.blackbelt.epsilon.runtime.model.test1.data.Entity type
   */
  public hu.blackbelt.epsilon.runtime.model.test1.data.Entity build() {
    final hu.blackbelt.epsilon.runtime.model.test1.data.Entity _newInstance = hu.blackbelt.epsilon.runtime.model.test1.data.DataFactory.eINSTANCE.createEntity();
    if (m_featureNameSet) {
      _newInstance.setName(m_name);
    }
    if (m_featureAttributeSet) {
      _newInstance.getAttribute().addAll(m_attribute);
    } else {
      if (!m_featureAttributeBuilder.isEmpty()) {
        for (hu.blackbelt.epsilon.runtime.model.test1.data.util.builder.IDataBuilder<? extends hu.blackbelt.epsilon.runtime.model.test1.data.Attribute> builder : m_featureAttributeBuilder) {
          _newInstance.getAttribute().add(builder.build());
        }
      }
    }
    if (m_featureReferenceSet) {
      _newInstance.getReference().addAll(m_reference);
    } else {
      if (!m_featureReferenceBuilder.isEmpty()) {
        for (hu.blackbelt.epsilon.runtime.model.test1.data.util.builder.IDataBuilder<? extends hu.blackbelt.epsilon.runtime.model.test1.data.EntityReference> builder : m_featureReferenceBuilder) {
          _newInstance.getReference().add(builder.build());
        }
      }
    }
    return _newInstance;
  }

  public EntityBuilder withName(java.lang.String p_name) {
    m_name = p_name;
    m_featureNameSet = true;
    return this;
  }

  public EntityBuilder withAttribute(hu.blackbelt.epsilon.runtime.model.test1.data.Attribute p_attribute) {
    m_attribute.add(p_attribute);
    m_featureAttributeSet = true;
    return this;
  }

  public EntityBuilder withAttribute(java.util.Collection<? extends hu.blackbelt.epsilon.runtime.model.test1.data.Attribute> p_attribute) {
    m_attribute.addAll(p_attribute);
    m_featureAttributeSet = true;
    return this;
  }

  public EntityBuilder withAttribute(hu.blackbelt.epsilon.runtime.model.test1.data.util.builder.IDataBuilder<? extends hu.blackbelt.epsilon.runtime.model.test1.data.Attribute> p_attributeBuilder) {
    m_featureAttributeBuilder.add(p_attributeBuilder);
    return this;
  }

  public EntityBuilder withReference(hu.blackbelt.epsilon.runtime.model.test1.data.EntityReference p_reference) {
    m_reference.add(p_reference);
    m_featureReferenceSet = true;
    return this;
  }

  public EntityBuilder withReference(java.util.Collection<? extends hu.blackbelt.epsilon.runtime.model.test1.data.EntityReference> p_reference) {
    m_reference.addAll(p_reference);
    m_featureReferenceSet = true;
    return this;
  }

  public EntityBuilder withReference(
      hu.blackbelt.epsilon.runtime.model.test1.data.util.builder.IDataBuilder<? extends hu.blackbelt.epsilon.runtime.model.test1.data.EntityReference> p_entityReferenceBuilder) {
    m_featureReferenceBuilder.add(p_entityReferenceBuilder);
    return this;
  }
}
