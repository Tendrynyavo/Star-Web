CREATE TABLE stock (
    idStock VARCHAR PRIMARY KEY,
    idComposant VARCHAR REFERENCES composants (idcomposant),
    prixunitaire DOUBLE PRECISION NOT NULL DEFAULT 0,
    quantite DOUBLE PRECISION NOT NULL DEFAULT 0,
    sortie BOOLEAN,
    date DATE
);

CREATE SEQUENCE seqstock 
START WITH 1
INCREMENT BY 1

INSERT INTO stock (idstock, idcomposant, prixunitaire, quantite, sortie, date) VALUES
    ('STO0001', 'C005', 2, 20, false, '2023/01/19'),
    ('STO0002', 'C005', 2, 12, true, '2023/01/19'),
    ('STO0003', 'C005', 3.4, 20, false, '2023/01/20'),
    ('STO0004', 'C005', 3, 10, true, '2023/01/20');

-- Afficher l'etat de stock des entrees des produits
SELECT s.idcomposant, sum(quantite) as entree
FROM stock s
    JOIN composants c ON s.idcomposant = c.idcomposant
WHERE sortie = false AND c.produit = true
GROUP BY s.idcomposant;

-- Afficher l'etat de stock des sorties des produits
SELECT s.idcomposant, sum(quantite) as sortie
FROM stock s
    JOIN composants c ON s.idcomposant = c.idcomposant
WHERE sortie = true AND c.produit = true
GROUP BY s.idcomposant;

-- View Entree
CREATE VIEW entree AS 
SELECT *
FROM stock
WHERE sortie = false;

-- View Entree Produit
CREATE VIEW entree_produit AS 
SELECT *
FROM stock
WHERE sortie = false AND idcomposant IN (
    SELECT idcomposant
    FROM produit
);

-- View Sortie
CREATE VIEW sortie AS 
SELECT *
FROM stock
WHERE sortie = true;

-- View Sortie Produit
CREATE VIEW sortie_produit AS 
SELECT *
FROM stock
WHERE sortie = true AND idcomposant IN (
    SELECT idcomposant
    FROM produit
);

-- View pour afficher l'etat de stock entree
SELECT idcomposant, sum(quantite) as entree
FROM entree_produit
GROUP BY idcomposant;

-- View pour afficher l'etat de stock sortie
SELECT idcomposant, sum(quantite) as sortie
FROM sortie_produit
GROUP BY idcomposant;

-- View pour initialiser les produits entrees
CREATE VIEW entree_initial AS
SELECT idcomposant, 0 as entree
FROM produit;

CREATE TABLE reporte (
    idReporte VARCHAR PRIMARY KEY,
    date DATE NOT NULL
);

CREATE TABLE reporte_produit (
    idReporte VARCHAR REFERENCES reporte (idReporte),
    idComposant VARCHAR REFERENCES composants (idComposant),
    quantite DOUBLE PRECISION
);

CREATE SEQUENCE seqreporte 
START WITH 1
INCREMENT BY 1;

CREATE VIEW composants_reporte AS
SELECT r.idComposant, nom, premiere, produit, prixunitaire, quantite, idReporte as idComposition
FROM reporte_produit r
    JOIN composants c on r.idComposant = c.idcomposant;

insert into stock (idstock, idcomposant, quantite, prixunitaire, sortie, date)
values  ('STO0001', 'C005', 90, 0, true, '2022-01-24'),
        ('STO0002', 'C005', 30, 0, true, '2022-01-26'),
        ('STO0003', 'C005',  20, 0,false, '2022-01-25'),
        ('STO0004', 'C005', 10, 0, true, '2022-01-24'),
        ('STO0005', 'C005',  40, 0,false, '2022-01-27'),
        ('STO0006', 'C005',  80, 0,false, '2022-01-31'),
        ('STO0007', 'C005', 70, 0, true, '2022-01-30'),
        ('STO0008', 'C005',  60, 0,false, '2022-01-29'),
        ('STO0009', 'C005', 50, 0, true, '2022-01-28'),
        ('STO0010', 'C005', 90, 0, true, '2022-02-01'),
        ('STO0011', 'C005',  100, 0,false, '2022-02-02');