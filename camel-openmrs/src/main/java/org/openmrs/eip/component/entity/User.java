package org.openmrs.eip.component.entity;

import org.openmrs.eip.component.entity.light.PersonLight;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "users")
@AttributeOverride(name = "id", column = @Column(name = "user_id"))
public class User extends BaseChangeableMetaDataEntity {
	
	@NotNull
	@Column(name = "system_id")
	private String systemId;
	
	@Column(name = "username")
	private String username;
	
	@Column(name = "password")
	private String password;
	
	@Column(name = "salt")
	private String salt;
	
	@Column(name = "secret_question")
	private String secretQuestion;
	
	@NotNull
	@OneToOne
	@JoinColumn(name = "person_id")
	private PersonLight person;
	
	@Column(name = "activation_key")
	private String activationKey;
	
	@Column(name = "email")
	private String email;
}
