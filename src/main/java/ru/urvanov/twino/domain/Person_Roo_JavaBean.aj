// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package ru.urvanov.twino.domain;

import ru.urvanov.twino.domain.Person;

privileged aspect Person_Roo_JavaBean {
    
    public String Person.getSurname() {
        return this.surname;
    }
    
    public void Person.setSurname(String surname) {
        this.surname = surname;
    }
    
    public String Person.getName() {
        return this.name;
    }
    
    public void Person.setName(String name) {
        this.name = name;
    }
    
    public Boolean Person.getEnabled() {
        return this.enabled;
    }
    
    public void Person.setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
    
}
