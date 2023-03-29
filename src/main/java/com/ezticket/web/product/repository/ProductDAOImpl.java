package com.ezticket.web.product.repository;

import com.ezticket.core.util.Util;
import com.ezticket.web.product.pojo.Product;
import jakarta.persistence.PersistenceContext;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ProductDAOImpl implements ProductDAO {
	@PersistenceContext
	private Session session;

	@Override
	public void insert(Product product) {
		// JDBC版本寫法
		final String INSERT_SQL = "insert into Product (pclassno,pname,hostno,pdiscrip,pprice,pspecialprice,pqty,psdate,pedate,ptag,pstatus)"
				+ " values (?,?,?,?,?,?,?,?,?,?,?);";

		try {
			Class.forName(Util.Driver);
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		//新增資料之後取得資料庫自動綁定的主鍵值()JDBC講義第20-2/3章
		try (Connection connection = Util.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);) {
			pstmt.setInt(1, product.getPclassno());
			pstmt.setString(2, product.getPname());
			pstmt.setInt(3, product.getHostno());
			pstmt.setString(4, product.getPdiscrip());
			pstmt.setInt(5, product.getPprice());
			pstmt.setInt(6, product.getPspecialprice());
			pstmt.setInt(7, product.getPqty());
			pstmt.setTimestamp(8, product.getPsdate());
			pstmt.setTimestamp(9, product.getPedate());
			pstmt.setString(10, product.getPtag());
			pstmt.setInt(11, product.getPstatus());
			pstmt.executeUpdate();

			try (ResultSet rs = pstmt.getGeneratedKeys()) {
				if (rs.next()) {
					product.setProductno(rs.getInt(1));
					System.out.println(product.getProductno());
				} else {
					throw new SQLException("Creating user failed, no ID obtained.");
				}
			}

		} catch (SQLException se) {
			se.printStackTrace();
		}

	}

	@Override
	public void update(Product product) {
		final StringBuilder UPDATE_SQL = new StringBuilder().append("UPDATE Product SET ");
		UPDATE_SQL.append("pclassno = ?, ").append("pname =?, ").append("hostno =?, ").append("pdiscrip = ?, ")
				.append("pprice =?, ").append("pspecialprice =?, ").append("pqty =?, ").append("psdate =?, ")
				.append("pedate =?, ").append("ptag =?, ").append("pstatus =?, ").append("pratetotal =?, ")
				.append("prateqty =? ").append("WHERE productno = ?");
		try {
			Class.forName(Util.Driver);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		try (Connection conn = Util.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(UPDATE_SQL.toString());) {
			pstmt.setInt(1, product.getPclassno());
			pstmt.setString(2, product.getPname());
			pstmt.setInt(3, product.getHostno());
			pstmt.setString(4, product.getPdiscrip());
			pstmt.setInt(5, product.getPprice());
			pstmt.setInt(6, product.getPspecialprice());
			pstmt.setInt(7, product.getPqty());
			pstmt.setTimestamp(8, product.getPsdate());
			pstmt.setTimestamp(9, product.getPedate());
			pstmt.setString(10, product.getPtag());
			pstmt.setInt(11, product.getPstatus());
			pstmt.setInt(12, product.getPratetotal());
			pstmt.setInt(13, product.getPrateqty());
			pstmt.setInt(14, product.getProductno());
			pstmt.executeUpdate();

		} catch (SQLException se) {
			se.printStackTrace();
		}

	}

	@Override
	public Product getByPrimaryKey(Integer productno) {
		return session.get(Product.class,productno);
		
		//原JDBC寫法
//		final String SELECT_BY_PK_SQL = "SELECT * FROM product WHERE productno =? ";
//		try {
//			Class.forName(Util.Driver);
//		} catch (ClassNotFoundException e) {
//			e.printStackTrace();
//		}
//		try (Connection connection = Util.getConnection();
//				PreparedStatement preparedStatement = connection.prepareStatement(SELECT_BY_PK_SQL);) {
//			preparedStatement.setInt(1, productno);
//			try (ResultSet resultset = preparedStatement.executeQuery();) {
//				if (resultset.next()) {
//					Product product = new Product();
//					product.setProductno(resultset.getInt("PRODUCTNO"));
//					product.setPclassno(resultset.getInt("PCLASSNO"));
//					product.setPname(resultset.getString("PNAME"));
//					product.setHostno(resultset.getInt("HOSTNO"));
//					product.setPdiscrip(resultset.getString("PDISCRIP"));
//					product.setPprice(resultset.getInt("PPRICE"));
//					product.setPspecialprice(resultset.getInt("PSPECIALPRICE"));
//					product.setPqty(resultset.getInt("PQTY"));
//					product.setPsdate(resultset.getTimestamp("PSDATE"));
//					product.setPedate(resultset.getTimestamp("PEDATE"));
//					product.setPtag(resultset.getString("PTAG"));
//					product.setPstatus(resultset.getInt("PSTATUS"));
//					product.setPratetotal(resultset.getInt("PRATETOTAL"));
//					product.setPrateqty(resultset.getInt("PRATEQTY"));
//					return product;
//				}
//			}
//		} catch (SQLException se) {
//			se.printStackTrace();
//		}
//		return null;
	}

	@Override
	public List<Product> getAll() {
		final String SELECT_ALL_HQL = "FROM Product ORDER BY productno DESC";
		return session.createQuery(SELECT_ALL_HQL,Product.class).getResultList();
//		final String SELECT_ALL_SQL = "SELECT * FROM PRODUCT ORDER BY PRODUCTNO DESC";
//		try {
//			Class.forName(Util.Driver);
//		} catch (ClassNotFoundException e) {
//			e.printStackTrace();
//		}
//
//		try (Connection conn = Util.getConnection();
//				PreparedStatement pstmt = conn.prepareStatement(SELECT_ALL_SQL);
//				ResultSet rs = pstmt.executeQuery();) {
//			List<Product> list = new ArrayList<Product>();
//			while (rs.next()) {
//				Product product = new Product();
//				product.setProductno(rs.getInt("PRODUCTNO"));
//				product.setPclassno(rs.getInt("PCLASSNO"));
//				product.setPname(rs.getString("PNAME"));
//				product.setHostno(rs.getInt("HOSTNO"));
//				product.setPdiscrip(rs.getString("PDISCRIP"));
//				product.setPprice(rs.getInt("PPRICE"));
//				product.setPspecialprice(rs.getInt("PSPECIALPRICE"));
//				product.setPqty(rs.getInt("PQTY"));
//				product.setPsdate(rs.getTimestamp("PSDATE"));
//				product.setPedate(rs.getTimestamp("PEDATE"));
//				product.setPtag(rs.getString("PTAG"));
//				product.setPstatus(rs.getInt("PSTATUS"));
//				product.setPratetotal(rs.getInt("PRATETOTAL"));
//				product.setPrateqty(rs.getInt("PRATEQTY"));
//				list.add(product);
//			}
//			return list;
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return null;
	}

	@Override
	public List<Product> findByVO(Product productVO) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Product> findByProductName(String pname) {
		final String SELECT_BY_NAME_SQL = "SELECT * FROM PRODUCT WHERE PNAME LIKE ? ";

		try {
			Class.forName(Util.Driver);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		try (Connection conn = Util.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(SELECT_BY_NAME_SQL);) {
//			System.out.println(pstmt);
			pstmt.setString(1, "%" + pname + "%");
//			System.out.println(pstmt);
			try (ResultSet rs = pstmt.executeQuery();) {
				List<Product> list = new ArrayList<Product>();
				while (rs.next()) {
					Product product = new Product();
					product.setProductno(rs.getInt("PRODUCTNO"));
					product.setPclassno(rs.getInt("PCLASSNO"));
					product.setPname(rs.getString("PNAME"));
					product.setHostno(rs.getInt("HOSTNO"));
					product.setPdiscrip(rs.getString("PDISCRIP"));
					product.setPprice(rs.getInt("PPRICE"));
					product.setPspecialprice(rs.getInt("PSPECIALPRICE"));
					product.setPqty(rs.getInt("PQTY"));
					product.setPsdate(rs.getTimestamp("PSDATE"));
					product.setPedate(rs.getTimestamp("PEDATE"));
					product.setPtag(rs.getString("PTAG"));
					product.setPstatus(rs.getInt("PSTATUS"));
					product.setPratetotal(rs.getInt("PRATETOTAL"));
					product.setPrateqty(rs.getInt("PRATEQTY"));
					list.add(product);
				}
				return list;
			}
		} catch (SQLException se) {
			se.printStackTrace();
		}
		return null;
	}

}
