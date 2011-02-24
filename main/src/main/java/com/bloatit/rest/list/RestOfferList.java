package com.bloatit.rest.list;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.bloatit.framework.utils.PageIterable;
import com.bloatit.model.Offer;
import com.bloatit.rest.list.master.RestListBinder;
import com.bloatit.rest.resources.RestOffer;

/**
 * <p>
 * Wraps a list of Offer into a list of RestElements
 * </p>
 * <p>
 * This class can be represented in Xml as a list of Offer<br />
 * Example: 
 * 
 * <pre>
 * {@code <Offers>}
 *     {@code <Offer name=Offer1 />}
 *     {@code <Offer name=Offer2 />}
 * {@code </Offers>}
 * </pre>
 * <p>
 */ 
@XmlRootElement
public class RestOfferList extends RestListBinder<RestOffer, Offer> {
    /**
     * Creates a RestOfferList from a {@codePageIterable<Offer>}
     *
     * @param collection the list of elements from the model
     */
    public RestOfferList(PageIterable<Offer> collection) {
        super(collection);
    }
    
    /**
     * This method is provided only to be able to represent the list as XmL
     */
    @XmlElementWrapper(name = "offers")
    @XmlElement(name = "offer")
    public RestOfferList getOffers() {
        return this;
    }
}

