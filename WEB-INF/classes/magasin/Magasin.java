package magasin;

import java.sql.Date;

import composition.Composition;
import connection.BddObject;
import stock.EtatStock;
import stock.Reporte;

public class Magasin extends BddObject<Magasin> {
    
/// FIELD
    String idMagasin;
    String nom;
    Composition[] produits;

/// SETTERS
    public void setIdMagasin(String idMagasin) {
        this.idMagasin = idMagasin;
    }
    public void setNom(String nom) {
        this.nom = nom;
    }
    public void setProduits(Composition[] produits) {
        this.produits = produits;
    }

/// GETTERS
    public String getIdMagasin() {
        return idMagasin;
    }
    public String getNom() {
        return nom;
    }
    public Composition[] getProduits() {
        return produits;
    }

/// CONSTRUCTORS
    public Magasin() throws Exception {
        setProduits(Composition.getProduits());
        setTable("magasin");
    }

    public Magasin(String id) throws Exception {
        setIdMagasin(id);
        setProduits(Composition.getProduits());
    }

/// FUNCTIONS

    public EtatStock[] getEtatStock() throws Exception {
        EtatStock[] etats = new EtatStock[getProduits().length];
        for (int i = 0; i < etats.length; i++)
            etats[i] = getProduits()[i].getEtatStock(this);
        return etats;
    }

    public EtatStock[] getEtatStock(String dateString) throws Exception {
        Date date = Date.valueOf(dateString);
        EtatStock[] etats = new EtatStock[getProduits().length];
        for (int i = 0; i < etats.length; i++)
            etats[i] = getProduits()[i].getEtatStock(date, this);
        return etats;
    }

    public EtatStock[] getEtatStockOptimisee(String dateString) throws Exception {
        Date date = Date.valueOf(dateString);
        Reporte reporte = Reporte.getReporte(date, this);
        EtatStock[] etats = new EtatStock[getProduits().length];
        for (int i = 0; i < etats.length; i++)
            etats[i] = getProduits()[i].getEtatStock(date, reporte, this);
        return etats;
    }
}