package com.bloatit.data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MappedSuperclass;

import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Indexed;

/**
 * Base class to use with Hibernate. (A persistent class do not need to inherit from
 * DaoIdentifiable) When using DaoIdentifiable as a superClass, you ensure to have a id
 * column in your table. There is no DaoIdentifiable Table.
 */
@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Indexed
public abstract class DaoIdentifiable implements IdentifiableInterface {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @DocumentId
    private Integer id;

    @Override
    public final Integer getId() {
        return id;
    }

    // ======================================================================
    // For hibernate mapping
    // ======================================================================

    protected DaoIdentifiable() {
        super();
    }

    // ======================================================================
    // equals and hashcode
    // ======================================================================

    @Override
    public abstract int hashCode();

    @Override
    public abstract boolean equals(Object obj);

}