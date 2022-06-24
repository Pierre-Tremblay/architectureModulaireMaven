package eu.unareil.dal.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLType;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import eu.unareil.bo.Auteur;
import eu.unareil.bo.CartePostale;
import eu.unareil.bo.Glace;
import eu.unareil.bo.Pain;
import eu.unareil.bo.Produit;
import eu.unareil.bo.Stylo;
import eu.unareil.bo.TypeCartePostale;
import eu.unareil.dal.DALException;
import eu.unareil.dal.DAO;

public class ProduitJdbcImpl implements DAO<Produit> {
	private static final String SQL_INSERT="insert into produit (libelle,marque,prix_unitaire,"
			+ "qte_stock,date_limite_conso,poids,parfum,temperature_conservation,couleur,"
			+ "type_mine,type_carte_postale,type)"
			+ " values(?,?,?,?,?,?,?,?,?,?,?,?)";
	private static final String SQL_UPDATE="update produit set libelle=?,marque=?,prix_unitaire=?,"
			+ "qte_stock=?,date_limite_conso=?,poids=?,parfum=?,temperature_conservation=?,couleur=?,"
			+ "type_mine=?,type_carte_postale=?,type=?"
			+ " where ref_prod=?";
	private static final String SQL_DELETE="delete from produit"
			+ " where ref_prod=?";
	private static final String SQL_SELECT_BY_ID="select * from produit"
			+ " where ref_prod=?";
	private static final String SQL_SELECT_ALL="select * from produit";
	private static final String SQL_INSERT_JOINTURE="insert into auteur_carte_postale(auteur_id,carte_postale_ref_prod)"
			+ " values(?,?)"; 

