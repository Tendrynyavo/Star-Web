package composition;

import java.sql.Connection;
import java.sql.Date;
import java.util.ArrayList;
import fabrication.*;
import connection.BddObject;

public class Composition extends BddObject<Composition> {

    String idComposant; // ID de ce composant
    String nom;
    boolean premiere, produit;
    double prixUnitaire = 0; // Les matières première ont des prix unitaires
    double quantite = 0;
    String idComposition; // ID pour avoir la composition de ce composant
    Composition[] composants;
    static Composition[] compositions; // Tous les Compositions dans la base de donnée

// Getter
    public String getIdComposant() { return idComposant; }
    public String getNom() { return nom; }
    public boolean getPremiere() { return premiere; }
    public boolean getProduit() { return produit; }
    public String getIdComposition() { return idComposition; }
    public double getQuantite() { return quantite; }
    public double getPrixUnitaire() throws Exception {
        if (getPremiere()) return prixUnitaire;
        double somme = 0; // initialisation d'une variable somme
        for (Composition composant : decomposer())
            somme += composant.getPrixUnitaire() * composant.getQuantite();
        return somme; // return prix Unitaire de cette composition
    }
    public static Composition[] getCompositions() {
        return compositions;
    }

    // Setter
    public void setIdComposant(String idComposant) { this.idComposant = idComposant; }
    public void setNom(String nom) { this.nom = nom; }
    public void setPrixUnitaire(double prixUnitaire) throws Exception {
        if (prixUnitaire < 0) throw new Exception("Montant prix unitaire invalide");
        this.prixUnitaire = prixUnitaire;
    }
    public void setPremiere(boolean premiere) { this.premiere = premiere; }
    public void setProduit(boolean produit) { this.produit = produit; }
    public void setIdComposition(String idComposition) { this.idComposition = idComposition; }
    public void setComposants(Composition[] composants) { this.composants = composants; }
    public void setQuantite(double quantite) { this.quantite = quantite; }
    public static void setCompositions(Composition[] compositions) {
        Composition.compositions = compositions;
    }
// Constructor
    public Composition() {
        // initialisation des attributs nécessaire pour BddObject
        setTable("Composants");
        setPrefix("C");
        setCountPK(4);
        setFunctionPK("nextval('seqcomposants')");
    }

    public Composition(String nom, boolean premiere, boolean produit, double prixUnitaire) throws Exception {
        this();
        setIdComposant(buildPrimaryKey(getPostgreSQL()));
        setNom(nom);
        setPremiere(premiere);
        setProduit(produit);
        setPrixUnitaire(prixUnitaire);
    }

// Function

    public Composition[] getAllCompositions() throws Exception {
        Composition composition = new Composition();
        composition.setTable("Melange"); // Melange est un VIEW dans la base qui retourne toutes les compositions
        Composition[] composants = composition.getData(BddObject.getPostgreSQL(), null);
        return composants;
    }

    public Composition[] decomposer() throws Exception {
        if (getCompositions() == null) setCompositions(getAllCompositions());
        ArrayList<Composition> composants = new ArrayList<Composition>(); // variable pour conserver les composants de cette compisition
        for (Composition composition : getCompositions()) {
            if (composition.getIdComposition().equals(getIdComposant()))
                composants.add(composition);
        }
        return composants.toArray(new Composition[composants.size()]);
    }

    public static Composition[] getProduits() throws Exception {
        Composition composition = new Composition();
        composition.setTable("Produit"); // VIEW pour avoir tous les produits
        return composition.getData(BddObject.getPostgreSQL(), null);
    }
    
/// Fonction recursive pour inserer les matieres premieres
    public void construct(double quantite, Connection connection) throws Exception {
        if (getPremiere()) {
            new Stock(this, quantite, true, new Date(System.currentTimeMillis())).insert(connection);
            return;
        }
        for (Composition composition : decomposer())
            composition.construct(quantite * composition.getQuantite(), connection);
        if (getProduit()) new Stock(this, quantite, false, new Date(System.currentTimeMillis())).insert(connection);
    }

/// Fonction pour inserer tous les sorties de ce produit: C'est une fonction seulement pour les produits
    public void fabriquer(double quantite) throws Exception {
        if (!getProduit()) throw new Exception("Ce n'est pas un produit");
        Connection connection = null;
        try {
            connection =  BddObject.getPostgreSQL();
            construct(quantite, connection);
            connection.commit();
        } catch (Exception e) {
            connection.rollback();
            throw e;
        } finally {
            connection.close();
        }
    }

/// Fonction pour ajouter des entrées en matiere premiere: S'applique seulement au matiere premiere
    public void add(double quantite) throws Exception {
        if (!getPremiere()) throw new Exception("Ce n'est pas une matière première");
        new Stock(this, quantite, false, new Date(System.currentTimeMillis())).insert(null);
    }

/// Fonction pour prendre les stocks de cette composition
    public Stock[] getStock() throws Exception {
        Stock[] stocks = new Stock(this).getData(getPostgreSQL(), "date", "composant");
        double total = 0; // variable de somme de tous les qtes dans le stocks
        double cump = 0; // initialisation du CUMP
        for (int n = 0; n < stocks.length; n++) {
            if (!stocks[n].getSortie()) {
                cump = ((total * cump) + (stocks[n].getPrixUnitaire() * stocks[n].getQuantite())) / (total + stocks[n].getQuantite());
                total += stocks[n].getQuantite();
            } else {
                total -= stocks[n].getQuantite();
            }
            stocks[n].setCump(cump);
            stocks[n].setValeurStock(cump * total);
        }
        return stocks;
    }

    public double getValeurStock() throws Exception {
        Stock[] stocks = getStock();
        return (stocks.length > 0) ? stocks[stocks.length - 1].getValeurStock() : 0;
    }
}