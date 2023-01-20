package connection;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD) // Pour les fields
public @interface ForeignKey {
    
/// Field
    String column(); // colonne qui represente le primary key de la table référencé
    Class<?> typeColumn();

}