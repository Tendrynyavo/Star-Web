package fabrication;

import java.sql.Date;
import connection.*;
import composition.Composition;

public class Stock extends BddObject<Stock> {
    
/// Field
    String idStock;
    @ForeignKey (column = "idComposant", typeColumn = String.class)
    Composition composant;
    double prixUnitaire;
    double quantite;
    boolean sortie;
    Date date;
    double cump; // cout moyen pondéré de ce stock
    double valeurStock; // valeur de Stock

/// Setters
    public void setSortie(boolean sortie) { this.sortie = sortie; }
    public void setIdStock(String idStock) { this.idStock = idStock; }
    public void setComposant(Composition composant) { this.composant = composant; }
    public void setPrixUnitaire(double prixUnitaire) { this.prixUnitaire = prixUnitaire; }
    public void setQuantite(double quantite) { this.quantite = quantite; }
    public void setCump(double cump) { this.cump = cump; }
    public void setDate(Date date) { this.date = date; }
    public void setValeurStock(double valeurStock) { this.valeurStock = valeurStock; }

/// Getters
    public boolean getSortie() { return sortie; } 
    public String getIdStock() { return idStock; }
    public Composition getComposant() { return composant; }
    public double getPrixUnitaire() { return prixUnitaire; }
    public double getQuantite() { return quantite; }
    public Date getDate() { return date; }
    public double getCump() { return cump; }
    public double getValeurStock() { return valeurStock; }

/// Constructor
    public Stock() {
        setTable("stock");
        setPrefix("STO");
        setCountPK(7);
        setFunctionPK("nextval('seqstock')");
    }

    public Stock(Composition composition) {
        this();
        setComposant(composition);
    }

    public Stock(Composition composition, double quantite, boolean sortie, Date date) throws Exception {
        this();
        setIdStock(buildPrimaryKey(getPostgreSQL()));
        setComposant(composition);
        setPrixUnitaire(composition.getPrixUnitaire());
        setQuantite(quantite);
        setDate(date);
        setSortie(sortie);
    }

    public Stock(Composition composant, double prixUnitaire, double quantite, Date date, double cump, double valeurStock) {
        setComposant(composant);
        setPrixUnitaire(prixUnitaire);
        setQuantite(quantite);
        setDate(date);
        setValeurStock(valeurStock);
        setCump(cump);
    }
}