	@Override
	public void insert(Produit data) throws DALException {
		// TODO Auto-generated method stub
		PreparedStatement pstmt= null;
		Connection cnx = JdbcTools.getConnection();
		long id=0;
		try {
			pstmt=cnx.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS);
			pstmt.setString(1, data.getMarque());
			pstmt.setString(2, data.getLibelle());
			pstmt.setLong(3, data.getQteStock());
			pstmt.setDouble(4, data.getPrixUnitaire());
			if(data instanceof Pain)
			{
				pstmt.setDate(5,Date.valueOf(((Pain)data).getDateLimiteConso()));
				pstmt.setInt(6, ((Pain)data).getPoids());
				pstmt.setNull(7, Types.VARCHAR);
				pstmt.setNull(8, Types.INTEGER);
				pstmt.setNull(9, Types.VARCHAR);
				pstmt.setNull(10, Types.VARCHAR);
				pstmt.setNull(11, Types.VARCHAR);
				pstmt.setString(12,"Pain");
				int nbRow=pstmt.executeUpdate();
				if (nbRow == 1) {
	                ResultSet rs = pstmt.getGeneratedKeys();
	                if (rs.next()) {
	                    data.setRefProd(rs.getLong(1));
	                    id = rs.getLong(1);
	                }
				}
			}
			else if (data instanceof Glace)
			{
				pstmt.setDate(5,Date.valueOf(((Glace)data).getDateLimiteConso()));
				pstmt.setNull(6, Types.VARCHAR);
				pstmt.setString(7, ((Glace)data).getParfum());
				pstmt.setInt(8, ((Glace)data).getTemperatureConservation());
				pstmt.setNull(9, Types.VARCHAR);
				pstmt.setNull(10, Types.VARCHAR);
				pstmt.setNull(11, Types.VARCHAR);
				pstmt.setString(12,"Glace");
				int nbRow=pstmt.executeUpdate();
				if (nbRow == 1) {
	                ResultSet rs = pstmt.getGeneratedKeys();
	                if (rs.next()) {
	                    data.setRefProd(rs.getLong(1));
	                    id = rs.getLong(1);
	                }
				}
				
			}
			else if (data instanceof Stylo)
			{
				pstmt.setNull(5,Types.DATE);
				pstmt.setNull(6, Types.VARCHAR);
				pstmt.setNull(7, Types.INTEGER);
				pstmt.setNull(8, Types.INTEGER);
				pstmt.setString(9, ((Stylo)data).getCouleur());
				pstmt.setString(10, ((Stylo)data).getTypeMine());
				pstmt.setNull(11, Types.VARCHAR);
				pstmt.setString(12,"Stylo");
				int nbRow=pstmt.executeUpdate();
				if (nbRow == 1) {
	                ResultSet rs = pstmt.getGeneratedKeys();
	                if (rs.next()) {
	                    data.setRefProd(rs.getLong(1));
	                    id = rs.getLong(1);
	                }
				}
				
			}
			else if (data instanceof CartePostale)
			{
				pstmt.setNull(5,Types.DATE);
				pstmt.setNull(6, Types.VARCHAR);
				pstmt.setNull(7, Types.INTEGER);
				pstmt.setNull(8, Types.INTEGER);
				pstmt.setNull(9, Types.VARCHAR);
				pstmt.setNull(10, Types.VARCHAR);
				pstmt.setString(11,((CartePostale)data).getType());
				pstmt.setString(12,"CartePostale");
				int nbRow=pstmt.executeUpdate();
				if (nbRow == 1) {
	                ResultSet rs = pstmt.getGeneratedKeys();
	                if (rs.next()) {
	                    data.setRefProd(rs.getLong(1));
	                    id = rs.getLong(1);
	                }
				}
				//je parcours l'ensemble des auteurs de ma carte postale
				AuteurJdbcImpl autImpl= new AuteurJdbcImpl();
				for(Auteur auteur:((CartePostale)data).getLesAuteurs())
				{
					if(!autImpl.existeByNomPrenom(auteur.getPrenom(), auteur.getNom()))
					{
						autImpl.insert(auteur);
					}
					Auteur auteurTrouve=autImpl.selectByNomPrenom(auteur.getPrenom(), auteur.getNom());
					pstmt=cnx.prepareStatement(SQL_INSERT_JOINTURE);
					pstmt.setLong(1,auteurTrouve.getId());
					pstmt.setLong(2, id);
					pstmt.executeUpdate();
				}
						
			}
			data.setRefProd(id);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			throw new DALException("erreur du insert -data="+data,e.getCause());
		}
		finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException e) {
                throw new DALException("erreur du insert au niveau du close- data=" + data, e.getCause());
            }
		}
		
		
	}

	@Override
	public void delete(Produit data) throws DALException {
		// TODO Auto-generated method stub
		PreparedStatement pstmt= null;
		Connection cnx = JdbcTools.getConnection();
		long id=data.getRefProd();
			try {
				pstmt= cnx.prepareStatement(SQL_DELETE);
				pstmt.setLong(1, id);
				pstmt.executeUpdate();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				throw new DALException("erreur du delete -data="+data,e.getCause());
			}
			finally {
	            try {
	                if (pstmt != null) {
	                    pstmt.close();
	                }
	            } catch (SQLException e) {
	                throw new DALException("erreur du delete au niveau du close- data=" + data, e.getCause());
	            }
			}
	}

	@Override
	public void update(Produit data) throws DALException {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		PreparedStatement pstmt= null;
		Connection cnx = JdbcTools.getConnection();
		long id=data.getRefProd();
		try {
			pstmt=cnx.prepareStatement(SQL_UPDATE);
			pstmt.setString(1, data.getMarque());
			pstmt.setString(2, data.getLibelle());
			pstmt.setLong(3, data.getQteStock());
			pstmt.setDouble(4, data.getPrixUnitaire());
			if(data instanceof Pain)
			{
				pstmt.setDate(5,Date.valueOf(((Pain)data).getDateLimiteConso()));
				pstmt.setInt(6, ((Pain)data).getPoids());
				pstmt.setNull(7, Types.VARCHAR);
				pstmt.setNull(8, Types.INTEGER);
				pstmt.setNull(9, Types.VARCHAR);
				pstmt.setNull(10, Types.VARCHAR);
				pstmt.setNull(11, Types.VARCHAR);
				pstmt.setString(12,"PAIN");
				pstmt.setLong(13, id);
				pstmt.executeUpdate();
			}
			else if (data instanceof Glace)
			{
				pstmt.setDate(5,Date.valueOf(((Glace)data).getDateLimiteConso()));
				pstmt.setNull(6, Types.VARCHAR);
				pstmt.setString(7, ((Glace)data).getParfum());
				pstmt.setInt(8, ((Glace)data).getTemperatureConservation());
				pstmt.setNull(9, Types.VARCHAR);
				pstmt.setNull(10, Types.VARCHAR);
				pstmt.setNull(11, Types.VARCHAR);
				pstmt.setString(12,"GLACE");
				pstmt.setLong(13, id);
				pstmt.executeUpdate();
				
			}
			else if (data instanceof Stylo)
			{
				pstmt.setNull(5,Types.DATE);
				pstmt.setNull(6, Types.VARCHAR);
				pstmt.setNull(7, Types.INTEGER);
				pstmt.setNull(8, Types.INTEGER);
				pstmt.setString(9, ((Stylo)data).getCouleur());
				pstmt.setString(10, ((Stylo)data).getTypeMine());
				pstmt.setNull(11, Types.VARCHAR);
				pstmt.setString(12,"Stylo");
				pstmt.setLong(13, id);
				pstmt.executeUpdate();
				
			}
			else if (data instanceof CartePostale)
			{
				pstmt.setNull(5,Types.DATE);
				pstmt.setNull(6, Types.VARCHAR);
				pstmt.setNull(7, Types.INTEGER);
				pstmt.setNull(8, Types.INTEGER);
				pstmt.setNull(9, Types.VARCHAR);
				pstmt.setNull(10, Types.VARCHAR);
				pstmt.setString(11,((CartePostale)data).getType());
				pstmt.setString(12,"CartePostale");
				pstmt.setLong(13, id);
				pstmt.executeUpdate();
				//je parcours l'ensemble des auteurs de ma carte postale
				AuteurJdbcImpl autImpl= new AuteurJdbcImpl();
				for(Auteur auteur:((CartePostale)data).getLesAuteurs())
				{
					if(!autImpl.existeByNomPrenom(auteur.getPrenom(), auteur.getNom()))
					{
						autImpl.insert(auteur);
					}
					Auteur auteurTrouve=autImpl.selectByNomPrenom(auteur.getPrenom(), auteur.getNom());
					pstmt=cnx.prepareStatement(SQL_INSERT_JOINTURE);
					pstmt.setLong(1,auteurTrouve.getId());
					pstmt.setLong(2, id);
					pstmt.executeUpdate();
				}
						
			}
			data.setRefProd(id);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			throw new DALException("erreur du insert -data="+data,e.getCause());
		}
		finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException e) {
                throw new DALException("erreur du insert au niveau du close- data=" + data, e.getCause());
            }
		}
		
	}

	@Override
	public Produit selectById(long id) throws DALException {
		// TODO Auto-generated method stub
		Produit produit=null;
		PreparedStatement pstmt= null;
		ResultSet rs=null;
		Connection cnx = JdbcTools.getConnection();
		try {
			pstmt=cnx.prepareStatement(SQL_SELECT_BY_ID);
			pstmt.setLong(1, id);
			rs=pstmt.executeQuery();
			if(rs.next())
			{
				switch(rs.getString(6).toUpperCase())
				{
				case "PAIN":
					//public Pain(long refProd,
					//String marque, String libelle,
					//int poids, long qteStock,
					//		float prixUnitaire) {
					produit=new Pain(rs.getLong("ref_prod")
							,rs.getString("marque"),rs.getString("libelle")
							,rs.getInt("poids"),rs.getLong("qte_stock"),rs.getFloat("prix_unitaire"));
					break;
				case "GLACE":
					//	public Glace(long refProd, LocalDate dateLimiteConso,
					//String marque, String libelle, long qteStock,	
					//float prixUnitaire, String parfum, int temperatureConservation) {
					produit=new Glace(rs.getLong("ref_prod"),rs.getDate("date_limite_conso").toLocalDate()
							,rs.getString("marque"),rs.getString("libelle"),rs.getInt("qte_stock")
							,rs.getFloat("prix_unitaire"),rs.getString("parfum"),rs.getInt("temperature_conservation"));
				break;
				case "STYLO":
					//public Stylo(long refProd,
					//String marque, String libelle, long qteStock,
					//float prixUnitaire, String couleur,String typeMine) {
					produit=new Stylo(rs.getLong("ref_prod")
							,rs.getString("marque"),rs.getString("libelle"),rs.getInt("qte_stock")
							,rs.getFloat("prix_unitaire"),rs.getString("couleur"),rs.getString("type_mine"));
				break;
				case "CARTEPOSTALE":
					//public CartePostale(long refProd,
					//String marque, String libelle, long qteStock, 
					//float prixUnitaire, List<Auteur> lesAuteurs,
					//		TypeCartePostale type) {
					AuteurJdbcImpl autImpl= new AuteurJdbcImpl();
					List<Auteur> lesAuteurs=autImpl.selectByRefProd(id);
					produit=new CartePostale(rs.getLong("ref_prod")
							,rs.getString("marque"),rs.getString("libelle"),rs.getInt("qte_stock")
							,rs.getFloat("prix_unitaire"),lesAuteurs,TypeCartePostale.valueOf(rs.getString("type_carte_postale")));
				break;
				}
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			throw new DALException("erreur du select by id -id="+id,e.getCause());
		}
		finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException e) {
                throw new DALException("erreur du select by id au niveau du close- id=" + id, e.getCause());
            }
		}
		
		return produit;
	}

	@Override
	public List<Produit> selectAll() throws DALException {
		// TODO Auto-generated method stub
		Statement stmt=null;
		ResultSet rs= null;	
		Produit produit=null;
		List<Produit> lesProduits= new ArrayList<>();

		Connection cnx = JdbcTools.getConnection();
		try {
			stmt=cnx.createStatement();
			rs=stmt.executeQuery(SQL_SELECT_ALL);
			while(rs.next())
			{
				switch(rs.getString(6).toUpperCase())
				{
				case "PAIN":
					//public Pain(long refProd,
					//String marque, String libelle,
					//int poids, long qteStock,
					//		float prixUnitaire) {
					produit=new Pain(rs.getLong("ref_prod")
							,rs.getString("marque"),rs.getString("libelle")
							,rs.getInt("poids"),rs.getLong("qte_stock"),rs.getFloat("prix_unitaire"));
					break;
				case "GLACE":
					//	public Glace(long refProd, LocalDate dateLimiteConso,
					//String marque, String libelle, long qteStock,	
					//float prixUnitaire, String parfum, int temperatureConservation) {
					produit=new Glace(rs.getLong("ref_prod"),rs.getDate("date_limite_conso").toLocalDate()
							,rs.getString("marque"),rs.getString("libelle"),rs.getInt("qte_stock")
							,rs.getFloat("prix_unitaire"),rs.getString("parfum"),rs.getInt("temperature_conservation"));
				break;
				case "STYLO":
					//public Stylo(long refProd,
					//String marque, String libelle, long qteStock,
					//float prixUnitaire, String couleur,String typeMine) {
					produit=new Stylo(rs.getLong("ref_prod")
							,rs.getString("marque"),rs.getString("libelle"),rs.getInt("qte_stock")
							,rs.getFloat("prix_unitaire"),rs.getString("couleur"),rs.getString("type_mine"));
				break;
				case "CARTEPOSTALE":
					//public CartePostale(long refProd,
					//String marque, String libelle, long qteStock, 
					//float prixUnitaire, List<Auteur> lesAuteurs,
					//		TypeCartePostale type) {
					AuteurJdbcImpl autImpl= new AuteurJdbcImpl();
					List<Auteur> lesAuteurs=autImpl.selectByRefProd(rs.getLong("ref_prod"));
					produit=new CartePostale(rs.getLong("ref_prod")
							,rs.getString("marque"),rs.getString("libelle"),rs.getInt("qte_stock")
							,rs.getFloat("prix_unitaire"),lesAuteurs,TypeCartePostale.valueOf(rs.getString("type_carte_postale")));
				break;
				}
				lesProduits.add(produit);
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			throw new DALException("erreur du select all",e.getCause());
		}
		finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException e) {
                throw new DALException("erreur du select all au niveau du close- id=", e.getCause());
            }
		}
		return lesProduits;
	}

}
