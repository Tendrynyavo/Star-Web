package connection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Vector;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;

@SuppressWarnings("unchecked")
public abstract class BddObject<T extends BddObject<?>> {

/// Field
    String prefix; // Prefix de L'ID de cette Object
    String functionPK; // fonction PlSQL pour prendre la sequence
    String table; // table de cette object
    Class<T> typeClass; // type de l'object
    int countPK; // nombre de caractere de l'ID

/// Getters
    public String getPrefix() { return prefix; }
    public String getTable() throws NullPointerException {
        if (table == null) throw new NullPointerException("Pas de table pour l'object");
        return table;
    }
    public String getFunctionPK() throws NullPointerException {
        if (functionPK == null) throw new NullPointerException("Pas de fonction de sequence pour l'object");
        return functionPK;
    }
    public int getCountPK() { return countPK; }
    public Class<T> getTypeClass() { return typeClass; }
    
/// Setters
    public void setTable(String table) { this.table = table; }
    public void setCountPK(int countPK) throws IllegalArgumentException {
        if (countPK < 0) throw new IllegalArgumentException("Count ne doit pas etre négative");
        this.countPK = countPK;
    }
    public void setFunctionPK(String function) { this.functionPK = function; }
    public void setPrefix(String prefix) { this.prefix = prefix; }
    public void setTypeClass(Class<T> typeClass) { this.typeClass = typeClass; }

/// Constructor
    public BddObject() {
        setTypeClass((Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
    }

/// Functions

/// Fonction pour prendre un connexion en Oracle
    public static Connection getOracle() throws SQLException, ClassNotFoundException {
        Class.forName("oracle.jdbc.driver.OracleDriver");
        Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@192.168.88.21:1521:orcl", "scott", "tiger");
        connection.setAutoCommit(false);
        return connection;
    }

/// Fonction pour prendre un connexion en PostgreSQL
    public static Connection getPostgreSQL() throws SQLException, ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5433/star?user=postgres&password=postgres");
        connection.setAutoCommit(false);
        return connection;
    }
    
/// Fonction pour prendre les listes de colonnes dans un requete
    public static String[] listColumn(String query, Connection connection) throws Exception {
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        ResultSetMetaData rsMetaData = rs.getMetaData(); // Classe avec des données plus détaillé de la requete
        int count = rsMetaData.getColumnCount();
        String[] colonnes = new String[count];
        int increment = 0;
        for(int i = 1; i <= count; i++) {
            colonnes[increment] = rsMetaData.getColumnName(i);
            increment++;
        }
        return colonnes;
    }

/// Prendre des données dans la base de données avec "SELECT"
    public T[] getData(Connection connection, String order, String... predicat) throws Exception {
        String sql = (predicat.length == 0) ? "SELECT * FROM " + this.getTable() 
                    : "SELECT * FROM " + this.getTable() + " WHERE " + predicat(predicat); // Requete SQL avec les pedicats si nécessaire
        if (order != null) sql += " ORDER BY " + order;
        return getData(sql, connection);
    }

/// Tous requete peut etre en input sur cette fonction
    public T[] getData(String query, Connection connection) throws Exception {
        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery(query);
        String[] liste = listColumn(query, connection); // get liste of column length
        T[] employees = convertToObject(result, liste.length);
        result.close();
        statement.close();
        connection.close();
        return employees;
    }

/// Convertir les réponse SQL en Object (T[])
    public T[] convertToObject(ResultSet result, int attribut) throws Exception {
        Field[] attributs = this.getClass().getDeclaredFields();
        Vector<T> objects = new Vector<T>(); // Initialisation du vector pour sauver les donnees
        while (result.next()) {
            T object = getTypeClass().getConstructor().newInstance(); // Nouveau instance de l'object qui hérite ce BddObject
            for (int i = 0; i < attribut; i++) {
               setFieldValue(object, attributs[i], result, i + 1);
            }
            objects.add(object);
        }
        return objects.toArray((T[]) Array.newInstance(getTypeClass(), objects.size())); // Fonction pour creer un tableau avec le generic
    }

    public void setFieldValue(Object object, Field attribut, ResultSet result, int index) throws Exception {
        Object value = null;
        if (attribut.isAnnotationPresent(ForeignKey.class)) {
            ForeignKey fk = attribut.getAnnotation(ForeignKey.class);
            BddObject<?> fkObject = (BddObject<?>) attribut.getType().getConstructor().newInstance();
            fkObject.getClass().getDeclaredMethod("set" + toUpperCase(fk.column()), fk.typeColumn()).invoke(fkObject, ResultSet.class.getMethod("get" + toUpperCase(fk.typeColumn().getSimpleName()), int.class).invoke(result, index));
            value = (attribut.getType().isArray()) ? fkObject.getData(BddObject.getPostgreSQL(), null, fk.column()) 
                : fkObject.getData(BddObject.getPostgreSQL(), null, fk.column())[0];
        } else {
            value = ResultSet.class.getMethod("get" + toUpperCase(attribut.getType().getSimpleName()), int.class).invoke(result, index);
        }
        this.getClass().getMethod("set" + toUpperCase(attribut.getName()), attribut.getType()).invoke(object, value);
    }

    public String predicat(String[] predicats) throws Exception {
        String sql = ""; // Condition with AND clause
        for (String predicat : predicats) {
            Object value = this.getClass().getMethod("get" + toUpperCase(predicat)).invoke(this);
            if (value instanceof BddObject) {
                ForeignKey fk =  this.getClass().getDeclaredField(predicat).getAnnotation(ForeignKey.class);
                predicat = "id" + predicat;
                value = value.getClass().getMethod("get" + toUpperCase(fk.column())).invoke(value);
            }
            sql += predicat + "=" + convertToLegal(value) + " AND ";
        }
        return sql.substring(0, sql.length() - 5); // Delete last " AND " in sql
    }
    
    public void insert(Connection connection) throws Exception {
        boolean connect = false;
        if (connection == null) {connection = getPostgreSQL(); connect = true;}
        String[] liste = listColumn("SELECT * FROM " + this.getTable(), connection);
        Statement statement = connection.createStatement();
        String sql = "INSERT INTO " + this.getTable() + " " + createColumn(liste) + " VALUES ("; // Insert with all column
        Field[] attributs = this.getClass().getDeclaredFields();
        for (int i = 0; i < liste.length; i++) {
            Object value = this.getClass().getMethod("get" + toUpperCase(attributs[i].getName())).invoke(this);
            if (attributs[i].isAnnotationPresent(ForeignKey.class))
                value = value.getClass().getMethod("get" + toUpperCase(attributs[i].getAnnotation(ForeignKey.class).column())).invoke(value);
            sql += convertToLegal(value) + ",";
        }
        sql = sql.substring(0, sql.length() - 1) + ")";
        statement.executeUpdate(sql);
        statement.close();
        if (connect) {connection.commit(); connection.close();}
    }
    
    static String createColumn(String[] colonnes) {
        String result = "(";
        for (String colonne : colonnes)
            result += colonne + ",";
        result = result.substring(0, result.length()-1)+")";
        return result;
    }

    public void update(String[] column, Object[] value, String ID, Connection connection) throws Exception {
        if (value.length != column.length) throw new Exception("Value and column must be equals");
        boolean connect = false;
        if (connection == null) {connection = getPostgreSQL(); connect = true;}
        Statement statement = connection.createStatement();
        String sql = "UPDATE " + this.getTable() + " \nSET ";
        for (int i = 0; i < column.length; i++)
            sql += column[i] + " = " + convertToLegal(value[i]) + ",\n";
        sql = sql.substring(0, sql.length() - 2);
        sql += " WHERE " + ID + " = " + convertToLegal(this.getClass().getMethod("get" + toUpperCase(ID)).invoke(this));
        statement.executeUpdate(sql);
        statement.close();
        if (connect) {connection.commit(); connection.close();}
    }

    public String convertToLegal(Object args) {
        return (args == null) ? "null"
        : (args.getClass() == Date.class) ? "TO_DATE('" + args + "', 'YYYY-MM-DD')"
        : (args.getClass() == Timestamp.class) ? "TO_TIMESTAMP('"+ args +"', 'YYYY-MM-DD HH24:MI:SS.FF')"
        : ((args.getClass() == String.class) || (args.getClass() == Time.class)) ? "'"+ args +"'"
        : args.toString();
    }

    public static String toUpperCase(String name) {
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    public String buildPrimaryKey(Connection connection) throws SQLException {
        return (getPrefix() == null) ? getSequence(connection) : this.getPrefix() + completeZero(getSequence(connection), this.getCountPK() - this.getPrefix().length());
    }

    public String getSequence(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        String sql = (connection.getMetaData().getDatabaseProductName().equals("PostgreSQL")) ? "SELECT " + this.getFunctionPK() : "SELECT " + this.getFunctionPK() + " FROM DUAL";
        ResultSet result = statement.executeQuery(sql);
        result.next();
        String sequence = result.getString(1);
        statement.close();
        result.close();
        connection.close();
        return sequence;
    }
    
    public static String completeZero(String seq, int count) {
        int length = count - seq.length();
        String zero = "";
        for (int i = 0; i < length; i++)
            zero += "0";
        return zero + seq;
    }
}