package stock;

import composition.Composition;
import connection.BddObject;
import connection.ForeignKey;

public class EtatStock extends BddObject<EtatStock> {
    
/// FIELD
    @ForeignKey(column = "idComposant", typeColumn = String.class)
    Composition produit;
    double entree;
    double sortie;

/// SETTERS
    public void setProduit(Composition produit) { 
        this.produit = produit;
    }
    public void setEntree(double entree) throws Exception {
        if (entree < 0) throw new Exception("Entree Invalide");
        this.entree = entree;
    }
    public void setSortie(double sortie) throws Exception {
        if (sortie < 0) throw new Exception("Sortie Invalide");
        this.sortie = sortie;
    }

/// GETTERS
    public Composition getProduit() {
        return produit;
    }
    public double getEntree() {
        return entree;
    }
    public double getSortie() {
        return sortie;
    }
    public double getReste() {
        return getEntree() - getSortie();
    }
    public double getValeur() throws Exception { 
        return getReste() * getProduit().getPrixUnitaire(); 
    }

/// CONSTRUCTORS
    public EtatStock(Composition produit, double entree, double sortie) throws Exception {
        setProduit(produit);
        setEntree(entree);
        setSortie(sortie);
    }
}
