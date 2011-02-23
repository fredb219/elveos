package com.bloatit.rest.resources;

import com.bloatit.rest.RestElement;
import com.bloatit.model.Description;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import com.bloatit.rest.utils.RestList;
import com.bloatit.framework.rest.RestServer.RequestMethod;
import com.bloatit.framework.rest.annotations.REST;

@XmlRootElement
public class RestDescription extends RestElement{ 
    private Description model;

    protected RestDescription(Description model){
        this.model=model;
    }

    @REST(name = "descriptions", method = RequestMethod.GET)
    public static RestDescription getById(int id){
        //TODO auto generated code
        return null;
    }

    @REST(name = "descriptions", method = RequestMethod.GET)
    public static RestList<RestDescription> getAll(){
        //TODO auto generated code
        return null;
    }

    Description getModel(){
        return model;
    }
}
