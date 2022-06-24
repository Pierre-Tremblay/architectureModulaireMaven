package eu.unareil.bll;

import eu.unareil.bo.*;
import eu.unareil.dal.DALException;
import eu.unareil.dal.DAO;
import eu.unareil.dal.DAOFactory;

import java.time.LocalDate;
import java.util.List;

public class ProduitManager {
    private static volatile ProduitManager instance;
    private static DAO<Produit> impl;

    private ProduitManager() {
        impl = DAOFactory.getProduitDAO();
    }

    public final static ProduitManager getInstance() {
        if (ProduitManager.instance == null) {
            synchronized (ProduitManager.class) {
                if (ProduitManager.instance == null) {
                    ProduitManager.instance = new ProduitManager();
                }
            }
        }
        return instance;
    }

    public List<Produit> getLesProduits() throws BLLException {
        List<Produit> lesProduits;
        try {
            lesProduits = impl.selectAll();
        } catch (DALException e) {
            throw new BLLException("Une erreur est survenue, impossible de récupérer les produits", e.getCause());
        }
        return lesProduits;
    }

    public void deleteProduit(Produit produit) throws BLLException {
        controlProduit(produit);
        try {
            impl.delete(produit);
        } catch (DALException e) {
            throw new BLLException("Une erreur est survenue, impossible de supprimer le produit", e.getCause());
        }
    }

    public void addProduit(Produit produit) throws BLLException {

        if (produit.getRefProd() != 0) {
            throw new BLLException("Une erreur est survenue le produit existe déjà");
        }
        controlProduit(produit);
        try {
            impl.insert(produit);
        } catch (DALException e) {
            throw new BLLException("Une erreur est survenue, impossible d'ajouter le produit", e.getCause());
        }
    }

    public void updateProduit(Produit produit) throws BLLException {
        controlProduit(produit);

        try {
            impl.update(produit);
        } catch (DALException e) {
            throw new BLLException("Une erreur est survenue, impossible de mettre à jour le produit", e.getCause());
        }
    }

    public Produit getLeProduit(long id) throws BLLException {
        Produit produit;
        try {
            produit = impl.selectById(id);
        } catch (DALException e) {
            throw new BLLException("Une erreur est survenue, impossible de récupérer le produit", e.getCause());
        }
        return produit;
    }

    public void controlProduit(Produit produit) throws BLLException {
        boolean valide = true;
        StringBuilder sb = new StringBuilder();

        // PRODUIT
        if (produit == null) {
            throw new BLLException("Une erreur est survenue, le produit ne peut pas être null");
        }
        if (produit.getMarque() == null) {
            sb.append("Une erreur est survenue, la marque du produit ne peut pas être null");
        }
        if (produit.getLibelle() == null) {
            sb.append("Une erreur est survenue, le libellé du produit ne peut pas être null");
        }
        if (produit.getPrixUnitaire() <= 0) {
            sb.append("Une erreur est survenue, le prix du produit doit être strictement supérieur à 0");
        }
        if (produit.getQteStock() < 1) {
            sb.append("Une erreur est survenue, le produit doit avoir au moins une produit en stock");
        }

        // STYLO
        if (produit instanceof Stylo && ((Stylo) produit).getTypeMine() == null) {
            sb.append("Une erreur est survenue, le type de mine du stylo ne peut pas être null");
            valide = false;
        }
        if (produit instanceof Stylo && ((Stylo) produit).getCouleur() == null) {
            sb.append("Une erreur est survenue, la couleur du stylo ne peut pas être null");
            valide = false;
        }

        // GLACE
        if (produit instanceof Glace && ((Glace) produit).getTemperatureConservation() > 0) {
            sb.append("Une erreur est survenue, la température de conservation de la glace ne peut être supérieur à 0");
            valide = false;
        }
        if (produit instanceof Glace && ((Glace) produit).getDateLimiteConso().isBefore(LocalDate.now())) {
            sb.append("Une erreur est survenue, le date limite de consommation de la glace ne peut être inférieur à la date actuel");
            valide = false;
        }

        // PAIN
        if (produit instanceof Pain && ((Pain) produit).getDateLimiteConso().isBefore(LocalDate.now())) {
            sb.append("Une erreur est survenue, le date limite de consommation du pain ne peut être inférieur à la date actuel");
            valide = false;
        }
        if (produit instanceof Pain && ((Pain) produit).getPoids() <= 0) {
            sb.append("Une erreur est survenue, le poids du pain ne peut pas être négatif");
            valide = false;
        }

        //CARTE POSTALE
        if (produit instanceof CartePostale && ((CartePostale) produit).getType() == null) {
            sb.append("Une erreur est survenue, le type de carte postale ne peut pas être null");
            valide = false;
        }

        if (!valide) {
            throw new BLLException(sb.toString());
        }
    }
}

