/*
 * Copyright 2005-2008 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.kns.util;

import java.beans.Beans;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

import org.apache.commons.lang.ArrayUtils;

/**
 * This class is used to maintain an ArrayList for a specified object type.
 * 
 * 
 */
public class TypedArrayList extends ArrayList {
    private static final long serialVersionUID = 6238521951259126730L;
    private final Class listObjectType;


    public TypedArrayList(Class listObjectType) {
        super();

        if (listObjectType == null) {
            throw new RuntimeException("class type for list is required.");
        }
        
        //the following code checks to see if the class has a visible default constructor
        
        boolean foundDefaultConstructor = false;
        
        Constructor[] constructors = listObjectType.getConstructors();
        for (Constructor constructor : constructors) {
            Class[] parameterType = constructor.getParameterTypes();
            
            //check for a constructor that takes no arguments and is public
            if(ArrayUtils.isEmpty(parameterType) &&
               Modifier.isPublic(constructor.getModifiers())) {
                foundDefaultConstructor = true;
                break;
            }
        }
        
        if(!foundDefaultConstructor) {
            throw new RuntimeException("unable to get default constructor for " + listObjectType.getName());
        }
        this.listObjectType = listObjectType;
        
    }

    /**
     * @see java.util.List#add(int, java.lang.Object)
     */
    public void add(int index, Object element) {
        checkType(element);
        super.add(index, element);
    }

    /**
     * @see java.util.Collection#add(java.lang.Object)
     */
    public boolean add(Object o) {
        checkType(o);
        return super.add(o);
    }

    /**
     * @see java.util.List#get(int)
     */
    public Object get(int index) {
        growArray(index);
        return super.get(index);
    }

    /**
     * @see java.util.List#set(int, java.lang.Object)
     */
    public Object set(int index, Object element) {
        growArray(index);
        return super.set(index, element);
    }


    /**
     * Adds new instances of type listObjectType to the arraylist until the size of the list is greater than the index required.
     */
    private void growArray(int index) {
        if (index < 0) {
            throw new IndexOutOfBoundsException("Index must be positive.");
        }
        // ensureCapacity(index); // Increments modCount

        while (size() <= index) {
            try {
                super.add(listObjectType.newInstance());
            }
            catch (InstantiationException e) {
                throw new RuntimeException("Cannot get new instance of class " + listObjectType.getName());
            }
            catch (IllegalAccessException e) {
                throw new RuntimeException("Cannot get new instance of class " + listObjectType.getName());
            }
        }
    }


    /**
     * Checks the type of an element matches the underlying list type.
     */
    private void checkType(Object element) {
        if (element != null) {
            if (!Beans.isInstanceOf(element, listObjectType)) {
                throw new RuntimeException("element is not of correct type.");
            }
        }
    }

    /**
     * @return Returns the listObjectType.
     */
    public Class getListObjectType() {
        return listObjectType;
    }

}