package eu.unareil.dal;

import eu.unareil.bo.Produit;
import eu.unareil.dal.jdbc.ProduitJdbcImpl;

public class DAOFactory {
    public static DAO<Produit> getProduitDAO() {
        return new ProduitJdbcImpl();
    }
}

