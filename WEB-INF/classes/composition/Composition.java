package composition;

import java.sql.Connection;
import java.sql.Date;
import java.util.ArrayList;
import connection.BddObject;
import magasin.Magasin;
import stock.*;

public class Composition extends BddObject<Composition> {

/// FIELD
    String idComposant; // ID de ce composant
    String nom;
    boolean premiere, produit;
    double prixUnitaire = 0; // Les matières première ont des prix unitaires
    double quantite = 0;
    String idComposition; // ID pour avoir la composition de ce composant
    Composition[] composants;
    static Composition[] compositions; // Tous les Compositions dans la base de donnée
    Stock[] stocks;

/// GETTER
    public Stock[] getStocks() { return stocks; }
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
    public static Composition[] getCompositions() { return compositions; }

/// SETTER
    public void setStocks(Stock[] stocks) { this.stocks = stocks; }
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
    public void setQuantite(double quantite) throws Exception { 
        if (quantite < 0) throw new Exception("Quantite invalide");
        this.quantite = quantite;
    }
    public static void setCompositions(Composition[] compositions) {
        Composition.compositions = compositions;
    }

/// CONSTRUCTOR
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

/// Function
    public Composition[] decomposer() throws Exception {
        if (getCompositions() == null) setCompositions(getCompositions("Melange"));
        ArrayList<Composition> composants = new ArrayList<Composition>(); // variable pour conserver les composants de cette compisition
        for (Composition composition : getCompositions()) {
            if (composition.getIdComposition().equals(getIdComposant()))
                composants.add(composition);
        }
        return composants.toArray(new Composition[composants.size()]);
    }
    
/// Fonction recursive pour inserer les matieres premieres
    public void construct(double quantite, Connection connection) throws Exception {
        if (getPremiere()) {
            if (getQuantiteStock(getAllStocks()) < quantite) throw new Exception(this.getNom() + " insuffisant pour la production");
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
            connection = BddObject.getPostgreSQL();
            construct(quantite, connection);
            connection.commit();
        } catch (Exception e) {
            connection.rollback();
            throw e;
        } finally {
            connection.close();
        }
    }

    public static Stock[] getAllStocks() throws Exception {
        return new Stock().getData(getPostgreSQL(), "date");
    }

/// Fonction pour ajouter des matières premières dans le stock
    public void add(double quantite) throws Exception {
        if (!getPremiere()) throw new Exception("Ce n'est pas une matière première");
        new Stock(this, quantite, false, new Date(System.currentTimeMillis())).insert(null);
        
    }

    public Stock[] getStocksById(Stock[] stocks) throws Exception {
        ArrayList<Stock> array = new ArrayList<Stock>();
        for (Stock stock : stocks) {
            if (stock.getComposant().getIdComposant().equals(this.getIdComposant()))
                array.add(stock);
        }
        return array.toArray(new Stock[array.size()]);
    }

/// Fonction pour prendre les stocks de cette composition
    public Stock[] getStockWithCump(Stock[] initial) throws Exception {
        Stock[] stocks = getStocksById(initial);
        double total = 0; // variable de somme de tous les qtes dans le stocks
        double cump = 0; // initialisation du CUMP
        for (Stock stock : stocks) {
            if (!stock.getSortie()) {
                cump = ((total * cump) + (stock.getPrixUnitaire() * stock.getQuantite())) / (total + stock.getQuantite());
                total += stock.getQuantite();
            } else {
                total -= stock.getQuantite();
            }
            stock.setCump(cump);
            stock.setValeurStock(cump * total);
        }
        setStocks(stocks);
        return stocks;
    }

    public double round(double number) {
        return (double) Math.round(number * 100) / 100;
    }

    public double getValeurStock(Stock[] allStocks) throws Exception {
        Stock[] stocks = (getStocks() == null) ? getStockWithCump(allStocks) : getStocks();
        return round(Math.abs((stocks.length > 0) ? stocks[stocks.length - 1].getValeurStock() : 0));
    }

    public double getCump(Stock[] allStocks) throws Exception {
        Stock[] stocks = (getStocks() == null) ? getStockWithCump(allStocks) : getStocks();
        return round(Math.abs((stocks.length > 0) ? stocks[stocks.length - 1].getCump() : 0));
    }

    public double getQuantiteStock(Stock[] allStocks) throws Exception {
        Stock[] stocks = (getStocks() == null) ? getStockWithCump(allStocks) : getStocks();
        return round(Math.abs(Math.abs((stocks.length > 0 && stocks[stocks.length - 1].getCump() != 0) ? stocks[stocks.length - 1].getValeurStock() / stocks[stocks.length - 1].getCump() : 0)));
    }

/// Fonction pour prendre des données des tables
    static Composition[] getCompositions(String table) throws Exception {
        Composition composition = new Composition();
        composition.setTable(table); // VIEW pour avoir tous les produits
        return composition.getData(BddObject.getPostgreSQL(), null);
    }

    public static Composition[] getProduits() throws Exception {
        return getCompositions("produit");
    }

    public static Composition[] getMatierePremiere() throws Exception {
        return getCompositions("matiere");
    }

    public static Composition[] getProduitMatiere() throws Exception {
        return getCompositions("produitmatiere");
    }

    public static Composition getCompositionById(String id) throws Exception {
        return getCompositions("composants WHERE idcomposant='" + id + "'")[0];
    }

    public Stock[] getStocks(String type, Magasin magasin) throws Exception {
        Stock stock = new Stock(this);
        stock.setTable(type + " WHERE idcomposant='" + this.getIdComposant() + "' AND \"idmagasin\" = '"+magasin.getIdMagasin()+"'");
        return stock.getData(getPostgreSQL(), null);
    }

    public EtatStock getEtatStock(Stock[] entrees, Stock[] sorties) throws Exception {
        double entree = 0, sortie = 0;
        for (int i = 0; i < entrees.length; i++) {
            entree += entrees[i].getQuantite();
        }
        for (int i = 0; i < sorties.length; i++) {
            sortie += sorties[i].getQuantite();
        }
        return new EtatStock(this, entree, sortie);
    }

    public EtatStock getEtatStock(Magasin magasin) throws Exception {
        return getEtatStock(getStocks("entree", magasin), getStocks("sortie", magasin));
    }

    public EtatStock getEtatStock(Date date, Magasin magasin) throws Exception {
        return getEtatStock(getStocksDate("entree", date, magasin), getStocksDate("sortie", date, magasin));
    }

    public EtatStock getEtatStock(Date date, Reporte reporte, Magasin magasin) throws Exception {
        EtatStock etat = getEtatStock(getStocksOptimise("entree", date, reporte, magasin), getStocksOptimise("sortie", date, reporte, magasin));
        etat.setEntree(etat.getEntree() + reporte.find(this).getQuantite());
        return etat;
    }

    public Stock[] getStocksDate(String type, Date date, Magasin magasin) throws Exception {
        Stock stock = new Stock(this);
        stock.setTable(type + " WHERE idcomposant='" + this.getIdComposant() + "' AND date <= '" + date + "' AND \"idmagasin\" = '"+magasin.getIdMagasin()+"'");
        return stock.getData(getPostgreSQL(), null);
    }

    public Stock[] getStocksOptimise(String type, Date date, Reporte reporte, Magasin magasin) throws Exception {
        Stock stock = new Stock(this);
        stock.setTable(type + " WHERE idcomposant='" + this.getIdComposant() + "' AND date <= '" + date + "' AND  date >= '" + reporte.getDate() + "' AND \"idmagasin\" = '"+magasin.getIdMagasin()+"'");
        return stock.getData(getPostgreSQL(), null);
    }
}