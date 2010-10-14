package com.bloatit.model.data;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

@Entity
@MappedSuperclass
public class Kudos extends UserContent {

	@Basic(optional = false)
	private int value;
	
	// For dbMapping only
	@ManyToOne
	private Kudosable kudosable;

	public Kudos() {
	}

	public Kudos(Member member, int value) {
		super(member);
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}
