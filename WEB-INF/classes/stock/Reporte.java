package stock;

import java.sql.Date;

import composition.Composition;
import connection.BddObject;
import connection.ForeignKey;
import magasin.Magasin;

public class Reporte extends BddObject<Reporte> {
    
/// FIELD
    String idReporte;
    Date date;
    @ForeignKey(column = "idMagasin", typeColumn = String.class)
    Magasin magasin;
    Composition[] produits;

/// SETTERS
    public void setIdReporte(String idReporte) {
        this.idReporte = idReporte;
    }
    public void setDate(Date date) {
        this.date = date;
    }
    public void setMagasin(Magasin magasin) {
        this.magasin = magasin;
    }
    public void setProduits(Composition[] produits) {
        this.produits = produits;
    }

/// GETTERS
    public String getIdReporte() {
        return idReporte;
    }
    public Date getDate() {
        return date;
    }
    public Composition[] getProduits() {
        return produits;
    }
    public void setProduits() throws Exception {
        Composition compo = new Composition();
        compo.setTable("composants_reporte");
        compo.setIdComposition(getIdReporte());
        setProduits(compo.getData(getPostgreSQL(), null, "idComposition"));
    }

/// CONSTRUCTOR
    public Reporte() {
        setTable("reporte");
        setCountPK(7);
        setPrefix("REP");
        setFunctionPK("nextval('seqreporte')");
    }

    public static Reporte getReporte(Date date, Magasin magasin) throws Exception {
        Reporte reporte = new Reporte();
        reporte.setTable(reporte.getTable() + " WHERE date <= '" + date + "' AND \"idmagasin\" = '"+magasin.getIdMagasin()+"'");
        Reporte[] r = reporte.getData(BddObject.getPostgreSQL(), "date DESC LIMIT 1");
        if (r.length <= 0) throw new Exception("Pas de reporte à cette date " + date);
        r[0].setProduits();
        return r[0];
    }

    public Composition find(Composition produit) throws Exception {
        for (Composition composition : getProduits()) {
            if (produit.getIdComposant().equals(composition.getIdComposant()))
                return composition;
        }
        throw new Exception(produit.getNom() + " n'a pas de report");
    }
}