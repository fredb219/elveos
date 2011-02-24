package com.bloatit.rest.resources;


import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import com.bloatit.rest.list.RestDescriptionList;
import com.bloatit.framework.rest.RestServer.RequestMethod;
import com.bloatit.framework.rest.annotations.REST;
import com.bloatit.framework.rest.RestElement;
import com.bloatit.model.Description;

/**
* <p>
* Representation of a Description for the ReST RPC calls
* </p>
* <p>
 * This class should implement any methods from Description that needs to be called
* through the ReST RPC. Every such method needs to be mapped with the
* {@code @REST} interface.
* <p>
* ReST uses the four HTTP request methods <code>GET</code>, <code>POST</code>,
* <code>PUT</code>, <code>DELETE</code> each with their own meaning. Please
* only bind the according to the following:
* <li>GET list: List the URIs and perhaps other details of the collection's
* members.</li>
* <li>GET list/id: Retrieve a representation of the addressed member of the
* collection, expressed in an appropriate Internet media type.</li>
* <li>POST list: Create a new entry in the collection. The new entry's URL is
* assigned automatically and is usually returned by the operation.</li>
* <li>POST list/id: Treat the addressed member as a collection in its own right
* and create a new entry in it.</li>
* <li>PUT list: Replace the entire collection with another collection.</li>
* <li>PUT list/id: Replace the addressed member of the collection, or if it
* doesn't exist, create it.</li>
* <li>DELETE list: Delete the entire collection.</li>
* <li>DELETE list/id: Delete the addressed member of the collection.</li>
* </p>
* </p>
* <p>
* This class will be serialized as XML (or maybe JSON who knows) to be sent
* over to the client RPC. Hence this class needs to be annotated to indicate
* which methods (and/or fields) are to be matched in the XML data. For this
* use:
* <li>@XmlRootElement at the root of the class</li>
* <li>@XmlElement on each method/attribute that will yield <i>complex</i> data</li>
* <li>@XmlAttribute on each method/attribute that will yield <i>simple</i> data
* </li>
* <li>Methods that return a list need to be annotated with @XmlElement and to
* return a RestDescriptionList</li>
* </p>
*/
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class RestDescription extends RestElement<Description>{ 
    private Description model;

    /**
     * Provided for JAXB 
     */
    @SuppressWarnings("unused")
    private RestDescription() {
    }

    protected RestDescription(Description model){
        this.model=model;
    }

    /**
     * <p>
     * Finds the RestDescription matching the <code>id</code> 
     * </p>
     * @param id the id of the RestDescription
     */
    @REST(name = "descriptions", method = RequestMethod.GET)
    public static RestDescription getById(int id){
        // TODO auto generated code
        // RestDescription restDescription = DescriptionManager.getById(id); 
        // if(restDescription == null) { 
        //     return null;
        // }
        return null;
    }

    /**
     * <p>
     * Finds the list of all (valid) RestDescription 
     * </p>
     */
    @REST(name = "descriptions", method = RequestMethod.GET)
    public static RestDescriptionList getAll(){
        //TODO auto generated code
        return null;
    }

    /**
     * Provided for JAXB 
     */
     void setModel(Description model){
        this.model = model;
    }

    /**
     * Package method to find the model
     */
    Description getModel(){
        return model;
    }

    @XmlAttribute
    @XmlID
    public String getId(){
        return model.getId().toString();
    }

    @Override    public boolean isNull(){
        return (model == null);
    }

}
