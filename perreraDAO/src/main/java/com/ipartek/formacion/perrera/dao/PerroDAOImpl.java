package com.ipartek.formacion.perrera.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.QueryException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

import com.ipartek.formacion.perrera.pojo.Perro;
import com.ipartek.formacion.perrera.util.HibernateUtil;

public class PerroDAOImpl implements PerroDAO {

	// instancia unica para 'patron Singleton'
	private static PerroDAOImpl INSTANCE = null;

	// constructor privado para que no se pueda instanciar esta clase
	private PerroDAOImpl() {
		super();
	}

	// unico metodo para crear un objeto de esta Clase
	public synchronized static PerroDAOImpl getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new PerroDAOImpl();
		}
		return INSTANCE;
	}

	/**
	 * Función que devuelve una lista de perros
	 * 
	 * @param order
	 *            Modo de ordenacion de la lista.<br>
	 *            Posibles valores asc/desc
	 * @param campo
	 *            Campo por el que se va a ordenar. <br>
	 *            Posibles valores id/nombre/raza
	 * @return List<Perro>
	 */
	public List<Perro> getAll(String order, String campo) {
		// inicializamos lista como un ArrayList de objetos Perro
		ArrayList<Perro> lista = new ArrayList<Perro>();
		// obtenemos la sesion
		Session s = HibernateUtil.getSession();
		try {
			
			try{
				if ( "desc".equals(order)){
					lista = (ArrayList<Perro>) s.createCriteria(Perro.class).addOrder(Order.desc(campo)).list();
				}else{
					lista = (ArrayList<Perro>) s.createCriteria(Perro.class).addOrder(Order.asc(campo)).list();
				}
			// Si falla porque esta mal la Query, por ejemplo una columna que no existe
			// retorno listado perros ordenados por id desc
			}catch(QueryException e){
				lista = (ArrayList<Perro>) s.createCriteria(Perro.class).addOrder(Order.desc("id")).list();
			}	
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// cerramos la transaccion
			s.close();
		}
		return lista;
	}

	public Perro getById(long idPerro) {
		Perro resul = null;
		Session s = HibernateUtil.getSession();
		try {			
			resul = (Perro) s.get(Perro.class, idPerro);			
		} catch (Exception e) {
			e.printStackTrace();			
		} finally {
			s.close();			
		}
		return resul;
	}

	public boolean delete(long idPerro) {
		Perro pElimnar = null;
		boolean resul = false;
		Session s = HibernateUtil.getSession();
		try {
			s.beginTransaction();
			pElimnar = (Perro) s.get(Perro.class, idPerro);
			if (pElimnar != null) {
				s.delete(pElimnar);
				s.beginTransaction().commit();
				resul = true;
			}
		} catch (final Exception e) {
			e.printStackTrace();
			s.beginTransaction().rollback();
		} finally {
			s.close();			
		}
		return resul;
	}

	public boolean update(Perro perro) {		
		boolean resul = false;
		Session s = HibernateUtil.getSession();
		try {
			s.beginTransaction();
			s.update(perro);
			s.beginTransaction().commit();
			resul = true;			
		} catch (final Exception e) {
			e.printStackTrace();
			s.beginTransaction().rollback();
		} finally {
			s.close();			
		}
		return resul;
	}

	public boolean insert(Perro perro) {
		boolean resul = false;
		Session s = HibernateUtil.getSession();
		try {
			s.beginTransaction();
			long idCreado = (Long)s.save(perro);
			if (idCreado > 0) {				
				resul  = true;
				s.beginTransaction().commit();
			}else{
				s.beginTransaction().rollback();
			}
		} catch (Exception e) {
			s.beginTransaction().rollback();
			e.printStackTrace();
		} finally {
			s.close();			
		}
		return resul;
	}

}
