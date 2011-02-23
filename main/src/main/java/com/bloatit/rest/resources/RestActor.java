package com.bloatit.rest.resources;

import com.bloatit.rest.RestElement;
import com.bloatit.model.Actor;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import com.bloatit.rest.utils.RestList;
import com.bloatit.framework.rest.RestServer.RequestMethod;
import com.bloatit.framework.rest.annotations.REST;

@XmlRootElement
public class RestActor extends RestElement{ 
    private Actor model;

    protected RestActor(Actor model){
        this.model=model;
    }

    @REST(name = "actors", method = RequestMethod.GET)
    public static RestActor getById(int id){
        //TODO auto generated code
        return null;
    }

    @REST(name = "actors", method = RequestMethod.GET)
    public static RestList<RestActor> getAll(){
        //TODO auto generated code
        return null;
    }

    Actor getModel(){
        return model;
    }
}
