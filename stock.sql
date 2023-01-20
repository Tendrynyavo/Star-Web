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