/**
 */
package hu.blackbelt.epsilon.runtime.model.test1.data;

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
