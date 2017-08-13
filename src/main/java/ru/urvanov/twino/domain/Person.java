package ru.urvanov.twino.domain;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@RooJavaBean
@RooToString
@RooJpaActiveRecord(table = "person")
public class Person {

    /**
     */
    @NotNull
    @Size(min = 1, max = 100)
    private String surname;

    /**
     */
    @NotNull
    @Size(min = 1, max = 100)
    private String name;

    /**
     */
    @NotNull
    private Boolean enabled = true;
}
